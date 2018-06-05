package com.flyme.systemuitools.aod.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.util.Log;

import com.flyme.systemuitools.aod.model.BatteryStatus;

import android.view.View;

public class AODBatteryView extends View {
    private static final String TAG = "AODBatteryView";
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    private static int LOW_LEVEL = 8;
    private int mLastLevel = -1;
    private boolean mLastPlugged;
    private boolean mCharging;
    private Drawable mDrawable;
    protected Drawable mBatteryNormal;
    protected Drawable mBatteryCharge;
    protected Drawable mBatteryLow;
    protected Drawable mBatteryNormalCore;
    protected Drawable mBatteryPlugged;
    protected Drawable mBatteryPluggedCore;

    public AODBatteryView(Context context) {
        super(context);
        initBattery();
    }

    private void initBattery() {
        refreshResource();
    }

    public void updateBatteryInfo(BatteryStatus status) {
        Log.d(TAG, "updateBatteryInfo level= " + status.level + ", pluggedIn="
                + status.plugged + ", status=" + status.status);
        boolean charging = status.status == BatteryManager.BATTERY_STATUS_CHARGING
                && !status.isCharged();
        if (status.level != mLastLevel || mLastPlugged != status.isPluggedIn()
                || mCharging != charging) {
            mLastLevel = status.level;
            mLastPlugged = status.isPluggedIn();
            mCharging = charging;
            apply();
        }
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mDrawableWidth = mDrawable.getIntrinsicWidth();
        int mDrawableHeight = mDrawable.getIntrinsicHeight();
        int widthSize = MeasureSpec.getSize(mDrawableWidth);
        int heightSize = MeasureSpec.getSize(mDrawableHeight);
        this.setMeasuredDimension(widthSize, heightSize);
    }

    protected void apply() {
        Drawable drawable = mBatteryNormal;
        if (mLastLevel >= 0 || mCharging) {
            if (mCharging) {
                drawable = mBatteryCharge;
            } else {
                if (mLastLevel < BatteryStatus.LOW_BATTERY_THRESHOLD) {
                    drawable = mBatteryLow;
                } else {
                    if (mLastPlugged) {
                        drawable = mBatteryPlugged;
                    }
                }
            }
        }
        if (drawable != mDrawable) {
            mDrawable = drawable;
            requestLayout();
        } else {
            postInvalidate();
        }
    }

    @Override
    public void draw(Canvas c) {
        mDrawable.setAlpha(150);
        mDrawable.setBounds(0, 0, c.getWidth(), c.getHeight());
        mDrawable.setLevel(mLastLevel * 100);
        mDrawable.draw(c);
    }

    public AODBatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBattery();
    }

    public AODBatteryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBattery();
    }

    public void refreshResource() {

    }
}
