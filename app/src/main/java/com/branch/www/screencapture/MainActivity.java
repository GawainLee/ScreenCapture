package com.branch.www.screencapture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.branch.www.screencapture.BroadCastRunnable.ActivityToServiceRunnable;

import static com.branch.www.screencapture.BroadCastRunnable.BroadCastUtil.FILTER;
import static com.branch.www.screencapture.BroadCastRunnable.BroadCastUtil.SERVICE_TO_ACTIVITY_KEY;
import static java.lang.Thread.sleep;

public class MainActivity extends FragmentActivity {


  public static final int REQUEST_MEDIA_PROJECTION = 18;

  private Thread thread;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    requestCapturePermission();

    manageActivityJobs();
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

//  ServiceConnection conn = new ServiceConnection() {
//    @Override
//    public void onServiceDisconnected(ComponentName name) {
//
//    }
//
//    private  FloatWindowsService floatWindowsService;
//    @Override
//    public void onServiceConnected(ComponentName name, IBinder service) {
//      //返回一个MsgService对象
//      floatWindowsService = ((FloatWindowsService.MsgBinder)service).getService();
//
//    }
//  };


  private void createServiceBroadCast(){
    // service'ten yayınlanacak olan broadcast'i dinliyoruz
    LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
//         Toast.makeText(MainActivity.this, intent.getStringExtra(SERVICE_TO_ACTIVITY_KEY), Toast.LENGTH_SHORT).show();
        String tempMessage = intent.getStringExtra(SERVICE_TO_ACTIVITY_KEY);
        if(tempMessage != null){
          System.out.println("intent.getStringExtra(SERVICE_TO_ACTIVITY_KEY) " + intent.getStringExtra(SERVICE_TO_ACTIVITY_KEY));
        }

      }
    }, new IntentFilter(FILTER));
  }

  private void sentMessageToService(String message){
    // farklı thread'e çıkarak asenkron işlem başlatılır
    ActivityToServiceRunnable activityToServiceRunnable = new ActivityToServiceRunnable(message,getBaseContext());
    thread = new Thread(activityToServiceRunnable);
    thread.start();
  }

  int i = 1;
  private void manageActivityJobs(){
    createServiceBroadCast();
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true){
          try {
            sleep(1000);
            sentMessageToService(" message from activity to service " + i);
            i++;
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }
}
