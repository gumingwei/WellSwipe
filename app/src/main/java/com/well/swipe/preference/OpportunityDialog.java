package com.well.swipe.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/31/16.
 * 滑出时机Dialog
 */
public class OpportunityDialog extends SwipeDialog {

    private View mContentView;

    public OpportunityDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.opportunity_dialog, null);
        return mContentView;
    }
}
