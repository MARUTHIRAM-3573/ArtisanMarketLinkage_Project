import { Pressable, StyleSheet, Text, View } from "react-native";

import { StatusBadge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import type { Product } from "@/types";

interface ProductCardProps {
  product: Product;
  priceLabel?: string;
  onPress: () => void;
}

/**
 * Shared across marketplace browse (Customer), seller product list
 * (Artisan), and anywhere else a product needs to render as a row —
 * per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §3 reuse strategy.
 */
export function ProductCard({ product, priceLabel, onPress }: ProductCardProps) {
  return (
    <Pressable onPress={onPress}>
      <Card>
        <View style={styles.row}>
          <View style={styles.thumbPlaceholder} />
          <View style={styles.info}>
            <Text style={styles.title} numberOfLines={1}>
              {product.title}
            </Text>
            {product.description ? (
              <Text style={styles.description} numberOfLines={2}>
                {product.description}
              </Text>
            ) : null}
            <View style={styles.footerRow}>
              <StatusBadge status={product.status} />
              {priceLabel ? <Text style={styles.price}>{priceLabel}</Text> : null}
            </View>
          </View>
        </View>
      </Card>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
  },
  thumbPlaceholder: {
    width: 64,
    height: 64,
    borderRadius: 8,
    backgroundColor: "#E2E8F0",
    marginRight: 12,
  },
  info: {
    flex: 1,
    justifyContent: "space-between",
  },
  title: {
    fontSize: 15,
    fontWeight: "700",
    color: "#0F172A",
  },
  description: {
    fontSize: 12,
    color: "#64748B",
    marginTop: 2,
  },
  footerRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginTop: 8,
  },
  price: {
    fontSize: 14,
    fontWeight: "700",
    color: "#0F172A",
  },
});
