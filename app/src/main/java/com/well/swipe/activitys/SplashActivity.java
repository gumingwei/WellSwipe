package com.well.swipe.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.well.swipe.R;
import com.well.swipe.SwipefreeApplication;
import com.well.swipe.service.SwipeService;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipecomm.utils.SettingHelper;

/**
 * Created by mingwei on 4/4/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class SplashActivity extends Activity {

    private final int REQUEST_ALERT_WINDOW = 1;

    RequestAlertDialog mAlertDialog;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (isEmulator(getApplicationContext())) {
            //Process.killProcess(android.os.Process.myPid());
        }
        SwipefreeApplication application = (SwipefreeApplication) getApplication();
        mTracker = application.getDefaultTracker();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                startPermission();
            } else {
                startSwipeSetting();
            }
        } else {
            startSwipeSetting();
        }


    }

    private void startPermission() {
        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void finishSplash() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                startSwipeSetting();
            } else {
                mAlertDialog = new RequestAlertDialog(this);
                mAlertDialog.setTitle(getString(R.string.request_windows_title)).
                        setContentDes(getString(R.string.request_windows_content)).
                        setPositiveTitle(getString(R.string.request_windows_pos)).
                        setNegativeTitle(getString(R.string.request_windows_nev)).
                        onPositive(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //google anylnitic
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Action")
                                        .setAction("authorized ALERT_WINDOWS")
                                        .build());
                                startPermission();
                                mAlertDialog.dissmis();
                            }
                        })
                        .onNegative(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Action")
                                        .setAction("denied ALERT_WINDOWS")
                                        .build());
                                finishSplash();
                                mAlertDialog.dissmis();
                            }
                        }).show();
            }
        }
    }

    private void startSwipeSetting() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SettingHelper.getInstance(getBaseContext()).getBoolean(SwipeSetting.SWIPE_TOGGLE, true)) {
                    startService(new Intent(getBaseContext(), SwipeService.class));
                }
                startActivity(new Intent(getBaseContext(), SwipeSettingActivity.class));
                finish();
            }
        }, 1000);
    }

    boolean isEmulator(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei != null && imei.equals("000000000000000")) {
                return true;
            }
            return (Build.MODEL.equals("sdk"))
                    || (Build.MODEL.equals("google_sdk"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
