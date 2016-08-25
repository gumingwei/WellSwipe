package com.well.swipe.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.well.swipe.BootReceiver;
import com.well.swipe.ItemApplication;
import com.well.swipe.LauncherModel;
import com.well.swipe.R;
import com.well.swipe.SwipefreeApplication;
import com.well.swipe.ItemSwipeTools;
import com.well.swipe.activitys.SwipeSettingActivity;
import com.well.swipe.tools.SwipeBluetooth;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipe.tools.ToolsStrategy;
import com.well.swipe.tools.WifiAndData;
import com.well.swipecomm.utils.SettingHelper;
import com.well.swipecomm.utils.Utils;
import com.well.swipe.view.AngleItemStartUp;
import com.well.swipe.view.AngleLayout;
import com.well.swipe.view.AngleView;
import com.well.swipecomm.view.BubbleView;
import com.well.swipecomm.view.CatchView;
import com.well.swipecomm.view.OnDialogListener;
import com.well.swipe.view.SwipeLayout;
import com.well.swipecomm.view.PositionState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by mingwei on 3/6/16.
 * <p/>
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 */
public class SwipeService extends Service implements CatchView.OnEdgeSlidingListener, LauncherModel.Callback,
        AngleView.OnClickListener, OnDialogListener, AngleLayout.OnItemDragListener, BubbleView.OnOpenClickListener {

    SwipefreeApplication mSwipeApplication;
    /**
     * LauncherModel广播接收机负责加载和更新数据
     */
    LauncherModel mLauncherModel;

    private BubbleView mBubbleView;

    /**
     * 屏幕底部负责捕获手势的试图
     */
    private CatchView mCatchViewLeft0;

    private CatchView mCatchViewLeft1;

    private CatchView mCatchViewLeft2;

    private CatchView mCatchViewRight0;

    private CatchView mCatchViewRight1;

    private CatchView mCatchViewRight2;

    private int mCatchViewHeight;

    private int mCatchViewWidth;

    private int mCatchViewBroadSize;

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
    private long lastClickTime = 0;

    private ChangedReceiver mReceiver;

    private MobileContentObserver mObserver;

    private Handler mHandler = new Handler();

    private Tracker mTracker;

    private HomeWatchReceiver mWatchRecevier = new HomeWatchReceiver();

    IBinder mBinder = new ServiceBind();

    public class ServiceBind extends Binder {

        public SwipeService getService() {
            return SwipeService.this;
        }
    }

    public SwipeService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Gmw", "SwipeService_onCreate");
        //Google
        SwipefreeApplication application = (SwipefreeApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mCatchViewWidth = getResources().getDimensionPixelSize(R.dimen.catch_view_width);
        mCatchViewHeight = getResources().getDimensionPixelSize(R.dimen.catch_view_height);
        mCatchViewBroadSize = getResources().getDimensionPixelSize(R.dimen.catch_view_broad_size_base);
        mSwipeApplication = (SwipefreeApplication) getApplication();
        mLauncherModel = mSwipeApplication.setLaunchr(this);
        float pre = (float) SettingHelper.getInstance(this).getInt(SwipeSetting.SWIPE_AREA_PROGRESS, 5) / 10;

        mCatchViewLeft0 = new CatchView(getBaseContext());
        mCatchViewLeft0.setOnEdgeSlidingListener(this);

        mCatchViewLeft1 = new CatchView(getBaseContext());
        mCatchViewLeft1.setOnEdgeSlidingListener(this);

        mCatchViewLeft2 = new CatchView(getBaseContext());
        mCatchViewLeft2.setOnEdgeSlidingListener(this);

        mCatchViewRight0 = new CatchView(getBaseContext());
        mCatchViewRight0.setOnEdgeSlidingListener(this);

        mCatchViewRight1 = new CatchView(getBaseContext());
        mCatchViewRight1.setOnEdgeSlidingListener(this);

        mCatchViewRight2 = new CatchView(getBaseContext());
        mCatchViewRight2.setOnEdgeSlidingListener(this);

        mBubbleView = new BubbleView(this);
        mBubbleView.setOnOpenClickListener(this);

        updataCatchView(pre);

        initCacthView(SettingHelper.getInstance(this).getInt(SwipeSetting.SWIPE_AREA));

        mSwipeLayout = (SwipeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.swipe_layout, null);
        /**
         * AngleView的单击监听
         */
        mSwipeLayout.getAngleLayout().getAngleView().setOnAngleClickListener(this);
        /**
         * 设置FavoriteAppEditLayout关闭时的监听
         */
        mSwipeLayout.getEditFavoriteLayout().setOnDialogListener(this);
        mSwipeLayout.getEditToolsLayout().setOnDialogListener(this);
        /**
         * AngleLayout的Item拖拽事件
         */
        mSwipeLayout.getAngleLayout().setOnDragItemListener(this);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //initForeground();
        initNotification();
        startForeground(123, mNotification);
        //startForegroundCompat(123, new Notification());

        mReceiver = new ChangedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);


        mObserver = new MobileContentObserver(this, mHandler);
        getContentResolver().registerContentObserver(Settings.Secure.
                getUriFor("mobile_data"), false, mObserver);
        getContentResolver().registerContentObserver(Settings.System
                .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, mObserver);
        getContentResolver().registerContentObserver(Settings.System
                .getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, mObserver);
        getContentResolver().registerContentObserver(Settings.System
                .getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, mObserver);

        /**
         * home监听
         */
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mWatchRecevier, homeFilter);

        /**
         * 小白点
         */
        initWhiteDot();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLauncherModel.startLoadTask();
        mTracker.setScreenName("SwipeService::onStartCommand()");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCatchViewLeft0.dismiss();
        mCatchViewLeft1.dismiss();
        mCatchViewLeft2.dismiss();
        mCatchViewRight0.dismiss();
        mCatchViewRight1.dismiss();
        mCatchViewRight2.dismiss();
        unregisterReceiver(mReceiver);
        getContentResolver().unregisterContentObserver(mObserver);
        unregisterReceiver(mWatchRecevier);

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

    /**
     * 设置CatchView
     * 0是两边都有
     * 1是左边
     * 2是右边
     *
     * @param area
     */
    public void initCacthView(int area) {
        if (area == 0) {
            mCatchViewLeft0.show();
            mCatchViewLeft1.show();
            mCatchViewLeft2.show();
            mCatchViewRight0.show();
            mCatchViewRight1.show();
            mCatchViewRight2.show();
        } else if (area == 1) {
            mCatchViewLeft0.show();
            mCatchViewLeft1.show();
            mCatchViewLeft2.show();
        } else if (area == 2) {
            mCatchViewRight0.show();
            mCatchViewRight1.show();
            mCatchViewRight2.show();
        }
    }


    public void changeCatchView(int area) {
        if (area == 0) {
            showCatchView();
        } else if (area == 1) {
            mCatchViewLeft0.show();
            mCatchViewLeft1.show();
            mCatchViewLeft2.show();
            mCatchViewRight0.dismiss();
            mCatchViewRight1.dismiss();
            mCatchViewRight2.dismiss();
        } else if (area == 2) {
            mCatchViewLeft0.dismiss();
            mCatchViewLeft1.dismiss();
            mCatchViewLeft2.dismiss();
            mCatchViewRight0.show();
            mCatchViewRight1.show();
            mCatchViewRight2.show();
        }
    }

    public void showCatchView() {
        mCatchViewLeft0.show();
        mCatchViewLeft1.show();
        mCatchViewLeft2.show();
        mCatchViewRight0.show();
        mCatchViewRight1.show();
        mCatchViewRight2.show();
    }

    public void hideCatchView() {
        mCatchViewLeft0.dismiss();
        mCatchViewLeft1.dismiss();
        mCatchViewLeft2.dismiss();
        mCatchViewRight0.dismiss();
        mCatchViewRight1.dismiss();
        mCatchViewRight2.dismiss();
    }

    /**
     * 更新
     *
     * @param pre
     */
    public void updataCatchView(float pre) {
        mCatchViewLeft0.setState(PositionState.POSITION_STATE_LEFT, 0, 0, (int) (mCatchViewBroadSize + mCatchViewBroadSize * pre),
                (int) (mCatchViewHeight + (mCatchViewHeight * pre)));
        mCatchViewLeft0.updata();
        mCatchViewLeft1.setState(PositionState.POSITION_STATE_LEFT, 0, 0, (int) (mCatchViewWidth + (mCatchViewWidth * pre)),
                (int) (mCatchViewBroadSize + mCatchViewBroadSize * pre));
        mCatchViewLeft1.updata();
        mCatchViewLeft2.setState(PositionState.POSITION_STATE_LEFT, 0, 0, (int) (mCatchViewBroadSize + mCatchViewBroadSize * pre) * 2,
                (int) (mCatchViewBroadSize + mCatchViewBroadSize * pre) * 4);
        mCatchViewLeft2.updata();
        mCatchViewRight0.setState(PositionState.POSITION_STATE_RIGHT, 0, 0, (int) (mCatchViewBroadSize + mCatchViewBroadSize * pre),
                (int) (mCatchViewHeight + (mCatchViewHeight * pre)));
        mCatchViewRight0.updata();
        mCatchViewRight1.setState(PositionState.POSITION_STATE_RIGHT, 0, 0, (int) (mCatchViewWidth + (mCatchViewWidth * pre)),
                (int) (mCatchViewBroadSize + mCatchViewBroadSize * pre));
        mCatchViewRight1.updata();
        mCatchViewRight2.setState(PositionState.POSITION_STATE_RIGHT, 0, 0, (int) (mCatchViewBroadSize + mCatchViewBroadSize * pre) * 2,
                (int) (mCatchViewBroadSize + mCatchViewBroadSize * pre) * 4);
        mCatchViewRight2.updata();
    }

    /**
     * 改变
     *
     * @param color
     */
    public void changColor(int color) {
        mCatchViewLeft0.setColor(color);
        mCatchViewLeft1.setColor(color);
        mCatchViewLeft2.setColor(color);
        mCatchViewRight0.setColor(color);
        mCatchViewRight1.setColor(color);
        mCatchViewRight2.setColor(color);
    }

    @Override
    public void openLeft() {
        // [START custom_event]
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("SwipeService::openLeft()")
                .build());
        // [END custom_event]
        /**
         * 0 仅桌面的时候打开
         */
        if (swipeSwipeSetting()) {
            mSwipeLayout.switchLeft();
        }

    }


    @Override
    public void openRight() {
        // [START custom_event]
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("SwipeService::openRight()")
                .build());
        // [END custom_event]
        if (swipeSwipeSetting()) {
            mSwipeLayout.switchRight();
        }
    }

    @Override
    public void change(float scale) {
        if (mSwipeLayout.hasView()) {
            if (mSwipeLayout.isSwipeOff()) {
                mSwipeLayout.getAngleLayout().setAngleLayoutScale(scale);
                mSwipeLayout.setSwipeBackgroundViewAlpha(scale);
            }
        }
    }

    @Override
    public void cancel(View view, boolean flag) {
        if (swipeSwipeSetting()) {
            if (mSwipeLayout.isSwipeOff()) {
                int state = ((CatchView) view).getState();
                if (state == PositionState.POSITION_STATE_LEFT) {
                    mSwipeLayout.switchLeft();
                } else if (state == PositionState.POSITION_STATE_RIGHT) {
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
                /**
                 * 设置RecentTask数据，如果新开机之后没有数据，就拿AllApps的数据做一个补充
                 */
                putRecentApps();
            }
        }
    }

    public void putRecentApps() {
        mSwipeLayout.getAngleLayout().getAngleView().putRecentTask(mLauncherModel.loadRecentTask(this),
                mLauncherModel.getAllAppsList().data);
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
    public void bindSwitch(ArrayList<ItemSwipeTools> switchlist) {
        mSwipeLayout.getAngleLayout().getAngleView().putItemQuickSwitch(switchlist);
    }

    @Override
    public void bindFinish() {
        mSwipeLayout.getAngleLayout().getAngleView().refresh();
    }

    @Override
    public void onAngleClick(View view) {
        Object object = view.getTag();
        AngleItemStartUp itemview = (AngleItemStartUp) view;
        if (object instanceof ActivityManager.RecentTaskInfo) {
            AngleItemStartUp.RecentTag recent = itemview.mRecentTag;
            Intent intent;
            ComponentName component = recent.intent.getComponent();
            String packageName = component.getPackageName();
            PackageManager packageManager = this.getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(packageName);
            if (null != intent) {
                startActivity(intent);
                mSwipeLayout.dismissAnimator();
            }
        } else if (object instanceof ItemApplication) {
            ItemApplication itemapp = (ItemApplication) view.getTag();
            startActivity(itemapp.mIntent);
            mSwipeLayout.dismissAnimator();
        } else if (object instanceof ItemSwipeTools) {
            if (safeClick()) {
                ItemSwipeTools itemswitch = (ItemSwipeTools) view.getTag();
                ToolsStrategy.getInstance().toolsClick(this, itemview, itemswitch, mSwipeLayout);
                /**
                 * 缺一不可，否则影响刷新界面
                 */
                mSwipeLayout.getAngleLayout().getAngleView().refreshToolsView();
                mSwipeLayout.getAngleLayout().getAngleView().requestLayout();
            }
        }
    }

    @Override
    public void onOpenClick() {
        if (swipeSwipeSetting()) {
            if (mBubbleView.isLeft()) {
                mSwipeLayout.switchLeft();
            } else {
                mSwipeLayout.switchRight();
            }
            mSwipeLayout.on();
            putRecentApps();

            // [START custom_event]
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("SwipeService::onClick()::WhiteDot")
                    .build());
            // [END custom_event]
        } else {
            Utils.swipeToast(getBaseContext(), getResources().getString(R.string.swipe_whitelist_item_unlauncher));
        }

    }

    //    @Override
//    public void onClick(View v) {
//
//    }

    public boolean safeClick() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > 600) {
            lastClickTime = currentTime;
            return true;
        }
        return false;
    }

    @Override
    public void onDeleteClick(View view) {
        Object tag = view.getTag();
        if (tag instanceof ItemApplication) {
            /**
             * 删除操作，删除成功之后会返回1，失败后返回-1
             */
            int index = ((ItemApplication) tag).delete(this);
            if (index > 0) {
                /**
                 *删除成功后更新界面
                 */
                mSwipeLayout.getAngleLayout().getAngleView().removeItem();
            }
        } else if (tag instanceof ItemSwipeTools) {
            int index = ((ItemSwipeTools) tag).delete(this);
            if (index > 0) {
                mSwipeLayout.getAngleLayout().getAngleView().removeItem();
            }
        }
    }

    @Override
    public void onAddClick(int index) {
        switch (index) {
            case 2:
                /**
                 * 点击AngleView的加号，打开编辑Favorite的窗口
                 */
                mSwipeLayout.setEditFavoritetVisiable();
                mSwipeLayout.getEditFavoriteLayout().setData(mLauncherModel.getAllAppsList().data);
                mSwipeLayout.getEditFavoriteLayout().setHeaderData(mLauncherModel.loadFavorite(this));
                //mLauncherModel.loafFavorite();这句话可以导致WindowsManager窗口失去焦点更新卡住，不知道为什么
                break;
            case 1:
                /**
                 * 点击AngleView的加号，打开编辑Tools的窗口
                 */
                mSwipeLayout.setEditToolsVisiable();
                mSwipeLayout.getEditToolsLayout().setGridData(mLauncherModel.getAllToolsList().mSwipeDataList);
                mSwipeLayout.getEditToolsLayout().setSelectedData(mLauncherModel.loadTools(this));
                break;
        }
    }


    @Override
    public void onPositive(View view) {
        if (view == mSwipeLayout.getEditFavoriteLayout()) {
            mSwipeLayout.setEditFavoriteGone();
            boolean refresh = mSwipeLayout.getEditFavoriteLayout().compare();
            /**
             * refresh是在compare中对比并且更新数据之后返回的，ture表示数据已经跟新过了，回来之后需要刷新
             * false表示不需要更新
             */
            if (refresh) {
                /**
                 * 重新读取数据
                 */
                mSwipeLayout.getAngleLayout().getAngleView().putItemApplications(mLauncherModel.loadFavorite(this));
                mSwipeLayout.getAngleLayout().getAngleView().refresh();
                /**
                 * 刷新过数据之后将编辑状态设置为STATE_NORMAL 即退出编辑状态
                 */
                mSwipeLayout.getAngleLayout().setEditState(AngleLayout.STATE_NORMAL);
            }
        } else if (view == mSwipeLayout.getEditToolsLayout()) {
            mSwipeLayout.setEditToolsGone();
            boolean refresh = mSwipeLayout.getEditToolsLayout().compare();
            if (refresh) {
                mSwipeLayout.getAngleLayout().getAngleView().putItemQuickSwitch(mLauncherModel.loadTools(this));
                mSwipeLayout.getAngleLayout().getAngleView().refresh();
                /**
                 * 刷新过数据之后将编辑状态设置为STATE_NORMAL 即退出编辑状态
                 */
                mSwipeLayout.getAngleLayout().setEditState(AngleLayout.STATE_NORMAL);
            }
        }
    }

    @Override
    public void onNegative(View view) {
        if (view == mSwipeLayout.getEditFavoriteLayout()) {
            mSwipeLayout.setEditFavoriteGone();
        } else if (view == mSwipeLayout.getEditToolsLayout()) {
            mSwipeLayout.setEditToolsGone();
        }
    }

    @Override
    public void onDragEnd(int index) {
        if (index == 1) {
            ArrayList<ItemSwipeTools> tools = mSwipeLayout.getAngleLayout().getAngleView().getToolsArrayList();
            mSwipeLayout.getEditToolsLayout().compare(this, mLauncherModel.loadTools(this), tools);
        } else if (index == 2) {
            ArrayList<ItemApplication> apps = mSwipeLayout.getAngleLayout().getAngleView().getItemApplications();
            mSwipeLayout.getEditFavoriteLayout().compare(this, mLauncherModel.loadFavorite(this), apps);
        }
    }

    /**
     * 判断当前的运行的app是否在桌面AppList中
     *
     * @return
     */
    private boolean isHomePackage() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = mActivityManager.getRunningTasks(1);
        //ItemApplication app = new ItemApplication();
        ActivityManager.RunningTaskInfo info = infos.get(0);
        String packagename = info.topActivity.getPackageName();
        String classname = info.topActivity.getClassName();
        for (int i = 0; i < mLauncherModel.getAllAppsList().homeapps.size(); i++) {
            ItemApplication application = mLauncherModel.getAllAppsList().homeapps.get(i);
            if (packagename.equals(application.mIntent.getComponent().getPackageName()) &&
                    classname.equals(application.mIntent.getComponent().getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否SwipeSetting 的值0为仅在桌面打开，1为桌面和所有app
     *
     * @return
     */
    public boolean swipeSwipeSetting() {
        /**
         * 判断是仅桌面还是所有app
         */
        if (SettingHelper.getInstance(this).getInt(SwipeSetting.SWIPE_FOR, 1) == 0) {
            /**
             * 当前的app是不是桌面app
             */
            if (isHomePackage()) {
                /**
                 * 过滤白名单
                 */
                if (!isWhitelistPackage()) {
                    return true;
                }

            }
        } else {
            if (!isWhitelistPackage()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是白名单
     *
     * @return
     */
    private boolean isWhitelistPackage() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = mActivityManager.getRunningTasks(1);
        //ItemApplication app = new ItemApplication();
        ActivityManager.RunningTaskInfo info = infos.get(0);
        String packagename = info.topActivity.getPackageName();
        String classname = info.topActivity.getClassName();
        for (int i = 0; i < mLauncherModel.loadWhitelist(this).size(); i++) {
            ItemApplication application = mLauncherModel.loadWhitelist(this).get(i);
            if (packagename.equals(application.mIntent.getComponent().getPackageName()) &&
                    classname.equals(application.mIntent.getComponent().getClassName())) {
                return true;
            }
        }
        return false;
    }

    public LauncherModel getLauncherMode() {
        return mLauncherModel;
    }

    public void initNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SwipeSettingActivity.class), 0);
        mNotification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_48)
                .setContentTitle(getResources().getString(R.string.swipe_nitification_title))// 设置在下拉status
                .setContentText(getResources().getString(R.string.swipe_nitification_content))// TextView中显示的详细内容
                .setContentIntent(pendingIntent) // 关联PendingIntent
                .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                .getNotification(); // 需要注意build()是在API level
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            mNotification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.logo_alpha)
                    .setContentTitle(getResources().getString(R.string.swipe_nitification_title))// 设置在下拉status
                    .setContentText(getResources().getString(R.string.swipe_nitification_content))// TextView中显示的详细内容
                    .setContentIntent(pendingIntent) // 关联PendingIntent
                    .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                    .getNotification(); // 需要注意build()是在API level
        }
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
    }


    /**
     * 小白点
     */
    public void initWhiteDot() {
        int values = SettingHelper.getInstance(this).getInt(SwipeSetting.SWIPE_OPEN_TYPE);
        if (values == 1) {
            mBubbleView.show();
        } else if (values == 0) {
            mBubbleView.dismiss();
        }
        isCatchViewOrWhiteDot();
    }

    public void isCatchViewOrWhiteDot() {
        int values = SettingHelper.getInstance(this).getInt(SwipeSetting.SWIPE_OPEN_TYPE);
        /**
         * 如果是小白点就不需要底部的划动区域
         */
        if (values == 1) {
            hideCatchView();
        } else if (values == 0) {
            showCatchView();
        }
    }

    /**
     * 监听Wifi变化
     */
    class ChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    mSwipeLayout.getAngleLayout().getAngleView().refreshToolsView();
                    mSwipeLayout.getAngleLayout().getAngleView().requestLayout();
                    if (WifiAndData.isWifiEnable(context)) {
                        //Utils.swipeToast(context, getResources().getString(R.string.wifi_on));
                    } else {
                        //Utils.swipeToast(context, getResources().getString(R.string.wifi_off));
                    }
                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    mSwipeLayout.getAngleLayout().getAngleView().refreshToolsView();
                    mSwipeLayout.getAngleLayout().getAngleView().requestLayout();
                } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    mSwipeLayout.getAngleLayout().getAngleView().refreshToolsView();
                    mSwipeLayout.getAngleLayout().getAngleView().requestLayout();
                    if (SwipeBluetooth.getInstance().getState()) {
                        //Utils.swipeToast(context, getResources().getString(R.string.bluetooth_on));
                    } else {
                        //Utils.swipeToast(context, getResources().getString(R.string.bluetooth_off));
                    }
                }
            }
        }
    }

    /**
     * 监听gprs data变化
     */
    class MobileContentObserver extends ContentObserver {

        private Context mContext;

        public MobileContentObserver(Context context, Handler handler) {
            super(handler);
            mContext = context;
        }

        public void onChange(boolean paramBoolean) {
            super.onChange(paramBoolean);
            mSwipeLayout.getAngleLayout().getAngleView().refreshToolsView();
            mSwipeLayout.getAngleLayout().getAngleView().requestLayout();
        }
    }

    class HomeWatchReceiver extends BootReceiver {

        private static final String TAG = "HomeWatchReceiver";

        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";

        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";

        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    // 短按Home键
                    mSwipeLayout.getAngleLayout().off();
                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // 长按Home键 或者 activity切换键
                    mSwipeLayout.getAngleLayout().off();
                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                    // 锁屏
                    mSwipeLayout.getAngleLayout().off();
                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    // samsung 长按Home键
                    mSwipeLayout.getAngleLayout().off();
                }
            }
        }
    }

}
