package com.branch.www.screencapture;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import static android.app.Activity.RESULT_OK;

public class MainActivity extends FragmentActivity {


  public static final int REQUEST_MEDIA_PROJECTION = 18;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    requestCapturePermission();
  }


  public void requestCapturePermission() {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      //5.0 之后才允许使用屏幕截图

      return;
    }

    MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
        getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    startActivityForResult(
        mediaProjectionManager.createScreenCaptureIntent(),
        REQUEST_MEDIA_PROJECTION);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case REQUEST_MEDIA_PROJECTION:

        if (resultCode == RESULT_OK && data != null) {
          FloatWindowsService.setResultData(data);
          Intent intentService = new Intent(getApplicationContext(), FloatWindowsService.class);
          startService(intentService);
        }
        break;
    }

  }

  ServiceConnection conn = new ServiceConnection() {
    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    private  FloatWindowsService floatWindowsService;
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      //返回一个MsgService对象
      floatWindowsService = ((FloatWindowsService.MsgBinder)service).getService();

    }
  };

}
