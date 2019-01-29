package com.branch.www.screencapture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 */
public class PreviewPictureActivity extends FragmentActivity implements GlobalScreenshot.onScreenShotListener {

  public static final Intent newIntent(Context context) {
    Intent intent = new Intent(context, PreviewPictureActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    return intent;
  }

  private ImageView mPreviewImageView;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_preview_layout);
    mPreviewImageView = (ImageView) findViewById(R.id.preview_image);

    GlobalScreenshot screenshot = new GlobalScreenshot(getApplicationContext());

    Bitmap bitmap = ((ScreenCaptureApplication) getApplication()).getmScreenCaptureBitmap();

      //系统相册目录
      String galleryPath= Environment.getExternalStorageDirectory()
              + File.separator + Environment.DIRECTORY_DCIM
              +File.separator+"Camera"+File.separator;

      saveImage(bitmap,galleryPath,"testImage123");
      System.out.println("@@@@@@@@@ "+getBitmapPixel(bitmap,100,100));


//    Log.e("ryze", "预览图片");
//    mPreviewImageView.setImageBitmap(bitmap);
//    mPreviewImageView.setVisibility(View.GONE);
//
//    if (bitmap != null) {
//      screenshot.takeScreenshot(bitmap, this, true, true);
//    }

  }

  @Override
  public void onStartShot() {

  }

  @Override
  public void onFinishShot(boolean success) {
    mPreviewImageView.setVisibility(View.VISIBLE);
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

    public String getBitmapPixel(Bitmap bitmap, int x, int y){
        int color = bitmap.getPixel(x, y);
//        int r = Color.red(color);
//        int g = Color.green(color);
//        int b = Color.blue(color);
        int a = Color.alpha(color);
        String r1=Integer.toHexString(Color.red(color));
        String g1=Integer.toHexString(Color.green(color));
        String b1=Integer.toHexString(Color.blue(color));
        String colorStr=r1+g1+b1+a;
        return colorStr;
    }
}
