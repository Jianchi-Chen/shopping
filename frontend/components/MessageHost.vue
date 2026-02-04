<template>
  <div class="fixed top-4 left-1/2 -translate-x-1/2 z-50 space-y-3 pointer-events-none">
    <TransitionGroup name="message" tag="div" class="space-y-3">
      <div
        v-for="item in messages"
        :key="item.id"
        class="pointer-events-none flex items-center gap-3 px-4 py-3 rounded-lg shadow-lg border bg-white/95 backdrop-blur-sm min-w-[280px] max-w-[420px]"
        :class="getBorderClass(item.type)"
      >
        <span class="text-lg" :class="getIconClass(item.type)">{{ getIcon(item.type) }}</span>
        <span class="text-sm text-gray-800 leading-relaxed">{{ item.text }}</span>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup lang="ts">
const { messages } = useMessage()

const getIcon = (type: string) => {
  switch (type) {
    case 'success':
      return '✅'
    case 'info':
      return 'ℹ️'
    case 'warning':
      return '⚠️'
    case 'error':
      return '❌'
    case 'loading':
      return '⏳'
    default:
      return 'ℹ️'
  }
}

const getBorderClass = (type: string) => {
  switch (type) {
    case 'success':
      return 'border-green-200'
    case 'info':
      return 'border-blue-200'
    case 'warning':
      return 'border-yellow-200'
    case 'error':
      return 'border-red-200'
    case 'loading':
      return 'border-gray-200'
    default:
      return 'border-gray-200'
  }
}

const getIconClass = (type: string) => {
  switch (type) {
    case 'success':
      return 'text-green-600'
    case 'info':
      return 'text-blue-600'
    case 'warning':
      return 'text-yellow-600'
    case 'error':
      return 'text-red-600'
    case 'loading':
      return 'text-gray-600'
    default:
      return 'text-gray-600'
  }
}
</script>

<style scoped>
.message-enter-active,
.message-leave-active {
  transition: all 0.2s ease;
}
.message-enter-from,
.message-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
