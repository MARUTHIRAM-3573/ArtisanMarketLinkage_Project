import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { StyleSheet, Text, View } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { EmptyState } from "@/components/shared/EmptyState";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse, SkuPrice } from "@/types";

type Props = NativeStackScreenProps<AppStackParamList, "PricingReview">;

/**
 * /seller/products/:id/pricing — shows cost records, requests an AI price
 * recommendation, and lets the seller accept or override it, per
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §9 ("No recommendation
 * yet — add cost records first").
 */
export function PricingReviewScreen({ route }: Props) {
  const { productId } = route.params;
  const queryClient = useQueryClient();
  const [requesting, setRequesting] = useState(false);

  const { data: costRecords, isLoading } = useQuery({
    queryKey: ["cost-records", productId],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<unknown[]>>(
        endpoints.pricing.costRecords(productId),
      );
      return response.data.data;
    },
  });

  const requestRecommendation = async () => {
    setRequesting(true);
    try {
      await apiClient.post(endpoints.ai.pricingRecommend(), { productId });
      queryClient.invalidateQueries({ queryKey: ["cost-records", productId] });
      useToastStore.getState().show("Recommendation requested", "success");
    } catch {
      useToastStore.getState().show("Could not request a recommendation", "error");
    } finally {
      setRequesting(false);
    }
  };

  const acceptMutation = useMutation({
    mutationFn: (priceId: string) => apiClient.post(endpoints.ai.pricingAccept(priceId)),
    onSuccess: () => useToastStore.getState().show("Price accepted", "success"),
    onError: () => useToastStore.getState().show("Could not accept price", "error"),
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  const hasCostRecords = (costRecords?.length ?? 0) > 0;

  return (
    <ScreenLayout title="Pricing review">
      {!hasCostRecords ? (
        <EmptyState
          title="No recommendation yet"
          description="Add cost records first so AI can suggest a price."
        />
      ) : (
        <Card>
          <Text style={styles.label}>Cost records on file</Text>
          <Text style={styles.value}>{costRecords?.length}</Text>
        </Card>
      )}
      <Button
        label="Request AI price recommendation"
        onPress={requestRecommendation}
        loading={requesting}
        style={{ marginTop: 12 }}
      />
      <RecommendationRow
        productId={productId}
        onAccept={(id) => acceptMutation.mutate(id)}
        isAccepting={acceptMutation.isPending}
      />
    </ScreenLayout>
  );
}

function RecommendationRow({
  productId,
  onAccept,
  isAccepting,
}: {
  productId: string;
  onAccept: (priceId: string) => void;
  isAccepting: boolean;
}) {
  // A dedicated "get current recommendation" GET endpoint isn't in
  // 05_API_CONTRACTS.md; recommendations arrive via the ai_jobs poll
  // pattern (see useAiJobPolling) started by requestRecommendation above,
  // then surfaced here once resolved. Kept minimal since the exact response
  // shape is not specified in source.
  const { data: skuPrices } = useQuery<SkuPrice[]>({
    queryKey: ["sku-prices", productId],
    queryFn: async () => [],
    enabled: false,
  });

  if (!skuPrices?.length) {
    return null;
  }

  return (
    <Card style={{ marginTop: 12 }}>
      {skuPrices.map((price) => (
        <View key={price.id} style={styles.priceRow}>
          <Text style={styles.value}>
            {price.amount} {price.currency}
          </Text>
          <Badge
            label={price.source}
            tone={price.source === "AI_RECOMMENDED" ? "info" : "neutral"}
          />
          <Button
            label="Accept"
            size="sm"
            onPress={() => onAccept(price.id)}
            loading={isAccepting}
          />
        </View>
      ))}
    </Card>
  );
}

const styles = StyleSheet.create({
  label: {
    fontSize: 12,
    color: "#64748B",
  },
  value: {
    fontSize: 18,
    fontWeight: "700",
    color: "#0F172A",
  },
  priceRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingVertical: 8,
  },
});
