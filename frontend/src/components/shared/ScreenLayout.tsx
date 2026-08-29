import { ReactNode } from "react";
import { RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

interface ScreenLayoutProps {
  title?: string;
  children: ReactNode;
  scroll?: boolean;
  onRefresh?: () => void;
  refreshing?: boolean;
  headerRight?: ReactNode;
}

/**
 * Standard screen chrome: safe-area padding, optional title bar, optional
 * pull-to-refresh (used by list screens backed by React Query, whose
 * `refetch`/`isFetching` wire directly into onRefresh/refreshing). Every
 * screen under src/screens/ wraps its content in this so spacing and the
 * header stay consistent app-wide.
 */
export function ScreenLayout({
  title,
  children,
  scroll = true,
  onRefresh,
  refreshing = false,
  headerRight,
}: ScreenLayoutProps) {
  const content = scroll ? (
    <ScrollView
      contentContainerStyle={styles.scrollContent}
      refreshControl={
        onRefresh ? <RefreshControl refreshing={refreshing} onRefresh={onRefresh} /> : undefined
      }
    >
      {children}
    </ScrollView>
  ) : (
    <View style={styles.flexContent}>{children}</View>
  );

  return (
    <SafeAreaView style={styles.safeArea} edges={["top", "left", "right"]}>
      {title ? (
        <View style={styles.header}>
          <Text style={styles.title}>{title}</Text>
          {headerRight}
        </View>
      ) : null}
      {content}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: "#F8FAFC",
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderColor: "#E2E8F0",
    backgroundColor: "#FFFFFF",
  },
  title: {
    fontSize: 20,
    fontWeight: "700",
    color: "#0F172A",
  },
  scrollContent: {
    padding: 16,
    flexGrow: 1,
  },
  flexContent: {
    flex: 1,
    padding: 16,
  },
});
