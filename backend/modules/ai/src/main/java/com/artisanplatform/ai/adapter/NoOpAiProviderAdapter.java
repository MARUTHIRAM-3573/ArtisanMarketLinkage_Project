package com.artisanplatform.ai.adapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Placeholder {@link AiProviderAdapter} so the {@code ai} module has a
 * working Spring bean to autowire before a concrete AI vendor is chosen
 * (docs/architecture/02_ARCHITECTURE_OVERVIEW.md §4, open question).
 *
 * <p><strong>This is NOT production behavior.</strong> Every method returns
 * a deterministic placeholder result instead of calling a real provider, so
 * local development and integration tests can exercise the full
 * voice-to-catalog / image-enhancement / pricing-recommendation request flow
 * end to end before a vendor is picked. Replace this bean (e.g. with an
 * {@code @ConditionalOnProperty}-gated real implementation) once a provider
 * is selected — do not delete this class outright, since it remains useful
 * for fast local development and CI without live external calls.
 */
@Component
public class NoOpAiProviderAdapter implements AiProviderAdapter {

    @Override
    public TranscriptionResult transcribe(UUID voiceInputId, byte[] audioBytes) {
        return new TranscriptionResult("[placeholder transcript — no AI provider configured]", 0.0);
    }

    @Override
    public TranslationResult translate(String sourceLanguage, String targetLanguage, String text) {
        return new TranslationResult(text);
    }

    @Override
    public CatalogDraft generateCatalogDraft(String translatedText) {
        return new CatalogDraft("[placeholder title]", translatedText, Map.of());
    }

    @Override
    public ImageEnhancementResult enhanceImage(byte[] imageBytes) {
        return new ImageEnhancementResult(imageBytes, List.of("NONE_PLACEHOLDER"));
    }

    @Override
    public PriceRecommendationResult recommendPrice(PricingInputs inputs) {
        BigDecimal total = inputs.rawMaterialCost().add(inputs.labourCost()).add(inputs.otherCost());
        return new PriceRecommendationResult(total);
    }
}
