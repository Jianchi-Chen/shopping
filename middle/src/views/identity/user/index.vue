<template>
  <n-card :bordered="false" class="proCard">
    <n-space vertical>
      <n-space justify="space-between">
        <n-space>
          <n-input
            v-model:value="queryParams.keyword"
            placeholder="搜索用户名"
            clearable
            @keyup.enter="handleSearch"
          />
          <n-select
            v-model:value="queryParams.status"
            :options="statusOptions"
            placeholder="用户状态"
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

    <!-- 用户状态修改弹窗 -->
    <n-modal v-model:show="showStatusModal">
      <n-card
        style="width: 400px"
        title="修改用户状态"
        :bordered="false"
        size="small"
        role="dialog"
        aria-modal="true"
      >
        <n-form>
          <n-form-item label="用户状态">
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
  import { getUserList, updateUserStatus } from '@/api/identity/user';
  import { formatUserId } from '@/utils/idFormatter';

  const loading = ref(false);
  const dataList = ref<any[]>([]);
  const showStatusModal = ref(false);
  const editUserId = ref<string | null>(null);
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
    { label: '正常', value: 'ACTIVE' },
    { label: '已封禁', value: 'BANNED' },
  ];

  const statusMap: Record<string, { label: string; type: any }> = {
    ACTIVE: { label: '正常', type: 'success' },
    BANNED: { label: '已封禁', type: 'error' },
  };

  const columns = [
    {
      title: '用户ID',
      key: 'id',
      width: 120,
      render(row: any) {
        return formatUserId(row.id);
      },
    },
    {
      title: '用户名',
      key: 'username',
      width: 150,
    },
    {
      title: '姓名',
      key: 'name',
      width: 120,
    },
    {
      title: '邮箱',
      key: 'email',
      width: 200,
      ellipsis: {
        tooltip: true,
      },
    },
    {
      title: '电话',
      key: 'phone',
      width: 130,
    },
    {
      title: '头像',
      key: 'avatar',
      width: 80,
      render(row: any) {
        if (row.avatar) {
          return h('img', {
            src: row.avatar,
            alt: row.username,
            style: { width: '32px', height: '32px', borderRadius: '50%', objectFit: 'cover' },
          });
        } else {
          return h('span', {}, '👤');
        }
      },
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
      // 过滤掉 null 和 undefined 参数
      const params: any = Object.entries(queryParams).reduce((acc: any, [key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
          acc[key] = value;
        }
        return acc;
      }, {});
      const data: any = await getUserList(params);
      dataList.value = data.list || [];
      pagination.page = data.page || 1;
      pagination.pageSize = data.pageSize || queryParams.pageSize || 10;
      pagination.pageCount = data.pageCount || 1;
      pagination.itemCount = data.itemCount || 0;
    } catch (error) {
      console.error('加载用户列表失败:', error);
      const message: any = (window as any).$message;
      message?.error('加载用户列表失败');
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
    editUserId.value = row.id;
    editStatus.value = row.status;
    showStatusModal.value = true;
  };

  const handleUpdateStatus = async () => {
    if (!editUserId.value) return;
    try {
      await updateUserStatus({
        id: editUserId.value,
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
