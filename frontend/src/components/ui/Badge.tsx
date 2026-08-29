import { StyleSheet, Text, View } from "react-native";

type Tone = "neutral" | "success" | "warning" | "danger" | "info";

interface BadgeProps {
  label: string;
  tone?: Tone;
}

const TONE_STYLES: Record<Tone, { bg: string; fg: string }> = {
  neutral: { bg: "#E2E8F0", fg: "#334155" },
  success: { bg: "#DCFCE7", fg: "#166534" },
  warning: { bg: "#FEF3C7", fg: "#92400E" },
  danger: { bg: "#FEE2E2", fg: "#991B1B" },
  info: { bg: "#DBEAFE", fg: "#1E40AF" },
};

/** Small pill used for statuses (order status, AI job status, product status, inquiry status). */
export function Badge({ label, tone = "neutral" }: BadgeProps) {
  const toneStyle = TONE_STYLES[tone];
  return (
    <View style={[styles.base, { backgroundColor: toneStyle.bg }]}>
      <Text style={[styles.text, { color: toneStyle.fg }]}>{label}</Text>
    </View>
  );
}

/**
 * Maps the known status vocabularies used across the platform (order status
 * from commerce.orders, ai_jobs.status, product status draft/active) to a
 * Badge tone. Falls back to "neutral" for anything unrecognized rather than
 * throwing, since new statuses may be added on the backend over time.
 */
const STATUS_TONE_MAP: Record<string, Tone> = {
  DRAFT: "neutral",
  ACTIVE: "success",
  PENDING: "warning",
  PENDING_REVIEW: "warning",
  PROCESSING: "info",
  IN_PROGRESS: "info",
  COMPLETED: "success",
  SUCCEEDED: "success",
  APPROVED: "success",
  ACCEPTED: "success",
  DELIVERED: "success",
  SHIPPED: "info",
  CONFIRMED: "info",
  CANCELLED: "danger",
  FAILED: "danger",
  REJECTED: "danger",
  EXPIRED: "danger",
};

export function StatusBadge({ status }: { status: string }) {
  const tone = STATUS_TONE_MAP[status?.toUpperCase()] ?? "neutral";
  return <Badge label={status?.replace(/_/g, " ") ?? "UNKNOWN"} tone={tone} />;
}

const styles = StyleSheet.create({
  base: {
    alignSelf: "flex-start",
    borderRadius: 999,
    paddingVertical: 3,
    paddingHorizontal: 10,
  },
  text: {
    fontSize: 12,
    fontWeight: "600",
    textTransform: "capitalize",
  },
});
