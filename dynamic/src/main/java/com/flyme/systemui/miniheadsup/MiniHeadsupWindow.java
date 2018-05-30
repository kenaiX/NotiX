package com.flyme.systemui.miniheadsup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyme.systemui.dynamic.R;

public class MiniHeadsupWindow extends ViewGroup implements View.OnClickListener {


    public MiniHeadsupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    GroupRectHolder mOriginRectHolder, mFinalRectHolder;
    Rect mWindowRect = new Rect();
    Rect mFirstGroupRect = new Rect();
    Rect mSecondGroupRect1 = new Rect();
    Rect mSecondGroupRect2 = new Rect();

    boolean isFirstLayout = true;

    public void setFirstGroupColor(int color) {
        mFirstGroup.setButtonColor(color);
    }

    public void setSecondGroupColor(int color1, int color2) {
        mSecondGroup1.setButtonColor(color1);
        mSecondGroup2.setButtonColor(color2);
    }

    public void setFirstGroupValue(Drawable d, String s) {
        mFirstIcon.setImageDrawable(d);
        mFirstText.setText(s);
    }

    public void setSecondGroupValue(Drawable d1, Drawable d2) {
        mSecondIcon1.setVisibility(VISIBLE);
        mSecondIcon2.setVisibility(VISIBLE);
        mSecondText1.setVisibility(INVISIBLE);
        mSecondText2.setVisibility(INVISIBLE);

        mSecondIcon1.setImageDrawable(d1);
        mSecondIcon2.setImageDrawable(d2);
    }

    public void setSecondGroupValue(String s1, String s2) {
        mSecondText1.setVisibility(VISIBLE);
        mSecondText2.setVisibility(VISIBLE);
        mSecondIcon1.setVisibility(INVISIBLE);
        mSecondIcon2.setVisibility(INVISIBLE);

        mSecondText1.setText(s1);
        mSecondText2.setText(s2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        mWindowRect.set(0, 47, width, height);
        if (isFirstLayout) {
            onFirstLayout(width);
            isFirstLayout = false;
        }
        computeRectsDependOnState(width, height);

        measureChild(mFirstGroup, mFirstGroupRect);
        measureChild(mSecondGroup1, mSecondGroupRect1);
        measureChild(mSecondGroup2, mSecondGroupRect2);

        setMeasuredDimension(width, height);
    }

    void measureChild(View v, Rect rect) {
        v.measure(
                MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChild(mSecondGroup1, mSecondGroupRect1);
        layoutChild(mSecondGroup2, mSecondGroupRect2);
        layoutChild(mFirstGroup, mFirstGroupRect);
    }

    final void layoutChild(View v, Rect rect) {
        v.layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    MiniHeadsupFirstGroup mFirstGroup;
    MiniHeadsupSecondGroup mSecondGroup1, mSecondGroup2;
    ImageView mFirstIcon, mSecondIcon1, mSecondIcon2;
    TextView mFirstText, mSecondText1, mSecondText2;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFirstGroup = (MiniHeadsupFirstGroup) findViewById(R.id.first_group);
        mSecondGroup1 = (MiniHeadsupSecondGroup) findViewById(R.id.second_group_1);
        mSecondGroup2 = (MiniHeadsupSecondGroup) findViewById(R.id.second_group_2);

        mFirstGroup.setOnClickListener(this);
        mSecondGroup1.setOnClickListener(this);
        mSecondGroup2.setOnClickListener(this);

        mFirstIcon = ((ImageView) findViewById(R.id.first_icon));
        mFirstText = ((TextView) findViewById(R.id.first_text));

        mSecondIcon1 = ((ImageView) findViewById(R.id.second_icon_1));
        mSecondText1 = ((TextView) findViewById(R.id.second_text_1));

        mSecondIcon2 = ((ImageView) findViewById(R.id.second_icon_2));
        mSecondText2 = ((TextView) findViewById(R.id.second_text_2));
    }

    float mState;

    public void changeState(float state) {
        mState = state;
        requestLayout();
    }

    private void computeRectsDependOnState(int windowWidth, int windowHeight) {
        final float originWeight = 1f - mState;
        final float finalWeight = mState;
        mFirstGroupRect.set((int) (mOriginRectHolder.mFirtGroupMargeLeft * originWeight + mFinalRectHolder.mFirtGroupMargeLeft * finalWeight),
                mWindowRect.top,
                windowWidth - (int) (mOriginRectHolder.mFirtGroupMargeRight * originWeight + mFinalRectHolder.mFirtGroupMargeRight * finalWeight),
                windowHeight);

        mSecondGroupRect1.set((int) (mOriginRectHolder.mSecondGroup1MargeLeft * originWeight + mFinalRectHolder.mSecondGroup1MargeLeft * finalWeight),
                mWindowRect.top,
                windowWidth - (int) (mOriginRectHolder.mSecondGroup1MargeRight * originWeight + mFinalRectHolder.mSecondGroup1MargeRight * finalWeight),
                windowHeight);

        mSecondGroupRect2.set((int) (mOriginRectHolder.mSecondGroup2MargeLeft * originWeight + mFinalRectHolder.mSecondGroup2MargeLeft * finalWeight),
                mWindowRect.top,
                windowWidth - (int) (mOriginRectHolder.mSecondGroup2MargeRight * originWeight + mFinalRectHolder.mSecondGroup2MargeRight * finalWeight),
                windowHeight);
    }


    public int mOriginPadding = 20;
    public int mFinalPadding = 0;
    public int mSecondWidth = 150;

    private void onFirstLayout(int windowWidth) {
        mOriginRectHolder = new GroupRectHolder();
        mOriginRectHolder.mFirtGroupMargeLeft = mOriginPadding;
        mOriginRectHolder.mFirtGroupMargeRight = mOriginPadding;
        mOriginRectHolder.mSecondGroup1MargeLeft = mOriginPadding;
        mOriginRectHolder.mSecondGroup1MargeRight = mOriginPadding;
        mOriginRectHolder.mSecondGroup2MargeLeft = mOriginPadding;
        mOriginRectHolder.mSecondGroup2MargeRight = mOriginPadding;

        mFinalRectHolder = new GroupRectHolder();
        mFinalRectHolder.mFirtGroupMargeLeft = mFinalPadding;
        mFinalRectHolder.mFirtGroupMargeRight = mFinalPadding;
        mFinalRectHolder.mSecondGroup1MargeLeft = mFinalPadding;
        mFinalRectHolder.mSecondGroup1MargeRight = windowWidth - (mFinalPadding + mSecondWidth);
        mFinalRectHolder.mSecondGroup2MargeLeft = windowWidth - (mFinalPadding + mSecondWidth);
        mFinalRectHolder.mSecondGroup2MargeRight = mFinalPadding;
    }

    boolean mDuringChange;
    ValueAnimator mAnimator;

    void animateChageState() {
        if (!mDuringChange) {
            mDuringChange = true;
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            mAnimator = ObjectAnimator.ofFloat(0f, 1f);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                Interpolator it = new AccelerateDecelerateInterpolator();
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (float) animation.getAnimatedValue();
                    changeState(it.getInterpolation(f));
                    mFirstGroup.setAlpha(1 - f);
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFirstGroup.setVisibility(INVISIBLE);
                    mFirstGroup.setAlpha(1f);
                    mDuringChange = false;
                }
            });
            mAnimator.setDuration(300);
            mAnimator.start();
        }
    }

    public void animateShow() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = ObjectAnimator.ofFloat(0f, 1f);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            Interpolator it = new DecelerateInterpolator();
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                setTranslationY(-200f + 200 * it.getInterpolation(f));
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                changeState(0f);
                setAlpha(1f);
                setTranslationY(-200f);
                mFirstGroup.setVisibility(VISIBLE);
            }
        });
        mAnimator.setDuration(300);
        mAnimator.start();
    }

    public void animateHide(final boolean isTarget) {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = ObjectAnimator.ofFloat(0f, 1f);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            Interpolator it = new AccelerateInterpolator();
            float startY = getTranslationY();
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = it.getInterpolation((float) animation.getAnimatedValue());
                if(!isTarget) {
                    setTranslationY(startY +(-200 -startY) * f);
                }else{
                    setAlpha(1-f);
                }
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setTranslationY(0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCallback != null) {
                    mCallback.onHideCallBack();
                }
            }
        });
        mAnimator.setDuration(300);
        mAnimator.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_group:
                animateChageState();
                break;
            case R.id.second_group_1:
                animateHide(true);
                break;
            case R.id.second_group_2:
                animateHide(true);
                break;
        }
    }

    Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    interface Callback {
        void onHideCallBack();
    }

    static class GroupRectHolder {
        int mFirtGroupMargeLeft;
        int mFirtGroupMargeRight;

        int mSecondGroup1MargeLeft;
        int mSecondGroup1MargeRight;

        int mSecondGroup2MargeLeft;
        int mSecondGroup2MargeRight;
    }


    float mDownX,mDownY;
    boolean mScroll;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                mScroll = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                float disY = Math.abs(y - mDownY);
                if(Math.abs(x - mDownX)<Math.abs(y - mDownY)&&disY>24){
                    mScroll = true;
                    mDownY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return mScroll || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                float move = mDownY - y;
                if(move>0){
                    setTranslationY(-move);
                }

                break;
            case MotionEvent.ACTION_UP:
                animateHide(false);
                break;
        }

        return true;
    }
}
