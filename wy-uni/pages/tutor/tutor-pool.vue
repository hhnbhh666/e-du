<template>
	<view class="tutor-pool-page">
		<!-- 顶栏：城市 / 教员库·视频课 / 搜索 -->
		<view class="top-bar safe-top">
			<view class="city-row" @click="onCityTap">
				<text class="city-name">{{ cityName }}</text>
				<text class="city-arrow">▼</text>
			</view>
			<view class="main-tabs">
				<view
					class="main-tab"
					:class="{ active: mainTab === 0 }"
					@click="mainTab = 0"
				>
					<text>教员库</text>
					<view v-if="mainTab === 0" class="tab-underline"></view>
				</view>
				<view
					class="main-tab"
					:class="{ active: mainTab === 1 }"
					@click="onVideoTab"
				>
					<text>视频课</text>
					<view v-if="mainTab === 1" class="tab-underline"></view>
				</view>
			</view>
			<view class="search-icon-btn" @click="onGlobalSearch">
				<text>🔍</text>
			</view>
		</view>

		<!-- 筛选条 -->
		<view class="filter-row">
			<scroll-view class="filter-scroll" scroll-x="true" :show-scrollbar="false">
				<view
					class="filter-chip"
					:class="{ open: openFilter === 'subject' }"
					@click="toggleFilter('subject')"
				>
					<text class="chip-text">{{ subjectLabel }}</text>
					<text class="chip-arrow">{{ openFilter === 'subject' ? '▲' : '▼' }}</text>
				</view>
				<view
					class="filter-chip"
					:class="{ open: openFilter === 'category' }"
					@click="toggleFilter('category')"
				>
					<text class="chip-text">{{ categoryLabel }}</text>
					<text class="chip-arrow">{{ openFilter === 'category' ? '▲' : '▼' }}</text>
				</view>
				<view
					class="filter-chip uni-chip"
					:class="{ open: openFilter === 'university' }"
					@click="toggleFilter('university')"
				>
					<text class="chip-text">{{ universityChipLabel }}</text>
					<text class="chip-arrow">{{ openFilter === 'university' ? '▲' : '▼' }}</text>
				</view>
				<view
					class="filter-chip"
					:class="{ open: openFilter === 'sort' }"
					@click="toggleFilter('sort')"
				>
					<text class="chip-text">{{ sortLabel }}</text>
					<text class="chip-arrow">{{ openFilter === 'sort' ? '▲' : '▼' }}</text>
				</view>
				<view class="filter-chip filter-more" @click="onMoreFilter">
					<text class="chip-text">筛选</text>
					<text class="filter-more-icon">☰</text>
				</view>
			</scroll-view>
		</view>

		<!-- 辅导方式 -->
		<view class="method-row">
			<text class="method-title">辅导方式</text>
			<view class="method-tags">
				<view
					class="method-tag"
					:class="{ active: teachMethod === '' }"
					@click="teachMethod = ''"
				>
					<text>不限</text>
				</view>
				<view
					class="method-tag"
					:class="{ active: teachMethod === 'home' }"
					@click="teachMethod = 'home'"
				>
					<text>上门家教</text>
				</view>
				<view
					class="method-tag"
					:class="{ active: teachMethod === 'online' }"
					@click="teachMethod = 'online'"
				>
					<text>网络教学</text>
				</view>
			</view>
		</view>

		<!-- 遮罩 + 下拉（科目 / 老师分类 / 排序） -->
		<view v-if="openFilter && openFilter !== 'university'" class="mask" @click="closeFilter"></view>
		<view v-if="openFilter === 'subject'" class="dropdown-panel">
			<view
				v-for="(item, i) in subjectOptions"
				:key="'s' + i"
				class="dropdown-item"
				:class="{ selected: filters.subject === item.value }"
				@click="selectSubject(item)"
			>
				<text>{{ item.label }}</text>
				<text v-if="filters.subject === item.value" class="check">✓</text>
			</view>
		</view>
		<view v-if="openFilter === 'category'" class="dropdown-panel">
			<view
				v-for="(item, i) in categoryOptions"
				:key="'c' + i"
				class="dropdown-item"
				:class="{ selected: filters.category === item.value }"
				@click="selectCategory(item)"
			>
				<text>{{ item.label }}</text>
				<text v-if="filters.category === item.value" class="check">✓</text>
			</view>
		</view>
		<view v-if="openFilter === 'sort'" class="dropdown-panel">
			<view
				v-for="(item, i) in sortOptions"
				:key="'o' + i"
				class="dropdown-item"
				:class="{ selected: filters.sort === item.value }"
				@click="selectSort(item)"
			>
				<text>{{ item.label }}</text>
				<text v-if="filters.sort === item.value" class="check">✓</text>
			</view>
		</view>

		<!-- 高校分类：全屏偏上层面板 + 搜索（不铺全国列表） -->
		<view v-if="openFilter === 'university'" class="uni-mask" @click="closeFilter"></view>
		<view v-if="openFilter === 'university'" class="uni-panel" @click.stop>
			<view class="uni-panel-title">高校分类</view>
			<view class="uni-search-wrap">
				<text class="uni-search-icon">🔍</text>
				<input
					class="uni-search-input"
					type="text"
					v-model="universityInput"
					placeholder="输入校名，如：清华、北师大、华中科技"
					confirm-type="search"
					@confirm="applyUniversityKeyword"
				/>
			</view>
			<view class="uni-hint">
				<text>从下方联想中选择，或直接输入校名后点「按关键词筛选」。</text>
			</view>
			<scroll-view class="uni-list" scroll-y="true">
				<view
					class="uni-list-item"
					:class="{ selected: !universityApplied.keyword && !universityApplied.name }"
					@click="clearUniversity"
				>
					<text>全部</text>
					<text v-if="!universityApplied.keyword && !universityApplied.name" class="check">✓</text>
				</view>
				<view
					v-for="(name, idx) in filteredCatalog"
					:key="'u' + idx"
					class="uni-list-item"
					:class="{ selected: universityApplied.name === name && !universityApplied.keyword }"
					@click="pickUniversity(name)"
				>
					<text>{{ name }}</text>
					<text v-if="universityApplied.name === name && !universityApplied.keyword" class="check">✓</text>
				</view>
				<view v-if="filteredCatalog.length === 0 && universityInputTrim" class="uni-empty">
					<text>本地联想无匹配，仍可用关键词筛选教员毕业院校。</text>
				</view>
			</scroll-view>
			<view class="uni-actions">
				<button class="uni-btn ghost" @click="closeFilter">关闭</button>
				<button
					class="uni-btn primary"
					:disabled="!universityInputTrim"
					@click="applyUniversityKeyword"
				>
					按关键词筛选
				</button>
			</view>
		</view>

		<!-- 教员列表 -->
		<scroll-view class="teacher-list" scroll-y="true" :show-scrollbar="false">
			<view v-if="filteredTeachers.length === 0" class="list-empty">
				<text>暂无符合条件的教员</text>
			</view>
			<view v-for="t in filteredTeachers" :key="t.id" class="teacher-card">
				<image class="teacher-avatar" :src="t.avatar" mode="aspectFill" />
				<view class="teacher-body">
					<view class="teacher-title-line">
						<text class="teacher-name">{{ t.name }}</text>
						<text class="teacher-meta">（{{ t.gender }}） {{ t.teacherNo }}</text>
					</view>
					<view class="school-line">
						<text class="school-name">{{ t.school }}</text>
						<text class="major-text"> · {{ t.major }}</text>
					</view>
					<view class="tag-row">
						<text v-for="(tag, ti) in t.tags" :key="ti" class="mini-tag">{{ tag }}</text>
					</view>
					<text class="block-label">自我描述</text>
					<text class="desc-text">{{ t.desc }}</text>
					<text class="block-label">可教授科目</text>
					<text class="subjects-text">{{ t.subjects.join('、') }}</text>
					<text class="block-label">薪水要求</text>
					<text class="salary-text">{{ t.salary }}</text>
				</view>
			</view>
			<view class="list-bottom-space"></view>
		</scroll-view>

		<view class="bottom-tab-bar">
			<view
				v-for="(tab, index) in bottomTabs"
				:key="index"
				class="bottom-tab-item"
				:class="{ active: index === 0 }"
				@click="goBottomTab(index)"
			>
				<text class="tab-icon">{{ tab.icon }}</text>
				<text class="tab-name">{{ tab.name }}</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { UNIVERSITY_CATALOG } from '@/utils/university-catalog.js'

	const MOCK_TEACHERS = [
		{
			id: 1,
			name: '孙教员',
			gender: '女',
			teacherNo: 'T102938',
			school: '北京外国语大学',
			major: '英语专业',
			tags: ['金牌教员', '认证教员', '支持网络教学'],
			desc: '五年一线教学经验，擅长中高考英语提分。',
			subjects: ['小学英语', '初中英语', '高中英语'],
			salary: '面议 · 参考平台指导价',
			avatar: 'https://picsum.photos/120/120?random=81',
			category: 'student',
			subjectKeys: ['english'],
			teachOnline: true,
			teachHome: true
		},
		{
			id: 2,
			name: '李教员',
			gender: '男',
			teacherNo: 'T887766',
			school: '北京师范大学',
			major: '物理教育专业',
			tags: ['星级教员', '在职专业培训机构教师'],
			desc: '物理竞赛背景，可带奥赛与高考冲刺。',
			subjects: ['初中物理', '高中物理'],
			salary: '700-900 元/小时（视学段）',
			avatar: 'https://picsum.photos/120/120?random=82',
			category: 'pro',
			subjectKeys: ['physics'],
			teachOnline: true,
			teachHome: true
		},
		{
			id: 3,
			name: '王教员',
			gender: '女',
			teacherNo: 'T554433',
			school: '中央音乐学院',
			major: '钢琴表演',
			tags: ['海归教员', '支持网络教学'],
			desc: '旅欧学习经历，儿童启蒙与考级辅导。',
			subjects: ['钢琴', '乐理'],
			salary: '按课时协商',
			avatar: 'https://picsum.photos/120/120?random=83',
			category: 'returnee',
			subjectKeys: ['music'],
			teachOnline: true,
			teachHome: false
		},
		{
			id: 4,
			name: '赵教员',
			gender: '男',
			teacherNo: 'T221100',
			school: '华中科技大学',
			major: '数学与应用数学',
			tags: ['在校大学生', '认证教员'],
			desc: '数学系大三，可带小学初中奥数与同步。',
			subjects: ['小学数学', '初中数学'],
			salary: '120-200 元/小时',
			avatar: 'https://picsum.photos/120/120?random=84',
			category: 'student',
			subjectKeys: ['math'],
			teachOnline: false,
			teachHome: true
		}
	]

	export default {
		name: 'TutorPool',
		data() {
			return {
				cityName: '北京',
				mainTab: 0,
				openFilter: '',
				teachMethod: '',
				universityInput: '',
				universityApplied: {
					name: '',
					keyword: ''
				},
				filters: {
					subject: '',
					category: '',
					sort: 'default'
				},
				subjectOptions: [
					{ label: '全部', value: '' },
					{ label: '英语', value: 'english' },
					{ label: '数学', value: 'math' },
					{ label: '物理', value: 'physics' },
					{ label: '音乐', value: 'music' }
				],
				categoryOptions: [
					{ label: '全部', value: '' },
					{ label: '专业老师', value: 'pro' },
					{ label: '在校大学生', value: 'student' },
					{ label: '星级教员', value: 'star' },
					{ label: '外籍教员', value: 'foreign' },
					{ label: '海归教员', value: 'returnee' },
					{ label: '金牌教员', value: 'gold' }
				],
				sortOptions: [
					{ label: '距离最近', value: 'distance' },
					{ label: '综合排序', value: 'default' },
					{ label: '口碑排序', value: 'reputation' },
					{ label: '热度排序', value: 'hot' }
				],
				teachers: MOCK_TEACHERS,
				bottomTabs: [
					{ name: '首页', icon: '🏠' },
					{ name: '科目', icon: '📚' },
					{ name: '消息', icon: '💬' },
					{ name: '我的', icon: '👤' }
				]
			}
		},
		computed: {
			universityInputTrim() {
				return (this.universityInput || '').trim()
			},
			filteredCatalog() {
				const q = this.universityInputTrim
				if (!q) {
					return UNIVERSITY_CATALOG.slice(0, 24)
				}
				return UNIVERSITY_CATALOG.filter((name) => name.includes(q))
			},
			subjectLabel() {
				const f = this.subjectOptions.find((o) => o.value === this.filters.subject)
				return f ? f.label : '科目'
			},
			categoryLabel() {
				const f = this.categoryOptions.find((o) => o.value === this.filters.category)
				return f ? f.label : '老师分类'
			},
			sortLabel() {
				const f = this.sortOptions.find((o) => o.value === this.filters.sort)
				return f ? f.label : '综合排序'
			},
			universityChipLabel() {
				if (this.universityApplied.keyword) {
					const k = this.universityApplied.keyword
					return k.length > 6 ? k.slice(0, 6) + '…' : k
				}
				if (this.universityApplied.name) {
					const n = this.universityApplied.name
					return n.length > 6 ? n.slice(0, 6) + '…' : n
				}
				return '高校分类'
			},
			filteredTeachers() {
				let list = [...this.teachers]
				if (this.filters.subject) {
					list = list.filter((t) => t.subjectKeys.includes(this.filters.subject))
				}
				if (this.filters.category) {
					list = list.filter((t) => t.category === this.filters.category)
				}
				if (this.universityApplied.keyword) {
					const k = this.universityApplied.keyword
					list = list.filter((t) => t.school.includes(k))
				} else if (this.universityApplied.name) {
					const n = this.universityApplied.name
					list = list.filter((t) => t.school.includes(n))
				}
				if (this.teachMethod === 'online') {
					list = list.filter((t) => t.teachOnline)
				}
				if (this.teachMethod === 'home') {
					list = list.filter((t) => t.teachHome)
				}
				return list
			}
		},
		methods: {
			toggleFilter(key) {
				this.openFilter = this.openFilter === key ? '' : key
				if (key === 'university' && this.openFilter === 'university') {
					this.universityInput = this.universityApplied.keyword || this.universityApplied.name || ''
				}
			},
			closeFilter() {
				this.openFilter = ''
			},
			selectSubject(item) {
				this.filters.subject = item.value
				this.closeFilter()
			},
			selectCategory(item) {
				this.filters.category = item.value
				this.closeFilter()
			},
			selectSort(item) {
				this.filters.sort = item.value
				this.closeFilter()
			},
			pickUniversity(name) {
				this.universityApplied = { name, keyword: '' }
				this.universityInput = name
				this.closeFilter()
			},
			clearUniversity() {
				this.universityApplied = { name: '', keyword: '' }
				this.universityInput = ''
				this.closeFilter()
			},
			applyUniversityKeyword() {
				const k = this.universityInputTrim
				if (!k) return
				this.universityApplied = { name: '', keyword: k }
				this.closeFilter()
				uni.showToast({ title: '已按关键词筛选', icon: 'none' })
			},
			onCityTap() {
				uni.showToast({ title: '城市选择开发中', icon: 'none' })
			},
			onVideoTab() {
				this.mainTab = 1
				uni.showToast({ title: '视频课开发中', icon: 'none' })
			},
			onGlobalSearch() {
				uni.showToast({ title: '全局搜索开发中', icon: 'none' })
			},
			onMoreFilter() {
				uni.showToast({ title: '更多筛选开发中', icon: 'none' })
			},
			goBottomTab(index) {
				if (index === 0) {
					uni.reLaunch({ url: '/pages/index/index' })
					return
				}
				uni.showToast({ title: '开发中', icon: 'none' })
			}
		}
	}
</script>

<style scoped>
	.tutor-pool-page {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background: #f5f5f5;
		box-sizing: border-box;
	}

	.safe-top {
		padding-top: var(--status-bar-height, 44rpx);
	}

	.top-bar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 16rpx 24rpx;
		background: #fff;
		border-bottom: 1rpx solid #eee;
	}

	.city-row {
		display: flex;
		align-items: center;
		min-width: 120rpx;
	}

	.city-name {
		font-size: 28rpx;
		color: #333;
		font-weight: 600;
	}

	.city-arrow {
		font-size: 20rpx;
		color: #999;
		margin-left: 6rpx;
	}

	.main-tabs {
		display: flex;
		flex: 1;
		justify-content: center;
		gap: 48rpx;
	}

	.main-tab {
		position: relative;
		padding: 8rpx 0;
	}

	.main-tab text {
		font-size: 30rpx;
		color: #666;
	}

	.main-tab.active text {
		color: #ff6b00;
		font-weight: 700;
	}

	.tab-underline {
		position: absolute;
		left: 50%;
		bottom: 0;
		transform: translateX(-50%);
		width: 48rpx;
		height: 6rpx;
		background: #ff6b00;
		border-radius: 3rpx;
	}

	.search-icon-btn {
		padding: 8rpx 16rpx;
		font-size: 32rpx;
	}

	.filter-row {
		background: #fff;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.filter-scroll {
		white-space: nowrap;
		padding: 16rpx 12rpx;
	}

	.filter-chip {
		display: inline-flex;
		align-items: center;
		padding: 12rpx 20rpx;
		margin-right: 12rpx;
		background: #f7f7f7;
		border-radius: 8rpx;
	}

	.filter-chip.open {
		background: #fff5eb;
	}

	.filter-chip.open .chip-text {
		color: #ff6b00;
	}

	.chip-text {
		font-size: 24rpx;
		color: #333;
		max-width: 160rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.chip-arrow {
		font-size: 18rpx;
		color: #999;
		margin-left: 8rpx;
	}

	.filter-more-icon {
		margin-left: 8rpx;
		font-size: 24rpx;
		color: #666;
	}

	.method-row {
		display: flex;
		align-items: center;
		padding: 16rpx 24rpx;
		background: #fff;
		margin-bottom: 12rpx;
	}

	.method-title {
		font-size: 24rpx;
		color: #999;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.method-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
	}

	.method-tag {
		padding: 8rpx 20rpx;
		background: #f5f5f5;
		border-radius: 8rpx;
	}

	.method-tag text {
		font-size: 24rpx;
		color: #666;
	}

	.method-tag.active {
		background: #ff6b00;
	}

	.method-tag.active text {
		color: #fff;
	}

	.mask {
		position: fixed;
		left: 0;
		right: 0;
		top: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.35);
		z-index: 50;
	}

	.dropdown-panel {
		position: fixed;
		left: 0;
		right: 0;
		top: 220rpx;
		max-height: 60vh;
		background: #fff;
		z-index: 60;
		border-radius: 0 0 16rpx 16rpx;
		box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
		overflow-y: auto;
	}

	.dropdown-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 28rpx 32rpx;
		border-bottom: 1rpx solid #f0f0f0;
		font-size: 28rpx;
		color: #333;
	}

	.dropdown-item.selected {
		color: #ff6b00;
		background: #fff8f2;
	}

	.check {
		color: #ff6b00;
		font-weight: bold;
	}

	.uni-mask {
		position: fixed;
		left: 0;
		right: 0;
		top: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.4);
		z-index: 70;
	}

	.uni-panel {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		height: 72vh;
		background: #fff;
		border-radius: 24rpx 24rpx 0 0;
		z-index: 80;
		display: flex;
		flex-direction: column;
		padding: 24rpx 24rpx calc(24rpx + env(safe-area-inset-bottom));
		box-sizing: border-box;
	}

	.uni-panel-title {
		font-size: 32rpx;
		font-weight: 700;
		color: #333;
		text-align: center;
		margin-bottom: 20rpx;
	}

	.uni-search-wrap {
		display: flex;
		align-items: center;
		background: #f5f5f5;
		border-radius: 12rpx;
		padding: 16rpx 20rpx;
	}

	.uni-search-icon {
		font-size: 28rpx;
		margin-right: 12rpx;
	}

	.uni-search-input {
		flex: 1;
		font-size: 28rpx;
		height: 40rpx;
	}

	.uni-hint {
		margin-top: 12rpx;
		margin-bottom: 8rpx;
	}

	.uni-hint text {
		font-size: 22rpx;
		color: #999;
		line-height: 1.4;
	}

	.uni-list {
		flex: 1;
		min-height: 200rpx;
		margin-top: 8rpx;
	}

	.uni-list-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 24rpx 8rpx;
		border-bottom: 1rpx solid #f0f0f0;
		font-size: 28rpx;
		color: #333;
	}

	.uni-list-item.selected {
		background: #fff5eb;
		color: #ff6b00;
	}

	.uni-empty {
		padding: 32rpx 16rpx;
	}

	.uni-empty text {
		font-size: 24rpx;
		color: #999;
		line-height: 1.5;
	}

	.uni-actions {
		display: flex;
		gap: 20rpx;
		margin-top: 16rpx;
	}

	.uni-btn {
		flex: 1;
		font-size: 28rpx;
		border-radius: 12rpx;
		line-height: 2.2;
	}

	.uni-btn.ghost {
		background: #f0f0f0;
		color: #333;
	}

	.uni-btn.primary {
		background: #ff6b00;
		color: #fff;
	}

	.uni-btn.primary[disabled] {
		opacity: 0.45;
	}

	.teacher-list {
		flex: 1;
		padding: 0 16rpx;
		box-sizing: border-box;
	}

	.list-empty {
		padding: 80rpx;
		text-align: center;
	}

	.list-empty text {
		font-size: 28rpx;
		color: #999;
	}

	.teacher-card {
		display: flex;
		background: #fff;
		border-radius: 16rpx;
		padding: 24rpx;
		margin-bottom: 16rpx;
	}

	.teacher-avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 12rpx;
		flex-shrink: 0;
		margin-right: 20rpx;
		background: #eee;
	}

	.teacher-body {
		flex: 1;
		min-width: 0;
	}

	.teacher-title-line {
		margin-bottom: 8rpx;
	}

	.teacher-name {
		font-size: 30rpx;
		font-weight: 700;
		color: #333;
	}

	.teacher-meta {
		font-size: 24rpx;
		color: #666;
	}

	.school-line {
		margin-bottom: 12rpx;
	}

	.school-name {
		font-size: 26rpx;
		color: #2b6cff;
		font-weight: 600;
	}

	.major-text {
		font-size: 24rpx;
		color: #666;
	}

	.tag-row {
		display: flex;
		flex-wrap: wrap;
		gap: 8rpx;
		margin-bottom: 12rpx;
	}

	.mini-tag {
		font-size: 20rpx;
		color: #ff6b00;
		background: #fff5eb;
		padding: 4rpx 12rpx;
		border-radius: 6rpx;
	}

	.block-label {
		display: block;
		font-size: 22rpx;
		color: #999;
		margin-top: 12rpx;
		margin-bottom: 6rpx;
	}

	.desc-text,
	.subjects-text,
	.salary-text {
		font-size: 24rpx;
		color: #555;
		line-height: 1.5;
	}

	.list-bottom-space {
		height: 120rpx;
	}

	.bottom-tab-bar {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		display: flex;
		background: #fff;
		border-top: 1rpx solid #eee;
		padding-bottom: env(safe-area-inset-bottom);
		z-index: 40;
	}

	.bottom-tab-item {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 12rpx 0;
	}

	.bottom-tab-item.active .tab-name {
		color: #ff6b00;
	}

	.tab-icon {
		font-size: 36rpx;
		margin-bottom: 4rpx;
	}

	.tab-name {
		font-size: 22rpx;
		color: #999;
	}
</style>
