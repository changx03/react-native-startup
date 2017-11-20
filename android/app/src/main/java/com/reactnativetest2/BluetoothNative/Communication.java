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

    interface FirmwareInformationCallback {
        void onFirmwareInformation(Map<String, String> firmwareInformationMap);
    }

    interface SerialNumberCallback {
        void onSerialNumber(Communication.Result communicateResult, String serialNumber);
    }

    interface SendCallback {
        void onStatus(boolean result, Communication.Result communicateResult);
    }

}
