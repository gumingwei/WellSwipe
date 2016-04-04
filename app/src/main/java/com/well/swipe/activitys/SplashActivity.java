package com.well.swipe.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.well.swipe.R;
import com.well.swipe.service.SwipeService;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipe.utils.SettingHelper;

/**
 * Created by mingwei on 4/4/16.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
}
