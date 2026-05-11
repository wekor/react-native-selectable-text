import {
  codegenNativeComponent,
  type ColorValue,
  type ViewProps,
} from 'react-native';
import type {
  DirectEventHandler,
  Float,
  Int32,
} from 'react-native/Libraries/Types/CodegenTypes';

export interface SelectionEvent {
  chosenOption: string;
  highlightedText: string;
}

interface NativeProps extends ViewProps {
  value?: string;
  menuOptions?: readonly string[];
  selectionColor?: ColorValue;
  textColor?: ColorValue;
  fontSize?: Float;
  lineHeight?: Float;
  letterSpacing?: Float;
  fontFamily?: string;
  fontWeight?: string;
  fontStyle?: string;
  textAlign?: string;
  numberOfLines?: Int32;
  onSelection?: DirectEventHandler<SelectionEvent>;
}

export default codegenNativeComponent<NativeProps>('SelectableTextView');
