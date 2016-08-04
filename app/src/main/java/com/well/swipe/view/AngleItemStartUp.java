package com.well.swipe.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/13/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class AngleItemStartUp extends AngleItemCommon {

    private ImageView mDelIcon;

    public RecentTag mRecentTag;

    public AngleItemStartUp(Context context) {
        super(context);
    }

    public AngleItemStartUp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AngleItemStartUp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDelIcon = (ImageView) findViewById(R.id.angle_item_delete);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void showDelBtn() {
        mDelIcon.setVisibility(View.VISIBLE);
    }

    public void hideDelBtn() {
        mDelIcon.setVisibility(View.GONE);
    }

    public View getDelBtn() {
        return mDelIcon;
    }


    public static class RecentTag {

        public ActivityManager.RecentTaskInfo info;

        public Intent intent;

    }
}
