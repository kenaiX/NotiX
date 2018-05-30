package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyme.systemuitools.R;
import com.flyme.systemuitools.gamemode.ProViewControler;
import com.flyme.systemuitools.gamemode.events.ClickEvents;
import com.flyme.systemuitools.gamemode.events.ConfigChangeEvents;
import com.flyme.systemuitools.gamemode.events.DragEvents;
import com.flyme.systemuitools.gamemode.model.AppInfo;
import com.flyme.systemuitools.gamemode.utils.Dragable;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

import java.util.List;

public class QuickAppsView extends LinearLayout implements View.OnClickListener, View.OnLongClickListener, Dragable {
    public QuickAppsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    ImageView[] quickItems = new ImageView[4];
    AppInfo[] mApps = new AppInfo[4];
    boolean isLand = false;
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
        quickItems[0] = (ImageView) findViewById(R.id.quick1);
        quickItems[1] = (ImageView) findViewById(R.id.quick2);
        quickItems[2] = (ImageView) findViewById(R.id.quick3);
        quickItems[3] = (ImageView) findViewById(R.id.quick4);

        for (int i = 0; i < 4; i++) {
            quickItems[i].setId(i);
            quickItems[i].setOnClickListener(this);
            quickItems[i].setOnLongClickListener(this);
        }

        if(ProViewControler.sLand){
            int margin = getResources().getDimensionPixelSize(R.dimen.game_mode_proview_apps_quick_margin_land);
            for (int i = 0; i < 4; i++) {
                LinearLayout.LayoutParams layoutParams = (LayoutParams) quickItems[i].getLayoutParams();
                layoutParams.setMarginEnd(margin);
            }
        }else{
            int margin = getResources().getDimensionPixelSize(R.dimen.game_mode_proview_apps_quick_margin);
            for (int i = 0; i < 4; i++) {
                LinearLayout.LayoutParams layoutParams = (LayoutParams) quickItems[i].getLayoutParams();
                layoutParams.setMarginEnd(margin);
            }
        }
    }

    public void bindApps(List<AppInfo> list) {
        for (int i = 0; i < 4 ; i++) {
            if(list.size() > i) {
                mApps[i] = list.get(i);
            }else{
                mApps[i] = null;
            }
        }
        for (int i = 0; i < 4; i++) {
            quickItems[i].setImageDrawable(null);
            if (mApps[i] != null) {
                quickItems[i].setImageDrawable(mApps[i].getIcon());
                quickItems[i].setTag(mApps[i]);
            } else {
                quickItems[i].setImageDrawable(null);
                quickItems[i].setTag(null);
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
        for (ImageView child : quickItems) {
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
        for (int i = 0; i < 4; i++) {
            ImageView child = quickItems[i];
            if (child.getTag() == null) {
                AppInfo childInfo = new AppInfo(info);
                child.setTag(childInfo);
                mApps[i] = childInfo;
                child.setImageDrawable(childInfo.getIcon());
                RxBus.get().post(new ConfigChangeEvents.QuickAppsConfigChange(mApps));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRemovedCompleted(View originView) {
        RxBus.get().post(new ConfigChangeEvents.QuickAppsConfigChange(mApps));
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
        for (int i = 0; i < 4; i++) {
            if (mApps[i] == null) {
                quickItems[i].setImageResource(R.drawable.game_mode_apps_blank);
            }
        }
    }

    @Subscribe
    public void onStopDrag(DragEvents.StopDragCompleted event) {
        for (int i = 0; i < 4; i++) {
            if (mApps[i] == null) {
                quickItems[i].setImageDrawable(null);
            }
        }
    }
}
