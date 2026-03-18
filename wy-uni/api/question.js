/**
 * 题目相关 API
 */
import { api } from '@/utils/request.js';

export const questionApi = {
	/**
	 * 获取题目列表
	 * @param {Object} params - { page, size, categoryId, type, difficulty }
	 */
	getQuestionList: (params = {}) => {
		return api.get('/questions', {
			page: 1,
			size: 20,
			...params
		});
	},
	
	/**
	 * 获取题目详情
	 * @param {number} id - 题目ID
	 */
	getQuestionDetail: (id) => {
		return api.get(`/questions/${id}`);
	},
	
	/**
	 * 提交答题结果
	 * @param {number} id - 题目ID
	 * @param {number} selectedOption - 选择的选项索引
	 */
	submitAnswer: (id, selectedOption) => {
		return api.post(`/questions/${id}/answer`, {
			questionId: id,
			selectedOption
		});
	},
	
	/**
	 * 收藏/取消收藏题目
	 * @param {number} id - 题目ID
	 */
	toggleFavorite: (id) => {
		return api.post(`/questions/${id}/favorite`);
	}
};
