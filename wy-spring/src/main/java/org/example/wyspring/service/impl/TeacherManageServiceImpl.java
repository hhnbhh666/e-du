package org.example.wyspring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.wyspring.dto.request.TeacherApplyRequest;
import org.example.wyspring.dto.request.TeacherProfileUpdateRequest;
import org.example.wyspring.dto.response.PageResult;
import org.example.wyspring.entity.Teacher;
import org.example.wyspring.entity.User;
import org.example.wyspring.enums.ErrorCode;
import org.example.wyspring.enums.TeacherStatus;
import org.example.wyspring.exception.BusinessException;
import org.example.wyspring.mapper.TeacherMapper;
import org.example.wyspring.mapper.UserMapper;
import org.example.wyspring.service.TeacherManageService;
import org.example.wyspring.vo.TeacherAdminVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherManageServiceImpl implements TeacherManageService {

	private final TeacherMapper teacherMapper;
	private final UserMapper userMapper;

	@Override
	public PageResult<TeacherAdminVO> pageForAdmin(int page, int size, Integer status, String keyword) {
		LambdaQueryWrapper<Teacher> w = new LambdaQueryWrapper<>();
		if (status != null) {
			w.eq(Teacher::getStatus, status);
		}
		if (StringUtils.isNotBlank(keyword)) {
			String kw = keyword.trim();
			List<User> phoneUsers = userMapper.selectList(
					new LambdaQueryWrapper<User>().like(User::getPhone, kw));
			Set<Long> uidByPhone = phoneUsers.stream().map(User::getId).collect(Collectors.toSet());
			w.and(q -> {
				q.like(Teacher::getName, kw);
				if (!uidByPhone.isEmpty()) {
					q.or().in(Teacher::getUserId, uidByPhone);
				}
			});
		}
		w.orderByDesc(Teacher::getCreatedAt);
		IPage<Teacher> p = teacherMapper.selectPage(new Page<>(page, size), w);
		List<Teacher> records = p.getRecords();
		if (records.isEmpty()) {
			return PageResult.of(Collections.emptyList(), p.getTotal(), page, size);
		}
		Set<Long> userIds = records.stream().map(Teacher::getUserId).collect(Collectors.toSet());
		List<User> users = userMapper.selectBatchIds(userIds);
		Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));
		List<TeacherAdminVO> vos = records.stream()
				.map(t -> toAdminVO(t, userMap.get(t.getUserId())))
				.collect(Collectors.toList());
		return PageResult.of(vos, p.getTotal(), page, size);
	}

	@Override
	public TeacherAdminVO getDetail(Long id) {
		Teacher t = teacherMapper.selectById(id);
		if (t == null) {
			throw new BusinessException(ErrorCode.TEACHER_NOT_FOUND);
		}
		User u = userMapper.selectById(t.getUserId());
		return toAdminVO(t, u);
	}

	@Override
	@Transactional
	public void review(Long id, boolean approve, String reviewReason) {
		Teacher t = teacherMapper.selectById(id);
		if (t == null) {
			throw new BusinessException(ErrorCode.TEACHER_NOT_FOUND);
		}
		if (!Objects.equals(t.getStatus(), TeacherStatus.PENDING.getCode())) {
			throw new BusinessException(ErrorCode.TEACHER_REVIEW_INVALID);
		}
		if (!approve && StringUtils.isBlank(reviewReason)) {
			throw new BusinessException(ErrorCode.PARAM_ERROR, "拒绝时请填写原因");
		}
		if (approve) {
			t.setStatus(TeacherStatus.APPROVED.getCode());
			t.setReviewReason(null);
		} else {
			t.setStatus(TeacherStatus.REJECTED.getCode());
			t.setReviewReason(reviewReason.trim());
		}
		teacherMapper.updateById(t);
	}

	@Override
	@Transactional
	public void softDelete(Long id) {
		Teacher t = teacherMapper.selectById(id);
		if (t == null) {
			throw new BusinessException(ErrorCode.TEACHER_NOT_FOUND);
		}
		teacherMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void apply(Long userId, TeacherApplyRequest request) {
		User user = userMapper.selectById(userId);
		if (user == null) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}
		Teacher existing = teacherMapper.selectByUserId(userId);
		if (existing != null) {
			int st = existing.getStatus() == null ? TeacherStatus.PENDING.getCode() : existing.getStatus();
			if (st == TeacherStatus.PENDING.getCode() || st == TeacherStatus.APPROVED.getCode()) {
				throw new BusinessException(ErrorCode.TEACHER_ALREADY_APPLIED);
			}
			fillApplyFields(existing, request);
			existing.setStatus(TeacherStatus.PENDING.getCode());
			existing.setReviewReason(null);
			teacherMapper.updateById(existing);
			return;
		}
		Teacher t = new Teacher();
		t.setUserId(userId);
		fillApplyFields(t, request);
		t.setStatus(TeacherStatus.PENDING.getCode());
		teacherMapper.insert(t);
	}

	@Override
	public TeacherAdminVO getMine(Long userId) {
		Teacher t = teacherMapper.selectByUserId(userId);
		if (t == null) {
			return null;
		}
		User u = userMapper.selectById(userId);
		return toAdminVO(t, u);
	}

	@Override
	@Transactional
	public void updateMine(Long userId, TeacherProfileUpdateRequest request) {
		Teacher t = teacherMapper.selectByUserId(userId);
		if (t == null) {
			throw new BusinessException(ErrorCode.TEACHER_NOT_FOUND);
		}
		int st = t.getStatus() == null ? TeacherStatus.PENDING.getCode() : t.getStatus();
		if (st == TeacherStatus.REJECTED.getCode()) {
			throw new BusinessException(ErrorCode.PARAM_ERROR, "已拒绝请重新提交申请");
		}
		if (StringUtils.isNotBlank(request.getName())) {
			t.setName(request.getName().trim());
		}
		if (request.getTitle() != null) {
			t.setTitle(request.getTitle());
		}
		if (request.getIntroduction() != null) {
			t.setIntroduction(request.getIntroduction());
		}
		if (request.getSubjects() != null) {
			t.setSubjects(request.getSubjects());
		}
		if (request.getAvatar() != null) {
			t.setAvatar(request.getAvatar());
		}
		if (request.getCredentials() != null) {
			t.setCredentials(request.getCredentials());
		}
		if (st == TeacherStatus.APPROVED.getCode() || st == TeacherStatus.PENDING.getCode()) {
			teacherMapper.updateById(t);
		}
	}

	private static void fillApplyFields(Teacher t, TeacherApplyRequest request) {
		t.setName(request.getName().trim());
		t.setTitle(request.getTitle());
		t.setIntroduction(request.getIntroduction());
		t.setSubjects(request.getSubjects());
		t.setAvatar(request.getAvatar());
		t.setCredentials(request.getCredentials());
	}

	private static TeacherAdminVO toAdminVO(Teacher t, User u) {
		TeacherAdminVO vo = new TeacherAdminVO();
		vo.setId(t.getId());
		vo.setUserId(t.getUserId());
		if (u != null) {
			vo.setPhone(u.getPhone());
			vo.setUserNickname(u.getNickname());
		}
		vo.setName(t.getName());
		vo.setAvatar(t.getAvatar());
		vo.setTitle(t.getTitle());
		vo.setIntroduction(t.getIntroduction());
		vo.setSubjects(t.getSubjects());
		vo.setCredentials(t.getCredentials());
		vo.setStatus(t.getStatus());
		vo.setStatusText(statusText(t.getStatus()));
		vo.setReviewReason(t.getReviewReason());
		vo.setCreatedAt(t.getCreatedAt());
		vo.setUpdatedAt(t.getUpdatedAt());
		return vo;
	}

	private static String statusText(Integer status) {
		if (status == null) {
			return "";
		}
		TeacherStatus ts = TeacherStatus.of(status);
		return ts != null ? ts.getDesc() : String.valueOf(status);
	}
}
