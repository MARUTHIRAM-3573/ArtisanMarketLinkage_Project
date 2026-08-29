package com.artisanplatform.ai.adapter;

import java.util.UUID;

/**
 * Provider-agnostic interface for every AI capability the platform needs
 * (source §5: "The exact AI provider/model can be changed without changing
 * the core domain model"; principle #14: isolate external integrations
 * behind adapters).
 *
 * <p>The concrete AI provider is an open question
 * (docs/architecture/02_ARCHITECTURE_OVERVIEW.md §4) — no vendor is named
 * in the source workflow. Implement one class per chosen provider (e.g.
 * {@code AcmeAiProviderAdapter}) once selected; {@code ai.ai_jobs} tracks
 * every invocation regardless of which implementation is active.
 */
public interface AiProviderAdapter {

    TranscriptionResult transcribe(UUID voiceInputId, byte[] audioBytes);

    TranslationResult translate(String sourceLanguage, String targetLanguage, String text);

    CatalogDraft generateCatalogDraft(String translatedText);

    ImageEnhancementResult enhanceImage(byte[] imageBytes);

    PriceRecommendationResult recommendPrice(PricingInputs inputs);

    record TranscriptionResult(String transcriptText, Double confidenceScore) {
    }

    record TranslationResult(String translatedText) {
    }

    record CatalogDraft(String title, String description, java.util.Map<String, String> attributes) {
    }

    record ImageEnhancementResult(byte[] enhancedImageBytes, java.util.List<String> operationsApplied) {
    }

    record PricingInputs(
            java.math.BigDecimal rawMaterialCost,
            java.math.BigDecimal labourCost,
            java.math.BigDecimal otherCost,
            java.math.BigDecimal marketReferencePrice) {
    }

    record PriceRecommendationResult(java.math.BigDecimal recommendedPrice) {
    }
}
