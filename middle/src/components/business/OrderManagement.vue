<template>
  <!-- 服务于前端：订单列表与状态/售后进度展示 -->
  <div>
    <div class="n-layout-page-header">
      <n-card :bordered="false" title="订单管理">
        用于维护前端订单状态流转与售后进度展示（Mock 数据）。
      </n-card>
    </div>
    <n-card :bordered="false" class="mt-4 proCard">
      <div class="mb-3">
        <n-space>
          <n-input v-model:value="params.orderNo" placeholder="订单号" clearable />
          <n-select
            v-model:value="params.status"
            :options="statusOptions"
            placeholder="订单状态"
            clearable
          />
          <n-select
            v-model:value="params.payStatus"
            :options="payStatusOptions"
            placeholder="支付状态"
            clearable
          />
          <n-button type="primary" @click="reloadTable">查询</n-button>
          <n-button @click="resetFilters">重置</n-button>
        </n-space>
      </div>
      <BasicTable
        :columns="columns"
        :request="loadDataTable"
        :row-key="(row: Order) => row.id"
        ref="actionRef"
        :actionColumn="actionColumn"
      />
    </n-card>
  </div>
</template>

<script lang="ts" setup>
  // 服务于前端功能：订单列表、订单状态与售后进度展示
  import { h, reactive, ref, unref } from 'vue';
  import { NTag, useMessage } from 'naive-ui';
  import { BasicTable, TableAction, type BasicColumn } from '@/components/Table';
  import { getOrderList, updateOrderStatus, updateRefundStatus } from '@/api/commerce/orders';
  import type { Order, OrderStatus, PayStatus, RefundStatus } from '@/types/Order';

  const message = useMessage();
  const actionRef = ref();

  const params = reactive<{ orderNo: string; status: OrderStatus | ''; payStatus: PayStatus | '' }>(
    {
      orderNo: '',
      status: '',
      payStatus: '',
    }
  );

  const statusOptions = [
    { label: '待支付', value: 'pending_payment' },
    { label: '待发货', value: 'pending_shipment' },
    { label: '已发货', value: 'shipped' },
    { label: '已完成', value: 'completed' },
    { label: '已关闭', value: 'closed' },
    { label: '售后中', value: 'after_sale' },
  ];

  const payStatusOptions = [
    { label: '未支付', value: 'unpaid' },
    { label: '已支付', value: 'paid' },
    { label: '已退款', value: 'refunded' },
  ];

  const statusTextMap: Record<OrderStatus, string> = {
    pending_payment: '待支付',
    pending_shipment: '待发货',
    shipped: '已发货',
    completed: '已完成',
    closed: '已关闭',
    after_sale: '售后中',
  };

  const payStatusTextMap: Record<PayStatus, string> = {
    unpaid: '未支付',
    paid: '已支付',
    refunded: '已退款',
  };

  const refundStatusTextMap: Record<RefundStatus, string> = {
    none: '无售后',
    requested: '已申请',
    approved: '已同意',
    rejected: '已拒绝',
    refunded: '已退款',
  };

  const columns: BasicColumn<Order>[] = [
    { title: '订单号', key: 'orderNo', width: 180 },
    { title: '买家', key: 'buyerName', width: 140 },
    { title: '件数', key: 'itemCount', width: 80 },
    {
      title: '订单金额',
      key: 'totalAmount',
      width: 120,
      render(record) {
        return `￥${record.totalAmount}`;
      },
    },
    {
      title: '支付状态',
      key: 'payStatus',
      width: 120,
      render(record) {
        const type = record.payStatus === 'paid' ? 'success' : 'warning';
        return h(
          NTag,
          { type },
          {
            default: () => payStatusTextMap[record.payStatus],
          }
        );
      },
    },
    {
      title: '订单状态',
      key: 'status',
      width: 120,
      render(record) {
        const type =
          record.status === 'completed'
            ? 'success'
            : record.status === 'closed'
            ? 'error'
            : record.status === 'pending_shipment'
            ? 'warning'
            : 'info';
        return h(
          NTag,
          { type },
          {
            default: () => statusTextMap[record.status],
          }
        );
      },
    },
    {
      title: '售后状态',
      key: 'refundStatus',
      width: 120,
      render(record) {
        const type = record.refundStatus === 'requested' ? 'warning' : 'default';
        return h(
          NTag,
          { type },
          {
            default: () => refundStatusTextMap[record.refundStatus],
          }
        );
      },
    },
    { title: '店铺', key: 'shopName', width: 140 },
    { title: '下单时间', key: 'createdAt', width: 160 },
  ];

  const actionColumn = reactive({
    width: 220,
    title: '操作',
    key: 'action',
    fixed: 'right',
    render(record: Order) {
      return h(TableAction, {
        style: 'button',
        actions: [
          {
            label: '发货',
            onClick: () => handleShip(record),
            ifShow: () => record.status === 'pending_shipment',
            auth: ['commerce_order_update'],
          },
          {
            label: '同意退款',
            onClick: () => handleRefund(record),
            ifShow: () => record.refundStatus === 'requested',
            auth: ['commerce_order_update'],
          },
          {
            label: '查看',
            onClick: () => handleView(record),
            auth: ['commerce_order_list'],
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
      payStatus: params.payStatus || undefined,
    };
    return await getOrderList(merged);
  };

  function reloadTable() {
    actionRef.value?.reload();
  }

  function resetFilters() {
    params.orderNo = '';
    params.status = '';
    params.payStatus = '';
    reloadTable();
  }

  async function handleShip(record: Order) {
    await updateOrderStatus({ id: record.id, status: 'shipped' });
    message.success('订单已发货');
    reloadTable();
  }

  async function handleRefund(record: Order) {
    await updateRefundStatus({ id: record.id, refundStatus: 'approved' });
    message.success('已同意退款');
    reloadTable();
  }

  function handleView(record: Order) {
    message.info(`查看订单：${record.orderNo}`);
  }
</script>
