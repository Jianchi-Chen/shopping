/**
 * 地址管理 API
 */
export interface Address {
  id: string
  userId: string
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  addressDetail: string
  postalCode?: string
  isDefault: boolean
  createdAt?: string
  updatedAt?: string
}

export const useAddressApi = () => {
  const { request } = useApi()

  const getAddresses = async () => {
    return await request<Address[]>('/user/addresses')
  }

  const createAddress = async (data: Omit<Address, 'id' | 'userId' | 'createdAt' | 'updatedAt'>) => {
    return await request<Address>('/user/addresses', {
      method: 'POST',
      body: JSON.stringify(data),
    })
  }

  const updateAddress = async (id: string, data: Partial<Address>) => {
    return await request<Address>(`/user/addresses/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    })
  }

  const deleteAddress = async (id: string) => {
    return await request<void>(`/user/addresses/${id}`, {
      method: 'DELETE',
    })
  }

  const setDefaultAddress = async (id: string) => {
    return await request<Address>(`/user/addresses/${id}/default`, {
      method: 'POST',
    })
  }

  return {
    getAddresses,
    createAddress,
    updateAddress,
    deleteAddress,
    setDefaultAddress,
  }
}
