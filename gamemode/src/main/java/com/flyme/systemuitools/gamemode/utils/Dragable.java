package com.flyme.systemuitools.gamemode.utils;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.flyme.systemuitools.gamemode.model.AppInfo;

public interface Dragable {
    Rect canAccept();

    boolean onAcceptedCompleted(MotionEvent event, AppInfo info);

    void onRemovedCompleted(View originView);

    void onDragOver(MotionEvent event);

    void onDragEnter();

    void onDragExit();
}
