package com.branch.www.screencapture;

import android.graphics.Bitmap;

import java.io.Serializable;

public class CaptureBitmap implements Serializable {
    private Bitmap captureBitmap;

    public CaptureBitmap() {
    }

    public CaptureBitmap(Bitmap captureBitmap) {
        this.captureBitmap = captureBitmap;
    }

    public Bitmap getCaptureBitmap() {
        return captureBitmap;
    }

    public void setCaptureBitmap(Bitmap captureBitmap) {
        this.captureBitmap = captureBitmap;
    }
}
