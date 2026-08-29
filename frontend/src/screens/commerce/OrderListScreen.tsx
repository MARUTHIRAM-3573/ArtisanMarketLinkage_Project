import type { BottomTabScreenProps } from "@react-navigation/bottom-tabs";
import type { CompositeScreenProps } from "@react-navigation/native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useQuery } from "@tanstack/react-query";
import { FlatList, Pressable, StyleSheet, Text, View } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { EmptyState } from "@/components/shared/EmptyState";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { StatusBadge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import type { AppStackParamList, MainTabParamList } from "@/navigation/types";
import type { ApiResponse, Order } from "@/types";

type Props = CompositeScreenProps<
  BottomTabScreenProps<MainTabParamList, "Orders">,
  NativeStackScreenProps<AppStackParamList>
>;

/** /orders — Order Tracking list, per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §2, §9. */
export function OrderListScreen({ navigation }: Props) {
  const {
    data: orders,
    isLoading,
    refetch,
    isFetching,
  } = useQuery({
    queryKey: ["orders"],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Order[]>>(endpoints.commerce.orders());
      return response.data.data;
    },
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout title="Orders" scroll={false}>
      <FlatList
        data={orders ?? []}
        keyExtractor={(item) => item.id}
        onRefresh={refetch}
        refreshing={isFetching}
        renderItem={({ item }) => (
          <Pressable onPress={() => navigation.navigate("OrderDetail", { orderId: item.id })}>
            <Card>
              <View style={styles.row}>
                <Text style={styles.orderNumber}>Order #{item.id.slice(0, 8)}</Text>
                <StatusBadge status={item.status} />
              </View>
              <Text style={styles.total}>
                {item.totalAmount} {item.currency}
              </Text>
            </Card>
          </Pressable>
        )}
        ListEmptyComponent={
          <EmptyState title="No orders yet" description="Your placed orders will show up here." />
        }
      />
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 6,
  },
  orderNumber: {
    fontSize: 14,
    fontWeight: "700",
    color: "#0F172A",
  },
  total: {
    fontSize: 13,
    color: "#64748B",
  },
});
