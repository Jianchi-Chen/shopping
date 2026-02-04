/**
 * 后端订单数据结构（根据 Implemented-API.md）
 */
export interface BackendOrderItem {
  productId: string
  title: string
  quantity: number
  price: number
}

export interface BackendOrder {
  id: string
  orderNo: string
  status: 'PENDING_PAYMENT' | 'PENDING_SHIPMENT' | 'SHIPPED' | 'COMPLETED' | 'CLOSED' | 'AFTER_SALE'
  payStatus: 'UNPAID' | 'PAID' | 'REFUNDED'
  refundStatus: 'NONE' | 'REQUESTED' | 'APPROVED' | 'REJECTED' | 'REFUNDED'
  totalAmount: number
  itemCount: number
  buyerName: string
  shopId: string
  shopName: string
  createdAt: string
  items: BackendOrderItem[]
}

/**
 * 订单查询参数
 */
export interface OrderQueryParams {
  page?: number
  pageSize?: number
  orderNo?: string
  status?: 'PENDING_PAYMENT' | 'PENDING_SHIPMENT' | 'SHIPPED' | 'COMPLETED' | 'CLOSED' | 'AFTER_SALE'
  payStatus?: 'UNPAID' | 'PAID' | 'REFUNDED'
  refundStatus?: 'NONE' | 'REQUESTED' | 'APPROVED' | 'REJECTED' | 'REFUNDED'
  shopId?: string
  buyerId?: string
}

/**
 * 订单 API
 * 对应后端接口：/api/commerce/orders/*
 */
export const useOrderApi = () => {
  const { request } = useApi()
  
  /**
   * 获取订单列表
   * GET /api/commerce/orders
   */
  const getOrders = async (params: OrderQueryParams = {}) => {
    const queryString = new URLSearchParams(
      Object.entries(params)
        .filter(([_, v]) => v !== undefined && v !== null)
        .map(([k, v]) => [k, String(v)])
    ).toString()
    
    const endpoint = queryString 
      ? `/commerce/orders?${queryString}` 
      : '/commerce/orders'
    
    return await request<BackendPaginatedResponse<BackendOrder>>(endpoint)
  }
  
  /**
   * 更新订单状态
   * POST /api/commerce/orders/status
   */
  const updateOrderStatus = async (
    id: string, 
    status: 'PENDING_PAYMENT' | 'PENDING_SHIPMENT' | 'SHIPPED' | 'COMPLETED' | 'CLOSED' | 'AFTER_SALE'
  ) => {
    return await request<BackendOrder>('/commerce/orders/status', {
      method: 'POST',
      body: JSON.stringify({ id, status }),
    })
  }
  
  /**
   * 更新售后状态
   * POST /api/commerce/orders/refund
   */
  const updateRefundStatus = async (
    id: string,
    refundStatus: 'NONE' | 'REQUESTED' | 'APPROVED' | 'REJECTED' | 'REFUNDED'
  ) => {
    return await request<BackendOrder>('/commerce/orders/refund', {
      method: 'POST',
      body: JSON.stringify({ id, refundStatus }),
    })
  }

  /**
   * 创建订单
   * POST /api/commerce/orders
   */
  const createOrder = async (orderData: any) => {
    return await request('/commerce/orders', {
      method: 'POST',
      body: JSON.stringify(orderData),
    })
  }

  /**
   * 获取当前用户订单列表
   * GET /api/commerce/orders/my
   */
  const getMyOrders = async (params: OrderQueryParams = {}) => {
    const queryString = new URLSearchParams(
      Object.entries(params)
        .filter(([_, v]) => v !== undefined && v !== null)
        .map(([k, v]) => [k, String(v)])
    ).toString()
    
    const endpoint = queryString 
      ? `/commerce/orders/my?${queryString}` 
      : '/commerce/orders/my'
    
    return await request<BackendPaginatedResponse<BackendOrder>>(endpoint)
  }

  /**
   * 获取当前用户订单列表（新接口）
   * GET /api/user/orders
   */
  const getUserOrders = async (params: OrderQueryParams = {}) => {
    const queryString = new URLSearchParams(
      Object.entries(params)
        .filter(([_, v]) => v !== undefined && v !== null)
        .map(([k, v]) => [k, String(v)])
    ).toString()
    
    const endpoint = queryString 
      ? `/user/orders?${queryString}` 
      : '/user/orders'
    
    return await request<BackendPaginatedResponse<BackendOrder>>(endpoint)
  }

  /**
   * 获取订单详情
   * GET /api/user/orders/{id}
   */
  const getUserOrderDetail = async (id: string) => {
    return await request<BackendOrder>(`/user/orders/${id}`)
  }
  
  return {
    getOrders,
    updateOrderStatus,
    updateRefundStatus,
    createOrder,
    getMyOrders,
    getUserOrders,
    getUserOrderDetail,
  }
}
