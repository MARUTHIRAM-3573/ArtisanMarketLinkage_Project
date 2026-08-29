import { useEffect, useState } from "react";
import { StyleSheet, Text, View } from "react-native";
import { create } from "zustand";

type ToastKind = "info" | "success" | "error";

interface ToastState {
  message: string | null;
  kind: ToastKind;
  show: (message: string, kind?: ToastKind) => void;
  hide: () => void;
}

/**
 * Minimal global toast store + host component. Used for the generic
 * "something went wrong" / "you don't have permission" surfaces referenced
 * as TODOs in api/client.ts, and for one-off success confirmations
 * (e.g. "Quotation accepted", "Product published").
 *
 * Usage: `useToastStore.getState().show("Saved", "success")` from anywhere
 * (including outside React components, such as the API client interceptor).
 */
export const useToastStore = create<ToastState>((set) => ({
  message: null,
  kind: "info",
  show: (message, kind = "info") => set({ message, kind }),
  hide: () => set({ message: null }),
}));

const KIND_COLORS: Record<ToastKind, string> = {
  info: "#0F172A",
  success: "#166534",
  error: "#991B1B",
};

/** Mount once, near the root (e.g. alongside RootNavigator in App.tsx). */
export function ToastHost() {
  const { message, kind, hide } = useToastStore();
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    if (message) {
      setVisible(true);
      const timer = setTimeout(() => {
        setVisible(false);
        hide();
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [message, hide]);

  if (!visible || !message) {
    return null;
  }

  return (
    <View style={[styles.toast, { backgroundColor: KIND_COLORS[kind] }]}>
      <Text style={styles.text}>{message}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  toast: {
    position: "absolute",
    bottom: 32,
    left: 20,
    right: 20,
    borderRadius: 10,
    paddingVertical: 12,
    paddingHorizontal: 16,
    elevation: 4,
    shadowColor: "#000",
    shadowOpacity: 0.15,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 2 },
  },
  text: {
    color: "#FFFFFF",
    fontSize: 14,
    fontWeight: "600",
    textAlign: "center",
  },
});
