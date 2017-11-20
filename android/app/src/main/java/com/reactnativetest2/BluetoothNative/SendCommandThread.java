package com.reactnativetest2.BluetoothNative;

import android.content.Context;
import android.os.Looper;
import android.os.Handler;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;


/**
 * Created by Luke Chang on 20/11/2017.
 */

public class SendCommandThread extends Thread {
    private final Object _lock;
    private Communication.SendCallback _callback;
    private byte[] _commands;

    private String _portName;
    private StarIOPort _port;
    private static final String _portSettings = "Portable";
    private static int _timeout = 1000;

    public SendCommandThread(Object lock, byte[] commands, String portName, Communication.SendCallback callback) {
        _lock = lock;
        _commands = commands;
        _portName = portName;
        _callback = callback;
    }

    @Override
    public void run() {
        Communication.Result communicateResult = Communication.Result.ErrorOpenPort;
        boolean result = false;

        synchronized (_lock) {
            try {
                if (_port == null) {
                    _port = StarIOPort.getPort(_portName, _portSettings, _timeout);
                }
                if (_port == null) {
                    communicateResult = Communication.Result.ErrorOpenPort;
                    resultSendCallback(false, communicateResult, _callback);
                    return;
                }

                StarPrinterStatus status;
                communicateResult = Communication.Result.ErrorBeginCheckedBlock;
                status = _port.beginCheckedBlock();
                if (status.offline) {
                    throw new StarIOPortException("A printer is offline.");
                }
                // send content to bluetooth
                communicateResult = Communication.Result.ErrorWritePort;
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

            if (_port != null && _portName != null) {
                try {
                    StarIOPort.releasePort(_port);
                } catch (StarIOPortException e) {
                    // Nothing
                }
                _port = null;
            }

            resultSendCallback(result, communicateResult, _callback);
        }
    }

    private static void resultSendCallback(final boolean result, final Communication.Result communicateResult, final Communication.SendCallback callback) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStatus(result, communicateResult);
                }
            });
        }
    }
}
