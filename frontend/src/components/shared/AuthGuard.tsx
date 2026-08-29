import { ReactNode, useEffect } from "react";
import { StyleSheet, Text, View } from "react-native";

import { Spinner } from "@/components/ui/Spinner";
import { useAuth } from "@/hooks/useAuth";
import type { Role } from "@/types";

interface AuthGateProps {
  children: ReactNode;
}

/**
 * Runs the auth hydration check (SecureStore token -> GET /auth/me) once on
 * mount, then renders children once resolved. navigation/index.tsx uses
 * `isAuthenticated` from useAuth() to pick between the auth stack and the
 * role-aware app stack — this component only owns the loading gate itself,
 * per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §6 auth flow.
 */
export function AuthGate({ children }: AuthGateProps) {
  const { isLoading } = useAuth();
  const hydrate = useAuth().user === null && isLoading;

  useEffect(() => {
    // Hydration is kicked off by RootNavigator (see navigation/index.tsx)
    // before this gate mounts; this effect is a no-op placeholder kept here
    // so future logic (e.g. deep-link resolution) has an obvious home.
  }, []);

  if (hydrate) {
    return <Spinner fullscreen label="Loading your account…" />;
  }

  return <>{children}</>;
}

interface RoleGuardProps {
  allow: Role[];
  children: ReactNode;
}

/**
 * Screen-level guard for role-restricted actions/screens (e.g. an
 * ARTISAN-only pricing review, or a B2B_BUYER-only inquiry form).
 * This is a UX convenience only — the backend's `@PreAuthorize` checks
 * (backend/app/config/SecurityConfig.java) are the actual authorization
 * boundary, per docs/architecture/08_SECURITY_AND_VAULT.md Part B.
 */
export function RoleGuard({ allow, children }: RoleGuardProps) {
  const { user } = useAuth();
  const authorized = Boolean(user?.roles?.some((role) => allow.includes(role)));

  if (!authorized) {
    return (
      <View style={styles.container}>
        <Text style={styles.text}>You don't have access to this screen.</Text>
      </View>
    );
  }

  return <>{children}</>;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 24,
  },
  text: {
    fontSize: 14,
    color: "#64748B",
    textAlign: "center",
  },
});
