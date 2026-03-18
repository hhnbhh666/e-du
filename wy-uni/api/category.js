/**
 * 分类相关 API
 */
import { api } from '@/utils/request.js';

export const categoryApi = {
	/**
	 * 获取分类列表
	 * @param {number} type - 类型：1课程分类 2题目分类
	 */
	getCategoryList: (type) => {
		return api.get('/categories', type ? { type } : {});
	}
};
