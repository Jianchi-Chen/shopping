import { defineMock } from '@alova/mock';
import { faker } from '@faker-js/faker';
import dayjs from 'dayjs';
import { pagination, resultSuccess } from '../_util';

const statusList = ['active', 'banned'] as const;

function buildUsers(total: number) {
  return Array.from({ length: total }).map((_, index) => {
    const status = faker.helpers.arrayElement(statusList);
    return {
      id: String(400000 + index),
      name: faker.person.fullName(),
      phone: faker.phone.number('1##########'),
      status,
      orderCount: faker.number.int({ min: 0, max: 68 }),
      totalSpent: faker.number.int({ min: 0, max: 99999 }),
      createdAt: dayjs(faker.date.past()).format('YYYY-MM-DD HH:mm'),
    };
  });
}

export default defineMock({
  '/api/identity/users': ({ query }) => {
    const { page = 1, pageSize = 10, keyword, status } = query;
    const source = buildUsers(66);
    const filtered = source.filter((item) => {
      const hitKeyword = keyword
        ? item.name.includes(keyword) || item.phone.includes(keyword)
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
  '[POST]/api/identity/users/status': ({ body }) => {
    return resultSuccess({
      ...body,
      updatedAt: dayjs().format('YYYY-MM-DD HH:mm'),
    });
  },
});
