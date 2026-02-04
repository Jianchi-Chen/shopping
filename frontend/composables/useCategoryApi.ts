/**
 * 类目数据结构
 */
export interface Category {
  id: string
  name: string
  icon?: string
  productCount?: number
  children?: Category[]
}

/**
 * 类目 API
 * 对应后端接口：/api/commerce/categories
 */
export const useCategoryApi = () => {
  const { request } = useApi()
  
  /**
   * 获取类目列表
   * GET /api/commerce/categories
   */
  const getCategories = async () => {
    return await request<Category[]>('/commerce/categories')
  }
  
  return {
    getCategories,
  }
}
