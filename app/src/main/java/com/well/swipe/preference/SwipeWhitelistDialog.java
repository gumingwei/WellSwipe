package com.well.swipe.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/31/16.
 */
public class SwipeWhitelistDialog extends SwipeDialog {

    private TextView mTitle;

    private View mContentView;

    private LinearLayout mItemContent;

    private GridView mGridView;

    private Button mNegativeBtn;

    private Button mPositiveBtn;

    public SwipeWhitelistDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.swipe_dialog_whitelist, null);
        mTitle = (TextView) mContentView.findViewById(R.id.dialog_title);
        mItemContent = (LinearLayout) mContentView.findViewById(R.id.dialog_content_list);
        mGridView = (GridView) mContentView.findViewById(R.id.dialog_gridview);
        mNegativeBtn = (Button) mContentView.findViewById(R.id.dialog_cancel);
        mPositiveBtn = (Button) mContentView.findViewById(R.id.dialog_ok);
        return mContentView;
    }

    public SwipeWhitelistDialog setTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    public SwipeWhitelistDialog onPositive(View.OnClickListener listener) {
        mPositiveBtn.setOnClickListener(listener);
        return this;
    }

    public SwipeWhitelistDialog onNegative(View.OnClickListener listener) {
        mNegativeBtn.setOnClickListener(listener);
        return this;
    }

    @Override
    public void dissmis() {
        super.dissmis();
        mItemContent.removeAllViews();
    }
}
