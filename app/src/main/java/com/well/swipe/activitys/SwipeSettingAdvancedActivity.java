package com.well.swipe.activitys;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.SeekBar;

import com.well.swipe.ItemApplication;
import com.well.swipe.R;
import com.well.swipe.preference.PreferenceTitleSummary;
import com.well.swipe.preference.SwipeAreaDialog;
import com.well.swipe.preference.SwipeCheckItemDialog;
import com.well.swipe.preference.SwipeWhitelistDialog;
import com.well.swipe.service.SwipeService;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipecomm.utils.SettingHelper;
import com.well.swipe.view.CheckItemLayout;

import java.util.ArrayList;

/**
 * Created by mingwei on 6/22/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class SwipeSettingAdvancedActivity extends BaseSettingActivity implements View.OnClickListener {


    /**
     * 用来存滑出时机
     * 0仅桌面
     * 1桌面和其他App
     */
    private PreferenceTitleSummary mSwipeFor;

    private SwipeCheckItemDialog mDialogFor;

    /**
     * 用来存滑出时机
     * 0左侧底部和右侧底部
     * 1仅左侧底部
     * 2仅右侧底部
     */
    private PreferenceTitleSummary mSwipeArea;

    private SwipeAreaDialog mDialogArea;
    /**
     * Swipe划出的三种方式
     */
    private int mSwipAreaValue;
    /**
     * Swipe划出范围百分数
     */
    private int mSeekBarProgress;
    /**
     * 白名单
     */
    private PreferenceTitleSummary mSwipeWhitelist;

    private SwipeWhitelistDialog mDialogWhitelist;

    /**
     * Service实例
     */
    SwipeService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_advanced_activity);
        setTitle(R.string.swipe_advanced_setting);

        mSwipeFor = (PreferenceTitleSummary) findViewById(R.id.swipe_for);
        mSwipeFor.setKey(SwipeSetting.SWIPE_FOR);
        mSwipeFor.setTitle(getResources().getString(R.string.swipe_for));
        mSwipeFor.setSummaryArray(getResources().getStringArray(R.array.swipe_for_type));
        mSwipeFor.refreshSummary(1);
        mSwipeFor.setOnClickListener(this);

        mSwipeArea = (PreferenceTitleSummary) findViewById(R.id.swipe_area);
        mSwipeArea.setKey(SwipeSetting.SWIPE_AREA);
        mSwipeArea.setTitle(getResources().getString(R.string.swipe_active_area));
        mSwipeArea.setSummaryArray(getResources().getStringArray(R.array.swipe_area_type));
        mSwipeArea.refreshSummary(0);
        mSwipeArea.setOnClickListener(this);

        mSwipeWhitelist = (PreferenceTitleSummary) findViewById(R.id.swipe_whitelist);
        mSwipeWhitelist.setKey(SwipeSetting.SWIPE_WHITELIST);
        mSwipeWhitelist.setTitle(String.format(getResources().getString(R.string.swipe_whitelist), 0));
        mSwipeWhitelist.setSummary(getResources().getString(R.string.swipe_whitelist_des));
        mSwipeWhitelist.showArrow();
        mSwipeWhitelist.setOnClickListener(this);


        /**
         * 绑定Service
         */
        if (SettingHelper.getInstance(this).getBoolean(SwipeSetting.SWIPE_TOGGLE, true)) {
            bindSwipeService();
        }
        /**
         * 当开关关闭时，设置项目不可点击
         */
        toogleSwipe(SettingHelper.getInstance(this).getBoolean(SwipeSetting.SWIPE_TOGGLE, true));

        int whiteDotValues = SettingHelper.getInstance(this).getInt(SwipeSetting.SWIPE_OPEN_TYPE);
        if (whiteDotValues == 1) {
            mSwipeArea.setClickable(false);
        }


    }

    @Override
    public void onClick(View v) {
        if (v == mSwipeFor) {
            mDialogFor = new SwipeCheckItemDialog(this);
            mDialogFor.setTitle(getString(R.string.swipe_for)).
                    addItem(mSwipeFor.getSummaryArray()[0], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeFor.setValues(0);
                            mSwipeFor.refreshSummary();
                            mDialogFor.dissmis();
                        }
                    }, mSwipeFor.getIntValue(1) == 0).
                    addItem(mSwipeFor.getSummaryArray()[1], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeFor.setValues(1);
                            mSwipeFor.refreshSummary();
                            mDialogFor.dissmis();
                        }
                    }, mSwipeFor.getIntValue(1) == 1).
                    show();

        } else if (v == mSwipeArea) {
            mDialogArea = new SwipeAreaDialog(this);
            mSwipAreaValue = mSwipeArea.getIntValue();
            mSeekBarProgress = SettingHelper.getInstance(this).getInt(SwipeSetting.SWIPE_AREA_PROGRESS, 5);

            mDialogArea.setTitle(getString(R.string.swipe_active_area)).
                    addItem(mSwipeArea.getSummaryArray()[1], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckItemLayout view = (CheckItemLayout) v;
                            if (mSwipAreaValue == 0) {
                                if (view.isChecked()) {
                                    view.setChecked(false);
                                    mSwipAreaValue = 2;
                                    mService.changeCatchView(2);
                                }
                            } else if (mSwipAreaValue == 2) {
                                if (!view.isChecked()) {
                                    view.setChecked(true);
                                    mSwipAreaValue = 0;
                                    mService.changeCatchView(0);
                                }
                            }
                        }
                    }, mSwipeArea.getIntValue() == 0 || mSwipeArea.getIntValue() == 1).
                    addItem(mSwipeArea.getSummaryArray()[2], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckItemLayout view = (CheckItemLayout) v;
                            if (mSwipAreaValue == 0) {
                                if (view.isChecked()) {
                                    view.setChecked(false);
                                    mSwipAreaValue = 1;
                                    mService.changeCatchView(1);
                                }
                            } else if (mSwipAreaValue == 1) {
                                if (!view.isChecked()) {
                                    view.setChecked(true);
                                    mSwipAreaValue = 0;
                                    mService.changeCatchView(0);
                                }
                            }
                        }
                    }, mSwipeArea.getIntValue() == 0 || mSwipeArea.getIntValue() == 2).
                    setProgress(mSeekBarProgress).
                    setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            mSeekBarProgress = progress;
                            //mService.updataCatchView(progress / 100);
                            //Log.i("Gmw", "progress=" + (progress / 10f));
                            mService.updataCatchView(progress / 10f);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    }).
                    onPositive(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeArea.setValues(mSwipAreaValue);
                            mSwipeArea.refreshSummary();
                            SettingHelper.getInstance(getBaseContext()).putInt(SwipeSetting.SWIPE_AREA_PROGRESS, mSeekBarProgress);
                            mDialogArea.dissmis();
                        }
                    }).
                    onNegative(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogArea.dissmis();
                        }
                    }).
                    setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            mService.changColor(getResources().getColor(R.color.white));
                        }
                    }).
                    setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mService.changColor(Color.TRANSPARENT);
                            mService.changeCatchView(SettingHelper.getInstance(getBaseContext()).getInt(SwipeSetting.SWIPE_AREA));
                        }
                    }).show();

        } else if (v == mSwipeWhitelist) {
            mDialogWhitelist = new SwipeWhitelistDialog(this);
            mDialogWhitelist.setTitle(getResources().getString(R.string.swipe_whitelist_title)).
                    onPositive(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogWhitelist.dissmis();
                        }
                    }).
                    onNegative(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogWhitelist.dissmis();
                        }
                    }).
                    setWhiteList(mService.getLauncherMode().loadWhitelist(this)).
                    setGridData(mService.getLauncherMode().getAllAppsList().data).
                    onPositive(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogWhitelist.dissmis();
                            mSwipeWhitelist.setTitle(String.format(getResources().getString(R.string.swipe_whitelist),
                                    mDialogWhitelist.getWhitelist().size()));
                            deleteWhitelist(getBaseContext());
                            addWhitelist(getBaseContext(), mDialogWhitelist.getWhitelist());
                        }
                    }).
                    onNegative(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogWhitelist.dissmis();
                        }
                    }).show();

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mService != null) {
            mService.changColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindSwipeService();

    }

    public void bindSwipeService() {
        Intent intent = new Intent(this, SwipeService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindSwipeService() {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteWhitelist(Context context) {
        new ItemApplication().deleteWhitelist(context);
    }

    /**
     * 切换Swipe
     *
     * @param click
     */
    public void toogleSwipe(boolean click) {
        mSwipeFor.setClickable(click);
        mSwipeArea.setClickable(click);
        mSwipeWhitelist.setClickable(click);
    }

    /**
     * 添加白名单
     *
     * @param context
     * @param newlist
     */
    public void addWhitelist(Context context, ArrayList<ItemApplication> newlist) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = context.getPackageManager();
        for (int i = 0; i < newlist.size(); i++) {
            newlist.get(i).insertWhitelist(context, intent);
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((SwipeService.ServiceBind) service).getService();
            mSwipeWhitelist.setTitle(String.format(getResources().getString(R.string.swipe_whitelist),
                    mService.getLauncherMode().loadWhitelist(getBaseContext()).size()));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}
