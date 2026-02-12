/**
 * ID格式化工具
 * 将UUID转换为短码显示
 */

/**
 * 格式化商品ID
 * 格式: P-8F3KQ9 (P + UUID前6位大写)
 */
export function formatProductId(uuid: string): string {
  if (!uuid) return '-';
  const shortCode = uuid.replace(/-/g, '').substring(0, 6).toUpperCase();
  return `P-${shortCode}`;
}

/**
 * 格式化商家ID
 * 格式: M-2L9X8A (M + UUID前6位大写)
 */
export function formatMerchantId(uuid: string): string {
  if (!uuid) return '-';
  const shortCode = uuid.replace(/-/g, '').substring(0, 6).toUpperCase();
  return `M-${shortCode}`;
}

/**
 * 格式化用户ID
 * 格式: U-7H4D2C (U + UUID前6位大写)
 */
export function formatUserId(uuid: string): string {
  if (!uuid) return '-';
  const shortCode = uuid.replace(/-/g, '').substring(0, 6).toUpperCase();
  return `U-${shortCode}`;
}

/**
 * 格式化订单ID
 * 格式: O-20240209-3K9A (O + 日期 + UUID前4位大写)
 * @param uuid 订单UUID
 * @param createTime 订单创建时间 (格式: YYYY-MM-DD HH:mm:ss 或 Date对象)
 */
export function formatOrderId(uuid: string, createTime?: string | Date): string {
  if (!uuid) return '-';
  
  let dateStr = '';
  if (createTime) {
    if (typeof createTime === 'string') {
      // 从字符串中提取日期部分
      dateStr = createTime.split(' ')[0].replace(/-/g, '');
    } else if (createTime instanceof Date) {
      // 从Date对象格式化
      const year = createTime.getFullYear();
      const month = String(createTime.getMonth() + 1).padStart(2, '0');
      const day = String(createTime.getDate()).padStart(2, '0');
      dateStr = `${year}${month}${day}`;
    }
  } else {
    // 如果没有提供时间，使用当前日期
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    dateStr = `${year}${month}${day}`;
  }
  
  const shortCode = uuid.replace(/-/g, '').substring(0, 4).toUpperCase();
  return `O-${dateStr}-${shortCode}`;
}

/**
 * 通用ID格式化
 * @param type ID类型: product, merchant, user, order
 * @param uuid UUID
 * @param createTime 创建时间(仅订单需要)
 */
export function formatId(type: 'product' | 'merchant' | 'user' | 'order', uuid: string, createTime?: string | Date): string {
  switch (type) {
    case 'product':
      return formatProductId(uuid);
    case 'merchant':
      return formatMerchantId(uuid);
    case 'user':
      return formatUserId(uuid);
    case 'order':
      return formatOrderId(uuid, createTime);
    default:
      return uuid;
  }
}

/**
 * 获取完整的图片URL
 * @param imagePath 图片路径
 * @param baseUrl 基础URL (默认从环境变量获取)
 */
export function getImageUrl(imagePath: string | null | undefined, baseUrl?: string): string {
  // 使用数据URI作为默认占位图（更可靠）
  const defaultImage = 'data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%27200%27 height=%27200%27%3E%3Crect fill=%27%23e0e0e0%27 width=%27200%27 height=%27200%27/%3E%3Ctext x=%2750%25%27 y=%2750%25%27 font-family=%27Arial%27 font-size=%2716%27 fill=%27%23999%27 text-anchor=%27middle%27 dy=%27.3em%27%3ENo Image%3C/text%3E%3C/svg%3E';
  
  if (!imagePath) {
    return defaultImage;
  }
  
  // 如果已经是完整URL，直接返回
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }
  
  // 获取基础URL
  const base = baseUrl || import.meta.env.VITE_GLOB_FILE_URL || 'http://localhost:8080/api';
  
  // 确保路径以 / 开头
  const path = imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
  
  return `${base}${path}`;
}
