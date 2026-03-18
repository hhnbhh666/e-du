package org.example.wyspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.entity.Category;
import org.example.wyspring.mapper.CategoryMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类接口
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "分类接口")
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @GetMapping
    @Operation(summary = "获取分类列表")
    public Result<List<Category>> list(
            @Parameter(description = "类型：1课程分类 2题目分类") @RequestParam(required = false) Integer type) {
        List<Category> list;
        if (type != null) {
            list = categoryMapper.selectByType(type);
        } else {
            list = categoryMapper.selectList(null);
        }
        return Result.success(list);
    }
}
