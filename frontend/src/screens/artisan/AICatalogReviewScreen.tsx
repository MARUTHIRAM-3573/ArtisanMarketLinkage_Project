import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useMutation } from "@tanstack/react-query";
import { useState } from "react";
import { StyleSheet, Text, View } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { AIJobStatusIndicator } from "@/components/shared/AIJobStatusIndicator";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";

type Props = NativeStackScreenProps<AppStackParamList, "AICatalogReview">;

interface DraftCatalog {
  generationId: string;
  title: string;
  description: string;
}

/**
 * /seller/products/new/voice → AI Catalog Review — polls the ai_jobs row
 * created by VoiceCaptureScreen, then presents the AI-drafted title/
 * description for edit + approval before POSTing the approved catalog
 * generation, per docs/architecture/06_COMMUNICATION_WORKFLOWS.md and
 * `ai.catalog_generations` in the schema.
 */
export function AICatalogReviewScreen({ route, navigation }: Props) {
  const { jobId } = route.params;
  const [draft, setDraft] = useState<DraftCatalog | null>(null);

  const approveMutation = useMutation({
    mutationFn: () =>
      apiClient.post(endpoints.ai.catalogGenerationApprove(draft!.generationId), {
        title: draft!.title,
        description: draft!.description,
      }),
    onSuccess: () => {
      useToastStore.getState().show("Product created from AI draft", "success");
      navigation.replace("SellerProducts");
    },
    onError: () => useToastStore.getState().show("Could not save this draft", "error"),
  });

  return (
    <ScreenLayout title="AI catalog draft">
      <AIJobStatusIndicator
        jobId={jobId}
        pendingLabel="Transcribing and drafting your listing…"
        onCompleted={(payload) => {
          if (payload && !draft) {
            setDraft({
              generationId: String(payload.generationId ?? jobId),
              title: String(payload.title ?? ""),
              description: String(payload.description ?? ""),
            });
          }
        }}
        onFailed={() => navigation.replace("ProductForm", {})}
      />

      {draft ? (
        <View style={styles.form}>
          <Input
            label="Title"
            value={draft.title}
            onChangeText={(v) => setDraft({ ...draft, title: v })}
          />
          <Input
            label="Description"
            value={draft.description}
            onChangeText={(v) => setDraft({ ...draft, description: v })}
            multiline
            numberOfLines={4}
          />
          <Text style={styles.hint}>
            Review and edit the AI draft above, then approve to publish it as a product.
          </Text>
          <Button
            label="Approve and create product"
            onPress={() => approveMutation.mutate()}
            loading={approveMutation.isPending}
          />
        </View>
      ) : null}
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  form: {
    marginTop: 16,
  },
  hint: {
    fontSize: 12,
    color: "#64748B",
    marginBottom: 12,
  },
});
