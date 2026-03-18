<template>
	<view class="login-container">
		<view class="login-header">
			<text class="app-name">网易云课堂</text>
			<text class="app-slogan">刷题学习，轻松考证</text>
		</view>
		
		<view class="login-form">
			<view class="input-group">
				<text class="input-label">手机号</text>
				<input 
					class="input-field"
					v-model="form.phone"
					placeholder="请输入手机号"
					maxlength="11"
					type="number"
				/>
			</view>
			
			<view class="input-group">
				<text class="input-label">密码</text>
				<input 
					class="input-field"
					v-model="form.password"
					placeholder="请输入密码"
					password
					maxlength="20"
				/>
			</view>
			
			<view class="button-group">
				<button class="login-btn" @click="handleLogin">登录</button>
				<button class="register-btn" @click="handleRegister">注册</button>
			</view>
		</view>
		
		<view class="login-tips">
			<text class="tips-text">首次使用请先注册账号</text>
		</view>
	</view>
</template>

<script>
	import { authApi } from '@/api/index.js';
	
	export default {
		data() {
			return {
				form: {
					phone: '',
					password: ''
				}
			}
		},
		methods: {
			// 登录
			async handleLogin() {
				if (!this.validateForm()) return;
				
				try {
					uni.showLoading({ title: '登录中...' });
					const res = await authApi.login({
						phone: this.form.phone,
						password: this.form.password
					});
					uni.hideLoading();
					
					// 保存token
					uni.setStorageSync('token', res.token);
					
					uni.showToast({
						title: '登录成功',
						icon: 'success'
					});
					
					// 跳转到首页
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/index/index'
						});
					}, 1500);
				} catch (e) {
					uni.hideLoading();
				}
			},
			
			// 注册
			async handleRegister() {
				if (!this.validateForm()) return;
				
				// 输入昵称
				uni.showModal({
					title: '注册',
					content: '',
					editable: true,
					placeholderText: '请输入昵称',
					success: async (res) => {
						if (res.confirm && res.content) {
							try {
								uni.showLoading({ title: '注册中...' });
								await authApi.register({
									phone: this.form.phone,
									password: this.form.password,
									nickname: res.content
								});
								uni.hideLoading();
								
								uni.showToast({
									title: '注册成功，请登录',
									icon: 'success'
								});
							} catch (e) {
								uni.hideLoading();
							}
						}
					}
				});
			},
			
			// 表单验证
			validateForm() {
				if (!this.form.phone || this.form.phone.length !== 11) {
					uni.showToast({
						title: '请输入正确的手机号',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.form.password || this.form.password.length < 6) {
					uni.showToast({
						title: '密码至少6位',
						icon: 'none'
					});
					return false;
				}
				
				return true;
			}
		}
	}
</script>

<style>
	.login-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		min-height: 100vh;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		padding: 100rpx 50rpx;
	}
	
	.login-header {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 80rpx;
	}
	
	.app-name {
		font-size: 56rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 20rpx;
	}
	
	.app-slogan {
		font-size: 32rpx;
		color: rgba(255,255,255,0.8);
	}
	
	.login-form {
		width: 100%;
		background: #fff;
		border-radius: 20rpx;
		padding: 60rpx 40rpx;
	}
	
	.input-group {
		margin-bottom: 40rpx;
	}
	
	.input-label {
		display: block;
		font-size: 28rpx;
		color: #333;
		margin-bottom: 16rpx;
	}
	
	.input-field {
		width: 100%;
		height: 80rpx;
		background: #f5f5f5;
		border-radius: 12rpx;
		padding: 0 24rpx;
		font-size: 30rpx;
	}
	
	.button-group {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
		margin-top: 40rpx;
	}
	
	.login-btn {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: #fff;
		font-size: 32rpx;
		border-radius: 44rpx;
		border: none;
	}
	
	.register-btn {
		width: 100%;
		height: 88rpx;
		background: #fff;
		color: #667eea;
		font-size: 32rpx;
		border-radius: 44rpx;
		border: 2rpx solid #667eea;
	}
	
	.login-tips {
		margin-top: 40rpx;
	}
	
	.tips-text {
		font-size: 26rpx;
		color: rgba(255,255,255,0.7);
	}
</style>
