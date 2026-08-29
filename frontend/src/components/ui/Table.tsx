import { Fragment, ReactNode } from "react";
import { ScrollView, StyleSheet, Text, View } from "react-native";

export interface TableColumn<T> {
  key: string;
  header: string;
  width?: number;
  render: (row: T) => ReactNode;
}

interface TableProps<T> {
  columns: TableColumn<T>[];
  data: T[];
  keyExtractor: (row: T) => string;
}

/**
 * Lightweight horizontally-scrollable table. Used sparingly — most list
 * screens use Card rows instead — but a few dense views (inventory manager's
 * SKU/quantity/threshold grid, admin moderation queue) read better as a
 * table per docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md §9.
 */
export function Table<T>({ columns, data, keyExtractor }: TableProps<T>) {
  return (
    <ScrollView horizontal showsHorizontalScrollIndicator={false}>
      <View>
        <View style={styles.headerRow}>
          {columns.map((col) => (
            <Text key={col.key} style={[styles.headerCell, { width: col.width ?? 120 }]}>
              {col.header}
            </Text>
          ))}
        </View>
        {data.map((row) => (
          <View key={keyExtractor(row)} style={styles.dataRow}>
            {columns.map((col) => (
              <Fragment key={col.key}>
                <View
                  style={{ width: col.width ?? 120, paddingVertical: 10, paddingHorizontal: 8 }}
                >
                  {typeof col.render(row) === "string" ? (
                    <Text style={styles.cellText}>{col.render(row)}</Text>
                  ) : (
                    col.render(row)
                  )}
                </View>
              </Fragment>
            ))}
          </View>
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  headerRow: {
    flexDirection: "row",
    backgroundColor: "#F1F5F9",
    borderBottomWidth: 1,
    borderColor: "#E2E8F0",
  },
  headerCell: {
    paddingVertical: 10,
    paddingHorizontal: 8,
    fontSize: 12,
    fontWeight: "700",
    color: "#475569",
    textTransform: "uppercase",
  },
  dataRow: {
    flexDirection: "row",
    borderBottomWidth: 1,
    borderColor: "#F1F5F9",
  },
  cellText: {
    fontSize: 13,
    color: "#0F172A",
  },
});
