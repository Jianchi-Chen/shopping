/**
 * 认证 API
 * 对应后端接口：/api/auth/*
 */
export const useAuth = () => {
  const { request, setToken, clearToken } = useApi()
  
  /**
   * 用户登录
   * POST /api/auth/login
   */
  const login = async (username: string, password: string) => {
    const result = await request<{ token: string }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    })
    
    // 保存 token
    setToken(result.token)
    return result
  }
  
  /**
   * 用户注册
   * POST /api/auth/register
   */
  const register = async (username: string, password: string, role: string = 'USER') => {
    const result = await request<{ token: string }>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, role }),
    })
    
    // 保存 token
    setToken(result.token)
    return result
  }
  
  /**
   * 登出
   * 清除本地 token
   */
  const logout = () => {
    clearToken()
  }

  /**
   * 获取当前用户信息
   * GET /api/auth/me
   */
  const getCurrentUser = async () => {
    return await request('/auth/me')
  }

  /**
   * 更新当前用户信息
   * PUT /api/auth/me
   */
  const updateProfile = async (name?: string, phone?: string, avatar?: string) => {
    const body: any = {}
    if (name !== undefined) body.name = name
    if (phone !== undefined) body.phone = phone
    if (avatar !== undefined) body.avatar = avatar
    
    return await request('/auth/me', {
      method: 'PUT',
      body: JSON.stringify(body),
    })
  }

  /**
   * 修改密码
   * POST /api/auth/change-password
   */
  const changePassword = async (oldPassword: string, newPassword: string) => {
    return await request('/auth/change-password', {
      method: 'POST',
      body: JSON.stringify({ oldPassword, newPassword }),
    })
  }

  /**
   * 上传头像
   * POST /api/user/upload/avatar
   */
  const uploadAvatar = async (file: File) => {
    const formData = new FormData()
    formData.append('file', file)

    const { baseURL, getToken } = useApi()
    const token = getToken()
    const headers: HeadersInit = {}
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }

    const response = await fetch(`${baseURL}/user/upload/avatar`, {
      method: 'POST',
      headers,
      body: formData,
    })

    const data = await response.json()
    if (data.code !== 200) {
      throw new Error(data.message || '上传失败')
    }

    return data.result.url
  }
  
  return {
    login,
    register,
    logout,
    getCurrentUser,
    updateProfile,
    changePassword,
    uploadAvatar,
  }
}
