package com.branch.www.screencapture.BroadCastRunnable;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.branch.www.screencapture.MainActivity;

public class ServiceToActivityRunnable implements Runnable {

    private String message = "";

    private Context mContext;
    public ServiceToActivityRunnable(String message, Context context) {
        this.message = message;
        this.mContext = context;
    }


    @Override
    public void run() {
        try {
            String result = null;
            // uzun süreli işlem yapılır; dosya indirme, hesaplama vs.
            // gecikmeyi simule edelim
            Thread.sleep(3000);

            // temsili islem sonucu
            result = "tek gercek fenerbahce :]";

            // işlem bitti sonucu takipçi activity'e bildirelim
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(
                    new Intent(BroadCastUtil.FILTER).putExtra(BroadCastUtil.SERVICE_TO_ACTIVITY_KEY, message)
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
