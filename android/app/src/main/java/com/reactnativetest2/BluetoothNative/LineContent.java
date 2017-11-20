package com.reactnativetest2.BluetoothNative;

import com.starmicronics.starioextension.ICommandBuilder.AlignmentPosition;

/**
 * Created by Luke Chang on 20/11/2017.
 */

public class LineContent {
    public String text;
    public AlignmentPosition align;
    public int multiplier;
    public boolean isBarcode;

    public LineContent(String text) {
        this.text = text;
    }

    public LineContent(String text, AlignmentPosition align) {
        this.text = text;
        this.align = align;
    }

    public LineContent(String text, AlignmentPosition align, int multiplier) {
        this.text = text;
        this.align = align;
        this.multiplier = multiplier;
    }

    public LineContent(String text, AlignmentPosition align, int multiplier, boolean isBarcode) {
        this.text = text;
        this.align = align;
        this.multiplier = multiplier;
        this.isBarcode = isBarcode;
    }
}
