export type OrderStatus =
  | 'pending_payment'
  | 'pending_shipment'
  | 'shipped'
  | 'completed'
  | 'closed'
  | 'after_sale';

export type PayStatus = 'unpaid' | 'paid' | 'refunded';

export type RefundStatus = 'none' | 'requested' | 'approved' | 'rejected' | 'refunded';

export interface OrderItem {
  productId: string;
  title: string;
  quantity: number;
  price: number;
}

export interface Order {
  id: string;
  orderNo: string;
  status: OrderStatus;
  payStatus: PayStatus;
  refundStatus: RefundStatus;
  totalAmount: number;
  itemCount: number;
  buyerName: string;
  shopId: string;
  shopName: string;
  createdAt: string;
  items: OrderItem[];
}
