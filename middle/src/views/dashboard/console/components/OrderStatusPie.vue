<template>
  <div ref="chartRef" :style="{ height, width }"></div>
</template>

<script lang="ts" setup>
  import { onMounted, ref, Ref } from 'vue';
  import { useECharts } from '@/hooks/web/useECharts';
  import { basicProps } from './props';

  const props = defineProps({
    ...basicProps,
    data: {
      type: Array as () => { name: string; value: number }[],
      default: () => [],
    },
  });

  const chartRef = ref<HTMLDivElement | null>(null);
  const { setOptions } = useECharts(chartRef as Ref<HTMLDivElement>);

  onMounted(() => {
    setOptions({
      tooltip: {
        trigger: 'item',
      },
      legend: {
        bottom: 0,
      },
      series: [
        {
          type: 'pie',
          radius: ['35%', '65%'],
          data: props.data,
        },
      ],
    });
  });
</script>
