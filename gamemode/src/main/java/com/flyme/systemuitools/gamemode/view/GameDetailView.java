package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.flyme.systemuitools.R;
import com.flyme.systemuitools.gamemode.events.GameEvents;
import com.flyme.systemuitools.gamemode.events.UIEvents;
import com.flyme.systemuitools.gamemode.utils.GameSdkUtil;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

public class GameDetailView extends FrameLayout {
    private String mGamePkg;
    private ViewGroup mSubView;

    public GameDetailView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void showGame(String pkg) {
        mGamePkg = pkg;
        RxBus.get().register(this);
        if (GameSdkUtil.isInstalled(getContext())) {
            removeAllViews();
            if (GameSdkUtil.isPluginInstalled()) { //先判断插件有没有安装
                GameSdkUtil.showGameView(this, mSubView, pkg);
            } else {  //没有安装则需要调用安装,等待收到 onInstallPluginSucceed （Application） 事件后，重新加载福利的界面
                GameSdkUtil.preparePluginIfneed(getContext());
            }
        } else {
            View noGameSDKSummary = findViewById(R.id.summary_no_game_sdk);
            noGameSDKSummary.setVisibility(VISIBLE);
            noGameSDKSummary.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    RxBus.get().post(new UIEvents.CloseProView());
                    GameSdkUtil.restoreGameSDK(getContext());
                }
            });
        }
    }

    public void setGameSubView(ViewGroup viewGroup) {
        mSubView = viewGroup;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        RxBus.get().unregister(this);
    }

    @Subscribe
    public void onPluginInstalledChange(GameEvents.PluginInstalled event) {
        if (event.succeed) {
            GameSdkUtil.showGameView(this, mSubView, mGamePkg);
        } else {
//            Toast.makeText(getContext(), "插件安装错误", Toast.LENGTH_SHORT).show();
        }
    }
}
