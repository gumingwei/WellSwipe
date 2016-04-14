package com.well.swipe.view;

/**
 * Created by mingwei on 4/13/16.
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
