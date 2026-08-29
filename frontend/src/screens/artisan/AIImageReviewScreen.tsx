import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useState } from "react";
import { Image, StyleSheet, View } from "react-native";

import { AIJobStatusIndicator } from "@/components/shared/AIJobStatusIndicator";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Button } from "@/components/ui/Button";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";

type Props = NativeStackScreenProps<AppStackParamList, "AIImageReview">;

/**
 * /seller/products/:id/image-studio → AI Image Review — shows the
 * AI-enhanced image once the job completes, and lets the seller keep it or
 * fall back to the original (per the error-state table in
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §9: "Enhancement failed,
 * try again or use original photo").
 */
export function AIImageReviewScreen({ route, navigation }: Props) {
  const { jobId, productId } = route.params;
  const [enhancedUrl, setEnhancedUrl] = useState<string | null>(null);

  return (
    <ScreenLayout title="Review enhanced photo">
      <AIJobStatusIndicator
        jobId={jobId}
        pendingLabel="Enhancing your photo…"
        onCompleted={(payload) => {
          if (payload && !enhancedUrl) {
            setEnhancedUrl(String(payload.enhancedMediaUrl ?? ""));
          }
        }}
      />

      {enhancedUrl ? (
        <View style={styles.body}>
          <Image source={{ uri: enhancedUrl }} style={styles.image} />
          <Button
            label="Use this photo"
            onPress={() => {
              useToastStore.getState().show("Photo saved to product", "success");
              navigation.navigate("ProductForm", { productId });
            }}
          />
          <Button
            label="Try again with a different photo"
            variant="ghost"
            onPress={() => navigation.replace("ImageStudio", { productId })}
            style={{ marginTop: 8 }}
          />
        </View>
      ) : null}
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  body: {
    marginTop: 16,
  },
  image: {
    width: "100%",
    height: 260,
    borderRadius: 12,
    marginBottom: 16,
  },
});
