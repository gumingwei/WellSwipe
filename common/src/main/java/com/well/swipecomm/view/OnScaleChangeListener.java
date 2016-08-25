package com.well.swipecomm.view;

/**
 * Created by mingwei on 3/12/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public interface OnScaleChangeListener {

    /**
     * 当scale发生变化的时候回传这个值
     * <p/>
     * 1.用于在手指拖动时:                       CatchView.OnEdgeSlidingListener
     * 2.松开手指时自动打开和关闭的过程中:         AngleLayout.OnOffListener
     * 3.点击Back键关闭动画的过程中
     * <改变背景SwipeBackgroundLayout的透明度>
     *
     * @param scale
     */
    void change(float scale);
}
