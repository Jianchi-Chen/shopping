export type CustomerStatus = 'active' | 'banned';

export interface Customer {
  id: string;
  name: string;
  phone: string;
  status: CustomerStatus;
  orderCount: number;
  totalSpent: number;
  createdAt: string;
}
