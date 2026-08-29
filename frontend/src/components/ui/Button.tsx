import { ActivityIndicator, Pressable, StyleSheet, Text, ViewStyle } from "react-native";

type Variant = "primary" | "secondary" | "ghost" | "danger";
type Size = "sm" | "md" | "lg";

interface ButtonProps {
  label: string;
  onPress: () => void;
  variant?: Variant;
  size?: Size;
  loading?: boolean;
  disabled?: boolean;
  style?: ViewStyle;
}

const VARIANT_STYLES: Record<Variant, { bg: string; text: string; border?: string }> = {
  primary: { bg: "#0F172A", text: "#FFFFFF" },
  secondary: { bg: "#E2E8F0", text: "#0F172A" },
  ghost: { bg: "transparent", text: "#0F172A", border: "#CBD5E1" },
  danger: { bg: "#DC2626", text: "#FFFFFF" },
};

const SIZE_STYLES: Record<
  Size,
  { paddingVertical: number; paddingHorizontal: number; fontSize: number }
> = {
  sm: { paddingVertical: 6, paddingHorizontal: 12, fontSize: 13 },
  md: { paddingVertical: 10, paddingHorizontal: 16, fontSize: 15 },
  lg: { paddingVertical: 14, paddingHorizontal: 20, fontSize: 17 },
};

export function Button({
  label,
  onPress,
  variant = "primary",
  size = "md",
  loading = false,
  disabled = false,
  style,
}: ButtonProps) {
  const variantStyle = VARIANT_STYLES[variant];
  const sizeStyle = SIZE_STYLES[size];

  return (
    <Pressable
      onPress={onPress}
      disabled={disabled || loading}
      style={[
        styles.base,
        {
          backgroundColor: variantStyle.bg,
          borderColor: variantStyle.border,
          borderWidth: variantStyle.border ? 1 : 0,
          paddingVertical: sizeStyle.paddingVertical,
          paddingHorizontal: sizeStyle.paddingHorizontal,
          opacity: disabled ? 0.5 : 1,
        },
        style,
      ]}
    >
      {loading ? (
        <ActivityIndicator color={variantStyle.text} />
      ) : (
        <Text style={{ color: variantStyle.text, fontSize: sizeStyle.fontSize, fontWeight: "600" }}>
          {label}
        </Text>
      )}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    borderRadius: 8,
    alignItems: "center",
    justifyContent: "center",
  },
});
