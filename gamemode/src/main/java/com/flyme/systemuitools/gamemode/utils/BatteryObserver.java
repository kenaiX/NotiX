package com.flyme.systemuitools.gamemode.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.flyme.systemuitools.aod.model.BatteryStatus;

public abstract class BatteryObserver {

    private Context mContext;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryManager bm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            int level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//            int status = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            onBatteryChange(new BatteryStatus(status, level, plugged, health), level, status == BatteryManager.BATTERY_STATUS_CHARGING ? "充电中" : "未充电", status == BatteryManager.BATTERY_STATUS_CHARGING);
        }
    };

    public abstract void onBatteryChange(BatteryStatus status, int level, String time, boolean isCharging);

    public void init(Context context) {
        mContext = context.getApplicationContext();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        /*filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);*/
        mContext.registerReceiver(mReceiver, filter);
    }

    public void reset() {
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContext = null;
    }
}
