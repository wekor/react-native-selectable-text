import {
  StyleSheet,
  type ColorValue,
  type NativeSyntheticEvent,
  type StyleProp,
  type TextStyle,
  type ViewProps,
  type ViewStyle,
} from 'react-native';
import NativeSelectableText from './SelectableTextViewNativeComponent';
import type { SelectionEvent } from './SelectableTextViewNativeComponent';

export type { SelectionEvent };

export interface SelectableTextProps extends Omit<
  ViewProps,
  'style' | 'children'
> {
  children: string;
  style?: StyleProp<TextStyle>;
  menuOptions?: readonly string[];
  selectionColor?: ColorValue;
  numberOfLines?: number;
  onSelection?: (event: NativeSyntheticEvent<SelectionEvent>) => void;
}

const TEXT_STYLE_PROPS = new Set([
  'color',
  'fontFamily',
  'fontSize',
  'fontStyle',
  'fontWeight',
  'letterSpacing',
  'lineHeight',
  'textAlign',
]);

function splitStyle(style: StyleProp<TextStyle>) {
  const flattened = StyleSheet.flatten(style) ?? {};
  const textStyle: Record<string, unknown> = {};
  const containerStyle: Record<string, unknown> = {};

  for (const [key, value] of Object.entries(flattened)) {
    if (TEXT_STYLE_PROPS.has(key)) {
      textStyle[key] = value;
    } else {
      containerStyle[key] = value;
    }
  }

  return {
    textStyle: textStyle as TextStyle,
    containerStyle: containerStyle as ViewStyle,
  };
}

export default function SelectableText({
  children,
  style,
  menuOptions,
  selectionColor,
  numberOfLines,
  onSelection,
  ...viewProps
}: SelectableTextProps) {
  const { textStyle, containerStyle } = splitStyle(style);

  // RN allows numeric fontWeight literals (100..900), but codegen wants a
  // string. Other style props are already string unions, so no conversion.
  const fontWeight =
    textStyle.fontWeight != null ? String(textStyle.fontWeight) : undefined;

  return (
    <NativeSelectableText
      {...viewProps}
      style={containerStyle}
      value={children}
      menuOptions={menuOptions}
      selectionColor={selectionColor}
      numberOfLines={numberOfLines}
      onSelection={onSelection}
      fontFamily={textStyle.fontFamily}
      fontSize={textStyle.fontSize}
      fontStyle={textStyle.fontStyle}
      fontWeight={fontWeight}
      letterSpacing={textStyle.letterSpacing}
      lineHeight={textStyle.lineHeight}
      textAlign={textStyle.textAlign}
      textColor={textStyle.color}
    />
  );
}
