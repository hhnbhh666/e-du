<template>
	<view class="anchor-live-container">
		<!-- 顶部导航 -->
		<view class="nav-bar">
			<text class="back-icon" @click="goBack">←</text>
			<text class="nav-title">直播管理</text>
			<text class="nav-right"></text>
		</view>

		<!-- 直播间信息 -->
		<view class="room-info-card">
			<view class="card-header">
				<text class="card-title">直播间信息</text>
				<text class="edit-btn" @click="editRoomInfo">编辑</text>
			</view>

			<view class="info-row">
				<text class="label">状态</text>
				<view class="status-badge" :class="{ living: roomInfo.status === 1 }">
					<text v-if="roomInfo.status === 1">● 直播中</text>
					<text v-else-if="roomInfo.status === 0">○ 未开播</text>
					<text v-else>直播已结束</text>
				</view>
			</view>

			<view class="info-row">
				<text class="label">标题</text>
				<text class="value">{{ roomInfo.title || '未设置' }}</text>
			</view>

			<view class="info-row">
				<text class="label">观众数</text>
				<text class="value">{{ roomInfo.viewerCount || 0 }}</text>
			</view>

			<view class="info-row">
				<text class="label">点赞数</text>
				<text class="value">{{ roomInfo.likeCount || 0 }}</text>
			</view>

			<!-- 直播控制按钮 -->
			<view class="live-control">
				<button
					class="control-btn create"
					v-if="!roomInfo.id"
					@click="createRoom"
				>
					创建直播间
				</button>
				<button
					class="control-btn start"
					v-else-if="roomInfo.status !== 1"
					@click="startLive"
				>
					开始直播
				</button>
				<button
					class="control-btn stop"
					v-else
					@click="stopLive"
				>
					结束直播
				</button>
			</view>
		</view>

		<!-- 商品管理 -->
		<view class="product-card">
			<view class="card-header">
				<text class="card-title">小黄车商品</text>
				<text class="add-btn" @click="showAddProduct = true">+ 添加</text>
			</view>

			<view class="product-list" v-if="products.length > 0">
				<view
					class="product-item"
					v-for="product in products"
					:key="product.id"
				>
					<image class="product-image" :src="product.image" mode="aspectFill"></image>
					<view class="product-detail">
						<text class="product-name">{{ product.name }}</text>
						<view class="product-price">
							<text class="price">¥{{ product.price }}</text>
							<text class="stock">库存: {{ product.stock }}</text>
						</view>
					</view>
					<view class="product-actions">
						<text
							class="status-text"
							:class="{ offline: product.status === 0 }"
							@click="toggleProductStatus(product)"
						>
							{{ product.status === 1 ? '已上架' : '已下架' }}
						</text>
						<text class="delete-btn" @click="deleteProduct(product.id)">删除</text>
					</view>
				</view>
			</view>

			<view class="empty-products" v-else>
				<text>暂无商品</text>
				<text class="hint">点击上方"添加"来添加直播商品</text>
			</view>
		</view>

		<!-- 直播统计 -->
		<view class="stats-card" v-if="roomInfo.id">
			<view class="card-header">
				<text class="card-title">直播统计</text>
			</view>
			<view class="stats-grid">
				<view class="stat-item">
					<text class="stat-value">{{ stats.totalViewers || 0 }}</text>
					<text class="stat-label">累计观看</text>
				</view>
				<view class="stat-item">
					<text class="stat-value">{{ stats.danmakuCount || 0 }}</text>
					<text class="stat-label">弹幕数</text>
				</view>
				<view class="stat-item">
					<text class="stat-value">{{ stats.productCount || 0 }}</text>
					<text class="stat-label">商品数</text>
				</view>
				<view class="stat-item">
					<text class="stat-value">{{ formatDuration(stats.totalWatchDuration) }}</text>
					<text class="stat-label">观看时长</text>
				</view>
			</view>
		</view>

		<!-- 添加商品弹窗 -->
		<view class="popup-mask" v-if="showAddProduct" @click="showAddProduct = false">
			<view class="popup-content" @click.stop>
				<view class="popup-header">
					<text class="popup-title">添加商品</text>
					<text class="popup-close" @click="showAddProduct = false">✕</text>
				</view>
				<view class="form-item">
					<text class="form-label">商品名称</text>
					<input
						class="form-input"
						v-model="productForm.name"
						placeholder="请输入商品名称"
					/>
				</view>
				<view class="form-item">
					<text class="form-label">商品图片</text>
					<view class="image-picker" @click="chooseImage">
						<image v-if="productForm.image" :src="productForm.image" mode="aspectFill"></image>
						<text v-else class="placeholder">+ 选择图片</text>
					</view>
				</view>
				<view class="form-item">
					<text class="form-label">商品链接</text>
					<input
						class="form-input"
						v-model="productForm.link"
						placeholder="请输入商品链接"
					/>
				</view>
				<view class="form-row">
					<view class="form-item half">
						<text class="form-label">原价(¥)</text>
						<input
							class="form-input"
							v-model="productForm.originalPrice"
							type="number"
							placeholder="原价"
						/>
					</view>
					<view class="form-item half">
						<text class="form-label">直播价(¥)</text>
						<input
							class="form-input"
							v-model="productForm.price"
							type="number"
							placeholder="直播价"
						/>
					</view>
				</view>
				<view class="form-item">
					<text class="form-label">库存</text>
					<input
						class="form-input"
						v-model="productForm.stock"
						type="number"
						placeholder="请输入库存数量"
					/>
				</view>
				<view class="form-item">
					<text class="form-label">商品描述</text>
					<textarea
						class="form-textarea"
						v-model="productForm.description"
						placeholder="请输入商品描述"
					></textarea>
				</view>

				<button class="submit-btn" @click="submitProduct">确认添加</button>
			</view>
		</view>

		<!-- 编辑直播间弹窗 -->
		<view class="popup-mask" v-if="showEditRoom" @click="showEditRoom = false">
			<view class="popup-content" @click.stop>
				<view class="popup-header">
					<text class="popup-title">编辑直播间</text>
					<text class="popup-close" @click="showEditRoom = false">✕</text>
				</view>
				<view class="form-item">
					<text class="form-label">直播间标题</text>
					<input
						class="form-input"
						v-model="editRoomForm.title"
						placeholder="请输入直播间标题"
					/>
				</view>
				<view class="form-item">
					<text class="form-label">直播间描述</text>
					<textarea
						class="form-textarea"
						v-model="editRoomForm.description"
						placeholder="请输入直播间描述"
					></textarea>
				</view>
				<view class="form-item">
					<text class="form-label">封面图片URL</text>
					<input
						class="form-input"
						v-model="editRoomForm.coverImage"
						placeholder="请输入封面图片URL"
					/>
				</view>
				<button class="submit-btn" @click="submitEditRoom">保存修改</button>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { liveApi } from '@/api/live.js'

const roomInfo = ref({})
const products = ref([])
const stats = ref({})
const showAddProduct = ref(false)
const showEditRoom = ref(false)

const editRoomForm = ref({
	title: '',
	description: '',
	coverImage: ''
})

const productForm = ref({
	name: '',
	image: '',
	link: '',
	originalPrice: '',
	price: '',
	stock: '',
	description: ''
})

function goBack() {
	uni.navigateBack()
}

function editRoomInfo() {
	if (!roomInfo.value.id) {
		uni.showToast({ title: '请先创建直播间', icon: 'none' })
		return
	}
	editRoomForm.value.title = roomInfo.value.title || ''
	editRoomForm.value.description = roomInfo.value.description || ''
	editRoomForm.value.coverImage = roomInfo.value.coverImage || ''
	showEditRoom.value = true
}

async function submitEditRoom() {
	if (!roomInfo.value.id) {
		uni.showToast({ title: '请先创建直播间', icon: 'none' })
		return
	}
	if (!editRoomForm.value.title) {
		uni.showToast({ title: '请输入直播间标题', icon: 'none' })
		return
	}

	try {
		uni.showLoading({ title: '保存中...' })
		await liveApi.updateRoom({
			id: roomInfo.value.id,
			title: editRoomForm.value.title,
			description: editRoomForm.value.description,
			coverImage: editRoomForm.value.coverImage
		})
		roomInfo.value.title = editRoomForm.value.title
		roomInfo.value.description = editRoomForm.value.description
		roomInfo.value.coverImage = editRoomForm.value.coverImage
		uni.hideLoading()
		showEditRoom.value = false
		uni.showToast({ title: '保存成功', icon: 'success' })
	} catch (e) {
		uni.hideLoading()
		uni.showToast({ title: e.message || '保存失败', icon: 'none' })
	}
}

async function startLive() {
	if (!roomInfo.value.id) {
		uni.showToast({ title: '请先创建直播间', icon: 'none' })
		return
	}

	try {
		uni.showLoading({ title: '正在开播...' })
		const res = await liveApi.startLive()
		roomInfo.value = res || {}
		uni.hideLoading()
		uni.showToast({ title: '直播已开始', icon: 'success' })
		// 开播成功后跳转到直播页面
		setTimeout(() => {
			uni.redirectTo({
				url: `/pages/live/live-room?roomId=${roomInfo.value.id}`
			})
		}, 1000)
	} catch (e) {
		uni.hideLoading()
		uni.showToast({ title: e.message || '开播失败', icon: 'none' })
	}
}

async function stopLive(silent = false) {
	if (silent) {
		try {
			const result = await liveApi.stopLive()
			roomInfo.value = result || {}
		} catch (e) {
			console.error('自动结束直播失败', e)
		}
	} else {
		uni.showModal({
			title: '确认结束',
			content: '确定要结束当前直播吗？',
			success: async (res) => {
				if (res.confirm) {
					try {
						uni.showLoading({ title: '正在结束直播...' })
						const result = await liveApi.stopLive()
						roomInfo.value = result || {}
						uni.hideLoading()
						uni.showToast({ title: '直播已结束', icon: 'success' })
					} catch (e) {
						uni.hideLoading()
						uni.showToast({ title: e.message || '结束失败', icon: 'none' })
					}
				}
			}
		})
	}
}

async function loadMyRoom() {
	try {
		const res = await liveApi.getMyRoom()
		roomInfo.value = res || {}
		if (roomInfo.value.id) {
			await loadStats()
		}
	} catch (e) {
		console.error('获取直播间信息失败', e)
	}
}

async function loadStats() {
	if (!roomInfo.value.id) return

	try {
		const res = await liveApi.getRoomStats(roomInfo.value.id)
		stats.value = res || {}
	} catch (e) {
		console.error('获取统计数据失败', e)
	}
}

async function loadProducts() {
	try {
		const res = await liveApi.getMyProducts()
		products.value = res || []
	} catch (e) {
		console.error('获取商品列表失败', e)
	}
}

async function createRoom() {
	if (roomInfo.value.id) return

	try {
		uni.showLoading({ title: '创建中...' })
		const res = await liveApi.createRoom({
			title: '我的直播间',
			description: '欢迎来到我的直播间',
			coverImage: ''
		})
		roomInfo.value = res || {}
		uni.hideLoading()
		uni.showToast({ title: '直播间创建成功', icon: 'success' })
	} catch (e) {
		uni.hideLoading()
		uni.showToast({ title: e.message || '创建失败', icon: 'none' })
	}
}

function chooseImage() {
	uni.chooseImage({
		count: 1,
		sizeType: ['compressed'],
		sourceType: ['album', 'camera'],
		success: (res) => {
			productForm.value.image = res.tempFilePaths[0]
		}
	})
}

async function submitProduct() {
	if (!productForm.value.name) {
		uni.showToast({ title: '请输入商品名称', icon: 'none' })
		return
	}
	if (!productForm.value.image) {
		uni.showToast({ title: '请选择商品图片', icon: 'none' })
		return
	}
	if (!productForm.value.link) {
		uni.showToast({ title: '请输入商品链接', icon: 'none' })
		return
	}
	if (!productForm.value.originalPrice || !productForm.value.price) {
		uni.showToast({ title: '请输入价格', icon: 'none' })
		return
	}
	if (!productForm.value.stock) {
		uni.showToast({ title: '请输入库存', icon: 'none' })
		return
	}

	try {
		uni.showLoading({ title: '添加中...' })
		await liveApi.addProduct({
			roomId: roomInfo.value.id,
			...productForm.value
		})
		uni.hideLoading()
		showAddProduct.value = false
		resetProductForm()
		await loadProducts()
		uni.showToast({ title: '商品添加成功', icon: 'success' })
	} catch (e) {
		uni.hideLoading()
		uni.showToast({ title: e.message || '添加失败', icon: 'none' })
	}
}

async function toggleProductStatus(product) {
	try {
		await liveApi.toggleProductStatus(product.id)
		product.status = product.status === 1 ? 0 : 1
		uni.showToast({ title: product.status === 1 ? '已上架' : '已下架', icon: 'success' })
	} catch (e) {
		uni.showToast({ title: e.message || '操作失败', icon: 'none' })
	}
}

async function deleteProduct(productId) {
	uni.showModal({
		title: '确认删除',
		content: '确定要删除这个商品吗？',
		success: async (res) => {
			if (res.confirm) {
				try {
					uni.showLoading({ title: '删除中...' })
					await liveApi.deleteProduct(productId)
					await loadProducts()
					uni.showToast({ title: '删除成功', icon: 'success' })
				} catch (e) {
					uni.showToast({ title: '删除失败', icon: 'none' })
				}
			}
		}
	})
}

function resetProductForm() {
	productForm.value = {
		name: '',
		image: '',
		link: '',
		originalPrice: '',
		price: '',
		stock: '',
		description: ''
	}
}

function formatDuration(seconds) {
	if (!seconds) return '0秒'
	const hours = Math.floor(seconds / 3600)
	const minutes = Math.floor((seconds % 3600) / 60)
	const secs = seconds % 60
	if (hours > 0) {
		return `${hours}小时${minutes}分`
	}
	if (minutes > 0) {
		return `${minutes}分${secs}秒`
	}
	return `${secs}秒`
}

onMounted(async () => {
	await loadMyRoom()
	await loadProducts()
})

onUnload(() => {
	// 离开页面时如果正在直播，自动结束（静默模式）
	if (roomInfo.value.status === 1) {
		stopLive(true)
	}
})

onLoad(() => {
	// 移除重复调用，避免多次创建
})
</script>

<style scoped>
.anchor-live-container {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 40rpx;
}

.nav-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 30rpx;
	background-color: #fff;
	border-bottom: 1rpx solid #f0f0f0;
}

.back-icon {
	font-size: 36rpx;
	color: #333;
}

.nav-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.nav-right {
	width: 36rpx;
}

.room-info-card,
.product-card,
.stats-card {
	margin: 20rpx;
	padding: 30rpx;
	background-color: #fff;
	border-radius: 20rpx;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.card-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}

.edit-btn,
.add-btn {
	font-size: 26rpx;
	color: #ff6b6b;
}

.info-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.info-row:last-child {
	border-bottom: none;
}

.label {
	font-size: 26rpx;
	color: #999;
}

.value {
	font-size: 26rpx;
	color: #333;
}

.status-badge {
	padding: 8rpx 20rpx;
	border-radius: 20rpx;
	background-color: #f0f0f0;
}

.status-badge.living {
	background-color: #ff6b6b;
}

.status-badge.living text {
	color: #fff;
}

.live-control {
	margin-top: 30rpx;
}

.control-btn {
	width: 100%;
	padding: 24rpx;
	border-radius: 40rpx;
	font-size: 28rpx;
	color: #fff;
	border: none;
}

.control-btn.start {
	background-color: #ff6b6b;
}

.control-btn.create {
	background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.control-btn.stop {
	background-color: #999;
}

.product-list {
	display: flex;
	flex-direction: column;
}

.product-item {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.product-image {
	width: 100rpx;
	height: 100rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}

.product-detail {
	flex: 1;
}

.product-name {
	display: block;
	font-size: 26rpx;
	color: #333;
	margin-bottom: 8rpx;
}

.product-price {
	display: flex;
	align-items: center;
}

.price {
	font-size: 28rpx;
	color: #ff6b6b;
	font-weight: bold;
	margin-right: 20rpx;
}

.stock {
	font-size: 22rpx;
	color: #999;
}

.product-actions {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
}

.status-text {
	font-size: 22rpx;
	color: #52c41a;
	margin-bottom: 10rpx;
}

.status-text.offline {
	color: #999;
}

.delete-btn {
	font-size: 22rpx;
	color: #ff4d4f;
}

.empty-products {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 60rpx 0;
	color: #999;
}

.hint {
	font-size: 24rpx;
	margin-top: 10rpx;
}

.stats-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20rpx;
}

.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx;
	background-color: #f9f9f9;
	border-radius: 12rpx;
}

.stat-value {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
}

.stat-label {
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
}

.popup-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 999;
	display: flex;
	align-items: center;
	justify-content: center;
}

.popup-content {
	width: 90%;
	max-width: 600rpx;
	max-height: 80vh;
	overflow-y: auto;
	background-color: #fff;
	border-radius: 20rpx;
	padding: 30rpx;
	z-index: 1000;
}

.popup-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.popup-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.popup-close {
	font-size: 40rpx;
	color: #999;
}

.form-item {
	margin-bottom: 24rpx;
}

.form-item.half {
	flex: 1;
}

.form-row {
	display: flex;
	gap: 20rpx;
}

.form-label {
	display: block;
	font-size: 26rpx;
	color: #333;
	margin-bottom: 12rpx;
}

.form-input {
	width: 100%;
	padding: 20rpx;
	border: 1rpx solid #e8e8e8;
	border-radius: 12rpx;
	font-size: 26rpx;
}

.form-textarea {
	width: 100%;
	padding: 20rpx;
	border: 1rpx solid #e8e8e8;
	border-radius: 12rpx;
	font-size: 26rpx;
	min-height: 120rpx;
}

.image-picker {
	width: 200rpx;
	height: 200rpx;
	border: 2rpx dashed #ddd;
	border-radius: 12rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	overflow: hidden;
}

.image-picker image {
	width: 100%;
	height: 100%;
}

.placeholder {
	color: #999;
	font-size: 26rpx;
}

.preview-image {
	width: 200rpx;
	height: 200rpx;
	border-radius: 12rpx;
	margin-top: 10rpx;
}

.submit-btn {
	width: 100%;
	padding: 24rpx;
	background-color: #ff6b6b;
	color: #fff;
	border-radius: 40rpx;
	font-size: 28rpx;
	border: none;
	margin-top: 20rpx;
}
</style>
