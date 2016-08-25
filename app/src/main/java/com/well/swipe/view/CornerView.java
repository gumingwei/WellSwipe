package com.well.swipe.view;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.well.swipe.R;
import com.well.swipecomm.view.PositionState;

/**
 * Created by mingwei on 4/25/16.
 * <p/>
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 * <p/>
 * <p/>
 * 角落里的状态View分别在正常模式，编辑模式，拖动模式下呈现不同的状态给用户
 */
public class CornerView extends FrameLayout {

    private int mHeight;

    private int mWidth;

    private ImageView mIcon;

    private int mPositionState;

    private int mIconSize;

    private int mIconOffset;

    private int mState = STATE_NORMAL;

    public static final int STATE_NORMAL = 1;

    public static final int STATE_EDIT = 2;

    public static final int STATE_DRAG = 3;

    private float mLastEventX;

    private float mLastEventY;

    private long mClickTime;

    private OnCornerClickListener mListener;

    public interface OnCornerClickListener {
        /**
         * 点击事件
         */
        void cornerEvent();
    }

    public CornerView(Context context) {
        this(context, null);
    }

    public CornerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mIcon = new ImageView(context);
        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.corner_icon_size);
        mIconOffset = context.getResources().getDimensionPixelSize(R.dimen.corner_icon_offset);
        LayoutParams params = new LayoutParams(mIconSize, mIconSize);
        setState(STATE_NORMAL);
        addView(mIcon, params);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //mBackgroung.layout(0, 0, mWidth, mHeight);
        mIcon.layout(0, 0, mWidth, mHeight);
        if (isLeft()) {
            mIcon.layout(mIconOffset, mHeight - mIconSize - mIconOffset, mIconSize + mIconOffset, mHeight - mIconOffset);
        } else if (isRight()) {
            mIcon.layout(mWidth - mIconSize - mIconOffset, mHeight - mIconSize - mIconOffset, mWidth - mIconOffset, mHeight - mIconOffset);
        }
    }

    public void setPositionState(int state) {
        mPositionState = state;
        //mBackgroung.setPositionState(state);
        invalidate();
    }

    public void setOnCornerListener(OnCornerClickListener listener) {
        mListener = listener;
    }

    public boolean isLeft() {
        return mPositionState == PositionState.POSITION_STATE_LEFT;
    }

    public boolean isRight() {
        return mPositionState == PositionState.POSITION_STATE_RIGHT;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
        if (mState == STATE_NORMAL) {
            mIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.close));
        } else if (mState == STATE_EDIT) {
            mIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_arrow_back));
        } else if (mState == STATE_DRAG) {
            mIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.delete_zone_trash_1));
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastEventX = event.getX();
                mLastEventY = event.getY();
                mClickTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                float newX = event.getX();
                float newY = event.getY();
                long clicktime = System.currentTimeMillis();
                if (Math.abs(mLastEventX - newX) < 10 && Math.abs(mLastEventY - newY) < 10) {
                    long time = Math.abs(mClickTime - clicktime);
                    if (time < 300) {
                        mListener.cornerEvent();
                    }
                }
                break;
        }
        return true;
    }

    public void setTrashState(float p) {
        if (p < 0.3f) {
            mIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.delete_zone_trash_1));
        } else if (p > 0.3f && p < 0.6f) {
            mIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.delete_zone_trash_2));
        } else if (p > 0.6f) {
            mIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.delete_zone_trash_3));
        }
    }
}
