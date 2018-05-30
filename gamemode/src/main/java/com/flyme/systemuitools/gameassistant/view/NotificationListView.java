package com.flyme.systemuitools.gameassistant.view;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.List;

public class NotificationListView  extends ListView {
    public NotificationListView(Context context) {
        super(context);
    }

    public NotificationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotificationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NotificationListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void updateNotifications(List<StatusBarNotification> sbns) {

    }
}
