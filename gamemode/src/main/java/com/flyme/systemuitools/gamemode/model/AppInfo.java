package com.flyme.systemuitools.gamemode.model;

import android.content.pm.LauncherActivityInfo;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

public class AppInfo {
    Drawable mIcon;
    CharSequence mLabel;
    LauncherActivityInfo mInfo;
    public ImageView targeView;

    public AppInfo(Drawable icon, CharSequence label, LauncherActivityInfo info, int index) {
        mIcon = icon;
        mLabel = label;
        mInfo = info;
    }

    public AppInfo(AppInfo info) {
        update(info);
    }

    public void update(AppInfo info) {
        mIcon = info.mIcon;
        mLabel = info.mLabel;
        mInfo = info.mInfo;
    }

    public boolean isEmpty() {
        return mInfo == null;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public CharSequence getLabel() {
        return mLabel;
    }

    public LauncherActivityInfo getInfo() {
        return mInfo;
    }
}

