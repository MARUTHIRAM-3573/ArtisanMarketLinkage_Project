import { StyleSheet, Text, View } from "react-native";

import { StatusBadge } from "@/components/ui/Badge";
import { Spinner } from "@/components/ui/Spinner";
import { useAiJobPolling } from "@/hooks/useAiJobPolling";

interface AIJobStatusIndicatorProps {
  jobId: string | undefined;
  pendingLabel?: string;
  onCompleted?: (resultPayload: Record<string, unknown> | undefined) => void;
  onFailed?: () => void;
}

/**
 * Shared by AICatalogReviewScreen, AIImageReviewScreen, and
 * PricingReviewScreen — they all poll the same underlying ai.ai_jobs
 * resource and only differ in what they do with the result payload once the
 * job completes (docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §3).
 */
export function AIJobStatusIndicator({
  jobId,
  pendingLabel = "AI is working on this…",
  onCompleted,
  onFailed,
}: AIJobStatusIndicatorProps) {
  const { data: job, isLoading } = useAiJobPolling(jobId);

  if (!jobId || isLoading) {
    return <Spinner label={pendingLabel} />;
  }

  if (!job) {
    return null;
  }

  if (job.status === "COMPLETED") {
    onCompleted?.(job.resultPayload);
    return (
      <View style={styles.row}>
        <StatusBadge status={job.status} />
        <Text style={styles.text}>Done — review the result below.</Text>
      </View>
    );
  }

  if (job.status === "FAILED") {
    onFailed?.();
    return (
      <View style={styles.row}>
        <StatusBadge status={job.status} />
        <Text style={styles.text}>
          This took longer than expected or failed. You can retry or enter details manually.
        </Text>
      </View>
    );
  }

  return (
    <View style={styles.row}>
      <Spinner />
      <StatusBadge status={job.status} />
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    alignItems: "center",
    gap: 8,
  },
  text: {
    fontSize: 13,
    color: "#64748B",
    textAlign: "center",
    marginTop: 4,
  },
});
