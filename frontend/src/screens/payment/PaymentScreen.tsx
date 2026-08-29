import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { StyleSheet, Text, View } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { StatusBadge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse, Order, Payment } from "@/types";

type Props = NativeStackScreenProps<AppStackParamList, "Payment">;

/**
 * /payment/:orderId — initiates payment via the provider-agnostic
 * PaymentGatewayAdapter (backend/modules/payment/.../gateway), currently
 * backed by MockPaymentGatewayAdapter per ADR (principle #14), per
 * docs/architecture/06_COMMUNICATION_WORKFLOWS.md payment flow.
 */
export function PaymentScreen({ route, navigation }: Props) {
  const { orderId } = route.params;
  const [payment, setPayment] = useState<Payment | null>(null);

  const { data: order, isLoading } = useQuery({
    queryKey: ["order", orderId],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Order>>(
        endpoints.commerce.orderById(orderId),
      );
      return response.data.data;
    },
  });

  const initiateMutation = useMutation({
    mutationFn: () => apiClient.post<ApiResponse<Payment>>(endpoints.payment.initiate(orderId)),
    onSuccess: (response) => {
      const result = response.data.data;
      setPayment(result);
      if (result.status === "SUCCEEDED") {
        useToastStore.getState().show("Payment successful", "success");
        navigation.navigate("OrderDetail", { orderId });
      } else if (result.status === "FAILED") {
        useToastStore.getState().show("Payment failed — please try again", "error");
      }
    },
    onError: () => useToastStore.getState().show("Could not start payment", "error"),
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout title="Payment">
      <Card>
        <Text style={styles.label}>Amount due</Text>
        <Text style={styles.amount}>
          {order?.totalAmount} {order?.currency}
        </Text>
      </Card>

      {payment ? (
        <View style={styles.statusRow}>
          <StatusBadge status={payment.status} />
        </View>
      ) : null}

      <Button
        label={payment?.status === "FAILED" ? "Retry payment" : "Pay now (mock gateway)"}
        onPress={() => initiateMutation.mutate()}
        loading={initiateMutation.isPending}
        style={{ marginTop: 16 }}
      />
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  label: {
    fontSize: 12,
    color: "#64748B",
  },
  amount: {
    fontSize: 22,
    fontWeight: "800",
    color: "#0F172A",
    marginTop: 4,
  },
  statusRow: {
    marginTop: 12,
  },
});
