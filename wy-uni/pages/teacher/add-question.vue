<template>
	<view class="add-question-container">
		<view class="page-header">
			<view class="header-left">
				<text class="back-icon" @click="goBack">←</text>
			</view>
			<text class="page-title">添加题目</text>
			<view class="header-right">
				<text class="save-btn" @click="saveQuestion">保存</text>
			</view>
		</view>

		<scroll-view class="form-content" scroll-y="true">
			<!-- 题目类型选择 -->
			<view class="form-section">
				<text class="section-label">题目类型</text>
				<view class="type-selector">
					<view 
						class="type-item" 
						:class="{ active: question.type === 1 }"
						@click="selectType(1)"
					>
						单选题
					</view>
					<view 
						class="type-item" 
						:class="{ active: question.type === 2 }"
						@click="selectType(2)"
					>
						多选题
					</view>
					<view 
						class="type-item" 
						:class="{ active: question.type === 3 }"
						@click="selectType(3)"
					>
						判断题
					</view>
				</view>
			</view>

			<!-- 题目内容 -->
			<view class="form-section">
				<text class="section-label">题目内容</text>
				<textarea 
					class="content-input"
					v-model="question.content"
					placeholder="请输入题目内容..."
					maxlength="1000"
				/>
				<view class="upload-area" @click="uploadImage">
					<text class="upload-icon">📷</text>
					<text class="upload-text">{{ question.image ? '更换配图' : '添加配图（可选）' }}</text>
				</view>
				<image v-if="question.image" :src="question.image" class="preview-image" mode="aspectFit"></image>
			</view>

			<!-- 选项设置 -->
			<view class="form-section">
				<text class="section-label">选项设置</text>
				<view class="options-list">
					<view 
						v-for="(option, index) in question.options" 
						:key="index"
						class="option-row"
					>
						<text class="option-label">{{ option.letter }}</text>
						<input 
							class="option-input"
							v-model="option.text"
							:placeholder="'请输入选项' + option.letter"
						/>
						<view 
							class="correct-checkbox"
							:class="{ checked: question.correctAnswer === index }"
							@click="selectCorrect(index)"
						>
							<text v-if="question.correctAnswer === index">✓</text>
						</view>
					</view>
				</view>
				<view class="add-option-btn" @click="addOption" v-if="question.options.length < 6">
					<text>+ 添加选项</text>
				</view>
			</view>

			<!-- 答题技巧 -->
			<view class="form-section">
				<text class="section-label">答题技巧（可选）</text>
				<textarea 
					class="content-input tip-input"
					v-model="question.tip"
					placeholder="请输入答题技巧或口诀..."
					maxlength="500"
				/>
			</view>

			<!-- 详细解析 -->
			<view class="form-section">
				<view class="section-header">
					<text class="section-label">详细解析</text>
				</view>
				<!-- 富文本编辑器 -->
				<view class="rich-editor-wrapper">
					<!-- 工具栏 -->
					<view class="editor-toolbar">
						<view class="toolbar-group">
							<text class="toolbar-btn" @click="editorFormat('bold')" :class="{ active: formats.bold }">B</text>
							<text class="toolbar-btn" @click="editorFormat('italic')" :class="{ active: formats.italic }">I</text>
							<text class="toolbar-btn" @click="editorFormat('underline')" :class="{ active: formats.underline }">U</text>
						</view>
						<view class="toolbar-divider"></view>
						<view class="toolbar-group">
							<text class="toolbar-btn" @click="editorFormat('header', 'H2')">H1</text>
							<text class="toolbar-btn" @click="editorFormat('list', 'ordered')">OL</text>
							<text class="toolbar-btn" @click="editorFormat('list', 'bullet')">UL</text>
						</view>
						<view class="toolbar-divider"></view>
						<view class="toolbar-group">
							<text class="toolbar-btn image-btn" @click="editorInsertImage">📷</text>
						</view>
					</view>
					<!-- 编辑器 -->
					<editor 
						id="explanationEditor"
						class="rich-editor"
						placeholder="请输入详细解析..."
						@ready="onEditorReady"
						@statuschange="onStatusChange"
					></editor>
				</view>
			</view>

			<!-- 讲解视频 -->
			<view class="form-section">
				<text class="section-label">讲解视频（可选）</text>
				<view class="upload-area video-upload" @click="uploadVideo">
					<text class="upload-icon">🎬</text>
					<text class="upload-text">{{ question.videoUrl ? '更换视频' : '上传讲解视频' }}</text>
				</view>
			</view>

			<!-- 难度设置 -->
			<view class="form-section">
				<text class="section-label">难度设置</text>
				<view class="difficulty-selector">
					<view 
						v-for="level in difficultyLevels" 
						:key="level.value"
						class="difficulty-item"
						:class="{ active: question.difficulty === level.value }"
						@click="question.difficulty = level.value"
					>
						{{ level.label }}
					</view>
				</view>
			</view>

			<view class="bottom-space"></view>
		</scroll-view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			question: {
				type: 1, // 1单选 2多选 3判断
				content: '',
				image: '',
				videoUrl: '',
				difficulty: 2, // 1简单 2中等 3困难
				options: [
					{ letter: 'A', text: '' },
					{ letter: 'B', text: '' },
					{ letter: 'C', text: '' },
					{ letter: 'D', text: '' }
				],
				correctAnswer: null,
				tip: '',
				explanation: ''
			},
			difficultyLevels: [
				{ value: 1, label: '简单' },
				{ value: 2, label: '中等' },
				{ value: 3, label: '困难' }
			],
			formats: {}, // 编辑器格式状态
			editorCtx: null // 编辑器上下文
		}
	},
	onReady() {
		// 页面加载完成后初始化编辑器
		this.onEditorReady()
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		// 编辑器准备就绪
		onEditorReady() {
			uni.createSelectorQuery().select('#explanationEditor').context((res) => {
				this.editorCtx = res.context
			}).exec()
		},
		// 编辑器格式变化
		onStatusChange(e) {
			this.formats = e.detail
		},
		// 应用格式
		editorFormat(name, value) {
			if (!this.editorCtx) return
			this.editorCtx.format(name, value)
		},
		// 插入图片到编辑器
		editorInsertImage() {
			if (!this.editorCtx) return
			uni.chooseImage({
				count: 1,
				success: (res) => {
					// 这里应该上传到服务器获取URL，先使用临时路径
					const imageUrl = res.tempFilePaths[0]
					this.editorCtx.insertImage({
						src: imageUrl,
						alt: '解析图片',
						width: '100%',
						height: 'auto'
					})
				}
			})
		},
		// 获取编辑器内容
		getEditorContent() {
			return new Promise((resolve) => {
				if (!this.editorCtx) {
					resolve('')
					return
				}
				this.editorCtx.getContents({
					success: (res) => {
						resolve(res.html)
					},
					fail: () => {
						resolve('')
					}
				})
			})
		},
		selectType(type) {
			this.question.type = type
			// 判断题只有对错两个选项
			if (type === 3) {
				this.question.options = [
					{ letter: 'A', text: '正确' },
					{ letter: 'B', text: '错误' }
				]
			} else {
				// 恢复默认4个选项
				this.question.options = [
					{ letter: 'A', text: '' },
					{ letter: 'B', text: '' },
					{ letter: 'C', text: '' },
					{ letter: 'D', text: '' }
				]
			}
		},
		selectCorrect(index) {
			if (this.question.type === 2) {
				// 多选题需要支持多选，这里简化处理
				this.question.correctAnswer = index
			} else {
				this.question.correctAnswer = index
			}
		},
		addOption() {
			const letters = ['A', 'B', 'C', 'D', 'E', 'F']
			if (this.question.options.length < 6) {
				this.question.options.push({
					letter: letters[this.question.options.length],
					text: ''
				})
			}
		},
		uploadImage() {
			uni.chooseImage({
				count: 1,
				success: (res) => {
					// 这里应该调用上传接口，先模拟使用临时路径
					this.question.image = res.tempFilePaths[0]
					uni.showToast({ title: '图片已选择', icon: 'success' })
				}
			})
		},
		uploadVideo() {
			uni.chooseVideo({
				success: (res) => {
					this.question.videoUrl = res.tempFilePath
					uni.showToast({ title: '视频已选择', icon: 'success' })
				}
			})
		},
		async saveQuestion() {
			// 表单验证
			if (!this.question.content.trim()) {
				uni.showToast({ title: '请输入题目内容', icon: 'none' })
				return
			}
			if (this.question.correctAnswer === null) {
				uni.showToast({ title: '请选择正确答案', icon: 'none' })
				return
			}
			// 检查选项是否完整
			const emptyOption = this.question.options.find(opt => !opt.text.trim())
			if (emptyOption) {
				uni.showToast({ title: '请完善所有选项内容', icon: 'none' })
				return
			}
			
			// 获取富文本编辑器内容
			const explanation = await this.getEditorContent()
			this.question.explanation = explanation
			
			// 模拟保存
			console.log('保存题目:', this.question)
			uni.showModal({
				title: '保存成功',
				content: '题目已提交审核，通过后将上架',
				showCancel: false,
				success: () => {
					uni.navigateBack()
				}
			})
		}
	}
}
</script>

<style>
.add-question-container {
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

.save-btn {
	font-size: 28rpx;
	color: #1890ff;
	padding: 10rpx 20rpx;
}

/* 表单内容 */
.form-content {
	flex: 1;
	padding: 20rpx 30rpx;
}

.form-section {
	background-color: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.section-label {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 20rpx;
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.section-header .section-label {
	margin-bottom: 0;
}

/* 题目类型选择 */
.type-selector {
	display: flex;
	gap: 20rpx;
}

.type-item {
	flex: 1;
	padding: 20rpx 30rpx;
	text-align: center;
	background-color: #f5f5f5;
	border-radius: 12rpx;
	font-size: 28rpx;
	color: #666;
}

.type-item.active {
	background-color: #1890ff;
	color: #fff;
}

/* 输入区域 */
.content-input {
	width: 100%;
	height: 200rpx;
	background-color: #f5f5f5;
	border-radius: 12rpx;
	padding: 20rpx;
	font-size: 28rpx;
	color: #333;
	box-sizing: border-box;
}

.tip-input {
	height: 120rpx;
}

.explanation-input {
	height: 300rpx;
}

/* 富文本编辑器 */
.rich-editor-wrapper {
	border: 2rpx solid #e0e0e0;
	border-radius: 12rpx;
	overflow: hidden;
	background-color: #fff;
}

.editor-toolbar {
	display: flex;
	align-items: center;
	padding: 16rpx 20rpx;
	background-color: #f9f9f9;
	border-bottom: 2rpx solid #e0e0e0;
	flex-wrap: wrap;
	gap: 10rpx;
}

.toolbar-group {
	display: flex;
	gap: 8rpx;
}

.toolbar-divider {
	width: 2rpx;
	height: 32rpx;
	background-color: #ddd;
	margin: 0 10rpx;
}

.toolbar-btn {
	width: 56rpx;
	height: 56rpx;
	background-color: #fff;
	border: 2rpx solid #e0e0e0;
	border-radius: 8rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 26rpx;
	color: #666;
	font-weight: bold;
}

.toolbar-btn.active {
	background-color: #1890ff;
	color: #fff;
	border-color: #1890ff;
}

.toolbar-btn.image-btn {
	font-size: 28rpx;
	background-color: #fff5e6;
	border-color: #ff8c42;
}

.rich-editor {
	height: 400rpx;
	background-color: #fff;
}

/* 上传区域 */
.upload-area {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 40rpx;
	background-color: #f5f5f5;
	border-radius: 12rpx;
	margin-top: 20rpx;
	border: 2rpx dashed #ddd;
}

.upload-icon {
	font-size: 48rpx;
	margin-bottom: 10rpx;
}

.upload-text {
	font-size: 26rpx;
	color: #999;
}

.preview-image {
	width: 100%;
	height: 300rpx;
	margin-top: 20rpx;
	border-radius: 12rpx;
}

.video-upload {
	padding: 60rpx;
}

/* 选项列表 */
.options-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.option-row {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.option-label {
	font-size: 32rpx;
	font-weight: bold;
	color: #1890ff;
	width: 40rpx;
}

.option-input {
	flex: 1;
	height: 80rpx;
	background-color: #f5f5f5;
	border-radius: 12rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	color: #333;
}

.correct-checkbox {
	width: 48rpx;
	height: 48rpx;
	border-radius: 50%;
	background-color: #f5f5f5;
	display: flex;
	align-items: center;
	justify-content: center;
	border: 2rpx solid #ddd;
}

.correct-checkbox.checked {
	background-color: #00d26a;
	border-color: #00d26a;
}

.correct-checkbox text {
	font-size: 28rpx;
	color: #fff;
}

.add-option-btn {
	text-align: center;
	padding: 20rpx;
	font-size: 28rpx;
	color: #1890ff;
	margin-top: 20rpx;
}

/* 难度选择 */
.difficulty-selector {
	display: flex;
	gap: 20rpx;
}

.difficulty-item {
	flex: 1;
	padding: 20rpx 30rpx;
	text-align: center;
	background-color: #f5f5f5;
	border-radius: 12rpx;
	font-size: 28rpx;
	color: #666;
}

.difficulty-item.active {
	background-color: #ff8c42;
	color: #fff;
}

.bottom-space {
	height: 40rpx;
}
</style>