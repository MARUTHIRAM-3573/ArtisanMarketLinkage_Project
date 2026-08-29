import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useQuery } from "@tanstack/react-query";
import { StyleSheet, Text } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { StatusBadge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Spinner } from "@/components/ui/Spinner";
import type { AppStackParamList } from "@/navigation/types";
import type { ApiResponse, B2bInquiry } from "@/types";

type Props = NativeStackScreenProps<AppStackParamList, "InquiryDetail">;

/** /b2b/inquiries/:id — inquiry detail, links into the quotation thread. */
export function InquiryDetailScreen({ route, navigation }: Props) {
  const { inquiryId } = route.params;

  const { data: inquiry, isLoading } = useQuery({
    queryKey: ["inquiry", inquiryId],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<B2bInquiry>>(
        endpoints.b2b.inquiryById(inquiryId),
      );
      return response.data.data;
    },
  });

  if (isLoading) {
    return <Spinner fullscreen />;
  }

  if (!inquiry) {
    return (
      <ScreenLayout title="Inquiry">
        <Text style={styles.error}>Unable to load this inquiry.</Text>
      </ScreenLayout>
    );
  }

  return (
    <ScreenLayout title="Inquiry">
      <StatusBadge status={inquiry.status} />
      <Text style={styles.message}>{inquiry.message}</Text>
      <Button
        label="View quotations"
        onPress={() => navigation.navigate("QuotationThread", { inquiryId })}
        style={{ marginTop: 16 }}
      />
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  message: {
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
