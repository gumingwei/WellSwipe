package com.well.swipe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.well.swipe.R;
import com.well.swipe.utils.SwipeWindowManager;

/**
 * Created by mingwei on 3/19/16.
 */
public class SwipeEditLayout extends LinearLayout {

    private SwipeWindowManager mManager;

    private TextView mHeaderTitle;

    private String mTitleFormat;

    public SwipeEditLayout(Context context) {
        this(context, null);
    }

    public SwipeEditLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeEditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mManager = new SwipeWindowManager(0, 0, context);
        mTitleFormat = getResources().getString(R.string.swipe_edit_header_title);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderTitle = (TextView) findViewById(R.id.swipe_edit_header_title);
        mHeaderTitle.setText(String.format(mTitleFormat, "1", "2"));
    }

    public void show() {
        mManager.show(this);
    }

    public void hide() {
        mManager.hide(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            hide();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
