import { ReactNode } from "react";
import { StyleSheet, View, ViewStyle } from "react-native";

interface CardProps {
  children: ReactNode;
  style?: ViewStyle;
  padded?: boolean;
}

/**
 * Generic surface used to contain list rows (product cards, inquiry rows,
 * order rows). Kept deliberately unopinionated — screens compose their own
 * content inside it (see components/shared/ProductCard.tsx for a concrete
 * use).
 */
export function Card({ children, style, padded = true }: CardProps) {
  return <View style={[styles.base, padded && styles.padded, style]}>{children}</View>;
}

const styles = StyleSheet.create({
  base: {
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    marginBottom: 12,
  },
  padded: {
    padding: 14,
  },
});
