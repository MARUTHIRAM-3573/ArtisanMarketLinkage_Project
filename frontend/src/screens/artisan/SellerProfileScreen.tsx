import { zodResolver } from "@hookform/resolvers/zod";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import { z } from "zod";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { StatusBadge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Spinner } from "@/components/ui/Spinner";
import { useToastStore } from "@/components/ui/Toast";
import { useAuth } from "@/hooks/useAuth";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse, Seller } from "@/types";

const schema = z.object({
  businessName: z.string().min(2, "Business name is required"),
  bio: z.string().optional(),
  region: z.string().optional(),
});

type FormValues = z.infer<typeof schema>;

type Props = NativeStackScreenProps<AppStackParamList, "SellerProfile">;

/** /seller/profile — create/edit the artisan's seller profile. */
export function SellerProfileScreen(_props: Props) {
  const { user } = useAuth();

  const { data: seller, isLoading } = useQuery({
    queryKey: ["seller-profile", user?.id],
    enabled: Boolean(user?.id),
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<Seller>>(endpoints.sellers.byId(user!.id));
      return response.data.data;
    },
  });

  const {
    control,
    handleSubmit,
    reset,
    formState: { isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  useEffect(() => {
    if (seller) {
      reset({
        businessName: seller.businessName,
        bio: seller.bio ?? "",
        region: seller.region ?? "",
      });
    }
  }, [seller, reset]);

  const mutation = useMutation({
    mutationFn: (values: FormValues) => apiClient.post(endpoints.sellers.create(), values),
    onSuccess: () => useToastStore.getState().show("Seller profile saved", "success"),
    onError: () => useToastStore.getState().show("Could not save profile", "error"),
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  return (
    <ScreenLayout title="Seller profile">
      {seller ? <StatusBadge status={seller.verified ? "APPROVED" : "PENDING"} /> : null}
      <Controller
        control={control}
        name="businessName"
        render={({ field: { onChange, onBlur, value } }) => (
          <Input label="Business name" value={value} onChangeText={onChange} onBlur={onBlur} />
        )}
      />
      <Controller
        control={control}
        name="region"
        render={({ field: { onChange, onBlur, value } }) => (
          <Input label="Region" value={value} onChangeText={onChange} onBlur={onBlur} />
        )}
      />
      <Controller
        control={control}
        name="bio"
        render={({ field: { onChange, onBlur, value } }) => (
          <Input
            label="About your craft"
            value={value}
            onChangeText={onChange}
            onBlur={onBlur}
            multiline
            numberOfLines={4}
          />
        )}
      />
      <Button
        label="Save profile"
        onPress={handleSubmit((v) => mutation.mutate(v))}
        loading={isSubmitting || mutation.isPending}
      />
    </ScreenLayout>
  );
}
