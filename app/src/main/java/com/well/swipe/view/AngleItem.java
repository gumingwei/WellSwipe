package com.well.swipe.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/13/16.
 */
public class AngleItem extends RelativeLayout {

    private TextView mText;

    private ImageView mIcon;

    public AngleItem(Context context) {
        super(context);
    }

    public AngleItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AngleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mText = (TextView) findViewById(R.id.angle_item_title);
        mIcon = (ImageView) findViewById(R.id.angle_item_icon);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //requestFocus();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //og.i("Gmw", "AngleItem-onTouchEvent-down=" + mText.getText().toString());
                break;
            case MotionEvent.ACTION_MOVE:
                //requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setTitle(String title) {
        mText.setText(title);
    }

    public String getTitle() {
        return mText.getText().toString();
    }

    public void setItemIcon(Bitmap icon) {
        mIcon.setImageBitmap(icon);
    }
}
