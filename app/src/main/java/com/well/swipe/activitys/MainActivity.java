package com.well.swipe.activitys;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.well.swipe.R;
import com.well.swipe.service.SwipeService;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //BubbleView b = new BubbleView(getBaseContext());
        //b.show();
        //Log.i("Gmw", "hello=" + Hello.hello(2));
        startService(new Intent(MainActivity.this, SwipeService.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(MainActivity.this, SwipeService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
