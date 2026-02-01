import { defineMock } from '@alova/mock';
import { faker } from '@faker-js/faker';
import dayjs from 'dayjs';
import { pagination, resultSuccess } from '../_util';

const statusList = [
  'pending_payment',
  'pending_shipment',
  'shipped',
  'completed',
  'closed',
  'after_sale',
] as const;
const refundStatusList = ['none', 'requested', 'approved', 'rejected', 'refunded'] as const;
const shopNames = ['旗舰店A', '品牌店B', '精选店C'];

function buildOrders(total: number) {
  return Array.from({ length: total }).map((_, index) => {
    const status = faker.helpers.arrayElement(statusList);
    const payStatus = status === 'pending_payment' ? 'unpaid' : 'paid';
    const refundStatus =
      status === 'after_sale' ? faker.helpers.arrayElement(refundStatusList) : 'none';
    const itemCount = faker.number.int({ min: 1, max: 5 });
    const totalAmount = faker.number.int({ min: 59, max: 4999 });
    const shopName = faker.helpers.arrayElement(shopNames);
    return {
      id: String(200000 + index),
      orderNo: `NO${faker.string.numeric(12)}`,
      status,
      payStatus,
      refundStatus,
      totalAmount,
      itemCount,
      buyerName: faker.person.fullName(),
      shopId: faker.string.numeric(4),
      shopName,
      createdAt: dayjs(faker.date.recent()).format('YYYY-MM-DD HH:mm'),
      items: Array.from({ length: itemCount }).map(() => ({
        productId: faker.string.numeric(6),
        title: faker.commerce.productName(),
        quantity: faker.number.int({ min: 1, max: 3 }),
        price: faker.number.int({ min: 29, max: 1999 }),
      })),
    };
  });
}

export default defineMock({
  '/api/commerce/orders': ({ query }) => {
    const { page = 1, pageSize = 10, orderNo, status, payStatus } = query;
    const source = buildOrders(64);
    const filtered = source.filter((item) => {
      const hitOrderNo = orderNo ? item.orderNo.includes(orderNo) : true;
      const hitStatus = status ? item.status === status : true;
      const hitPayStatus = payStatus ? item.payStatus === payStatus : true;
      return hitOrderNo && hitStatus && hitPayStatus;
    });

    const list = pagination(Number(page), Number(pageSize), filtered);
    const itemCount = filtered.length;
    const pageCount = Math.ceil(itemCount / Number(pageSize));

    return resultSuccess({
      page: Number(page),
      pageSize: Number(pageSize),
      pageCount,
      itemCount,
      list,
    });
  },
  '[POST]/api/commerce/orders/status': ({ data }) => {
    return resultSuccess({
      ...data,
      updatedAt: dayjs().format('YYYY-MM-DD HH:mm'),
    });
  },
  '[POST]/api/commerce/orders/refund': ({ data }) => {
    return resultSuccess({
      ...data,
      updatedAt: dayjs().format('YYYY-MM-DD HH:mm'),
    });
  },
});
