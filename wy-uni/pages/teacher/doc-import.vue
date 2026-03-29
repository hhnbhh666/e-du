<template>
	<view class="doc-import-container">
		<!-- 顶部导航 -->
		<view class="page-header">
			<view class="header-left">
				<text class="back-icon" @click="goBack">←</text>
			</view>
			<text class="page-title">文档导入</text>
			<view class="header-right">
				<text class="help-btn" @click="showHelp">?</text>
			</view>
		</view>

		<scroll-view class="content" scroll-y="true">
			<!-- 上传区域 -->
			<view class="upload-section" v-if="!isRecognizing && !recognitionResult">
				<view class="upload-box" @click="chooseImage">
					<view class="upload-icon">
						<text>📷</text>
					</view>
					<text class="upload-title">点击上传试卷图片</text>
					<text class="upload-desc">支持 JPG、PNG 格式，单张最大 10MB</text>
					<text class="upload-tip">支持拍摄或从相册选择</text>
				</view>
			</view>

			<!-- 识别中 -->
			<view class="recognizing-section" v-if="isRecognizing">
				<view class="loading-box">
					<view class="loading-animation">
						<view class="loading-dot"></view>
						<view class="loading-dot"></view>
						<view class="loading-dot"></view>
					</view>
					<text class="loading-text">正在识别试卷内容...</text>
					<text class="loading-desc">上传至服务器并调用百度 OCR 识别</text>
				</view>
			</view>

			<!-- 识别结果 -->
			<view class="result-section" v-if="recognitionResult">
				<view class="result-header">
					<view class="result-info">
						<text class="result-title">识别完成</text>
						<text class="result-count">共识别 {{ recognitionResult.questions.length }} 道题目</text>
					</view>
					<view class="reupload-btn" @click="reupload">
						<text>重新上传</text>
					</view>
				</view>

				<view class="original-image">
					<text class="image-label">原始图片</text>
					<image :src="uploadedImage" mode="aspectFit" class="preview-img"></image>
				</view>

				<view class="raw-text" v-if="recognitionResult.rawText">
					<text class="raw-label">OCR 原文（可对照修改）</text>
					<text class="raw-content">{{ recognitionResult.rawText }}</text>
				</view>

				<view class="questions-list">
					<view class="list-header">
						<text class="header-title">识别结果</text>
						<text class="header-tip">请核对并修正识别内容</text>
					</view>

					<view 
						class="question-card" 
						v-for="(question, index) in recognitionResult.questions" 
						:key="index"
						:class="{ 'low-confidence': question.confidence < 0.8 }"
					>
						<view class="card-header">
							<text class="question-index">题目 {{ index + 1 }}</text>
							<view class="confidence-badge" :class="{ 'warning': question.confidence < 0.8 }">
								<text>置信度: {{ (question.confidence * 100).toFixed(0) }}%</text>
							</view>
							<text class="delete-btn" @click="deleteQuestion(index)">删除</text>
						</view>

						<view class="question-content">
							<textarea 
								class="question-input"
								v-model="question.content"
								placeholder="题目内容"
							/>
						</view>

						<view class="options-edit">
							<view class="option-row" v-for="(option, optIndex) in question.options" :key="optIndex">
								<text class="option-letter">{{ option.letter }}</text>
								<input class="option-input" v-model="option.text" :placeholder="'选项' + option.letter" />
								<view 
									class="correct-tag"
									:class="{ active: question.correctAnswer === optIndex }"
									@click="setCorrectAnswer(index, optIndex)"
								>
									<text>✓</text>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 历史记录 -->
			<view class="history-section" v-if="ocrHistory.length > 0 && !recognitionResult">
				<view class="section-title">
					<text>识别历史</text>
					<text class="clear-btn" @click="clearHistory">清空</text>
				</view>
				<view class="history-list">
					<view class="history-item" v-for="(item, index) in ocrHistory" :key="index" @click="viewHistory(item)">
						<image :src="item.image" class="history-thumb" mode="aspectFill"></image>
						<view class="history-info">
							<text class="history-date">{{ item.date }}</text>
							<text class="history-count">{{ item.count }} 道题</text>
						</view>
						<text class="history-status" :class="item.status">{{ item.statusText }}</text>
					</view>
				</view>
			</view>

			<view class="bottom-space"></view>
		</scroll-view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar" v-if="recognitionResult">
			<view class="bar-left">
				<checkbox :checked="createPaper" @click="createPaper = !createPaper" />
				<text class="checkbox-label">同时创建试卷</text>
			</view>
			<button class="confirm-btn" @click="confirmImport">确认导入</button>
		</view>
	</view>
</template>

<script>
	import { teacherApi } from '@/api/index.js'

	export default {
	data() {
		return {
			isRecognizing: false,
			uploadedImage: '',
			recognitionResult: null,
			createPaper: false,
			// 识别历史可后续接后端 ocr_records
			ocrHistory: []
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		showHelp() {
			uni.showModal({
				title: '使用说明',
				content: '1. 拍摄或选择试卷图片\n2. 系统自动识别题目内容\n3. 核对识别结果并修正\n4. 确认导入题库',
				showCancel: false
			})
		},
		chooseImage() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.uploadedImage = res.tempFilePaths[0]
					this.startRecognize()
				}
			})
		},
		async startRecognize() {
			this.isRecognizing = true
			try {
				const data = await teacherApi.recognizePaper(this.uploadedImage)
				this.recognitionResult = {
					rawText: data.rawText || '',
					questions: (data.questions || []).map((q) => ({
						content: q.content || '',
						options: (q.options || []).map((o) => ({
							letter: o.letter,
							text: o.text || ''
						})),
						correctAnswer: q.correctAnswer != null ? q.correctAnswer : 0,
						confidence: q.confidence != null ? q.confidence : 0.55
					}))
				}
				if (!this.recognitionResult.questions.length) {
					uni.showToast({ title: '未识别到题目，请检查图片或 OCR 配置', icon: 'none' })
				}
			} catch (e) {
				// teacherApi 已 toast；未配置百度 key 时后端会返回明确错误
				this.recognitionResult = null
			} finally {
				this.isRecognizing = false
			}
		},
		reupload() {
			this.recognitionResult = null
			this.uploadedImage = ''
		},
		deleteQuestion(index) {
			uni.showModal({
				title: '确认删除',
				content: '确定要删除这道题目吗？',
				success: (res) => {
					if (res.confirm) {
						this.recognitionResult.questions.splice(index, 1)
					}
				}
			})
		},
		setCorrectAnswer(questionIndex, optionIndex) {
			this.recognitionResult.questions[questionIndex].correctAnswer = optionIndex
		},
		async confirmImport() {
			const questions = this.recognitionResult.questions
			if (!questions.length) {
				uni.showToast({ title: '没有可导入的题目', icon: 'none' })
				return
			}
			uni.showLoading({ title: '导入中...' })
			let ok = 0
			let fail = 0
			for (const q of questions) {
				try {
					await teacherApi.createQuestion({
						content: q.content,
						imageUrl: null,
						videoUrl: null,
						type: 1,
						categoryId: 2,
						difficulty: 2,
						options: q.options.map((opt) => ({
							letter: opt.letter,
							content: opt.text
						})),
						correctAnswer: q.correctAnswer,
						tip: null,
						explanation: null
					})
					ok++
				} catch (e) {
					fail++
				}
			}
			uni.hideLoading()
			if (fail) {
				uni.showModal({
					title: '导入完成',
					content: `成功 ${ok} 道，失败 ${fail} 道。请检查题目内容或网络后重试。`,
					showCancel: false
				})
				return
			}
			uni.showToast({ title: `已导入 ${ok} 道题`, icon: 'success' })
			if (this.createPaper && ok > 0) {
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/teacher/create-paper?questions=' + encodeURIComponent(JSON.stringify(questions))
					})
				}, 400)
				return
			}
			setTimeout(() => uni.navigateBack(), 600)
		},
		viewHistory(item) {
			uni.showToast({ title: '查看历史记录', icon: 'none' })
		},
		clearHistory() {
			uni.showModal({
				title: '清空历史',
				content: '确定要清空所有识别历史吗？',
				success: (res) => {
					if (res.confirm) {
						this.ocrHistory = []
					}
				}
			})
		}
	}
}
</script>

<style>
.doc-import-container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

/* 顶部导航 */
.page-header {
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

.page-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.help-btn {
	font-size: 28rpx;
	color: #1890ff;
	width: 48rpx;
	height: 48rpx;
	border-radius: 50%;
	background-color: #e6f7ff;
	display: flex;
	align-items: center;
	justify-content: center;
}

/* 内容区域 */
.content {
	flex: 1;
}

/* 上传区域 */
.upload-section {
	padding: 40rpx 30rpx;
}

.upload-box {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 80rpx 60rpx;
	background-color: #fff;
	border-radius: 20rpx;
	border: 4rpx dashed #e0e0e0;
}

.upload-icon {
	font-size: 80rpx;
	margin-bottom: 20rpx;
}

.upload-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 16rpx;
}

.upload-desc {
	font-size: 26rpx;
	color: #666;
	margin-bottom: 10rpx;
}

.upload-tip {
	font-size: 24rpx;
	color: #999;
}

/* 识别中 */
.recognizing-section {
	padding: 60rpx 30rpx;
}

.loading-box {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 100rpx 60rpx;
	background-color: #fff;
	border-radius: 20rpx;
}

.loading-animation {
	display: flex;
	gap: 16rpx;
	margin-bottom: 30rpx;
}

.loading-dot {
	width: 20rpx;
	height: 20rpx;
	border-radius: 50%;
	background-color: #1890ff;
	animation: bounce 1.4s ease-in-out infinite both;
}

.loading-dot:nth-child(1) { animation-delay: -0.32s; }
.loading-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
	0%, 80%, 100% { transform: scale(0); }
	40% { transform: scale(1); }
}

.loading-text {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 16rpx;
}

.loading-desc {
	font-size: 26rpx;
	color: #999;
}

/* 识别结果 */
.result-section {
	padding: 30rpx;
}

.result-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.result-info {
	flex: 1;
}

.result-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	display: block;
}

.result-count {
	font-size: 26rpx;
	color: #666;
}

.reupload-btn {
	padding: 16rpx 30rpx;
	background-color: #f5f5f5;
	border-radius: 30rpx;
	font-size: 26rpx;
	color: #666;
}

.original-image {
	background-color: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
	margin-bottom: 30rpx;
}

.image-label {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 16rpx;
}

.preview-img {
	width: 100%;
	height: 400rpx;
	border-radius: 12rpx;
}

.raw-text {
	background-color: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
	margin-bottom: 30rpx;
}

.raw-label {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 12rpx;
}

.raw-content {
	font-size: 24rpx;
	color: #666;
	line-height: 1.6;
	white-space: pre-wrap;
	word-break: break-all;
}

.questions-list {
	background-color: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
}

.list-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.header-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}

.header-tip {
	font-size: 24rpx;
	color: #ff8c42;
}

.question-card {
	background-color: #f9f9f9;
	border-radius: 12rpx;
	padding: 20rpx;
	margin-bottom: 20rpx;
	border: 2rpx solid transparent;
}

.question-card.low-confidence {
	border-color: #ffaa00;
	background-color: #fffbe6;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16rpx;
}

.question-index {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.confidence-badge {
	font-size: 22rpx;
	color: #00d26a;
	background-color: #e6ffed;
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
}

.confidence-badge.warning {
	color: #ffaa00;
	background-color: #fffbe6;
}

.delete-btn {
	font-size: 24rpx;
	color: #ff4444;
	padding: 8rpx 16rpx;
}

.question-input {
	width: 100%;
	height: 120rpx;
	background-color: #fff;
	border-radius: 8rpx;
	padding: 16rpx;
	font-size: 28rpx;
	color: #333;
	margin-bottom: 16rpx;
	box-sizing: border-box;
}

.options-edit {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.option-row {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.option-letter {
	font-size: 28rpx;
	font-weight: bold;
	color: #1890ff;
	width: 36rpx;
}

.option-input {
	flex: 1;
	height: 64rpx;
	background-color: #fff;
	border-radius: 8rpx;
	padding: 0 16rpx;
	font-size: 26rpx;
	color: #333;
}

.correct-tag {
	width: 44rpx;
	height: 44rpx;
	border-radius: 50%;
	background-color: #f0f0f0;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 24rpx;
	color: #ccc;
}

.correct-tag.active {
	background-color: #00d26a;
	color: #fff;
}

/* 历史记录 */
.history-section {
	padding: 30rpx;
}

.section-title {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.section-title text:first-child {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.clear-btn {
	font-size: 26rpx;
	color: #999;
}

.history-list {
	background-color: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
}

.history-item {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.history-item:last-child {
	border-bottom: none;
}

.history-thumb {
	width: 100rpx;
	height: 140rpx;
	border-radius: 8rpx;
	margin-right: 20rpx;
	background-color: #f5f5f5;
}

.history-info {
	flex: 1;
}

.history-date {
	font-size: 26rpx;
	color: #666;
	display: block;
	margin-bottom: 8rpx;
}

.history-count {
	font-size: 24rpx;
	color: #999;
}

.history-status {
	font-size: 24rpx;
	padding: 6rpx 16rpx;
	border-radius: 8rpx;
}

.history-status.success {
	color: #00d26a;
	background-color: #e6ffed;
}

.history-status.pending {
	color: #ffaa00;
	background-color: #fffbe6;
}

/* 底部操作栏 */
.bottom-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 30rpx;
	background-color: #fff;
	border-top: 1rpx solid #f0f0f0;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.bar-left {
	display: flex;
	align-items: center;
	gap: 16rpx;
}

.checkbox-label {
	font-size: 28rpx;
	color: #666;
}

.confirm-btn {
	background-color: #1890ff;
	color: #fff;
	font-size: 28rpx;
	padding: 16rpx 40rpx;
	border-radius: 40rpx;
	border: none;
}

.bottom-space {
	height: 40rpx;
}
</style>