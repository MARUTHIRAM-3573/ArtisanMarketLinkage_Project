import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useQuery } from "@tanstack/react-query";
import { StyleSheet, Text } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { StatusBadge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse, Order } from "@/types";

type Props = NativeStackScreenProps<AppStackParamList, "OrderDetail">;

/** /orders/:id — order detail + status history, per source §27's order tracking requirement. */
export function OrderDetailScreen({ route, navigation }: Props) {
  const { orderId } = route.params;

  const {
    data: order,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["order", orderId],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Order>>(
        endpoints.commerce.orderById(orderId),
      );
      return response.data.data;
    },
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  if (isError || !order) {
    return (
      <ScreenLayout title="Order">
        <Text style={styles.error}>Unable to load order.</Text>
      </ScreenLayout>
    );
  }

  return (
    <ScreenLayout title={`Order #${order.id.slice(0, 8)}`}>
      <Card>
        <StatusBadge status={order.status} />
        <Text style={styles.total}>
          {order.totalAmount} {order.currency}
        </Text>
        <Text style={styles.date}>Placed {new Date(order.createdAt).toLocaleDateString()}</Text>
      </Card>

      {order.status === "PENDING" ? (
        <Button
          label="Pay now"
          onPress={() => navigation.navigate("Payment", { orderId: order.id })}
          style={{ marginTop: 12 }}
        />
      ) : null}
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  total: {
    fontSize: 20,
    fontWeight: "700",
    color: "#0F172A",
    marginTop: 8,
  },
  date: {
    fontSize: 12,
    color: "#94A3B8",
    marginTop: 4,
  },
  error: {
    fontSize: 14,
    color: "#DC2626",
  },
});
