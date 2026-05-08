import {
  codegenNativeComponent,
  type ColorValue,
  type ViewProps,
} from 'react-native';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

export interface SelectionEvent {
  chosenOption: string;
  highlightedText: string;
}

interface NativeProps extends ViewProps {
  menuOptions?: readonly string[];
  selectionColor?: ColorValue;
  onSelection?: DirectEventHandler<SelectionEvent>;
}

export default codegenNativeComponent<NativeProps>('SelectableTextView');
