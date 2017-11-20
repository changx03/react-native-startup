package com.reactnativetest2.BluetoothNative;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

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
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starioextension.StarIoExt.Emulation;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by sunfishcc on 19/11/17.
 */

public class BluetoothNativeModule extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "BluetoothNative";
    public static ReactApplicationContext reactContext;

    private int mLanguage = PrinterSetting.LANGUAGE_ENGLISH;
    private int mPaperSize = PrinterSetting.PAPER_SIZE_THREE_INCH;
    private final Object lock;
    private ICommandBuilder _builder;
    private final static Charset encoding = Charset.forName("UTF-8");

    public BluetoothNativeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        BluetoothNativeModule.reactContext = reactContext;
        lock = new Object();
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public Map<String, Object> getConstants() {
        // Export any constants to be used in your native module
        // https://facebook.github.io/react-native/docs/native-modules-android.html#the-toast-module
        final Map<String, Object> constants = new HashMap<>();
        constants.put("EXAMPLE_CONSTANT", "example");

        return constants;
    }

    @ReactMethod
    public void exampleMethod() {
        // An example native method that you will expose to React
        // https://facebook.github.io/react-native/docs/native-modules-android.html#the-toast-module
        System.out.println("execute exampleMethod()");

        final WritableMap event = Arguments.createMap();
        event.putString("greeting", "Hello world");
        emitDeviceEvent("EXAMPLE_EVENT", event);
    }

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

    @ReactMethod
    public void append(String lineContent, Promise promise) {
        try {
            _builder.append(lineContent.getBytes(encoding));
            promise.resolve("append");
        } catch (Exception e) {
            promise.reject("BUILDER_APPEND_ERROR", e);
        }
    }

    @ReactMethod
    public void beginDocument(Promise promise) {
        try {
            _builder = StarIoExt.createCommandBuilder(getEmulation());
            _builder.beginDocument();

            _builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);

            _builder.appendInternational(ICommandBuilder.InternationalType.USA);
            _builder.appendCharacterSpace(0);

//            _builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);

            promise.resolve("builder created");
        } catch (Exception e) {
            promise.reject("ICommandBuilder_ERROR", e);
        }
    }

    @ReactMethod
    public void endDocument(Promise promise) {
        try {
            _builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
            _builder.endDocument();
            promise.resolve("builder end");
        } catch (Exception e) {
            promise.reject("ICommandBuilder_ERROR", e);
        }
    }

    @ReactMethod
    public void print(String portName) {
        System.out.println("execute print()");
        StarIOPort port = null;
        synchronized (lock) {
            try {
                port = StarIOPort.getPort(portName, "Portable", 1000);
                StarPrinterStatus status = port.beginCheckedBlock();
                System.out.println(Arrays.toString(status.raw));
//                Emulation emulation = getEmulation();
                //byte[] command = new PrinterFunctions(emulation).createTextReceiptData();
                byte[] command = _builder.getCommands();
                port.writePort(command, 0, command.length);
                status = port.endCheckedBlock();
                if (!status.offline) {
                    System.out.println("Print successful end");
                } else {
                    // check status
                }

            } catch (StarIOPortException e) {

            } finally {
                try {
                    StarIOPort.releasePort(port);
                } catch (StarIOPortException e) {

                }
            }
        }
    }

    private Emulation getEmulation() {
        return Emulation.StarPRNT;
    }

    private static void emitDeviceEvent(String eventName, @Nullable WritableMap eventData) {
        // A method for emitting from the native side to JS
        // https://facebook.github.io/react-native/docs/native-modules-android.html#sending-events-to-javascript
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, eventData);
    }

    class SearchTask extends AsyncTask<String, Void, List<PortInfo>> {
        private List<PortInfo> mPortList;

        public SearchTask() {
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

    public static String addItem(PortInfo info) {
        String modalName = "";
        String portName = "";
//            String macAddress = "";

        // Bluetooth
        if (info.getPortName().startsWith(PrinterSetting.IF_TYPE_BLUETOOTH)) {
            modalName = info.getPortName().substring(PrinterSetting.IF_TYPE_BLUETOOTH.length());
            portName = PrinterSetting.IF_TYPE_BLUETOOTH + info.getMacAddress();
//                macAddress = info.getMacAddress();
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
}
