/**
 * 后端商品数据结构（根据 Implemented-API.md）
 */
export interface BackendProduct {
  id: string
  title: string
  sku: string
  price: number
  originalPrice: number
  stock: number
  status: 'ON_SALE' | 'OFF_SALE' | 'OUT_OF_STOCK'
  category: string
  shopId: string
  shopName: string
  updatedAt: string
}

/**
 * 后端分页响应结构
 */
export interface BackendPaginatedResponse<T> {
  page: number
  pageSize: number
  pageCount: number
  itemCount: number
  list: T[]
}

/**
 * 商品查询参数
 */
export interface ProductQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  status?: 'ON_SALE' | 'OFF_SALE' | 'OUT_OF_STOCK'
  category?: string
  shopId?: string
}

/**
 * 商品 API
 * 对应后端接口：/api/commerce/products/*
 */
export const useProductApi = () => {
  const { request } = useApi()
  
  /**
   * 获取商品列表
   * GET /api/commerce/products
   */
  const getProducts = async (params: ProductQueryParams = {}) => {
    const queryString = new URLSearchParams(
      Object.entries(params)
        .filter(([_, v]) => v !== undefined && v !== null)
        .map(([k, v]) => [k, String(v)])
    ).toString()
    
    const endpoint = queryString 
      ? `/commerce/products?${queryString}` 
      : '/commerce/products'
    
    return await request<BackendPaginatedResponse<BackendProduct>>(endpoint)
  }
  
  /**
   * 更新商品状态
   * POST /api/commerce/products/status
   */
  const updateProductStatus = async (id: string, status: 'ON_SALE' | 'OFF_SALE') => {
    return await request<BackendProduct>('/commerce/products/status', {
      method: 'POST',
      body: JSON.stringify({ id, status }),
    })
  }

  /**
   * 获取商品详情
   * GET /api/commerce/products/{id}
   */
  const getProductDetail = async (id: string) => {
    return await request<BackendProduct>(`/commerce/products/${id}`)
  }
  
  return {
    getProducts,
    updateProductStatus,
    getProductDetail,
  }
}
