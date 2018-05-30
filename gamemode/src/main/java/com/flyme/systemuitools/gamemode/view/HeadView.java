package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyme.systemuitools.R;
import com.flyme.systemuitools.gamemode.model.AppInfo;
import com.flyme.systemuitools.gamemode.utils.Dragable;

public class HeadView extends RelativeLayout implements Dragable {
    public HeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    QuickAppsView mQuickAppsView;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mQuickAppsView = (QuickAppsView) findViewById(R.id.quckAppsFrame);

    }

    @Override
    public Rect canAccept() {
        return mQuickAppsView.canAccept();
    }

    @Override
    public boolean onAcceptedCompleted(MotionEvent event, AppInfo info) {
        return mQuickAppsView.onAcceptedCompleted(event, info);
    }

    @Override
    public void onRemovedCompleted(View originView) {
        mQuickAppsView.onRemovedCompleted(originView);
    }
}
