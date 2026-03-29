/**
 * 请求封装 - 开发环境无需登录验证
 */

export const API_BASE_URL = 'http://localhost:8080/api';
const BASE_URL = API_BASE_URL;

// 统一请求封装
const request = (options) => {
	const token = uni.getStorageSync('token');
	const headers = {
		'Content-Type': 'application/json',
		...options.header
	};
	if (token) {
		headers.Authorization = 'Bearer ' + token;
	}
	return new Promise((resolve, reject) => {
		uni.request({
			url: BASE_URL + options.url,
			method: options.method || 'GET',
			data: options.data,
			header: headers,
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
