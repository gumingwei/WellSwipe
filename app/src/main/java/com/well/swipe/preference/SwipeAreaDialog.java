package com.well.swipe.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.well.swipe.R;
import com.well.swipe.view.CheckItemLayout;
import com.well.swipe.view.SeekBarCompat;

/**
 * Created by mingwei on 3/31/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class SwipeAreaDialog extends SwipeDialog {

    private TextView mTitle;

    private LinearLayout mContentView;

    private LinearLayout mItemContent;

    private CheckItemLayout mItemView;

    private SeekBarCompat mSeekBak;

    private Button mNegativeBtn;

    private Button mPositiveBtn;

    public SwipeAreaDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        mContentView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.swipe_dialog_area, null);
        mTitle = (TextView) mContentView.findViewById(R.id.dialog_title);
        mItemContent = (LinearLayout) mContentView.findViewById(R.id.dialog_content_list);
        mSeekBak = (SeekBarCompat) mContentView.findViewById(R.id.materialSeekBar);
        mSeekBak.setMax(10);
        mNegativeBtn = (Button) mContentView.findViewById(R.id.dialog_cancel);
        mPositiveBtn = (Button) mContentView.findViewById(R.id.dialog_ok);
        return mContentView;
    }

    public SwipeAreaDialog addItem(String text, View.OnClickListener listener, boolean check) {
        mItemView = (CheckItemLayout) LayoutInflater.from(getContext()).inflate(R.layout.check_item, null);
        mItemView.setTitle(text);
        mItemView.setChecked(check);
        mItemView.setOnClickListener(listener);
        mItemContent.addView(mItemView);
        return this;
    }

    public SwipeAreaDialog onPositive(View.OnClickListener listener) {
        mPositiveBtn.setOnClickListener(listener);
        return this;
    }

    public SwipeAreaDialog onNegative(View.OnClickListener listener) {
        mNegativeBtn.setOnClickListener(listener);
        return this;
    }

    public SwipeAreaDialog setTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    public SwipeAreaDialog setProgress(int progress) {
        mSeekBak.setProgress(progress);
        return this;
    }

    public SwipeAreaDialog setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        mSeekBak.setOnSeekBarChangeListener(listener);
        return this;
    }

    public int getProgress() {
        return mSeekBak.getProgress();
    }

    public boolean isChecked() {
        return mItemView.isChecked();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dissmis() {
        super.dissmis();
        mItemContent.removeAllViews();
    }
}
