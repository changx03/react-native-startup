package com.reactnativetest2.BluetoothNative;

/**
 * Created by sunfishcc on 19/11/17.
 */

public class ImgInfo {
    private int mImgResourceID;
    private int mResourceID;

    ImgInfo(int imgResourceID) {
        this.mImgResourceID = imgResourceID;
    }

    ImgInfo(int imgResourceID, int reousrceID) {
        this.mImgResourceID = imgResourceID;
        this.mResourceID = reousrceID;
    }

    public int getImgResourceID() {
        return mImgResourceID;
    }

    public int getResourceID() {
        return mResourceID;
    }
}
