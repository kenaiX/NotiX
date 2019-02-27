package cc.kenai.noti.utils;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.lang.reflect.Method;

import cc.kenai.noti.XService;

public class SystemProx {
    static XService mNotificationListenerService;

    public static void enableNotificationListenerService(Application context, boolean b) {
        if (b) {
            try {
                if (mNotificationListenerService == null) {
                    Method method = NotificationListenerService.class.getMethod("registerAsSystemService", Context.class, ComponentName.class, Integer.TYPE);
                    mNotificationListenerService = new XService();
                    mNotificationListenerService.start(context);
                    method.invoke(mNotificationListenerService,
                            context,
                            new ComponentName(context.getPackageName(),
                                    context.getClass().getCanonicalName()), 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (mNotificationListenerService != null) {
                    Method method = NotificationListenerService.class.getMethod("unregisterAsSystemService");
                    method.invoke(mNotificationListenerService);
                    mNotificationListenerService.stop();
                    mNotificationListenerService = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getOriginPkg(StatusBarNotification sbn) {
        try {
            Method method = StatusBarNotification.class.getMethod("getOrigPackageName");
            return (String) method.invoke(sbn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbn.getPackageName();
    }
}
