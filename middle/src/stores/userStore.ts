import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    role: '',
  }),
  actions: {
    setUser(user) {
      this.user = user;
      this.role = user.role;
    },
  },
});
