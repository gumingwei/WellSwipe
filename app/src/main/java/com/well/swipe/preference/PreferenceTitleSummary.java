package com.well.swipe.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/30/16.
 */
public class PreferenceTitleSummary extends SwipePreference {

    private TextView mTitle;

    private TextView mSummary;

    private String mSummaryArray[] = new String[]{};

    public PreferenceTitleSummary(Context context) {
        this(context, null);
    }

    public PreferenceTitleSummary(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceTitleSummary(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitle = (TextView) findViewById(R.id.preference_title);
        mSummary = (TextView) findViewById(R.id.preference_summary);
    }

    public void setTitle(int title) {
        setTitle(getContext().getString(title));
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setSummary(int summary) {
        setSummary(getContext().getString(summary));
    }

    public void setSummaryArray(String array[]) {
        mSummaryArray = array;
    }

    public void setSummary(String summary) {
        mSummary.setText(summary);
    }

    public String[] getSummaryArray() {
        return mSummaryArray;
    }

    public void refreshSummary() {
        mSummary.setText(mSummaryArray[getIntValue()]);
    }

}
