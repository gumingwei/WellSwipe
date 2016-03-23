package com.well.swipe.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.well.swipe.ItemApplication;
import com.well.swipe.LauncherModel;
import com.well.swipe.R;
import com.well.swipe.SwipeApplication;
import com.well.swipe.ItemSwipeSwitch;
import com.well.swipe.view.SwipeEditLayout;
import com.well.swipe.view.AngleView;
import com.well.swipe.view.BubbleView;
import com.well.swipe.view.CatchView;
import com.well.swipe.view.SwipeLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * Created by mingwei on 3/6/16.
 */
public class SwipeService extends Service implements CatchView.OnEdgeSlidingListener, LauncherModel.Callback,
        AngleView.OnClickListener {

    SwipeApplication mSwipeApplication;
    /**
     * LauncherModel广播接收机负责加载和更新数据
     */
    LauncherModel mLauncherModel;

    private BubbleView mBubble;
    /**
     * 屏幕底部负责捕获手势的试图
     */
    private CatchView mView;
    /**
     * Swipe的跟布局
     */
    private SwipeLayout mSwipeLayout;

    //private SwipeEditLayout mEditLayout;

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
        mBubble = new BubbleView(getBaseContext());
        mSwipeApplication = (SwipeApplication) getApplication();
        mLauncherModel = mSwipeApplication.setLaunchr(this);

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
        /**
         * AngleView的单击监听
         */
        mSwipeLayout.getAngleLayout().getAngleView().setOnClickListener(this);


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
        mLauncherModel.startLoadTask();

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
        mSwipeLayout.switchLeft();
    }

    @Override
    public void openRight() {
        mSwipeLayout.switchRight();

    }

    @Override
    public void change(float scale) {
        mSwipeLayout.setScale(scale);
        mSwipeLayout.setSwipeBackgroundViewAlpha(scale);
    }

    @Override
    public void cancel(View view, boolean flag) {
        int state = ((CatchView) view).getState();
        if (state == CatchView.POSITION_STATE_LEFT) {
            mSwipeLayout.switchLeft();
        } else if (state == CatchView.POSITION_STATE_RIGHT) {
            mSwipeLayout.switchRight();
        }
        /**
         * flag==true  速度满足时自动打开
         * flag==flase 根据当前的sacle判断是否打开
         */
        if (flag) {
            mSwipeLayout.getAngleLayout().on();
        } else {
            mSwipeLayout.getAngleLayout().switchAngleLayout();
        }

    }

    @Override
    public void bindStart() {
    }

    @Override
    public void bindAllApps(ArrayList<ItemApplication> appslist) {
        //mSwipeLayout.getSwipeEditLayout().setData(appslist);
    }

    @Override
    public void bindFavorites(ArrayList<ItemApplication> appslist) {
        mSwipeLayout.getAngleLayout().getAngleView().putItemApplications(appslist);
        //mSwipeLayout.getSwipeEditLayout().setHeaderData(appslist);
    }

    @Override
    public void bindSwitch(ArrayList<ItemSwipeSwitch> switchlist) {
        mSwipeLayout.getAngleLayout().getAngleView().putItemQuickSwitch(switchlist);
    }

    @Override
    public void bindFinish() {
        mSwipeLayout.getAngleLayout().getAngleView().refresh();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onDeleteClick(View view) {
        Object tag = view.getTag();
        if (tag instanceof ItemApplication) {
            int index = ((ItemApplication) tag).deleted(getBaseContext());
            if (index > 0) {
                mSwipeLayout.getAngleLayout().getAngleView().removeItem();
            }
        } else if (tag instanceof ItemSwipeSwitch) {

        }
    }

    @Override
    public void onAddClick(int index) {
        switch (index) {
            case 2:
                //mBubble.show();
                mSwipeLayout.addBubble();
                //mLauncherModel.loafFavorite();
                //mSwipeLayout.showA();
                //mEditLayout.show();
                break;
            case 1:
                break;
        }

    }

    private native void swipeDaemon(String serviceName, int sdkVersion);

    static {
        System.loadLibrary("SwipeDaemon");
    }

}
