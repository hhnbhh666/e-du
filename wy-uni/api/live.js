/**
 * 直播相关API
 */
import { api } from '@/utils/request.js';

export const liveApi = {
	/**
	 * 创建直播间
	 */
	createRoom: (data) => {
		return api.post('/live/room/create', data);
	},

	/**
	 * 更新直播间
	 */
	updateRoom: (data) => {
		return api.put('/live/room/update', data);
	},

	/**
	 * 开始直播
	 */
	startLive: () => {
		return api.post('/live/room/start', {});
	},

	/**
	 * 结束直播
	 */
	stopLive: () => {
		return api.post('/live/room/stop', {});
	},

	/**
	 * 删除直播间
	 */
	deleteRoom: (roomId) => {
		return api.delete(`/live/room/${roomId}`);
	},

	/**
	 * 获取直播间详情
	 */
	getRoomDetail: (roomId) => {
		return api.get(`/live/room/${roomId}`);
	},

	/**
	 * 获取当前用户的直播间
	 */
	getMyRoom: () => {
		return api.get('/live/room/anchor/current');
	},

	/**
	 * 获取热门直播间
	 */
	getHotRooms: (limit = 10) => {
		return api.get('/live/rooms/hot', { limit });
	},

	/**
	 * 获取最新直播间
	 */
	getLatestRooms: (limit = 10) => {
		return api.get('/live/rooms/latest', { limit });
	},

	/**
	 * 获取分类下的直播间
	 */
	getRoomsByCategory: (categoryId, page = 1, size = 20) => {
		return api.get(`/live/rooms/category/${categoryId}`, { page, size });
	},

	/**
	 * 进入直播间
	 */
	enterRoom: (roomId) => {
		return api.post(`/live/room/${roomId}/enter`, {});
	},

	/**
	 * 离开直播间
	 */
	leaveRoom: (roomId) => {
		return api.post(`/live/room/${roomId}/leave`, {});
	},

	/**
	 * 点赞直播间
	 */
	likeRoom: (roomId) => {
		return api.post(`/live/room/${roomId}/like`, {});
	},

	/**
	 * 发送弹幕
	 */
	sendDanmaku: (roomId, data) => {
		return api.post(`/live/room/${roomId}/danmaku`, data);
	},

	/**
	 * 获取最近弹幕
	 */
	getRecentDanmaku: (roomId, limit = 50) => {
		return api.get(`/live/room/${roomId}/danmaku`, { limit });
	},

	/**
	 * 获取直播间统计
	 */
	getRoomStats: (roomId) => {
		return api.get(`/live/room/${roomId}/stats`);
	},

	/**
	 * 添加直播商品
	 */
	addProduct: (data) => {
		return api.post('/live/product/add', data);
	},

	/**
	 * 更新直播商品
	 */
	updateProduct: (productId, data) => {
		return api.put(`/live/product/update/${productId}`, data);
	},

	/**
	 * 删除直播商品
	 */
	deleteProduct: (productId) => {
		return api.delete(`/live/product/${productId}`);
	},

	/**
	 * 切换商品上下架状态
	 */
	toggleProductStatus: (productId) => {
		return api.post(`/live/product/${productId}/toggle`, {});
	},

	/**
	 * 获取商品详情
	 */
	getProductDetail: (productId) => {
		return api.get(`/live/product/${productId}`);
	},

	/**
	 * 获取直播间的商品列表
	 */
	getRoomProducts: (roomId) => {
		return api.get(`/live/product/room/${roomId}`);
	},

	/**
	 * 获取当前主播的商品列表
	 */
	getMyProducts: () => {
		return api.get('/live/product/anchor');
	}
};