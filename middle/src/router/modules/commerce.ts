import { RouteRecordRaw } from 'vue-router';
import { Layout } from '@/router/constant';
import { ShoppingOutlined } from '@vicons/antd';
import { renderIcon } from '@/utils/index';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/commerce',
    name: 'Commerce',
    redirect: '/commerce/product',
    component: Layout,
    meta: {
      title: '商品与订单',
      icon: renderIcon(ShoppingOutlined),
      sort: 3,
      permissions: ['commerce_product_list', 'commerce_order_list'],
    },
    children: [
      {
        path: 'product',
        name: 'commerce_product',
        meta: {
          title: '商品管理',
          permissions: ['commerce_product_list'],
        },
        component: () => import('@/views/commerce/product/index.vue'),
      },
      {
        path: 'order',
        name: 'commerce_order',
        meta: {
          title: '订单管理',
          permissions: ['commerce_order_list'],
        },
        component: () => import('@/views/commerce/order/index.vue'),
      },
    ],
  },
];

export default routes;
