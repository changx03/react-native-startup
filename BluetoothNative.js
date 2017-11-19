import {
  NativeModules,
  NativeEventEmitter
} from 'react-native';

const {
    BluetoothNative
} = NativeModules;
const BluttoothEmitter = new NativeEventEmitter(BluetoothNative);

export default {
  exampleMethod() {
    return BluetoothNative.exampleMethod();
  },
  bluetoothList() {
    return BluetoothNative.bluetoothList();
  },
  emitter: BluttoothEmitter,
  EXAMPLE_CONSTANT: BluetoothNative.EXAMPLE_CONSTANT,
};
