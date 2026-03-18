<template>
	<view class="page-container">
		<!-- 固定头部区域 -->
		<view class="fixed-header">
			<!-- 状态栏 -->
			<view class="status-bar">
				<text class="time">23:45</text>
				<view class="status-icons">
					<text class="icon">📶</text>
					<text class="icon">📡</text>
					<text class="icon">🔋</text>
				</view>
			</view>

			<!-- 搜索栏 -->
			<view class="search-header">
				<view class="check-in-btn">
					<text class="check-icon">☑</text>
					<text class="check-text">签到</text>
				</view>
				<view class="search-box">
					<text class="search-icon">🔍</text>
					<input class="search-input" type="text" placeholder="搜课程、找练习" />
				</view>
				<view class="header-right">
					<view class="live-btn">
						<text class="live-tag">LIVE</text>
						<text class="live-text">直播课</text>
					</view>
					<view class="message-btn">
						<text class="message-icon">✉</text>
					</view>
				</view>
			</view>

			<!-- 分类标签 -->
			<scroll-view class="category-tabs" scroll-x="true" show-scrollbar="false">
				<view 
					v-for="(tab, index) in tabs" 
					:key="index"
					:class="['tab-item', activeTab === index ? 'active' : '']"
					@click="switchTab(index)"
				>
					<text>{{tab}}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 页面内容区域 - 使用swiper实现切换 -->
		<swiper 
			class="content-swiper" 
			:current="activeTab" 
			@change="onSwiperChange"
			:duration="300"
		>
			<!-- 推荐页面 -->
			<swiper-item>
				<scroll-view 
					class="page-scroll" 
					scroll-y="true" 
					@scroll="onRecommendScroll"
				>
					<view class="recommend-page" id="recommendPage">
						<!-- 好课秒杀横幅 -->
						<view class="banner-section">
							<view class="banner-card">
								<image class="banner-image" src="https://picsum.photos/700/200?random=100" mode="aspectFill"></image>
								<view class="banner-content">
									<text class="banner-tag">好课秒杀</text>
									<view class="banner-title">
										<text class="title-main">低至</text>
										<text class="title-highlight">5折</text>
										<text class="title-main">速抢</text>
									</view>
									<text class="banner-subtitle">本周特惠好课</text>
								</view>
							</view>
						</view>

						<!-- 分类图标 - 全部分区固定，右侧4个可滑动，支持吸顶 -->
						<view class="category-wrapper" :class="{ 'sticky': categorySticky }">
							<!-- 固定的全部分区 -->
							<view class="icon-item fixed-category">
								<view class="icon-box" style="background-color: #4A90E2">
									<text class="iconfont">⊞</text>
								</view>
								<text class="icon-name">全部分区</text>
							</view>
							<!-- 可滑动的分类区域 - 使用scroll-view -->
							<scroll-view class="category-scroll-box" scroll-x="true" show-scrollbar="false">
								<view class="category-row">
									<view class="icon-item scrollable-item" v-for="(item, index) in scrollableCategories" :key="index">
										<view class="icon-box" :style="{backgroundColor: item.bgColor}">
											<text class="iconfont">{{item.icon}}</text>
										</view>
										<text class="icon-name">{{item.name}}</text>
									</view>
								</view>
							</scroll-view>
						</view>

						<!-- 刷题分类（后端题目分类 type=2） -->
						<view v-if="quizCategories.length" class="quiz-categories-section">
							<view class="section-header">
								<text class="section-title">按分类刷题</text>
								<text class="arrow-icon" @click="goToQuiz()">去刷题 ›</text>
							</view>
							<scroll-view class="quiz-categories-scroll" scroll-x="true" show-scrollbar="false">
								<view class="quiz-categories-row">
									<view 
										class="quiz-cat-item" 
										v-for="(cat, index) in quizCategories" 
										:key="cat.id || index"
										@click="goToQuiz(cat.id)"
									>
										<view class="quiz-cat-icon" :style="{ backgroundColor: cat.bgColor || '#00d26a' }">{{ cat.icon || '✏' }}</view>
										<text class="quiz-cat-name">{{ cat.name }}</text>
									</view>
								</view>
							</scroll-view>
						</view>

						<!-- 排行榜轮播（精品好课、最多收藏、热播好课） -->
						<view class="ranking-carousel-section">
							<view class="section-header" @click="goToMore('课程排行榜')">
								<text class="section-title">课程排行榜</text>
								<text class="arrow-icon">›</text>
							</view>
							<swiper class="ranking-swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500">
								<swiper-item v-for="(rankPage, pageIndex) in rankingList" :key="pageIndex">
									<view class="ranking-page">
										<text class="ranking-title">{{rankPage.title}}</text>
										<view class="ranking-courses">
											<view class="ranking-course-item" v-for="(course, cIndex) in rankPage.courses" :key="cIndex">
												<view class="rank-num">{{cIndex + 1}}</view>
												<image class="rank-course-image" :src="course.image" mode="aspectFill"></image>
												<view class="rank-course-info">
													<text class="rank-course-title">{{course.title}}</text>
													<text class="rank-course-plays">{{course.plays}}次播放</text>
												</view>
											</view>
										</view>
									</view>
								</swiper-item>
							</swiper>
						</view>

						<!-- 今日最多收藏TOP3 -->
						<view class="rank-section">
							<view class="section-header">
								<view class="title-with-arrow">
									<text class="section-title">今日最多收藏TOP3</text>
									<text class="arrow-icon">›</text>
								</view>
								<text class="tag">精辟</text>
							</view>
							<view class="rank-list">
								<view class="rank-item" v-for="(item, index) in topCollectionList" :key="index">
									<view class="rank-number">{{index + 1}}</view>
									<image class="rank-thumb" :src="item.image" mode="aspectFill"></image>
									<view class="rank-info">
										<text class="rank-title">{{item.title}}</text>
										<text class="rank-play">{{item.plays}}次播放</text>
									</view>
									<view class="rank-side-thumb">
										<image :src="item.sideImage" mode="aspectFill"></image>
										<text class="side-rank">{{index + 1}}</text>
									</view>
								</view>
							</view>
						</view>

						<!-- 三级目录视频列表 - 九宫格滑动 -->
						<view class="video-directory">
							<view class="directory-section" v-for="(section, sIndex) in videoSections" :key="sIndex">
								<view class="directory-header">
									<text class="directory-title">{{section.title}}</text>
									<text class="directory-more">点击整页面游览</text>
								</view>
								<!-- 3x3九宫格滑动 -->
								<swiper class="nine-grid-swiper" :indicator-dots="true" :autoplay="false" :duration="300">
									<swiper-item v-for="(page, pIndex) in section.pages" :key="pIndex">
										<view class="nine-grid">
											<view class="grid-row" v-for="(row, rIndex) in page.rows" :key="rIndex">
												<view class="grid-item" v-for="(item, iIndex) in row" :key="iIndex">
													<image class="grid-image" :src="item.image" mode="aspectFill"></image>
													<text class="grid-title">{{item.title}}</text>
												</view>
											</view>
										</view>
									</swiper-item>
								</swiper>
							</view>
						</view>
						<view class="bottom-space"></view>
					</view>
				</scroll-view>
			</swiper-item>

			<!-- 公开课页面 -->
			<swiper-item>
				<scroll-view class="page-scroll" scroll-y="true">
					<view class="open-course-page">
						<!-- 公开课排行榜 -->
						<view class="ranking-carousel-section">
							<view class="section-header" @click="goToMore('公开课排行榜')">
								<text class="section-title">公开课排行榜</text>
								<text class="arrow-icon">›</text>
							</view>
							<view class="open-course-list">
								<view class="open-course-item" v-for="(course, cIndex) in openCourseRanking" :key="cIndex">
									<view class="rank-num large">{{cIndex + 1}}</view>
									<image class="open-course-image" :src="course.image" mode="aspectFill"></image>
									<view class="open-course-info">
										<text class="open-course-title">{{course.title}}</text>
										<text class="open-course-plays">{{course.plays}}次播放</text>
									</view>
								</view>
							</view>
						</view>

						<!-- 热门大学 -->
						<view class="university-section">
							<view class="section-header" @click="goToMore('热门大学')">
								<text class="section-title">热门大学</text>
								<text class="arrow-icon">›</text>
							</view>
							<view class="university-grid">
								<view class="university-item" v-for="(uni, index) in universityList" :key="index">
									<image class="uni-logo" :src="uni.logo" mode="aspectFill"></image>
									<view class="uni-info">
										<text class="uni-name">{{uni.name}}</text>
										<text class="uni-courses">{{uni.courses}}门课程</text>
									</view>
								</view>
							</view>
						</view>

						<!-- 热门推荐 -->
						<view class="hot-recommend-section">
							<view class="section-header">
								<text class="section-title">热门推荐</text>
							</view>
							<view class="hot-course-grid">
								<view class="hot-course-card" v-for="(course, index) in hotCourses" :key="index">
									<view class="hot-course-image">
										<image class="hot-thumb" :src="course.image" mode="aspectFill"></image>
										<view class="hot-plays">{{course.plays}}</view>
										<view class="hot-tag" v-if="course.tag">{{course.tag}}</view>
										<view class="hot-duration" v-if="course.duration">{{course.duration}}</view>
									</view>
									<text class="hot-course-title">{{course.title}}</text>
									<view class="hot-course-meta">
										<text class="hot-label" v-if="course.label">{{course.label}}</text>
										<text class="hot-play-count">{{course.playCount}}播放</text>
									</view>
								</view>
							</view>
						</view>
						<view class="bottom-space"></view>
					</view>
				</scroll-view>
			</swiper-item>

			<!-- 精品课/演讲/纪录片页面 -->
			<swiper-item v-for="tabIndex in [2, 3, 4]" :key="tabIndex">
				<scroll-view class="page-scroll" scroll-y="true">
					<view class="other-page">
						<!-- 分类图标 -->
						<view class="category-icons">
							<view class="icon-item" v-for="(item, index) in categories" :key="index">
								<view class="icon-box" :style="{backgroundColor: item.bgColor}">
									<text class="iconfont">{{item.icon}}</text>
								</view>
								<text class="icon-name">{{item.name}}</text>
							</view>
						</view>

						<!-- 多个课程模块 -->
						<view v-for="(module, moduleIndex) in courseModules" :key="moduleIndex" class="course-module">
							<view class="section-header">
								<text class="section-title">{{module.title}}</text>
								<view class="more-btn" v-if="module.showMore" @click="goToMore(module.title)">
									<text class="more-text">更多</text>
									<text class="arrow">›</text>
								</view>
							</view>
							<view class="course-grid">
								<view class="course-card" v-for="(course, courseIndex) in module.courses" :key="courseIndex">
									<view class="course-image">
										<image class="thumb" :src="course.image" mode="aspectFill"></image>
										<view class="course-tag" v-if="course.tag">
											<text>{{course.tag}}</text>
										</view>
										<view class="episode-count">
											<text>共{{course.episodes}}集</text>
										</view>
									</view>
									<view class="course-info">
										<text class="course-title">{{course.title}}</text>
										<text class="course-subtitle" v-if="course.subtitle">{{course.subtitle}}</text>
										<view class="course-meta">
											<text class="price" :class="{ free: course.price === '0' }">¥{{course.price}}</text>
											<text class="students">{{course.students}}人在学</text>
										</view>
									</view>
								</view>
							</view>
						</view>
						<view class="bottom-space"></view>
					</view>
				</scroll-view>
			</swiper-item>
		</swiper>

		<!-- 底部导航栏（内联，避免组件解析白屏） -->
		<view class="bottom-tab-bar">
			<view
				v-for="(tab, index) in bottomTabs"
				:key="index"
				class="bottom-tab-item"
				:class="{ active: activeBottomTab === index }"
				@click="switchBottomTab(index)"
			>
				<text class="tab-icon">{{ tab.icon }}</text>
				<text class="tab-name">{{ tab.name }}</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { categoryApi } from '@/api/index.js'

	export default {
		data() {
			return {
				activeTab: 0,
				activeBottomTab: 0,
				categorySticky: false,
				tabs: ['推荐', '公开课', '精品课', '演讲', '纪录片'],
				bottomTabs: [
					{ name: '首页', icon: '🏠' },
					{ name: '找课', icon: '📝' },
					{ name: '刷题', icon: '✏' },
					{ name: '社区', icon: '💬' },
					{ name: '我', icon: '👤' }
				],

				// 推荐页面数据 - 可滑动的分类（8个，不含全部分区）
				scrollableCategories: [
					{ name: '精品课', icon: '▶', bgColor: '#00d26a' },
					{ name: '播客听书', icon: '◉', bgColor: '#00c8a0' },
					{ name: '考试考证', icon: '📋', bgColor: '#5A7FFF' },
					{ name: '职场', icon: '💼', bgColor: '#4A90E2' },
					{ name: '心理', icon: '♥', bgColor: '#5A7FFF' },
					{ name: '外语学习', icon: 'A', bgColor: '#00c8a0' },
					{ name: '理工学科', icon: '⚗', bgColor: '#00d26a' },
					{ name: '语言文学', icon: '文', bgColor: '#4A90E2' }
				],
				// 题目分类（后端 type=2），用于「按分类刷题」
				quizCategories: [],
				// 三级目录视频列表数据 - 3x3九宫格分页
				videoSections: [
					{
						title: '六年级上册数学',
						pages: [
							{
								// 第一页 3x3
								rows: [
									[{ title: '1.1', image: 'https://picsum.photos/200/150?random=101' }, { title: '1.2', image: 'https://picsum.photos/200/150?random=102' }, { title: '1.3', image: 'https://picsum.photos/200/150?random=103' }],
									[{ title: '1.4', image: 'https://picsum.photos/200/150?random=104' }, { title: '1.5', image: 'https://picsum.photos/200/150?random=105' }, { title: '1.6', image: 'https://picsum.photos/200/150?random=106' }],
									[{ title: '1.7', image: 'https://picsum.photos/200/150?random=107' }, { title: '1.8', image: 'https://picsum.photos/200/150?random=108' }, { title: '1.9', image: 'https://picsum.photos/200/150?random=109' }]
								]
							},
							{
								// 第二页 3x3
								rows: [
									[{ title: '1.10', image: 'https://picsum.photos/200/150?random=110' }, { title: '1.11', image: 'https://picsum.photos/200/150?random=111' }, { title: '1.12', image: 'https://picsum.photos/200/150?random=112' }],
									[{ title: '1.13', image: 'https://picsum.photos/200/150?random=113' }, { title: '1.14', image: 'https://picsum.photos/200/150?random=114' }, { title: '1.15', image: 'https://picsum.photos/200/150?random=115' }],
									[{ title: '1.16', image: 'https://picsum.photos/200/150?random=116' }, { title: '1.17', image: 'https://picsum.photos/200/150?random=117' }, { title: '1.18', image: 'https://picsum.photos/200/150?random=118' }]
								]
							}
						]
					},
					{
						title: '六年级下册数学',
						pages: [
							{
								rows: [
									[{ title: '2.1', image: 'https://picsum.photos/200/150?random=201' }, { title: '2.2', image: 'https://picsum.photos/200/150?random=202' }, { title: '2.3', image: 'https://picsum.photos/200/150?random=203' }],
									[{ title: '2.4', image: 'https://picsum.photos/200/150?random=204' }, { title: '2.5', image: 'https://picsum.photos/200/150?random=205' }, { title: '2.6', image: 'https://picsum.photos/200/150?random=206' }],
									[{ title: '2.7', image: 'https://picsum.photos/200/150?random=207' }, { title: '2.8', image: 'https://picsum.photos/200/150?random=208' }, { title: '2.9', image: 'https://picsum.photos/200/150?random=209' }]
								]
							},
							{
								rows: [
									[{ title: '2.10', image: 'https://picsum.photos/200/150?random=210' }, { title: '2.11', image: 'https://picsum.photos/200/150?random=211' }, { title: '2.12', image: 'https://picsum.photos/200/150?random=212' }],
									[{ title: '2.13', image: 'https://picsum.photos/200/150?random=213' }, { title: '2.14', image: 'https://picsum.photos/200/150?random=214' }, { title: '2.15', image: 'https://picsum.photos/200/150?random=215' }],
									[{ title: '2.16', image: 'https://picsum.photos/200/150?random=216' }, { title: '2.17', image: 'https://picsum.photos/200/150?random=217' }, { title: '2.18', image: 'https://picsum.photos/200/150?random=218' }]
								]
							}
						]
					},
					{
						title: '奥数',
						pages: [
							{
								rows: [
									[{ title: '3.1', image: 'https://picsum.photos/200/150?random=301' }, { title: '3.2', image: 'https://picsum.photos/200/150?random=302' }, { title: '3.3', image: 'https://picsum.photos/200/150?random=303' }],
									[{ title: '3.4', image: 'https://picsum.photos/200/150?random=304' }, { title: '3.5', image: 'https://picsum.photos/200/150?random=305' }, { title: '3.6', image: 'https://picsum.photos/200/150?random=306' }],
									[{ title: '3.7', image: 'https://picsum.photos/200/150?random=307' }, { title: '3.8', image: 'https://picsum.photos/200/150?random=308' }, { title: '3.9', image: 'https://picsum.photos/200/150?random=309' }]
								]
							},
							{
								rows: [
									[{ title: '3.10', image: 'https://picsum.photos/200/150?random=310' }, { title: '3.11', image: 'https://picsum.photos/200/150?random=311' }, { title: '3.12', image: 'https://picsum.photos/200/150?random=312' }],
									[{ title: '3.13', image: 'https://picsum.photos/200/150?random=313' }, { title: '3.14', image: 'https://picsum.photos/200/150?random=314' }, { title: '3.15', image: 'https://picsum.photos/200/150?random=315' }],
									[{ title: '3.16', image: 'https://picsum.photos/200/150?random=316' }, { title: '3.17', image: 'https://picsum.photos/200/150?random=317' }, { title: '3.18', image: 'https://picsum.photos/200/150?random=318' }]
								]
							}
						]
					}
				],
				topCollectionList: [
					{
						title: '学霸分享:学不下去时,怎么办?',
						plays: '41.8万',
						image: 'https://picsum.photos/120/80?random=1',
						sideImage: 'https://picsum.photos/80/80?random=11'
					},
					{
						title: '谁才是你无节制消费的元凶?',
						plays: '65.1万',
						image: 'https://picsum.photos/120/80?random=2',
						sideImage: 'https://picsum.photos/80/80?random=12'
					},
					{
						title: '哲理短片丨别焦虑！人间值得',
						plays: '35.4万',
						image: 'https://picsum.photos/120/80?random=3',
						sideImage: 'https://picsum.photos/80/80?random=13'
					}
				],

				// 公开课页面数据
				openCourseRanking: [
					{
						title: '中国人民大学公开课：法理学',
						plays: '423.8万',
						image: 'https://picsum.photos/300/160?random=20'
					},
					{
						title: '易经完全通（高级）全集',
						plays: '760.6万',
						image: 'https://picsum.photos/300/160?random=21'
					},
					{
						title: 'Python编程从入门到精通',
						plays: '892.3万',
						image: 'https://picsum.photos/300/160?random=22'
					},
					{
						title: 'Photoshop全能教程',
						plays: '567.2万',
						image: 'https://picsum.photos/300/160?random=23'
					}
				],
				rankingList: [
					{
						title: '精品好课排行榜',
						courses: [
							{
								title: '中国人民大学公开课：法理学',
								plays: '423.8万',
								image: 'https://picsum.photos/300/160?random=20'
							},
							{
								title: '易经完全通（高级）全集',
								plays: '760.6万',
								image: 'https://picsum.photos/300/160?random=21'
							}
						]
					},
					{
						title: '最多收藏排行榜',
						courses: [
							{
								title: 'Python编程从入门到精通',
								plays: '892.3万',
								image: 'https://picsum.photos/300/160?random=22'
							},
							{
								title: 'Photoshop全能教程',
								plays: '567.2万',
								image: 'https://picsum.photos/300/160?random=23'
							}
						]
					},
					{
						title: '热播好课排行榜',
						courses: [
							{
								title: '零基础学日语',
								plays: '345.6万',
								image: 'https://picsum.photos/300/160?random=24'
							},
							{
								title: '职场沟通技巧',
								plays: '234.1万',
								image: 'https://picsum.photos/300/160?random=25'
							}
						]
					}
				],
				universityList: [
					{ name: '哈佛大学', courses: '43', logo: 'https://picsum.photos/60/60?random=30' },
					{ name: '清华大学', courses: '36', logo: 'https://picsum.photos/60/60?random=31' },
					{ name: '牛津大学', courses: '72', logo: 'https://picsum.photos/60/60?random=32' },
					{ name: '北京大学', courses: '73', logo: 'https://picsum.photos/60/60?random=33' },
					{ name: '耶鲁大学', courses: '33', logo: 'https://picsum.photos/60/60?random=34' },
					{ name: '复旦大学', courses: '89', logo: 'https://picsum.photos/60/60?random=35' }
				],
				hotCourses: [
					{
						title: '少女漫画中的亲密关系:少女漫画史（上）',
						image: 'https://picsum.photos/320/180?random=40',
						plays: '7.6万',
						tag: '戏剧与影视',
						duration: '45课时',
						label: '大学课程',
						playCount: '10万'
					},
					{
						title: '【新概念英语】全四册+讲解版(1)',
						image: 'https://picsum.photos/320/180?random=41',
						plays: '78.3万',
						tag: 'Tube口语听力',
						duration: '09:04',
						label: '',
						playCount: '10万'
					},
					{
						title: '沟通的方法：职场高效沟通技巧',
						image: 'https://picsum.photos/320/180?random=42',
						plays: '81.9万',
						tag: '',
						duration: '',
						label: '职场技能',
						playCount: '50万'
					},
					{
						title: '数据分析从入门到精通',
						image: 'https://picsum.photos/320/180?random=43',
						plays: '70.0万',
						tag: '',
						duration: '',
						label: '技术课程',
						playCount: '30万'
					}
				],

				// 其他页面数据
				categories: [
					{ name: '个人提升', icon: '📚', bgColor: '#4A90E2' },
					{ name: '人文历史', icon: '🏛', bgColor: '#50C878' },
					{ name: '升职加薪', icon: '💼', bgColor: '#4A90E2' },
					{ name: '好书导读', icon: '📖', bgColor: '#5A7FFF' },
					{ name: '生活知识', icon: '📷', bgColor: '#4A90E2' }
				],
				courseModules: [
					{
						title: '精品推荐',
						showMore: false,
						courses: [
							{
								title: '10套高情商人际交往术，成为受欢迎的人',
								subtitle: '',
								image: 'https://picsum.photos/400/300?random=1',
								price: '99',
								students: '4832',
								episodes: '10',
								tag: '人气大V讲授'
							},
							{
								title: '职场中遇到突发健康问题该怎么办',
								subtitle: '',
								image: 'https://picsum.photos/400/300?random=2',
								price: '70',
								students: '872',
								episodes: '10',
								tag: ''
							},
							{
								title: '高情商沟通课',
								subtitle: '',
								image: 'https://picsum.photos/400/300?random=3',
								price: '99',
								students: '2098',
								episodes: '10',
								tag: '职场沟通'
							},
							{
								title: '青少年人格发展与培养',
								subtitle: '',
								image: 'https://picsum.photos/400/300?random=4',
								price: '99',
								students: '927',
								episodes: '10',
								tag: '专业推荐'
							}
						]
					},
					{
						title: '经济',
						showMore: true,
						courses: [
							{
								title: '《从T+1到T+0》期货筑基训练营-3期',
								subtitle: '期货筑基训练营',
								image: 'https://picsum.photos/400/300?random=7',
								price: '0',
								students: '3004',
								episodes: '1',
								tag: ''
							},
							{
								title: '《从T+1到T+0》期货筑基训练营',
								subtitle: '期货筑基训练营',
								image: 'https://picsum.photos/400/300?random=8',
								price: '0',
								students: '3446',
								episodes: '1',
								tag: ''
							}
						]
					}
				]
			}
		},
		onLoad() {
			// 课程分类（type=1）→ 首页可滑动分类
			categoryApi.getCategoryList(1).then((list) => {
				if (list && list.length) {
					this.scrollableCategories = list.map((c) => ({
						name: c.name,
						icon: c.icon || '▶',
						bgColor: c.bgColor || '#4A90E2'
					}))
				}
			}).catch(() => {})
			// 题目分类（type=2）→ 按分类刷题
			categoryApi.getCategoryList(2).then((list) => {
				if (list && list.length) {
					this.quizCategories = list.map((c) => ({
						id: c.id,
						name: c.name,
						icon: c.icon || '✏',
						bgColor: c.bgColor || '#00d26a'
					}))
				}
			}).catch(() => {})
		},
		methods: {
			switchTab(index) {
				console.log('切换标签:', index)
				this.activeTab = index
			},
			switchBottomTab(index) {
				if (this.activeBottomTab === index) return
				this.activeBottomTab = index
				const routes = ['/pages/index/index', '/pages/index/index', '/pages/quiz/quiz', '/pages/index/index', '/pages/index/index']
				uni.reLaunch({ url: routes[index] })
			},
			onSwiperChange(e) {
				console.log('swiper切换:', e.detail.current)
				this.activeTab = e.detail.current
			},	
			onRecommendScroll(e) {
				const scrollTop = e.detail.scrollTop
				// 获取banner的高度（约120rpx）作为临界点
				// 当banner完全滚出视口时，二级目录吸顶
				if (scrollTop >= 120) {
					this.categorySticky = true
				} else {
					this.categorySticky = false
				}
			},
			loadMore() {
				console.log('加载更多课程')
			},
			goToMore(moduleTitle) {
				console.log('进入更多页面:', moduleTitle)
				uni.showToast({
					title: `查看${moduleTitle}更多`,
					icon: 'none'
				})
			},
			goToQuiz(categoryId) {
				const url = categoryId ? `/pages/quiz/quiz?categoryId=${categoryId}` : '/pages/quiz/quiz'
				uni.navigateTo({ url })
			}
		}
	}
</script>

<style>
	/* 页面容器 */
	.page-container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f5f5f5;
	}

	/* 固定头部区域 */
	.fixed-header {
		flex-shrink: 0;
		background-color: #fff;
		z-index: 100;
	}

	/* 状态栏 */
	.status-bar {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 10rpx 30rpx;
		background-color: #fff;
	}

	.time {
		font-size: 28rpx;
		font-weight: 500;
	}

	.status-icons {
		display: flex;
		gap: 10rpx;
	}

	.icon {
		font-size: 24rpx;
	}

	/* 搜索栏 */
	.search-header {
		display: flex;
		align-items: center;
		padding: 20rpx 30rpx;
		background-color: #fff;
	}

	.check-in-btn {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-right: 20rpx;
	}

	.check-icon {
		font-size: 40rpx;
		color: #ff9500;
	}

	.check-text {
		font-size: 20rpx;
		color: #666;
		margin-top: 4rpx;
	}

	.search-box {
		flex: 1;
		display: flex;
		align-items: center;
		background-color: #f0f0f0;
		border-radius: 40rpx;
		padding: 15rpx 25rpx;
	}

	.search-icon {
		font-size: 28rpx;
		color: #999;
		margin-right: 10rpx;
	}

	.search-input {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.header-right {
		display: flex;
		align-items: center;
		margin-left: 20rpx;
	}

	.live-btn {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.live-tag {
		font-size: 18rpx;
		color: #fff;
		background: linear-gradient(135deg, #00d26a, #00a854);
		padding: 4rpx 10rpx;
		border-radius: 8rpx;
		font-weight: bold;
	}

	.live-text {
		font-size: 20rpx;
		color: #00a854;
		margin-top: 4rpx;
	}

	.message-btn {
		margin-left: 20rpx;
		padding: 10rpx;
	}

	.message-icon {
		font-size: 40rpx;
		color: #666;
	}

	/* 分类标签 */
	.category-tabs {
		display: flex;
		white-space: nowrap;
		background-color: #fff;
		padding: 0 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.tab-item {
		display: inline-flex;
		padding: 25rpx 30rpx;
		font-size: 30rpx;
		color: #666;
		position: relative;
	}

	.tab-item.active {
		color: #333;
		font-weight: bold;
		font-size: 32rpx;
	}

	.tab-item.active::after {
		content: '';
		position: absolute;
		bottom: 10rpx;
		left: 50%;
		transform: translateX(-50%);
		width: 40rpx;
		height: 6rpx;
		background-color: #333;
		border-radius: 3rpx;
	}

	/* 页面swiper */
	.content-swiper {
		flex: 1;
		height: calc(100vh - 300rpx);
	}

	/* 页面内滚动区域 */
	.page-scroll {
		height: 100%;
	}

	/* ========== 推荐页面样式 ========== */
	.recommend-page {
		padding-bottom: 20rpx;
	}

	/* 横幅区域 */
	.banner-section {
		padding: 20rpx;
		background-color: #fff;
	}

	.banner-card {
		position: relative;
		border-radius: 16rpx;
		overflow: hidden;
		height: 200rpx;
	}

	.banner-image {
		width: 100%;
		height: 100%;
	}

	.banner-content {
		position: absolute;
		left: 30rpx;
		top: 30rpx;
	}

	.banner-tag {
		font-size: 22rpx;
		color: #d4a574;
		background-color: rgba(0,0,0,0.3);
		padding: 4rpx 12rpx;
		border-radius: 8rpx;
		margin-bottom: 10rpx;
		display: inline-block;
	}

	.banner-title {
		display: flex;
		align-items: baseline;
		margin-bottom: 10rpx;
	}

	.title-main {
		font-size: 36rpx;
		color: #fff;
		font-weight: 500;
	}

	.title-highlight {
		font-size: 56rpx;
		color: #ffd700;
		font-weight: bold;
		margin: 0 10rpx;
	}

	.banner-subtitle {
		font-size: 28rpx;
		color: #fff;
	}

	/* 分类容器 - 固定+滑动布局 */
	.category-wrapper {
		display: flex;
		align-items: center;
		padding: 30rpx 0;
		background-color: #fff;
		margin-bottom: 20rpx;
		transition: all 0.3s ease;
	}

	/* 吸顶状态 - 紧贴一级目录下方（改成0rpx） */
	.category-wrapper.sticky {
		position: sticky;
		top: 0rpx;
		z-index: 50;
		margin-bottom: 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	/* 固定的全部分区 */
	.fixed-category {
		flex-shrink: 0;
		width: 20%;
		min-width: 140rpx;
		padding: 0 5rpx;
		border-right: 1rpx solid #f0f0f0;
	}

	/* 可滑动区域容器 */
	.category-scroll-box {
		flex: 1;
		height: 140rpx;
		white-space: nowrap;
	}

	/* 横向排列的分类 */
	.category-row {
		display: flex;
		align-items: center;
		height: 100%;
	}

	/* 每个可滑动的分类项 - 固定宽度确保一行显示4个（加上全部分区共5个） */
	.scrollable-item {
		flex-shrink: 0;
		width: 20%;
		min-width: 130rpx;
		padding: 0 5rpx;
	}

	/* 原有的分类图标样式 */
	.category-icons {
		display: flex;
		justify-content: space-around;
		padding: 30rpx 20rpx;
		background-color: #fff;
	}

	.icon-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.icon-box {
		width: 90rpx;
		height: 90rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 15rpx;
	}

	.iconfont {
		font-size: 48rpx;
		color: #fff;
	}

	.icon-name {
		font-size: 24rpx;
		color: #333;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width: 100%;
	}

	/* 排行区域 */
	.rank-section {
		background-color: #fff;
		padding: 20rpx 0;
		margin-bottom: 20rpx;
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0 30rpx 20rpx;
	}

	.title-with-arrow {
		display: flex;
		align-items: center;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.arrow-icon {
		font-size: 36rpx;
		color: #999;
		margin-left: 10rpx;
	}

	.tag {
		font-size: 28rpx;
		color: #666;
	}

	.rank-list {
		padding: 0 20rpx;
	}

	.rank-item {
		display: flex;
		align-items: center;
		padding: 15rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
	}

	.rank-number {
		width: 40rpx;
		height: 40rpx;
		background-color: #ffd700;
		color: #fff;
		font-size: 24rpx;
		font-weight: bold;
		border-radius: 8rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;
	}

	.rank-thumb {
		width: 140rpx;
		height: 90rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
	}

	.rank-info {
		flex: 1;
	}

	.rank-title {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 8rpx;
		display: block;
	}

	.rank-play {
		font-size: 24rpx;
		color: #999;
	}

	.rank-side-thumb {
		position: relative;
		width: 80rpx;
		height: 80rpx;
		border-radius: 12rpx;
		overflow: hidden;
	}

	.rank-side-thumb image {
		width: 100%;
		height: 100%;
	}

	.side-rank {
		position: absolute;
		top: 5rpx;
		left: 5rpx;
		width: 30rpx;
		height: 30rpx;
		background-color: #ffd700;
		color: #fff;
		font-size: 20rpx;
		border-radius: 6rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	/* ========== 三级目录视频列表样式 ========== */
	.video-directory {
		background-color: #f5f5f5;
		padding: 20rpx 0;
	}

	.directory-section {
		background-color: #fff;
		margin-bottom: 20rpx;
		padding: 20rpx 0;
	}

	.directory-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0 30rpx 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.directory-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}

	.directory-more {
		font-size: 24rpx;
		color: #00a854;
	}

	/* 九宫格swiper */
	.nine-grid-swiper {
		height: 600rpx;
		margin-top: 20rpx;
	}

	/* 九宫格布局 */
	.nine-grid {
		display: flex;
		flex-direction: column;
		padding: 0 20rpx;
		height: 100%;
	}

	.grid-row {
		display: flex;
		justify-content: space-between;
		flex: 1;
		margin-bottom: 20rpx;
	}

	.grid-row:last-child {
		margin-bottom: 0;
	}

	.grid-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		width: calc(33.33% - 14rpx);
	}

	.grid-image {
		width: 100%;
		height: 140rpx;
		border-radius: 12rpx;
		margin-bottom: 10rpx;
	}

	.grid-title {
		font-size: 24rpx;
		color: #333;
		text-align: center;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		width: 100%;
	}

	/* ========== 公开课页面样式 ========== */
	.open-course-page {
		padding-bottom: 20rpx;
	}

	/* 排行榜轮播 */
	.ranking-carousel-section {
		background-color: #fff;
		padding: 20rpx 0;
		margin-bottom: 20rpx;
	}

	.quiz-categories-section {
		background-color: #fff;
		padding: 20rpx 0;
		margin-bottom: 20rpx;
	}
	.quiz-categories-scroll {
		white-space: nowrap;
		padding: 0 20rpx;
	}
	.quiz-categories-row {
		display: inline-flex;
		gap: 24rpx;
		padding: 10rpx 0;
	}
	.quiz-cat-item {
		display: inline-flex;
		flex-direction: column;
		align-items: center;
		min-width: 120rpx;
	}
	.quiz-cat-icon {
		width: 80rpx;
		height: 80rpx;
		border-radius: 16rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 36rpx;
		color: #fff;
		margin-bottom: 8rpx;
	}
	.quiz-cat-name {
		font-size: 24rpx;
		color: #333;
	}

	.ranking-swiper {
		height: 400rpx;
		margin: 0 20rpx;
	}

	.ranking-page {
		background-color: #f8f8f8;
		border-radius: 16rpx;
		padding: 20rpx;
	}

	.ranking-title {
		font-size: 28rpx;
		color: #333;
		font-weight: bold;
		margin-bottom: 20rpx;
		display: block;
	}

	.ranking-courses {
		display: flex;
		gap: 20rpx;
	}

	.ranking-course-item {
		flex: 1;
		position: relative;
	}

	.rank-num {
		position: absolute;
		top: 10rpx;
		left: 10rpx;
		width: 40rpx;
		height: 40rpx;
		background-color: #ffd700;
		color: #fff;
		font-size: 24rpx;
		font-weight: bold;
		border-radius: 8rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 10;
	}

	.rank-num.large {
		position: static;
		width: 50rpx;
		height: 50rpx;
		font-size: 28rpx;
		margin-right: 20rpx;
	}

	.rank-course-image {
		width: 100%;
		height: 160rpx;
		border-radius: 12rpx;
		margin-bottom: 10rpx;
	}

	.rank-course-title {
		font-size: 24rpx;
		color: #333;
		display: block;
		margin-bottom: 6rpx;
		line-height: 1.3;
	}

	.rank-course-plays {
		font-size: 20rpx;
		color: #999;
	}

	/* 公开课排行榜列表 */
	.open-course-list {
		padding: 0 20rpx;
	}

	.open-course-item {
		display: flex;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
	}

	.open-course-image {
		width: 200rpx;
		height: 120rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
	}

	.open-course-info {
		flex: 1;
	}

	.open-course-title {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 10rpx;
		display: block;
		line-height: 1.4;
	}

	.open-course-plays {
		font-size: 24rpx;
		color: #999;
	}

	/* 热门大学 */
	.university-section {
		background-color: #fff;
		padding: 20rpx 0;
		margin-bottom: 20rpx;
	}

	.university-grid {
		display: flex;
		flex-wrap: wrap;
		padding: 0 20rpx;
		gap: 20rpx;
	}

	.university-item {
		width: calc(33.33% - 14rpx);
		display: flex;
		align-items: center;
		padding: 15rpx;
		background-color: #f8f8f8;
		border-radius: 12rpx;
	}

	.uni-logo {
		width: 60rpx;
		height: 60rpx;
		border-radius: 50%;
		margin-right: 15rpx;
	}

	.uni-info {
		flex: 1;
	}

	.uni-name {
		font-size: 24rpx;
		color: #333;
		font-weight: 500;
		display: block;
		margin-bottom: 4rpx;
	}

	.uni-courses {
		font-size: 20rpx;
		color: #999;
	}

	/* 热门推荐 */
	.hot-recommend-section {
		background-color: #fff;
		padding: 20rpx 0;
	}

	.hot-course-grid {
		display: flex;
		flex-wrap: wrap;
		padding: 0 20rpx;
		gap: 20rpx;
	}

	.hot-course-card {
		width: calc(50% - 10rpx);
		background-color: #fff;
		border-radius: 16rpx;
		overflow: hidden;
	}

	.hot-course-image {
		position: relative;
		width: 100%;
		height: 180rpx;
	}

	.hot-thumb {
		width: 100%;
		height: 100%;
		border-radius: 16rpx 16rpx 0 0;
	}

	.hot-plays {
		position: absolute;
		top: 10rpx;
		right: 10rpx;
		background-color: rgba(0,0,0,0.5);
		color: #fff;
		font-size: 20rpx;
		padding: 4rpx 10rpx;
		border-radius: 20rpx;
	}

	.hot-tag {
		position: absolute;
		bottom: 10rpx;
		left: 10rpx;
		background-color: rgba(0,0,0,0.6);
		color: #fff;
		font-size: 20rpx;
		padding: 4rpx 12rpx;
		border-radius: 8rpx;
	}

	.hot-duration {
		position: absolute;
		bottom: 10rpx;
		right: 10rpx;
		background-color: rgba(0,0,0,0.6);
		color: #fff;
		font-size: 20rpx;
		padding: 4rpx 12rpx;
		border-radius: 8rpx;
	}

	.hot-course-title {
		font-size: 28rpx;
		color: #333;
		padding: 15rpx;
		display: block;
		line-height: 1.4;
	}

	.hot-course-meta {
		display: flex;
		align-items: center;
		padding: 0 15rpx 15rpx;
	}

	.hot-label {
		font-size: 22rpx;
		color: #00a854;
		background-color: rgba(0,212,106,0.1);
		padding: 4rpx 10rpx;
		border-radius: 8rpx;
		margin-right: 15rpx;
	}

	.hot-play-count {
		font-size: 22rpx;
		color: #999;
	}

	/* ========== 其他页面公共样式 ========== */
	.other-page {
		padding-bottom: 20rpx;
	}

	.course-module {
		margin-bottom: 20rpx;
	}

	.more-btn {
		display: flex;
		align-items: center;
		font-size: 26rpx;
		color: #999;
		padding: 10rpx 0;
	}

	.more-text {
		font-size: 26rpx;
		color: #999;
	}

	.arrow {
		margin-left: 6rpx;
		font-size: 32rpx;
		color: #ccc;
		font-weight: 300;
	}

	.course-grid {
		display: flex;
		flex-wrap: wrap;
		padding: 0 20rpx;
		gap: 20rpx;
		background-color: #f5f5f5;
	}

	.course-card {
		width: calc(50% - 10rpx);
		background-color: #fff;
		border-radius: 16rpx;
		overflow: hidden;
	}

	.course-image {
		position: relative;
		width: 100%;
		height: 200rpx;
	}

	.thumb {
		width: 100%;
		height: 100%;
		border-radius: 16rpx 16rpx 0 0;
	}

	.course-tag {
		position: absolute;
		top: 10rpx;
		left: 10rpx;
		background-color: rgba(0, 0, 0, 0.6);
		color: #fff;
		font-size: 20rpx;
		padding: 4rpx 12rpx;
		border-radius: 8rpx;
	}

	.episode-count {
		position: absolute;
		bottom: 10rpx;
		right: 10rpx;
		background-color: rgba(0, 0, 0, 0.6);
		color: #fff;
		font-size: 20rpx;
		padding: 4rpx 12rpx;
		border-radius: 8rpx;
	}

	.course-info {
		padding: 20rpx;
	}

	.course-title {
		font-size: 28rpx;
		color: #333;
		line-height: 1.4;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
		margin-bottom: 8rpx;
	}

	.course-subtitle {
		font-size: 24rpx;
		color: #666;
		margin-bottom: 10rpx;
		display: block;
	}

	.course-meta {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.price {
		font-size: 32rpx;
		color: #ff6b6b;
		font-weight: bold;
	}

	.price.free {
		color: #00a854;
	}

	.students {
		font-size: 22rpx;
		color: #999;
	}

	/* 底部留白 - 为自定义tabBar留出空间 */
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
