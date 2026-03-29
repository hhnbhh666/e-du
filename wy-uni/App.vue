<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/* 每个页面公共 css；小程序 WXSS 不支持 * 选择器，改用常用组件 */
	page,
	view,
	text,
	image,
	button,
	input,
	textarea,
	scroll-view,
	swiper,
	swiper-item,
	navigator,
	form,
	label {
		box-sizing: border-box;
	}

	page {
		margin: 0;
		padding: 0;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
		background-color: #f5f5f5;
	}

	view,
	text,
	image,
	button,
	input,
	textarea {
		margin: 0;
		padding: 0;
	}

	/* 隐藏滚动条（仅 H5；小程序 WXSS 不支持 ::-webkit-scrollbar） */
	/* #ifdef H5 */
	::-webkit-scrollbar {
		display: none;
	}
	/* #endif */

	/* 安全区域适配 */
	.safe-area-bottom {
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);
	}
</style>
