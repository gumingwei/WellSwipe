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
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.well.swipe.ItemApplication;
import com.well.swipe.R;
import com.well.swipe.SwipefreeApplication;
import com.well.swipe.preference.PreferenceCategory;
import com.well.swipe.preference.PreferenceTitle;
import com.well.swipe.preference.PreferenceTitleSummary;
import com.well.swipe.preference.SwipeCheckItemDialog;
import com.well.swipe.service.SwipeService;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipecomm.utils.SettingHelper;
import com.well.swipecomm.utils.Utils;

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
public class SwipeSettingActivity extends BaseSettingActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private TextView test;
    /**
     * 用来存SwipeService是否打开
     */
    private PreferenceCategory mSwipeToggle;

    /**
     * 选择触发方式
     * 1.底部边缘
     * 2.小白点
     */
    private PreferenceTitleSummary mSwipeOpenType;

    private SwipeCheckItemDialog mDialogOpenType;

    /**
     * 高级设置
     */
    private PreferenceTitle mAdvanced;

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

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        isBack(false);
        test = (TextView) findViewById(R.id.test_text);
        test.setText("density=" + this.getResources().getDisplayMetrics().density + ",swipe_dialog_for=" +
                getResources().getDimensionPixelSize(R.dimen.test));

        //Google
        SwipefreeApplication application = (SwipefreeApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mSwipeToggle = (PreferenceCategory) findViewById(R.id.swipe_toggle);

        mSwipeToggle.setTitle(getResources().getString(R.string.swipe_toggle));
        mSwipeToggle.setKey(SwipeSetting.SWIPE_TOGGLE);
        mSwipeToggle.getSwitchBtn().setChecked(mSwipeToggle.getBooleanValue(true));
        mSwipeToggle.getSwitchBtn().setOnCheckedChangeListener(this);
        mSwipeToggle.setIcon(getResources().getDrawable(R.drawable.enable_icon));

        mSwipeOpenType = (PreferenceTitleSummary) findViewById(R.id.swipe_open_type);
        mSwipeOpenType.setIcon(getResources().getDrawable(R.drawable.trigger_icon));
        mSwipeOpenType.setKey(SwipeSetting.SWIPE_OPEN_TYPE);
        mSwipeOpenType.setTitle(getResources().getString(R.string.swipe_open_type));
        mSwipeOpenType.setSummaryArray(getResources().getStringArray(R.array.swipe_open_type));
        mSwipeOpenType.refreshSummary(0);
        mSwipeOpenType.setOnClickListener(this);

        mAdvanced = (PreferenceTitle) findViewById(R.id.swipe_advanced);
        mAdvanced.setTitle(getResources().getString(R.string.swipe_advanced_setting));
        mAdvanced.setIcon(getResources().getDrawable(R.drawable.settings_icon));
        mAdvanced.showArrow();
        mAdvanced.setOnClickListener(this);


        //toogleSwipe(SettingHelper.getInstance(this).getBoolean(SwipeSetting.SWIPE_TOGGLE, true));

        mRoter5Star = (PreferenceTitle) findViewById(R.id.swipe_rate);
        mRoter5Star.setTitle(getResources().getString(R.string.swipe_about_rate5start));
        mRoter5Star.setIcon(getResources().getDrawable(R.drawable.rate_icon));
        mRoter5Star.setOnClickListener(this);

        mFeedback = (PreferenceTitle) findViewById(R.id.swipe_feedback);
        mFeedback.setTitle(getResources().getString(R.string.swipe_about_feedback));
        mFeedback.setIcon(getResources().getDrawable(R.drawable.feedback_icon));
        mFeedback.setOnClickListener(this);

        mVersion = (PreferenceTitleSummary) findViewById(R.id.swipe_version);
        mVersion.setTitle(getResources().getString(R.string.swipe_about_version));
        mVersion.setIcon(getResources().getDrawable(R.drawable.version_icon));
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

        mTracker.setScreenName("SwipeSettingsActivity::onResume()");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
        if (v == mRoter5Star) {
            openAppStore(this);
        } else if (v == mFeedback) {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:wellswipe.dev@foxmail.com"));
            data.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_title));
            data.putExtra(Intent.EXTRA_TEXT, "version:" + Utils.getVersionName(this) + "_phone:" +
                    android.os.Build.MODEL + "_android:" + android.os.Build.VERSION.RELEASE + "_" +
                    getResources().getString(R.string.feedback_content) + ":");
            startActivity(data);
        } else if (v == mSwipeOpenType) {
            mDialogOpenType = new SwipeCheckItemDialog(this);
            mDialogOpenType.setTitle(getResources().getString(R.string.swipe_open_type))
                    .addItem(mSwipeOpenType.getSummaryArray()[0], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeOpenType.setValues(0);
                            mSwipeOpenType.refreshSummary();
                            mDialogOpenType.dissmis();
                        }
                    }, mSwipeOpenType.getIntValue() == 0)
                    .addItem(mSwipeOpenType.getSummaryArray()[1], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeOpenType.setValues(1);
                            mSwipeOpenType.refreshSummary();
                            mDialogOpenType.dissmis();
                        }
                    }, mSwipeOpenType.getIntValue() == 1)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mService.initWhiteDot();
                        }
                    })
                    .show();
        } else if (v == mAdvanced) {
            Intent intent = new Intent(this, SwipeSettingAdvancedActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mSwipeToggle.getSwitchBtn()) {
            mSwipeToggle.setValues(isChecked);
            if (isChecked) {
                startService(new Intent(SwipeSettingActivity.this, SwipeService.class));
                bindSwipeService();
                //toogleSwipe(true);
            } else {
                //toogleSwipe(false);
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
//            mSwipeWhitelist.setTitle(String.format(getResources().getString(R.string.swipe_whitelist),
//                    mService.getLauncherMode().loadWhitelist(getBaseContext()).size()));

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}
