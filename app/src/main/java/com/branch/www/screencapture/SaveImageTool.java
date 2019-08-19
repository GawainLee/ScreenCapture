package com.branch.www.screencapture;


import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveImageTool {

    private String saveImagePath;
    private String saveImageName;
    private Bitmap imageBitmap;

    public SaveImageTool() {
    }

    public SaveImageTool(String saveImagePath, String saveImageName, Bitmap imageBitmap) {
        this.saveImagePath = saveImagePath;
        this.saveImageName = saveImageName;
        this.imageBitmap = imageBitmap;
    }

    public String getSaveImagePath() {
        return saveImagePath;
    }

    public void setSaveImagePath(String saveImagePath) {
        this.saveImagePath = saveImagePath;
    }

    public String getSaveImageName() {
        return saveImageName;
    }

    public void setSaveImageName(String saveImageName) {
        this.saveImageName = saveImageName;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    /**
     * 保存图片到本地路径
     * @param bmp
     * @param path
     * @param fileName
     * @return
     */
    public static File saveImage(Bitmap bmp, String path, String fileName) {
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir,  fileName  + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
