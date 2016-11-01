package com.droi.sdk.pushdemo;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.droi.sdk.push.DroiMessageHandler;
import com.droi.sdk.push.DroiPush;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initWork();
    }

    private void initWork() {
        DroiPush.initialize(getApplicationContext());
        DroiPush.setMessageHandler(new DroiMessageHandler() {

            @Override
            public void onHandleCustomMessage(final Context context, final String message) {
                // TODO Auto-generated method stub
                new Handler(getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Demo Custom Msg:" + message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
