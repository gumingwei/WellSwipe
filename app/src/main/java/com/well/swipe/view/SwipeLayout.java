package com.well.swipe.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.well.swipe.R;
import com.well.swipecomm.utils.SwipeWindowManager;
import com.well.swipecomm.view.SwipeToast;


/**
 * Created by mingwei on 3/9/16.
 * <p/>
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 */
public class SwipeLayout extends RelativeLayout implements AngleLayout.OnOffListener {

    private SwipeWindowManager mManager;

    private AngleLayout mAngleLayout;

    private LinearLayout mBgLayout;

    private SwipeEditFavoriteEditDialog mFavoriteLayout;

    private SwipeEditToolsEditDialog mToolsLayout;

    static int mAnimatorDur = 250;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mFavoriteLayout = (SwipeEditFavoriteEditDialog) LayoutInflater.from(context).inflate(R.layout.swipe_edit_favorite_layout, null);
        addView(mFavoriteLayout, params);

        mToolsLayout = (SwipeEditToolsEditDialog) LayoutInflater.from(context).inflate(R.layout.swipe_edit_tools_layout, null);
        addView(mToolsLayout, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAngleLayout = (AngleLayout) findViewById(R.id.anglelayout);
        mAngleLayout.setOnOffListener(this);
        mBgLayout = (LinearLayout) findViewById(R.id.swipe_bg_layout);
        mManager = new SwipeWindowManager(0, 0, getContext());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 向左切换
     */
    public void switchLeft() {
        show();
        if (isSwipeOff()) {
            mAngleLayout.setPositionLeft();
        }
    }

    /**
     * 向右切换
     */
    public void switchRight() {
        show();
        if (isSwipeOff()) {
            mAngleLayout.setPositionRight();
        }
    }

    public boolean hasView() {
        return mManager.hasView(this);
    }

    /**
     * 是否关闭
     *
     * @return
     */
    public boolean isSwipeOff() {
        return mAngleLayout.getSwitchType() == AngleLayout.SWITCH_TYPE_OFF;
    }

    public void show() {
        mManager.show(this);
    }

    public void dismiss() {
        mManager.hide(this);
    }

    public void dismissAnimator() {
        mAngleLayout.offnoAnimator();
        dismiss();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            if (mFavoriteLayout.getVisibility() == VISIBLE) {
                setEditFavoriteGone();
                return true;
            }
            if (mToolsLayout.getVisibility() == VISIBLE) {
                setEditToolsGone();
                return true;
            }
            if (mAngleLayout.getEditState() == AngleLayout.STATE_EDIT) {
                mAngleLayout.setEditState(AngleLayout.STATE_NORMAL);
                return true;
            }
            if (mAngleLayout.getEditState() == AngleLayout.STATE_NORMAL) {
                mAngleLayout.off();
                return true;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 切换AngleLayout
     */
    public void switchAngleLayout() {
        mAngleLayout.switchAngleLayout();
    }

    public void on() {
        mAngleLayout.on();
    }

    @Override
    public void onOff() {
        dismiss();
    }

    @Override
    public void change(float alpha) {
        setSwipeBackgroundViewAlpha(alpha);
    }

    /**
     * 改变背景的透明度
     *
     * @param a
     */
    public void setSwipeBackgroundViewAlpha(float a) {
        mBgLayout.setAlpha(((int) (a * 10) / 10f));
    }

    public AngleLayout getAngleLayout() {
        return mAngleLayout;
    }

    public SwipeEditFavoriteEditDialog getEditFavoriteLayout() {
        return mFavoriteLayout;
    }

    public SwipeEditToolsEditDialog getEditToolsLayout() {
        return mToolsLayout;
    }

    /**
     * 打开Favorite编辑Dialog
     */
    public void setEditFavoritetVisiable() {
        mFavoriteLayout.setVisibility(INVISIBLE);
        mFavoriteLayout.bringToFront();
        showAnimator(mFavoriteLayout);
    }

    /**
     * 关闭Favorite编辑Dialog
     */
    public void setEditFavoriteGone() {
        hideAnimator(mFavoriteLayout);
    }

    public void setEditToolsVisiable() {
        mToolsLayout.setVisibility(INVISIBLE);
        mToolsLayout.bringToFront();
        showAnimator(mToolsLayout);
    }

    public void setEditToolsGone() {
        hideAnimator(mToolsLayout);
    }

    /**
     * 打开动画
     *
     * @param view
     */
    public void showAnimator(final View view) {
        final AnimatorSet animSet = new AnimatorSet();
        ValueAnimator scaleAnim = ValueAnimator.ofFloat(0.85f, 1f);
        scaleAnim.setDuration(mAnimatorDur);
        scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float s = (float) animation.getAnimatedValue();
                view.setScaleX(s);
                view.setScaleY(s);
            }
        });

        ValueAnimator alphaAnim = ValueAnimator.ofFloat(0f, 1f);
        alphaAnim.setDuration(mAnimatorDur);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float a = (float) animation.getAnimatedValue();
                view.setAlpha(a);
            }
        });
        animSet.playTogether(scaleAnim, alphaAnim);
        post(new Runnable() {
            @Override
            public void run() {
                view.setPivotY(view.getHeight() / 2);
                view.setPivotX(view.getWidth() / 2);
                view.setVisibility(View.VISIBLE);
                animSet.start();
            }
        });
    }

    /**
     * 关闭动画
     *
     * @param view
     */
    public void hideAnimator(final View view) {
        final AnimatorSet animSet = new AnimatorSet();
        ValueAnimator scaleAnim = ValueAnimator.ofFloat(1f, 0.85f);
        scaleAnim.setDuration(mAnimatorDur);
        scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float s = (float) animation.getAnimatedValue();
                view.setScaleX(s);
                view.setScaleY(s);
            }
        });

        ValueAnimator alphaAnim = ValueAnimator.ofFloat(1f, 0f);
        alphaAnim.setDuration(mAnimatorDur);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float a = (float) animation.getAnimatedValue();
                view.setAlpha(a);
            }
        });
        alphaAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSet.playTogether(scaleAnim, alphaAnim);
        animSet.start();
    }

    public void removeBubble() {
        mFavoriteLayout.setVisibility(View.GONE);
    }

}
