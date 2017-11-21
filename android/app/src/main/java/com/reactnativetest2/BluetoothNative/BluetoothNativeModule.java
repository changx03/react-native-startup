package com.reactnativetest2.BluetoothNative;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starioextension.StarIoExt.Emulation;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by sunfishcc on 19/11/17.
 * <p>
 * Known bug in Mashmallow: E/Surface: getSlotFromBufferLocked: unknown buffer: 0xb9a03ee8
 */

public class BluetoothNativeModule extends ReactContextBaseJavaModule {
    private static final String REACT_CLASS = "BluetoothNative";
    @SuppressLint("SimpleDateFormat")
    public static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private final Object _starIOLock;
    private final static Charset _encoding = Charset.forName("UTF-8");

    private static ReactApplicationContext reactContext;
    private ICommandBuilder _builder;
    private StarIOPort _port;

    BluetoothNativeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        BluetoothNativeModule.reactContext = reactContext;
        _starIOLock = new Object();
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("ALIGN_LEFT", ICommandBuilder.AlignmentPosition.Left.ordinal());
        constants.put("ALIGN_CENTER", ICommandBuilder.AlignmentPosition.Center.ordinal());
        constants.put("ALIGN_RIGHT", ICommandBuilder.AlignmentPosition.Right.ordinal());
        constants.put("BARCODE_DEFAULT_HEIGHT", 60);
        constants.put("BARCODE_WIDTH_SMALL", ICommandBuilder.BarcodeWidth.Mode1.ordinal());
        constants.put("BARCODE_WIDTH_MEDIUM", ICommandBuilder.BarcodeWidth.Mode2.ordinal());
        constants.put("BARCODE_WIDTH_LARGE", ICommandBuilder.BarcodeWidth.Mode3.ordinal());
        return constants;
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void bluetoothList(Promise promise) {
        System.out.println("execute bluetoothList()");
        SearchTask searchTask = new SearchTask();
        List<PortInfo> portList;
        try {
            portList = searchTask.execute(PrinterSetting.IF_TYPE_BLUETOOTH).get();
            List<String> deviceList = getDeviceList(portList);
            WritableArray rnArray = Arguments.createArray();
            for (String device : deviceList) {
                rnArray.pushString(device);
            }
            promise.resolve(rnArray);
        } catch (InterruptedException e) {
            promise.reject("Interrupted_Exception", e);
        } catch (ExecutionException e) {
            promise.reject("Execution_Exception", e);
        }
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void append(String content) {
        _builder.append(content.getBytes(_encoding));
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void appendAlignment(int position) {
        _builder.appendAlignment(ICommandBuilder.AlignmentPosition.values()[position]);
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void appendWithAlignment(String content, int position) {
        _builder.appendAlignment(content.getBytes(_encoding), ICommandBuilder.AlignmentPosition.values()[position]);
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void appendMultiple(String content, int multiplier) {
        _builder.appendMultiple(content.getBytes(_encoding), multiplier, multiplier);
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void appendEmphasis(String content) {
        _builder.appendEmphasis(content.getBytes(_encoding));
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void appendBarcode(String barcode, int width, int height, Boolean showCharacters) {
        _builder.appendBarcode(("{B" + barcode).getBytes(Charset.forName("US-ASCII")), ICommandBuilder.BarcodeSymbology.Code128,
                ICommandBuilder.BarcodeWidth.values()[width], height, showCharacters);
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void appendLineSeparator() {
        _builder.append(("------------------------------------------------\n").getBytes(_encoding));
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void beginDocument() {
        System.out.println("execute beginDocument()");
        _builder = StarIoExt.createCommandBuilder(getEmulation());
        _builder.beginDocument();
        _builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);
        _builder.appendInternational(ICommandBuilder.InternationalType.USA);
        _builder.appendCharacterSpace(0);
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void endDocument() {
        System.out.println("execute endDocument()");
        _builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        _builder.endDocument();
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void print(String portName) {
        System.out.println("execute print()");

        if (_port != null) {
            System.out.println("Port:" + _port.getPortName());
        } else {
            System.out.println("Port: null");
        }
        System.out.println("Start: " + dateFormat.format(new Date()));
        Communication.sendCommands(_starIOLock, _builder.getCommands(), _port, portName, _callback);
    }

//    @SuppressWarnings("unused")
//    @ReactMethod
//    public void exampleMethod () {
//        // An example native method that you will expose to React
//        // https://facebook.github.io/react-native/docs/native-modules-android.html#the-toast-module
//        final WritableMap event = Arguments.createMap();
//        event.putString("greeting", "Hello world");
//        emitDeviceEvent("EXAMPLE_EVENT", event);
//    }

    private static void emitDeviceEvent(String eventName, @Nullable WritableMap eventData) {
        // A method for emitting from the native side to JS
        // https://facebook.github.io/react-native/docs/native-modules-android.html#sending-events-to-javascript
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, eventData);
    }

    private Emulation getEmulation() {
        return Emulation.StarPRNT;
    }

    @SuppressLint("StaticFieldLeak")
    private class SearchTask extends AsyncTask<String, Void, List<PortInfo>> {
        private List<PortInfo> mPortList;

        SearchTask() {
            super();
        }

        @Override
        protected List<PortInfo> doInBackground(String... interfaceType) {
            try {
                mPortList = StarIOPort.searchPrinter(interfaceType[0]);
            } catch (StarIOPortException e) {
                mPortList = new ArrayList<>();
            }
            return mPortList;
        }

        @Override
        protected void onPostExecute(List<PortInfo> result) {
            List<String> devices = new ArrayList<>();
            for (PortInfo info : mPortList) {
                String device = addItem(info);
                System.out.println("device: " + device);
                devices.add(device);
            }
            WritableArray deviceArray = new WritableNativeArray();
            for (String device : devices) {
                deviceArray.pushString(device);
            }
        }
    }

    private static String addItem(PortInfo info) {
        String modalName = "";
        String portName = "";

        // Bluetooth
        if (info.getPortName().startsWith(PrinterSetting.IF_TYPE_BLUETOOTH)) {
            modalName = info.getPortName().substring(PrinterSetting.IF_TYPE_BLUETOOTH.length());
            portName = PrinterSetting.IF_TYPE_BLUETOOTH + info.getMacAddress();
        }
        return modalName + " (" + portName + ")";
    }

    private List<String> getDeviceList(List<PortInfo> portList) {
        List<String> deviceList = new ArrayList<>();
        for (PortInfo info : portList) {
            deviceList.add(addItem(info));
        }
        return deviceList;
    }

    private final Communication.SendCallback _callback = new Communication.SendCallback() {
        @Override
        public void onStatus(boolean result, Communication.Result communicateResult, StarIOPort port) {
            System.out.println("End of Print call back: " + dateFormat.format(new Date()));
            _port = port;
            String msg;
            switch (communicateResult) {
                case Success:
                    msg = "Success!";
                    break;
                case ErrorOpenPort:
                    msg = "Fail to openPort";
                    break;
                case ErrorBeginCheckedBlock:
                    msg = "Printer is offline (beginCheckedBlock)";
                    break;
                case ErrorEndCheckedBlock:
                    msg = "Printer is offline (endCheckedBlock)";
                    break;
                case ErrorReadPort:
                    msg = "Read port error (readPort)";
                    break;
                case ErrorWritePort:
                    msg = "Write port error (writePort)";
                    break;
                default:
                    msg = "Unknown error";
                    break;
            }
            Toast.makeText(getReactApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    };
}
