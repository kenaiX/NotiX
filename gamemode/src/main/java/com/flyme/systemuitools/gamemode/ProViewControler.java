package com.flyme.systemuitools.gamemode;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyme.systemuitools.R;
import com.flyme.systemuitools.gameassistant.view.NotificationListView;
import com.flyme.systemuitools.gamemode.events.ClickEvents;
import com.flyme.systemuitools.gamemode.events.ConfigChangeEvents;
import com.flyme.systemuitools.gamemode.events.DragEvents;
import com.flyme.systemuitools.gamemode.events.UIEvents;
import com.flyme.systemuitools.gamemode.model.AppInfo;
import com.flyme.systemuitools.gamemode.view.GameDetailView;
import com.flyme.systemuitools.gamemode.view.QuickAppsView;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.meizu.flyme.launcher.IExternalService;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProViewControler implements View.OnClickListener {
    final int DETAIL_VIEW_TYPE_NOTI = 0;
    final int DETAIL_VIEW_TYPE_GAME = 1;
    final int DETAIL_VIEW_TYPE_SETTINGS = 2;


    final int DETAIL_STATE_TYPE_NOTI = 0;
    final int DETAIL_STATE_TYPE_GAME = 1;
    final int DETAIL_STATE_TYPE_SETTINGS = 2;

    private final RelativeLayout mHeadFrame;
    private final View mTabFrame;
    private final ImageView mPanda;
    private final com.flyme.systemuitools.gamemode.view.BatteryView mBatteryFrame;
    private final ImageView mMoreAppsMenu;
    private final com.flyme.systemuitools.gamemode.view.MoreAppsView mMoreAppsFrame;
    private final TextView mTabNoti;
    private final TextView mTabGame;
    private final TextView mTabSettings;
    private final FrameLayout mDetailFrame;
    private final QuickAppsView mQuckAppsFrame;

    private final SharedPreferences mSharedPreferences;

    List<LauncherActivityInfo> mAllApps;

    private final Context mContext;

    private int mDetailState = -1;

    public static boolean sLand = false;

    public interface CallBack {
        List<AppInfo> loadAllApps(List<QuickAppsRecord> filter);

        AppInfo loadApps(String pkg, String name, int userid);

        void startApps(AppInfo info);

        List<StatusBarNotification> getNotifications();

        String getGamePkg();

        void onShowChange(boolean isShow);
    }

    private final CallBack mCallBack;

    public View getProView() {
        return mProView;
    }

    public void bindBatteryService() {
        UIEvents.BatteryChange event = new UIEvents.BatteryChange();
        event.leave = 1;
        event.sumary = "test";
        RxBus.get().post(event);
    }

    public void onStart() {
        /*RxBus.get().register(this);
        bindBatteryService();*/
    }

    public void onStop() {
        /*RxBus.get().unregister(this);
        unbindBatteryService();*/
    }

    @Subscribe
    public void onBatteryChange(UIEvents.BatteryChange event) {
    }

    @Subscribe
    public void onStartDrag(DragEvents.StartDrag event) {
        toggleMoreApps(true,false);
    }

    @Subscribe
    public void onStartApp(ClickEvents.OnAppsClick event) {
        toggleProViewShow(false);
        mCallBack.startApps(event.info);
    }

    @Subscribe
    public void onConfigChange(ConfigChangeEvents.PhoneConfigChange event) {
        toggleProViewShow(false);
    }

    @Subscribe
    public void onEvent(UIEvents.CloseProView event) {
        toggleProViewShow(false);
    }

    @Subscribe
    public void onSaveQuickAppsConfig(ConfigChangeEvents.QuickAppsConfigChange event) {
        StringBuilder sb = new StringBuilder();
        quickAppsList.clear();
        for (AppInfo info : event.list) {
            if (info != null) {
                sb.append(QuickAppsRecord.AppInfo2String(info)).append(";");
                quickAppsList.add(QuickAppsRecord.fromAppInfo(info));
            }
        }
        mSharedPreferences.edit().putString("quick_apps_list", sb.toString()).apply();
    }

    public void unbindBatteryService() {

    }

    private final View mProView;
    List<QuickAppsRecord> quickAppsList;

    private Map<String, Integer> mAppsUsedRecord = new HashMap<>();

    public ProViewControler(Context context, CallBack callBack) {
        mCallBack = callBack;
        mContext = context;

        mSharedPreferences = mContext.getSharedPreferences("game_mode", Context.MODE_PRIVATE);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        if (dm.widthPixels < dm.heightPixels) {
            sLand = false;
        }else{
            sLand = true;
        }

        mProView = View.inflate(context, R.layout.game_pro_main_land, null);

        mHeadFrame = (RelativeLayout) mProView.findViewById(R.id.headFrame);
        mPanda = (ImageView) mProView.findViewById(R.id.panda);
        mBatteryFrame = (com.flyme.systemuitools.gamemode.view.BatteryView) mProView.findViewById(R.id.batteryFrame);
        mMoreAppsMenu = (ImageView) mProView.findViewById(R.id.moreAppsMenu);
        mQuckAppsFrame = (com.flyme.systemuitools.gamemode.view.QuickAppsView) mProView.findViewById(R.id.quckAppsFrame);

        mTabNoti = (TextView) mProView.findViewById(R.id.tabNoti);
        mTabGame = (TextView) mProView.findViewById(R.id.tabGame);
        mTabSettings = (TextView) mProView.findViewById(R.id.tabSettings);

        //用来放置详情页
        mDetailFrame = (FrameLayout) mProView.findViewById(R.id.detailFrame);
        //用来放置应用列表
        mMoreAppsFrame = (com.flyme.systemuitools.gamemode.view.MoreAppsView) mProView.findViewById(R.id.moreAppsFrame);
        //用来放置Tab
        mTabFrame = mProView.findViewById(R.id.tabFrame);

        mMoreAppsMenu.setOnClickListener(this);
        mTabNoti.setOnClickListener(this);
        mTabGame.setOnClickListener(this);
        mTabSettings.setOnClickListener(this);

        Intent it = new Intent();
        it.setComponent(new ComponentName("com.meizu.flyme.launcher", "com.meizu.flyme.launcher.ExternalService"));
        mContext.bindService(it, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IExternalService ss = IExternalService.Stub.asInterface(service);
                try {
                    String appsUsedRecord = ss.getAppsUsedRecord();
                    String[] pkgAndTimes = appsUsedRecord.split(";");
                    for (String temp : pkgAndTimes) {
                        if (!TextUtils.isEmpty(temp)) {
                            String[] temp2 = temp.split(":");
                            mAppsUsedRecord.put(temp2[0], Integer.valueOf(temp2[1]));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Service.BIND_AUTO_CREATE);
    }

    public static class QuickAppsRecord {
        public ComponentName componentName;
        public int userId;

        public QuickAppsRecord(ComponentName componentName, int userId) {
            this.componentName = componentName;
            this.userId = userId;
        }

        public static QuickAppsRecord fromString(String s) {
            String[] temp = s.split(":");
            String[] temp2 = temp[0].split("/");
            return new QuickAppsRecord(new ComponentName(temp2[0], temp2[1]), Integer.parseInt(temp[1]));
        }

        public static QuickAppsRecord fromAppInfo(AppInfo info) {
            ComponentName componentName = info.getInfo().getComponentName();
            return new QuickAppsRecord(componentName, 0);
        }

        @Override
        public String toString() {
            return componentName.getPackageName() + "/" + componentName.getClassName() + ":" + userId;
        }

        static String AppInfo2String(AppInfo info) {
            ComponentName componentName = info.getInfo().getComponentName();
            return componentName.getPackageName() + "/" + componentName.getClassName() + ":0";
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.moreAppsMenu) {
            toggleMoreApps(!showMoreAppsFlag,true);
            return;
        }

        View replace = null;
        switch (id) {
            case R.id.tabNoti:
                if (mDetailState == DETAIL_STATE_TYPE_NOTI) {
                    return;
                }
                mDetailState = DETAIL_STATE_TYPE_NOTI;
                replace = buildDetailView(mContext, DETAIL_VIEW_TYPE_NOTI);
                NotificationListView notificationListView = (NotificationListView) replace;
                notificationListView.updateNotifications(mCallBack.getNotifications());
                break;
            case R.id.tabGame:
                if (mDetailState == DETAIL_STATE_TYPE_GAME) {
                    return;
                }
                mDetailState = DETAIL_STATE_TYPE_GAME;
                replace = buildDetailView(mContext, DETAIL_VIEW_TYPE_GAME);
                GameDetailView gameDetailView = (GameDetailView) replace;
                gameDetailView.showGame(mCallBack.getGamePkg());
                break;
            case R.id.tabSettings:
                if (mDetailState == DETAIL_STATE_TYPE_SETTINGS) {
                    return;
                }
                mDetailState = DETAIL_STATE_TYPE_SETTINGS;
                replace = buildDetailView(mContext, DETAIL_VIEW_TYPE_SETTINGS);
                break;
        }
        if (replace != null) {

            updateTabColor(id);

            mDetailFrame.removeAllViews();
            mDetailFrame.addView(replace);
        }
    }

    public void updateTabColor(int id) {
        mTabNoti.setBackgroundColor(Color.TRANSPARENT);
        mTabGame.setBackgroundColor(Color.TRANSPARENT);
        mTabSettings.setBackgroundColor(Color.TRANSPARENT);
        switch (id) {
            case R.id.tabNoti:
                mTabNoti.setBackgroundColor(Color.WHITE);
                break;
            case R.id.tabGame:
                mTabGame.setBackgroundColor(Color.WHITE);
                break;
            case R.id.tabSettings:
                mTabSettings.setBackgroundColor(Color.WHITE);
                break;
        }
    }

    private boolean showMoreAppsFlag = false;

    private void toggleMoreApps(boolean show,boolean needSummary) {
        if (showMoreAppsFlag == show) {
            if(show){
                mMoreAppsFrame.setSummary(false);
            }
            return;
        } else {
            showMoreAppsFlag = show;
        }
        if (show) {
            List<AppInfo> appInfos = mCallBack.loadAllApps(quickAppsList);
            sortAllApps(appInfos);
            mMoreAppsFrame.init(appInfos, isLand ? 6 : 4);
            mMoreAppsFrame.setVisibility(View.VISIBLE);
            mMoreAppsFrame.setSummary(needSummary);
            mTabFrame.setVisibility(View.INVISIBLE);
            mDetailFrame.setVisibility(View.INVISIBLE);
            mMoreAppsMenu.setImageResource(R.drawable.game_mode_more_apps_menu_collapse);
        } else {
            mMoreAppsFrame.setVisibility(View.INVISIBLE);
            mTabFrame.setVisibility(View.VISIBLE);
            mDetailFrame.setVisibility(View.VISIBLE);
            mMoreAppsMenu.setImageResource(R.drawable.game_mode_more_apps_menu);
        }
    }

    void  sortAllApps(List<AppInfo> list){
        final Collator collator = Collator.getInstance(Locale.CHINESE);
        Collections.sort(list, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                Integer times1 = mAppsUsedRecord.get(o1.getInfo().getComponentName().getPackageName());
                Integer times2 = mAppsUsedRecord.get(o2.getInfo().getComponentName().getPackageName());
                if (times1 == null && times2 == null) {
                    return collator.getCollationKey(o1.getLabel().toString()).compareTo(collator.getCollationKey(o2.getLabel().toString()));
                } else if (times1 == null) {
                    return 1;
                } else if (times2 == null) {
                    return -1;
                } else {
                    return times2 - times1;
                }
            }
        });
    }

    private @Nullable
    View buildDetailView(Context context, int type) {
        View v = null;
        switch (type) {
            case DETAIL_VIEW_TYPE_NOTI:
                v = View.inflate(context, R.layout.game_pro_detail_noti, null);
                break;
            case DETAIL_VIEW_TYPE_GAME:
                v = View.inflate(context, R.layout.game_pro_detail_game, null);
                break;
            case DETAIL_VIEW_TYPE_SETTINGS:
                v = View.inflate(context, R.layout.game_pro_detail_settings, null);
                break;
        }
        return v;
    }

    private boolean isShow = false;

    public boolean isShow() {
        return isShow;
    }

    private boolean isLand = false;

    private void prepareShow() {
        mDetailState = -1;

        if(mCallBack.getNotifications().size()!=0) {
            onClick(mTabNoti);//mDetailState = noti
        }else{
            onClick(mTabGame);//mDetailState = game
        }

        toggleMoreApps(false,false);

        //绑定quick—apps，todo区分用户
        List<AppInfo> list = new ArrayList<>();
        quickAppsList = new ArrayList<>();
        String saveString = mSharedPreferences.getString("quick_apps_list", null);
        if (saveString == null) {
            quickAppsList.add(new QuickAppsRecord(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"), 0));
            quickAppsList.add(new QuickAppsRecord(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity"), 0));
        } else {
            String[] split = saveString.split(";");
            for (String s : split) {
                if (!TextUtils.isEmpty(s)) {
                    quickAppsList.add(QuickAppsRecord.fromString(s));
                }
            }
        }
        for (QuickAppsRecord record : quickAppsList) {
            AppInfo test = mCallBack.loadApps(record.componentName.getPackageName(), record.componentName.getClassName(), record.userId);
            if (test != null) {
                list.add(test);
            }
        }
        mQuckAppsFrame.bindApps(list);
    }

    public void toggleProViewShow(boolean show) {
        if (show == isShow) {
            return;
        }
        isShow = show;
        if (show) {
            RxBus.get().register(this);
            bindBatteryService();


            prepareShow();


            Resources resources = mContext.getResources();

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            layoutParams.setTitle("GameMode");

            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            if (dm.widthPixels < dm.heightPixels) {
                layoutParams.width = resources.getDimensionPixelSize(R.dimen.game_mode_proview_width);
                layoutParams.height = resources.getDimensionPixelSize(R.dimen.game_mode_proview_height);
                isLand = false;
            } else {
                layoutParams.width = resources.getDimensionPixelSize(R.dimen.game_mode_proview_width_land);
                layoutParams.height = resources.getDimensionPixelSize(R.dimen.game_mode_proview_height_land);
                isLand = true;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            //layoutParams.meizuParams.flags |= MeizuLayoutParams.MEIZU_FLAG_DISABLE_HIDING_ON_FULL_SCREEN;

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            wm.addView(getProView(), layoutParams);


            mCallBack.onShowChange(true);

        } else {
            RxBus.get().unregister(this);
            unbindBatteryService();

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            wm.removeViewImmediate(getProView());

            mCallBack.onShowChange(false);
        }
    }
}
