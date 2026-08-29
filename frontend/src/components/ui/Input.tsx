import { forwardRef, useState } from "react";
import { StyleSheet, Text, TextInput, TextInputProps, View } from "react-native";

interface InputProps extends TextInputProps {
  label?: string;
  error?: string;
  helperText?: string;
}

/**
 * Shared text input used by every form in the app (login, register, product
 * forms, cost records, etc.). Renders a label, the field itself, and either
 * a validation error (react-hook-form + zod, per
 * docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §3 "forms/") or helper text.
 */
export const Input = forwardRef<TextInput, InputProps>(function Input(
  { label, error, helperText, style, onFocus, onBlur, ...rest },
  ref,
) {
  const [focused, setFocused] = useState(false);

  return (
    <View style={styles.container}>
      {label ? <Text style={styles.label}>{label}</Text> : null}
      <TextInput
        ref={ref}
        style={[
          styles.input,
          focused && styles.inputFocused,
          error ? styles.inputError : undefined,
          style,
        ]}
        placeholderTextColor="#94A3B8"
        onFocus={(e) => {
          setFocused(true);
          onFocus?.(e);
        }}
        onBlur={(e) => {
          setFocused(false);
          onBlur?.(e);
        }}
        {...rest}
      />
      {error ? (
        <Text style={styles.errorText}>{error}</Text>
      ) : helperText ? (
        <Text style={styles.helperText}>{helperText}</Text>
      ) : null}
    </View>
  );
});

const styles = StyleSheet.create({
  container: {
    marginBottom: 16,
  },
  label: {
    fontSize: 13,
    fontWeight: "600",
    color: "#334155",
    marginBottom: 6,
  },
  input: {
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 15,
    color: "#0F172A",
    backgroundColor: "#FFFFFF",
  },
  inputFocused: {
    borderColor: "#0F172A",
  },
  inputError: {
    borderColor: "#DC2626",
  },
  errorText: {
    marginTop: 4,
    fontSize: 12,
    color: "#DC2626",
  },
  helperText: {
    marginTop: 4,
    fontSize: 12,
    color: "#64748B",
  },
});
