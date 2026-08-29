import type { BottomTabScreenProps } from "@react-navigation/bottom-tabs";
import type { CompositeScreenProps } from "@react-navigation/native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useQuery } from "@tanstack/react-query";
import { FlatList } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { EmptyState } from "@/components/shared/EmptyState";
import { ProductCard } from "@/components/shared/ProductCard";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Spinner } from "@/components/ui/Spinner";
import { useAuth } from "@/hooks/useAuth";
import type { AppStackParamList, MainTabParamList } from "@/navigation/types";
import type { ApiResponse, Product } from "@/types";

type Props = CompositeScreenProps<
  BottomTabScreenProps<MainTabParamList, "Home">,
  NativeStackScreenProps<AppStackParamList>
>;

/**
 * /home — content differs per persona per
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §1: "My Products" for
 * Artisan, "Marketplace Home" for Customer, "My Inquiries" summary link for
 * B2B Buyer. Admin has no defined dashboard (open question in that doc) so
 * an ADMIN-only user falls back to the marketplace view here.
 */
export function HomeScreen({ navigation }: Props) {
  const { hasRole } = useAuth();
  const isArtisan = hasRole("ARTISAN");

  const { data: products, isLoading } = useQuery({
    queryKey: ["home-products", isArtisan],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Product[]>>(endpoints.catalog.products());
      return response.data.data;
    },
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout title={isArtisan ? "My Products" : "Marketplace"} scroll={false}>
      <FlatList
        data={products ?? []}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <ProductCard
            product={item}
            onPress={() => navigation.navigate("ProductDetail", { productId: item.id })}
          />
        )}
        ListEmptyComponent={
          <EmptyState
            title={isArtisan ? "No products yet" : "No products found"}
            description={
              isArtisan
                ? "Start your first product with a voice recording and let AI draft the listing."
                : "Check back soon, or browse categories."
            }
            actionLabel={isArtisan ? "Record a new product" : undefined}
            onAction={isArtisan ? () => navigation.navigate("VoiceCapture") : undefined}
          />
        }
      />
    </ScreenLayout>
  );
}
