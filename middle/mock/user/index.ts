import Mock from 'mockjs';
import { resultSuccess } from '../_util';
import { defineMock } from '@alova/mock';

const Random = Mock.Random;

const token = Random.string('upper', 32, 32);

const adminInfo = {
  userId: '1',
  username: 'admin',
  realName: 'Admin',
  avatar: Random.image(),
  desc: 'manager',
  password: Random.string('upper', 4, 16),
  token,
  permissions: [
    {
      label: '主控台',
      value: 'dashboard_console',
    },
    {
      label: '监控页',
      value: 'dashboard_monitor',
    },
    {
      label: '工作台',
      value: 'dashboard_workplace',
    },
    {
      label: '基础列表',
      value: 'basic_list',
    },
    {
      label: '基础列表删除',
      value: 'basic_list_delete',
    },
    {
      label: '商品列表',
      value: 'commerce_product_list',
    },
    {
      label: '商品维护',
      value: 'commerce_product_update',
    },
    {
      label: '订单列表',
      value: 'commerce_order_list',
    },
    {
      label: '订单处理',
      value: 'commerce_order_update',
    },
    {
      label: '商家列表',
      value: 'identity_merchant_list',
    },
    {
      label: '商家管理',
      value: 'identity_merchant_update',
    },
    {
      label: '用户列表',
      value: 'identity_user_list',
    },
    {
      label: '用户管理',
      value: 'identity_user_update',
    },
  ],
};

export default defineMock({
  '[POST]/api/login': () => resultSuccess({ token }),
  '/api/admin_info': () => resultSuccess(adminInfo),
});
