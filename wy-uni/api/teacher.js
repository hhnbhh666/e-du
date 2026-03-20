/**
 * 老师端 API
 */
import { api, API_BASE_URL } from '@/utils/request.js';

export const teacherApi = {
	/**
	 * 创建题目
	 * @param {Object} data - 题目数据
	 */
	createQuestion: (data) => {
		return api.post('/v1/teacher/questions', data);
	},

	/**
	 * 上传试卷图片，服务端调用百度 OCR 识别并拆题
	 * @param {string} filePath - uni.chooseImage 返回的临时路径
	 */
	recognizePaper: (filePath) => {
		return new Promise((resolve, reject) => {
			uni.uploadFile({
				url: `${API_BASE_URL}/v1/teacher/ocr/recognize`,
				filePath,
				name: 'file',
				success: (res) => {
					try {
						const body = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
						if (body.code === 200) {
							resolve(body.data);
						} else {
							uni.showToast({
								title: body.message || '识别失败',
								icon: 'none',
								duration: 3000
							});
							reject(body);
						}
					} catch (e) {
						uni.showToast({ title: '解析响应失败', icon: 'none' });
						reject(e);
					}
				},
				fail: (err) => {
					uni.showToast({ title: '上传失败，请检查网络', icon: 'none' });
					reject(err);
				}
			});
		});
	}
};
