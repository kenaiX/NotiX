package com.flyme.systemui.dynamic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.flyme.systemui.utils.ZipUtils;
import com.meizu.flyme.activeview.listener.OnUpdateListener;
import com.meizu.flyme.activeview.views.ActiveView;

import java.io.File;
import java.io.IOException;

public class ActiveNotificationTest extends AppCompatActivity {
    private NotificationManager notificationManager;
    private Handler handler = new Handler();

    String storagePath = Environment.getExternalStorageDirectory().getPath();
    String activePath = storagePath + "/ttt";

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.active_notification_test);

        textView = findViewById(R.id.text);

        //init
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        File f = new File(activePath);
        if (f.exists() && f.isDirectory()) {
            Toast.makeText(this, "active文件存在", Toast.LENGTH_LONG).show();
        } else {
            if (f.exists()) {
                f.delete();
            }
            try {
                ZipUtils.Unzip(getAssets().open("test.act"), storagePath);
                Toast.makeText(this, "active文件解压成功", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "active文件解压失败", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }



        ActiveView activeView = findViewById(R.id.active);
        activeView.setOnUpdateListener(new OnUpdateListener() {
                                           @Override
                                           public void onUpdateFinished(int i, int i1, String s) {
                                               Log.i("@@@@", "activeview i1: " + i + " | i2: " + i1 + " | s:" + s);
                                           }
                                       });
        activeView.loadData(activePath);

    }

    //>>>>>>>>>>>>>>>>>>>> dynamic-headsup-active

    public void demoActiveHeadsupNotification(View view) {
        //发送四条通知以测试兼容性问题
        testCustom(false);
        testBigstyle(false);
        testCustom(true);
        testBigstyle(true);

        textView.setText("此时应当发送四条通知，并且四条通知都可以展开，其中两条展开后会显示active动画，另外两条会显示普通大通知的内容");
    }

    public void demoActiveHeadsupNotificationDelay(View view) {
        //发送四条通知以测试兼容性问题
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //testCustom(true);
                testBigstyle(true);
            }
        }, 2000);

        textView.setText("延时两秒发送");
    }

    public void demoActiveHeadsupNotification2(View view) {
        Notification notification = factoryBaseNotificationBuilder()
                .setContentTitle("这是一条常驻active通知")
                .setContentText("10s后自动消失")
                .setOngoing(true)
                .build();
        RemoteViews remoteViews1 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        RemoteViews remoteViews2 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.headsUpContentView = remoteViews1;
        notification.bigContentView = remoteViews2;

        //规避无法展开的需求
        RemoteViews remoteViews3 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.contentView = remoteViews3;

        //Build dynamic-notification info
        Bundle headsupBundle = new Bundle();
        headsupBundle.putString("path", activePath);
        Bundle stringBundleForHeadsup = new Bundle();
        stringBundleForHeadsup.putString("text-1", "tttt");
        stringBundleForHeadsup.putString("text-2", "ttttttttttt");
        headsupBundle.putBundle("string", stringBundleForHeadsup);

        Bundle bigBundle = new Bundle();
        bigBundle.putString("path", activePath);

        Bundle stringBundleForHeadsup2 = new Bundle();
        stringBundleForHeadsup2.putString("text-1", "tttt");
        stringBundleForHeadsup2.putString("text-2", "ttttttttttt");
        bigBundle.putBundle("string", stringBundleForHeadsup2);

        Bundle actionBundleForBig = new Bundle();
        actionBundleForBig.putParcelable("image-7", PendingIntent.getBroadcast(this, 0, new Intent("aaa"), 0));
        //actionBundleForBig.putParcelable("image-8", PendingIntent.getBroadcast(this,0,new Intent("aaa"),0 ));
        bigBundle.putBundle("action", actionBundleForBig);

        Bundle bundle = new Bundle();
        bundle.putBundle("headsup", headsupBundle);
        bundle.putBundle("big", bigBundle);
        notification.extras.putBundle("flyme.active", bundle);

        //Send notification
        notificationManager.notify(44, notification);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(44);
            }
        }, 10000);

        textView.setText("此时应当发送一条常驻active通知，10s后取消");
    }

    public void demoActiveHeadsupNotification3(View view) {
        Notification notification = factoryBaseNotificationBuilder()
                .setContentTitle("这是一条连续发送两次的active通知")
                .build();
        RemoteViews remoteViews1 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        RemoteViews remoteViews2 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.headsUpContentView = remoteViews1;
        notification.bigContentView = remoteViews2;

        //规避无法展开的需求
        RemoteViews remoteViews3 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.contentView = remoteViews3;

        //Build dynamic-notification info
        Bundle headsupBundle = new Bundle();
        headsupBundle.putString("path", activePath);
        Bundle stringBundleForHeadsup = new Bundle();
        stringBundleForHeadsup.putString("text-1", "tttt");
        stringBundleForHeadsup.putString("text-2", "ttttttttttt");
        headsupBundle.putBundle("string", stringBundleForHeadsup);

        Bundle bigBundle = new Bundle();
        bigBundle.putString("path", activePath);

        Bundle stringBundleForHeadsup2 = new Bundle();
        stringBundleForHeadsup2.putString("text-1", "tttt");
        stringBundleForHeadsup2.putString("text-2", "ttttttttttt");
        bigBundle.putBundle("string", stringBundleForHeadsup2);

        Bundle actionBundleForBig = new Bundle();
        actionBundleForBig.putParcelable("image-7", PendingIntent.getBroadcast(this, 0, new Intent("aaa"), 0));
        //actionBundleForBig.putParcelable("image-8", PendingIntent.getBroadcast(this,0,new Intent("aaa"),0 ));
        bigBundle.putBundle("action", actionBundleForBig);

        Bundle bundle = new Bundle();
        bundle.putBundle("headsup", headsupBundle);
        bundle.putBundle("big", bigBundle);
        notification.extras.putBundle("flyme.active", bundle);

        //Send notification
        notificationManager.notify(444, notification);
        notificationManager.notify(444, notification);

        textView.setText("此时应当发送两条一样的active通知，但只能看到一条");
    }

    public void demoActiveHeadsupNotification4(View view) {
        Notification notification = factoryBaseNotificationBuilder()
                .setContentTitle("看到这条通知说明存在bug")
                .setOngoing(true)
                .build();
        RemoteViews remoteViews1 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        RemoteViews remoteViews2 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.headsUpContentView = remoteViews1;
        notification.bigContentView = remoteViews2;

        //规避无法展开的需求
        RemoteViews remoteViews3 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.contentView = remoteViews3;

        //Build dynamic-notification info
        Bundle headsupBundle = new Bundle();
        headsupBundle.putString("path", activePath);
        Bundle stringBundleForHeadsup = new Bundle();
        stringBundleForHeadsup.putString("text-1", "tttt");
        stringBundleForHeadsup.putString("text-2", "ttttttttttt");
        headsupBundle.putBundle("string", stringBundleForHeadsup);

        Bundle bigBundle = new Bundle();
        bigBundle.putString("path", activePath);

        Bundle stringBundleForHeadsup2 = new Bundle();
        stringBundleForHeadsup2.putString("text-1", "tttt");
        stringBundleForHeadsup2.putString("text-2", "ttttttttttt");
        bigBundle.putBundle("string", stringBundleForHeadsup2);

        Bundle actionBundleForBig = new Bundle();
        actionBundleForBig.putParcelable("image-7", PendingIntent.getBroadcast(this, 0, new Intent("aaa"), 0));
        //actionBundleForBig.putParcelable("image-8", PendingIntent.getBroadcast(this,0,new Intent("aaa"),0 ));
        bigBundle.putBundle("action", actionBundleForBig);

        Bundle bundle = new Bundle();
        bundle.putBundle("headsup", headsupBundle);
        bundle.putBundle("big", bigBundle);
        notification.extras.putBundle("flyme.active", bundle);

        //Send notification
        notificationManager.notify(4444, notification);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(4444);
            }
        }, 100);

        textView.setText("此时会发送并立即取消，不应看到通知");

    }

    private Notification buildActiveNotification(Notification n, String path) {
        //Build dynamic-notification info
        Bundle headsupBundle = new Bundle();
        headsupBundle.putString("path", path);

        Bundle bigBundle = new Bundle();
        bigBundle.putString("path", path);

        Bundle bundle = new Bundle();
        bundle.putBundle("headsup", headsupBundle);
        bundle.putBundle("big", bigBundle);
        n.extras.putBundle("flyme.active", bundle);
        return n;
    }

    private Notification.Builder factoryBaseNotificationBuilder() {
        return new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(), 0))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_HIGH);
    }

    private void testCustom(Boolean withActive) {
        Notification.Builder builder = factoryBaseNotificationBuilder()
                .setContentTitle(withActive ? "这是一条带active信息的通知" : "这是一条不带active信息的通知")
                .setContentText("大通知类型为自定义");


        Notification notification = builder.build();
        RemoteViews remoteViews1 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        RemoteViews remoteViews2 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.headsUpContentView = remoteViews1;
        notification.bigContentView = remoteViews2;

        //规避无法展开的需求
        RemoteViews remoteViews3 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.contentView = remoteViews3;

        //Send notification
        notificationManager.notify((int) SystemClock.elapsedRealtime(), withActive ? buildActiveNotification(notification, activePath) : notification);
    }

    private void testBigstyle(Boolean withActive) {
        Notification.Builder builder = factoryBaseNotificationBuilder()
                .setContentTitle(withActive ? "这是一条带active信息的通知" : "这是一条不带active信息的通知")
                .setContentText("大通知类型为BigTextStyle");
        builder.setStyle(new Notification.BigTextStyle().setBigContentTitle(withActive ? "这是一条带active信息的通知" : "这是一条不带active信息的通知").setSummaryText("展开后Summary\n展开后Summary\n展开后Summary").bigText("展开后Text"));
        Notification notification = builder.build();

        //规避无法展开的需求
        RemoteViews remoteViews3 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.contentView = remoteViews3;

        //Send notification
        notificationManager.notify((int) SystemClock.elapsedRealtime(), withActive ? buildActiveNotification(notification, activePath) : notification);
    }
}
