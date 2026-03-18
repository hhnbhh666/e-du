/**
 * 老师端 API
 */
import { api } from '@/utils/request.js';

export const teacherApi = {
	/**
	 * 创建题目
	 * @param {Object} data - 题目数据
	 */
	createQuestion: (data) => {
		return api.post('/v1/teacher/questions', data);
	}
};
