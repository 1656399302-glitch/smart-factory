/**
 * 主题切换工具
 * 支持 localStorage + prefers-color-scheme
 * 不改动业务逻辑，仅处理主题切换
 */

const THEME_STORAGE_KEY = 'app-theme'
const THEME_ATTRIBUTE = 'data-theme'

/**
 * 获取系统偏好主题
 */
function getSystemTheme() {
  if (typeof window === 'undefined') return 'light'
  
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    return 'dark'
  }
  return 'light'
}

/**
 * 获取存储的主题
 */
function getStoredTheme() {
  if (typeof window === 'undefined') return null
  
  try {
    return localStorage.getItem(THEME_STORAGE_KEY)
  } catch (e) {
    console.warn('无法访问 localStorage:', e)
    return null
  }
}

/**
 * 保存主题到 localStorage
 */
function setStoredTheme(theme) {
  if (typeof window === 'undefined') return
  
  try {
    localStorage.setItem(THEME_STORAGE_KEY, theme)
  } catch (e) {
    console.warn('无法保存到 localStorage:', e)
  }
}

/**
 * 应用主题到 DOM
 */
function applyTheme(theme) {
  if (typeof document === 'undefined') return
  
  const html = document.documentElement
  
  if (theme === 'dark') {
    html.setAttribute(THEME_ATTRIBUTE, 'dark')
    html.classList.add('dark')
  } else {
    html.removeAttribute(THEME_ATTRIBUTE)
    html.classList.remove('dark')
  }
}

/**
 * 初始化主题
 * 优先级：localStorage > 系统偏好 > light
 */
export function initTheme() {
  const storedTheme = getStoredTheme()
  const systemTheme = getSystemTheme()
  const theme = storedTheme || systemTheme
  
  applyTheme(theme)
  return theme
}

/**
 * 切换主题
 */
export function toggleTheme() {
  const currentTheme = getStoredTheme() || getSystemTheme()
  const newTheme = currentTheme === 'dark' ? 'light' : 'dark'
  
  setStoredTheme(newTheme)
  applyTheme(newTheme)
  
  return newTheme
}

/**
 * 设置主题
 */
export function setTheme(theme) {
  if (theme !== 'light' && theme !== 'dark') {
    console.warn(`无效的主题: ${theme}，使用 'light'`)
    theme = 'light'
  }
  
  setStoredTheme(theme)
  applyTheme(theme)
  
  return theme
}

/**
 * 获取当前主题
 */
export function getCurrentTheme() {
  if (typeof document === 'undefined') return 'light'
  
  const html = document.documentElement
  return html.getAttribute(THEME_ATTRIBUTE) === 'dark' || html.classList.contains('dark')
    ? 'dark'
    : 'light'
}

/**
 * 监听系统主题变化
 */
export function watchSystemTheme(callback) {
  if (typeof window === 'undefined' || !window.matchMedia) {
    return () => {}
  }
  
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  
  const handler = (e) => {
    // 只有在没有存储主题时才响应系统变化
    if (!getStoredTheme()) {
      const theme = e.matches ? 'dark' : 'light'
      applyTheme(theme)
      if (callback) callback(theme)
    }
  }
  
  // 兼容性处理
  if (mediaQuery.addEventListener) {
    mediaQuery.addEventListener('change', handler)
    return () => mediaQuery.removeEventListener('change', handler)
  } else if (mediaQuery.addListener) {
    mediaQuery.addListener(handler)
    return () => mediaQuery.removeListener(handler)
  }
  
  return () => {}
}

