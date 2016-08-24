package com.well.swipecomm.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.well.swipecomm.R;


/**
 * Created by mingwei on 3/9/16.
 * <p/>
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 */
public class SwipeToast extends RelativeLayout {

    private Toast mToast;

    private TextView mTextContent;

    public SwipeToast(Context context) {
        this(context, null);
    }

    public SwipeToast(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeToast(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mToast = new Toast(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextContent = (TextView) findViewById(R.id.swipe_toast_content);
    }

    public SwipeToast setText(String text) {
        mTextContent.setText(text);
        return this;
    }

    public SwipeToast setHtmlText(String html) {
        mTextContent.setText(Html.fromHtml(html));
        return this;
    }

}
