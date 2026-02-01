<template>
  <!-- 服务于前端：商品列表展示、价格/库存/状态信息呈现 -->
  <div>
    <div class="n-layout-page-header">
      <n-card :bordered="false" title="商品管理">
        用于维护前端商品展示所需的价格、库存与上下架状态（Mock 数据）。
      </n-card>
    </div>
    <n-card :bordered="false" class="mt-4 proCard">
      <div class="mb-3">
        <n-space>
          <n-input v-model:value="params.keyword" placeholder="商品名称/ID" clearable />
          <n-select
            v-model:value="params.status"
            :options="statusOptions"
            placeholder="商品状态"
            clearable
          />
          <n-button type="primary" @click="reloadTable">查询</n-button>
          <n-button @click="resetFilters">重置</n-button>
        </n-space>
      </div>
      <BasicTable
        :columns="columns"
        :request="loadDataTable"
        :row-key="(row: Product) => row.id"
        ref="actionRef"
        :actionColumn="actionColumn"
      >
        <template #tableTitle>
          <n-button type="primary" disabled>新增商品</n-button>
        </template>
      </BasicTable>
    </n-card>
  </div>
</template>

<script lang="ts" setup>
  // 服务于前端功能：商品列表展示、价格/库存/状态管理
  import { h, reactive, ref, unref } from 'vue';
  import { NTag, useMessage } from 'naive-ui';
  import { BasicTable, TableAction, type BasicColumn } from '@/components/Table';
  import { getProductList, updateProductStatus } from '@/api/commerce/products';
  import type { Product, ProductStatus } from '@/types/Product';

  const message = useMessage();
  const actionRef = ref();

  const params = reactive<{ keyword: string; status: ProductStatus | '' }>({
    keyword: '',
    status: '',
  });

  const statusOptions = [
    { label: '上架中', value: 'on_sale' },
    { label: '已下架', value: 'off_sale' },
    { label: '售罄', value: 'out_of_stock' },
  ];

  const statusTextMap: Record<ProductStatus, string> = {
    on_sale: '上架中',
    off_sale: '已下架',
    out_of_stock: '售罄',
  };

  const columns: BasicColumn<Product>[] = [
    { title: '商品ID', key: 'id', width: 120 },
    { title: '商品名称', key: 'title', minWidth: 220 },
    { title: 'SKU', key: 'sku', width: 120 },
    {
      title: '价格',
      key: 'price',
      width: 120,
      render(record) {
        return `￥${record.price}`;
      },
    },
    {
      title: '划线价',
      key: 'originalPrice',
      width: 120,
      render(record) {
        return `￥${record.originalPrice}`;
      },
    },
    { title: '库存', key: 'stock', width: 100 },
    {
      title: '状态',
      key: 'status',
      width: 120,
      render(record) {
        const type =
          record.status === 'on_sale'
            ? 'success'
            : record.status === 'off_sale'
            ? 'warning'
            : 'error';
        return h(
          NTag,
          { type },
          {
            default: () => statusTextMap[record.status],
          }
        );
      },
    },
    { title: '类目', key: 'category', width: 140 },
    { title: '所属店铺', key: 'shopName', width: 140 },
    { title: '更新时间', key: 'updatedAt', width: 160 },
  ];

  const actionColumn = reactive({
    width: 200,
    title: '操作',
    key: 'action',
    fixed: 'right',
    render(record: Product) {
      const isOnSale = record.status === 'on_sale';
      return h(TableAction, {
        style: 'button',
        actions: [
          {
            label: isOnSale ? '下架' : '上架',
            onClick: () => toggleStatus(record),
            auth: ['commerce_product_update'],
          },
          {
            label: '改价',
            onClick: () => mockEditPrice(record),
            auth: ['commerce_product_update'],
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
    return await getProductList(merged);
  };

  function reloadTable() {
    actionRef.value?.reload();
  }

  function resetFilters() {
    params.keyword = '';
    params.status = '';
    reloadTable();
  }

  async function toggleStatus(record: Product) {
    const nextStatus: ProductStatus = record.status === 'on_sale' ? 'off_sale' : 'on_sale';
    await updateProductStatus({ id: record.id, status: nextStatus });
    message.success('商品状态已更新');
    reloadTable();
  }

  function mockEditPrice(record: Product) {
    message.info(`待对接改价流程：${record.title}`);
  }
</script>
