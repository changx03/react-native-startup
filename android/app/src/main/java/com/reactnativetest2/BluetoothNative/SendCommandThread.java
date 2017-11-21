package com.reactnativetest2.BluetoothNative;

import android.os.Handler;
import android.os.Looper;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

import java.util.Date;


/**
 * Created by Luke Chang on 20/11/2017.
 */

public class SendCommandThread extends Thread {
    private static final String PORT_SETTING = "Portable";
    private static final int TIMEOUT = 1000;

    private final Object _lock;
    private Communication.SendCallback _callback;
    private byte[] _commands;
    private String _portName;
    private StarIOPort _port;

    public SendCommandThread(Object lock, byte[] commands, StarIOPort port, String portName, Communication.SendCallback callback) {
        _lock = lock;
        _port = port;
        _commands = commands;
        _portName = portName;
        _callback = callback;
    }

    @Override
    public void run() {
        Communication.Result communicateResult = Communication.Result.ErrorOpenPort;
        Boolean result = false;

        synchronized (_lock) {
            try {
                if (_port == null) {
                    System.out.println("Start getPort: " + BluetoothNativeModule.dateFormat.format(new Date()));
                    _port = StarIOPort.getPort(_portName, PORT_SETTING, TIMEOUT);
                }
                if (_port == null) {
                    communicateResult = Communication.Result.ErrorOpenPort;
                    resultSendCallback(false, communicateResult, _port, _callback);
                    return;
                }

                System.out.println("Start beginCheckedBlock: " + BluetoothNativeModule.dateFormat.format(new Date()));
                StarPrinterStatus status;
                communicateResult = Communication.Result.ErrorBeginCheckedBlock;
                status = _port.beginCheckedBlock();
                if (status.offline) {
                    throw new StarIOPortException("The printer is offline.");
                }
                // send content to bluetooth
                communicateResult = Communication.Result.ErrorWritePort;

                System.out.println("Start writePort: " + BluetoothNativeModule.dateFormat.format(new Date()));
                _port.writePort(_commands, 0, _commands.length);

                _port.setEndCheckedBlockTimeoutMillis(30000);   // 30,000ms

                status = _port.endCheckedBlock();
                if (status.coverOpen) {
                    throw new StarIOPortException("Printer cover is open");
                } else if (status.receiptPaperEmpty) {
                    throw new StarIOPortException("Receipt paper is empty");
                } else if (status.offline) {
                    throw new StarIOPortException("Printer is offline");
                }
                result = true;
                communicateResult = Communication.Result.Success;
            } catch (StarIOPortException e) {
                System.err.println("StarIOPortException: " + e.getMessage());
            }

//            if (_port != null && _portName != null) {
//                try {
//                    StarIOPort.releasePort(_port);
//                } catch (StarIOPortException e) {
//                    // Nothing
//                }
//            }

            resultSendCallback(result, communicateResult, _port, _callback);
        }
    }

    private static void resultSendCallback(final boolean result, final Communication.Result communicateResult,
                                           final StarIOPort port, final Communication.SendCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStatus(result, communicateResult, port);
                }
            });
        }
    }
}
