-- AI schema: job tracking + every AI intermediate/result record.
-- AI never writes directly to catalog/pricing core tables — see
-- ai.catalog_generations.review_status / ai.price_recommendations.review_status,
-- the human-approval gate (principle #9). Owning module: backend/modules/ai.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.6

CREATE SCHEMA IF NOT EXISTS ai;

CREATE TABLE ai.ai_jobs (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_type                 VARCHAR(32) NOT NULL,
    status                   VARCHAR(32) NOT NULL DEFAULT 'QUEUED',
    requested_by_user_id     UUID NOT NULL REFERENCES identity.users(id),
    model_name               VARCHAR(128),
    model_version            VARCHAR(64),
    error_message            TEXT,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_ai_jobs_type CHECK (
        job_type IN ('STT','TRANSLATION','CATALOG_GENERATION','IMAGE_ENHANCEMENT','PRICE_RECOMMENDATION')
    ),
    CONSTRAINT chk_ai_jobs_status CHECK (status IN ('QUEUED','RUNNING','SUCCEEDED','FAILED'))
);

CREATE TABLE ai.voice_inputs (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    media_asset_id     UUID NOT NULL REFERENCES media.media_assets(id),
    ai_job_id          UUID REFERENCES ai.ai_jobs(id),
    seller_id          UUID NOT NULL REFERENCES seller.sellers(id),
    language_code      VARCHAR(16),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_voice_inputs_media_asset_id ON ai.voice_inputs(media_asset_id);
CREATE INDEX idx_voice_inputs_seller_id ON ai.voice_inputs(seller_id);

CREATE TABLE ai.speech_transcriptions (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    voice_input_id        UUID NOT NULL REFERENCES ai.voice_inputs(id) ON DELETE CASCADE,
    ai_job_id             UUID REFERENCES ai.ai_jobs(id),
    transcript_text       TEXT NOT NULL,
    confidence_score      NUMERIC(5,4),
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_speech_transcriptions_voice_input_id UNIQUE (voice_input_id)
);

CREATE TABLE ai.translations (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    speech_transcription_id     UUID NOT NULL REFERENCES ai.speech_transcriptions(id) ON DELETE CASCADE,
    ai_job_id                   UUID REFERENCES ai.ai_jobs(id),
    source_language             VARCHAR(16) NOT NULL,
    target_language             VARCHAR(16) NOT NULL,
    translated_text             TEXT NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_translations_speech_transcription_id ON ai.translations(speech_transcription_id);

CREATE TABLE ai.catalog_generations (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    translation_id           UUID REFERENCES ai.translations(id),
    ai_job_id                UUID REFERENCES ai.ai_jobs(id),
    seller_id                UUID NOT NULL REFERENCES seller.sellers(id),
    generated_title          VARCHAR(255),
    generated_description    TEXT,
    generated_attributes     JSONB,
    review_status            VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    approved_product_id      UUID REFERENCES catalog.products(id),
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_catalog_generations_review_status CHECK (review_status IN ('PENDING','APPROVED','REJECTED'))
);
CREATE INDEX idx_catalog_generations_seller_id ON ai.catalog_generations(seller_id);

CREATE TABLE ai.image_processing_results (
    id                         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_media_asset_id      UUID NOT NULL REFERENCES media.media_assets(id),
    output_media_asset_id      UUID REFERENCES media.media_assets(id),
    ai_job_id                  UUID REFERENCES ai.ai_jobs(id),
    operations_applied         JSONB NOT NULL,
    created_at                 TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                 TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_image_processing_results_source_media_asset_id ON ai.image_processing_results(source_media_asset_id);

CREATE TABLE ai.price_recommendations (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id            UUID NOT NULL REFERENCES catalog.products(id),
    ai_job_id             UUID REFERENCES ai.ai_jobs(id),
    recommended_price     NUMERIC(12,2) NOT NULL,
    inputs_snapshot       JSONB NOT NULL,
    review_status         VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_price_recommendations_review_status CHECK (review_status IN ('PENDING','ACCEPTED','REJECTED'))
);
CREATE INDEX idx_price_recommendations_product_id ON ai.price_recommendations(product_id);

-- Follow-up soft reference from catalog.products back to its originating AI draft
-- (deferred here, not in V003, to avoid a forward FK reference across migration files).
ALTER TABLE catalog.products
    ADD CONSTRAINT fk_products_source_catalog_generation
    FOREIGN KEY (source_catalog_generation_id) REFERENCES ai.catalog_generations(id) ON DELETE SET NULL;
