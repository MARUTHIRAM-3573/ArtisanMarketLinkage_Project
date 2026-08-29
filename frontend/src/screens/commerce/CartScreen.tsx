import type { BottomTabScreenProps } from "@react-navigation/bottom-tabs";
import type { CompositeScreenProps } from "@react-navigation/native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { FlatList, StyleSheet, Text, View } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { EmptyState } from "@/components/shared/EmptyState";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList, MainTabParamList } from "@/navigation/types";
import type { ApiResponse } from "@/types";

interface CartItem {
  id: string;
  productId: string;
  productTitle: string;
  quantity: number;
  unitPrice: number;
  currency: string;
}

type Props = CompositeScreenProps<
  BottomTabScreenProps<MainTabParamList, "Cart">,
  NativeStackScreenProps<AppStackParamList>
>;

/**
 * /cart — per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §2 and §9
 * ("Your cart is empty" with a browse CTA; stock-conflict errors surface at
 * checkout rather than here).
 */
export function CartScreen({ navigation }: Props) {
  const queryClient = useQueryClient();

  const { data: cart, isLoading } = useQuery({
    queryKey: ["cart"],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<{ items: CartItem[] }>>(
        endpoints.commerce.cart(),
      );
      return response.data.data;
    },
  });

  const removeMutation = useMutation({
    mutationFn: (itemId: string) => apiClient.delete(endpoints.commerce.cartItem(itemId)),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["cart"] }),
    onError: () => useToastStore.getState().show("Could not update cart", "error"),
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  const items = cart?.items ?? [];
  const total = items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);

  return (
    <ScreenLayout title="Cart" scroll={false}>
      <FlatList
        data={items}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <Card>
            <View style={styles.row}>
              <Text style={styles.title} numberOfLines={1}>
                {item.productTitle}
              </Text>
              <Text style={styles.price}>
                {item.quantity} × {item.unitPrice} {item.currency}
              </Text>
            </View>
            <Button
              label="Remove"
              variant="ghost"
              size="sm"
              onPress={() => removeMutation.mutate(item.id)}
            />
          </Card>
        )}
        ListEmptyComponent={
          <EmptyState
            title="Your cart is empty"
            description="Browse the marketplace to find something you like."
            actionLabel="Browse products"
            onAction={() => navigation.navigate("Home")}
          />
        }
      />
      {items.length > 0 ? (
        <View style={styles.footer}>
          <Text style={styles.totalLabel}>Total: {total.toFixed(2)}</Text>
          <Button label="Checkout" onPress={() => navigation.navigate("Checkout")} />
        </View>
      ) : null}
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  row: {
    marginBottom: 8,
  },
  title: {
    fontSize: 15,
    fontWeight: "700",
    color: "#0F172A",
  },
  price: {
    fontSize: 13,
    color: "#64748B",
    marginTop: 2,
  },
  footer: {
    padding: 16,
    borderTopWidth: 1,
    borderColor: "#E2E8F0",
    backgroundColor: "#FFFFFF",
  },
  totalLabel: {
    fontSize: 16,
    fontWeight: "700",
    color: "#0F172A",
    marginBottom: 10,
  },
});
