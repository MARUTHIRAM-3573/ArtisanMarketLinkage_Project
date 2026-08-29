import { zodResolver } from "@hookform/resolvers/zod";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import { StyleSheet, View } from "react-native";
import { z } from "zod";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Spinner } from "@/components/ui/Spinner";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse, Product } from "@/types";

const schema = z.object({
  title: z.string().min(3, "Title is required"),
  description: z.string().optional(),
});

type FormValues = z.infer<typeof schema>;

type Props = NativeStackScreenProps<AppStackParamList, "ProductForm">;

/**
 * /seller/products (create/edit) — manual product form, used both as a
 * standalone entry point and as the "manual entry fallback" referenced in
 * docs/architecture/12_FAILURE_RESILIENCE_PLAN.md §4 when the AI voice flow
 * times out.
 */
export function ProductFormScreen({ route, navigation }: Props) {
  const productId = route.params?.productId;
  const isEdit = Boolean(productId);
  const queryClient = useQueryClient();

  const { data: existing, isLoading } = useQuery({
    queryKey: ["product", productId],
    enabled: isEdit,
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Product>>(
        endpoints.catalog.productById(productId!),
      );
      return response.data.data;
    },
  });

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  useEffect(() => {
    if (existing) {
      reset({ title: existing.title, description: existing.description ?? "" });
    }
  }, [existing, reset]);

  const mutation = useMutation({
    mutationFn: (values: FormValues) =>
      isEdit
        ? apiClient.put(endpoints.catalog.productById(productId!), values)
        : apiClient.post(endpoints.catalog.products(), values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["seller-products"] });
      useToastStore.getState().show(isEdit ? "Product updated" : "Product created", "success");
      navigation.goBack();
    },
    onError: () => useToastStore.getState().show("Could not save product", "error"),
  });

  if (isEdit && isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout title={isEdit ? "Edit product" : "New product"}>
      <View>
        <Controller
          control={control}
          name="title"
          render={({ field: { onChange, onBlur, value } }) => (
            <Input
              label="Title"
              value={value}
              onChangeText={onChange}
              onBlur={onBlur}
              error={errors.title?.message}
            />
          )}
        />
        <Controller
          control={control}
          name="description"
          render={({ field: { onChange, onBlur, value } }) => (
            <Input
              label="Description"
              value={value}
              onChangeText={onChange}
              onBlur={onBlur}
              multiline
              numberOfLines={4}
              style={styles.multiline}
            />
          )}
        />
        <Button
          label="Save product"
          onPress={handleSubmit((v) => mutation.mutate(v))}
          loading={isSubmitting || mutation.isPending}
        />
        {isEdit ? (
          <Button
            label="Manage pricing"
            variant="secondary"
            onPress={() => navigation.navigate("PricingReview", { productId: productId! })}
            style={{ marginTop: 8 }}
          />
        ) : null}
        {isEdit ? (
          <Button
            label="Image studio"
            variant="secondary"
            onPress={() => navigation.navigate("ImageStudio", { productId: productId! })}
            style={{ marginTop: 8 }}
          />
        ) : null}
      </View>
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  multiline: {
    minHeight: 90,
    textAlignVertical: "top",
  },
});
