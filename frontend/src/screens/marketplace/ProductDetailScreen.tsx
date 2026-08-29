import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useQuery } from "@tanstack/react-query";
import { StyleSheet, Text, View } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { StatusBadge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Spinner } from "@/components/ui/Spinner";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse, Product } from "@/types";

type Props = NativeStackScreenProps<AppStackParamList, "ProductDetail">;

/** /products/:id — per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §2. */
export function ProductDetailScreen({ route }: Props) {
  const { productId } = route.params;

  const {
    data: product,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["product", productId],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Product>>(
        endpoints.catalog.productById(productId),
      );
      return response.data.data;
    },
  });

  const addToCart = async () => {
    try {
      await apiClient.post(endpoints.commerce.cart(), { productId, quantity: 1 });
      useToastStore.getState().show("Added to cart", "success");
    } catch {
      useToastStore.getState().show("Could not add to cart", "error");
    }
  };

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  if (isError || !product) {
    return (
      <ScreenLayout title="Product">
        <Text style={styles.error}>Product unavailable.</Text>
      </ScreenLayout>
    );
  }

  return (
    <ScreenLayout title={product.title}>
      <View style={styles.thumbPlaceholder} />
      <StatusBadge status={product.status} />
      {product.description ? <Text style={styles.description}>{product.description}</Text> : null}
      <Button label="Add to cart" onPress={addToCart} style={{ marginTop: 20 }} />
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  thumbPlaceholder: {
    width: "100%",
    height: 220,
    borderRadius: 12,
    backgroundColor: "#E2E8F0",
    marginBottom: 16,
  },
  description: {
    marginTop: 12,
    fontSize: 14,
    color: "#334155",
    lineHeight: 20,
  },
  error: {
    fontSize: 14,
    color: "#DC2626",
  },
});
