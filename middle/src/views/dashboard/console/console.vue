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

    <n-grid cols="1 s:1 m:2 l:3" responsive="screen" :x-gap="12" :y-gap="12" class="mt-4">
      <n-grid-item>
        <n-card title="访问与成交趋势" :bordered="false" size="small">
          <FluxTrend
            :height="'260px'"
            :x-data="metrics.dates"
            :series="trendSeries"
          />
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="订单量趋势" :bordered="false" size="small">
          <VisitAmount
            :height="'260px'"
            :x-data="metrics.dates"
            :data="metrics.orders"
          />
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="订单状态分布" :bordered="false" size="small">
          <OrderStatusPie :height="'260px'" :data="metrics.statusBreakdown" />
        </n-card>
      </n-grid-item>
    </n-grid>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { CountTo } from '@/components/CountTo';
import { getStatistics, getMetrics } from '@/api/dashboard/index';
import { useUserStore } from '@/store/modules/user';
import FluxTrend from './components/FluxTrend.vue';
import VisitAmount from './components/VisitAmount.vue';
import OrderStatusPie from './components/OrderStatusPie.vue';

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

const metrics = ref<any>({
  dates: [],
  visits: [],
  orders: [],
  revenue: [],
  statusBreakdown: [],
});

const trendSeries = ref([
  { name: '访问量', data: [] as number[] },
  { name: '成交额', data: [] as number[] },
]);

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

const loadMetrics = async () => {
  try {
    const params: any = { days: 30 };
    if (userStore.isMerchant && userStore.getMerchantId) {
      params.shopId = userStore.getMerchantId;
    }
    const data = await getMetrics(params);
    metrics.value = data || metrics.value;
    const dates = Array.isArray(metrics.value.dates) ? metrics.value.dates : [];
    const visits = Array.isArray(metrics.value.visits) ? metrics.value.visits : [];
    const orders = Array.isArray(metrics.value.orders) ? metrics.value.orders : [];
    const revenue = Array.isArray(metrics.value.revenue) ? metrics.value.revenue : [];
    const statusBreakdown = Array.isArray(metrics.value.statusBreakdown) ? metrics.value.statusBreakdown : [];

    metrics.value = {
      dates,
      visits,
      orders,
      revenue,
      statusBreakdown,
    };

    trendSeries.value = [
      { name: '访问量', data: visits.map((v: any) => Number(v)) },
      { name: '成交额', data: revenue.map((v: any) => Number(v)) },
    ];
  } catch (error) {
    console.error('加载趋势数据失败:', error);
  }
};

onMounted(() => {
  loadStatistics();
  loadMetrics();
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
