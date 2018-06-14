package com.flyme.systemuitools.gamemode.events;

import android.service.notification.StatusBarNotification;

import java.util.List;

public class UIEvents {
    public static class CloseProView {

    }

    public static class NotificationUpdate {
        public List<StatusBarNotification> list;

        public NotificationUpdate(List<StatusBarNotification> list) {
            this.list = list;
        }
    }
}
