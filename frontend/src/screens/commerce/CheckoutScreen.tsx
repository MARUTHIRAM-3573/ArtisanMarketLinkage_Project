import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { StyleSheet, Text } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse } from "@/types";

interface Address {
  id: string;
  line1: string;
  city: string;
}

type Props = NativeStackScreenProps<AppStackParamList, "Checkout">;

/**
 * /checkout — reviews the cart, picks a saved address, and submits
 * POST /checkout, which creates a commerce.orders row. On a stock conflict,
 * the per-item error surfaces here per
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §9.
 */
export function CheckoutScreen({ navigation }: Props) {
  const [checkoutError, setCheckoutError] = useState<string | null>(null);
  const [selectedAddressId, setSelectedAddressId] = useState<string | null>(null);

  const { data: addresses, isLoading } = useQuery({
    queryKey: ["addresses"],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Address[]>>(endpoints.auth.addresses());
      return response.data.data;
    },
  });

  const checkoutMutation = useMutation({
    mutationFn: () =>
      apiClient.post(endpoints.commerce.checkout(), { shippingAddressId: selectedAddressId }),
    onSuccess: (response) => {
      const order = (response.data as ApiResponse<{ id: string }>)?.data;
      useToastStore.getState().show("Order placed", "success");
      if (order?.id) {
        navigation.replace("OrderDetail", { orderId: order.id });
      }
    },
    onError: () =>
      setCheckoutError("Some items in your cart are no longer in stock. Please review your cart."),
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout title="Checkout">
      <Text style={styles.sectionLabel}>Shipping address</Text>
      {(addresses ?? []).map((address) => (
        <Card
          key={address.id}
          style={
            selectedAddressId === address.id
              ? { ...styles.addressCard, ...styles.addressCardSelected }
              : styles.addressCard
          }
        >
          <Text onPress={() => setSelectedAddressId(address.id)} style={styles.addressText}>
            {address.line1}, {address.city}
          </Text>
        </Card>
      ))}

      {checkoutError ? <Text style={styles.errorText}>{checkoutError}</Text> : null}

      <Button
        label="Place order"
        onPress={() => checkoutMutation.mutate()}
        loading={checkoutMutation.isPending}
        disabled={!selectedAddressId}
        style={{ marginTop: 16 }}
      />
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  sectionLabel: {
    fontSize: 13,
    fontWeight: "600",
    color: "#334155",
    marginBottom: 8,
  },
  addressCard: {
    borderColor: "#E2E8F0",
  },
  addressCardSelected: {
    borderColor: "#0F172A",
  },
  addressText: {
    fontSize: 14,
    color: "#0F172A",
  },
  errorText: {
    color: "#DC2626",
    fontSize: 13,
    marginTop: 8,
  },
});
