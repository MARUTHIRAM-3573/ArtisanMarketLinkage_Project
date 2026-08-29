import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";
import { randomUUID } from "expo-crypto";
import * as SecureStore from "expo-secure-store";

import { useAuthStore } from "@/store/auth";

/**
 * Shared Axios instance used by every API call in the app. Attaches the JWT
 * bearer token, a correlation id, and handles the 401/403/5xx cases per
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §6 and
 * docs/architecture/08_SECURITY_AND_VAULT.md Part C.4.
 */
export const apiClient = axios.create({
  baseURL: process.env.EXPO_PUBLIC_API_BASE_URL ?? "http://10.0.2.2:8080/api/v1",
  timeout: 15_000,
});

apiClient.interceptors.request.use(async (config: InternalAxiosRequestConfig) => {
  const token = await SecureStore.getItemAsync("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  config.headers["X-Correlation-Id"] = randomUUID();
  return config;
});

let isRefreshing = false;

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const status = error.response?.status;

    if (status === 401 && !isRefreshing) {
      isRefreshing = true;
      try {
        const refreshed = await useAuthStore.getState().refreshToken();
        isRefreshing = false;
        if (refreshed && error.config) {
          // Retry the original request once with the new token.
          return apiClient.request(error.config);
        }
      } catch {
        isRefreshing = false;
      }
      // Refresh also failed — clear the session and let the AuthGuard redirect to login.
      await useAuthStore.getState().logout();
    }

    if (status === 403) {
      // TODO: surface a "you don't have permission" toast — see components/ui/Toast.tsx.
      console.warn("Forbidden:", error.config?.url);
    }

    if (status !== undefined && status >= 500) {
      // TODO: surface a generic "something went wrong, try again" toast.
      console.error("Server error:", status, error.config?.url);
    }

    return Promise.reject(error);
  },
);
