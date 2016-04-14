package com.well.swipe.activitys;


import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.well.swipe.ItemApplication;
import com.well.swipe.R;
import com.well.swipe.preference.PreferenceCategory;
import com.well.swipe.preference.PreferenceTitle;
import com.well.swipe.preference.PreferenceTitleSummary;
import com.well.swipe.preference.SwipeAreaDialog;
import com.well.swipe.preference.SwipeDialog;
import com.well.swipe.preference.SwipeForDialog;
import com.well.swipe.preference.SwipeWhitelistDialog;
import com.well.swipe.service.SwipeService;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipe.utils.SettingHelper;
import com.well.swipe.utils.Utils;
import com.well.swipe.view.CheckItemLayout;

import java.util.ArrayList;

public class SwipeSettingActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private TextView test;
    /**
     * 用来存SwipeService是否打开
     */
    private PreferenceCategory mSwipeToggle;
    /**
     * 用来存滑出时机
     * 0仅桌面
     * 1桌面和其他App
     */
    private PreferenceTitleSummary mSwipeFor;

    private SwipeForDialog mDialogFor;
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
     * 关于
     */
    private PreferenceCategory mAboutCategory;
    /**
     * 评分
     */
    private PreferenceTitle mRoter5Star;
    /**
     * 反馈
     */
    private PreferenceTitle mFeedback;
    /**
     * 版本
     */
    private PreferenceTitleSummary mVersion;
    /**
     * Service实例
     */
    SwipeService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = (TextView) findViewById(R.id.test_text);
        test.setText("density=" + this.getResources().getDisplayMetrics().density + ",swipe_dialog_for=" +
                getResources().getDimensionPixelSize(R.dimen.test));
        mSwipeToggle = (PreferenceCategory) findViewById(R.id.swipe_toggle);
        mSwipeToggle.setTitle(getResources().getString(R.string.swipe_toggle));
        mSwipeToggle.setKey(SwipeSetting.SWIPE_TOGGLE);
        mSwipeToggle.getSwitchBtn().setChecked(mSwipeToggle.getBooleanValue(true));
        mSwipeToggle.getSwitchBtn().setOnCheckedChangeListener(this);
        mSwipeToggle.setIcon(getResources().getDrawable(R.drawable.ic_setting_notify_toggle));

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
        mSwipeWhitelist.setOnClickListener(this);

        toogleSwipe(SettingHelper.getInstance(this).getBoolean(SwipeSetting.SWIPE_TOGGLE, true));

        mAboutCategory = (PreferenceCategory) findViewById(R.id.swipe_about);
        mAboutCategory.setTitle(getResources().getString(R.string.swipe_about));
        mAboutCategory.setToggleVisiable(View.GONE);
        mAboutCategory.setIcon(getResources().getDrawable(R.drawable.ic_setting_rate));

        mRoter5Star = (PreferenceTitle) findViewById(R.id.swipe_rate);
        mRoter5Star.setTitle(getResources().getString(R.string.swipe_about_rate5start));
        mRoter5Star.setOnClickListener(this);

        mFeedback = (PreferenceTitle) findViewById(R.id.swipe_feedback);
        mFeedback.setTitle(getResources().getString(R.string.swipe_about_feedback));
        mFeedback.setOnClickListener(this);

        mVersion = (PreferenceTitleSummary) findViewById(R.id.swipe_version);
        mVersion.setTitle(getResources().getString(R.string.swipe_about_version));
        mVersion.setSummary(Utils.getVersionName(this));
        /**
         * 绑定Service
         */
        if (SettingHelper.getInstance(this).getBoolean(SwipeSetting.SWIPE_TOGGLE, true)) {
            bindSwipeService();
        }
        //mService.privatef();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //startService(new Intent(SwipeSettingActivity.this, SwipeService.class));
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v == mSwipeFor) {
            mDialogFor = new SwipeForDialog(this);
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
                    }, mSwipeFor.getIntValue(1) == 1).show();

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

        } else if (v == mRoter5Star) {
            openAppStore(this);
        } else if (v == mFeedback) {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:wellswipe.dev@foxmail.com"));
            data.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_title));
            data.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.feedback_content));
            startActivity(data);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mSwipeToggle.getSwitchBtn()) {
            mSwipeToggle.setValues(isChecked);
            if (isChecked) {
                startService(new Intent(SwipeSettingActivity.this, SwipeService.class));
                bindSwipeService();
                toogleSwipe(true);
            } else {
                toogleSwipe(false);
                stopService(new Intent(SwipeSettingActivity.this, SwipeService.class));
            }
        }
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

    public void deleteWhitelist(Context context) {
        new ItemApplication().deleteWhitelist(context);
    }

    public static void openAppStore(Context context) {
        if (Utils.isApkInstalled(context, "com.android.vending")) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.getMarketUrl(context
                        .getPackageName())));
                browserIntent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Uri u = Uri.parse(Utils.getMarketUrl(context.getPackageName()));
                Intent market = new Intent(Intent.ACTION_VIEW, u);
                market.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(market);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, context.getString(R.string.googleplay_not_found), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
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
