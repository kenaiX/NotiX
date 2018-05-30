package com.flyme.systemuitools.launcher;

import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RedPointManager {

    private final static String TAG = "RedPoint.Manager";

    private final static RedPointManager mInstance = new RedPointManager();

    private final Map<String, ArrayList<String>> mRedPointList = new HashMap<>();

    private RedPointChangeCallback mCallback;

    public interface RedPointChangeCallback {
        void onChange();
    }

    private RedPointManager() {
    }

    public static RedPointManager getInstance() {
        return mInstance;
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        String key = sbn.getKey();
        String pkg = sbn.getPackageName();
        //需要判断是否拦截，需要判断是否常驻
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    String getListString() {
        StringBuilder sb = new StringBuilder();
        synchronized (mRedPointList) {
            for (String pkg : mRedPointList.keySet()) {
                sb.append(pkg);
                sb.append(";");
            }
        }
        return sb.toString();
    }

    void markRedPointList(String listString) {
        String[] pkgs = listString.split(";");
        if (pkgs.length > 0) {
            synchronized (mRedPointList) {
                for (String pkg : pkgs) {
                    if (!TextUtils.isEmpty(pkg)) {
                        mRedPointList.remove(pkg);
                        Log.i(TAG, "mark " + pkg);
                    }
                }
            }
        }
    }

    void setChangeListener(RedPointChangeCallback callback) {
        mCallback = callback;
    }

    //单独抽出来方便测试
    void dealWithNotificationChange(boolean isRemoved, String pkg, String key) {
        synchronized (mRedPointList) {
            if (!isRemoved) {
                ArrayList<String> result = mRedPointList.get(pkg);
                if (result == null) {
                    ArrayList<String> keyList = new ArrayList<>();
                    keyList.add(key);
                    mRedPointList.put(pkg, keyList);
                    notifyChange();
                } else {
                    if (result.contains(key)) {
                        Log.w(TAG, pkg + " - " + key + " is already exist");
                    } else {
                        result.add(key);
                    }
                }
            } else {
                ArrayList<String> result = mRedPointList.get(pkg);
                if (result != null) {
                    boolean exist = result.remove(key);
                    if (exist && result.isEmpty()) {
                        mRedPointList.remove(pkg);
                        notifyChange();
                    }
                }
            }
        }
    }

    private void notifyChange() {
        if (mCallback != null) {
            mCallback.onChange();
        }
    }
}
