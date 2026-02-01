<template>
    <div class="bg-white">
        <!-- 主要轮播区 -->
        <div
            class="relative w-full h-40 md:h-96 bg-gradient-to-r from-orange-400 to-red-500 overflow-hidden"
        >
            <!-- 轮播图片 -->
            <div
                v-for="(slide, index) in slides"
                :key="index"
                class="absolute inset-0 transition-opacity duration-500"
                :class="{ 'opacity-0': currentSlide !== index }"
            >
                <img
                    :src="slide.image"
                    :alt="slide.title"
                    class="w-full h-full object-cover"
                />
            </div>

            <!-- 轮播文字内容 -->
            <div
                class="absolute inset-0 flex items-center justify-center bg-black/20"
            >
                <div class="text-center text-white px-4">
                    <h1 class="text-2xl md:text-4xl font-bold mb-2">
                        {{ currentSlideData.title }}
                    </h1>
                    <p class="text-sm md:text-base mb-4">
                        {{ currentSlideData.subtitle }}
                    </p>
                    <button
                        class="bg-white text-orange-500 font-bold py-2 px-6 rounded hover:bg-gray-100 transition-colors"
                    >
                        了解更多
                    </button>
                </div>
            </div>

            <!-- 轮播指示器 -->
            <div class="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2">
                <button
                    v-for="(_, index) in slides"
                    :key="index"
                    @click="currentSlide = index"
                    class="w-2 h-2 rounded-full transition-all"
                    :class="{
                        'bg-white w-6': currentSlide === index,
                        'bg-white/50': currentSlide !== index,
                    }"
                />
            </div>
        </div>

        <!-- 分类快捷链接 -->
        <div
            class="grid grid-cols-3 md:grid-cols-6 gap-4 p-4 md:p-6 bg-gray-50"
        >
            <div
                v-for="category in categories"
                :key="category.id"
                @click="handleCategoryClick(category.id)"
                class="text-center cursor-pointer hover:opacity-80 transition-opacity"
            >
                <div
                    class="w-16 h-16 md:w-20 md:h-20 mx-auto mb-2 rounded-full overflow-hidden bg-white shadow hover:shadow-md transition-shadow"
                >
                    <img
                        :src="category.image"
                        :alt="category.name"
                        class="w-full h-full object-cover"
                    />
                </div>
                <p class="text-xs md:text-sm font-medium text-gray-800">
                    {{ category.name }}
                </p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import type { Category } from "../types/product";
import { getMockCategories } from "../utils/mockData";

const router = useRouter();
const route = useRoute();
const currentSlide = ref(0);
const categories = ref<Category[]>(getMockCategories());
let slideInterval: number | null = null;

const slides = [
    {
        title: "夏季大促销",
        subtitle: "全场商品最高优惠 50%",
        image: "https://images.unsplash.com/photo-1556740738-b6a63e27c4df?w=1200&h=500&fit=crop",
    },
    {
        title: "科技新品上市",
        subtitle: "最新电子产品，立即选购",
        image: "https://images.unsplash.com/photo-1505940338710-b272ca3fb100?w=1200&h=500&fit=crop",
    },
    {
        title: "家居装修节",
        subtitle: "打造梦想家园，品质生活",
        image: "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=1200&h=500&fit=crop",
    },
];

const currentSlideData = computed(() => {
    return slides[currentSlide.value] || slides[0]!;
});

// 处理分类点击 - 再次点击相同分类则取消过滤
const handleCategoryClick = (categoryId: string) => {
    const currentCategory = route.query.category as string | undefined;
    
    if (currentCategory === categoryId) {
        // 已选中该分类，则取消过滤
        router.push({
            path: '/',
            query: {}
        });
    } else {
        // 选择新分类
        router.push({
            path: '/',
            query: { category: categoryId }
        });
    }
};

// 自动轮播
onMounted(() => {
    slideInterval = window.setInterval(() => {
        currentSlide.value = (currentSlide.value + 1) % slides.length;
    }, 5000);
});

onUnmounted(() => {
    if (slideInterval !== null) {
        clearInterval(slideInterval);
    }
});
</script>
