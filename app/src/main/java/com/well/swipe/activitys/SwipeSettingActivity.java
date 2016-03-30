package com.well.swipe.activitys;


import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.well.swipe.R;
import com.well.swipe.preference.PreferenceCategory;
import com.well.swipe.preference.PreferenceItem;
import com.well.swipe.preference.OpportunityDialog;
import com.well.swipe.service.SwipeService;

public class SwipeSettingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView test;
    PreferenceCategory mToggle;
    PreferenceItem mOpenSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = (TextView) findViewById(R.id.test_text);
        test.setText("density=" + this.getResources().getDisplayMetrics().density + ",opportunity_dialog=" +
                getResources().getDimensionPixelSize(R.dimen.test));
        findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mToggle = (PreferenceCategory) findViewById(R.id.swipe_toggle);
        mToggle.setPreferenceTitle("开启");
        mToggle.setKey("swipe_toogle");
        mToggle.setValues(true);
        mToggle.getSwitchBtn().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(SwipeSettingActivity.this, SwipeService.class));
                } else {
                    stopService(new Intent(SwipeSettingActivity.this, SwipeService.class));
                }
            }
        });

        //Log.i("Gmw", "o=" + mToggle.getBooleanValue());
        mOpenSetting = (PreferenceItem) findViewById(R.id.swipe_opensetting);
        mOpenSetting.setTitle("划出时机");
        mOpenSetting.setSummary("桌面");
        mOpenSetting.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startService(new Intent(SwipeSettingActivity.this, SwipeService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v == mOpenSetting) {
            OpportunityDialog dialog = new OpportunityDialog(this);
            dialog.show();
        }
    }
}
