package com.branch.www.screencapture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.os.AsyncTaskCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.branch.www.screencapture.TargetImageTool.AnalystListTargetImagePoints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

//import android.os.Handler;

/**
 * Created by branch on 2016-5-25.
 *
 * 启动悬浮窗界面
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FloatWindowsService extends Service {

  public static Intent newIntent(Context context, Intent mResultData) {

    Intent intent = new Intent(context, FloatWindowsService.class);

    if (mResultData != null) {
      intent.putExtras(mResultData);
    }
    return intent;
  }

  private MediaProjection mMediaProjection;
  private VirtualDisplay mVirtualDisplay;

  private static Intent mResultData = null;


  private ImageReader mImageReader;
  private WindowManager mWindowManager;
  private WindowManager.LayoutParams mLayoutParams;
  private GestureDetector mGestureDetector;

  private ImageView mFloatView;

  private int mScreenWidth;
  private int mScreenHeight;
  private int mScreenDensity;

  private Thread thread;

  private String targetList = "";

  @Override
  public void onCreate() {
    super.onCreate();

    createFloatView();

    createImageReader();

    //read target image list

    String filePath= Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator+"Camera"+File.separator
            + "MyFile1.txt";

    targetList = TXTTool.ReadTxtFile(filePath);
//    System.out.println(targetList);
//    targetList = "1,777,203,90,90,92]2,790,204,255,255,255]3,794,204,90,90,92]4,794,222,255,255,255]5,794,228,90,90,92]6,790,228,255,255,255]7,784,217,90,90,92]8,782,226,90,90,92]9,776,222,255,255,255]10,784,217,90,90,92]}1,505,301,90,90,92]2,649,296,48,48,48]}";
  }

  public static Intent getResultData() {
    return mResultData;
  }

  public static void setResultData(Intent mResultData) {
    FloatWindowsService.mResultData = mResultData;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  private void createFloatView() {
    mGestureDetector = new GestureDetector(getApplicationContext(), new FloatGestrueTouchListener());
    mLayoutParams = new WindowManager.LayoutParams();
    mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

    DisplayMetrics metrics = new DisplayMetrics();
    mWindowManager.getDefaultDisplay().getMetrics(metrics);
    mScreenDensity = metrics.densityDpi;
    mScreenWidth = metrics.widthPixels;
    mScreenHeight = metrics.heightPixels;

    mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    mLayoutParams.format = PixelFormat.RGBA_8888;
    // 设置Window flag
    mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
    mLayoutParams.x = mScreenWidth;
    mLayoutParams.y = 100;
    mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
    mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


    mFloatView = new ImageView(getApplicationContext());
    mFloatView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_imagetool_crop));
    mWindowManager.addView(mFloatView, mLayoutParams);


    mFloatView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
      }
    });

  }

//  private void runAutoClick(){
//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//        while (true){
//          autoCupture();
////          System.out.println("Running Auto Click");
//          try {
//            sleep(20000);
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }
//        }
//      }
//    }).start();
//  }

//  public void autoCupture(){
//    //模拟触屏点击屏幕事件
//    int x = 0;
//    int y = 0;
//    long downTime = SystemClock.uptimeMillis();
//    final MotionEvent downEvent = MotionEvent.obtain(
//            downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
//    downTime += 500;
//    final MotionEvent upEvent = MotionEvent.obtain(
//            downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
//    //添加到webview_loading_round_iv上
//    mFloatView.onTouchEvent(downEvent);
//    mFloatView.onTouchEvent(upEvent);
//    downEvent.recycle();
//    upEvent.recycle();
//  }

  private class FloatGestrueTouchListener implements GestureDetector.OnGestureListener {
    int lastX, lastY;
    int paramX, paramY;

    @Override
    public boolean onDown(MotionEvent event) {
      lastX = (int) event.getRawX();
      lastY = (int) event.getRawY();
      paramX = mLayoutParams.x;
      paramY = mLayoutParams.y;
      return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      startScreenShot();
      return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      int dx = (int) e2.getRawX() - lastX;
      int dy = (int) e2.getRawY() - lastY;
      mLayoutParams.x = paramX + dx;
      mLayoutParams.y = paramY + dy;
      // 更新悬浮窗位置
      mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
      return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      return false;
    }
  }


  private void startScreenShot() {

    mFloatView.setVisibility(View.GONE);

    Handler handler1 = new Handler();
    handler1.postDelayed(new Runnable() {
      public void run() {
        //start virtual
        startVirtual();
      }
    }, 5);

    handler1.postDelayed(new Runnable() {
      public void run() {
        //capture the screen
        startCapture();

      }
    }, 30);
  }


  private void createImageReader() {

    mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1);

  }

  public void startVirtual() {
    if (mMediaProjection != null) {
      virtualDisplay();
    } else {
      setUpMediaProjection();
      virtualDisplay();
    }
  }

  public void setUpMediaProjection() {
    if (mResultData == null) {
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_LAUNCHER);
      startActivity(intent);
    } else {
      mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData);
    }
  }

  private MediaProjectionManager getMediaProjectionManager() {

    return (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
  }

  private void virtualDisplay() {
    mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
        mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
        mImageReader.getSurface(), null, null);
  }

  private void startCapture() {

    Image image = mImageReader.acquireLatestImage();

    if (image == null) {
      startScreenShot();
    } else {
      SaveTask mSaveTask = new SaveTask();
      AsyncTaskCompat.executeParallel(mSaveTask, image);
    }
  }


  public class SaveTask extends AsyncTask<Image, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(Image... params) {

      if (params == null || params.length < 1 || params[0] == null) {

        return null;
      }

      Image image = params[0];

      int width = image.getWidth();
      int height = image.getHeight();
      final Image.Plane[] planes = image.getPlanes();
      final ByteBuffer buffer = planes[0].getBuffer();
      //每个像素的间距
      int pixelStride = planes[0].getPixelStride();
      //总的间距
      int rowStride = planes[0].getRowStride();
      int rowPadding = rowStride - pixelStride * width;
      Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
      bitmap.copyPixelsFromBuffer(buffer);
      bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
      image.close();
      File fileImage = null;
      if (bitmap != null) {
        try {
          fileImage = new File(FileUtil.getScreenShotsName(getApplicationContext()));
          if (!fileImage.exists()) {
            fileImage.createNewFile();
          }
          FileOutputStream out = new FileOutputStream(fileImage);
          if (out != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(fileImage);
            media.setData(contentUri);
            sendBroadcast(media);
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
          fileImage = null;
        } catch (IOException e) {
          e.printStackTrace();
          fileImage = null;
        }
      }

      if (fileImage != null) {
        return bitmap;
      }
      return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      super.onPostExecute(bitmap);
      //预览图片
      if (bitmap != null) {

        ((ScreenCaptureApplication) getApplication()).setmScreenCaptureBitmap(bitmap);
        Log.e("ryze", "获取图片成功");

//        startActivity(PreviewPictureActivity.newIntent(getApplicationContext()));

        //create by Gawain
        bitmapCapture = bitmap;
        startDownLoad();
      }

      mFloatView.setVisibility(View.VISIBLE);

    }
  }


  private void tearDownMediaProjection() {
    if (mMediaProjection != null) {
      mMediaProjection.stop();
      mMediaProjection = null;
    }
  }

  private void stopVirtual() {
    if (mVirtualDisplay == null) {
      return;
    }
    mVirtualDisplay.release();
    mVirtualDisplay = null;
  }

  @Override
  public void onDestroy() {
    // to remove mFloatLayout from windowManager
    super.onDestroy();
    if (mFloatView != null) {
      mWindowManager.removeView(mFloatView);
    }
    stopVirtual();

    tearDownMediaProjection();
  }


  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    //create by gawain
//    startDownLoad();
    ////
    return super.onStartCommand(intent, flags, startId);
  }


  //create by gawain

  /**
   * 进度条的最大值
   */
  public static final int MAX_PROGRESS = 100;
  /**
   * 进度条的进度值
   */
  private int progress = 0;

  //create by Gawain
  private Intent intent = new Intent("com.branch.www.screencapture.MainActivity");

  //create by Gawain
  //pass to main bitmap
  public Bitmap bitmapCapture;

  //create by Gawain
  public CaptureBitmap captureBitmap = new CaptureBitmap();

  /**
   * 模拟下载任务，每秒钟更新一次
   */
  //create by Gawain
  public void startDownLoad(){
    new Thread(new Runnable() {

      @Override
      public void run() {

        if(bitmapCapture != null)
        {
          String message = analystBitmap(bitmapCapture);
          saveImage(bitmapCapture);
          intent.putExtra("progress",message);
          sendBroadcast(intent);
        }
      }
    }).start();
  }

  //create by Gawain
  public String analystBitmap(Bitmap bitmap)
  {
    //**************
    //create by gawain
    // analysis image
    AnalystListTargetImagePoints analystListTargetImagePoints = new AnalystListTargetImagePoints(this.targetList);
    FindTargetImageByPoint findTargetImageByPoint = new FindTargetImageByPoint();
    boolean isFind = findTargetImageByPoint.findTarget(bitmap, analystListTargetImagePoints.getListTargetImagePoints().getListTargetImagePoints().get(0).getImagePoints());
    System.out.println("@@@@@@@@@@**************  isFind " + isFind);
    return "check " + isFind;
    //**************
  }

  //create by Gawain
  public void saveImage(Bitmap bitmap)
  {

    //**************
    //save image gawain
    // 系统相册目录
    String galleryPath= Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            +File.separator+"Camera"+File.separator;
    SaveImageTool.saveImage(bitmap,galleryPath,"testImage123");
    //**************
  }

}

