/**
 * Route param lists for every stack/tab navigator, matching the page
 * hierarchy in docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §2. Kept in
 * one file so a screen's params and its navigation call sites can't drift.
 */

export type AuthStackParamList = {
  Login: undefined;
  Register: undefined;
};

export type MainTabParamList = {
  Home: undefined;
  Products: undefined;
  Orders: undefined;
  B2B: undefined;
  Cart: undefined;
};

export type AppStackParamList = {
  MainTabs: undefined;

  // Marketplace
  ProductDetail: { productId: string };
  CategoryBrowse: undefined;

  // Artisan / seller
  SellerProfile: undefined;
  SellerProducts: undefined;
  ProductForm: { productId?: string };
  VoiceCapture: undefined;
  AICatalogReview: { jobId: string };
  ImageStudio: { productId: string };
  AIImageReview: { jobId: string; productId: string };
  PricingReview: { productId: string };
  InventoryManager: undefined;

  // B2B
  InquiryDetail: { inquiryId: string };
  QuotationThread: { inquiryId: string };

  // Commerce
  Checkout: undefined;
  OrderDetail: { orderId: string };

  // Payment
  Payment: { orderId: string };
};

export type RootStackParamList = AppStackParamList;
