package com.flyme.systemuitools.gamemode;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.LauncherActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.os.UserHandle;
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
import com.flyme.systemuitools.gamemode.utils.QuickAppsHelper;
import com.flyme.systemuitools.gamemode.view.BatteryView;
import com.flyme.systemuitools.gamemode.view.FullView;
import com.flyme.systemuitools.gamemode.view.GameDetailView;
import com.flyme.systemuitools.gamemode.view.QuickAppsView;
import com.flyme.systemuitools.gamemode.view.RedPointFrameLayout;
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
    private static final int DETAIL_VIEW_TYPE_NOTI = 0;
    private static final int DETAIL_VIEW_TYPE_GAME = 1;
    private static final int DETAIL_VIEW_TYPE_SETTINGS = 2;
    private static final int DETAIL_STATE_TYPE_NOTI = 0;
    private static final int DETAIL_STATE_TYPE_GAME = 1;
    private static final int DETAIL_STATE_TYPE_SETTINGS = 2;

    private final Context mContext;
    private final CallBack mCallBack;
    private final boolean isLand;

    //view持有
    private final View mProView;
    private final RelativeLayout mHeadFrame;
    private final View mTabFrame;
    private final RedPointFrameLayout mTabNoti;
    private final RedPointFrameLayout mTabGame;
    private final com.flyme.systemuitools.gamemode.view.BatteryView mBatteryFrame;
    private final ImageView mMoreAppsMenu;
    private final com.flyme.systemuitools.gamemode.view.MoreAppsView mMoreAppsFrame;
    private final TextView mTabSettings;
    private final FrameLayout mDetailFrame;
    private final QuickAppsView mQuckAppsFrame;
    private final FullView mFullView;
    //控制变量
    private boolean mAppsloadFinished = false;
    private int mDetailState = -1;//标注当前显示状态
    private List<QuickAppsRecord> mQuickAppsList;//快捷栏应用
    private List<AppInfo> mAppApps;//所有应用
    private Map<String, Integer> mAppsUsedRecord = new HashMap<>();//用来给应用排序
    private boolean mShowMoreAppsFlag = false;
    private boolean isShow = false;

    public interface CallBack {
        List<AppInfo> loadAllApps();

        AppInfo loadApps(String pkg, String name, int userid);

        void startApps(AppInfo info);

        List<StatusBarNotification> getNotifications();

        String getGamePkg();

        void onShowChange(boolean isShow, boolean showPanadaImmediately);

        int getWelfareNum();

        String computeBatteryTimeRemaining();
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
            //todo  bug
            return new QuickAppsRecord(componentName, info.getInfo().getUser().equals(Process.myUserHandle()) ? 0 : 999);
        }

        public static String AppInfo2String(AppInfo info) {
            ComponentName componentName = info.getInfo().getComponentName();
            return componentName.getPackageName() + "/" + componentName.getClassName() + ":" + (info.getInfo().getUser().equals(Process.myUserHandle()) ? 0 : 999);
        }

        @Override
        public String toString() {
            return componentName.getPackageName() + "/" + componentName.getClassName() + ":" + userId;
        }
    }

    public ProViewControler(Context context, CallBack callBack) {
        mCallBack = callBack;
        mContext = context;

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        isLand = dm.widthPixels > dm.heightPixels;

        mProView = View.inflate(context, isLand ? R.layout.gamemode_pro_main_land : R.layout.gamemode_pro_main, null);
        mHeadFrame = (RelativeLayout) mProView.findViewById(R.id.gamemode_head_frame);
        mBatteryFrame = (com.flyme.systemuitools.gamemode.view.BatteryView) mProView.findViewById(R.id.gamemode_battery_frame);
        mMoreAppsMenu = (ImageView) mProView.findViewById(R.id.gamemode_more_menu);
        mQuckAppsFrame = (com.flyme.systemuitools.gamemode.view.QuickAppsView) mProView.findViewById(R.id.gamemode_quick_apps_frame);
        mTabNoti = (RedPointFrameLayout) mProView.findViewById(R.id.gamemode_tab_noti);
        mTabGame = (RedPointFrameLayout) mProView.findViewById(R.id.gamemode_tab_game);
        mTabSettings = (TextView) mProView.findViewById(R.id.gamemode_tab_settings);
        mFullView = (FullView) mProView.findViewById(R.id.gamemode_full);

        //用来放置详情页
        mDetailFrame = (FrameLayout) mProView.findViewById(R.id.gamemode_detail_frame);
        //用来放置应用列表
        mMoreAppsFrame = (com.flyme.systemuitools.gamemode.view.MoreAppsView) mProView.findViewById(R.id.gamemode_more_apps_frame);
        //用来放置Tab
        mTabFrame = mProView.findViewById(R.id.gamemode_tab_frame);

        Resources resources = mContext.getResources();
        mTabNoti.setTitle(resources.getString(R.string.game_mode_pro_noti));
        mTabGame.setTitle(resources.getString(R.string.game_mode_pro_game));

        mMoreAppsMenu.setOnClickListener(this);
        mTabNoti.setOnClickListener(this);
        mTabGame.setOnClickListener(this);
        mTabSettings.setOnClickListener(this);

        mBatteryFrame.setCallback(new BatteryView.Callback() {
            @Override
            public String computeBatteryTimeRemaining() {
                return mCallBack.computeBatteryTimeRemaining();
            }
        });

        mFullView.setVisibilityChangedCallback(new FullView.Callback() {
            @Override
            public void onVisibilityChanged(boolean show) {
                if (mProView.isShown()) {
                    if (show) {
                        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mProView.getLayoutParams();
                        layoutParams.flags = buildWindowFlag(true);
                        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                        wm.updateViewLayout(mProView, layoutParams);
                    } else {
                        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mProView.getLayoutParams();
                        layoutParams.flags = buildWindowFlag(false);
                        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                        wm.updateViewLayout(mProView, layoutParams);
                    }
                }
            }
        });

        //从桌面拉取应用启动次数
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

        mQuickAppsList = QuickAppsHelper.getList(mContext);

        //开始加载当前所有应用，会导致内存加大
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppApps = mCallBack.loadAllApps();
                mAppsloadFinished = true;
            }
        }).start();
    }

    public boolean isShow() {
        return isShow;
    }

    public void toggleProViewShow(boolean show) {
        toggleProViewShow(show, true);
    }

    public void toggleProViewShow(boolean show, boolean showPanadaImmediately) {
        if (show == isShow) {
            return;
        }
        isShow = show;
        if (show) {
            RxBus.get().register(this);

            prepareShow();
            WindowManager.LayoutParams layoutParams;
            if (mContext.getPackageName().equals("com.flyme.systemuitools")) {
                layoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                    layoutParams = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 38);
                } else {
                    layoutParams = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                }
            }
            layoutParams.setTitle("GameMode");

            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            layoutParams.width = metrics.widthPixels;
            layoutParams.height = metrics.heightPixels;

            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.flags = buildWindowFlag(false);
            //layoutParams.meizuParams.flags |= MeizuLayoutParams.MEIZU_FLAG_DISABLE_HIDING_ON_FULL_SCREEN;

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            wm.addView(mProView, layoutParams);

            mCallBack.onShowChange(true, showPanadaImmediately);

        } else {
            RxBus.get().unregister(this);

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            wm.removeViewImmediate(mProView);

            mCallBack.onShowChange(false, showPanadaImmediately);
        }
    }

    @Subscribe
    public void onNotificationChange(UIEvents.NotificationUpdate event) {
        if (mDetailState != DETAIL_STATE_TYPE_NOTI) {
            mTabNoti.toggleGameRedPoint(event.list.size() > 0 ? true : false);
        }
    }

    @Subscribe
    public void onStartDrag(DragEvents.StartDrag event) {
        toggleMoreApps(true, false);
    }

    @Subscribe
    public void onStartApp(ClickEvents.OnAppsClick event) {
        toggleProViewShow(false, false);
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
    public void onSaveQuickAppsConfig(ConfigChangeEvents.OnSaveQuickAppsConfig event) {
        mQuickAppsList.clear();
        for (AppInfo info : event.list) {
            if (info != null) {
                mQuickAppsList.add(QuickAppsRecord.fromAppInfo(info));
            }
        }
        QuickAppsHelper.save(mContext, event.list);
        RxBus.get().post(new ConfigChangeEvents.OnQuickAppsConfigChanged());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.gamemode_more_menu) {
            toggleMoreApps(!mShowMoreAppsFlag, true);
            return;
        }

        View replace = null;
        switch (id) {
            case R.id.gamemode_tab_noti:
                mTabNoti.toggleGameRedPoint(false);
                if (mDetailState == DETAIL_STATE_TYPE_NOTI) {
                    return;
                }
                mDetailState = DETAIL_STATE_TYPE_NOTI;
                replace = buildDetailView(mContext, DETAIL_VIEW_TYPE_NOTI);
                NotificationListView notificationListView = (NotificationListView) replace.findViewById(R.id.noti_list);
                List<StatusBarNotification> notifications = mCallBack.getNotifications();
                notificationListView.updateNotifications(notifications);
                replace.findViewById(R.id.summary_no_noti).setVisibility(notifications.size() > 0 ? View.INVISIBLE : View.VISIBLE);
                break;
            case R.id.gamemode_tab_game:
                mTabGame.toggleGameRedPoint(false);
                if (mDetailState == DETAIL_STATE_TYPE_GAME) {
                    return;
                }
                mDetailState = DETAIL_STATE_TYPE_GAME;
                replace = buildDetailView(mContext, DETAIL_VIEW_TYPE_GAME);
                GameDetailView gameDetailView = (GameDetailView) replace;
                gameDetailView.setGameSubView(mFullView);
                gameDetailView.showGame(mCallBack.getGamePkg());
                break;
            case R.id.gamemode_tab_settings:
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

    private void updateTabColor(int id) {
        mTabNoti.setBackgroundColor(Color.TRANSPARENT);
        mTabGame.setBackgroundColor(Color.TRANSPARENT);
        mTabSettings.setBackgroundColor(Color.TRANSPARENT);
        switch (id) {
            case R.id.gamemode_tab_noti:
                mTabNoti.setBackgroundColor(Color.WHITE);
                break;
            case R.id.gamemode_tab_game:
                mTabGame.setBackgroundColor(Color.WHITE);
                break;
            case R.id.gamemode_tab_settings:
                mTabSettings.setBackgroundColor(Color.WHITE);
                break;
        }
    }

    private List<AppInfo> loadAllApps(List<QuickAppsRecord> filter) {
        List<AppInfo> list = new ArrayList<>();
        String currentPkg = mCallBack.getGamePkg();
        UserHandle owner = Process.myUserHandle();
        outer:
        for (AppInfo app : mAppApps) {
            LauncherActivityInfo info = app.getInfo();
            if (TextUtils.equals(currentPkg, info.getComponentName().getPackageName())) {
                continue outer;
            }
            for (int i = 0, n = filter.size(); i < n; i++) {
                ProViewControler.QuickAppsRecord record = filter.get(i);
                boolean isOwner = record.userId == 0;
                if ((isOwner == info.getUser().equals(owner)) && record.componentName.equals(info.getComponentName())) {
                    continue outer;
                }
            }
            list.add(app);
        }
        return list;
    }


    private void toggleMoreApps(boolean show, boolean needSummary) {
        if (mShowMoreAppsFlag == show) {
            if (show) {
                mMoreAppsFrame.setSummary(false);
            }
            return;
        } else {
            mShowMoreAppsFlag = show;
        }
        if (show) {
            while (!mAppsloadFinished) {
                Thread.yield();
            }
            List<AppInfo> appInfos = loadAllApps(mQuickAppsList);
            sortAllApps(appInfos);
            mMoreAppsFrame.init(appInfos, isLand ? 6 : 4);
            mMoreAppsFrame.setVisibility(View.VISIBLE);
            mMoreAppsFrame.setSummary(needSummary);
            mTabFrame.setVisibility(View.INVISIBLE);
            mDetailFrame.setVisibility(View.INVISIBLE);
            mMoreAppsMenu.setImageResource(R.drawable.gamemode_more_apps_menu_collapse);
        } else {
            mMoreAppsFrame.setVisibility(View.INVISIBLE);
            mTabFrame.setVisibility(View.VISIBLE);
            mDetailFrame.setVisibility(View.VISIBLE);
            mMoreAppsMenu.setImageResource(R.drawable.gamemode_more_apps_menu);
        }
    }

    private void sortAllApps(List<AppInfo> list) {
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
                v = View.inflate(context, R.layout.gamemode_pro_detail_noti, null);
                break;
            case DETAIL_VIEW_TYPE_GAME:
                v = View.inflate(context, R.layout.gamemode_pro_detail_game, null);
                break;
            case DETAIL_VIEW_TYPE_SETTINGS:
                v = View.inflate(context, R.layout.gamemode_pro_detail_settings, null);
                break;
        }
        return v;
    }

    private void prepareShow() {
        mDetailState = -1;

        if (mCallBack.getNotifications().size() != 0) {
            onClick(mTabNoti);//mDetailState = noti
            mTabGame.toggleGameRedPoint(mCallBack.getWelfareNum() > 0);
        } else {
            onClick(mTabGame);//mDetailState = game
            mTabNoti.toggleGameRedPoint(false);
        }

        toggleMoreApps(false, false);

        List<AppInfo> list = new ArrayList<>();
        for (QuickAppsRecord record : mQuickAppsList) {
            AppInfo test = mCallBack.loadApps(record.componentName.getPackageName(), record.componentName.getClassName(), record.userId);
            if (test != null) {
                list.add(test);
            }
        }
        mQuckAppsFrame.bindApps(list);
    }

    private int buildWindowFlag(boolean focusable) {
        int flag = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        if (focusable) {
            return flag;
        } else {
            return flag | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
    }
}
