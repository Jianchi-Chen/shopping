import { defineStore } from 'pinia';
import { store } from '@/store';
import { ACCESS_TOKEN, CURRENT_USER, IS_SCREENLOCKED } from '@/store/mutation-types';
import { ResultEnum } from '@/enums/httpEnum';

import { getUserInfo as getUserInfoApi, login } from '@/api/system/user';
import { storage } from '@/utils/Storage';

export type UserInfoType = {
  id?: number;
  username: string;
  name?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  role?: string; // USER | MERCHANT | ADMIN
  merchantId?: number; // 商家ID，仅商家账号有值
  createdAt?: string;
};

export interface IUserState {
  token: string;
  username: string;
  welcome: string;
  avatar: string;
  permissions: any[];
  info: UserInfoType;
  role: string; // 当前角色
}

export const useUserStore = defineStore({
  id: 'app-user',
  state: (): IUserState => ({
    token: storage.get(ACCESS_TOKEN, ''),
    username: '',
    welcome: '',
    avatar: '',
    permissions: [],
    info: storage.get(CURRENT_USER, {}),
    role: storage.get(CURRENT_USER, {}).role || '',
  }),
  getters: {
    getToken(): string {
      return this.token;
    },
    getAvatar(): string {
      return this.avatar;
    },
    getNickname(): string {
      return this.username;
    },
    getPermissions(): [any][] {
      return this.permissions;
    },
    getUserInfo(): UserInfoType {
      return this.info;
    },
    getRole(): string {
      return this.role || this.info?.role || '';
    },
    isAdmin(): boolean {
      return this.getRole === 'ADMIN';
    },
    isMerchant(): boolean {
      return this.getRole === 'MERCHANT';
    },
    getMerchantId(): number | undefined {
      return this.info?.merchantId;
    },
  },
  actions: {
    setToken(token: string) {
      this.token = token;
    },
    setAvatar(avatar: string) {
      this.avatar = avatar;
    },
    setPermissions(permissions) {
      this.permissions = permissions;
    },
    setUserInfo(info: UserInfoType) {
      this.info = info;
      this.role = info.role || '';
      this.username = info.username || info.name || '';
      this.avatar = info.avatar || '';
    },
    // 登录
    async login(params: any) {
      const response = await login(params);
      const { result, code } = response;
      if (code === ResultEnum.SUCCESS) {
        const ex = 7 * 24 * 60 * 60;
        storage.set(ACCESS_TOKEN, result.token, ex);
        storage.set(CURRENT_USER, result, ex);
        storage.set(IS_SCREENLOCKED, false);
        this.setToken(result.token);
        this.setUserInfo(result);
      }
      return response;
    },

    // 获取用户信息
    async getInfo() {
      const data = await getUserInfoApi();
      const { result } = data;
      // 根据角色生成权限列表
      const role = result.role || 'USER';
      const permissionsList = this.generatePermissions(role);
      this.setPermissions(permissionsList);
      this.setUserInfo(result);
      this.setAvatar(result.avatar || '');
      storage.set(CURRENT_USER, result);
      return result;
    },

    // 根据角色生成权限
    generatePermissions(role: string): string[] {
      const basePermissions = ['dashboard_console', 'dashboard_workplace'];
      const commercePermissions = ['commerce_product_list', 'commerce_order_list'];
      const identityPermissions = ['identity_merchant_list', 'identity_user_list'];

      if (role === 'ADMIN') {
        return [...basePermissions, ...commercePermissions, ...identityPermissions];
      } else if (role === 'MERCHANT') {
        return [...basePermissions, ...commercePermissions];
      }
      return basePermissions;
    },

    // 登出
    async logout() {
      this.setPermissions([]);
      this.setUserInfo({ username: '', email: '' });
      this.role = '';
      storage.remove(ACCESS_TOKEN);
      storage.remove(CURRENT_USER);
    },
  },
});

// Need to be used outside the setup
export function useUser() {
  return useUserStore(store);
}
