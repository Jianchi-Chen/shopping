<template>
  <n-card :bordered="false" class="proCard">
    <n-space vertical>
      <n-space justify="space-between">
        <n-space>
          <n-input
            v-model:value="queryParams.orderNo"
            placeholder="订单号"
            clearable
            @keyup.enter="handleSearch"
          />
          <n-select
            v-model:value="queryParams.status"
            :options="statusOptions"
            placeholder="订单状态"
            clearable
            style="width: 150px"
            @update:value="handleSearch"
          />
          <n-button type="primary" @click="handleSearch">搜索</n-button>
        </n-space>
      </n-space>

      <n-data-table
        :columns="columns"
        :data="dataList"
        :loading="loading"
        :pagination="pagination"
        :row-key="(row: any) => row.id"
        @update:page="handlePageChange"
      />
    </n-space>

    <!-- 订单状态修改弹窗 -->
    <n-modal v-model:show="showStatusModal">
      <n-card
        style="width: 400px"
        title="修改订单状态"
        :bordered="false"
        size="small"
        role="dialog"
        aria-modal="true"
      >
        <n-form>
          <n-form-item label="订单状态">
            <n-select v-model:value="editStatus" :options="statusOptions" />
          </n-form-item>
        </n-form>
        <template #footer>
          <n-space justify="end">
            <n-button @click="showStatusModal = false">取消</n-button>
            <n-button type="primary" @click="handleUpdateStatus">确定</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>
  </n-card>
</template>

<script lang="ts" setup>
  import { h, ref, onMounted, reactive } from 'vue';
  import { NButton, NSpace, NTag } from 'naive-ui';
  import { getOrderList, updateOrderStatus } from '@/api/commerce/order';
  import { useUserStore } from '@/store/modules/user';
  import { formatOrderId } from '@/utils/idFormatter';

  const userStore = useUserStore();
  const loading = ref(false);
  const dataList = ref<any[]>([]);
  const showStatusModal = ref(false);
  const editOrderId = ref<string | null>(null);
  const editStatus = ref('');

  const queryParams = reactive({
    orderNo: '',
    status: null,
    page: 1,
    pageSize: 10,
  });

  const pagination = reactive({
    page: 1,
    pageSize: 10,
    pageCount: 1,
    itemCount: 0,
    showSizePicker: true,
    pageSizes: [10, 20, 50, 100],
    onChange: (page: number) => {
      queryParams.page = page;
      handleSearch();
    },
    onUpdatePageSize: (pageSize: number) => {
      queryParams.pageSize = pageSize;
      queryParams.page = 1;
      handleSearch();
    },
  });

  const statusOptions = [
    { label: '待付款', value: 'PENDING_PAYMENT' },
    { label: '待发货', value: 'PENDING_SHIPMENT' },
    { label: '已发货', value: 'SHIPPED' },
    { label: '已完成', value: 'COMPLETED' },
    { label: '已关闭', value: 'CLOSED' },
    { label: '售后中', value: 'AFTER_SALE' },
  ];

  const statusMap: Record<string, { label: string; type: any }> = {
    PENDING_PAYMENT: { label: '待付款', type: 'warning' },
    PENDING_SHIPMENT: { label: '待发货', type: 'info' },
    SHIPPED: { label: '已发货', type: 'primary' },
    COMPLETED: { label: '已完成', type: 'success' },
    CLOSED: { label: '已关闭', type: 'default' },
    AFTER_SALE: { label: '售后中', type: 'error' },
  };

  const columns = [
    {
      title: '订单ID',
      key: 'id',
      width: 150,
      render(row: any) {
        return formatOrderId(row.id, row.createdAt);
      },
    },
    {
      title: '订单号',
      key: 'orderNo',
      width: 180,
    },
    {
      title: '买家',
      key: 'buyerName',
      width: 120,
    },
    {
      title: '总金额',
      key: 'totalAmount',
      width: 100,
      render(row: any) {
        return `¥${row.totalAmount}`;
      },
    },
    {
      title: '订单状态',
      key: 'status',
      width: 120,
      render(row: any) {
        const status = statusMap[row.status] || { label: '未知', type: 'default' };
        return h(NTag, { type: status.type }, { default: () => status.label });
      },
    },
    {
      title: '创建时间',
      key: 'createdAt',
      width: 160,
    },
    {
      title: '操作',
      key: 'actions',
      width: 120,
      render(row: any) {
        return h(
          NSpace,
          {},
          {
            default: () => [
              h(
                NButton,
                {
                  size: 'small',
                  type: 'primary',
                  text: true,
                  onClick: () => handleEditStatus(row),
                },
                { default: () => '修改状态' }
              ),
            ],
          }
        );
      },
    },
  ];

  const loadData = async () => {
    try {
      loading.value = true;
      // 过滤掉 null 和 undefined 参数
      const params: any = Object.entries(queryParams).reduce((acc: any, [key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
          acc[key] = value;
        }
        return acc;
      }, {});
      // 商家只能查看自己的订单
      if (userStore.isMerchant && userStore.getMerchantId) {
        params.shopId = userStore.getMerchantId;
      }
      const data: any = await getOrderList(params);
      dataList.value = Array.isArray(data?.list) ? data.list : [];
      pagination.page = data?.page || 1;
      pagination.pageSize = data?.pageSize || queryParams.pageSize || 10;
      pagination.pageCount = data?.pageCount || 1;
      pagination.itemCount = data?.itemCount || 0;
    } catch (error) {
      console.error('加载订单列表失败:', error);
      const message: any = (window as any).$message;
      message?.error('加载订单列表失败');
    } finally {
      loading.value = false;
    }
  };

  const handleSearch = () => {
    queryParams.page = 1;
    loadData();
  };

  const handlePageChange = (page: number) => {
    queryParams.page = page;
    loadData();
  };

  const handleEditStatus = (row: any) => {
    editOrderId.value = row.id;
    editStatus.value = row.status;
    showStatusModal.value = true;
  };

  const handleUpdateStatus = async () => {
    if (!editOrderId.value) return;
    try {
      const response: any = await updateOrderStatus({
        id: editOrderId.value,
        status: editStatus.value,
      });
      const message: any = (window as any).$message;
      message?.success('状态更新成功');
      showStatusModal.value = false;
      // 直接更新列表中的项目，然后重新加载以保持同步
      const index = dataList.value.findIndex((item: any) => item.id === editOrderId.value);
      if (index !== -1 && response) {
        dataList.value[index].status = response.status || editStatus.value;
      }
      dataList.value = [...dataList.value];
      // 延迟后重新加载以确保同步
      setTimeout(() => {
        loadData();
      }, 500);
    } catch (error) {
      console.error('更新状态失败:', error);
      const message: any = (window as any).$message;
      message?.error('更新状态失败');
    }
  };

  onMounted(() => {
    loadData();
  });
</script>

<style scoped>
  .proCard {
    min-height: 500px;
  }
</style>
