<template>
	<view class="map-page">
		<!--
			全端统一用 <map>：微信小程序走腾讯；H5 使用 manifest 里 h5.sdkConfigs.maps 的 Key（已配腾讯）；
			App 真机需在 manifest 勾选地图并配置腾讯，且通常要自定义调试基座/云打包（标准基座多为高德）。
			注意：用浏览器打开的 localhost:5173 是 H5，不是安装包 App。
		-->
		<map
			id="fullMap"
			class="native-map"
			:style="nativeMapStyle"
			:latitude="lat"
			:longitude="lng"
			:scale="scale"
			:circles="circles"
			:show-location="true"
			enable-scroll
			enable-zoom
			show-compass
		/>
		<view class="map-actions safe-bottom">
			<button class="action-btn primary" type="primary" size="mini" @click="relocateMe">定位到当前</button>
			<button class="action-btn" size="mini" @click="goBack">返回</button>
		</view>
	</view>
</template>

<script>
	import { readLocationCache, getUserLocation, saveLocationCache } from '@/utils/location.js'

	export default {
		data() {
			return {
				lat: 39.9042,
				lng: 116.4074,
				scale: 15,
				circles: [],
				mapHeightPx: 0
			}
		},
		computed: {
			nativeMapStyle() {
				if (this.mapHeightPx <= 0) {
					return { width: '100%', height: '500px' }
				}
				return { width: '100%', height: this.mapHeightPx + 'px' }
			}
		},
		onLoad(options) {
			if (options.lat && options.lng) {
				const la = parseFloat(options.lat)
				const lo = parseFloat(options.lng)
				if (!isNaN(la) && !isNaN(lo)) {
					this.lat = la
					this.lng = lo
				}
			} else {
				const c = readLocationCache()
				if (c && c.latitude && c.longitude) {
					this.lat = c.latitude
					this.lng = c.longitude
				}
			}
			this.refreshCircles()
		},
		onReady() {
			this.calcMapHeight()
		},
		methods: {
			calcMapHeight() {
				try {
					const sys = uni.getSystemInfoSync()
					const winH = sys.windowHeight || sys.screenHeight || 667
					const safeBottom = (sys.safeAreaInsets && sys.safeAreaInsets.bottom) || 0
					const actionBar = 120
					this.mapHeightPx = Math.max(280, Math.floor(winH - actionBar - safeBottom))
				} catch (e) {
					this.mapHeightPx = 520
				}
			},
			refreshCircles() {
				this.circles = [
					{
						latitude: this.lat,
						longitude: this.lng,
						radius: 120,
						strokeWidth: 2,
						color: '#1890FF22',
						strokeColor: '#1890FF'
					}
				]
			},
			async relocateMe() {
				try {
					uni.showLoading({ title: '定位中...' })
					const loc = await getUserLocation({ showConfirm: false })
					this.lat = loc.latitude
					this.lng = loc.longitude
					saveLocationCache(loc.latitude, loc.longitude)
					this.refreshCircles()
					uni.showToast({ title: '已更新位置', icon: 'success' })
				} catch (e) {
					uni.showToast({
						title: (e && e.errMsg) ? String(e.errMsg).slice(0, 36) : '定位失败',
						icon: 'none'
					})
				} finally {
					uni.hideLoading()
				}
			},
			goBack() {
				uni.navigateBack({
					fail: () => {
						uni.reLaunch({ url: '/pages/index/index' })
					}
				})
			}
		}
	}
</script>

<style scoped>
	.map-page {
		display: flex;
		flex-direction: column;
		min-height: 100vh;
		width: 100%;
		background-color: #dfe3e6;
		box-sizing: border-box;
	}

	.native-map {
		flex-shrink: 0;
		width: 100%;
	}

	.map-actions {
		flex-shrink: 0;
		display: flex;
		gap: 20rpx;
		padding: 20rpx 30rpx;
		padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
		background: #fff;
		border-top: 1rpx solid #eee;
		box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.action-btn {
		flex: 1;
	}

	.action-btn.primary {
		flex: 1.2;
	}
</style>
