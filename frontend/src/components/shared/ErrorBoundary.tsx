import { Component, ErrorInfo, ReactNode } from "react";
import { StyleSheet, Text, View } from "react-native";

import { Button } from "@/components/ui/Button";

interface ErrorBoundaryProps {
  children: ReactNode;
}

interface ErrorBoundaryState {
  error: Error | null;
}

/**
 * Top-level React error boundary (mounted once in App.tsx, above the
 * navigation tree). Catches render-time exceptions that would otherwise
 * crash the whole app and shows a recoverable "Something went wrong" screen
 * instead. This does NOT catch API/network errors — those are handled per
 * screen via React Query's `isError` state and the api/client.ts
 * interceptor; this only guards against unexpected render failures.
 */
export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  state: ErrorBoundaryState = { error: null };

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { error };
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    // eslint-disable-next-line no-console
    console.error("Unhandled render error:", error, info.componentStack);
  }

  reset = () => this.setState({ error: null });

  render() {
    if (this.state.error) {
      return (
        <View style={styles.container}>
          <Text style={styles.title}>Something went wrong</Text>
          <Text style={styles.message}>
            The app hit an unexpected error. You can try again, and if it keeps happening please let
            us know what you were doing.
          </Text>
          {__DEV__ ? <Text style={styles.debug}>{this.state.error.message}</Text> : null}
          <Button label="Try again" onPress={this.reset} style={{ marginTop: 16 }} />
        </View>
      );
    }
    return this.props.children;
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 24,
    backgroundColor: "#F8FAFC",
  },
  title: {
    fontSize: 18,
    fontWeight: "700",
    color: "#0F172A",
    marginBottom: 8,
  },
  message: {
    fontSize: 14,
    color: "#64748B",
    textAlign: "center",
  },
  debug: {
    marginTop: 12,
    fontSize: 12,
    color: "#DC2626",
    textAlign: "center",
  },
});
