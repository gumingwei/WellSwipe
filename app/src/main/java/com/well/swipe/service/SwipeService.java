package com.well.swipe.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.well.swipe.view.BubbleView;
import com.well.swipe.view.CatchView;

import java.util.logging.Level;


/**
 * Created by mingwei on 3/6/16.
 */
public class SwipeService extends Service {

    private BubbleView mBubble;

    private CatchView mView;

    private CatchView mView2;

    public NotificationManager mNotificationManager;

    public Notification mNotification;


    public SwipeService() {
        Log.i("Gmw", "SwipeService()");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Gmw", "create()");
        mBubble = new BubbleView(getBaseContext());
        mView = new CatchView(getBaseContext(), 50, 300);
        mView.setState(CatchView.STATE_LEFT);
        mView.show();

        mView = new CatchView(getBaseContext(), 100, 50);
        mView.setState(CatchView.STATE_LEFT);
        mView.show();

        mView = new CatchView(getBaseContext(), 50, 300);
        mView.setState(CatchView.STATE_RIGHT);
        mView.show();

        mView = new CatchView(getBaseContext(), 100, 50);
        mView.setState(CatchView.STATE_RIGHT);
        mView.show();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mNotification = mBuilder.build();
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Gmw", "onBind()");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Gmw", "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Gmw", "onDestory()");
        Intent intent = new Intent();
        intent.setClass(this, SwipeService.class);
        startService(intent);
    }

    private native void swipeDaemon(String serviceName, int sdkVersion);

    static {
        System.loadLibrary("SwipeDaemon");
    }

}
