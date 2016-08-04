package com.well.swipe.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/19/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class AngleItemCommon extends RelativeLayout {

    private ImageView mIcon;

    private TextView mText;
    /**
     * 存储的view索引
     */
    private int mIndex = -1;
    /**
     * 相对父控件的坐标X
     */
    private double mParentX;
    /**
     * 相对父控件的坐标Y
     */
    private double mParentY;


    public AngleItemCommon(Context context) {
        this(context, null);
    }

    public AngleItemCommon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleItemCommon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIcon = (ImageView) findViewById(R.id.item_icon);
        mText = (TextView) findViewById(R.id.item_title);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

    public void setItemIconBackground(Drawable drawable) {
        mIcon.setBackgroundDrawable(drawable);
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setParentX(double x) {
        mParentX = x;
    }

    public void setParentY(double y) {
        mParentY = y;
    }

    public double getParentX() {
        return mParentX;
    }

    public double getParentY() {
        return mParentY;
    }

}
