package com.well.swipecomm.view;

import android.view.View;

/**
 * Created by mingwei on 3/25/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public interface OnDialogListener {

    /**
     * 积极的
     * 对话框确定按钮
     *
     * @param view 当前对话框对象
     */
    void onPositive(View view);

    /**
     * 消极的
     * 对话框关闭按钮
     *
     * @param view 当前对话框对象
     */
    void onNegative(View view);
}
