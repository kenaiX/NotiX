package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyme.systemuitools.R;
import com.flyme.systemuitools.gamemode.events.ConfigChangeEvents;
import com.flyme.systemuitools.gamemode.events.DragEvents;
import com.flyme.systemuitools.gamemode.events.UIEvents;
import com.flyme.systemuitools.gamemode.utils.DragHelper;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

public class StageView extends RelativeLayout {
    public StageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), getResources().getDimensionPixelOffset(R.dimen.game_mode_proview_outline_radius));
            }
        });
        setClipToOutline(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        RxBus.get().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        RxBus.get().unregister(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        DragHelper.getInstance().init(this);
    }

    @Subscribe
    public void onStartDrag(DragEvents.StartDrag event) {
        DragHelper.getInstance().startDrag(event.dragView, event.dragInfo, event.parent, mLastX, mLastY);
    }

    float mLastX, mLastY;

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility ==VISIBLE){
            DragHelper.getInstance().reset();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("@@@@", "onInterceptTouchEvent " + ev.getAction());
        if (DragHelper.getInstance().idDraging()) {
            Log.e("@@@@", "enter drag");
            DragHelper.getInstance().onTouchEvent(ev);
            return true;
        }
        mLastX = ev.getX();
        mLastY = ev.getY();
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e("@@@@", "onTouchEvent " + ev.getAction());

        if (DragHelper.getInstance().idDraging()) {
            return DragHelper.getInstance().onTouchEvent(ev);
        }

        return super.onTouchEvent(ev);

    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RxBus.get().post(new ConfigChangeEvents.PhoneConfigChange());
    }

    private int mWidth, mHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            float x = ev.getX();
            float y = ev.getY();
            if (x < 0 || x > mWidth || y < 0 || y > mHeight) {
                RxBus.get().post(new UIEvents.CloseProView());
                return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        RxBus.get().post(new UIEvents.CloseProView());

        return super.dispatchKeyEvent(event);
    }
}
