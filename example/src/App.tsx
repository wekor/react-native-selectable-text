import { Text, View, StyleSheet } from 'react-native';
import { SelectableTextView } from 'react-native-selectable-text';

export default function App() {
  return (
    <View style={styles.container}>
      <SelectableTextView
        selectionColor="#FF6B3530"
        style={styles.selectableText}
      >
        <Text style={styles.text}>
          Long press to select text and see the context menu. Copy and Select
          All are built-in, plus custom options like Share, Search, and
          Translate.
        </Text>
      </SelectableTextView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  selectableText: {
    padding: 16,
    backgroundColor: '#f5f5f5',
    borderRadius: 8,
  },
  text: {
    fontSize: 16,
    lineHeight: 24,
    color: '#333',
  },
});
