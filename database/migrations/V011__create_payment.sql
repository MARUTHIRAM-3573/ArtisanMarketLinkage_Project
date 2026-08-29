-- Payment schema: payments, transactions, refunds, seller settlements, invoices.
-- NEVER stores card number/CVV/full payment credentials (source §22, §40, principle
-- enforced at the application layer too — see backend/modules/payment).
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.11
-- Owning module: backend/modules/payment

CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE payment.payments (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id               UUID NOT NULL REFERENCES commerce.orders(id),
    amount                 NUMERIC(12,2) NOT NULL,
    status                 VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    gateway_reference       VARCHAR(255),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_payments_order_id ON payment.payments(order_id);

CREATE TABLE payment.payment_transactions (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id               UUID NOT NULL REFERENCES payment.payments(id) ON DELETE CASCADE,
    outcome                  VARCHAR(16) NOT NULL,
    gateway_transaction_id   VARCHAR(255),
    attempted_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_payment_transactions_outcome CHECK (outcome IN ('SUCCESS','FAILED'))
);
CREATE INDEX idx_payment_transactions_payment_id ON payment.payment_transactions(payment_id);

CREATE TABLE payment.refunds (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id    UUID NOT NULL REFERENCES payment.payments(id) ON DELETE CASCADE,
    amount        NUMERIC(12,2) NOT NULL,
    reason        TEXT,
    status        VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_refunds_payment_id ON payment.refunds(payment_id);

CREATE TABLE payment.seller_settlements (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id                 UUID NOT NULL REFERENCES commerce.orders(id),
    seller_id                UUID NOT NULL REFERENCES seller.sellers(id),
    gross_amount             NUMERIC(12,2) NOT NULL,
    platform_commission      NUMERIC(12,2) NOT NULL,
    net_settlement_amount    NUMERIC(12,2) NOT NULL,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_seller_settlements_order_id ON payment.seller_settlements(order_id);
CREATE INDEX idx_seller_settlements_seller_id ON payment.seller_settlements(seller_id);

CREATE TABLE payment.invoices (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id         UUID NOT NULL REFERENCES payment.payments(id),
    invoice_number     VARCHAR(64) NOT NULL,
    issued_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_invoices_number UNIQUE (invoice_number)
);
CREATE INDEX idx_invoices_payment_id ON payment.invoices(payment_id);
