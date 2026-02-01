export type MerchantStatus = 'pending' | 'active' | 'rejected' | 'suspended';

export interface Merchant {
  id: string;
  shopId: string;
  shopName: string;
  ownerName: string;
  contactPhone: string;
  status: MerchantStatus;
  createdAt: string;
}
