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

		<!-- 题目区域（老师入口见底部导航「找课」） -->
		<scroll-view class="quiz-content" scroll-y="true">
			<!-- 无题目时的空状态（后端无数据时显示） -->
			<view v-if="showEmptyState" class="empty-state">
				<text class="empty-icon">📝</text>
				<text class="empty-text">暂无题目</text>
				<text class="empty-hint">请稍后再来或去首页看看</text>
			</view>

			<!-- 题目信息（有题目时显示） -->
			<view v-else-if="currentQuestion && currentQuestion.options" class="question-block">
			<view class="question-info">
				<text class="question-type">单选</text>
				<text class="question-content">{{currentQuestion.content}}</text>
				<text class="read-question" @click="readQuestion">🔊 读题</text>
			</view>

			<!-- 题目图片 -->
			<view class="question-image" v-if="currentQuestion.imageUrl">
				<image :src="currentQuestion.imageUrl" mode="aspectFit"></image>
			</view>

			<!-- 选项列表 -->
			<view class="options-list">
				<view 
					v-for="(option, index) in currentQuestion.options" 
					:key="index"
					class="option-item"
					:class="{ 
						selected: selectedOption === index,
						correct: hasAnswered && index === Number(currentQuestion.correctAnswer),
						wrong: hasAnswered && selectedOption === index && index !== Number(currentQuestion.correctAnswer)
					}"
					@click="selectOption(index)"
				>
					<text class="option-letter">{{option.letter}}</text>
					<text class="option-text">{{option.content || option.text}}</text>
					<text class="option-icon" v-if="hasAnswered">
						<text v-if="index === Number(currentQuestion.correctAnswer)" class="correct-icon">✓</text>
						<text v-else-if="selectedOption === index" class="wrong-icon">✗</text>
					</text>
				</view>
			</view>

			<!-- 答题结果 -->
			<view class="answer-result" v-if="hasAnswered">
				<view class="result-header">
					<text class="result-title">答案</text>
					<text class="correct-answer" v-if="currentQuestion.options && currentQuestion.options[Number(currentQuestion.correctAnswer)]">
						{{currentQuestion.options[Number(currentQuestion.correctAnswer)].letter}}
					</text>
					<text class="your-answer" :class="{ wrong: !isCorrect }">您选择 {{selectedOption !== null && currentQuestion.options ? currentQuestion.options[selectedOption].letter : '-'}}</text>
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

			<!-- 上一题/下一题按钮（答完题后显示） -->
			<view class="nav-question-section" v-if="hasAnswered">
				<view class="nav-btn prev-btn" @click="goToPrevQuestion">
					<text class="nav-icon">‹</text>
					<text class="nav-text">上一题</text>
				</view>
				<view class="nav-btn next-btn" @click="goToNextQuestion">
					<text class="nav-text">下一题</text>
					<text class="nav-icon">›</text>
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
			</view>
			<!-- /question-block -->

			<view class="bottom-space"></view>
		</scroll-view>

		

		<!-- 老师讲解悬浮按钮 -->
		<view class="teacher-float" @click="askTeacher" v-if="showTeacherFloat">
			<image class="teacher-avatar" src="https://picsum.photos/80/80?random=teacher" mode="aspectFill"></image>
			<text class="teacher-text">点我讲题</text>
		</view>

		<!-- 底部导航栏（内联） -->
		<view class="bottom-tab-bar">
			<view
				v-for="(tab, index) in bottomTabs"
				:key="index"
				class="bottom-tab-item"
				:class="{ active: index === 2 }"
				@click="goBottomTab(index)"
			>
				<text class="tab-icon">{{ tab.icon }}</text>
				<text class="tab-name">{{ tab.name }}</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { questionApi } from '@/api/index.js'

// ========= 响应式状态 =========

const currentMode = ref<'answer' | 'review' | 'video'>('answer')
const showAd = ref(true)
const currentIndex = ref(1)
const totalQuestions = ref(0)
const selectedOption = ref(null)
const hasAnswered = ref(false)
const isCorrect = ref(false)
const isCollected = ref(false)
const wrongCount = ref(1)
const rightCount = ref(19)
const showTeacherFloat = ref(true)
// 老师入口统一走底部导航「找课」→ teacher-manage；此处保留便于以后接登录态
const isTeacher = ref(true)
const commentSort = ref('hot')
const newComment = ref('')
const questionList = ref([])
/** 接口成功但无题目时显示空状态；接口失败时用默认题不显示空状态 */
const showEmptyState = ref(false)

const bottomTabs = [
  { name: '首页', icon: '🏠' },
  { name: '找课', icon: '📝' },
  { name: '刷题', icon: '✏' },
  { name: '社区', icon: '💬' },
  { name: '我', icon: '👤' }
]
function goBottomTab(index) {
  if (index === 2) return
  if (index === 1 && isTeacher.value) {
    uni.reLaunch({ url: '/pages/teacher/teacher-manage' })
    return
  }
  const routes = ['/pages/index/index', '/pages/index/index', '/pages/quiz/quiz', '/pages/index/index', '/pages/index/index']
  uni.reLaunch({ url: routes[index] })
}

// 默认示例题目（中小学题型），后端加载成功后会覆盖
const currentQuestion = ref({
  id: 1,
  content: '小明有 12 个苹果，吃了 3 个，又买来 5 个。现在小明一共有几个苹果？',
  imageUrl: '',
  videoThumb: 'https://picsum.photos/600/340?random=video',
  options: [
    { letter: 'A', content: '10 个' },
    { letter: 'B', content: '13 个' },
    { letter: 'C', content: '14 个' },
    { letter: 'D', content: '15 个' }
  ],
  correctAnswer: 2,
  tip: '先减后加，注意顺序。',
  explanation: '12 - 3 = 9（个），9 + 5 = 14（个）。现在一共有 14 个苹果。'
})

// 默认示例评论（中小学学习场景）
const comments = ref([
  {
    name: '乐乐妈妈',
    avatar: 'https://picsum.photos/60/60?random=1',
    text: '这类应用题要先理清数量关系，孩子多做几道就熟了！',
    likes: 128,
    liked: false,
    time: '2小时前',
    isAuthor: false
  },
  {
    name: '五年级小明',
    avatar: 'https://picsum.photos/60/60?random=2',
    text: '老师讲过先算吃了以后剩几个，再加买来的，答案 14 个。',
    likes: 256,
    liked: true,
    time: '5小时前',
    isAuthor: false
  },
  {
    name: '张老师',
    avatar: 'https://picsum.photos/60/60?random=3',
    text: '加减混合运算要按顺序算，注意审题别漏掉步骤哦。',
    likes: 89,
    liked: false,
    time: '1天前',
    isAuthor: true
  }
])

// ========= 生命周期 =========

onLoad(async (options) => {
  const categoryId = options && options.categoryId ? parseInt(options.categoryId, 10) : undefined
  await loadQuestions(categoryId)
})

// ========= 方法 =========

// 加载题目列表（真数据，可选按分类筛选）
async function loadQuestions(categoryId) {
  uni.showLoading({ title: '加载中...' })
  try {
    const res = await questionApi.getQuestionList({
      page: 1,
      size: 20,
      ...(categoryId ? { categoryId } : {})
    })
    const list = res?.list || []
    const total = res?.total ?? 0
    questionList.value = list
    totalQuestions.value = total
    showEmptyState.value = list.length === 0

    if (list.length > 0) {
      await loadQuestionDetail(list[0].id)
    } else {
      currentQuestion.value = null
    }
  } catch (e) {
    showEmptyState.value = false
    console.warn('加载题目失败，使用默认示例题目', e)
  } finally {
    uni.hideLoading()
  }
}

// 加载题目详情
async function loadQuestionDetail(id) {
  uni.showLoading({ title: '加载中...' })
  try {
    const question = await questionApi.getQuestionDetail(id)

    currentQuestion.value = {
      ...question,
      videoThumb: question.videoUrl || 'https://picsum.photos/600/340?random=video'
    }

    // 重置答题状态
    selectedOption.value = null
    hasAnswered.value = question.hasAnswered || false
    isCorrect.value = question.correct || false
    isCollected.value = question.isFavorited || false
  } catch (e) {
    uni.showToast({ title: '加载题目详情失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

function goBack() {
  uni.navigateBack()
}

function switchMode(mode) {
  currentMode.value = mode
}

function showSettings() {
  uni.showToast({ title: '设置功能', icon: 'none' })
}

function closeAd() {
  showAd.value = false
}

function readQuestion() {
  uni.showToast({ title: '语音读题', icon: 'none' })
}

// 选择选项并提交答案
async function selectOption(index) {
  if (hasAnswered.value) return

  selectedOption.value = index

  try {
    uni.showLoading({ title: '提交中...' })
    const result = await questionApi.submitAnswer(currentQuestion.value.id, index)
    
    // 调试输出
    console.log('submitAnswer result:', JSON.stringify(result))
    console.log('correctAnswer:', result.correctAnswer, 'type:', typeof result.correctAnswer)

    hasAnswered.value = true
    // 后端返回的是 correct 字段（Boolean）
    isCorrect.value = result.correct === true

    // 更新题目数据（包含解析）- 确保 correctAnswer 有效
    const updatedQuestion = {
      ...currentQuestion.value,
      ...result
    }
    // 如果后端没有返回 correctAnswer，保留原来的值
    if (result.correctAnswer === undefined || result.correctAnswer === null) {
      updatedQuestion.correctAnswer = currentQuestion.value.correctAnswer
    }
    currentQuestion.value = updatedQuestion

    if (isCorrect.value) {
      rightCount.value++
      uni.showToast({ title: '回答正确！', icon: 'success' })
    } else {
      wrongCount.value++
      uni.showToast({ title: '回答错误', icon: 'none' })
    }
  } catch (e) {
    // 接口失败时本地判题，避免“提交失败”且保证 loading 配对
    const correctIndex = currentQuestion.value.correctAnswer
    const ok = correctIndex !== undefined && index === correctIndex
    hasAnswered.value = true
    isCorrect.value = ok
    if (ok) {
      rightCount.value++
      uni.showToast({ title: '回答正确！', icon: 'success' })
    } else {
      wrongCount.value++
      uni.showToast({ title: '回答错误', icon: 'none' })
    }
  } finally {
    uni.hideLoading()
  }
}

function viewFullTip() {
  uni.showModal({
    title: '完整技巧',
    content: currentQuestion.value.explanation,
    showCancel: false
  })
}

function playVideo() {
  uni.showToast({ title: '播放视频讲解', icon: 'none' })
}

function sortComments(sort) {
  commentSort.value = sort
}

function likeComment(index) {
  const comment = comments.value[index]
  comment.liked = !comment.liked
  comment.likes += comment.liked ? 1 : -1
}

function replyComment(index) {
  uni.showToast({ title: `回复${comments.value[index].name}`, icon: 'none' })
}

function submitComment() {
  if (!newComment.value.trim()) {
    uni.showToast({ title: '请输入评论内容', icon: 'none' })
    return
  }
  comments.value.unshift({
    name: '我',
    avatar: 'https://picsum.photos/60/60?random=me',
    text: newComment.value,
    likes: 0,
    liked: false,
    time: '刚刚',
    isAuthor: false
  })
  newComment.value = ''
  uni.showToast({ title: '评论发表成功', icon: 'success' })
}

function askTeacher() {
  uni.showToast({ title: '呼叫老师讲解', icon: 'none' })
}

// 收藏/取消收藏题目
async function collectQuestion() {
  uni.showLoading({ title: '处理中...' })
  try {
    const isFavorited = await questionApi.toggleFavorite(currentQuestion.value.id)
    isCollected.value = !!isFavorited
    uni.showToast({
      title: isCollected.value ? '已收藏' : '取消收藏',
      icon: 'none'
    })
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

function viewWrong() {
  uni.showToast({ title: `错题本：${wrongCount.value}题`, icon: 'none' })
}

function viewRight() {
  uni.showToast({ title: `对题：${rightCount.value}题`, icon: 'none' })
}

function showQuestionList() {
  uni.showToast({ title: '题目列表', icon: 'none' })
}

// 上一题
function goToPrevQuestion() {
  const currentId = currentQuestion.value?.id
  const currentIndex = questionList.value.findIndex(q => q.id === currentId)
  if (currentIndex > 0) {
    const prevId = questionList.value[currentIndex - 1].id
    loadQuestionDetail(prevId)
  } else if (currentIndex === 0) {
    uni.showToast({ title: '已经是第一题了', icon: 'none' })
  } else {
    uni.showToast({ title: '暂无更多题目', icon: 'none' })
  }
}

// 下一题
function goToNextQuestion() {
  const currentId = currentQuestion.value?.id
  const currentIndex = questionList.value.findIndex(q => q.id === currentId)
  if (currentIndex >= 0 && currentIndex < questionList.value.length - 1) {
    const nextId = questionList.value[currentIndex + 1].id
    loadQuestionDetail(nextId)
  } else if (currentIndex === questionList.value.length - 1) {
    uni.showToast({ title: '已经是最后一题了', icon: 'none' })
  } else {
    uni.showToast({ title: '暂无更多题目', icon: 'none' })
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

	/* 题目区域 */
	.quiz-content {
		flex: 1;
		padding: 30rpx;
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

	/* 上一题/下一题按钮 */
	.nav-question-section {
		margin: 30rpx 0;
		display: flex;
		justify-content: space-between;
		gap: 20rpx;
	}
	.nav-btn {
		flex: 1;
		border-radius: 50rpx;
		padding: 30rpx 40rpx;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.prev-btn {
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
	}
	.prev-btn .nav-text, .prev-btn .nav-icon {
		color: #666;
	}
	.next-btn {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}
	.next-btn .nav-text, .next-btn .nav-icon {
		color: #fff;
	}
	.nav-text {
		font-size: 32rpx;
		font-weight: bold;
		margin: 0 10rpx;
	}
	.nav-icon {
		font-size: 36rpx;
	}

	.bottom-space {
		height: 140rpx;
	}

	/* 底部导航栏 */
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
</style>
