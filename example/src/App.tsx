import { useMemo, useState } from 'react';
import {
  Button,
  Pressable,
  SafeAreaView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { FlashList } from '@shopify/flash-list';
import { SelectableText } from '@wekor/react-native-selectable-text';

type Mode = 'simple' | 'flashlist';

type Item = { id: number; text: string };

const SAMPLE_LINES = [
  'The quick brown fox jumps over the lazy dog near the riverbank.',
  'Pack my box with five dozen liquor jugs before the storm arrives.',
  '在一个安静的午后，阳光透过窗户洒在书页上，时间仿佛静止了。',
  'Long press anywhere in this paragraph to verify selection still works after recycling.',
  '红豆生南国，春来发几枝。愿君多采撷，此物最相思。',
  'Selection should engage on the very first long-press, regardless of scroll position.',
  '昨夜西风凋碧树，独上高楼，望尽天涯路。',
  'FlashList recycles cells aggressively — this is exactly where the bug used to hit.',
  '春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。',
  'If you can highlight this text and see the copy menu, the Android fix is working.',
];

const ITEMS: Item[] = Array.from({ length: 60 }, (_, i) => ({
  id: i,
  text: `[#${i}] ${SAMPLE_LINES[i % SAMPLE_LINES.length]}`,
}));

function SimpleScene() {
  const [isRed, setIsRed] = useState(false);

  return (
    <View style={styles.simpleContainer}>
      <SelectableText
        selectionColor="#FF6B3530"
        style={[styles.selectableText, styles.text, isRed && styles.redText]}
      >
        Long press to select text and see the context menu. Copy and Select All
        are built-in, plus custom options like Share, Search, and Translate.
      </SelectableText>

      <View style={styles.buttonContainer}>
        <Button
          title={isRed ? '恢复默认颜色' : '字体变红'}
          onPress={() => setIsRed((prev) => !prev)}
        />
      </View>
    </View>
  );
}

function FlashListScene() {
  const data = useMemo(() => ITEMS, []);

  return (
    <FlashList
      data={data}
      keyExtractor={(item) => String(item.id)}
      renderItem={({ item }) => (
        <Pressable
          onPress={() => console.log('[item pressed]', item.text)}
          style={({ pressed }) => [styles.row, pressed && styles.rowPressed]}
        >
          <Text style={styles.rowIndex}>#{item.id}</Text>
          <SelectableText
            selectionColor="#FF6B3530"
            style={[styles.rowSelectable, styles.rowText]}
          >
            {item.text}
          </SelectableText>
        </Pressable>
      )}
      contentContainerStyle={styles.flashContent}
    />
  );
}

export default function App() {
  const [mode, setMode] = useState<Mode>('simple');

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.tabBar}>
        <ModeTab
          label="Simple"
          active={mode === 'simple'}
          onPress={() => setMode('simple')}
        />
        <ModeTab
          label="FlashList"
          active={mode === 'flashlist'}
          onPress={() => setMode('flashlist')}
        />
      </View>

      <View style={styles.sceneWrapper}>
        {mode === 'simple' ? <SimpleScene /> : <FlashListScene />}
      </View>
    </SafeAreaView>
  );
}

function ModeTab({
  label,
  active,
  onPress,
}: {
  label: string;
  active: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable
      onPress={onPress}
      style={[styles.tab, active && styles.tabActive]}
    >
      <Text style={[styles.tabLabel, active && styles.tabLabelActive]}>
        {label}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  tabBar: {
    flexDirection: 'row',
    paddingHorizontal: 16,
    paddingTop: 8,
    paddingBottom: 8,
    gap: 8,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#ddd',
  },
  tab: {
    flex: 1,
    paddingVertical: 10,
    borderRadius: 8,
    backgroundColor: '#f0f0f0',
    alignItems: 'center',
  },
  tabActive: {
    backgroundColor: '#FF6B35',
  },
  tabLabel: {
    fontSize: 15,
    fontWeight: '500',
    color: '#333',
  },
  tabLabelActive: {
    color: '#fff',
  },
  sceneWrapper: {
    flex: 1,
  },
  simpleContainer: {
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
  flashContent: {
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  row: {
    marginBottom: 12,
    padding: 12,
    backgroundColor: '#fafafa',
    borderRadius: 8,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: '#e5e5e5',
  },
  rowPressed: {
    backgroundColor: '#efefef',
  },
  rowIndex: {
    fontSize: 12,
    color: '#888',
    marginBottom: 4,
    fontWeight: '600',
  },
  rowSelectable: {
    paddingVertical: 4,
  },
  rowText: {
    fontSize: 15,
    lineHeight: 22,
    color: '#222',
  },
});
