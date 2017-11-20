import { NativeModules, NativeEventEmitter } from 'react-native';

const { BluetoothNative } = NativeModules;
const BluttoothEmitter = new NativeEventEmitter(BluetoothNative);

export default {
  ALIGN_LEFT: BluetoothNative.ALIGN_LEFT,
  ALIGN_CENTER: BluetoothNative.ALIGN_CENTER,
  ALIGN_RIGHT: BluetoothNative.ALIGN_RIGHT,
  BARCODE_DEFAULT_HEIGHT: BluetoothNative.BARCODE_DEFAULT_HEIGHT,
  BARCODE_WIDTH_SMALL: BluetoothNative.BARCODE_WIDTH_SMALL,
  BARCODE_WIDTH_MEDIUM: BluetoothNative.BARCODE_WIDTH_MEDIUM,
  BARCODE_WIDTH_LARGE: BluetoothNative.BARCODE_WIDTH_LARGE,
  bluetoothList() {
    return BluetoothNative.bluetoothList();
  },
  print(portName) {
    return BluetoothNative.print(portName);
  },
  beginDocument() {
    return BluetoothNative.beginDocument();
  },
  endDocument() {
    return BluetoothNative.endDocument();
  },
  append(content) {
    return BluetoothNative.append(content);
  },
  appendAlignment() {
    return BluetoothNative.appendAlignment(position);
  },
  appendWithAlignment(content, position) {
    return BluetoothNative.appendWithAlignment(content, position);
  },
  appendMultiple(content, multiplier) {
    return BluetoothNative.appendMultiple(content, multiplier);
  },
  appendEmphasis(content) {
    return BluetoothNative.appendEmphasis(content);
  },
  appendBarcode(barcode, width, height, showCharacters) {
    return BluetoothNative.appendBarcode(
      barcode,
      width,
      height,
      showCharacters
    );
  },
  appendLineSeparator() {
    return BluetoothNative.appendLineSeparator();
  },
  emmiter: BluttoothEmitter,
};
