/**
 * 文件上传 API
 * 对应后端接口：/api/upload
 */
export const useUploadApi = () => {
  const { getToken } = useApi()
  
  /**
   * 上传文件
   * POST /api/upload
   */
  const uploadFile = async (file: File) => {
    const token = getToken()
    const formData = new FormData()
    formData.append('file', file)
    
    try {
      const response = await fetch('http://localhost:8080/api/upload', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
        body: formData,
      })
      
      const data = await response.json()
      
      if (data.code !== 200) {
        throw new Error(data.message || '上传失败')
      }
      
      return data.result
    } catch (error) {
      console.error('File Upload Error:', error)
      throw error
    }
  }
  
  return {
    uploadFile,
  }
}
