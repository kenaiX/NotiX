package com.flyme.systemuitools;

import android.content.res.Resources;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SystemUIToolsNotificationListenerService extends NotificationListenerService {

    private static final String TAG = "tools.NService";
    private final List<NotificationCallback> mCallbacks = new ArrayList<>();

    private static SystemUIToolsNotificationListenerService sInstance;
    private boolean mOnListenerConnected;

    public static interface NotificationCallback {
        void updateNotifications(StatusBarNotification[] sbns);

        void onNotificationRemoved(StatusBarNotification sbn);

        void onNotificationPosted(StatusBarNotification sbn);
    }

    public static SystemUIToolsNotificationListenerService getInstance() {
        if (sInstance == null) {
            sInstance = new SystemUIToolsNotificationListenerService();
        }
        return sInstance;
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d("ListenerService", "onNotificationPosted");
        synchronized (this) {
            if (sbn != null) {
                if (mCallbacks.size() > 0) {
                    for (NotificationCallback callback : mCallbacks) {
                        try {
                            callback.onNotificationPosted(sbn);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        synchronized (this) {
            if (sbn != null) {
                if (mCallbacks.size() > 0) {
                    for (NotificationCallback callback : mCallbacks) {
                        try {
                            callback.onNotificationRemoved(sbn);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void addNotificationListenerCallbacks(NotificationCallback callback) {
        synchronized (this) {
            mCallbacks.add(callback);
            if (mOnListenerConnected) {
                try {
                    final StatusBarNotification[] statusBarNotifications = getActiveNotifications();
                    if (statusBarNotifications != null) {
                        try {
                            callback.updateNotifications(statusBarNotifications);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Resources.NotFoundException ");
                }

            }
        }

    }

    public void removeNotificationListenerCallback(NotificationCallback callback) {
        synchronized (this) {
            if (mCallbacks.size() > 0) {
                mCallbacks.remove(callback);
            }
        }
    }

    @Override
    public void onListenerConnected() {
        synchronized (this) {
            mOnListenerConnected = true;
            if (mCallbacks.size() > 0) {
                try {
                    final StatusBarNotification[] statusBarNotifications = getActiveNotifications();
                    for (NotificationCallback callback : mCallbacks) {
                        try {
                            callback.updateNotifications(statusBarNotifications);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Resources.NotFoundException ");
                }
            }
        }
    }
}
