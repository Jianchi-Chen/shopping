import { defineMock } from '@alova/mock';
import { faker } from '@faker-js/faker';
import dayjs from 'dayjs';
import { pagination, resultSuccess } from '../_util';

const statusList = ['pending', 'active', 'rejected', 'suspended'] as const;

function buildMerchants(total: number) {
  return Array.from({ length: total }).map((_, index) => {
    const status = faker.helpers.arrayElement(statusList);
    return {
      id: String(300000 + index),
      shopId: faker.string.numeric(6),
      shopName: `${faker.company.name()}店` ,
      ownerName: faker.person.fullName(),
      contactPhone: faker.phone.number('1##########'),
      status,
      createdAt: dayjs(faker.date.past()).format('YYYY-MM-DD HH:mm'),
    };
  });
}

export default defineMock({
  '/api/identity/merchants': ({ query }) => {
    const { page = 1, pageSize = 10, keyword, status } = query;
    const source = buildMerchants(48);
    const filtered = source.filter((item) => {
      const hitKeyword = keyword
        ? item.shopName.includes(keyword) || item.ownerName.includes(keyword)
        : true;
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
  '[POST]/api/identity/merchants/status': ({ body }) => {
    return resultSuccess({
      ...body,
      updatedAt: dayjs().format('YYYY-MM-DD HH:mm'),
    });
  },
});
