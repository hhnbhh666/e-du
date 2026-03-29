package org.example.wyspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.request.QuestionCreateRequest;
import org.example.wyspring.dto.request.TeacherApplyRequest;
import org.example.wyspring.dto.request.TeacherProfileUpdateRequest;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.enums.ErrorCode;
import org.example.wyspring.exception.BusinessException;
import org.example.wyspring.service.QuestionService;
import org.example.wyspring.service.TeacherManageService;
import org.example.wyspring.utils.CurrentUserUtils;
import org.example.wyspring.vo.TeacherAdminVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 老师端：题目、入驻申请、资料
 */
@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
@Tag(name = "老师端")
public class TeacherController {

	private final QuestionService questionService;
	private final TeacherManageService teacherManageService;

	private static Long requireUserId() {
		Long uid = CurrentUserUtils.getCurrentUserId();
		if (uid == null) {
			throw new BusinessException(ErrorCode.USER_NOT_LOGIN);
		}
		return uid;
	}

	@PostMapping("/questions")
	@Operation(summary = "创建题目")
	public Result<Long> createQuestion(@RequestBody @Valid QuestionCreateRequest request) {
		Long teacherId = 1L;
		Long questionId = questionService.createQuestion(teacherId, request);
		return Result.success(questionId);
	}

	@PostMapping("/apply")
	@Operation(summary = "申请成为教师（需登录）；曾被拒绝可再次提交")
	public Result<Void> apply(@RequestBody @Valid TeacherApplyRequest request) {
		teacherManageService.apply(requireUserId(), request);
		return Result.success(null);
	}

	@GetMapping("/me")
	@Operation(summary = "当前用户的教师档案（未申请则为空 data）")
	public Result<TeacherAdminVO> myProfile() {
		TeacherAdminVO vo = teacherManageService.getMine(requireUserId());
		return Result.success(vo);
	}

	@PutMapping("/me")
	@Operation(summary = "更新本人教师资料")
	public Result<Void> updateMyProfile(@RequestBody @Valid TeacherProfileUpdateRequest request) {
		teacherManageService.updateMine(requireUserId(), request);
		return Result.success(null);
	}
}
