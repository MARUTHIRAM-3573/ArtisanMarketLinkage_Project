import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { useEffect } from "react";

import type { AppStackParamList, AuthStackParamList, MainTabParamList } from "./types";
import { Spinner } from "@/components/ui/Spinner";
import { ToastHost } from "@/components/ui/Toast";
import { useAuth } from "@/hooks/useAuth";

import { VoiceCaptureScreen } from "@/screens/artisan/VoiceCaptureScreen";
import { AICatalogReviewScreen } from "@/screens/artisan/AICatalogReviewScreen";
import { ImageStudioScreen } from "@/screens/artisan/ImageStudioScreen";
import { AIImageReviewScreen } from "@/screens/artisan/AIImageReviewScreen";
import { PricingReviewScreen } from "@/screens/artisan/PricingReviewScreen";
import { InventoryManagerScreen } from "@/screens/artisan/InventoryManagerScreen";
import { ProductFormScreen } from "@/screens/artisan/ProductFormScreen";
import { ProductListScreen } from "@/screens/artisan/ProductListScreen";
import { SellerProfileScreen } from "@/screens/artisan/SellerProfileScreen";
import { LoginScreen } from "@/screens/auth/LoginScreen";
import { RegisterScreen } from "@/screens/auth/RegisterScreen";

import { InquiryDetailScreen } from "@/screens/b2b/InquiryDetailScreen";
import { InquiryListScreen } from "@/screens/b2b/InquiryListScreen";
import { QuotationThreadScreen } from "@/screens/b2b/QuotationThreadScreen";

import { CartScreen } from "@/screens/commerce/CartScreen";
import { CheckoutScreen } from "@/screens/commerce/CheckoutScreen";
import { OrderDetailScreen } from "@/screens/commerce/OrderDetailScreen";
import { OrderListScreen } from "@/screens/commerce/OrderListScreen";
import { CategoryBrowseScreen } from "@/screens/marketplace/CategoryBrowseScreen";
import { HomeScreen } from "@/screens/marketplace/HomeScreen";
import { ProductDetailScreen } from "@/screens/marketplace/ProductDetailScreen";

import { PaymentScreen } from "@/screens/payment/PaymentScreen";
import { useAuthStore } from "@/store/auth";

const AuthStack = createNativeStackNavigator<AuthStackParamList>();
const MainTab = createBottomTabNavigator<MainTabParamList>();
const AppStack = createNativeStackNavigator<AppStackParamList>();

function AuthNavigator() {
  return (
    <AuthStack.Navigator screenOptions={{ headerShown: false }}>
      <AuthStack.Screen name="Login" component={LoginScreen} />
      <AuthStack.Screen name="Register" component={RegisterScreen} />
    </AuthStack.Navigator>
  );
}

/**
 * Bottom tabs: which tabs are visible depends on the active user's roles,
 * per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §1 ("tabs differ by
 * active role"). Every role sees Home/Orders/Cart; B2B is shown to both
 * B2B_BUYER (their own inquiries) and ARTISAN (incoming inquiries).
 */
function MainTabs() {
  const { hasRole } = useAuth();
  const showB2b = hasRole("ARTISAN") || hasRole("B2B_BUYER");

  return (
    <MainTab.Navigator screenOptions={{ headerShown: false }}>
      <MainTab.Screen name="Home" component={HomeScreen} />
      <MainTab.Screen name="Products" component={CategoryBrowseScreen as never} />
      <MainTab.Screen name="Orders" component={OrderListScreen} />
      {showB2b ? <MainTab.Screen name="B2B" component={InquiryListScreen} /> : null}
      <MainTab.Screen name="Cart" component={CartScreen} />
    </MainTab.Navigator>
  );
}

/**
 * Full app stack once authenticated: MainTabs plus every detail/flow screen
 * pushed on top of it, matching the page hierarchy in
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §2.
 */
function AppNavigator() {
  return (
    <AppStack.Navigator screenOptions={{ headerShown: false }}>
      <AppStack.Screen name="MainTabs" component={MainTabs} />

      <AppStack.Screen name="ProductDetail" component={ProductDetailScreen} />
      <AppStack.Screen name="CategoryBrowse" component={CategoryBrowseScreen} />

      <AppStack.Screen name="SellerProfile" component={SellerProfileScreen} />
      <AppStack.Screen name="SellerProducts" component={ProductListScreen} />
      <AppStack.Screen name="ProductForm" component={ProductFormScreen} />
      <AppStack.Screen name="VoiceCapture" component={VoiceCaptureScreen} />
      <AppStack.Screen name="AICatalogReview" component={AICatalogReviewScreen} />
      <AppStack.Screen name="ImageStudio" component={ImageStudioScreen} />
      <AppStack.Screen name="AIImageReview" component={AIImageReviewScreen} />
      <AppStack.Screen name="PricingReview" component={PricingReviewScreen} />
      <AppStack.Screen name="InventoryManager" component={InventoryManagerScreen} />

      <AppStack.Screen name="InquiryDetail" component={InquiryDetailScreen} />
      <AppStack.Screen name="QuotationThread" component={QuotationThreadScreen} />

      <AppStack.Screen name="Checkout" component={CheckoutScreen} />
      <AppStack.Screen name="OrderDetail" component={OrderDetailScreen} />

      <AppStack.Screen name="Payment" component={PaymentScreen} />
    </AppStack.Navigator>
  );
}

/**
 * Root navigator: hydrates the auth session once on mount (SecureStore
 * token -> GET /auth/me, see store/auth.ts `hydrate`), then renders either
 * the auth stack or the full app stack depending on the result. This is the
 * "AuthGate" behavior described in
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §6.
 */
export function RootNavigator() {
  const { isAuthenticated, isLoading } = useAuth();

  useEffect(() => {
    useAuthStore.getState().hydrate();
  }, []);

  if (isLoading) {
    return <Spinner fullscreen label="Loading…" />;
  }

  return (
    <>
      {isAuthenticated ? <AppNavigator /> : <AuthNavigator />}
      <ToastHost />
    </>
  );
}
