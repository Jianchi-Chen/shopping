<template>
  <div class="console">
    <!-- 数据卡片 -->
    <n-grid cols="1 s:2 m:3 l:4 xl:4 2xl:4" responsive="screen" :x-gap="12" :y-gap="8">
      <n-grid-item>
        <n-card
          title="访问量"
          :segmented="{ content: true, footer: true }"
          size="small"
          :bordered="false"
        >
          <template #header-extra>
            <n-tag type="success">日</n-tag>
          </template>
          <div class="flex justify-between px-1 py-1">
            <n-skeleton v-if="loading" :width="100" size="medium" />
            <count-to v-else :start-val="1" :end-val="statistics.visits || 0" class="text-3xl" />
          </div>
          <template #footer>
            <div class="flex justify-between">
              <div class="text-sn">总访问量</div>
              <div class="text-sn">
                <count-to :start-val="1" :end-val="statistics.totalVisits || 0" />
              </div>
            </div>
          </template>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card
          title="销售额"
          :segmented="{ content: true, footer: true }"
          size="small"
          :bordered="false"
        >
          <template #header-extra>
            <n-tag type="info">周</n-tag>
          </template>
          <div class="flex justify-between px-1 py-1">
            <n-skeleton v-if="loading" :width="100" size="medium" />
            <count-to
              v-else
              prefix="￥"
              :start-val="1"
              :end-val="statistics.sales || 0"
              class="text-3xl"
            />
          </div>
          <template #footer>
            <div class="flex justify-between">
              <div class="text-sn">总销售额</div>
              <div class="text-sn">
                <count-to prefix="￥" :start-val="1" :end-val="statistics.totalSales || 0" />
              </div>
            </div>
          </template>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card
          title="订单量"
          :segmented="{ content: true, footer: true }"
          size="small"
          :bordered="false"
        >
          <template #header-extra>
            <n-tag type="warning">周</n-tag>
          </template>
          <div class="flex justify-between px-1 py-1">
            <n-skeleton v-if="loading" :width="100" size="medium" />
            <count-to v-else :start-val="1" :end-val="statistics.orders || 0" class="text-3xl" />
          </div>
          <template #footer>
            <div class="flex justify-between">
              <div class="text-sn">总订单量</div>
              <div class="text-sn">
                <count-to :start-val="1" :end-val="statistics.totalOrders || 0" />
              </div>
            </div>
          </template>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card
          title="成交额"
          :segmented="{ content: true, footer: true }"
          size="small"
          :bordered="false"
        >
          <template #header-extra>
            <n-tag type="error">月</n-tag>
          </template>
          <div class="flex justify-between px-1 py-1">
            <n-skeleton v-if="loading" :width="100" size="medium" />
            <count-to
              v-else
              prefix="￥"
              :start-val="1"
              :end-val="statistics.revenue || 0"
              class="text-3xl"
            />
          </div>
          <template #footer>
            <div class="flex justify-between">
              <div class="text-sn">总成交额</div>
              <div class="text-sn">
                <count-to prefix="￥" :start-val="1" :end-val="statistics.totalRevenue || 0" />
              </div>
            </div>
          </template>
        </n-card>
      </n-grid-item>
    </n-grid>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { CountTo } from '@/components/CountTo';
import { getStatistics } from '@/api/dashboard/index';
import { useUserStore } from '@/store/modules/user';

const userStore = useUserStore();
const loading = ref(true);
const statistics = ref<any>({
  visits: 0,
  totalVisits: 0,
  sales: 0,
  totalSales: 0,
  orders: 0,
  totalOrders: 0,
  revenue: 0,
  totalRevenue: 0,
});

const loadStatistics = async () => {
  try {
    loading.value = true;
    const params: any = {};
    // 如果是商家，只查询自己店铺的数据
    if (userStore.isMerchant && userStore.getMerchantId) {
      params.shopId = userStore.getMerchantId;
    }
    const data = await getStatistics(params);
    statistics.value = data || statistics.value;
  } catch (error) {
    console.error('加载统计数据失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadStatistics();
});
</script>

<style lang="less" scoped>
.console {
  padding: 10px;
}
.text-sn {
  font-size: 14px;
  color: #666;
}
</style>
