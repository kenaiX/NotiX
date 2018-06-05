package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.graphics.Color;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyme.systemuitools.R;
import com.flyme.systemuitools.aod.model.BatteryStatus;
import com.flyme.systemuitools.aod.view.AODBatteryView;
import com.flyme.systemuitools.gamemode.utils.BatteryObserver;

public class BatteryView extends RelativeLayout {
    private AODBatteryView mBatteryIcon;
    private TextView mBatteryLevel;
    private TextView mBaterrySummary;
    private BatteryObserver mBatteryObserver;

    public BatteryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BatteryView(Context context) {
        super(context);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility == VISIBLE) {
            mBatteryObserver.init(getContext());
        } else {
            mBatteryObserver.reset();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBatteryIcon = (AODBatteryView) findViewById(R.id.batteryIcon);
        mBatteryLevel = (TextView) findViewById(R.id.batteryLevel);
        mBaterrySummary = (TextView) findViewById(R.id.baterrySummary);

        mBatteryObserver = new BatteryObserver() {
            @Override
            public void onBatteryChange(BatteryStatus status,int level, String time, boolean isCharging) {
                mBatteryLevel.setText(level + "%");
                if (level < 10) {
                    mBaterrySummary.setText(time);
                    mBaterrySummary.setVisibility(VISIBLE);
                } else {
                    mBaterrySummary.setText(null);
                    mBaterrySummary.setVisibility(GONE);
                }
                mBatteryIcon.updateBatteryInfo(status);
            }
        };
    }
}
