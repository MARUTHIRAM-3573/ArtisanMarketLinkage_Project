import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useQuery } from "@tanstack/react-query";
import { FlatList } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { EmptyState } from "@/components/shared/EmptyState";
import { ProductCard } from "@/components/shared/ProductCard";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Button } from "@/components/ui/Button";
import { Spinner } from "@/components/ui/Spinner";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse, Product } from "@/types";

type Props = NativeStackScreenProps<AppStackParamList, "SellerProducts">;

/**
 * /seller/products — product management list (Artisan persona), per
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §2. The "My Products"
 * home tab links here for full management (edit, pricing, inventory).
 */
export function ProductListScreen({ navigation }: Props) {
  const {
    data: products,
    isLoading,
    refetch,
    isFetching,
  } = useQuery({
    queryKey: ["seller-products"],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Product[]>>(endpoints.catalog.products());
      return response.data.data;
    },
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout
      title="My products"
      scroll={false}
      headerRight={
        <Button label="+ New" size="sm" onPress={() => navigation.navigate("VoiceCapture")} />
      }
    >
      <FlatList
        data={products ?? []}
        keyExtractor={(item) => item.id}
        onRefresh={refetch}
        refreshing={isFetching}
        renderItem={({ item }) => (
          <ProductCard
            product={item}
            onPress={() => navigation.navigate("ProductForm", { productId: item.id })}
          />
        )}
        ListEmptyComponent={
          <EmptyState
            title="No products yet"
            description="Record your product to get started."
            actionLabel="Record a new product"
            onAction={() => navigation.navigate("VoiceCapture")}
          />
        }
      />
    </ScreenLayout>
  );
}
