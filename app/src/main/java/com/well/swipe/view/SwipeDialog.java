package com.well.swipe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/25/16.
 */
public abstract class SwipeDialog extends RelativeLayout implements View.OnClickListener {

    public TextView mDialogTitle;

    public Button mPositiveBtn;

    public Button mNegativeBtn;

    public LinearLayout mContentLayout;

    public int mSize;

    public String mTitleFormat;

    OnDialogListener mOnDialogListener;

    public SwipeDialog(Context context) {
        this(context, null);
    }

    public SwipeDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSize = getResources().getDimensionPixelSize(R.dimen.angleitem_size);
        mTitleFormat = getResources().getString(R.string.swipe_edit_header_title);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDialogTitle = (TextView) findViewById(R.id.swipe_edit_header_title);
        mContentLayout = (LinearLayout) findViewById(R.id.swipe_edit_content);
        mPositiveBtn = (Button) findViewById(R.id.swipe_edit_footer_ok);
        mNegativeBtn = (Button) findViewById(R.id.swipe_edit_footer_cancel);
        mContentLayout.addView(createContentView());
    }

    public void setOnDialogListener(OnDialogListener listener) {
        mOnDialogListener = listener;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public abstract View createContentView();
}
