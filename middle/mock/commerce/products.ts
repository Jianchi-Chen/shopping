import { defineMock } from '@alova/mock';
import { faker } from '@faker-js/faker';
import dayjs from 'dayjs';
import { pagination, resultSuccess } from '../_util';

const categories = ['手机数码', '家用电器', '服饰鞋包', '食品生鲜', '美妆个护'];
const statusList = ['on_sale', 'off_sale', 'out_of_stock'] as const;
const shopNames = ['旗舰店A', '品牌店B', '精选店C'];

function buildProducts(total: number) {
  return Array.from({ length: total }).map((_, index) => {
    const price = faker.number.int({ min: 19, max: 3999 });
    const originalPrice = price + faker.number.int({ min: 5, max: 299 });
    const status = faker.helpers.arrayElement(statusList);
    const shopName = faker.helpers.arrayElement(shopNames);
    return {
      id: String(100000 + index),
      title: faker.commerce.productName(),
      sku: faker.string.alphanumeric(8).toUpperCase(),
      price,
      originalPrice,
      stock: faker.number.int({ min: 0, max: 999 }),
      status,
      category: faker.helpers.arrayElement(categories),
      shopId: faker.string.numeric(4),
      shopName,
      updatedAt: dayjs(faker.date.recent()).format('YYYY-MM-DD HH:mm'),
    };
  });
}

export default defineMock({
  '/api/commerce/products': ({ query }) => {
    const { page = 1, pageSize = 10, keyword, status } = query;
    const source = buildProducts(58);
    const filtered = source.filter((item) => {
      const hitKeyword = keyword ? item.title.includes(keyword) || item.id.includes(keyword) : true;
      const hitStatus = status ? item.status === status : true;
      return hitKeyword && hitStatus;
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
  '[POST]/api/commerce/products/status': ({ data }) => {
    return resultSuccess({
      ...data,
      updatedAt: dayjs().format('YYYY-MM-DD HH:mm'),
    });
  },
});
