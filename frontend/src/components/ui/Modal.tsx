import { ReactNode } from "react";
import { Modal as RNModal, Pressable, StyleSheet, Text, View } from "react-native";

interface AppModalProps {
  visible: boolean;
  onClose: () => void;
  title?: string;
  children: ReactNode;
}

/**
 * Centered modal sheet used for confirmations and short forms (e.g. accept
 * quotation, refund confirmation). Wraps React Native's built-in Modal with
 * the app's standard chrome (dimmed backdrop, title bar, close button).
 */
export function AppModal({ visible, onClose, title, children }: AppModalProps) {
  return (
    <RNModal visible={visible} transparent animationType="fade" onRequestClose={onClose}>
      <Pressable style={styles.backdrop} onPress={onClose}>
        <Pressable style={styles.sheet} onPress={(e) => e.stopPropagation()}>
          {title ? (
            <View style={styles.header}>
              <Text style={styles.title}>{title}</Text>
              <Pressable onPress={onClose} hitSlop={12}>
                <Text style={styles.close}>✕</Text>
              </Pressable>
            </View>
          ) : null}
          <View style={styles.body}>{children}</View>
        </Pressable>
      </Pressable>
    </RNModal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: "rgba(15, 23, 42, 0.5)",
    justifyContent: "center",
    padding: 24,
  },
  sheet: {
    backgroundColor: "#FFFFFF",
    borderRadius: 14,
    padding: 18,
    maxHeight: "80%",
  },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 12,
  },
  title: {
    fontSize: 17,
    fontWeight: "700",
    color: "#0F172A",
  },
  close: {
    fontSize: 16,
    color: "#64748B",
  },
  body: {},
});
