package org.example.wyspring.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.wyspring.dto.request.AnswerSubmitRequest;
import org.example.wyspring.dto.request.QuestionCreateRequest;
import org.example.wyspring.vo.QuestionVO;

/**
 * 题目服务接口
 */
public interface QuestionService {

    /**
     * 分页查询题目列表
     */
    IPage<QuestionVO> getQuestionPage(Page<QuestionVO> page, Integer categoryId, Integer type, Integer difficulty, Long userId);

    /**
     * 获取题目详情
     */
    QuestionVO getQuestionDetail(Long questionId, Long userId);

    /**
     * 提交答题结果
     */
    QuestionVO submitAnswer(Long userId, AnswerSubmitRequest request);

    /**
     * 收藏/取消收藏题目
     */
    Boolean toggleFavorite(Long userId, Long questionId);

    /**
     * 检查用户是否答过该题
     */
    Boolean hasAnswered(Long userId, Long questionId);

    /**
     * 创建题目（老师端）
     */
    Long createQuestion(Long teacherId, QuestionCreateRequest request);
}
