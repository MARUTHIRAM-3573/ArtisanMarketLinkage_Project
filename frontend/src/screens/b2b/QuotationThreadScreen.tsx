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
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse } from "@/types";

interface Quotation {
  id: string;
  amount: number;
  currency: string;
  notes?: string;
  status: string;
}

type Props = NativeStackScreenProps<AppStackParamList, "QuotationThread">;

/**
 * /b2b/inquiries/:id (quotation thread) — the buyer accepts a quotation
 * here, which per docs/architecture/06_COMMUNICATION_WORKFLOWS.md creates a
 * b2b.purchase_orders row referencing a commerce.orders row (the deferred
 * FK reconciled in database/migrations/V010__create_b2b.sql).
 */
export function QuotationThreadScreen({ route, navigation }: Props) {
  const { inquiryId } = route.params;
  const queryClient = useQueryClient();

  const { data: quotations, isLoading } = useQuery({
    queryKey: ["quotations", inquiryId],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Quotation[]>>(
        endpoints.b2b.quotations(inquiryId),
      );
      return response.data.data;
    },
  });

  const acceptMutation = useMutation({
    mutationFn: (quotationId: string) => apiClient.post(endpoints.b2b.acceptQuotation(quotationId)),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ["quotations", inquiryId] });
      useToastStore.getState().show("Quotation accepted — purchase order created", "success");
      const purchaseOrderId = (response.data as ApiResponse<{ id: string }>)?.data?.id;
      if (purchaseOrderId) {
        navigation.navigate("OrderDetail", { orderId: purchaseOrderId });
      }
    },
    onError: () => useToastStore.getState().show("Could not accept quotation", "error"),
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout title="Quotations" scroll={false}>
      <FlatList
        data={quotations ?? []}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <Card>
            <Text style={styles.amount}>
              {item.amount} {item.currency}
            </Text>
            {item.notes ? <Text style={styles.notes}>{item.notes}</Text> : null}
            <View style={styles.footer}>
              <Text style={styles.status}>{item.status}</Text>
              {item.status === "PENDING" ? (
                <Button
                  label="Accept"
                  size="sm"
                  onPress={() => acceptMutation.mutate(item.id)}
                  loading={acceptMutation.isPending}
                />
              ) : null}
            </View>
          </Card>
        )}
        ListEmptyComponent={
          <EmptyState
            title="No quotations yet"
            description="The seller hasn't sent a quotation for this inquiry yet."
          />
        }
      />
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  amount: {
    fontSize: 18,
    fontWeight: "700",
    color: "#0F172A",
  },
  notes: {
    fontSize: 13,
    color: "#64748B",
    marginTop: 4,
  },
  footer: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginTop: 10,
  },
  status: {
    fontSize: 12,
    color: "#94A3B8",
    textTransform: "uppercase",
  },
});
