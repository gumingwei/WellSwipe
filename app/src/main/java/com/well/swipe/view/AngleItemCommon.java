package com.well.swipe.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/19/16.
 */
public abstract class AngleItemCommon extends RelativeLayout {

    private ImageView mIcon;

    private TextView mText;

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
        mText = (TextView) findViewById(R.id.item_title);
        mIcon = (ImageView) findViewById(R.id.item_icon);
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
