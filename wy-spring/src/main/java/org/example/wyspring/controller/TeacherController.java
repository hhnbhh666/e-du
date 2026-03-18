package org.example.wyspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.request.QuestionCreateRequest;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 老师端接口（题目管理）
 */
@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
@Tag(name = "老师端-题目管理")
public class TeacherController {

    private final QuestionService questionService;

    @PostMapping("/questions")
    @Operation(summary = "创建题目")
    public Result<Long> createQuestion(@RequestBody @Valid QuestionCreateRequest request) {
        // 未登录时使用默认老师ID=1
        Long teacherId = 1L;
        Long questionId = questionService.createQuestion(teacherId, request);
        return Result.success(questionId);
    }
}
