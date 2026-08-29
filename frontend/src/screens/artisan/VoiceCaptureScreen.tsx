import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { Audio } from "expo-av";
import { useState } from "react";
import { StyleSheet, Text, View } from "react-native";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { ScreenLayout } from "@/components/shared/ScreenLayout";
import { Button } from "@/components/ui/Button";
import { useToastStore } from "@/components/ui/Toast";
import type { AppStackParamList } from "@/navigation/types";
import type { AiJob, ApiResponse } from "@/types";

type Props = NativeStackScreenProps<AppStackParamList, "VoiceCapture">;

/**
 * /seller/products/new/voice — records a short voice description of a
 * product and uploads it to POST /ai/voice/upload, which starts an
 * ai.ai_jobs row (jobType VOICE_CATALOG). Navigates to AICatalogReview to
 * poll the job. Per docs/architecture/06_COMMUNICATION_WORKFLOWS.md the
 * voice-to-catalog flow is the primary product-creation path for artisans
 * with limited literacy/typing comfort.
 */
export function VoiceCaptureScreen({ navigation }: Props) {
  const [recording, setRecording] = useState<Audio.Recording | null>(null);
  const [isRecording, setIsRecording] = useState(false);
  const [isUploading, setIsUploading] = useState(false);

  const startRecording = async () => {
    const { status } = await Audio.requestPermissionsAsync();
    if (status !== "granted") {
      useToastStore.getState().show("Microphone permission is required", "error");
      return;
    }
    await Audio.setAudioModeAsync({ allowsRecordingIOS: true, playsInSilentModeIOS: true });
    const { recording: newRecording } = await Audio.Recording.createAsync(
      Audio.RecordingOptionsPresets.HIGH_QUALITY,
    );
    setRecording(newRecording);
    setIsRecording(true);
  };

  const stopAndUpload = async () => {
    if (!recording) return;
    setIsRecording(false);
    await recording.stopAndUnloadAsync();
    const uri = recording.getURI();
    setRecording(null);
    if (!uri) return;

    setIsUploading(true);
    try {
      const formData = new FormData();
      // React Native FormData file shape — see Expo docs for FormData + file uploads.
      formData.append("audio", {
        uri,
        name: "product-voice.m4a",
        type: "audio/m4a",
      } as unknown as Blob);

      const response = await apiClient.post<ApiResponse<AiJob>>(
        endpoints.ai.voiceUpload(),
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        },
      );
      navigation.replace("AICatalogReview", { jobId: response.data.data.id });
    } catch {
      useToastStore
        .getState()
        .show("Upload failed — you can enter details manually instead", "error");
      navigation.replace("ProductForm", {});
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <ScreenLayout title="Describe your product">
      <View style={styles.center}>
        <Text style={styles.hint}>
          Tap record and describe your product out loud — materials, size, story. AI will draft a
          listing you can review and edit.
        </Text>
        <View style={styles.recordCircleWrap}>
          <View style={[styles.recordCircle, isRecording && styles.recordCircleActive]} />
        </View>
        {!isRecording ? (
          <Button label="Start recording" onPress={startRecording} loading={isUploading} />
        ) : (
          <Button label="Stop and submit" variant="danger" onPress={stopAndUpload} />
        )}
        <Button
          label="Enter details manually instead"
          variant="ghost"
          onPress={() => navigation.replace("ProductForm", {})}
          style={{ marginTop: 12 }}
        />
      </View>
    </ScreenLayout>
  );
}

const styles = StyleSheet.create({
  center: {
    alignItems: "center",
    paddingVertical: 24,
  },
  hint: {
    fontSize: 14,
    color: "#64748B",
    textAlign: "center",
    marginBottom: 32,
  },
  recordCircleWrap: {
    marginBottom: 32,
  },
  recordCircle: {
    width: 96,
    height: 96,
    borderRadius: 48,
    backgroundColor: "#0F172A",
  },
  recordCircleActive: {
    backgroundColor: "#DC2626",
  },
});
