package com.well.swipe.view;

/**
 * Created by mingwei on 4/13/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 * 拖拽控制器
 */
public class DragController {


    /**
     * Drag监听
     */
    interface DragListener {

        /**
         * 拖拽开始时调用
         */
        void onDragStart();

        /**
         * 拖拽结束时调用
         */
        void onDragEnd();

    }
}
