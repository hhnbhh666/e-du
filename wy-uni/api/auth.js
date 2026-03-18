/**
 * 认证相关 API
 */
import { api } from '@/utils/request.js';

export const authApi = {
	/**
	 * 用户注册
	 * @param {Object} data - { phone, password, nickname }
	 */
	register: (data) => {
		return api.post('/auth/register', data);
	},
	
	/**
	 * 用户登录
	 * @param {Object} data - { phone, password }
	 * @returns {Promise<{token: string}>}
	 */
	login: (data) => {
		return api.post('/auth/login', data);
	},
	
	/**
	 * 获取当前用户信息
	 */
	getCurrentUser: () => {
		return api.get('/auth/me');
	}
};
