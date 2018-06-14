package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyme.systemuitools.R;
import com.flyme.systemuitools.gamemode.events.ClickEvents;
import com.flyme.systemuitools.gamemode.events.ConfigChangeEvents;
import com.flyme.systemuitools.gamemode.events.DragEvents;
import com.flyme.systemuitools.gamemode.model.AppInfo;
import com.flyme.systemuitools.gamemode.utils.Dragable;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

import java.util.List;

public class QuickAppsView extends LinearLayout implements View.OnClickListener, View.OnLongClickListener, Dragable {
    public static final int NUM_APPS = 3;

    ImageView[] mQuickItems = new ImageView[3];
    AppInfo[] mApps = new AppInfo[3];
    int mDragPosition = -1;

    public QuickAppsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mQuickItems[0] = (ImageView) findViewById(R.id.gamemode_quick_app_1);
        mQuickItems[1] = (ImageView) findViewById(R.id.gamemode_quick_app_2);
        mQuickItems[2] = (ImageView) findViewById(R.id.gamemode_quick_app_3);

        for (int i = 0; i < NUM_APPS; i++) {
            mQuickItems[i].setId(i);
            mQuickItems[i].setOnClickListener(this);
            mQuickItems[i].setOnLongClickListener(this);
        }
    }

    public void bindApps(List<AppInfo> list) {
        for (int i = 0; i < NUM_APPS; i++) {
            if (list.size() > i) {
                mApps[i] = list.get(i);
            } else {
                mApps[i] = null;
            }
        }
        for (int i = 0; i < NUM_APPS; i++) {
            mQuickItems[i].setImageDrawable(null);
            if (mApps[i] != null) {
                mQuickItems[i].setImageDrawable(mApps[i].getIcon());
                mQuickItems[i].setTag(mApps[i]);
            } else {
                mQuickItems[i].setImageDrawable(null);
                mQuickItems[i].setTag(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            RxBus.get().post(new ClickEvents.OnAppsClick(mApps[v.getId()]));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getTag() != null) {
            RxBus.get().post(new DragEvents.StartDrag(v, (AppInfo) v.getTag(), (Dragable) QuickAppsView.this.getParent()));
            v.setTag(null);
            ((ImageView) v).setImageDrawable(null);
            mApps[v.getId()] = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Rect canAccept() {
        for (ImageView child : mQuickItems) {
            if (child.getTag() == null) {
                int[] position = new int[2];
                child.getLocationInWindow(position);
                return new Rect(position[0], position[1], position[0] + child.getWidth(), position[1] + child.getHeight());
            }
        }
        return null;
    }

    @Override
    public boolean onAcceptedCompleted(MotionEvent event, AppInfo info) {
        for (int i = 0; i < NUM_APPS; i++) {
            ImageView child = mQuickItems[i];
            if (child.getTag() == null) {
                AppInfo childInfo = new AppInfo(info);
                child.setTag(childInfo);
                mApps[i] = childInfo;
                child.setImageDrawable(childInfo.getIcon());
                RxBus.get().post(new ConfigChangeEvents.OnSaveQuickAppsConfig(mApps));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRemovedCompleted(View originView) {
        AppInfo[] temp = new AppInfo[NUM_APPS];
        int index = 0;
        for (int i = 0; i < NUM_APPS; i++) {
            ImageView child = mQuickItems[i];
            if (child.getTag() != null) {
                temp[index++] = (AppInfo) child.getTag();
            }
        }
        for (int i = 0; i < NUM_APPS; i++) {
            ImageView child = mQuickItems[i];
            AppInfo tag = (AppInfo) child.getTag();
            if (temp[i] != null) {
                if (tag != temp[i]) {
                    AppInfo childInfo = new AppInfo(temp[i]);
                    child.setTag(childInfo);
                    mApps[i] = childInfo;
                    child.setImageDrawable(childInfo.getIcon());
                }
            } else {
                child.setTag(null);
                mApps[i] = null;
                child.setImageDrawable(null);
            }
        }

        RxBus.get().post(new ConfigChangeEvents.OnSaveQuickAppsConfig(mApps));
    }

    private void fitPotion(int position) {
        AppInfo[] cacheApps = mApps.clone();
        AppInfo[] temp = new AppInfo[NUM_APPS];
        int index = 0;
        for (int i = 0; i < NUM_APPS; i++) {
            ImageView child = mQuickItems[i];
            if (child.getTag() != null) {
                temp[index++] = (AppInfo) child.getTag();
            }
        }
        index = 0;
        for (int i = 0; i < NUM_APPS; i++) {
            ImageView child = mQuickItems[i];
            if (i == position) {
                child.setTag(null);
                mApps[i] = null;
                child.setImageResource(R.drawable.gamemode_apps_blank);
            } else {
                AppInfo childInfo = temp[index];
                if (childInfo != null) {
                    child.setTag(childInfo);
                    mApps[i] = childInfo;
                    child.setImageDrawable(childInfo.getIcon());
                    index++;
                } else {
                    child.setTag(null);
                    mApps[i] = null;
                    child.setImageResource(R.drawable.gamemode_apps_blank);
                }
            }
        }
        //做动画
        for (int i = 0; i < NUM_APPS; i++) {
            Object tag = mQuickItems[i].getTag();
            if (tag != null) {
                for (int j = 0; j < NUM_APPS; j++) {
                    if (tag == cacheApps[j] && i != j) {
                        int offset = i - j;
                        mQuickItems[i].setTranslationX(offset * mQuickItems[i].getWidth());
                        mQuickItems[i].animate().setDuration(150).translationX(0);
                    }
                }
            }
        }
    }

    @Override
    public void onDragOver(MotionEvent event) {
        if (existPosition()) {
            int position = findNearestArea(event);
            if (mDragPosition == position) {
                return;
            }
            mDragPosition = position;
            fitPotion(position);
        }
    }

    @Override
    public void onDragEnter() {
        mDragPosition = -1;
    }

    @Override
    public void onDragExit() {
        mDragPosition = -1;
        fitPotion(-1);
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

    @Subscribe
    public void onStartDrag(DragEvents.StartDrag event) {
        for (int i = 0; i < NUM_APPS; i++) {
            if (mApps[i] == null) {
                mQuickItems[i].setImageResource(R.drawable.gamemode_apps_blank);
            }
        }
    }

    @Subscribe
    public void onStopDrag(DragEvents.StopDragCompleted event) {
        for (int i = 0; i < NUM_APPS; i++) {
            if (mApps[i] == null) {
                mQuickItems[i].setImageDrawable(null);
            }
        }
    }

    private boolean existPosition() {
        for (int i = 0; i < NUM_APPS; i++) {
            if (mApps[i] == null) {
                return true;
            }
        }
        return false;
    }

    //返回挤动的空位置
    private int findNearestArea(MotionEvent event) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        int x = (int) (event.getRawX() - location[0]);
        int y = (int) (event.getRawY() - location[1]);

        for (int i = 0; i < NUM_APPS; i++) {
            View child = mQuickItems[i];
            if (x > child.getLeft() && x <= child.getRight() && y > child.getTop() && y <= child.getBottom()) {
                return i;
            }
        }
        return -1;
    }
}
