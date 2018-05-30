package com.flyme.systemui.miniheadsup;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.flyme.systemui.dynamic.R;


public class MiniHeadsupService extends Service {
    static MiniHeadsupService mService;
    Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        mMainViewGroup = (MiniHeadsupWindow) View.inflate(this, R.layout.mini_headsup_window,null);
        mMainViewGroup.setCallback(new MiniHeadsupWindow.Callback() {
            @Override
            public void onHideCallBack() {
                hideStage();
            }
        });
        addStage();
        hideStage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
        handler.removeCallbacks(hideMessage);
        removeStage();
        mMainViewGroup = null;
    }

    Runnable hideMessage = new Runnable() {
        @Override
        public void run() {
            //hideStage();
        }
    };

    public static void newNotification(){
        if(mService!=null){
            mService.onNewNotification();
        }
    }

    public void onNewNotification(){
        handler.removeCallbacks(hideMessage);
        handler.postDelayed(hideMessage,5000);
        showStage();
        mMainViewGroup.setFirstGroupColor(Color.rgb(50,180,230));
        mMainViewGroup.setSecondGroupColor(Color.BLACK,Color.rgb(50,180,230));
        mMainViewGroup.setFirstGroupValue(getDrawable(R.drawable.btn_answer_headsup_normal),"18575635422");
        mMainViewGroup.setSecondGroupValue(getDrawable(R.drawable.btn_decline_headsup_normal),
                getDrawable(R.drawable.btn_answer_headsup_normal));

        mMainViewGroup.animateShow();
    }




    MiniHeadsupWindow mMainViewGroup;
    private final void addStage() {
        if (!mMainViewGroup.isShown()) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            layoutParams.setTitle("RecentsWindow");
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = 400;
            layoutParams.height = 200;
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;
            ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).
                    addView(mMainViewGroup, layoutParams);

        }
    }

    private final void removeStage() {
        if (mMainViewGroup != null && mMainViewGroup.isShown()) {
            ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).removeView(mMainViewGroup);
        }
    }

    private final void showStage(){
        mMainViewGroup.setVisibility(View.VISIBLE);
    }

    private final void hideStage(){
        mMainViewGroup.setVisibility(View.INVISIBLE);
    }
}
