package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.flyme.systemuitools.R;
import com.flyme.systemuitools.aod.view.AODBatteryView;

public class BatteryIconView extends AODBatteryView {
    public BatteryIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setScaleX(0.9f);
        setScaleY(0.9f);
    }


    @Override
    public void refreshResource() {
        super.refreshResource();
        mBatteryNormal = getResources().getDrawable(
                R.drawable.gamemode_battery_normal, null);
        mBatteryCharge = getResources().getDrawable(
                R.drawable.gamemode_battery_charge, null);
        mBatteryPlugged = getResources().getDrawable(
                R.drawable.gamemode_battery_plugged, null);
        mBatteryLow = getResources().getDrawable(R.drawable.gamemode_battery_low,
                null);

        mBatteryNormalCore = ((LayerDrawable) mBatteryNormal)
                .findDrawableByLayerId(R.id.battery_core);
        mBatteryPluggedCore = ((LayerDrawable) mBatteryPlugged)
                .findDrawableByLayerId(R.id.battery_core);
        apply();
    }
}
