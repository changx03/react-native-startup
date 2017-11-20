package com.reactnativetest2.BluetoothNative;

import android.content.Context;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.IPeripheralCommandParser;

import java.util.Map;

/**
 * Created by Luke Chang on 20/11/2017.
 */

public class Communication {
    @SuppressWarnings("unused")
    public enum Result {
        Success,
        ErrorUnknown,
        ErrorOpenPort,
        ErrorBeginCheckedBlock,
        ErrorEndCheckedBlock,
        ErrorWritePort,
        ErrorReadPort,
    }

    interface StatusCallback {
        void onStatus(StarPrinterStatus result);
    }

    interface SendCallback {
        void onStatus(boolean result, Communication.Result communicateResult);
    }

    public static void sendCommands(Object lock, byte[] commands, String portName, SendCallback callback) {
        SendCommandThread thread = new SendCommandThread(lock, commands, portName, callback);
        thread.start();
    }

}
