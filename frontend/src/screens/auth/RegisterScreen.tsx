import { zodResolver } from "@hookform/resolvers/zod";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { z } from "zod";

import { apiClient } from "@/api/client";
import { endpoints } from "@/api/endpoints";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { useToastStore } from "@/components/ui/Toast";
import type { AuthStackParamList } from "@/navigation/types";
import type { Role } from "@/types";

const ROLE_OPTIONS: { value: Role; label: string }[] = [
  { value: "CUSTOMER", label: "I want to shop" },
  { value: "ARTISAN", label: "I'm an artisan seller" },
  { value: "B2B_BUYER", label: "I'm a business buyer" },
];

const schema = z.object({
  fullName: z.string().min(2, "Enter your full name"),
  email: z.string().email("Enter a valid email"),
  password: z.string().min(8, "Password must be at least 8 characters"),
});

type FormValues = z.infer<typeof schema>;

type Props = NativeStackScreenProps<AuthStackParamList, "Register">;

/** /register — per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §2. */
export function RegisterScreen({ navigation }: Props) {
  const [role, setRole] = useState<Role>("CUSTOMER");
  const [submitError, setSubmitError] = useState<string | null>(null);
  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const onSubmit = async (values: FormValues) => {
    setSubmitError(null);
    try {
      await apiClient.post(endpoints.auth.register(), { ...values, role });
      useToastStore.getState().show("Account created — please sign in", "success");
      navigation.navigate("Login");
    } catch {
      setSubmitError("Could not create your account. That email may already be in use.");
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Create your account</Text>

      <Controller
        control={control}
        name="fullName"
        render={({ field: { onChange, onBlur, value } }) => (
          <Input
            label="Full name"
            value={value}
            onChangeText={onChange}
            onBlur={onBlur}
            error={errors.fullName?.message}
          />
        )}
      />
      <Controller
        control={control}
        name="email"
        render={({ field: { onChange, onBlur, value } }) => (
          <Input
            label="Email"
            autoCapitalize="none"
            keyboardType="email-address"
            value={value}
            onChangeText={onChange}
            onBlur={onBlur}
            error={errors.email?.message}
          />
        )}
      />
      <Controller
        control={control}
        name="password"
        render={({ field: { onChange, onBlur, value } }) => (
          <Input
            label="Password"
            secureTextEntry
            value={value}
            onChangeText={onChange}
            onBlur={onBlur}
            error={errors.password?.message}
          />
        )}
      />

      <Text style={styles.roleLabel}>I am here to…</Text>
      <View style={styles.roleRow}>
        {ROLE_OPTIONS.map((option) => (
          <Pressable
            key={option.value}
            onPress={() => setRole(option.value)}
            style={[styles.roleChip, role === option.value && styles.roleChipActive]}
          >
            <Text style={[styles.roleChipText, role === option.value && styles.roleChipTextActive]}>
              {option.label}
            </Text>
          </Pressable>
        ))}
      </View>

      {submitError ? <Text style={styles.errorText}>{submitError}</Text> : null}

      <Button
        label="Create account"
        onPress={handleSubmit(onSubmit)}
        loading={isSubmitting}
        style={{ marginTop: 8 }}
      />
      <Button
        label="Back to sign in"
        variant="ghost"
        onPress={() => navigation.navigate("Login")}
        style={{ marginTop: 8 }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    justifyContent: "center",
    padding: 24,
    backgroundColor: "#F8FAFC",
  },
  title: {
    fontSize: 22,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 20,
  },
  roleLabel: {
    fontSize: 13,
    fontWeight: "600",
    color: "#334155",
    marginBottom: 8,
  },
  roleRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
    marginBottom: 16,
  },
  roleChip: {
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 999,
    paddingVertical: 8,
    paddingHorizontal: 14,
  },
  roleChipActive: {
    backgroundColor: "#0F172A",
    borderColor: "#0F172A",
  },
  roleChipText: {
    fontSize: 13,
    color: "#334155",
  },
  roleChipTextActive: {
    color: "#FFFFFF",
    fontWeight: "600",
  },
  errorText: {
    color: "#DC2626",
    fontSize: 13,
    marginBottom: 8,
  },
});
