<template>
  <!-- 服务于前端：用户登录身份、订单与售后可用性 -->
  <div>
    <div class="n-layout-page-header">
      <n-card :bordered="false" title="用户管理">
        用于维护用户状态与登录权限（Mock 数据）。
      </n-card>
    </div>
    <n-card :bordered="false" class="mt-4 proCard">
      <div class="mb-3">
        <n-space>
          <n-input v-model:value="params.keyword" placeholder="姓名/手机号" clearable />
          <n-select
            v-model:value="params.status"
            :options="statusOptions"
            placeholder="用户状态"
            clearable
          />
          <n-button type="primary" @click="reloadTable">查询</n-button>
          <n-button @click="resetFilters">重置</n-button>
        </n-space>
      </div>
      <BasicTable
        :columns="columns"
        :request="loadDataTable"
        :row-key="(row: Customer) => row.id"
        ref="actionRef"
        :actionColumn="actionColumn"
      />
    </n-card>
  </div>
</template>

<script lang="ts" setup>
  // 服务于前端功能：用户登录、订单与售后功能权限
  import { h, reactive, ref, unref } from 'vue';
  import { NTag, useMessage } from 'naive-ui';
  import { BasicTable, TableAction, type BasicColumn } from '@/components/Table';
  import { getCustomerList, updateCustomerStatus } from '@/api/identity/users';
  import type { Customer, CustomerStatus } from '@/types/Customer';

  const message = useMessage();
  const actionRef = ref();

  const params = reactive<{ keyword: string; status: CustomerStatus | '' }>({
    keyword: '',
    status: '',
  });

  const statusOptions = [
    { label: '正常', value: 'active' },
    { label: '已禁用', value: 'banned' },
  ];

  const statusTextMap: Record<CustomerStatus, string> = {
    active: '正常',
    banned: '已禁用',
  };

  const columns: BasicColumn<Customer>[] = [
    { title: '用户ID', key: 'id', width: 120 },
    { title: '姓名', key: 'name', width: 140 },
    { title: '手机号', key: 'phone', width: 140 },
    {
      title: '状态',
      key: 'status',
      width: 120,
      render(record) {
        const type = record.status === 'active' ? 'success' : 'error';
        return h(
          NTag,
          { type },
          {
            default: () => statusTextMap[record.status],
          }
        );
      },
    },
    { title: '订单数', key: 'orderCount', width: 100 },
    {
      title: '累计消费',
      key: 'totalSpent',
      width: 120,
      render(record) {
        return `￥${record.totalSpent}`;
      },
    },
    { title: '注册时间', key: 'createdAt', width: 160 },
  ];

  const actionColumn = reactive({
    width: 180,
    title: '操作',
    key: 'action',
    fixed: 'right',
    render(record: Customer) {
      return h(TableAction, {
        style: 'button',
        actions: [
          {
            label: record.status === 'banned' ? '启用' : '禁用',
            onClick: () => updateStatus(record, record.status === 'banned' ? 'active' : 'banned'),
            auth: ['identity_user_update'],
          },
        ],
      });
    },
  });

  const loadDataTable = async (res: any) => {
    const merged = {
      ...unref(params),
      ...res,
      status: params.status || undefined,
    };
    return await getCustomerList(merged);
  };

  function reloadTable() {
    actionRef.value?.reload();
  }

  function resetFilters() {
    params.keyword = '';
    params.status = '';
    reloadTable();
  }

  async function updateStatus(record: Customer, status: CustomerStatus) {
    await updateCustomerStatus({ id: record.id, status });
    message.success('用户状态已更新');
    reloadTable();
  }
</script>
