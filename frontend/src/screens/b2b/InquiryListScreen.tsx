import type { BottomTabScreenProps } from "@react-navigation/bottom-tabs";
import type { CompositeScreenProps } from "@react-navigation/native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useQuery } from "@tanstack/react-query";
import { FlatList, Pressable, Text } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { EmptyState } from "@/components/shared/EmptyState";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { StatusBadge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import { useAuth } from "@/hooks/useAuth";
import type { AppStackParamList, MainTabParamList } from "@/navigation/types";
import type { ApiResponse, B2bInquiry } from "@/types";

type Props = CompositeScreenProps<
  BottomTabScreenProps<MainTabParamList, "B2B">,
  NativeStackScreenProps<AppStackParamList>
>;

/**
 * /b2b/inquiries — "My Inquiries" for a B2B Buyer, "incoming inquiries" for
 * a seller, per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §1 and §9.
 */
export function InquiryListScreen({ navigation }: Props) {
  const { hasRole } = useAuth();
  const isSeller = hasRole("ARTISAN");

  const {
    data: inquiries,
    isLoading,
    refetch,
    isFetching,
  } = useQuery({
    queryKey: ["b2b-inquiries"],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<B2bInquiry[]>>(endpoints.b2b.inquiries());
      return response.data.data;
    },
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout title={isSeller ? "Incoming inquiries" : "My inquiries"} scroll={false}>
      <FlatList
        data={inquiries ?? []}
        keyExtractor={(item) => item.id}
        onRefresh={refetch}
        refreshing={isFetching}
        renderItem={({ item }) => (
          <Pressable onPress={() => navigation.navigate("InquiryDetail", { inquiryId: item.id })}>
            <Card>
              <Text style={{ fontSize: 14, color: "#0F172A", marginBottom: 6 }} numberOfLines={2}>
                {item.message}
              </Text>
              <StatusBadge status={item.status} />
            </Card>
          </Pressable>
        )}
        ListEmptyComponent={
          <EmptyState title={isSeller ? "No incoming inquiries" : "No inquiries yet"} />
        }
      />
    </ScreenLayout>
  );
}
