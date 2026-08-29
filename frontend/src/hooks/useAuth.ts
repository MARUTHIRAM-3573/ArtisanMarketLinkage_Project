import { useAuthStore } from "@/store/auth";

/**
 * Thin convenience wrapper around the Zustand auth store so screens/guards
 * don't import the store directly. Also centralizes role-check helpers used
 * by navigation/index.tsx's RoleGuard and by screens that show/hide actions
 * based on the active user's roles (e.g. an ARTISAN-only "Publish"
 * button on a product owned by someone else should never render, but the
 * backend is still the source of truth for authorization — see
 * docs/architecture/08_SECURITY_AND_VAULT.md Part B).
 */
export function useAuth() {
  const { user, isAuthenticated, isLoading, login, logout, setUser } = useAuthStore();

  const hasRole = (role: string) => Boolean(user?.roles?.includes(role as never));

  return { user, isAuthenticated, isLoading, login, logout, setUser, hasRole };
}
