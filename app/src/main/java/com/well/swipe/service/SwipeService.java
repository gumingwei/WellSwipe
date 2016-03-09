package com.well.swipe.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;

import com.well.swipe.R;
import com.well.swipe.view.BubbleView;
import com.well.swipe.view.CatchView;
import com.well.swipe.view.SwipeLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by mingwei on 3/6/16.
 */
public class SwipeService extends Service implements CatchView.OnEdgeSlidingListener {

    private BubbleView mBubble;

    private CatchView mView;

    private SwipeLayout mSwipeLayout;

    public NotificationManager mNotificationManager;

    public Notification mNotification;
    /**
     * Service前台
     */
    private boolean mReflectFlg = true;
    private static final Class<?>[] mSetForegroundSignature = new Class[]{boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[]{int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[]{boolean.class};
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];


    public SwipeService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mBubble = new BubbleView(getBaseContext());
        mView = new CatchView(getBaseContext(), 0, 0, 50, 300);
        mView.setState(CatchView.POSITION_STATE_LEFT);
        mView.setOnEdgeSlidingListener(this);
        mView.show();

        mView = new CatchView(getBaseContext(), 0, 0, 100, 50);
        mView.setState(CatchView.POSITION_STATE_LEFT);
        mView.setOnEdgeSlidingListener(this);
        mView.show();

        mView = new CatchView(getBaseContext(), 0, 0, 50, 300);
        mView.setState(CatchView.POSITION_STATE_RIGHT);
        mView.setOnEdgeSlidingListener(this);
        mView.show();

        mView = new CatchView(getBaseContext(), 0, 0, 100, 50);
        mView.setState(CatchView.POSITION_STATE_RIGHT);
        mView.setOnEdgeSlidingListener(this);
        mView.show();

        mSwipeLayout = (SwipeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.swipe_layout, null);
        mSwipeLayout.setR();
        mSwipeLayout.setL();
        mSwipeLayout.setR();


        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mNotification = mBuilder.build();
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        initForeground();
        startForegroundCompat(123, new Notification());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setClass(this, SwipeService.class);
        startService(intent);
    }

    public void initForeground() {
        try {
            mStartForeground = SwipeService.class.getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = SwipeService.class.getMethod("stopForeground", mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            mStartForeground = mStopForeground = null;
        }

        try {
            mSetForeground = getClass().getMethod("setForeground",
                    mSetForegroundSignature);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("");
        }
    }

    public void startForegroundCompat(int id, Notification notification) {
        if (mReflectFlg) {
            if (mStartForeground != null) {
                mStartForegroundArgs[0] = Integer.valueOf(id);
                mStartForegroundArgs[1] = notification;
                invokeMethod(mStartForeground, mStartForegroundArgs);
                return;
            }
            mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
            mNotificationManager.notify(id, notification);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startForeground(id, notification);
            } else {
                mNotificationManager.notify(0, notification);
            }
        }
    }

    public void stopForegroundCompat(int id) {
        if (mReflectFlg) {
            if (mStopForeground != null) {
                mStopForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mStopForeground, mStopForegroundArgs);
                return;
            }
            mNotificationManager.cancel(id);
            mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                stopForeground(true);
            } else {
                mNotificationManager.cancel(0);
            }
        }
    }

    void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openLeft() {
        mSwipeLayout.setL();
        mSwipeLayout.show();
    }

    @Override
    public void openRight() {
        mSwipeLayout.setR();
        mSwipeLayout.show();
    }

    @Override
    public void change(float precent) {

    }

    private native void swipeDaemon(String serviceName, int sdkVersion);

    static {
        System.loadLibrary("SwipeDaemon");
    }

}
