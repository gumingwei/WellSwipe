package com.well.swipe.view;

import android.view.View;

/**
 * Created by mingwei on 3/25/16.
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
