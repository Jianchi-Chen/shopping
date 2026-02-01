export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  
  // SPA 模式
  ssr: false,
  
  // 启用 pages 目录
  features: {
    preloadComponentMetadata: true,
  },
  
  // PostCSS 配置 - Tailwind 会自动处理样式生成
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },
  
  // 模块配置
  modules: [
    '@pinia/nuxt',
  ],
  
  // Pinia 配置
  pinia: {
    storesDirs: ['./stores/'],
  },
  
  // 全局组件自动注册
  components: [
    { path: './components', prefix: '' },
  ],
})
