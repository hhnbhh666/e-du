<template>
	<view class="quiz-container">
		<!-- 顶部导航 -->
		<view class="quiz-header">
			<view class="header-left">
				<text class="back-icon" @click="goBack">←</text>
			</view>
			<view class="mode-tabs">
				<view class="tab" :class="{ active: currentMode === 'answer' }" @click="switchMode('answer')">答题</view>
				<view class="tab" :class="{ active: currentMode === 'review' }" @click="switchMode('review')">背题</view>
				<view class="tab" :class="{ active: currentMode === 'video' }" @click="switchMode('video')">
					视频
					<text class="new-badge">NEW</text>
				</view>
			</view>
			<view class="header-right">
				<text class="settings-icon" @click="showSettings">⚙</text>
			</view>
		</view>

		<!-- 广告横幅（可关闭） -->
		<view class="ad-banner" v-if="showAd">
			<view class="ad-content">
				<view class="ad-icon">
					<view class="traffic-light">
						<view class="light red"></view>
						<view class="light yellow"></view>
						<view class="light green"></view>
					</view>
				</view>
				<text class="ad-title">安全出行 文明交通</text>
			</view>
			<text class="ad-close" @click="closeAd">✕</text>
		</view>

		<!-- 老师功能区（仅老师角色显示） -->
		<view class="teacher-actions" v-if="isTeacher">
			<view class="action-card" @click="goToAddQuestion">
				<view class="action-icon add-icon">
					<text>➕</text>
				</view>
				<view class="action-info">
					<text class="action-title">添加题目</text>
					<text class="action-desc">手动创建新题目</text>
				</view>
				<text class="action-arrow">›</text>
			</view>
			<view class="action-card" @click="goToDocImport">
				<view class="action-icon import-icon">
					<text>📄</text>
				</view>
				<view class="action-info">
					<text class="action-title">文档导入</text>
					<text class="action-desc">OCR识别试卷自动导入</text>
				</view>
				<text class="action-arrow">›</text>
			</view>
		</view>

		<!-- 题目区域 -->
		<scroll-view class="quiz-content" scroll-y="true">
			<!-- 题目信息 -->
			<view class="question-info">
				<text class="question-type">单选</text>
				<text class="question-content">{{currentQuestion.content}}</text>
				<text class="read-question" @click="readQuestion">🔊 读题</text>
			</view>

			<!-- 题目图片 -->
			<view class="question-image" v-if="currentQuestion.image">
				<image :src="currentQuestion.image" mode="aspectFit"></image>
			</view>

			<!-- 选项列表 -->
			<view class="options-list">
				<view 
					v-for="(option, index) in currentQuestion.options" 
					:key="index"
					class="option-item"
					:class="{ 
						selected: selectedOption === index,
						correct: hasAnswered && index === currentQuestion.correctAnswer,
						wrong: hasAnswered && selectedOption === index && index !== currentQuestion.correctAnswer
					}"
					@click="selectOption(index)"
				>
					<text class="option-letter">{{option.letter}}</text>
					<text class="option-text">{{option.text}}</text>
					<text class="option-icon" v-if="hasAnswered">
						<text v-if="index === currentQuestion.correctAnswer" class="correct-icon">✓</text>
						<text v-else-if="selectedOption === index" class="wrong-icon">✗</text>
					</text>
				</view>
			</view>

			<!-- 答题结果 -->
			<view class="answer-result" v-if="hasAnswered">
				<view class="result-header">
					<text class="result-title">答案</text>
					<text class="correct-answer">{{currentQuestion.options[currentQuestion.correctAnswer].letter}}</text>
					<text class="your-answer" :class="{ wrong: !isCorrect }">您选择 {{selectedOption !== null ? currentQuestion.options[selectedOption].letter : '-'}}</text>
				</view>
			</view>

			<!-- 技巧提示 -->
			<view class="tip-section" v-if="hasAnswered">
				<view class="tip-header">
					<text class="tip-title">本题技巧</text>
					<text class="tip-apply">适用于2道题 ></text>
				</view>
				<view class="tip-content">
					<text class="tip-text">{{currentQuestion.tip}}</text>
					<text class="view-full-tip" @click="viewFullTip">查看完整技巧 >></text>
				</view>
			</view>

			<!-- 试题详解 -->
			<view class="explanation-section" v-if="hasAnswered">
				<view class="section-divider">
					<text class="divider-line"></text>
					<text class="section-title">试题详解</text>
					<text class="divider-line"></text>
				</view>
				
				<view class="explanation-content">
					<text class="explanation-title">视频讲解</text>
					<view class="video-player" @click="playVideo">
						<image :src="currentQuestion.videoThumb" mode="aspectFit"></image>
						<view class="play-button">
							<text class="play-icon">▶</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 评论区 -->
			<view class="comments-section" v-if="hasAnswered">
				<view class="comments-header">
					<text class="comments-title">做题心得 ({{comments.length}})</text>
					<view class="sort-tabs">
						<text :class="{ active: commentSort === 'hot' }" @click="sortComments('hot')">最热</text>
						<text :class="{ active: commentSort === 'new' }" @click="sortComments('new')">最新</text>
					</view>
				</view>

				<view class="comment-list">
					<view class="comment-item" v-for="(comment, index) in comments" :key="index">
						<image class="comment-avatar" :src="comment.avatar" mode="aspectFill"></image>
						<view class="comment-content">
							<view class="comment-header">
								<text class="comment-name">{{comment.name}}</text>
								<text class="comment-tag" v-if="comment.isAuthor">作者</text>
							</view>
							<text class="comment-text">{{comment.text}}</text>
							<view class="comment-actions">
								<text class="action-btn" @click="likeComment(index)">
									<text :class="{ liked: comment.liked }">♥</text> {{comment.likes}}
								</text>
								<text class="action-btn" @click="replyComment(index)">回复</text>
								<text class="comment-time">{{comment.time}}</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 发表评论 -->
				<view class="comment-input-bar">
					<input 
						class="comment-input" 
						v-model="newComment" 
						placeholder="分享你的做题心得..."
						@confirm="submitComment"
					/>
					<text class="send-btn" @click="submitComment">发送</text>
				</view>
			</view>

			<view class="bottom-space"></view>
		</scroll-view>

		

		<!-- 老师讲解悬浮按钮 -->
		<view class="teacher-float" @click="askTeacher" v-if="showTeacherFloat">
			<image class="teacher-avatar" src="https://picsum.photos/80/80?random=teacher" mode="aspectFill"></image>
			<text class="teacher-text">点我讲题</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				currentMode: 'answer',
				showAd: true,
				currentIndex: 20,
				totalQuestions: 2013,
				selectedOption: null,
				hasAnswered: false,
				isCorrect: false,
				isCollected: false,
				wrongCount: 1,
				rightCount: 19,
				showTeacherFloat: true,
				isTeacher: true, // 是否是老师角色，实际应从用户信息获取
				commentSort: 'hot',
				newComment: '',
				currentQuestion: {
					content: '遇到图中情形可加速通过。',
					image: 'https://picsum.photos/600/400?random=quiz1',
					videoThumb: 'https://picsum.photos/600/340?random=video',
					options: [
						{ letter: 'A', text: '正确' },
						{ letter: 'B', text: '错误' }
					],
					correctAnswer: 1,
					tip: '遇路口，先减速。',
					explanation: '图中显示的是让行标志，遇到这种情形应当减速慢行，确保安全后再通过，而不是加速通过。'
				},
				comments: [
					{
						name: '驾考小学员',
						avatar: 'https://picsum.photos/60/60?random=1',
						text: '这种题最容易错了，记住看到让行标志就一定要减速！',
						likes: 128,
						liked: false,
						time: '2小时前',
						isAuthor: false
					},
					{
						name: '老司机带带我',
						avatar: 'https://picsum.photos/60/60?random=2',
						text: '科目一考了98分，分享经验：遇到路口、人行横道、学校区域都要减速慢行',
						likes: 256,
						liked: true,
						time: '5小时前',
						isAuthor: false
					},
					{
						name: '教练小王',
						avatar: 'https://picsum.photos/60/60?random=3',
						text: '记住口诀：让行标志必减速，安全驾驶第一位！',
						likes: 89,
						liked: false,
						time: '1天前',
						isAuthor: true
					}
				]
			}
		},
		methods: {
			goBack() {
				uni.navigateBack()
			},
			switchMode(mode) {
				this.currentMode = mode
			},
			showSettings() {
				uni.showToast({ title: '设置功能', icon: 'none' })
			},
			closeAd() {
				this.showAd = false
			},
			readQuestion() {
				uni.showToast({ title: '语音读题', icon: 'none' })
			},
			// 跳转到添加题目页面
			goToAddQuestion() {
				uni.navigateTo({
					url: '/pages/teacher/add-question'
				})
			},
			// 跳转到文档导入页面
			goToDocImport() {
				uni.navigateTo({
					url: '/pages/teacher/doc-import'
				})
			},
			selectOption(index) {
				if (this.hasAnswered) return
				
				this.selectedOption = index
				this.hasAnswered = true
				this.isCorrect = index === this.currentQuestion.correctAnswer
				
				if (this.isCorrect) {
					this.rightCount++
					uni.showToast({ title: '回答正确！', icon: 'success' })
				} else {
					this.wrongCount++
					uni.showToast({ title: '回答错误', icon: 'none' })
				}
			},
			viewFullTip() {
				uni.showModal({
					title: '完整技巧',
					content: this.currentQuestion.explanation,
					showCancel: false
				})
			},
			playVideo() {
				uni.showToast({ title: '播放视频讲解', icon: 'none' })
			},
			sortComments(sort) {
				this.commentSort = sort
			},
			likeComment(index) {
				const comment = this.comments[index]
				comment.liked = !comment.liked
				comment.likes += comment.liked ? 1 : -1
			},
			replyComment(index) {
				uni.showToast({ title: `回复${this.comments[index].name}`, icon: 'none' })
			},
			submitComment() {
				if (!this.newComment.trim()) {
					uni.showToast({ title: '请输入评论内容', icon: 'none' })
					return
				}
				this.comments.unshift({
					name: '我',
					avatar: 'https://picsum.photos/60/60?random=me',
					text: this.newComment,
					likes: 0,
					liked: false,
					time: '刚刚',
					isAuthor: false
				})
				this.newComment = ''
				uni.showToast({ title: '评论发表成功', icon: 'success' })
			},
			askTeacher() {
				uni.showToast({ title: '呼叫老师讲解', icon: 'none' })
			},
			collectQuestion() {
				this.isCollected = !this.isCollected
				uni.showToast({ 
					title: this.isCollected ? '已收藏' : '取消收藏', 
					icon: 'none' 
				})
			},
			viewWrong() {
				uni.showToast({ title: `错题本：${this.wrongCount}题`, icon: 'none' })
			},
			viewRight() {
				uni.showToast({ title: `对题：${this.rightCount}题`, icon: 'none' })
			},
			showQuestionList() {
				uni.showToast({ title: '题目列表', icon: 'none' })
			}
		}
	}
</script>

<style>
	.quiz-container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f5f5f5;
	}

	/* 顶部导航 */
	.quiz-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 30rpx;
		background-color: #fff;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.back-icon {
		font-size: 40rpx;
		color: #333;
	}

	.mode-tabs {
		display: flex;
		background-color: #f5f5f5;
		border-radius: 40rpx;
		padding: 6rpx;
	}

	.tab {
		padding: 12rpx 30rpx;
		font-size: 28rpx;
		color: #666;
		border-radius: 30rpx;
		position: relative;
	}

	.tab.active {
		background-color: #333;
		color: #fff;
	}

	.new-badge {
		position: absolute;
		top: -8rpx;
		right: -8rpx;
		background-color: #ff4444;
		color: #fff;
		font-size: 18rpx;
		padding: 2rpx 8rpx;
		border-radius: 10rpx;
	}

	.settings-icon {
		font-size: 36rpx;
		color: #333;
	}

	/* 广告横幅 */
	.ad-banner {
		display: flex;
		justify-content: space-between;
		align-items: center;
		background: linear-gradient(135deg, #1a1a2e, #16213e);
		padding: 20rpx 30rpx;
	}

	.ad-content {
		display: flex;
		align-items: center;
	}

	.traffic-light {
		display: flex;
		flex-direction: column;
		gap: 6rpx;
		margin-right: 20rpx;
	}

	.light {
		width: 16rpx;
		height: 16rpx;
		border-radius: 50%;
	}

	.light.red { background-color: #ff4444; }
	.light.yellow { background-color: #ffaa00; }
	.light.green { background-color: #00d26a; }

	.ad-title {
		font-size: 32rpx;
		color: #fff;
		font-weight: bold;
	}

	.ad-close {
		font-size: 32rpx;
		color: #999;
		padding: 10rpx;
	}

	/* 老师功能区 */
	.teacher-actions {
		padding: 20rpx 30rpx;
		background-color: #fff;
		margin-bottom: 20rpx;
	}

	.action-card {
		display: flex;
		align-items: center;
		padding: 30rpx;
		background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		border: 1rpx solid #e8e8e8;
	}

	.action-card:last-child {
		margin-bottom: 0;
	}

	.action-icon {
		width: 80rpx;
		height: 80rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;
		font-size: 40rpx;
	}

	.add-icon {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}

	.import-icon {
		background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
	}

	.action-info {
		flex: 1;
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
	}

	.action-arrow {
		font-size: 40rpx;
		color: #ccc;
	}

	/* 题目区域 */
	.quiz-content {
		flex: 1;
		padding: 30rpx;
	}

	.question-info {
		margin-bottom: 30rpx;
	}

	.question-type {
		display: inline-block;
		background-color: #e6f7ff;
		color: #1890ff;
		font-size: 22rpx;
		padding: 4rpx 16rpx;
		border-radius: 8rpx;
		margin-bottom: 20rpx;
	}

	.question-content {
		font-size: 32rpx;
		color: #333;
		line-height: 1.6;
		display: block;
		margin-bottom: 16rpx;
	}

	.read-question {
		font-size: 24rpx;
		color: #1890ff;
	}

	.question-image {
		width: 100%;
		height: 400rpx;
		border-radius: 16rpx;
		overflow: hidden;
		margin-bottom: 30rpx;
	}

	.question-image image {
		width: 100%;
		height: 100%;
	}

	/* 选项列表 */
	.options-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		margin-bottom: 30rpx;
	}

	.option-item {
		display: flex;
		align-items: center;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 16rpx;
		border: 2rpx solid transparent;
	}

	.option-item.selected {
		border-color: #1890ff;
		background-color: #e6f7ff;
	}

	.option-item.correct {
		border-color: #00d26a;
		background-color: #e6ffed;
	}

	.option-item.wrong {
		border-color: #ff4444;
		background-color: #fff1f0;
	}

	.option-letter {
		width: 48rpx;
		height: 48rpx;
		background-color: #f5f5f5;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 24rpx;
		color: #666;
		margin-right: 20rpx;
	}

	.option-item.correct .option-letter {
		background-color: #00d26a;
		color: #fff;
	}

	.option-item.wrong .option-letter {
		background-color: #ff4444;
		color: #fff;
	}

	.option-text {
		flex: 1;
		font-size: 30rpx;
		color: #333;
	}

	.option-icon {
		font-size: 36rpx;
	}

	.correct-icon {
		color: #00d26a;
	}

	.wrong-icon {
		color: #ff4444;
	}

	/* 答题结果 */
	.answer-result {
		background-color: #fff;
		padding: 30rpx;
		border-radius: 16rpx;
		margin-bottom: 30rpx;
	}

	.result-header {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.result-title {
		font-size: 28rpx;
		color: #666;
	}

	.correct-answer {
		font-size: 32rpx;
		color: #00d26a;
		font-weight: bold;
	}

	.your-answer {
		font-size: 28rpx;
		color: #00d26a;
	}

	.your-answer.wrong {
		color: #ff4444;
	}

	/* 技巧提示 */
	.tip-section {
		background-color: #fff;
		padding: 30rpx;
		border-radius: 16rpx;
		margin-bottom: 30rpx;
	}

	.tip-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.tip-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.tip-apply {
		font-size: 24rpx;
		color: #999;
	}

	.tip-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.tip-text {
		font-size: 28rpx;
		color: #666;
	}

	.view-full-tip {
		font-size: 26rpx;
		color: #ff8c42;
		background-color: #fff5e6;
		padding: 10rpx 20rpx;
		border-radius: 30rpx;
	}

	/* 试题详解 */
	.explanation-section {
		background-color: #fff;
		padding: 30rpx;
		border-radius: 16rpx;
		margin-bottom: 30rpx;
	}

	.section-divider {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 30rpx;
	}

	.divider-line {
		flex: 1;
		height: 1rpx;
		background-color: #e0e0e0;
	}

	.section-title {
		font-size: 28rpx;
		color: #999;
		padding: 0 20rpx;
	}

	.explanation-title {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.video-player {
		position: relative;
		width: 100%;
		height: 340rpx;
		border-radius: 16rpx;
		overflow: hidden;
	}

	.video-player image {
		width: 100%;
		height: 100%;
	}

	.play-button {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 100rpx;
		height: 100rpx;
		background-color: rgba(0,0,0,0.6);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.play-icon {
		font-size: 48rpx;
		color: #fff;
		margin-left: 10rpx;
	}

	/* 评论区 */
	.comments-section {
		background-color: #fff;
		padding: 30rpx;
		border-radius: 16rpx;
		margin-bottom: 30rpx;
	}

	.comments-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.comments-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.sort-tabs {
		display: flex;
		gap: 20rpx;
	}

	.sort-tabs text {
		font-size: 24rpx;
		color: #999;
	}

	.sort-tabs text.active {
		color: #333;
		font-weight: bold;
	}

	.comment-list {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
	}

	.comment-item {
		display: flex;
		gap: 20rpx;
	}

	.comment-avatar {
		width: 70rpx;
		height: 70rpx;
		border-radius: 50%;
		flex-shrink: 0;
	}

	.comment-content {
		flex: 1;
	}

	.comment-header {
		display: flex;
		align-items: center;
		gap: 15rpx;
		margin-bottom: 10rpx;
	}

	.comment-name {
		font-size: 26rpx;
		color: #333;
		font-weight: 500;
	}

	.comment-tag {
		font-size: 20rpx;
		color: #fff;
		background-color: #1890ff;
		padding: 2rpx 10rpx;
		border-radius: 8rpx;
	}

	.comment-text {
		font-size: 28rpx;
		color: #333;
		line-height: 1.5;
		margin-bottom: 15rpx;
		display: block;
	}

	.comment-actions {
		display: flex;
		gap: 30rpx;
	}

	.action-btn {
		font-size: 24rpx;
		color: #999;
	}

	.action-btn .liked {
		color: #ff4444;
	}

	.comment-time {
		font-size: 22rpx;
		color: #ccc;
		margin-left: auto;
	}

	/* 评论输入栏 */
	.comment-input-bar {
		display: flex;
		gap: 20rpx;
		margin-top: 30rpx;
		padding-top: 30rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.comment-input {
		flex: 1;
		height: 70rpx;
		background-color: #f5f5f5;
		border-radius: 35rpx;
		padding: 0 30rpx;
		font-size: 28rpx;
	}

	.send-btn {
		font-size: 28rpx;
		color: #1890ff;
		height: 70rpx;
		line-height: 70rpx;
		padding: 0 20rpx;
	}

	/* 底部操作栏 */
	.quiz-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 30rpx;
		background-color: #fff;
		border-top: 1rpx solid #f0f0f0;
		padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	}

	.ask-btn {
		display: flex;
		align-items: center;
		gap: 10rpx;
		background-color: #1890ff;
		color: #fff;
		font-size: 26rpx;
		padding: 16rpx 30rpx;
		border-radius: 40rpx;
		border: none;
	}

	.ask-icon {
		font-size: 32rpx;
	}

	.footer-right {
		display: flex;
		gap: 30rpx;
	}

	.footer-right .action-btn {
		font-size: 24rpx;
		color: #666;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 6rpx;
	}

	.footer-right .action-btn text {
		font-size: 32rpx;
	}

	.collected {
		color: #ffaa00 !important;
	}

	.wrong-count .wrong-icon {
		color: #ff4444;
	}

	.right-count .right-icon {
		color: #00d26a;
	}

	/* 老师悬浮按钮 */
	.teacher-float {
		position: fixed;
		bottom: 200rpx;
		right: 30rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		z-index: 100;
	}

	.teacher-avatar {
		width: 90rpx;
		height: 90rpx;
		border-radius: 50%;
		border: 4rpx solid #fff;
		box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.15);
	}

	.teacher-text {
		font-size: 20rpx;
		color: #fff;
		background-color: #1890ff;
		padding: 4rpx 16rpx;
		border-radius: 20rpx;
		margin-top: -10rpx;
	}

	.bottom-space {
		height: 40rpx;
	}
</style>
