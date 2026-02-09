import { RouteRecordRaw } from 'vue-router';
import { Layout } from '@/router/constant';
import { TeamOutlined } from '@vicons/antd';
import { renderIcon } from '@/utils/index';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/identity',
    name: 'Identity',
    redirect: '/identity/merchant',
    component: Layout,
    meta: {
      title: '用户与商家',
      icon: renderIcon(TeamOutlined),
      sort: 2,
      permissions: ['identity_merchant_list', 'identity_user_list'],
      roles: ['ADMIN'], // 仅 ADMIN 可见
    },
    children: [
      {
        path: 'merchant',
        name: 'identity_merchant',
        meta: {
          title: '商家管理',
          permissions: ['identity_merchant_list'],
        },
        component: () => import('@/views/identity/merchant/index.vue'),
      },
      {
        path: 'user',
        name: 'identity_user',
        meta: {
          title: '用户管理',
          permissions: ['identity_user_list'],
        },
        component: () => import('@/views/identity/user/index.vue'),
      },
    ],
  },
];

export default routes;
