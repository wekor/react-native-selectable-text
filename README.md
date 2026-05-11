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

`SelectableText` works as a drop-in for `Text`. Children must be a single string.

```tsx
import { SelectableText } from '@wekor/react-native-selectable-text';

function App() {
  return (
    <SelectableText
      menuOptions={['Share', 'Search', 'Translate']}
      selectionColor="#FF6B3530"
      style={{ fontSize: 16, lineHeight: 24, color: '#222' }}
      onSelection={(event) => {
        const { chosenOption, highlightedText } = event.nativeEvent;
        console.log(chosenOption, highlightedText);
      }}
    >
      Long press to select text and see the context menu.
    </SelectableText>
  );
}
```

## Props

| Prop | Type | Required | Description |
|------|------|----------|-------------|
| `children` | `string` | Yes | Text content. Must be a plain string — nested `Text` or other elements are not supported |
| `style` | `TextStyle` | No | Text and layout style. Text properties (`color`, `fontSize`, `lineHeight`, `fontWeight`, `fontStyle`, `fontFamily`, `letterSpacing`, `textAlign`) drive both the native rendering and the shadow-node measurement; remaining properties (padding, background, etc.) are applied to the underlying view |
| `numberOfLines` | `number` | No | Maximum number of lines; tail-truncates beyond this |
| `menuOptions` | `string[]` | No | Additional custom menu items. **Copy** and **Select All** are always included by default. Do not pass "Copy" or "Select All" here, as it will create duplicates |
| `selectionColor` | `ColorValue` | No | Highlight color for selected text |
| `onSelection` | `(event) => void` | No | Callback when a custom menu item is tapped. `event.nativeEvent` contains `chosenOption` and `highlightedText` |

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
