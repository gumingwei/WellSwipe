package com.well.swipe.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.well.swipe.R;
import com.well.swipe.view.SwitchButton;

/**
 * Created by mingwei on 3/30/16.
 */
public class PreferenceCategory extends SwipePreference {

    private ImageView mIcon;

    private TextView mTitle;

    private SwitchButton mSwitchBtn;

    public PreferenceCategory(Context context) {
        this(context, null);
    }

    public PreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIcon = (ImageView) findViewById(R.id.preference_icon);
        mTitle = (TextView) findViewById(R.id.preference_title);
        mSwitchBtn = (SwitchButton) findViewById(R.id.preference_switchbtn);
    }

    public void setPreferenceTitle(int title) {
        setPreferenceTitle(getContext().getString(title));
    }

    public void setPreferenceTitle(String title) {
        mTitle.setText(title);
    }

    public SwitchButton getSwitchBtn() {
        return mSwitchBtn;
    }
}
