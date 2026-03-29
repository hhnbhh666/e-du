import { createRequire } from 'node:module'
import { fileURLToPath } from 'node:url'
import path from 'node:path'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const root = path.resolve(__dirname, '..')
const require = createRequire(path.join(root, 'package.json'))

try {
	const p = require.resolve('@dcloudio/uni-uts-v1')
	console.log('[ok] @dcloudio/uni-uts-v1 ->', p)
} catch (e) {
	console.error('[fail] 找不到 @dcloudio/uni-uts-v1，请在 wy-uni 目录执行 npm install')
	console.error(e)
	process.exit(1)
}

try {
	const p = require.resolve('@dcloudio/vite-plugin-uni')
	console.log('[ok] @dcloudio/vite-plugin-uni ->', p)
} catch (e) {
	console.error('[fail] 找不到 @dcloudio/vite-plugin-uni')
	console.error(e)
	process.exit(1)
}
