import { ActivityIndicator, StyleSheet, Text, View } from "react-native";

interface SpinnerProps {
  label?: string;
  fullscreen?: boolean;
}

/**
 * Standard loading indicator. `fullscreen` centers it in the available
 * space, used for full-page loading states (e.g. auth hydration in
 * navigation/index.tsx, initial screen data fetch before content renders).
 */
export function Spinner({ label, fullscreen = false }: SpinnerProps) {
  return (
    <View style={fullscreen ? styles.fullscreen : styles.inline}>
      <ActivityIndicator size="large" color="#0F172A" />
      {label ? <Text style={styles.label}>{label}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  fullscreen: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#F8FAFC",
  },
  inline: {
    paddingVertical: 24,
    alignItems: "center",
    justifyContent: "center",
  },
  label: {
    marginTop: 10,
    fontSize: 13,
    color: "#64748B",
  },
});
