<template>
	<view class="global-tab-bar">
		<view
			v-for="(tab, index) in tabs"
			:key="index"
			class="tab-item"
			:class="{ active: current === index }"
			@click="onTab(index)"
		>
			<text class="tab-icon">{{ tab.icon }}</text>
			<text class="tab-name">{{ tab.name }}</text>
			<view class="badge" v-if="tab.badge > 0">{{ tab.badge }}</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'GlobalTabBar',
	props: {
		current: { type: Number, default: 0 }
	},
	data() {
		return {
			tabs: [
				{ name: '首页', icon: '🏠', badge: 0 },
				{ name: '新增', icon: '📝', badge: 0 },
				{ name: '刷题', icon: '✏', badge: 0 },
				{ name: '社区', icon: '💬', badge: 0 },
				{ name: '我', icon: '👤', badge: 0 }
			]
		}
	},
	methods: {
		onTab(index) {
			if (this.current === index) return
			// 与首页/刷题内联底栏一致：老师「找课」→ teacher-manage（可接登录态）
			const isTeacher = true
			if (index === 1 && isTeacher) {
				uni.reLaunch({ url: '/pages/teacher/teacher-manage' })
				return
			}
			const routes = [
				'/pages/index/index',
				'/pages/index/index',
				'/pages/quiz/quiz',
				'/pages/index/index',
				'/pages/index/index'
			]
			uni.reLaunch({ url: routes[index] })
		}
	}
}
</script>

<style scoped>
.global-tab-bar {
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
	position: relative;
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
	color: #00d26a;
}

.badge {
	position: absolute;
	top: -10rpx;
	right: -15rpx;
	background-color: #ff4444;
	color: #fff;
	font-size: 18rpx;
	width: 30rpx;
	height: 30rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}
</style>
