import { useQuery } from "@tanstack/react-query";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import type { AiJob, ApiResponse } from "@/types";

const TERMINAL_STATUSES: AiJob["status"][] = ["COMPLETED", "FAILED"];

/**
 * Polls GET /ai/jobs/{id} until the job reaches a terminal status. This is
 * the reasoned polling substitute for a push mechanism, per
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §5 and
 * docs/architecture/06_COMMUNICATION_WORKFLOWS.md §1 (no WebSocket/SSE
 * exists in source). Shared by AICatalogReviewScreen, AIImageReviewScreen,
 * and PricingReviewScreen via AIJobStatusIndicator.
 *
 * PROPOSED interval: 2.5s (source does not specify one).
 */
export function useAiJobPolling(jobId: string | undefined, intervalMs = 2500) {
  return useQuery({
    queryKey: ["ai-job", jobId],
    enabled: Boolean(jobId),
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<AiJob>>(endpoints.ai.jobById(jobId!));
      return response.data.data;
    },
    refetchInterval: (query) => {
      const job = query.state.data;
      if (!job || TERMINAL_STATUSES.includes(job.status)) {
        return false;
      }
      return intervalMs;
    },
  });
}
