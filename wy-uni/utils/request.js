/**
 * 请求封装 - 开发环境无需登录验证
 */

const BASE_URL = 'http://localhost:8080/api';

// 统一请求封装
const request = (options) => {
	return new Promise((resolve, reject) => {
		uni.request({
			url: BASE_URL + options.url,
			method: options.method || 'GET',
			data: options.data,
			header: {
				'Content-Type': 'application/json',
				...options.header
			},
			success: (res) => {
				if (res.statusCode === 200) {
					if (res.data.code === 200) {
						resolve(res.data.data);
					} else {
						uni.showToast({
							title: res.data.message || '请求失败',
							icon: 'none'
						});
						reject(res.data);
					}
				} else {
					uni.showToast({
						title: '网络错误：' + res.statusCode,
						icon: 'none'
					});
					reject(res);
				}
			},
			fail: (err) => {
				uni.showToast({
					title: '请求失败，请检查网络',
					icon: 'none'
				});
				reject(err);
			}
		});
	});
};

// API 方法
export const api = {
	// GET 请求
	get: (url, params = {}) => {
		return request({ url, method: 'GET', data: params });
	},
	
	// POST 请求
	post: (url, data = {}) => {
		return request({ url, method: 'POST', data });
	},
	
	// PUT 请求
	put: (url, data = {}) => {
		return request({ url, method: 'PUT', data });
	},
	
	// DELETE 请求
	delete: (url) => {
		return request({ url, method: 'DELETE' });
	}
};

export default request;
