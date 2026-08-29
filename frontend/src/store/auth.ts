import * as SecureStore from "expo-secure-store";
import { create } from "zustand";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import type { Role } from "@/types";

export interface AuthUser {
  id: string;
  email: string;
  fullName: string;
  roles: Role[];
}

interface AuthState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshToken: () => Promise<boolean>;
  setUser: (user: AuthUser) => void;
  hydrate: () => Promise<void>;
}

/**
 * Global auth state. Tokens are persisted in Expo SecureStore (Android
 * Keystore-backed), never in AsyncStorage, since they are bearer
 * credentials (docs/architecture/08_SECURITY_AND_VAULT.md Part C.4).
 */
export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isAuthenticated: false,
  isLoading: true,

  async login(email: string, password: string) {
    const response = await apiClient.post(endpoints.auth.login(), { email, password });
    const { accessToken, refreshToken, user } = response.data.data;
    await SecureStore.setItemAsync("accessToken", accessToken);
    await SecureStore.setItemAsync("refreshToken", refreshToken);
    set({ user, isAuthenticated: true });
  },

  async logout() {
    await SecureStore.deleteItemAsync("accessToken");
    await SecureStore.deleteItemAsync("refreshToken");
    set({ user: null, isAuthenticated: false });
  },

  async refreshToken() {
    const storedRefreshToken = await SecureStore.getItemAsync("refreshToken");
    if (!storedRefreshToken) {
      return false;
    }
    try {
      const response = await apiClient.post(endpoints.auth.refresh(), {
        refreshToken: storedRefreshToken,
      });
      const { accessToken } = response.data.data;
      await SecureStore.setItemAsync("accessToken", accessToken);
      return true;
    } catch {
      return false;
    }
  },

  setUser(user: AuthUser) {
    set({ user, isAuthenticated: true });
  },

  async hydrate() {
    const token = await SecureStore.getItemAsync("accessToken");
    if (!token) {
      set({ isLoading: false });
      return;
    }
    try {
      const response = await apiClient.get(endpoints.auth.me());
      set({ user: response.data.data, isAuthenticated: true, isLoading: false });
    } catch {
      set({ user: null, isAuthenticated: false, isLoading: false });
    }
  },
}));
