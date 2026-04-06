<template>
	<view class="live-list-container">
		<!-- 顶部导航 -->
		<view class="nav-bar">
			<text class="nav-title">直播广场</text>
			<view class="nav-buttons">
				<view class="test-btn" @click="goToTestRoom">
					<text class="test-text">测试直播</text>
				</view>
				<view class="anchor-btn" @click="goToAnchor">
					<text class="anchor-icon">🎬</text>
					<text class="anchor-text">我要开播</text>
				</view>
			</view>
		</view>

		<!-- 分类标签 -->
		<scroll-view class="category-scroll" scroll-x="true">
			<view
				class="category-tag"
				:class="{ active: currentCategory === 0 }"
				@click="selectCategory(0)"
			>
				全部
			</view>
			<view
				v-for="cat in categories"
				:key="cat.id"
				class="category-tag"
				:class="{ active: currentCategory === cat.id }"
				@click="selectCategory(cat.id)"
			>
				{{ cat.name }}
			</view>
		</scroll-view>

		<!-- 直播列表 -->
		<scroll-view 
			class="live-list" 
			scroll-y="true" 
			@scrolltolower="loadMore"
			refresher-enabled
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
		>
			<view class="live-grid">
				<view
					v-for="room in liveRooms"
					:key="room.id"
					class="live-card"
					@click="enterRoom(room.id)"
				>
					<view class="card-cover">
						<image :src="room.coverImage || '/static/logo.png'" mode="aspectFill"></image>
						<view class="live-tag" v-if="room.status === 1">
							<text class="live-dot"></text>
							<text class="live-text">直播中</text>
						</view>
						<view class="viewer-count">
							<text>👁 {{ room.viewerCount || 0 }}</text>
						</view>
					</view>
					<view class="card-info">
						<view class="anchor-info">
							<image class="anchor-avatar" :src="room.anchorAvatar || '/static/logo.png'" mode="aspectFill"></image>
							<text class="anchor-name">{{ room.anchorName || '主播' }}</text>
						</view>
						<text class="room-title">{{ room.title }}</text>
						<view class="room-tags" v-if="room.categoryName">
							<text class="tag">{{ room.categoryName }}</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 加载状态 -->
			<view class="load-more" v-if="loading">
				<text>加载中...</text>
			</view>
			<view class="no-more" v-if="noMore && liveRooms.length > 0">
				<text>没有更多了</text>
			</view>
			<view class="empty-state" v-if="!loading && liveRooms.length === 0">
				<text class="empty-icon">📺</text>
				<text class="empty-text">暂无直播</text>
				<text class="empty-hint">主播正在赶来的路上...</text>
			</view>
		</scroll-view>

		<!-- 底部导航 -->
		<view class="bottom-tab-bar">
			<view
				v-for="(tab, index) in bottomTabs"
				:key="index"
				class="tab-item"
				:class="{ active: currentTab === index }"
				@click="switchTab(index)"
			>
				<text class="tab-icon">{{ tab.icon }}</text>
				<text class="tab-name">{{ tab.name }}</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { liveApi } from '@/api/live.js'

const currentTab = ref(3)
const bottomTabs = [
	{ name: '首页', icon: '🏠' },
	{ name: '找课', icon: '📝' },
	{ name: '刷题', icon: '✏' },
	{ name: '直播', icon: '📺' },
	{ name: '我', icon: '👤' }
]

const currentCategory = ref(0)
const categories = ref([])
const liveRooms = ref([])
const loading = ref(false)
const refreshing = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = 20

function switchTab(index) {
	if (index === 3) return
	const routes = ['/pages/index/index', '/pages/index/index', '/pages/quiz/quiz', '/pages/live/live-list', '/pages/index/index']
	uni.reLaunch({ url: routes[index] })
}

function selectCategory(categoryId) {
	currentCategory.value = categoryId
	page.value = 1
	noMore.value = false
	liveRooms.value = []
	loadLiveRooms()
}

async function loadLiveRooms() {
	if (loading.value) return
	loading.value = true

	try {
		let list
		if (currentCategory.value === 0) {
			list = await liveApi.getLatestRooms(pageSize)
		} else {
			const res = await liveApi.getRoomsByCategory(currentCategory.value, page.value, pageSize)
			list = res?.list || []
		}

		list = list || []
		if (page.value === 1) {
			liveRooms.value = list
		} else {
			liveRooms.value = [...liveRooms.value, ...list]
		}

		if (list.length < pageSize) {
			noMore.value = true
		}
	} catch (e) {
		console.error('加载直播列表失败', e)
	} finally {
		loading.value = false
	}
}

function loadMore() {
	if (noMore.value || loading.value) return
	page.value++
	loadLiveRooms()
}

function enterRoom(roomId) {
	uni.navigateTo({
		url: `/pages/live/live-room?roomId=${roomId}`
	})
}

function goToTestRoom() {
	// #ifdef MP-WEIXIN
	// 微信小程序：直接进入直播间页面（用原生 live-player 组件）
	uni.navigateTo({
		url: '/pages/live/live-room?roomId=test'
	})
	// #endif
	// #ifdef H5
	// H5 浏览器：打开独立 FLV 播放器
	window.open('/static/flv-player.html?url=http://localhost:8081/live/testroom123.flv', '_blank')
	// #endif
	// #ifdef APP-PLUS
	// APP 端：进入直播间页面
	uni.navigateTo({
		url: '/pages/live/live-room?roomId=test'
	})
	// #endif
}

function goToAnchor() {
	uni.navigateTo({
		url: '/pages/live/anchor-live'
	})
}

async function onRefresh() {
	refreshing.value = true
	page.value = 1
	noMore.value = false
	await loadLiveRooms()
	refreshing.value = false
}

onMounted(() => {
	loadLiveRooms()
})

onLoad((options) => {
	if (options.categoryId) {
		currentCategory.value = parseInt(options.categoryId, 10)
	}
	loadLiveRooms()
})
</script>

<style scoped>
.live-list-container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.nav-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	background-color: #fff;
	border-bottom: 1rpx solid #f0f0f0;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.nav-buttons {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.test-btn {
	padding: 16rpx 32rpx;
	background: linear-gradient(135deg, #667eea, #764ba2);
	border-radius: 40rpx;
	box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.test-text {
	font-size: 24rpx;
	color: #fff;
	font-weight: 500;
}

.nav-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.anchor-btn {
	display: flex;
	align-items: center;
	padding: 10rpx 20rpx;
	background: linear-gradient(135deg, #ff6b6b, #ff4757);
	border-radius: 30rpx;
}

.anchor-icon {
	font-size: 24rpx;
	margin-right: 6rpx;
}

.anchor-text {
	font-size: 24rpx;
	color: #fff;
	font-weight: 500;
}

.category-scroll {
	display: flex;
	white-space: nowrap;
	padding: 20rpx 30rpx;
	background-color: #fff;
}

.category-tag {
	display: inline-block;
	padding: 12rpx 30rpx;
	margin-right: 20rpx;
	font-size: 26rpx;
	color: #666;
	background-color: #f5f5f5;
	border-radius: 30rpx;
}

.category-tag.active {
	color: #fff;
	background-color: #ff6b6b;
}

.live-list {
	flex: 1;
	padding: 20rpx 30rpx;
}

.live-grid {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
}

.live-card {
	width: 48%;
	margin-bottom: 20rpx;
	background-color: #fff;
	border-radius: 16rpx;
	overflow: hidden;
}

.card-cover {
	position: relative;
	width: 100%;
	height: 240rpx;
}

.card-cover image {
	width: 100%;
	height: 100%;
}

.live-tag {
	position: absolute;
	top: 16rpx;
	left: 16rpx;
	display: flex;
	align-items: center;
	padding: 6rpx 16rpx;
	background-color: rgba(255, 0, 0, 0.8);
	border-radius: 20rpx;
}

.live-dot {
	width: 12rpx;
	height: 12rpx;
	background-color: #fff;
	border-radius: 50%;
	margin-right: 8rpx;
	animation: pulse 1.5s infinite;
}

@keyframes pulse {
	0%, 100% { opacity: 1; }
	50% { opacity: 0.5; }
}

.live-text {
	font-size: 20rpx;
	color: #fff;
}

.viewer-count {
	position: absolute;
	bottom: 16rpx;
	right: 16rpx;
	padding: 6rpx 16rpx;
	background-color: rgba(0, 0, 0, 0.6);
	border-radius: 20rpx;
	font-size: 20rpx;
	color: #fff;
}

.card-info {
	padding: 20rpx;
}

.anchor-info {
	display: flex;
	align-items: center;
	margin-bottom: 12rpx;
}

.anchor-avatar {
	width: 40rpx;
	height: 40rpx;
	border-radius: 50%;
	margin-right: 10rpx;
}

.anchor-name {
	font-size: 22rpx;
	color: #666;
}

.room-title {
	display: block;
	font-size: 28rpx;
	color: #333;
	margin-bottom: 12rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.room-tags {
	display: flex;
}

.tag {
	font-size: 20rpx;
	color: #ff6b6b;
	background-color: #fff0f0;
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
}

.load-more,
.no-more {
	text-align: center;
	padding: 30rpx;
	color: #999;
	font-size: 24rpx;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 40rpx;
}

.empty-icon {
	font-size: 100rpx;
	margin-bottom: 24rpx;
}

.empty-text {
	font-size: 32rpx;
	color: #333;
	margin-bottom: 12rpx;
}

.empty-hint {
	font-size: 26rpx;
	color: #999;
}

.bottom-tab-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 100rpx;
	background-color: #fff;
	display: flex;
	justify-content: space-around;
	align-items: center;
	border-top: 1rpx solid #f0f0f0;
	padding-bottom: env(safe-area-inset-bottom);
	z-index: 1000;
}

.tab-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.tab-icon {
	font-size: 40rpx;
	margin-bottom: 6rpx;
}

.tab-name {
	font-size: 22rpx;
	color: #999;
}

.tab-item.active .tab-name {
	color: #ff6b6b;
}
</style>