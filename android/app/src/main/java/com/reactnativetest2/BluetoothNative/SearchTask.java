package com.reactnativetest2.BluetoothNative;

import android.os.AsyncTask;
import android.view.textservice.TextInfo;

import java.util.ArrayList;
import java.util.List;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;

/**
 * Created by sunfishcc on 19/11/17.
 */

public class SearchTask extends AsyncTask<String, Void, Void> {
    private List<PortInfo> mPortList;

    public SearchTask() {
        super();
    }

    @Override
    protected Void doInBackground(String... interfaceType) {
        try {
            mPortList = StarIOPort.searchPrinter(interfaceType[0]);
        }
        catch (StarIOPortException e) {
            mPortList = new ArrayList<>();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void doNotUse) {
        for (PortInfo info : mPortList) {
            addItem(info);
        }
    }

    private void addItem(PortInfo info) {
        List<TextInfo> textList = new ArrayList<>();
        List<ImgInfo> imgList = new ArrayList<>();
        String modalName = "";
        String portName = "";
        String macAddress = "";

        // Bluetooth
        if (info.getPortName().startsWith(PrinterSetting.IF_TYPE_BLUETOOTH)) {
            modalName = info.getPortName().substring(PrinterSetting.IF_TYPE_BLUETOOTH.length());
            portName = PrinterSetting.IF_TYPE_BLUETOOTH + info.getMacAddress();
            macAddress = info.getMacAddress();
        }

        textList.add(new TextInfo(modalName));
        textList.add(new TextInfo(portName));

        // Bluetooth
        if (info.getPortName().startsWith(PrinterSetting.IF_TYPE_BLUETOOTH)) {
            textList.add(new TextInfo("("+macAddress+")"));
        }

        // We will use bluetooth only. PrinterSetting should be fixed.
//        PrinterSetting setting = new PrinterSetting();
//
//        if (setting.getPortName().equals(portName)) {
//            // add check icon to imgList
//        }
//
        for (TextInfo i: textList) {
            System.out.println(i);
        }

    }
}
