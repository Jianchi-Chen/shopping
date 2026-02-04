/**
 * API 基础配置
 */
export const useApi = () => {
  const config = useRuntimeConfig()
  
  // API 基础 URL
  const baseURL = 'http://localhost:8080/api'
  
  // 获取存储的 token
  const getToken = (): string | null => {
    if (typeof window === 'undefined') return null
    return localStorage.getItem('token')
  }
  
  // 设置 token
  const setToken = (token: string) => {
    if (typeof window !== 'undefined') {
      localStorage.setItem('token', token)
    }
  }
  
  // 清除 token
  const clearToken = () => {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('token')
    }
  }
  
  // 统一的请求方法
  const request = async <T = any>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> => {
    const token = getToken()
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
      ...options.headers,
    }
    
    // 如果有 token，添加到请求头
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    
    const url = `${baseURL}${endpoint}`
    const method = options.method || 'GET'
    const safeHeaders: Record<string, string> = {}
    Object.entries(headers as Record<string, string>).forEach(([key, value]) => {
      safeHeaders[key] = key.toLowerCase() === 'authorization' ? 'Bearer ***' : value
    })
    
    console.log('[API Request]', {
      method,
      url,
      headers: safeHeaders,
      body: options.body,
    })

    try {
      const response = await fetch(url, {
        ...options,
        headers,
      })
      
      const data = await response.json()
      console.log('[API Response]', {
        method,
        url,
        status: response.status,
        ok: response.ok,
        data,
      })
      
      // 检查业务状态码
      if (data.code !== 200) {
        throw new Error(data.message || '请求失败')
      }
      
      return data.result as T
    } catch (error) {
      console.error('API Request Error:', error)
      throw error
    }
  }
  
  return {
    baseURL,
    getToken,
    setToken,
    clearToken,
    request,
  }
}
