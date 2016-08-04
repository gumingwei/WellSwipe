package com.well.swipe.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.well.swipe.R;
import com.well.swipe.view.CheckItemLayout;

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
 * 滑出时机Dialog
 */
public class SwipeCheckItemDialog extends SwipeDialog {

    private TextView mTitle;

    private View mContentView;

    private LinearLayout mItemContent;

    private CheckItemLayout mItemView;

    public SwipeCheckItemDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.swipe_dialog_for, null);
        mTitle = (TextView) mContentView.findViewById(R.id.dialog_title);
        mItemContent = (LinearLayout) mContentView.findViewById(R.id.dialog_content_list);
        return mContentView;
    }

    public SwipeCheckItemDialog addItem(String text, View.OnClickListener listener, boolean check) {
        mItemView = (CheckItemLayout) LayoutInflater.from(getContext()).inflate(R.layout.check_item, null);
        mItemView.setTitle(text);
        mItemView.setChecked(check);
        mItemView.setOnClickListener(listener);
        mItemContent.addView(mItemView);
        return this;
    }

    public SwipeCheckItemDialog setTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    @Override
    public void dissmis() {
        super.dissmis();
        mItemContent.removeAllViews();
    }
}
