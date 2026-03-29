<template>
	<view class="page">
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>

		<!-- 短信登录 -->
		<view v-if="loginMode === 'sms'" class="sms-wrap">
			<view class="nav-row">
				<text class="close-btn" @click="goHome">×</text>
				<text class="switch-pwd" @click="loginMode = 'password'">密码登录</text>
			</view>
			<text class="sms-title">短信验证码登录</text>
			<text class="sms-sub">新用户登录自动创建账号</text>

			<view class="phone-box">
				<text class="cc">+86</text>
				<text class="cc-arrow">▼</text>
				<input
					class="phone-input"
					type="number"
					maxlength="11"
					v-model="phone"
					placeholder="请输入手机号"
				/>
				<text v-if="phone" class="clear-x" @click="phone = ''">×</text>
			</view>

			<view class="code-box">
				<input
					class="code-input"
					type="number"
					maxlength="6"
					v-model="smsCode"
					placeholder="请输入验证码"
				/>
			</view>

			<button
				class="btn-send"
				:disabled="smsCooldown > 0"
				@click="onTapSendSms"
			>
				{{ smsCooldown > 0 ? smsCooldown + ' 秒后重试' : '获取验证码' }}
			</button>

			<button class="btn-login" @click="handleSmsLogin">登 录</button>

			<view class="agree-row" @click="toggleAgree">
				<view :class="['agree-check', agreed ? 'on' : '']">
					<text v-if="agreed" class="agree-tick">✓</text>
				</view>
				<text class="agree-text">登录即表示同意《用户协议》和《隐私政策》</text>
			</view>
		</view>

		<!-- 密码登录 -->
		<view v-else class="pwd-wrap">
			<view class="nav-row">
				<text class="close-btn" @click="goHome">×</text>
				<text class="switch-pwd" @click="loginMode = 'sms'">短信登录</text>
			</view>
			<text class="sms-title">账号密码登录</text>
			<view class="phone-box single">
				<input
					class="phone-input"
					type="number"
					maxlength="11"
					v-model="form.phone"
					placeholder="请输入手机号"
				/>
			</view>
			<view class="phone-box single">
				<input
					class="phone-input"
					v-model="form.password"
					password
					maxlength="20"
					placeholder="请输入密码"
				/>
			</view>
			<button class="btn-login" @click="handlePwdLogin">登 录</button>
			<button class="btn-secondary" @click="handleRegister">注 册</button>
		</view>

		<!-- 滑动验证（可替换为极验等 SDK） -->
		<view v-if="captchaVisible" class="captcha-mask" @click="closeCaptcha">
			<view class="captcha-sheet" @click.stop>
				<text class="captcha-title">拖动滑块完成拼图</text>
				<view class="puzzle-mock">
					<text class="puzzle-tip">安全验证</text>
				</view>
				<view class="slide-row">
					<view id="captchaTrack" class="slide-track">
						<view class="slide-bg-text">向右滑动验证</view>
						<view
							id="captchaThumb"
							class="slide-thumb"
							:style="{ transform: 'translateX(' + thumbX + 'px)' }"
							@touchstart.stop.prevent="onThumbStart"
							@touchmove.stop.prevent="onThumbMove"
							@touchend.stop="onThumbEnd"
							@touchcancel.stop="onThumbEnd"
							@mousedown.stop.prevent="onThumbStart"
						>
							<text class="thumb-grip">|||</text>
						</view>
					</view>
				</view>
				<text v-if="!slideId" class="slide-loading">验证加载中，请稍候再拖</text>
				<view class="captcha-actions">
					<text class="cap-link" @click="resetThumb">刷新</text>
					<text class="cap-link" @click="closeCaptcha">关闭</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { authApi } from '@/api/index.js';

	export default {
		data() {
			return {
				statusBarHeight: 44,
				loginMode: 'sms',
				phone: '',
				smsCode: '',
				agreed: false,
				smsCooldown: 0,
				captchaVisible: false,
				slideId: '',
				thumbX: 0,
				thumbMaxX: 220,
				dragging: false,
				dragStartX: 0,
				thumbStartX: 0,
				touchStartTs: 0,
				cooldownTimer: null,
				form: {
					phone: '',
					password: ''
				}
			};
		},
		onLoad() {
			try {
				const sys = uni.getSystemInfoSync();
				this.statusBarHeight = sys.statusBarHeight || 44;
			} catch (e) {
				this.statusBarHeight = 44;
			}
		},
		onUnload() {
			if (this.cooldownTimer) {
				clearInterval(this.cooldownTimer);
				this.cooldownTimer = null;
			}
		},
		methods: {
			goHome() {
				uni.reLaunch({ url: '/pages/index/index' });
			},
			toggleAgree() {
				this.agreed = !this.agreed;
			},
			normalizePhone(p) {
				return String(p || '').replace(/\D/g, '');
			},
			onTapSendSms() {
				const p = this.normalizePhone(this.phone);
				if (p.length !== 11) {
					uni.showToast({ title: '请输入正确手机号', icon: 'none' });
					return;
				}
				if (!this.agreed) {
					uni.showToast({ title: '请先阅读并同意协议', icon: 'none' });
					return;
				}
				if (this.smsCooldown > 0) return;
				this.phone = p;
				this.openCaptcha();
			},
			openCaptcha() {
				this.captchaVisible = true;
				this.slideId = '';
				this.resetThumb();
				this.$nextTick(() => {
					this.measureTrack();
					setTimeout(() => this.measureTrack(), 150);
					authApi
						.slideStart()
						.then((res) => {
							this.slideId = res.slideId;
							this.$nextTick(() => this.measureTrack());
						})
						.catch(() => {
							this.captchaVisible = false;
							uni.showToast({
								title: '连不上后端，请启动 wy-spring 并确认端口 8080',
								icon: 'none',
								duration: 3500
							});
						});
				});
			},
			measureTrack() {
				const q = uni.createSelectorQuery().in(this);
				q.select('#captchaTrack').boundingClientRect();
				q.select('#captchaThumb').boundingClientRect();
				q.exec((res) => {
					const tr = res[0];
					const th = res[1];
					if (tr && th && tr.width > th.width) {
						this.thumbMaxX = tr.width - th.width;
					}
				});
			},
			closeCaptcha() {
				this.captchaVisible = false;
				this.slideId = '';
			},
			resetThumb() {
				this.thumbX = 0;
				this.dragging = false;
			},
			getClientX(e) {
				if (e.touches && e.touches.length > 0) {
					return e.touches[0].clientX;
				}
				if (e.changedTouches && e.changedTouches.length > 0) {
					return e.changedTouches[0].clientX;
				}
				if (typeof e.clientX === 'number') {
					return e.clientX;
				}
				return null;
			},
			onThumbStart(e) {
				const x = this.getClientX(e);
				if (x == null) return;
				this.dragging = true;
				this.dragStartX = x;
				this.thumbStartX = this.thumbX;
				this.touchStartTs = Date.now();
				// 浏览器 H5：鼠标拖出滑块后继续跟随（mousedown 后挂 document）
				if (typeof document !== 'undefined' && e.type === 'mousedown') {
					document.addEventListener('mousemove', this.onDocMouseMove);
					document.addEventListener('mouseup', this.onDocMouseUp);
				}
			},
			onDocMouseMove(e) {
				if (!this.dragging) return;
				const nx = this.thumbStartX + (e.clientX - this.dragStartX);
				this.applyThumbX(nx);
			},
			onDocMouseUp() {
				if (typeof document !== 'undefined') {
					document.removeEventListener('mousemove', this.onDocMouseMove);
					document.removeEventListener('mouseup', this.onDocMouseUp);
				}
				this.onThumbEnd();
			},
			applyThumbX(nx) {
				const max = this.thumbMaxX > 0 ? this.thumbMaxX : 220;
				this.thumbX = Math.max(0, Math.min(max, nx));
			},
			onThumbMove(e) {
				if (!this.dragging) return;
				const x = this.getClientX(e);
				if (x == null) return;
				this.applyThumbX(this.thumbStartX + (x - this.dragStartX));
			},
			onThumbEnd() {
				if (!this.dragging) return;
				this.dragging = false;
				if (typeof document !== 'undefined') {
					document.removeEventListener('mousemove', this.onDocMouseMove);
					document.removeEventListener('mouseup', this.onDocMouseUp);
				}
				const maxW = this.thumbMaxX > 0 ? this.thumbMaxX : 220;
				const progress = Math.round((this.thumbX / maxW) * 100);
				const durationMs = Date.now() - this.touchStartTs;
				if (progress < 88) {
					uni.showToast({ title: '请拖至最右侧', icon: 'none' });
					this.resetThumb();
					return;
				}
				if (!this.slideId) {
					uni.showToast({
						title: '安全验证未初始化（多为后端未启动或网络不通）',
						icon: 'none',
						duration: 3000
					});
					this.closeCaptcha();
					return;
				}
				this.submitSlideVerify(durationMs, progress);
			},
			async submitSlideVerify(durationMs, progress) {
				try {
					uni.showLoading({ title: '验证中' });
					const gate = await authApi.slideVerify({
						slideId: this.slideId,
						durationMs,
						progress
					});
					await authApi.sendSmsCode({
						phone: this.normalizePhone(this.phone),
						gateToken: gate.gateToken
					});
					uni.hideLoading();
					this.closeCaptcha();
					uni.showToast({ title: '验证码已发送', icon: 'none' });
					this.startCooldown(60);
				} catch (err) {
					uni.hideLoading();
					this.resetThumb();
					this.slideId = '';
					try {
						const r = await authApi.slideStart();
						this.slideId = r.slideId;
					} catch (e2) {
						this.closeCaptcha();
					}
				}
			},
			startCooldown(sec) {
				this.smsCooldown = sec;
				if (this.cooldownTimer) clearInterval(this.cooldownTimer);
				this.cooldownTimer = setInterval(() => {
					this.smsCooldown -= 1;
					if (this.smsCooldown <= 0) {
						clearInterval(this.cooldownTimer);
						this.cooldownTimer = null;
						this.smsCooldown = 0;
					}
				}, 1000);
			},
			async handleSmsLogin() {
				const p = this.normalizePhone(this.phone);
				if (p.length !== 11) {
					uni.showToast({ title: '请输入正确手机号', icon: 'none' });
					return;
				}
				if (!this.smsCode || this.smsCode.length < 4) {
					uni.showToast({ title: '请输入验证码', icon: 'none' });
					return;
				}
				if (!this.agreed) {
					uni.showToast({ title: '请先阅读并同意协议', icon: 'none' });
					return;
				}
				try {
					uni.showLoading({ title: '登录中' });
					const res = await authApi.smsLogin({
						phone: p,
						code: String(this.smsCode).trim()
					});
					uni.hideLoading();
					uni.setStorageSync('token', res.token);
					uni.showToast({ title: '登录成功', icon: 'success' });
					setTimeout(() => {
						uni.reLaunch({ url: '/pages/index/index' });
					}, 600);
				} catch (e) {
					uni.hideLoading();
				}
			},
			validatePwdForm() {
				const p = this.normalizePhone(this.form.phone);
				if (p.length !== 11) {
					uni.showToast({ title: '请输入正确手机号', icon: 'none' });
					return false;
				}
				if (!this.form.password || this.form.password.length < 6) {
					uni.showToast({ title: '密码至少6位', icon: 'none' });
					return false;
				}
				this.form.phone = p;
				return true;
			},
			async handlePwdLogin() {
				if (!this.validatePwdForm()) return;
				try {
					uni.showLoading({ title: '登录中' });
					const res = await authApi.login({
						phone: this.form.phone,
						password: this.form.password
					});
					uni.hideLoading();
					uni.setStorageSync('token', res.token);
					uni.showToast({ title: '登录成功', icon: 'success' });
					setTimeout(() => {
						uni.reLaunch({ url: '/pages/index/index' });
					}, 600);
				} catch (e) {
					uni.hideLoading();
				}
			},
			handleRegister() {
				if (!this.validatePwdForm()) return;
				uni.showModal({
					title: '注册',
					editable: true,
					placeholderText: '请输入昵称',
					success: async (res) => {
						if (!res.confirm || !res.content) return;
						try {
							uni.showLoading({ title: '注册中' });
							await authApi.register({
								phone: this.form.phone,
								password: this.form.password,
								nickname: res.content
							});
							uni.hideLoading();
							uni.showToast({ title: '注册成功，请登录', icon: 'success' });
						} catch (e) {
							uni.hideLoading();
						}
					}
				});
			}
		}
	};
</script>

<style>
	.page {
		min-height: 100vh;
		background: #f7f7f7;
	}

	.status-bar {
		width: 100%;
	}

	.nav-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 8rpx 8rpx 32rpx;
	}

	.close-btn {
		font-size: 56rpx;
		color: #999;
		line-height: 1;
		padding: 0 16rpx;
	}

	.switch-pwd {
		font-size: 28rpx;
		color: #666;
		padding: 8rpx 16rpx;
	}

	.sms-wrap,
	.pwd-wrap {
		padding: 0 48rpx 48rpx;
	}

	.sms-title {
		display: block;
		font-size: 44rpx;
		font-weight: 700;
		color: #222;
		margin-bottom: 16rpx;
	}

	.sms-sub {
		display: block;
		font-size: 26rpx;
		color: #999;
		margin-bottom: 48rpx;
	}

	.phone-box {
		display: flex;
		align-items: center;
		background: #fff;
		border-radius: 16rpx;
		padding: 24rpx 28rpx;
		margin-bottom: 24rpx;
		border: 1rpx solid #eee;
	}

	.phone-box.single {
		padding: 24rpx 28rpx;
	}

	.cc {
		font-size: 30rpx;
		color: #333;
		font-weight: 600;
		margin-right: 8rpx;
	}

	.cc-arrow {
		font-size: 20rpx;
		color: #999;
		margin-right: 20rpx;
	}

	.phone-input {
		flex: 1;
		font-size: 30rpx;
	}

	.clear-x {
		font-size: 36rpx;
		color: #ccc;
		padding: 0 8rpx;
	}

	.code-box {
		background: #fff;
		border-radius: 16rpx;
		padding: 24rpx 28rpx;
		margin-bottom: 32rpx;
		border: 1rpx solid #eee;
	}

	.code-input {
		font-size: 30rpx;
	}

	.btn-send {
		width: 100%;
		height: 96rpx;
		line-height: 96rpx;
		background: #e53935;
		color: #fff;
		font-size: 32rpx;
		border-radius: 48rpx;
		border: none;
		margin-bottom: 24rpx;
	}

	.btn-send[disabled] {
		opacity: 0.55;
	}

	.btn-login {
		width: 100%;
		height: 96rpx;
		line-height: 96rpx;
		background: #fff;
		color: #e53935;
		font-size: 32rpx;
		border-radius: 48rpx;
		border: 2rpx solid #e53935;
		margin-bottom: 24rpx;
	}

	.btn-secondary {
		width: 100%;
		height: 88rpx;
		line-height: 88rpx;
		background: #fff;
		color: #666;
		font-size: 28rpx;
		border-radius: 44rpx;
		border: 1rpx solid #ddd;
	}

	.agree-row {
		display: flex;
		align-items: flex-start;
		margin-top: 40rpx;
		padding-right: 16rpx;
	}

	.agree-check {
		width: 32rpx;
		height: 32rpx;
		border: 2rpx solid #ccc;
		border-radius: 6rpx;
		margin-right: 12rpx;
		margin-top: 4rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.agree-check.on {
		background: #e53935;
		border-color: #e53935;
	}

	.agree-tick {
		font-size: 22rpx;
		color: #fff;
		font-weight: bold;
	}

	.agree-text {
		font-size: 24rpx;
		color: #999;
		line-height: 1.5;
	}

	.captcha-mask {
		position: fixed;
		left: 0;
		right: 0;
		top: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 999;
		display: flex;
		align-items: flex-end;
		justify-content: center;
	}

	.captcha-sheet {
		width: 100%;
		background: #fff;
		border-radius: 24rpx 24rpx 0 0;
		padding: 32rpx 32rpx calc(32rpx + env(safe-area-inset-bottom));
		box-sizing: border-box;
	}

	.captcha-title {
		display: block;
		text-align: center;
		font-size: 28rpx;
		color: #666;
		margin-bottom: 24rpx;
	}

	.puzzle-mock {
		height: 160rpx;
		background: linear-gradient(135deg, #26a69a 0%, #00897b 100%);
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 32rpx;
	}

	.puzzle-tip {
		font-size: 28rpx;
		color: rgba(255, 255, 255, 0.9);
	}

	.slide-row {
		margin-bottom: 24rpx;
	}

	.slide-track {
		position: relative;
		height: 88rpx;
		background: #e0e0e0;
		border-radius: 44rpx;
		overflow: hidden;
		touch-action: none;
	}

	.slide-bg-text {
		position: absolute;
		left: 0;
		right: 0;
		text-align: center;
		line-height: 88rpx;
		font-size: 26rpx;
		color: #999;
	}

	.slide-thumb {
		position: absolute;
		left: 4rpx;
		top: 4rpx;
		width: 80rpx;
		height: 80rpx;
		background: #fff;
		border-radius: 40rpx;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 2;
		touch-action: none;
		user-select: none;
		cursor: grab;
	}

	.thumb-grip {
		font-size: 24rpx;
		color: #1976d2;
		font-weight: bold;
		letter-spacing: -4rpx;
	}

	.captcha-actions {
		display: flex;
		justify-content: space-between;
		padding: 0 8rpx;
	}

	.cap-link {
		font-size: 26rpx;
		color: #1976d2;
	}

	.slide-loading {
		display: block;
		text-align: center;
		font-size: 24rpx;
		color: #999;
		margin-top: 12rpx;
	}
</style>
