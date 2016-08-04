package com.well.swipe.activitys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.well.swipe.R;
import com.well.swipe.preference.SwipeDialog;

/**
 * Created by mingwei on 4/19/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class RequestAlertDialog extends SwipeDialog {

    private View mContentView;

    private TextView mTitle;

    private TextView mContentDes;

    private Button mNegativeBtn;

    private Button mPositiveBtn;

    public RequestAlertDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.swipe_dialog_alertwindows, null);
        mTitle = (TextView) mContentView.findViewById(R.id.dialog_title);
        mContentDes = (TextView) mContentView.findViewById(R.id.dialog_content_des);
        mNegativeBtn = (Button) mContentView.findViewById(R.id.dialog_cancel);
        mPositiveBtn = (Button) mContentView.findViewById(R.id.dialog_ok);
        return mContentView;
    }

    public RequestAlertDialog setTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    public RequestAlertDialog setContentDes(String content) {
        mContentDes.setText(content);
        return this;
    }

    public RequestAlertDialog setPositiveTitle(String title) {
        mPositiveBtn.setText(title);
        return this;
    }

    public RequestAlertDialog setNegativeTitle(String title) {
        mNegativeBtn.setText(title);
        return this;
    }

    public RequestAlertDialog onPositive(View.OnClickListener listener) {
        mPositiveBtn.setOnClickListener(listener);
        return this;
    }

    public RequestAlertDialog onNegative(View.OnClickListener listener) {
        mNegativeBtn.setOnClickListener(listener);
        return this;
    }
}
