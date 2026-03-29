/**
 * 获取设备地理位置（uni.getLocation 封装）
 *
 * 注意：
 * - App：需在 manifest 配置定位权限与说明；首次会出系统授权框。
 * - 微信小程序：需在 manifest 的 mp-weixin 声明 scope.userLocation、requiredPrivateInfos。
 * - H5：依赖浏览器定位，需 HTTPS（localhost 除外）。
 *
 * 【重要】HBuilderX 自定义虚拟经纬度、部分模拟器上若使用 gcj02，内部会做坐标转换，
 * 容易报 getLocation:fail translate coordinate… 此时请用 wgs84（本工具默认先尝试 wgs84）。
 * 国内地图（高德/腾讯）叠点若需 gcj02，请在服务端或用地图 SDK 做坐标转换。
 */

/**
 * @param {Object} [options]
 * @param {boolean} [options.showConfirm=true] 是否先弹出说明框
 * @param {string} [options.confirmTitle]
 * @param {string} [options.confirmContent]
 * @param {string} [options.type] 指定只使用某一种：'wgs84' | 'gcj02'；不传则先 wgs84 再 gcj02（提高模拟器成功率）
 * @returns {Promise<{latitude:number, longitude:number, coordType?:string, ...}>}
 */
export function getUserLocation(options = {}) {
	const {
		showConfirm = true,
		confirmTitle = '提示',
		confirmContent = '应用需要访问您的位置以提供更好的服务，例如推荐附近课程与服务。',
		type: userType
	} = options

	const requestOnce = (type) =>
		new Promise((resolve, reject) => {
			uni.getLocation({
				type,
				isHighAccuracy: true,
				highAccuracyExpireTime: 5000,
				success: (res) => {
					resolve({
						latitude: res.latitude,
						longitude: res.longitude,
						speed: res.speed,
						accuracy: res.accuracy,
						altitude: res.altitude,
						verticalAccuracy: res.verticalAccuracy,
						horizontalAccuracy: res.horizontalAccuracy,
						coordType: type
					})
				},
				fail: (err) => {
					const msg = err.errMsg || err.message || '获取位置失败'
					reject(Object.assign(new Error(msg), { errMsg: msg, raw: err, typeTried: type }))
				}
			})
		})

	const requestWithFallback = async () => {
		if (userType) {
			try {
				return await requestOnce(userType)
			} catch (e) {
				const msg = ((e && e.errMsg) || '').toLowerCase()
				const translateFail =
					msg.includes('translate') ||
					msg.includes('coord') ||
					msg.includes('convert')
				// 仅 gcj02 做坐标转换失败时（常见于 HBuilderX 虚拟定位 / 模拟器），再试 WGS84
				if (userType === 'gcj02' && translateFail) {
					return await requestOnce('wgs84')
				}
				throw e
			}
		}
		// 未指定类型：优先 wgs84（与模拟器虚拟经纬度、浏览器一致），失败再试 gcj02
		try {
			return await requestOnce('wgs84')
		} catch (e1) {
			return await requestOnce('gcj02')
		}
	}

	if (!showConfirm) {
		return requestWithFallback()
	}

	return new Promise((resolve, reject) => {
		uni.showModal({
			title: confirmTitle,
			content: confirmContent,
			confirmText: '确定',
			cancelText: '取消',
			success: (r) => {
				if (r.confirm) {
					requestWithFallback().then(resolve).catch(reject)
				} else {
					reject(Object.assign(new Error('用户取消'), { code: 'CANCEL' }))
				}
			},
			fail: () => reject(new Error('弹窗失败'))
		})
	})
}

/**
 * 将坐标存本地（可选，便于其它页读取）
 */
export function saveLocationCache(latitude, longitude) {
	try {
		uni.setStorageSync('user_lat', String(latitude))
		uni.setStorageSync('user_lng', String(longitude))
	} catch (e) {
		console.warn('saveLocationCache', e)
	}
}

export function readLocationCache() {
	try {
		const lat = uni.getStorageSync('user_lat')
		const lng = uni.getStorageSync('user_lng')
		if (lat && lng) {
			return { latitude: parseFloat(lat), longitude: parseFloat(lng) }
		}
	} catch (e) {}
	return null
}
