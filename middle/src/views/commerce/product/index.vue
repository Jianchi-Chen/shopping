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
        <n-space>
          <n-button v-if="userStore.isMerchant" type="success" @click="showCreateModal = true">
            新增商品
          </n-button>
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

    <!-- 新增商品弹窗 -->
    <n-modal v-model:show="showCreateModal">
      <n-card
        style="width: 520px"
        title="新增商品"
        :bordered="false"
        size="small"
        role="dialog"
        aria-modal="true"
      >
        <n-form>
          <n-form-item label="商品名称 *" required>
            <n-input v-model:value="createForm.title" placeholder="请输入商品名称" />
          </n-form-item>
          <n-form-item label="SKU *" required>
            <n-input v-model:value="createForm.sku" placeholder="请输入SKU" />
          </n-form-item>
          <n-form-item label="价格 *" required>
            <n-input-number v-model:value="createForm.price" :min="0" style="width: 100%" />
          </n-form-item>
          <n-form-item label="原价">
            <n-input-number v-model:value="createForm.originalPrice" :min="0" style="width: 100%" />
          </n-form-item>
          <n-form-item label="库存">
            <n-input-number v-model:value="createForm.stock" :min="0" style="width: 100%" />
          </n-form-item>
          <n-form-item label="状态">
            <n-select v-model:value="createForm.status" :options="statusOptions" />
          </n-form-item>
          <n-form-item label="分类">
            <n-input v-model:value="createForm.category" placeholder="请输入分类" />
          </n-form-item>
          <n-form-item label="图片链接">
            <n-input
              v-model:value="createForm.images"
              placeholder="多个图片用逗号分隔"
            />
          </n-form-item>
          <n-form-item label="描述">
            <n-input
              v-model:value="createForm.description"
              type="textarea"
              placeholder="请输入商品描述"
            />
          </n-form-item>
        </n-form>
        <template #footer>
          <n-space justify="end">
            <n-button @click="showCreateModal = false">取消</n-button>
            <n-button type="primary" @click="handleCreateProduct">提交</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>

    <!-- 编辑商品弹窗 -->
    <n-modal v-model:show="showEditModal">
      <n-card
        style="width: 520px"
        title="编辑商品"
        :bordered="false"
        size="small"
        role="dialog"
        aria-modal="true"
      >
        <n-form>
          <n-form-item label="商品名称 *" required>
            <n-input v-model:value="editForm.title" placeholder="请输入商品名称" />
          </n-form-item>
          <n-form-item label="SKU *" required>
            <n-input v-model:value="editForm.sku" placeholder="请输入SKU" />
          </n-form-item>
          <n-form-item label="价格 *" required>
            <n-input-number v-model:value="editForm.price" :min="0" style="width: 100%" />
          </n-form-item>
          <n-form-item label="原价">
            <n-input-number v-model:value="editForm.originalPrice" :min="0" style="width: 100%" />
          </n-form-item>
          <n-form-item label="库存">
            <n-input-number v-model:value="editForm.stock" :min="0" style="width: 100%" />
          </n-form-item>
          <n-form-item label="状态">
            <n-select v-model:value="editForm.status" :options="statusOptions" />
          </n-form-item>
          <n-form-item label="分类">
            <n-input v-model:value="editForm.category" placeholder="请输入分类" />
          </n-form-item>
          <n-form-item label="图片链接">
            <n-input
              v-model:value="editForm.images"
              placeholder="多个图片用逗号分隔"
            />
          </n-form-item>
          <n-form-item label="描述">
            <n-input
              v-model:value="editForm.description"
              type="textarea"
              placeholder="请输入商品描述"
            />
          </n-form-item>
        </n-form>
        <template #footer>
          <n-space justify="end">
            <n-button @click="showEditModal = false">取消</n-button>
            <n-button type="primary" @click="handleUpdateProduct">提交</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>
  </n-card>
</template>

<script lang="ts" setup>
  import { h, ref, onMounted, reactive } from 'vue';
  import { NButton, NSpace, NTag, NImage } from 'naive-ui';
  import { getProductList, updateProductStatus, createProduct, updateProduct } from '@/api/commerce/product';
  import { useUserStore } from '@/store/modules/user';
  import { formatProductId, getImageUrl } from '@/utils/idFormatter';

  const userStore = useUserStore();
  const loading = ref(false);
  const dataList = ref<any[]>([]);
  const showStatusModal = ref(false);
  const editProductId = ref<string | null>(null);
  const editStatus = ref('');
  const showCreateModal = ref(false);
  const showEditModal = ref(false);

  const createForm = reactive({
    title: '',
    sku: '',
    price: 0,
    originalPrice: 0,
    stock: 0,
    status: 'ON_SALE',
    category: '',
    images: '',
    description: '',
  });

  const editForm = reactive({
    title: '',
    sku: '',
    price: 0,
    originalPrice: 0,
    stock: 0,
    status: 'ON_SALE',
    category: '',
    images: '',
    description: '',
  });

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
      width: 120,
      render(row: any) {
        return formatProductId(row.id);
      },
    },
    {
      title: '商品图片',
      key: 'image',
      width: 100,
      render(row: any) {
        // 处理images字段，可能是字符串或数组
        let images = [];
        if (typeof row.images === 'string') {
          try {
            images = JSON.parse(row.images);
          } catch (e) {
            // 如果不是JSON，可能是单个URL
            images = row.images ? [row.images] : [];
          }
        } else if (Array.isArray(row.images)) {
          images = row.images;
        }
        const firstImage = images[0] || '';
        return h(NImage, {
          src: getImageUrl(firstImage),
          width: 60,
          height: 60,
          objectFit: 'cover',
          fallbackSrc: 'https://via.placeholder.com/60?text=No+Image',
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
                  onClick: () => handleEdit(row),
                },
                { default: () => '编辑' }
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
      // 商家只能查看自己的商品
      if (userStore.isMerchant && userStore.getMerchantId) {
        params.shopId = userStore.getMerchantId;
      }
      const data: any = await getProductList(params);
      dataList.value = data.list || [];
      pagination.page = data.page || 1;
      pagination.pageSize = data.pageSize || queryParams.pageSize || 10;
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

  const handleEdit = (row: any) => {
    editProductId.value = row.id;
    editForm.title = row.name || row.title;
    editForm.sku = row.sku;
    editForm.price = row.price;
    editForm.originalPrice = row.originalPrice || 0;
    editForm.stock = row.stock;
    editForm.status = row.status;
    editForm.category = row.category || '';
    editForm.images = row.images || '';
    editForm.description = row.description || '';
    showEditModal.value = true;
  };

  const handleUpdateProduct = async () => {
    try {
      if (!editForm.title || !editForm.sku || !editForm.price) {
        const message: any = (window as any).$message;
        message?.error('请填写商品名称、SKU 和价格');
        return;
      }

      const payload = {
        title: editForm.title,
        sku: editForm.sku,
        price: editForm.price,
        originalPrice: editForm.originalPrice,
        stock: editForm.stock,
        status: editForm.status,
        category: editForm.category,
        images: editForm.images,
        description: editForm.description,
      };

      await updateProduct(editProductId.value!, payload);
      const message: any = (window as any).$message;
      message?.success('商品更新成功');
      showEditModal.value = false;
      loadData();
    } catch (error) {
      console.error('更新商品失败:', error);
      const message: any = (window as any).$message;
      message?.error('更新商品失败');
    }
  };

  const handleCreateProduct = async () => {
    try {
      // 检查必填字段
      if (!createForm.title || !createForm.sku || !createForm.price) {
        const message: any = (window as any).$message;
        message?.error('请填写商品名称、SKU 和价格');
        return;
      }
      
      // 从localStorage获取当前用户信息（包含merchantId）
      const currentUserStr = localStorage.getItem('CURRENT_USER');
      let merchantId = null;
      let shopName = '商家店铺';
      
      if (currentUserStr) {
        try {
          const currentUser = JSON.parse(currentUserStr);
          merchantId = currentUser.merchantId;
          shopName = currentUser.name || currentUser.username || '商家店铺';
        } catch (e) {
          console.error('解析用户信息失败:', e);
        }
      }
      
      // 构建请求体 - 从localStorage传递商家ID和店铺名称
      const payload = {
        ...createForm,
        shopId: merchantId ? String(merchantId) : undefined,
        shopName: shopName,
      };
      
      await createProduct(payload);
      const message: any = (window as any).$message;
      message?.success('商品创建成功');
      showCreateModal.value = false;
      Object.assign(createForm, {
        title: '',
        sku: '',
        price: 0,
        originalPrice: 0,
        stock: 0,
        status: 'ON_SALE',
        category: '',
        images: '',
        description: '',
      });
      loadData();
    } catch (error) {
      console.error('新增商品失败:', error);
      const message: any = (window as any).$message;
      message?.error('新增商品失败');
    }
  };

  const handleUpdateStatus = async () => {
    if (!editProductId.value) return;
    try {
      const response: any = await updateProductStatus({
        id: editProductId.value,
        status: editStatus.value,
      });
      const message: any = (window as any).$message;
      message?.success('状态更新成功');
      showStatusModal.value = false;
      // 直接更新列表中的项目，然后重新加载以保持同步
      const index = dataList.value.findIndex((item: any) => item.id === editProductId.value);
      if (index !== -1 && response) {
        dataList.value[index].status = response.status || editStatus.value;
      }
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
