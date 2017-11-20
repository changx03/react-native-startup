import { NativeModules, NativeEventEmitter } from 'react-native';

const { BluetoothNative } = NativeModules;
const BluttoothEmitter = new NativeEventEmitter(BluetoothNative);

export default {
  bluetoothList() {
    return BluetoothNative.bluetoothList();
  },
  print(portName) {
    return BluetoothNative.print(portName);
  },
  append(lineContent) {
    return BluetoothNative.append(lineContent);
  },
  beginDocument() {
    return BluetoothNative.beginDocument();
  },
  endDocument() {
    return BluetoothNative.endDocument();
  },
  emitter: BluttoothEmitter,
  EXAMPLE_CONSTANT: BluetoothNative.EXAMPLE_CONSTANT,
};
