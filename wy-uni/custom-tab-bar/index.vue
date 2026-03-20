<template>
	<view class="tab-bar-container">
		<view 
			class="tab-item" 
			v-for="(item, index) in tabList" 
			:key="index"
			:class="{ active: selected === index }"
			@click="switchTab(index)"
		>
			<text class="tab-icon">{{ item.icon }}</text>
			<text class="tab-text">{{ item.name }}</text>
			<view class="tab-badge" v-if="item.badge > 0">
				<text class="badge-text">{{ item.badge }}</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			selected: 0,
			tabList: [
				{ name: '首页', icon: '🏠', badge: 0 },
				{ name: '新增', icon: '📝', badge: 0 },
				{ name: '刷题', icon: '✏️', badge: 0 },
				{ name: '社区', icon: '💬', badge: 0 },
				{ name: '我', icon: '👤', badge: 0 }
			]
		}
	},
	methods: {
		switchTab(index) {
			if (this.selected === index) return
			this.selected = index
			const isTeacher = true
			if (index === 1 && isTeacher) {
				uni.reLaunch({ url: '/pages/teacher/teacher-manage' })
				return
			}
			const pages = [
				'/pages/index/index',
				'/pages/index/index',
				'/pages/quiz/quiz',
				'/pages/index/index',
				'/pages/index/index'
			]
			uni.reLaunch({ url: pages[index] })
		}
	}
}
</script>

<style scoped>
.tab-bar-container {
	display: flex;
	justify-content: space-around;
	align-items: center;
	height: 100rpx;
	background-color: #ffffff;
	border-top: 1rpx solid #f0f0f0;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	z-index: 999;
}

.tab-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	flex: 1;
	height: 100%;
	position: relative;
}

.tab-icon {
	font-size: 44rpx;
	margin-bottom: 6rpx;
	transition: transform 0.2s;
}

.tab-item.active .tab-icon {
	transform: scale(1.1);
}

.tab-text {
	font-size: 22rpx;
	color: #999999;
	transition: color 0.2s;
}

.tab-item.active .tab-text {
	color: #00d26a;
	font-weight: 500;
}

.tab-badge {
	position: absolute;
	top: 10rpx;
	right: calc(50% - 30rpx);
	min-width: 32rpx;
	height: 32rpx;
	background-color: #ff4444;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0 8rpx;
}

.badge-text {
	font-size: 20rpx;
	color: #ffffff;
}
</style>
