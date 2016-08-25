package com.well.swipecomm.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.well.swipecomm.R;
import com.well.swipecomm.utils.SettingHelper;
import com.well.swipecomm.utils.Utils;


/**
 * Created by mingwei on 1/12/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class BubbleView extends ImageView {

    private static final String POSITION_X = "bubble_x_in_screen";

    private static final String POSITION_Y = "bubble_y_in_screen";

    private int mDefaultHeight = 100;

    private int mDefaultWidth = 115;

    private WindowManager.LayoutParams mParams;

    private WindowManager mManager;

    private float mX;

    private float mY;

    private float mStartX;

    private float mStartY;

    private long mStartTime;

    private int mStatusH;

    private int x;

    private int y;

    private boolean isMove;

    private OnOpenClickListener mOpenClickListener;

    public interface OnOpenClickListener {

        void onOpenClick();
    }

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(context.getResources().getDrawable(R.drawable.swipe_whitedot_pressed));
        mStatusH = Utils.getStatusBarHeight(context);
        int xy[] = loadPosition();
        if (x == 0 && y == 0) {
            x = context.getResources().getDisplayMetrics().widthPixels;
            y = context.getResources().getDisplayMetrics().widthPixels + mStatusH - 150;
        } else {
            x = xy[0];
            y = xy[1];
        }
        initManager(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mX = e.getRawX();
        mY = e.getRawY() - mStatusH;
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = e.getX();
                mStartY = e.getY();
                mStartTime = System.currentTimeMillis();
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mParams.x = (int) (mX - mStartX);
                mParams.y = (int) (mY - mStartY);
                mManager.updateViewLayout(this, mParams);
                float x = e.getX();
                float y = e.getY();
                if (Math.abs(x - mStartX) > 5 || Math.abs(y - mStartY) > 5) {
                    isMove = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //savePosition(mParams.x, mParams.y);
                springBack();
                float newX = e.getX();
                float newY = e.getY();
                long newTime = System.currentTimeMillis();
                if (Math.abs(newX - mStartX) < 8 && Math.abs(newY - mStartY) < 8 && (newTime - mStartTime) < 500) {
                    if (!isMove) {
                        mOpenClickListener.onOpenClick();
                    }
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Utils.dp2px(getContext(), 40);
        setMeasuredDimension(size, size);
    }

    private void initManager(int x, int y) {
        mParams = new WindowManager.LayoutParams();
        mManager = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;

        mParams.x = x;
        mParams.y = y;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public boolean isManager() {
        return mManager != null;
    }

    public void show() {
        if (isManager()) {
            if (this.getParent() == null) {
                mManager.addView(this, mParams);
            }
        }
    }

    public void dismiss() {
        if (isManager()) {
            if (this.getParent() != null) {
                mManager.removeView(this);
            }
        }
    }

    public void springBack() {
        int x = mParams.x;
        int halfwidth = getContext().getResources().getDisplayMetrics().widthPixels / 2;
        ValueAnimator valueAnimator = null;
        if (x > halfwidth) {
            valueAnimator = ValueAnimator.ofFloat(x, halfwidth * 2);
        } else {
            valueAnimator = ValueAnimator.ofFloat(x, 0);
        }
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                mParams.x = (int) values;
                update();
            }
        });
        valueAnimator.start();
    }

    public void update() {
        mManager.updateViewLayout(this, mParams);
    }

    public boolean isLeft() {
        return mParams.x < (getContext().getResources().getDisplayMetrics().widthPixels / 2);
    }

    /**
     * 保存位置
     *
     * @param x
     * @param y
     */
    public void savePosition(int x, int y) {
        SettingHelper.getInstance(getContext()).putInt(POSITION_X, x);
        SettingHelper.getInstance(getContext()).putInt(POSITION_Y, y);
    }

    /**
     * 读书位置
     *
     * @return
     */
    public int[] loadPosition() {
        int xy[] = new int[2];
        xy[0] = SettingHelper.getInstance(getContext()).getInt(POSITION_X);
        xy[1] = SettingHelper.getInstance(getContext()).getInt(POSITION_Y);
        return xy;
    }

    public void setOnOpenClickListener(OnOpenClickListener listener) {
        mOpenClickListener = listener;
    }

}

