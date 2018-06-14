package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
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
    private Callback mCallback;

    public BatteryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BatteryView(Context context) {
        super(context);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
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
        mBatteryIcon = (AODBatteryView) findViewById(R.id.gamemode_battery_icon);
        mBatteryLevel = (TextView) findViewById(R.id.gamemode_battery_level);
        mBaterrySummary = (TextView) findViewById(R.id.gamemode_baterry_summary);

        mBatteryObserver = new BatteryObserver() {
            @Override
            public void onBatteryChange(BatteryStatus status, int level, String time, boolean isCharging) {
                mBatteryLevel.setText(level + "%");

                String timeRemaining = null;
                if (level < 10) {
                    if (mCallback != null) {
                        timeRemaining = mCallback.computeBatteryTimeRemaining();
                    }
                }
                if (timeRemaining != null) {
                    mBaterrySummary.setText(getResources().getString(R.string.game_mode_pro_time_remaining) + timeRemaining);
                    mBaterrySummary.setVisibility(VISIBLE);
                } else {
                    mBaterrySummary.setVisibility(GONE);
                }


                mBatteryIcon.updateBatteryInfo(status);
            }
        };
    }

    public interface Callback {
        String computeBatteryTimeRemaining();
    }
}
