import { useState } from 'react';
import { Button, Text, View, StyleSheet } from 'react-native';
import { SelectableTextView } from '@wekor/react-native-selectable-text';

export default function App() {
  const [isRed, setIsRed] = useState(false);

  return (
    <View style={styles.container}>
      <SelectableTextView
        selectionColor="#FF6B3530"
        style={styles.selectableText}
      >
        <Text style={[styles.text, isRed && styles.redText]}>
          Long press to select text and see the context menu. Copy and Select
          All are built-in, plus custom options like Share, Search, and
          Translate.
        </Text>
      </SelectableTextView>

      <View style={styles.buttonContainer}>
        <Button
          title={isRed ? '恢复默认颜色' : '字体变红'}
          onPress={() => setIsRed((prev) => !prev)}
        />
      </View>
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
  redText: {
    color: 'red',
  },
  buttonContainer: {
    marginTop: 20,
  },
});
