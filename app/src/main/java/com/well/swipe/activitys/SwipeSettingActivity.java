package com.well.swipe.activitys;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.well.swipe.R;
import com.well.swipe.service.SwipeService;

public class SwipeSettingActivity extends AppCompatActivity {

    TextView test;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = (TextView) findViewById(R.id.test_text);
        test.setText("density=" + this.getResources().getDisplayMetrics().density + ",test=" +
                getResources().getDimensionPixelSize(R.dimen.test));

//        test.setOnLongClickListener();
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(SwipeSettingActivity.this, SwipeService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(SwipeSettingActivity.this, SwipeService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
