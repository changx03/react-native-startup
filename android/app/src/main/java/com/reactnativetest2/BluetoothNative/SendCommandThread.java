package com.reactnativetest2.BluetoothNative;

import android.content.Context;

import com.starmicronics.stario.StarIOPort;

/**
 * Created by Luke Chang on 20/11/2017.
 */

public class SendCommandThread extends Thread {
    private Communication.SendCallback _callback;
    private byte[] _commands;

    private String _portName;
    private static final String _portSettings = "Portable";
    private static int _timeout = 1000;
    private Context _context;

    public SendCommandThread(byte[] commands, String portName, Context context, Communication.SendCallback callback) {
        _commands = commands;
        _portName = portName;
        _context = context;
        _callback = callback;
    }
}
