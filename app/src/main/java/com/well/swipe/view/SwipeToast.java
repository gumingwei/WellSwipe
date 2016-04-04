package com.well.swipe.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.well.swipe.R;

/**
 * Created by mingwei on 4/3/16.
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
