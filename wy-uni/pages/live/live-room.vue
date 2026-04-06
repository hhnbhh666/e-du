<template>
	<view class="live-room-container">
		<!-- 视频播放区域 -->
		<view class="video-area">
			<!-- 微信小程序：使用原生 live-player 组件 -->
			<!-- #ifdef MP-WEIXIN -->
			<live-player
				class="live-player"
				src="{{ videoSource }}"
				autoplay
				muted
				object-fit="fillCrop"
				mode="RTC"
				sound-mode="speaker"
				@statechange="onLivePlayerStateChange"
				@error="onLivePlayerError"
			></live-player>
			<!-- #endif -->
			<!-- #ifndef MP-WEIXIN -->
			<!-- H5 浏览器和 APP：使用 video 标签 -->
			<video
				id="liveVideo"
				class="video-player"
				:src="videoSource"
				:poster="roomInfo.coverImage"
				controls
				:autoplay="true"
				object-fit="fill"
				:muted="true"
				@error="onVideoError"
				@play="onVideoPlay"
			></video>
			<!-- #endif -->

			<!-- 顶部信息栏 -->
			<view class="top-bar">
				<view class="anchor-info" @click="goToAnchor">
					<image class="anchor-avatar" :src="roomInfo.anchorAvatar || '/static/logo.png'" mode="aspectFill"></image>
					<view class="anchor-text">
						<text class="anchor-name">{{ roomInfo.anchorName }}</text>
						<text class="viewer-count">👁 {{ viewerCount }}</text>
					</view>
				</view>
				<view class="follow-btn" v-if="!isAnchor" @click="followAnchor">
					<text>+ 关注</text>
				</view>
				<view class="close-btn" @click="goBack">
					<text>✕</text>
				</view>
			</view>

			<!-- 直播信息 -->
			<view class="live-info">
				<text class="room-title">{{ roomInfo.title }}</text>
			</view>
		</view>

		<!-- 弹幕显示区域 -->
		<view class="danmaku-area" id="danmakuContainer">
			<view
				v-for="(danmaku, index) in danmakuList"
				:key="index"
				class="danmaku-item"
				:style="{ color: danmaku.color }"
			>
				<text class="danmaku-nickname">{{ danmaku.nickname }}:</text>
				<text class="danmaku-content">{{ danmaku.content }}</text>
			</view>
		</view>

		<!-- 商品展示区域 -->
		<view class="product-area" :class="{ show: showProducts }">
			<view class="product-header">
				<text class="product-title">直播商品</text>
				<text class="product-close" @click="showProducts = false">✕</text>
			</view>
			<scroll-view class="product-list" scroll-x="true">
				<view
					v-for="product in products"
					:key="product.id"
					class="product-card"
					@click="openProductLink(product)"
				>
					<image class="product-image" :src="product.image" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ product.name }}</text>
						<view class="product-price">
							<text class="current-price">¥{{ product.price }}</text>
							<text class="original-price" v-if="product.originalPrice > product.price">
								¥{{ product.originalPrice }}
							</text>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>

		<!-- 底部交互区 -->
		<view class="bottom-area">
			<!-- 小黄车按钮 -->
			<view class="tool-btn" @click="toggleProducts">
				<text class="tool-icon">🛒</text>
				<text class="tool-text">小黄车</text>
				<view class="product-badge" v-if="products.length > 0">{{ products.length }}</view>
			</view>

			<!-- 弹幕输入 -->
			<view class="danmaku-input-area">
				<input
					class="danmaku-input"
					v-model="danmakuContent"
					placeholder="说点什么..."
					@confirm="sendDanmaku"
					maxlength="100"
				/>
				<text class="send-btn" @click="sendDanmaku">发送</text>
			</view>

			<!-- 点赞 -->
			<view class="tool-btn" @click="likeRoom">
				<text class="tool-icon">❤️</text>
				<text class="tool-text">{{ likeCount }}</text>
			</view>

			<!-- 分享 -->
			<view class="tool-btn" @click="shareRoom">
				<text class="tool-icon">📤</text>
				<text class="tool-text">分享</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { liveApi } from '@/api/live.js'

// 检测是否是微信小程序
const isMiniProgram = computed(() => {
  // #ifdef MP-WEIXIN
  return true
  // #endif
  return false
})

const roomId = ref(null)
const roomInfo = ref({})
const viewerCount = ref(0)
const likeCount = ref(0)
const danmakuList = ref([])
const danmakuContent = ref('')
const products = ref([])
const showProducts = ref(false)
const isAnchor = ref(false)
const videoSource = ref('')

let hlsPlayer = null
let websocket = null
let heartbeatTimer = null
let danmakuTimer = null

watch(videoSource, (newSource) => {
	if (newSource) {
		nextTick(() => {
			initPlayer()
		})
	}
})

function initPlayer() {
	// #ifdef MP-WEIXIN
	// 微信小程序：使用原生 live-player 组件，不需要手动初始化
	console.log('小程序原生播放器已初始化，地址:', videoSource.value)
	return
	// #endif
	// #ifndef MP-WEIXIN
	// H5 浏览器：使用 video 标签 + hls.js
	if (!videoSource.value) return

	const video = document.getElementById('liveVideo')
	if (!video) return

	console.log('初始化H5播放器，地址:', videoSource.value)

	if (typeof Hls !== 'undefined' && Hls.isSupported()) {
		hlsPlayer = new Hls()
		hlsPlayer.loadSource(videoSource.value)
		hlsPlayer.attachMedia(video)

		hlsPlayer.on(Hls.Events.MANIFEST_PARSED, function() {
			console.log('HLS解析完成，开始播放')
			video.play().catch(e => console.log('自动播放失败:', e))
		})

		hlsPlayer.on(Hls.Events.ERROR, function(event, data) {
			console.error('HLS播放错误:', data)
			if (data.fatal) {
				switch (data.type) {
					case Hls.ErrorTypes.NETWORK_ERROR:
						console.log('网络错误，尝试重连...')
						hlsPlayer.startLoad()
						break
					case Hls.ErrorTypes.MEDIA_ERROR:
						console.log('媒体错误，尝试恢复...')
						hlsPlayer.recoverMediaError()
						break
					default:
						hlsPlayer.destroy()
						hlsPlayer = null
						break
				}
			}
		})
	} else if (video.canPlayType('application/vnd.apple.mpegurl')) {
		video.src = videoSource.value
		video.addEventListener('loadedmetadata', function() {
			console.log('HLS解析完成(Safari)，开始播放')
			video.play()
		})
	} else {
		console.log('浏览器不支持HLS，使用原生video标签')
		video.src = videoSource.value
		video.play().catch(e => console.log('播放失败:', e))
	}
	// #endif
}

// #ifdef MP-WEIXIN
function onLivePlayerStateChange(e) {
	console.log('live-player 状态变化:', e.detail)
}

function onLivePlayerError(e) {
	console.error('live-player 错误:', e.detail)
	uni.showToast({
		title: '播放错误: ' + (e.detail.errMsg || '未知错误'),
		icon: 'none',
		duration: 3000
	})
}
// #endif

// #ifndef MP-WEIXIN
// H5 和 APP 端的 video 事件处理
function onVideoPlay(e) {
	console.log('video 开始播放:', e)
	uni.showToast({
		title: '开始播放',
		icon: 'success',
		duration: 2000
	})
}

function onVideoError(e) {
	console.error('video 播放错误:', JSON.stringify(e))
	console.error('当前视频源:', videoSource.value)
	
	// APP 端无法使用 document，直接用 uni.showToast 显示错误
	uni.showModal({
		title: '播放失败',
		content: '视频地址：' + videoSource.value + '\n\n请检查 OBS 是否在推流到：rtmp://192.168.0.18/live/testroom123',
		showCancel: false
	})
}
// #endif

function goBack() {
	uni.navigateBack()
}

function goToAnchor() {
	uni.showToast({ title: '进入主播主页', icon: 'none' })
}

function followAnchor() {
	uni.showToast({ title: '关注成功', icon: 'success' })
}

function toggleProducts() {
	showProducts.value = !showProducts.value
}

function openProductLink(product) {
	uni.setClipboardData({
		data: product.link,
		success: () => {
			uni.showToast({ title: '已复制商品链接', icon: 'success' })
		}
	})
}

async function sendDanmaku() {
	if (!danmakuContent.value.trim()) {
		return
	}

	try {
		const res = await liveApi.sendDanmaku(roomId.value, {
			content: danmakuContent.value,
			color: '#FFFFFF',
			type: 1
		})

		danmakuList.value.push({
			nickname: res.nickname,
			content: res.content,
			color: res.color || '#FFFFFF'
		})

		danmakuContent.value = ''

		setTimeout(() => {
			scrollToBottom()
		}, 100)
	} catch (e) {
		uni.showToast({ title: '发送失败', icon: 'none' })
	}
}

async function likeRoom() {
	try {
		await liveApi.likeRoom(roomId.value)
		likeCount.value++
		uni.showToast({ title: '点赞成功', icon: 'success' })
	} catch (e) {
		console.error('点赞失败', e)
	}
}

function shareRoom() {
	uni.showShareMenu({
		withShareTicket: true,
		menus: ['shareAppMessage', 'shareTimeline']
	})
}

function scrollToBottom() {
	const query = uni.createSelectorQuery().in(getCurrentInstance())
	query.select('#danmakuContainer').boundingClientRect()
}

function connectWebSocket() {
	const token = uni.getStorageSync('token') || ''
	const wsUrl = `ws://localhost:8080/ws/danmaku/${roomId.value}?token=${token}`

	uni.connectSocket({
		url: wsUrl,
		success: () => {
			console.log('WebSocket连接成功')
		},
		fail: (err) => {
			console.error('WebSocket连接失败', err)
		}
	})

	uni.onSocketOpen(() => {
		console.log('WebSocket已打开')
		startHeartbeat()
	})

	uni.onSocketMessage((res) => {
		try {
			const data = JSON.parse(res.data)
			handleWebSocketMessage(data)
		} catch (e) {
			console.error('解析WebSocket消息失败', e)
		}
	})

	uni.onSocketClose(() => {
		console.log('WebSocket已关闭')
		stopHeartbeat()
	})

	uni.onSocketError((err) => {
		console.error('WebSocket错误', err)
	})
}

function handleWebSocketMessage(data) {
	switch (data.type) {
		case 'danmaku':
			danmakuList.value.push({
				nickname: data.data.nickname,
				content: data.data.content,
				color: data.data.color || '#FFFFFF'
			})
			if (danmakuList.value.length > 100) {
				danmakuList.value.shift()
			}
			break
		case 'viewer_count':
			viewerCount.value = data.data
			break
		case 'like':
			likeCount.value++
			break
		case 'heartbeat':
			break
	}
}

function startHeartbeat() {
	heartbeatTimer = setInterval(() => {
		uni.sendSocketMessage({
			data: JSON.stringify({ type: 'heartbeat' })
		})
	}, 30000)
}

function stopHeartbeat() {
	if (heartbeatTimer) {
		clearInterval(heartbeatTimer)
		heartbeatTimer = null
	}
}

async function loadRoomInfo() {
	try {
		const res = await liveApi.getRoomDetail(roomId.value)
		roomInfo.value = res || {}
		viewerCount.value = res.viewerCount || 0
		likeCount.value = res.likeCount || 0
		videoSource.value = res.pullUrl || ''
	} catch (e) {
		console.error('加载直播间信息失败', e)
	}
}

async function loadProducts() {
	try {
		const res = await liveApi.getRoomProducts(roomId.value)
		products.value = res || []
	} catch (e) {
		console.error('加载商品列表失败', e)
	}
}

async function enterRoom() {
	try {
		await liveApi.enterRoom(roomId.value)
	} catch (e) {
		console.error('进入直播间失败', e)
	}
}

async function leaveRoom() {
	try {
		await liveApi.leaveRoom(roomId.value)
	} catch (e) {
		console.error('离开直播间失败', e)
	}
}

onLoad(async (options) => {
	const idStr = options.roomId
	
	// 处理测试房间的特殊情况
	if (idStr === 'test') {
		roomId.value = 'test'
		roomInfo.value = {
			title: '我的测试直播',
			anchorName: '测试主播',
			anchorAvatar: 'https://picsum.photos/200/200?random=99',
			viewerCount: 10,
			likeCount: 50
		}
		// #ifdef H5
		// H5 浏览器：使用 FLV 格式 + flv.js（本地地址）
		videoSource.value = 'http://localhost:8081/live/testroom123.flv'
		// #endif
		// #ifdef MP-WEIXIN
		// 微信小程序：使用 FLV 格式 + live-player（ngrok 公网地址）
		videoSource.value = 'https://emma-rockiest-positively.ngrok-free.dev/live/testroom123.flv'
		// #endif
		// #ifdef APP-PLUS
		// APP 端：使用 HLS 格式（.m3u8）（ngrok 公网地址）
		videoSource.value = 'https://emma-rockiest-positively.ngrok-free.dev/live/testroom123.m3u8'
		// #endif
		viewerCount.value = 10
		likeCount.value = 50
		return
	}
	
	// 正常的直播间处理
	roomId.value = idStr ? parseInt(idStr, 10) : null
	if (!roomId.value) {
		uni.showToast({ title: '参数错误', icon: 'error' })
		return
	}

	await loadRoomInfo()
	await loadProducts()
	await enterRoom()
	connectWebSocket()
})

onUnmounted(() => {
	if (hlsPlayer) {
		hlsPlayer.destroy()
		hlsPlayer = null
	}
	uni.closeSocket()
	stopHeartbeat()
	leaveRoom()
})
</script>

<style scoped>
.live-room-container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #000;
}

.video-area {
	position: relative;
	width: 100%;
	height: 500rpx;
}

.video-player {
	width: 100%;
	height: 100%;
}

.live-player {
	width: 100%;
	height: 100%;
}

.top-bar {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 30rpx;
	background: linear-gradient(to bottom, rgba(0, 0, 0, 0.6), transparent);
}

.anchor-info {
	display: flex;
	align-items: center;
	background-color: rgba(0, 0, 0, 0.5);
	padding: 10rpx 20rpx;
	border-radius: 40rpx;
}

.anchor-avatar {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	margin-right: 12rpx;
}

.anchor-text {
	display: flex;
	flex-direction: column;
}

.anchor-name {
	font-size: 24rpx;
	color: #fff;
}

.viewer-count {
	font-size: 20rpx;
	color: #fff;
	opacity: 0.8;
}

.follow-btn {
	background-color: #ff6b6b;
	color: #fff;
	font-size: 24rpx;
	padding: 12rpx 30rpx;
	border-radius: 30rpx;
}

.close-btn {
	width: 60rpx;
	height: 60rpx;
	background-color: rgba(0, 0, 0, 0.5);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #fff;
	font-size: 32rpx;
}

.live-info {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 20rpx 30rpx;
	background: linear-gradient(to top, rgba(0, 0, 0, 0.6), transparent);
}

.room-title {
	font-size: 28rpx;
	color: #fff;
}

.danmaku-area {
	position: absolute;
	top: 200rpx;
	left: 0;
	right: 200rpx;
	height: 300rpx;
	overflow: hidden;
	pointer-events: none;
}

.danmaku-item {
	display: flex;
	padding: 10rpx 20rpx;
	font-size: 26rpx;
	color: #fff;
	text-shadow: 1rpx 1rpx 2rpx #000;
	animation: danmakuFly 8s linear forwards;
}

@keyframes danmakuFly {
	0% {
		transform: translateX(100%);
		opacity: 1;
	}
	90% {
		opacity: 1;
	}
	100% {
		transform: translateX(-100%);
		opacity: 0;
	}
}

.danmaku-nickname {
	color: #ffd700;
	margin-right: 10rpx;
}

.product-area {
	position: absolute;
	bottom: 120rpx;
	left: 0;
	right: 0;
	background-color: rgba(255, 255, 255, 0.95);
	border-radius: 24rpx 24rpx 0 0;
	transform: translateY(100%);
	transition: transform 0.3s ease;
}

.product-area.show {
	transform: translateY(0);
}

.product-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 30rpx;
	border-bottom: 1rpx solid #eee;
}

.product-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.product-close {
	font-size: 32rpx;
	color: #999;
}

.product-list {
	display: flex;
	padding: 20rpx;
	white-space: nowrap;
}

.product-card {
	display: inline-block;
	width: 200rpx;
	margin-right: 20rpx;
	background-color: #fff;
	border-radius: 12rpx;
	overflow: hidden;
}

.product-image {
	width: 200rpx;
	height: 200rpx;
}

.product-info {
	padding: 12rpx;
}

.product-name {
	display: block;
	font-size: 24rpx;
	color: #333;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	margin-bottom: 8rpx;
}

.product-price {
	display: flex;
	align-items: center;
}

.current-price {
	font-size: 28rpx;
	color: #ff6b6b;
	font-weight: bold;
	margin-right: 10rpx;
}

.original-price {
	font-size: 20rpx;
	color: #999;
	text-decoration: line-through;
}

.bottom-area {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	display: flex;
	align-items: center;
	padding: 20rpx 30rpx;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	background-color: rgba(255, 255, 255, 0.95);
	border-top: 1rpx solid #eee;
}

.tool-btn {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-right: 30rpx;
	position: relative;
}

.tool-icon {
	font-size: 40rpx;
	margin-bottom: 4rpx;
}

.tool-text {
	font-size: 20rpx;
	color: #666;
}

.product-badge {
	position: absolute;
	top: -8rpx;
	right: -8rpx;
	background-color: #ff6b6b;
	color: #fff;
	font-size: 18rpx;
	padding: 4rpx 10rpx;
	border-radius: 20rpx;
}

.danmaku-input-area {
	flex: 1;
	display: flex;
	align-items: center;
	background-color: #f5f5f5;
	border-radius: 40rpx;
	padding: 10rpx 20rpx;
	margin-right: 20rpx;
}

.danmaku-input {
	flex: 1;
	font-size: 26rpx;
}

.send-btn {
	font-size: 26rpx;
	color: #ff6b6b;
	margin-left: 10rpx;
}
</style>