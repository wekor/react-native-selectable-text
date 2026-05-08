# @wekor/react-native-selectable-text

A React Native Fabric component that makes text selectable with customizable context menus.

| iOS | Android |
|-----|---------|
| 15.1+ | API 24+ |

## Features

- Text selection with native selection handles
- Built-in **Copy** and **Select All** actions
- Custom context menu options via `menuOptions`
- Selection event callback via `onSelection`
- Customizable selection highlight color
- React Native New Architecture (Fabric) only

## Installation

```sh
npm install @wekor/react-native-selectable-text
```

or

```sh
yarn add @wekor/react-native-selectable-text
```

### iOS

```sh
cd ios && pod install
```

## Usage

```tsx
import { Text } from 'react-native';
import { SelectableTextView } from '@wekor/react-native-selectable-text';

function App() {
  return (
    <SelectableTextView
      menuOptions={['Share', 'Search', 'Translate']}
      selectionColor="#FF6B3530"
      onSelection={(event) => {
        const { chosenOption, highlightedText } = event.nativeEvent;
        console.log(chosenOption, highlightedText);
      }}
    >
      <Text style={{ fontSize: 16, lineHeight: 24 }}>
        Long press to select text and see the context menu.
      </Text>
    </SelectableTextView>
  );
}
```

## Props

| Prop | Type | Required | Description |
|------|------|----------|-------------|
| `menuOptions` | `string[]` | No | Additional custom menu items. **Copy** and **Select All** are always included by default. Do not pass "Copy" or "Select All" here, as it will create duplicates |
| `selectionColor` | `ColorValue` | No | Highlight color for selected text |
| `onSelection` | `(event) => void` | No | Callback when a custom menu item is tapped. `event.nativeEvent` contains `chosenOption` and `highlightedText` |

Inherits all [View](https://reactnative.dev/docs/view#props) props.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
