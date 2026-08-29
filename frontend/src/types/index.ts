/**
 * Shared domain types used across screens/hooks. These mirror the backend
 * response DTOs (backend/modules/<domain>/.../dto/*ResponseDto.java) at the
 * field-name level so API responses can be typed without a separate
 * generation step. Kept intentionally to the primary aggregate-root fields
 * that the mobile screens actually read/write — not a 1:1 mirror of every
 * backend field.
 */

export type Role = "ARTISAN" | "CUSTOMER" | "B2B_BUYER" | "ADMIN";

export interface User {
  id: string;
  email: string;
  fullName: string;
  roles: Role[];
}

export interface Seller {
  id: string;
  userId: string;
  businessName: string;
  bio?: string;
  region?: string;
  verified: boolean;
}

export interface Product {
  id: string;
  sellerId: string;
  title: string;
  description?: string;
  categoryId?: string;
  status: "DRAFT" | "ACTIVE" | "ARCHIVED";
  sourceCatalogGenerationId?: string | null;
  createdAt: string;
}

export interface Sku {
  id: string;
  productId: string;
  skuCode: string;
  variantAttributes?: Record<string, string>;
}

export interface MediaAsset {
  id: string;
  productId: string;
  url: string;
  type: "IMAGE" | "VIDEO" | "AUDIO";
  isEnhanced: boolean;
}

export interface InventoryRecord {
  id: string;
  skuId: string;
  quantityOnHand: number;
  reorderThreshold: number;
}

export interface AiJob {
  id: string;
  jobType: "VOICE_CATALOG" | "IMAGE_ENHANCE" | "PRICING_RECOMMENDATION";
  status: "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";
  resultPayload?: Record<string, unknown>;
  createdAt: string;
}

export interface SkuPrice {
  id: string;
  skuId: string;
  amount: number;
  currency: string;
  source: "MANUAL" | "AI_RECOMMENDED";
}

export interface MarketListing {
  id: string;
  productId: string;
  channel: string;
  isActive: boolean;
}

export interface B2bInquiry {
  id: string;
  buyerId: string;
  productId?: string;
  message: string;
  status: "OPEN" | "QUOTED" | "ACCEPTED" | "REJECTED" | "EXPIRED";
  createdAt: string;
}

export interface Order {
  id: string;
  buyerId: string;
  status: "PENDING" | "CONFIRMED" | "PROCESSING" | "SHIPPED" | "DELIVERED" | "CANCELLED";
  totalAmount: number;
  currency: string;
  createdAt: string;
}

export interface Payment {
  id: string;
  orderId: string;
  status: "PENDING" | "SUCCEEDED" | "FAILED" | "REFUNDED";
  amount: number;
  currency: string;
  gatewayReference?: string;
}

/** Envelope every backend endpoint returns, per common/ApiResponse.java. */
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: { code: string; message: string } | null;
  correlationId?: string;
}
