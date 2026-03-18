package org.example.wyspring.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wyspring.dto.request.AnswerSubmitRequest;
import org.example.wyspring.dto.request.QuestionCreateRequest;
import org.example.wyspring.entity.FavoriteQuestion;
import org.example.wyspring.entity.Question;
import org.example.wyspring.entity.QuestionOption;
import org.example.wyspring.entity.UserAnswerRecord;
import org.example.wyspring.enums.ErrorCode;
import org.example.wyspring.enums.QuestionStatus;
import org.example.wyspring.enums.QuestionType;
import org.example.wyspring.exception.BusinessException;
import org.example.wyspring.mapper.FavoriteQuestionMapper;
import org.example.wyspring.mapper.QuestionMapper;
import org.example.wyspring.mapper.QuestionOptionMapper;
import org.example.wyspring.mapper.UserAnswerRecordMapper;
import org.example.wyspring.service.QuestionService;
import org.example.wyspring.vo.QuestionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper optionMapper;
    private final UserAnswerRecordMapper recordMapper;
    private final FavoriteQuestionMapper favoriteMapper;

    @Override
    public IPage<QuestionVO> getQuestionPage(Page<QuestionVO> page, Integer categoryId, Integer type, Integer difficulty, Long userId) {
        IPage<Question> questionPage = questionMapper.selectPageList(
                new Page<>(page.getCurrent(), page.getSize()),
                categoryId, type, difficulty);

        List<QuestionVO> voList = questionPage.getRecords().stream()
                .map(q -> convertToVO(q, userId, false))
                .collect(Collectors.toList());

        Page<QuestionVO> resultPage = new Page<>(page.getCurrent(), page.getSize(), questionPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    @Override
    public QuestionVO getQuestionDetail(Long questionId, Long userId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        // 查询选项
        List<QuestionOption> options = optionMapper.selectByQuestionId(questionId);
        question.setOptions(options);

        return convertToVO(question, userId, false);
    }

    @Override
    @Transactional
    public QuestionVO submitAnswer(Long userId, AnswerSubmitRequest request) {
        Long questionId = request.getQuestionId();
        Integer selectedOption = request.getSelectedOption();

        // 查询题目
        Question question = questionMapper.selectDetailById(questionId);
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        if (question.getStatus() != QuestionStatus.APPROVED.getCode()) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_APPROVED);
        }

        // 判断是否正确
        boolean isCorrect = selectedOption.equals(question.getCorrectAnswer());

        // 保存/更新答题记录
        UserAnswerRecord existing = recordMapper.selectByUserAndQuestion(userId, questionId);
        UserAnswerRecord record = new UserAnswerRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setSelectedOption(selectedOption);
        record.setIsCorrect(isCorrect ? 1 : 0);
        record.setCreatedAt(LocalDateTime.now());

        if (existing == null) {
            recordMapper.insert(record);
            // 更新题目统计
            questionMapper.updateStats(questionId, isCorrect ? 1 : 0);
        } else {
            record.setId(existing.getId());
            recordMapper.updateById(record);
        }

        log.info("[QUIZ_ANSWER] userId={}, questionId={}, isCorrect={}, correctAnswer={}", 
                userId, questionId, isCorrect, question.getCorrectAnswer());

        // 查询选项
        List<QuestionOption> options = optionMapper.selectByQuestionId(questionId);
        question.setOptions(options);

        // 直接传入答题结果，避免再次查询数据库
        return convertToVO(question, userId, true, isCorrect);
    }

    @Override
    @Transactional
    public Boolean toggleFavorite(Long userId, Long questionId) {
        FavoriteQuestion existing = favoriteMapper.selectByUserAndQuestion(userId, questionId);

        if (existing != null) {
            // 取消收藏
            favoriteMapper.deleteById(existing.getId());
            log.info("[FAVORITE_REMOVE] userId={}, questionId={}", userId, questionId);
            return false;
        } else {
            // 添加收藏
            FavoriteQuestion favorite = new FavoriteQuestion();
            favorite.setUserId(userId);
            favorite.setQuestionId(questionId);
            favorite.setCreatedAt(LocalDateTime.now());
            favoriteMapper.insert(favorite);
            log.info("[FAVORITE_ADD] userId={}, questionId={}", userId, questionId);
            return true;
        }
    }

    @Override
    public Boolean hasAnswered(Long userId, Long questionId) {
        if (userId == null) {
            return false;
        }
        return recordMapper.countByUserAndQuestion(userId, questionId) > 0;
    }

    @Override
    @Transactional
    public Long createQuestion(Long teacherId, QuestionCreateRequest request) {
        // 创建题目实体
        Question question = new Question();
        question.setContent(request.getContent());
        question.setImageUrl(request.getImageUrl());
        question.setVideoUrl(request.getVideoUrl());
        question.setType(request.getType());
        question.setCategoryId(request.getCategoryId());
        question.setTeacherId(teacherId);
        question.setDifficulty(request.getDifficulty());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setTip(request.getTip());
        question.setExplanation(request.getExplanation());
        question.setStatus(1); // 1=已通过，直接上架
        question.setAnswerCount(0);
        question.setCorrectCount(0);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        // 插入题目
        questionMapper.insert(question);

        // 插入选项
        if (request.getOptions() != null) {
            for (int i = 0; i < request.getOptions().size(); i++) {
                QuestionCreateRequest.OptionRequest optReq = request.getOptions().get(i);
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setLetter(optReq.getLetter());
                option.setContent(optReq.getContent());
                option.setSortOrder(i);
                optionMapper.insert(option);
            }
        }

        log.info("[CREATE_QUESTION] teacherId={}, questionId={}", teacherId, question.getId());
        return question.getId();
    }

    /**
     * 转换为VO
     */
    private QuestionVO convertToVO(Question question, Long userId, boolean showAnswer) {
        return convertToVO(question, userId, showAnswer, null);
    }

    /**
     * 转换为VO（带答题结果）
     */
    private QuestionVO convertToVO(Question question, Long userId, boolean showAnswer, Boolean isCorrect) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setContent(question.getContent());
        vo.setImageUrl(question.getImageUrl());
        vo.setType(question.getType());
        vo.setDifficulty(question.getDifficulty());
        vo.setCategoryId(question.getCategoryId());
        vo.setAnswerCount(question.getAnswerCount());
        vo.setCorrectCount(question.getCorrectCount());

        // 选项
        if (question.getOptions() != null) {
            vo.setOptions(question.getOptions().stream()
                    .map(opt -> {
                        QuestionVO.OptionVO optionVO = new QuestionVO.OptionVO();
                        optionVO.setLetter(opt.getLetter());
                        optionVO.setContent(opt.getContent());
                        return optionVO;
                    }).collect(Collectors.toList()));
        }

        // 用户答题状态
        if (userId != null) {
            // 如果传入了答题结果，直接使用；否则查询数据库
            if (isCorrect != null) {
                vo.setHasAnswered(true);
                vo.setCorrect(isCorrect);
            } else {
                UserAnswerRecord record = recordMapper.selectByUserAndQuestion(userId, question.getId());
                vo.setHasAnswered(record != null);
                if (record != null) {
                    vo.setCorrect(record.getIsCorrect() == 1);
                }
            }

            // 收藏状态
            vo.setIsFavorited(favoriteMapper.countByUserAndQuestion(userId, question.getId()) > 0);
        }

        // 如果已答题或要求显示答案，显示解析
        if (showAnswer || (userId != null && hasAnswered(userId, question.getId()))) {
            vo.setCorrectAnswer(question.getCorrectAnswer());
            vo.setTip(question.getTip());
            vo.setExplanation(question.getExplanation());
        }

        return vo;
    }
}
