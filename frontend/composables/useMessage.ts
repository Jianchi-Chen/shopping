type MessageType = 'success' | 'info' | 'warning' | 'error' | 'loading'

export interface MessageItem {
  id: string
  type: MessageType
  text: string
  duration: number
}

export interface MessageOptions {
  duration?: number
}

const getDefaultDuration = (type: MessageType) => {
  if (type === 'error') return 8000
  return 5000
}

const timers = new Map<string, ReturnType<typeof setTimeout>>()

export const useMessage = () => {
  const messages = useState<MessageItem[]>('messages', () => [])

  const remove = (id: string) => {
    messages.value = messages.value.filter((m) => m.id !== id)
    const timer = timers.get(id)
    if (timer) {
      clearTimeout(timer)
      timers.delete(id)
    }
  }

  const show = (type: MessageType, text: string, options: MessageOptions = {}) => {
    const id = `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`
    const duration = options.duration ?? getDefaultDuration(type)
    messages.value.push({ id, type, text, duration })

    if (duration > 0) {
      const timer = setTimeout(() => remove(id), duration)
      timers.set(id, timer)
    }

    return id
  }

  const success = (text: string, options?: MessageOptions) => show('success', text, options)
  const info = (text: string, options?: MessageOptions) => show('info', text, options)
  const warning = (text: string, options?: MessageOptions) => show('warning', text, options)
  const error = (text: string, options?: MessageOptions) => show('error', text, options)
  const loading = (text: string, options?: MessageOptions) => show('loading', text, options)

  return {
    messages,
    show,
    remove,
    success,
    info,
    warning,
    error,
    loading,
  }
}
