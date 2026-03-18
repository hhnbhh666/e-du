package org.example.wyspring.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.request.AnswerSubmitRequest;
import org.example.wyspring.dto.response.PageResult;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.service.QuestionService;
import org.example.wyspring.utils.CurrentUserUtils;
import org.example.wyspring.vo.QuestionVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 刷题接口（学生端）
 */
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "刷题接口")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "获取题目列表")
    public Result<PageResult<QuestionVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "分类ID") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "题型：1单选 2多选 3判断") @RequestParam(required = false) Integer type,
            @Parameter(description = "难度：1简单 2中等 3困难") @RequestParam(required = false) Integer difficulty) {

        Long userId = CurrentUserUtils.getCurrentUserId();
        IPage<QuestionVO> result = questionService.getQuestionPage(
                new Page<>(page, size), categoryId, type, difficulty, userId);

        return Result.success(PageResult.of(
                result.getRecords(),
                result.getTotal(),
                (int) result.getCurrent(),
                (int) result.getSize()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取题目详情")
    public Result<QuestionVO> detail(@PathVariable Long id) {
        Long userId = CurrentUserUtils.getCurrentUserId();
        QuestionVO question = questionService.getQuestionDetail(id, userId);
        return Result.success(question);
    }

    @PostMapping("/{id}/answer")
    @Operation(summary = "提交答题结果")
    public Result<QuestionVO> submitAnswer(
            @PathVariable Long id,
            @RequestBody @Valid AnswerSubmitRequest request) {

        // 未登录时使用默认用户ID，便于本地开发/匿名刷题
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            userId = 1L;
        }

        request.setQuestionId(id);
        QuestionVO result = questionService.submitAnswer(userId, request);
        return Result.success(result);
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "收藏/取消收藏题目")
    public Result<Boolean> toggleFavorite(@PathVariable Long id) {
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            userId = 1L;
        }

        Boolean isFavorited = questionService.toggleFavorite(userId, id);
        return Result.success(isFavorited);
    }
}
