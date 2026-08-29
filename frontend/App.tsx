import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { NavigationContainer } from "@react-navigation/native";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

import { RootNavigator } from "@/navigation";
import { ErrorBoundary } from "@/components/shared/ErrorBoundary";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 30_000,
    },
  },
});

/**
 * Root component: wraps the whole app in the providers every screen needs —
 * React Query for server-state (including AI job polling, per
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §5), navigation, and a
 * top-level error boundary.
 */
export default function App() {
  return (
    <SafeAreaProvider>
      <ErrorBoundary>
        <QueryClientProvider client={queryClient}>
          <NavigationContainer>
            <RootNavigator />
          </NavigationContainer>
          <StatusBar style="auto" />
        </QueryClientProvider>
      </ErrorBoundary>
    </SafeAreaProvider>
  );
}
