//package com.reactnativetest2.BluetoothNative;
//
//import com.starmicronics.starioextension.ICommandBuilder;
//import com.starmicronics.starioextension.StarIoExt;
//import com.starmicronics.starioextension.StarIoExt.Emulation;
//
//import java.nio.charset.Charset;
//
///**
// * Created by Luke Chang on 20/11/2017.
// */
//
//public class PrinterFunctions {
//    private  ICommandBuilder _builder;
//
//    public PrinterFunctions(Emulation emulation) {
//        _builder = StarIoExt.createCommandBuilder(emulation);
//    }
//
//    public byte[] createTextReceiptData() {
//        _builder.beginDocument();
//        append3inchTextReceiptData(_builder, "Hello world\n");
//        _builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
//        _builder.endDocument();
//        return _builder.getCommands();
//    }
//
//    private void append3inchTextReceiptData(ICommandBuilder builder, String testContent) {
//        Charset encoding;
//
//        // Use utf8 as default
//        encoding = Charset.forName("UTF-8");
//        builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);
//
//        builder.appendInternational(ICommandBuilder.InternationalType.USA);
//        builder.appendCharacterSpace(0);
//
//        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
//        builder.append(testContent.getBytes(encoding));
//        builder.append(testContent.getBytes(encoding));
//        builder.appendAlignment(testContent.getBytes(encoding), ICommandBuilder.AlignmentPosition.Right);
//        builder.append(("\n").getBytes(encoding));
//        builder.append(("------------------------------------------------\n").getBytes(encoding));
//        builder.appendAlignment(("On your left\n").getBytes(encoding), ICommandBuilder.AlignmentPosition.Left);
//        builder.appendMultiple(("123456789012345678901\n").getBytes(encoding), 2, 2);
//        builder.appendEmphasis(testContent.getBytes(encoding));
//        builder.append(testContent.getBytes(encoding));
//        String nStr = "1234567890";
//        builder.append((nStr + nStr + nStr + nStr + "12345678\n").getBytes(encoding));
//        builder.appendMultiple((nStr + nStr + "1234\n").getBytes(encoding), 2, 2);
//        builder.appendMultiple((nStr + "123456\n").getBytes(encoding), 3, 3);
//        builder.appendMultiple((nStr + "12\n").getBytes(encoding), 4, 4);
//        builder.append(("------------------------------------------------\n").getBytes(encoding));
//        builder.append(("\n").getBytes(encoding));
//        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
//        builder.appendBarcode(("{BCR0000002147").getBytes(Charset.forName("US-ASCII")), ICommandBuilder.BarcodeSymbology.Code128, ICommandBuilder.BarcodeWidth.Mode3, 60, true);
//    }
//}
