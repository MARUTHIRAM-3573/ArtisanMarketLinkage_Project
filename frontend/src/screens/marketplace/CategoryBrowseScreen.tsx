import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useQuery } from "@tanstack/react-query";
import { FlatList, Pressable, Text } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { EmptyState } from "@/components/shared/EmptyState";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Badge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse } from "@/types";

interface Category {
  id: string;
  name: string;
}

type Props = NativeStackScreenProps<AppStackParamList, "CategoryBrowse">;

/** /products (category navigation) — per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §2. */
export function CategoryBrowseScreen({ navigation }: Props) {
  const { data: categories, isLoading } = useQuery({
    queryKey: ["categories"],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Category[]>>(endpoints.catalog.categories());
      return response.data.data;
    },
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout title="Browse categories" scroll={false}>
      <FlatList
        data={categories ?? []}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <Pressable onPress={() => navigation.navigate("CategoryBrowse")}>
            <Card>
              <Text style={{ fontSize: 15, fontWeight: "600", color: "#0F172A" }}>{item.name}</Text>
              <Badge label="Browse" tone="info" />
            </Card>
          </Pressable>
        )}
        ListEmptyComponent={
          <EmptyState
            title="No categories found"
            description="Categories will appear here once sellers add products."
          />
        }
      />
    </ScreenLayout>
  );
}
