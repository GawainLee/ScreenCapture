package com.branch.www.screencapture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
//import android.os.Handler;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.AsyncTaskCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.branch.www.screencapture.BroadCastRunnable.ServiceToActivityRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.branch.www.screencapture.BroadCastRunnable.BroadCastUtil.ACTIVITY_TO_SERVICE_KEY;
import static com.branch.www.screencapture.BroadCastRunnable.BroadCastUtil.FILTER;
import static java.lang.Thread.sleep;

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

  @Override
  public void onCreate() {
    super.onCreate();

    createFloatView();

    createImageReader();
    manageServiceJobs();
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
        runAutoClick();
        System.out.println("OnTouchEven");
        return mGestureDetector.onTouchEvent(event);
      }
    });

  }

  private void runAutoClick(){
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true){
          autoCupture();
          System.out.println("Running Auto Click");
          try {
            sleep(20000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  public void autoCupture(){
    //模拟触屏点击屏幕事件
    int x = 0;
    int y = 0;
    long downTime = SystemClock.uptimeMillis();
    final MotionEvent downEvent = MotionEvent.obtain(
            downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
    downTime += 500;
    final MotionEvent upEvent = MotionEvent.obtain(
            downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
    //添加到webview_loading_round_iv上
    mFloatView.onTouchEvent(downEvent);
    mFloatView.onTouchEvent(upEvent);
    downEvent.recycle();
    upEvent.recycle();
  }

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
          analystImage(bitmap);

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

    public void analystImage (Bitmap bitmap){
      //系统相册目录
      String galleryPath= Environment.getExternalStorageDirectory()
              + File.separator + Environment.DIRECTORY_DCIM
              +File.separator+"Camera"+File.separator;

      saveImage(bitmap,galleryPath,"testImage");
    }

    //保存图片到本地路径
    public File saveImage(Bitmap bmp, String path, String fileName) {
      File appDir = new File(path);
      if (!appDir.exists()) {
        appDir.mkdir();
      }
      File file = new File(appDir, fileName);
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

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      super.onPostExecute(bitmap);
      //预览图片
      if (bitmap != null) {

        ((ScreenCaptureApplication) getApplication()).setmScreenCaptureBitmap(bitmap);
        Log.e("ryze", "获取图片成功");
        startActivity(PreviewPictureActivity.newIntent(getApplicationContext()));
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

  /**
   * 返回一个Binder对象
   */
//  @Override
//  public IBinder onBind(Intent intent) {
//    return new MsgBinder();
//  }

//  public class MsgBinder extends Binder {
//    /**
//     * 获取当前Service的实例
//     * @return
//     */
//    public FloatWindowsService getService(){
//      return FloatWindowsService.this;
//    }
//
//
//    public void setData(String data){
//      FloatWindowsService.this.data = data;
//    }
//  }


  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(" ", "onStartCommand");



    return super.onStartCommand(intent, flags, startId);
  }

  private void sentMessageToActivity(String message){
    // farklı thread'e çıkarak asenkron işlem başlatılır

    ServiceToActivityRunnable serviceToActivityRunnable = new ServiceToActivityRunnable(message ,getBaseContext());
    thread = new Thread(serviceToActivityRunnable);
    thread.start();
  }


  private void createBroadCastFromActivity(){
    // service'ten yayınlanacak olan broadcast'i dinliyoruz
    LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
          String tempMessage =  intent.getStringExtra(ACTIVITY_TO_SERVICE_KEY);
          if (tempMessage != null){
              System.out.println("intent.getStringExtra(ACTIVITY_TO_SERVICE_KEY) " + intent.getStringExtra(ACTIVITY_TO_SERVICE_KEY));
          }
      }
    }, new IntentFilter(FILTER));
  }

  int i = 0;
  private void manageServiceJobs(){
    createBroadCastFromActivity();
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true){
          try {
            sleep(2000);
            sentMessageToActivity(" message service to activity " + i);
            i++;
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }
}

