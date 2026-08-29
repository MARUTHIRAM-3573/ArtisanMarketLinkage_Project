/**
 * Every API endpoint used by the app, grouped by domain module, matching
 * docs/architecture/05_API_CONTRACTS.md exactly. Centralizing these as
 * functions (not scattered string literals) is what keeps a backend path
 * change a one-line fix here instead of a grep-and-replace across screens.
 */
export const endpoints = {
  auth: {
    register: () => "/auth/register",
    login: () => "/auth/login",
    refresh: () => "/auth/refresh",
    me: () => "/auth/me",
    addresses: () => "/addresses",
  },
  sellers: {
    create: () => "/sellers",
    byId: (id: string) => `/sellers/${id}`,
    artisanProfile: (id: string) => `/sellers/${id}/artisan-profile`,
  },
  catalog: {
    products: () => "/products",
    productById: (id: string) => `/products/${id}`,
    productSkus: (productId: string) => `/products/${productId}/skus`,
    productAttributes: (productId: string) => `/products/${productId}/attributes`,
    categories: () => "/categories",
  },
  media: {
    upload: () => "/media/upload",
    productMedia: (productId: string) => `/products/${productId}/media`,
  },
  inventory: {
    bySku: (skuId: string) => `/skus/${skuId}/inventory`,
    movements: (skuId: string) => `/skus/${skuId}/inventory/movements`,
  },
  ai: {
    voiceUpload: () => "/ai/voice/upload",
    catalogGenerate: () => "/ai/catalog/generate",
    catalogGenerationById: (id: string) => `/ai/catalog/generations/${id}`,
    catalogGenerationApprove: (id: string) => `/ai/catalog/generations/${id}/approve`,
    imageEnhance: () => "/ai/image/enhance",
    pricingRecommend: () => "/ai/pricing/recommend",
    pricingAccept: (id: string) => `/ai/pricing/recommendations/${id}/accept`,
    jobById: (id: string) => `/ai/jobs/${id}`,
  },
  pricing: {
    costRecords: (productId: string) => `/products/${productId}/cost-records`,
    skuPrices: (skuId: string) => `/skus/${skuId}/prices`,
  },
  market: {
    listings: (productId: string) => `/products/${productId}/listings`,
    channels: () => "/market/channels",
  },
  b2b: {
    buyers: () => "/b2b/buyers",
    inquiries: () => "/b2b/inquiries",
    inquiryById: (id: string) => `/b2b/inquiries/${id}`,
    quotations: (inquiryId: string) => `/b2b/inquiries/${inquiryId}/quotations`,
    acceptQuotation: (id: string) => `/b2b/quotations/${id}/accept`,
    purchaseOrderById: (id: string) => `/b2b/purchase-orders/${id}`,
  },
  commerce: {
    cart: () => "/cart",
    cartItem: (itemId: string) => `/cart/items/${itemId}`,
    checkout: () => "/checkout",
    orders: () => "/orders",
    orderById: (id: string) => `/orders/${id}`,
  },
  payment: {
    initiate: (orderId: string) => `/orders/${orderId}/payments`,
    byId: (id: string) => `/payments/${id}`,
    refund: (id: string) => `/payments/${id}/refunds`,
    invoiceById: (id: string) => `/invoices/${id}`,
  },
} as const;
