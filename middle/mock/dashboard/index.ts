import { defineMock } from '@alova/mock';
import { resultSuccess } from '../_util';

export default defineMock(
  {
    // Dashboard 统计数据
    '/api/dashboard/statistics': () => {
      return resultSuccess({
        visits: 1234,
        totalVisits: 56789,
        sales: 12345,
        totalSales: 678901,
        orders: 234,
        totalOrders: 5678,
        revenue: 23456,
        totalRevenue: 789012,
      });
    },

    // Dashboard 待办列表
    '/api/dashboard/todos': () => {
      return resultSuccess([
        {
          id: 1,
          title: '处理待发货订单',
          status: 'PENDING',
          createTime: '2026-02-04 09:00',
        },
        {
          id: 2,
          title: '审核新上架商品',
          status: 'PENDING',
          createTime: '2026-02-04 10:00',
        },
        {
          id: 3,
          title: '回复客户咨询',
          status: 'COMPLETED',
          createTime: '2026-02-03 15:00',
        },
      ]);
    },
  },
  true
);
