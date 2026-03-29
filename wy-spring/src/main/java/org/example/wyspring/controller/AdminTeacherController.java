package org.example.wyspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.request.TeacherReviewRequest;
import org.example.wyspring.dto.response.PageResult;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.enums.ErrorCode;
import org.example.wyspring.exception.BusinessException;
import org.example.wyspring.service.TeacherManageService;
import org.example.wyspring.utils.CurrentUserUtils;
import org.example.wyspring.vo.TeacherAdminVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 后台 — 教师入驻审核与管理
 */
@RestController
@RequestMapping("/api/v1/admin/teachers")
@RequiredArgsConstructor
@Tag(name = "后台-教师管理")
public class AdminTeacherController {

	private final TeacherManageService teacherManageService;

	private static void requireLogin() {
		if (CurrentUserUtils.getCurrentUserId() == null) {
			throw new BusinessException(ErrorCode.USER_NOT_LOGIN);
		}
	}

	@GetMapping
	@Operation(summary = "教师分页列表（姓名/手机号模糊搜）")
	public Result<PageResult<TeacherAdminVO>> page(
			@Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
			@Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
			@Parameter(description = "状态：0待审核 1通过 2拒绝") @RequestParam(required = false) Integer status,
			@Parameter(description = "关键词：教师姓名或用户手机号") @RequestParam(required = false) String keyword) {
		requireLogin();
		return Result.success(teacherManageService.pageForAdmin(page, size, status, keyword));
	}

	@GetMapping("/{id}")
	@Operation(summary = "教师详情")
	public Result<TeacherAdminVO> detail(@PathVariable Long id) {
		requireLogin();
		return Result.success(teacherManageService.getDetail(id));
	}

	@PostMapping("/{id}/review")
	@Operation(summary = "审核（仅待审核可操作）")
	public Result<Void> review(@PathVariable Long id, @RequestBody @Valid TeacherReviewRequest request) {
		requireLogin();
		teacherManageService.review(id, Boolean.TRUE.equals(request.getApprove()), request.getReviewReason());
		return Result.success(null);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除教师记录（逻辑删除）")
	public Result<Void> delete(@PathVariable Long id) {
		requireLogin();
		teacherManageService.softDelete(id);
		return Result.success(null);
	}
}
