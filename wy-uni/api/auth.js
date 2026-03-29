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
	},

	/** 滑动验证：开始，返回 { slideId } */
	slideStart: () => {
		return api.post('/auth/slide/start', {});
	},

	/** 滑动验证：完成，返回 { gateToken } */
	slideVerify: (data) => {
		return api.post('/auth/slide/verify', data);
	},

	/** 发送登录短信（需先 slide 拿到 gateToken） */
	sendSmsCode: (data) => {
		return api.post('/auth/sms/send', data);
	},

	/** 短信登录 / 自动注册 */
	smsLogin: (data) => {
		return api.post('/auth/sms/login', data);
	}
};
