<template>
  <div>
    <div class="n-layout-page-header">
      <n-card :bordered="false" title="工作台">
        <n-grid cols="2 s:1 m:1 l:2 xl:2 2xl:2" responsive="screen">
          <n-gi>
            <div class="flex items-center">
              <div>
                <n-avatar circle :size="64" :src="userStore.getAvatar || defaultAvatar" />
              </div>
              <div>
                <p class="px-4 text-xl">早安,{{ userStore.getNickname }},开始您一天的工作吧！</p>
                <p class="px-4 text-gray-400">{{ roleLabel }},今日待办 {{ todoCount }} 项</p>
              </div>
            </div>
          </n-gi>
          <n-gi>
            <div class="flex justify-end w-full">
              <div class="flex flex-col justify-center flex-1 text-right">
                <span class="text-secondary">已上架商品</span>
                <span class="text-2xl">{{ productCount }}</span>
              </div>
              <div class="flex flex-col justify-center flex-1 text-right">
                <span class="text-secondary">待办</span>
                <span class="text-2xl">{{ finishedTodoCount }}/{{ todoCount }}</span>
              </div>
            </div>
          </n-gi>
        </n-grid>
      </n-card>
    </div>

    <n-grid class="mt-4" cols="1 s:1 m:1 l:1 xl:1 2xl:1" responsive="screen" :x-gap="12" :y-gap="9">
      <n-gi>
        <n-card
          :segmented="{ content: true }"
          content-style="padding-top: 0;padding-bottom: 0;"
          :bordered="false"
          size="small"
          title="待办列表"
        >
          <template #header-extra>
            <n-button text type="primary" @click="showAddTodo = true">+ 添加</n-button>
          </template>
          <n-list v-if="todoList.length > 0">
            <n-list-item v-for="item in todoList" :key="item.id">
              <n-thing :title="item.title">
                <template #description>
                  <p class="text-xs text-gray-500">{{ item.createTime }}</p>
                </template>
                <template #action>
                  <n-space>
                    <n-tag v-if="item.status === 'PENDING'" type="warning">待处理</n-tag>
                    <n-tag v-else type="success">已完成</n-tag>
                    <n-button
                      v-if="item.status === 'PENDING'"
                      size="small"
                      @click="completeTodo(item.id)"
                      >完成</n-button
                    >
                  </n-space>
                </template>
              </n-thing>
            </n-list-item>
          </n-list>
          <n-empty v-else description="暂无待办事项" />
        </n-card>
      </n-gi>
    </n-grid>

    <!-- 添加待办弹窗 -->
    <n-modal v-model:show="showAddTodo">
      <n-card
        style="width: 500px"
        title="添加待办"
        :bordered="false"
        size="small"
        role="dialog"
        aria-modal="true"
      >
        <n-form ref="formRef" :model="todoForm">
          <n-form-item label="待办标题" path="title">
            <n-input v-model:value="todoForm.title" placeholder="请输入待办标题" />
          </n-form-item>
        </n-form>
        <template #footer>
          <n-space justify="end">
            <n-button @click="showAddTodo = false">取消</n-button>
            <n-button type="primary" @click="handleAddTodo">确定</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted, computed } from 'vue';
  import { useUserStore } from '@/store/modules/user';
  import { getTodoList } from '@/api/dashboard/index';
  import { getProductList } from '@/api/commerce/product';

  const userStore = useUserStore();
  const defaultAvatar = 'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg';

  const todoList = ref<any[]>([]);
  const productCount = ref(0);
  const showAddTodo = ref(false);
  const todoForm = ref({
    title: '',
  });

  const roleLabel = computed(() => {
    if (userStore.isAdmin) {
      return '平台管理员';
    } else if (userStore.isMerchant) {
      return '商家';
    }
    return '用户';
  });

  const todoCount = computed(() => todoList.value.length);
  const finishedTodoCount = computed(
    () => todoList.value.filter((item) => item.status === 'COMPLETED').length
  );

  const loadTodoList = async () => {
    try {
      const data: any = await getTodoList();
      todoList.value = data || [];
    } catch (error) {
      console.error('加载待办列表失败:', error);
      // Mock 数据
      todoList.value = [
        {
          id: 1,
          title: '处理待发货订单',
          status: 'PENDING',
          createTime: '2026-02-04 09:00',
        },
        {
          id: 2,
          title: '审核新上架商品',
          status: 'PENDING',
          createTime: '2026-02-04 10:00',
        },
        {
          id: 3,
          title: '回复客户咨询',
          status: 'COMPLETED',
          createTime: '2026-02-03 15:00',
        },
      ];
    }
  };

  const loadProductCount = async () => {
    try {
      const params: any = { page: 1, pageSize: 1, status: 'ON_SALE' };
      if (userStore.isMerchant && userStore.getMerchantId) {
        params.shopId = userStore.getMerchantId;
      }
      const data: any = await getProductList(params);
      productCount.value = data?.itemCount || 0;
    } catch (error) {
      console.error('加载商品数量失败:', error);
      productCount.value = 0;
    }
  };

  const completeTodo = (id: number) => {
    const todo = todoList.value.find((item) => item.id === id);
    if (todo) {
      todo.status = 'COMPLETED';
    }
    const message: any = (window as any).$message;
    message?.success('待办已完成');
  };

  const handleAddTodo = () => {
    if (!todoForm.value.title) {
      const message: any = (window as any).$message;
      message?.warning('请输入待办标题');
      return;
    }
    todoList.value.unshift({
      id: Date.now(),
      title: todoForm.value.title,
      status: 'PENDING',
      createTime: new Date().toLocaleString('zh-CN'),
    });
    todoForm.value.title = '';
    showAddTodo.value = false;
    const message: any = (window as any).$message;
    message?.success('添加成功');
  };

  onMounted(() => {
    loadTodoList();
    loadProductCount();
  });
</script>

<style scoped>
  .text-secondary {
    color: #999;
  }
</style>
