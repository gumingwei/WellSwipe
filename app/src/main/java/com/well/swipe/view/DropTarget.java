package com.well.swipe.view;

import android.graphics.Rect;

/**
 * Created by mingwei on 4/13/16.
 * 拖拽目标
 */
public interface DropTarget {


    class DragObject {


        private DragObject() {

        }
    }

    public static class DragEnforcer implements DragController.DragListener {


        DragEnforcer() {

        }

        /**
         *
         */
        void onDragEnter() {

        }

        /**
         *
         */
        void onDragExit() {

        }

        @Override
        public void onDragStart() {

        }

        @Override
        public void onDragEnd() {

        }


    }

    /**
     * 是否允许拖拽
     *
     * @return
     */
    boolean isDropEnable();

    /**
     * 拖动时调用
     */
    void onDrop();

    /**
     * 拖进去时调用
     */
    void onDropEnter();

    /**
     * 拖到上方时调用
     */
    void onDropOver();

    /**
     * 拖出来时调用
     */
    void onDropExit();

    /**
     * @param object
     * @return
     */
    boolean accessDrop(DragObject object);

    /**
     * @param outRect
     */
    void getHitRect(Rect outRect);

    /**
     * @param loc
     */
    void getLocationInDragLayer(int[] loc);
}
