package org.example.wyspring.service;

import org.example.wyspring.dto.request.TeacherApplyRequest;
import org.example.wyspring.dto.request.TeacherProfileUpdateRequest;
import org.example.wyspring.dto.response.PageResult;
import org.example.wyspring.vo.TeacherAdminVO;

/**
 * 教师入驻申请、资料、后台审核
 */
public interface TeacherManageService {

	PageResult<TeacherAdminVO> pageForAdmin(int page, int size, Integer status, String keyword);

	TeacherAdminVO getDetail(Long id);

	void review(Long id, boolean approve, String reviewReason);

	void softDelete(Long id);

	void apply(Long userId, TeacherApplyRequest request);

	TeacherAdminVO getMine(Long userId);

	void updateMine(Long userId, TeacherProfileUpdateRequest request);
}
