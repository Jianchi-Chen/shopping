export type ProductStatus = 'on_sale' | 'off_sale' | 'out_of_stock';

export interface Product {
  id: string;
  title: string;
  sku: string;
  price: number;
  originalPrice: number;
  stock: number;
  status: ProductStatus;
  category: string;
  shopId: string;
  shopName: string;
  updatedAt: string;
}
