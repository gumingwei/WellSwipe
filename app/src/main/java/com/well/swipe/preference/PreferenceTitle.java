package com.well.swipe.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/31/16.
 */
public class PreferenceTitle extends SwipePreference {

    private TextView mTitle;

    public PreferenceTitle(Context context) {
        this(context, null);
    }

    public PreferenceTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitle = (TextView) findViewById(R.id.preference_title);
    }

    public void setTitle(int title) {
        setTitle(getResources().getString(title));
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }
}
