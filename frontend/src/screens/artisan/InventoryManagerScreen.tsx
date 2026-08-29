import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Text } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { EmptyState } from "@/components/shared/EmptyState";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Button } from "@/components/ui/Button";
import { Spinner } from "@/components/ui/Spinner";
import { Table, type TableColumn } from "@/components/ui/Table";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse, InventoryRecord } from "@/types";

type Props = NativeStackScreenProps<AppStackParamList, "InventoryManager">;

/**
 * /seller/inventory — inventory across all of the seller's SKUs, per
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §9. A quick "+1 / -1"
 * adjustment posts an inventory movement (inventory.inventory_movements),
 * matching the audit-trail design in 04_DATA_MODEL_ERD.md.
 */
export function InventoryManagerScreen({ route }: Props) {
  const queryClient = useQueryClient();
  // NOTE: source has no "list all inventory for this seller" endpoint —
  // per-SKU inventory is fetched individually (endpoints.inventory.bySku).
  // Since this screen needs an aggregate view, it is scoped here to the SKU
  // ids the seller navigated in with (PROPOSED — no aggregate endpoint
  // exists in 05_API_CONTRACTS.md).
  const skuIds = (route.params as { skuIds?: string[] } | undefined)?.skuIds ?? [];

  const { data: records, isLoading } = useQuery({
    queryKey: ["inventory", skuIds],
    enabled: skuIds.length > 0,
    queryFn: async () => {
      const responses = await Promise.all(
        skuIds.map((id) =>
          apiClient.get<ApiResponse<InventoryRecord>>(endpoints.inventory.bySku(id)),
        ),
      );
      return responses.map((r) => r.data.data);
    },
  });

  const adjustMutation = useMutation({
    mutationFn: ({ skuId, delta }: { skuId: string; delta: number }) =>
      apiClient.post(endpoints.inventory.movements(skuId), {
        quantityDelta: delta,
        reason: "MANUAL_ADJUSTMENT",
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["inventory", skuIds] });
      useToastStore.getState().show("Inventory updated", "success");
    },
    onError: () => useToastStore.getState().show("Could not update inventory", "error"),
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  const columns: TableColumn<InventoryRecord>[] = [
    { key: "skuId", header: "SKU", width: 140, render: (row) => row.skuId },
    { key: "qty", header: "On hand", width: 90, render: (row) => String(row.quantityOnHand) },
    {
      key: "threshold",
      header: "Reorder at",
      width: 90,
      render: (row) => String(row.reorderThreshold),
    },
    {
      key: "actions",
      header: "Adjust",
      width: 150,
      render: (row) => (
        <>
          <Button
            label="-1"
            size="sm"
            variant="secondary"
            onPress={() => adjustMutation.mutate({ skuId: row.skuId, delta: -1 })}
          />
          <Button
            label="+1"
            size="sm"
            variant="secondary"
            onPress={() => adjustMutation.mutate({ skuId: row.skuId, delta: 1 })}
          />
        </>
      ),
    },
  ];

  return (
    <ScreenLayout title="Inventory" scroll={false}>
      {!records?.length ? (
        <EmptyState
          title="No inventory to show"
          description="Inventory appears here once your products have SKUs."
        />
      ) : (
        <Table data={records} columns={columns} keyExtractor={(row) => row.id} />
      )}
      <Text style={{ marginTop: 12, fontSize: 11, color: "#94A3B8" }}>
        Reorder threshold alerts follow docs/architecture/04_DATA_MODEL_ERD.md's inventory schema.
      </Text>
    </ScreenLayout>
  );
}
