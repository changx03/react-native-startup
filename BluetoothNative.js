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
    emitter: BluttoothEmitter,
    EXAMPLE_CONSTANT: BluetoothNative.EXAMPLE_CONSTANT,
};
