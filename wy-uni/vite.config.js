import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

// HBuilderX Vue3 真机/Android 编译会走 Vite；需安装 @dcloudio/vite-plugin-uni，
// 否则可能报 Cannot find module: @dcloudio/uni-uts-v1（插件链未从工程 node_modules 解析）
export default defineConfig({
	plugins: [uni()],
	build: {
		rollupOptions: {
			output: {
				// App 等目标用 IIFE 单包；若存在 import() 会拆 chunk，与 IIFE 冲突
				inlineDynamicImports: true
			}
		}
	}
})
