import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import * as ImagePicker from "expo-image-picker";
import { useState } from "react";
import { Image, StyleSheet, Text, View } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Button } from "@/components/ui/Button";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";
import type { AiJob, ApiResponse } from "@/types";

type Props = NativeStackScreenProps<AppStackParamList, "ImageStudio">;

/**
 * /seller/products/:id/image-studio — pick or take a product photo, upload
 * for AI enhancement (POST /ai/image/enhance), then move to
 * AIImageReviewScreen to poll and approve. Per
 * docs/architecture/06_COMMUNICATION_WORKFLOWS.md's image enhancement flow.
 */
export function ImageStudioScreen({ route, navigation }: Props) {
  const { productId } = route.params;
  const [imageUri, setImageUri] = useState<string | null>(null);
  const [isUploading, setIsUploading] = useState(false);

  const pickImage = async () => {
    const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (status !== "granted") {
      useToastStore.getState().show("Photo library permission is required", "error");
      return;
    }
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.8,
    });
    if (!result.canceled && result.assets[0]) {
      setImageUri(result.assets[0].uri);
    }
  };

  const submitForEnhancement = async () => {
    if (!imageUri) return;
    setIsUploading(true);
    try {
      const formData = new FormData();
      formData.append("image", {
        uri: imageUri,
        name: "product-photo.jpg",
        type: "image/jpeg",
      } as unknown as Blob);
      formData.append("productId", productId);

      const response = await apiClient.post<ApiResponse<AiJob>>(
        endpoints.ai.imageEnhance(),
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        },
      );
      navigation.replace("AIImageReview", { jobId: response.data.data.id, productId });
    } catch {
      useToastStore.getState().show("Enhancement failed, try again or use original photo", "error");
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <ScreenLayout title="Image studio">
      <View style={styles.previewWrap}>
        {imageUri ? (
          <Image source={{ uri: imageUri }} style={styles.preview} />
        ) : (
          <View style={[styles.preview, styles.placeholder]}>
            <Text style={styles.placeholderText}>No photo selected</Text>
          </View>
        )}
      </View>
      <Button label="Choose a photo" variant="secondary" onPress={pickImage} />
      <Button
        label="Enhance with AI"
        onPress={submitForEnhancement}
        loading={isUploading}
        disabled={!imageUri}
        style={{ marginTop: 8 }}
      />
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  previewWrap: {
    marginBottom: 16,
  },
  preview: {
    width: "100%",
    height: 260,
    borderRadius: 12,
  },
  placeholder: {
    backgroundColor: "#E2E8F0",
    alignItems: "center",
    justifyContent: "center",
  },
  placeholderText: {
    color: "#64748B",
    fontSize: 13,
  },
});
