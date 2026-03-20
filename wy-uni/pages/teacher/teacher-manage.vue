<template>
	<view class="teacher-manage-page">
		<view class="page-header">
			<view class="header-left">
				<text class="back-icon" @click="goBack">←</text>
			</view>
			<text class="page-title">题目与试卷</text>
			<view class="header-right">
				<text class="header-placeholder"></text>
			</view>
		</view>

		<scroll-view class="content" scroll-y="true" :show-scrollbar="false">
			<view class="intro">
				<text class="intro-title">老师工作台</text>
				<text class="intro-desc">手动录入题目，或通过拍照识别试卷批量导入题目</text>
			</view>

			<view class="section-label">新增方式</view>

			<view class="action-card" @click="goAddQuestion">
				<view class="action-icon add-icon">
					<text>➕</text>
				</view>
				<view class="action-info">
					<text class="action-title">手动新增题目</text>
					<text class="action-desc">单题录入：题干、选项、解析与技巧</text>
				</view>
				<text class="action-arrow">›</text>
			</view>

			<view class="action-card" @click="goDocImport">
				<view class="action-icon scan-icon">
					<text>📷</text>
				</view>
				<view class="action-info">
					<text class="action-title">扫描导入试卷</text>
					<text class="action-desc">上传试卷图片，OCR 识别后校对并导入题目</text>
				</view>
				<text class="action-arrow">›</text>
			</view>

			<view class="tips">
				<text class="tips-title">提示</text>
				<text class="tips-line">• 手动新增适合零散题目、配图与富文本解析。</text>
				<text class="tips-line">• 扫描导入适合整卷拍照，识别后请核对后再批量入库。</text>
			</view>
			<view class="bottom-space"></view>
		</scroll-view>

		<!-- 底部导航（与首页/刷题一致，「找课」为老师工作台入口） -->
		<view class="bottom-tab-bar">
			<view
				v-for="(tab, index) in bottomTabs"
				:key="index"
				class="bottom-tab-item"
				:class="{ active: index === 1 }"
				@click="goBottomTab(index)"
			>
				<text class="tab-icon">{{ tab.icon }}</text>
				<text class="tab-name">{{ tab.name }}</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'TeacherManage',
	data() {
		return {
			bottomTabs: [
				{ name: '首页', icon: '🏠' },
				{ name: '新增', icon: '📝' },
				{ name: '刷题', icon: '✏' },
				{ name: '社区', icon: '💬' },
				{ name: '我', icon: '👤' }
			]
		}
	},
	methods: {
		goBack() {
			uni.reLaunch({ url: '/pages/index/index' })
		},
		goBottomTab(index) {
			if (index === 1) return
			const routes = [
				'/pages/index/index',
				'/pages/teacher/teacher-manage',
				'/pages/quiz/quiz',
				'/pages/index/index',
				'/pages/index/index'
			]
			uni.reLaunch({ url: routes[index] })
		},
		goAddQuestion() {
			uni.navigateTo({ url: '/pages/teacher/add-question' })
		},
		goDocImport() {
			uni.navigateTo({ url: '/pages/teacher/doc-import' })
		}
	}
}
</script>

<style scoped>
.teacher-manage-page {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.page-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 30rpx;
	background-color: #fff;
	border-bottom: 1rpx solid #f0f0f0;
	flex-shrink: 0;
}

.back-icon {
	font-size: 40rpx;
	color: #333;
	padding: 10rpx 30rpx 10rpx 0;
}

.page-title {
	font-size: 34rpx;
	font-weight: bold;
	color: #333;
}

.header-placeholder {
	width: 40rpx;
}

.content {
	flex: 1;
	padding: 24rpx 30rpx 40rpx;
	padding-bottom: 160rpx;
	box-sizing: border-box;
}

.bottom-space {
	height: 20rpx;
}

/* 与首页、刷题页底部栏一致 */
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

.bottom-tab-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.bottom-tab-bar .tab-icon {
	font-size: 40rpx;
	margin-bottom: 6rpx;
}

.bottom-tab-bar .tab-name {
	font-size: 22rpx;
	color: #999;
}

.bottom-tab-item.active .tab-name {
	color: #00d26a;
}

.intro {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 20rpx;
	padding: 36rpx 32rpx;
	margin-bottom: 36rpx;
}

.intro-title {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #fff;
	margin-bottom: 12rpx;
}

.intro-desc {
	font-size: 26rpx;
	color: rgba(255, 255, 255, 0.9);
	line-height: 1.5;
}

.section-label {
	font-size: 28rpx;
	color: #666;
	margin-bottom: 20rpx;
	padding-left: 4rpx;
}

.action-card {
	display: flex;
	align-items: center;
	padding: 32rpx 28rpx;
	background: #fff;
	border-radius: 16rpx;
	margin-bottom: 24rpx;
	border: 1rpx solid #e8e8e8;
	box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.04);
}

.action-icon {
	width: 88rpx;
	height: 88rpx;
	border-radius: 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 24rpx;
	font-size: 40rpx;
}

.add-icon {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.scan-icon {
	background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.action-info {
	flex: 1;
	min-width: 0;
}

.action-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
}

.action-desc {
	font-size: 26rpx;
	color: #999;
	line-height: 1.45;
}

.action-arrow {
	font-size: 40rpx;
	color: #ccc;
	flex-shrink: 0;
}

.tips {
	margin-top: 24rpx;
	padding: 24rpx;
	background: #fff;
	border-radius: 16rpx;
	border: 1rpx solid #eee;
}

.tips-title {
	display: block;
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 16rpx;
}

.tips-line {
	display: block;
	font-size: 24rpx;
	color: #888;
	line-height: 1.6;
	margin-bottom: 8rpx;
}
</style>
