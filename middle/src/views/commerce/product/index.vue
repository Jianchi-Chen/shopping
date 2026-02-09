<template>
  <n-card :bordered="false" class="proCard">
    <n-space vertical>
      <n-space justify="space-between">
        <n-space>
          <n-input
            v-model:value="queryParams.keyword"
            placeholder="搜索商品名称"
            clearable
            @keyup.enter="handleSearch"
          />
          <n-select
            v-model:value="queryParams.status"
            :options="statusOptions"
            placeholder="商品状态"
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

    <!-- 商品状态修改弹窗 -->
    <n-modal v-model:show="showStatusModal">
      <n-card
        style="width: 400px"
        title="修改商品状态"
        :bordered="false"
        size="small"
        role="dialog"
        aria-modal="true"
      >
        <n-form>
          <n-form-item label="商品状态">
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
  import { NButton, NSpace, NTag, NImage } from 'naive-ui';
  import { getProductList, updateProductStatus } from '@/api/commerce/product';
  import { useUserStore } from '@/store/modules/user';

  const userStore = useUserStore();
  const loading = ref(false);
  const dataList = ref<any[]>([]);
  const showStatusModal = ref(false);
  const editProductId = ref<number | null>(null);
  const editStatus = ref('');

  const queryParams = reactive({
    keyword: '',
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
    { label: '在售', value: 'ON_SALE' },
    { label: '下架', value: 'OFF_SALE' },
    { label: '缺货', value: 'OUT_OF_STOCK' },
  ];

  const statusMap: Record<string, { label: string; type: any }> = {
    ON_SALE: { label: '在售', type: 'success' },
    OFF_SALE: { label: '下架', type: 'default' },
    OUT_OF_STOCK: { label: '缺货', type: 'warning' },
  };

  const columns = [
    {
      title: '商品ID',
      key: 'id',
      width: 80,
    },
    {
      title: '商品图片',
      key: 'image',
      width: 100,
      render(row: any) {
        const images = row.images || [];
        const firstImage = images[0] || '';
        return h(NImage, {
          src: firstImage,
          width: 60,
          height: 60,
          objectFit: 'cover',
          fallbackSrc: 'https://via.placeholder.com/60',
        });
      },
    },
    {
      title: '商品名称',
      key: 'name',
      ellipsis: {
        tooltip: true,
      },
    },
    {
      title: '价格',
      key: 'price',
      width: 100,
      render(row: any) {
        return `¥${row.price}`;
      },
    },
    {
      title: '库存',
      key: 'stock',
      width: 80,
    },
    {
      title: '状态',
      key: 'status',
      width: 100,
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
      const params: any = { ...queryParams };
      // 商家只能查看自己的商品
      if (userStore.isMerchant && userStore.getMerchantId) {
        params.shopId = userStore.getMerchantId;
      }
      const data: any = await getProductList(params);
      dataList.value = data.list || [];
      pagination.page = data.page || 1;
      pagination.pageCount = data.pageCount || 1;
      pagination.itemCount = data.itemCount || 0;
    } catch (error) {
      console.error('加载商品列表失败:', error);
      const message: any = (window as any).$message;
      message?.error('加载商品列表失败');
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
    editProductId.value = row.id;
    editStatus.value = row.status;
    showStatusModal.value = true;
  };

  const handleUpdateStatus = async () => {
    if (!editProductId.value) return;
    try {
      await updateProductStatus({
        id: editProductId.value,
        status: editStatus.value,
      });
      const message: any = (window as any).$message;
      message?.success('状态更新成功');
      showStatusModal.value = false;
      loadData();
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
