import { Alova } from '@/utils/http/alova/index';

/**
 * 获取统计数据
 */
export function getStatistics(params?: any) {
  return Alova.Get('/dashboard/statistics', { params });
}

/**
 * 获取待办列表
 */
export function getTodoList(params?: any) {
  return Alova.Get('/dashboard/todos', { params });
}
