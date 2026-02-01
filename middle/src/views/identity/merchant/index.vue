<template>
  <!-- 服务于前端：商家身份/资质状态决定商品是否可展示与交易 -->
  <div>
    <div class="n-layout-page-header">
      <n-card :bordered="false" title="商家管理">
        用于维护商家资质与店铺状态（Mock 数据）。
      </n-card>
    </div>
    <n-card :bordered="false" class="mt-4 proCard">
      <div class="mb-3">
        <n-space>
          <n-input v-model:value="params.keyword" placeholder="店铺/负责人" clearable />
          <n-select
            v-model:value="params.status"
            :options="statusOptions"
            placeholder="商家状态"
            clearable
          />
          <n-button type="primary" @click="reloadTable">查询</n-button>
          <n-button @click="resetFilters">重置</n-button>
        </n-space>
      </div>
      <BasicTable
        :columns="columns"
        :request="loadDataTable"
        :row-key="(row: Merchant) => row.id"
        ref="actionRef"
        :actionColumn="actionColumn"
      />
    </n-card>
  </div>
</template>

<script lang="ts" setup>
  // 服务于前端功能：商家身份、店铺状态、商品上架资格
  import { h, reactive, ref, unref } from 'vue';
  import { NTag, useMessage } from 'naive-ui';
  import { BasicTable, TableAction, type BasicColumn } from '@/components/Table';
  import { getMerchantList, updateMerchantStatus } from '@/api/identity/merchants';
  import type { Merchant, MerchantStatus } from '@/types/Merchant';

  const message = useMessage();
  const actionRef = ref();

  const params = reactive<{ keyword: string; status: MerchantStatus | '' }>({
    keyword: '',
    status: '',
  });

  const statusOptions = [
    { label: '待审核', value: 'pending' },
    { label: '已通过', value: 'active' },
    { label: '已拒绝', value: 'rejected' },
    { label: '已停用', value: 'suspended' },
  ];

  const statusTextMap: Record<MerchantStatus, string> = {
    pending: '待审核',
    active: '已通过',
    rejected: '已拒绝',
    suspended: '已停用',
  };

  const columns: BasicColumn<Merchant>[] = [
    { title: '店铺ID', key: 'shopId', width: 120 },
    { title: '店铺名称', key: 'shopName', minWidth: 200 },
    { title: '负责人', key: 'ownerName', width: 140 },
    { title: '联系方式', key: 'contactPhone', width: 140 },
    {
      title: '状态',
      key: 'status',
      width: 120,
      render(record) {
        const type =
          record.status === 'active'
            ? 'success'
            : record.status === 'pending'
            ? 'warning'
            : record.status === 'rejected'
            ? 'error'
            : 'default';
        return h(
          NTag,
          { type },
          {
            default: () => statusTextMap[record.status],
          }
        );
      },
    },
    { title: '创建时间', key: 'createdAt', width: 160 },
  ];

  const actionColumn = reactive({
    width: 220,
    title: '操作',
    key: 'action',
    fixed: 'right',
    render(record: Merchant) {
      return h(TableAction, {
        style: 'button',
        actions: [
          {
            label: '通过',
            onClick: () => updateStatus(record, 'active'),
            ifShow: () => record.status === 'pending',
            auth: ['identity_merchant_update'],
          },
          {
            label: '拒绝',
            onClick: () => updateStatus(record, 'rejected'),
            ifShow: () => record.status === 'pending',
            auth: ['identity_merchant_update'],
          },
          {
            label: record.status === 'suspended' ? '启用' : '停用',
            onClick: () =>
              updateStatus(record, record.status === 'suspended' ? 'active' : 'suspended'),
            ifShow: () => record.status === 'active' || record.status === 'suspended',
            auth: ['identity_merchant_update'],
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
    return await getMerchantList(merged);
  };

  function reloadTable() {
    actionRef.value?.reload();
  }

  function resetFilters() {
    params.keyword = '';
    params.status = '';
    reloadTable();
  }

  async function updateStatus(record: Merchant, status: MerchantStatus) {
    await updateMerchantStatus({ id: record.id, status });
    message.success('商家状态已更新');
    reloadTable();
  }
</script>
