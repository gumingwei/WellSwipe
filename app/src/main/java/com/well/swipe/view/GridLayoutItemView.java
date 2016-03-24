package com.well.swipe.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/22/16.
 */
public class GridLayoutItemView extends AngleItemCommon {

    private ImageView mImageView;

    private TextView mTextView;

    private CheckBox mCheckBox;

    public GridLayoutItemView(Context context) {
        this(context, null);
    }

    public GridLayoutItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridLayoutItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImageView = (ImageView) findViewById(R.id.appindex_item_icon);
        mTextView = (TextView) findViewById(R.id.appindex_item_title);
        mCheckBox = (CheckBox) findViewById(R.id.appindex_item_check);
    }

    public void setItemIcon(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    public void setItemTitle(String title) {
        mTextView.setText(title);
    }

    public void setChecked(boolean check) {
        mCheckBox.setChecked(check);
    }

    public CheckBox getCheckBox() {
        return mCheckBox;
    }
}
