package com.well.swipe.view;

/**
 * Created by mingwei on 3/12/16.
 */
public interface OnScaleChangeListener {

    /**
     * 当scale发生变化的时候回传这个值
     * <p/>
     * 1.用于在手指拖动时:                  CatchView.OnEdgeSlidingListener
     * 2.松开手指时自动打开和关闭的过程中:    AngleLayout.OnOffListener
     * 3.点击Back键关闭动画的过程中
     * <改变背景SwipeBackgroundLayout的透明度>
     *
     * @param scale
     */
    void change(float scale);
}
