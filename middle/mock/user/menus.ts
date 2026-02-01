import { defineMock } from '@alova/mock';
import { resultSuccess } from '../_util';

const menusList = [
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: 'LAYOUT',
    redirect: '/dashboard/console',
    meta: {
      icon: 'DashboardOutlined',
      title: 'Dashboard',
    },
    children: [
      {
        path: 'console',
        name: 'dashboard_console',
        component: '/dashboard/console/console',
        meta: {
          title: '主控台',
        },
      },
      {
        path: 'monitor',
        name: 'dashboard_monitor',
        component: '/dashboard/monitor/monitor',
        meta: {
          title: '监控页',
        },
      },
      {
        path: 'workplace',
        name: 'dashboard_workplace',
        component: '/dashboard/workplace/workplace',
        meta: {
          hidden: true,
          title: '工作台',
        },
      },
    ],
  },
  {
    path: '/commerce',
    name: 'Commerce',
    component: 'LAYOUT',
    redirect: '/commerce/product',
    meta: {
      icon: 'ShoppingOutlined',
      title: '商品与订单',
    },
    children: [
      {
        path: 'product',
        name: 'commerce_product',
        component: '/commerce/product/index',
        meta: {
          title: '商品管理',
        },
      },
      {
        path: 'order',
        name: 'commerce_order',
        component: '/commerce/order/index',
        meta: {
          title: '订单管理',
        },
      },
    ],
  },
  {
    path: '/identity',
    name: 'Identity',
    component: 'LAYOUT',
    redirect: '/identity/merchant',
    meta: {
      icon: 'TeamOutlined',
      title: '用户与商家',
    },
    children: [
      {
        path: 'merchant',
        name: 'identity_merchant',
        component: '/identity/merchant/index',
        meta: {
          title: '商家管理',
        },
      },
      {
        path: 'user',
        name: 'identity_user',
        component: '/identity/user/index',
        meta: {
          title: '用户管理',
        },
      },
    ],
  },
];

export default defineMock({
  '/api/menus': () => resultSuccess(menusList),
});
