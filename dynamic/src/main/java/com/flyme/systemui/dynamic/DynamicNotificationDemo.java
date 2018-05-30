package com.flyme.systemui.dynamic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.flyme.systemui.miniheadsup.MiniHeadsupService;
import com.flyme.systemui.recents.IRecentsRemoteService;
import com.flyme.systemui.utils.ZipUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class DynamicNotificationDemo extends AppCompatActivity {
    private final static String TAG = "DynamicNotification";
    private final static String DynamicFlag = "dynamic_notification";
    private final static String ACTION_UPDATE_CUSTOMCONTENT_PAUSE = "ACTION_PAUSE";
    private final static String ACTION_UPDATE_CUSTOMCONTENT_NEXT = "ACTION_NEXT";
    private final static String ACTION_UPDATE_CUSTOMCONTENT_PREV = "ACTION_PREV";
    private final static String ACTION_UPDATE_CUSTOMCONTENT_COLLECT = "ACTION_COLLECT";
    private NotificationManager mNM;
    private Handler mHandler = new Handler();
    private boolean mCollected = false, mMusicPause;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_UPDATE_CUSTOMCONTENT_PAUSE.equals(action)) {
                updateCustomContentNotificationPause();
            } else if (ACTION_UPDATE_CUSTOMCONTENT_NEXT.equals(action)) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        testMiniHeadsupCallAnswer(null);
                    }
                }, 100);
                Toast.makeText(DynamicNotificationDemo.this,"收到next",Toast.LENGTH_SHORT).show();
            } else if (ACTION_UPDATE_CUSTOMCONTENT_PREV.equals(action)) {
                updateCustomContentNotificationPrev();
                Toast.makeText(DynamicNotificationDemo.this,"收到prev",Toast.LENGTH_SHORT).show();
            } else if (ACTION_UPDATE_CUSTOMCONTENT_COLLECT.equals(action)) {
                updateCustomContentNotificationCollect();
            }
        }
    };


    class BitmapView extends View{
        private boolean mDraw;
        public BitmapView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!mDraw) {
                for(int i=0;i<5;i++) {
                    Bitmap b = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                    canvas.drawBitmap(b,0,0,null);
                    b.recycle();
                }
                mDraw = true;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*BitmapView view = new BitmapView(this);
        setContentView(view);*/

        setContentView(R.layout.activity_main);

        //init
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //receiver
        IntentFilter it = new IntentFilter();
        it.addAction(ACTION_UPDATE_CUSTOMCONTENT_PREV);
        it.addAction(ACTION_UPDATE_CUSTOMCONTENT_PAUSE);
        it.addAction(ACTION_UPDATE_CUSTOMCONTENT_NEXT);
        it.addAction(ACTION_UPDATE_CUSTOMCONTENT_COLLECT);
        registerReceiver(mReceiver, it);

        lockActivityInRecents();

        initMiniHeadupTestService();

        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/"+"ttt");
        if(f.exists()&&f.isDirectory()){
            Toast.makeText(this,"测试文件存在",Toast.LENGTH_LONG).show();
        }else{
            try {
                ZipUtils.Unzip(getAssets().open("test.act"),Environment.getExternalStorageDirectory().getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
/*
        //receiver
        unregisterReceiver(mReceiver);*/
    }

    //>>>>>>>>>>>>>>>>>>> DynamicLoad
    public void demoDynamicLoad(View view) {
        final File optimizedDexOutputPath = new File("sdcard/test.jar");
        File dexOutputDir = getDir("dex", 0);
        DexClassLoader cl = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
                dexOutputDir.getAbsolutePath(), null, ClassLoader.getSystemClassLoader().getParent());
        Class libProviderClazz = null;

        try {
            libProviderClazz = cl.loadClass("cc.kenai.dynamic.DynamicTest");
            Method inject = libProviderClazz.getMethod("inject", ListAdapter.class, Integer.TYPE, Integer.TYPE);
            inject.invoke(libProviderClazz.newInstance());
        } catch (Exception exception) {
            // Handle exception gracefully here.
            exception.printStackTrace();
        }
    }


    //>>>>>>>>>>>>>>>>>>> dynamic-handsup
    public void demoHandsupNotification(View view) {
        //Build normal notification
        Notification noti = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("魅族")
                .setContentText("123456789")
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        String primaryText = "魅族";
        String secondaryText = "123456789";
        remoteViews.setTextViewText(R.id.caller_label_primary, primaryText);
        remoteViews.setTextViewText(R.id.caller_label_secondary, secondaryText);
        noti.headsUpContentView = remoteViews;

        //Build dynamic-notification info
        DynamicNotificationModel model = new DynamicNotificationModel();
        DynamicNotificationModel.DynamicContent customContent = new DynamicNotificationModel.DynamicContent();
        customContent.setAnim(new int[]{(1 << 31) + 2, R.id.control_btn_accept, R.animator.dynamic_headsup_on_answer_bg_1,
                (1 << 31) + 2, R.id.control_btn_decline, R.animator.dynamic_headsup_on_answer_bg_2});

        model.setHeadsupcontent(customContent);
        Gson gson = new Gson();
        String json = gson.toJson(model);
        Log.e(TAG, json);



        new DynamicNotificationBuilder(noti)
                .appendHeadsupAnim(new DynamicNotificationBuilder.AnimationItem(DynamicNotificationBuilder.TYPE_ANIMATOR,R.id.control_btn_decline,R.animator.dynamic_headsup_on_answer_test,true))
                .appendHeadsupAnim(new DynamicNotificationBuilder.AnimationItem(DynamicNotificationBuilder.TYPE_ANIMATOR,R.id.control_btn_accept_bg_3,R.animator.dynamic_headsup_on_answer_bg_3))
                .appendHeadsupAnim(new DynamicNotificationBuilder.AnimationItem(DynamicNotificationBuilder.TYPE_ANIMATOR,R.id.control_btn_accept_bg_2,R.animator.dynamic_headsup_on_answer_bg_2))
                .appendHeadsupAnim(new DynamicNotificationBuilder.AnimationItem(DynamicNotificationBuilder.TYPE_ANIMATOR,R.id.control_btn_accept_bg_1,R.animator.dynamic_headsup_on_answer_bg_1))
                .build();

        /*StringBuilder sb = new StringBuilder();
        sb.append("{\"headsupcontent\":{\"anim\":[")
                .append((1 << 31)*//*Repeat flag*//* + 2).append(",").append(R.id.control_btn_decline).append(",").append(R.animator.dynamic_headsup_on_answer_test)
                .append(",").append(2).append(",").append(R.id.control_btn_accept_bg_3).append(",").append(R.animator.dynamic_headsup_on_answer_bg_3)
                .append(",").append(2).append(",").append(R.id.control_btn_accept_bg_2).append(",").append(R.animator.dynamic_headsup_on_answer_bg_2)
                .append(",").append(2).append(",").append(R.id.control_btn_accept_bg_1).append(",").append(R.animator.dynamic_headsup_on_answer_bg_1)
                .append("]}}");
        noti.extras.putString(DynamicFlag, sb.toString());*/

        //Send notification
        mNM.notify(1, noti);
    }

    //>>>>>>>>>>>>>>>>>>>> dynamic-normalcontent
    public void demoContentNotification(View view) {
        //Build normal notification
        Notification notification = factoryDemoContentNotification();

        //Send notification
        mNM.notify(1, notification);
    }

    private Notification factoryDemoContentNotification() {
        Intent updateIntentPrev = new Intent(ACTION_UPDATE_CUSTOMCONTENT_PREV);
        Intent updateIntentPause = new Intent(ACTION_UPDATE_CUSTOMCONTENT_PAUSE);
        Intent updateIntentNext = new Intent(ACTION_UPDATE_CUSTOMCONTENT_NEXT);
        Intent updateIntentCollect = new Intent(ACTION_UPDATE_CUSTOMCONTENT_COLLECT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(), 0))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();

        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.test_music_layout);
        if (mMusicPause) {
            remoteViews.setImageViewResource(R.id.music_pause, R.drawable.music_pause);
        } else {
            remoteViews.setImageViewResource(R.id.music_pause, R.drawable.music_play);
        }

        if (mCollected) {
            remoteViews.setImageViewResource(R.id.music_collect, R.drawable.heart_pink);
        } else {
            remoteViews.setImageViewResource(R.id.music_collect, R.drawable.heart_white);
        }

        remoteViews.setOnClickPendingIntent(R.id.music_prev, PendingIntent.getBroadcast(this, 0, updateIntentPrev, PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.music_pause, PendingIntent.getBroadcast(this, 0, updateIntentPause, PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.music_next, PendingIntent.getBroadcast(this, 0, updateIntentNext, PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.music_collect, PendingIntent.getBroadcast(this, 0, updateIntentCollect, PendingIntent.FLAG_UPDATE_CURRENT));
        notification.contentView = remoteViews;

        return notification;
    }

    private void updateCustomContentNotificationCollect() {
        mCollected = !mCollected;

        //Build normal notification
        Notification notification = factoryDemoContentNotification();

        //Build dynamic-notification info
        DynamicNotificationModel model = new DynamicNotificationModel();
        DynamicNotificationModel.DynamicContent customContent = new DynamicNotificationModel.DynamicContent();
        customContent.setAnim(new int[]{1, R.id.music_collect, mCollected ? R.drawable.music_white_to_pink : R.drawable.music_pink_to_white});
        model.setContent(customContent);
        Gson gson = new Gson();
        String json = gson.toJson(model);
        Log.e(TAG, json);
        notification.extras.putString(DynamicFlag, json);

        //Send notification
        mNM.notify(1, notification);
    }

    private void updateCustomContentNotificationPrev() {
        //Build normal notification
        Notification notification = factoryDemoContentNotification();

        //Build dynamic-notification info
        DynamicNotificationModel model = new DynamicNotificationModel();
        DynamicNotificationModel.DynamicContent customContent = new DynamicNotificationModel.DynamicContent();
        customContent.setAnim(new int[]{0, R.id.music_prev, R.anim.anim_shake_x_left_once});
        model.setContent(customContent);
        Gson gson = new Gson();
        String json = gson.toJson(model);
        Log.e(TAG, json);
        notification.extras.putString(DynamicFlag, json);

        //Send notification
        mNM.notify(1, notification);
    }

    private void updateCustomContentNotificationPause() {
        mMusicPause = !mMusicPause;

        //Build normal notification
        Notification notification = factoryDemoContentNotification();

        //Build dynamic-notification info
        DynamicNotificationModel model = new DynamicNotificationModel();
        DynamicNotificationModel.DynamicContent customContent = new DynamicNotificationModel.DynamicContent();
        customContent.setAnim(new int[]{0, R.id.music_pause, R.anim.anim_shake_scale_once});
        model.setContent(customContent);
        Gson gson = new Gson();
        String json = gson.toJson(model);
        Log.e(TAG, json);
        notification.extras.putString(DynamicFlag, json);

        //Send notification
        mNM.notify(1, notification);
    }

    private void updateCustomContentNotificationNext() {
        //Build normal notification
        Notification notification = factoryDemoContentNotification();

        //Build dynamic-notification info
        DynamicNotificationModel model = new DynamicNotificationModel();
        DynamicNotificationModel.DynamicContent customContent = new DynamicNotificationModel.DynamicContent();
        customContent.setAnim(new int[]{0, R.id.music_next, R.anim.anim_shake_x_right_once});
        model.setContent(customContent);
        Gson gson = new Gson();
        String json = gson.toJson(model);
        Log.e(TAG, json);
        notification.extras.putString(DynamicFlag, json);

        //Send notification
        mNM.notify(1, notification);
    }

    //>>>>>>>>>>>>>>>>>>>> mini-headsup
    private void initMiniHeadupTestService(){
        startService(new Intent(this,MiniHeadsupService.class));
    }

    public void testMiniHeadsup(View view){
        MiniHeadsupService.newNotification();
    }

    public void testMiniHeadsupIcon(View view){
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(), 0))
                .build();

        Bundle b = new Bundle();
        b.putInt("first_color", Color.BLACK);
        b.putInt("second_left_color", Color.BLACK);
        b.putInt("second_right_color", Color.BLACK);
        b.putParcelable("first_icon",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));
        b.putString("first_text","魅族客服");

        b.putParcelable("second_left_value",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_refuse));
        b.putParcelable("second_right_value",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));

        b.putParcelable("second_left_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_PREV),0 ));
        b.putParcelable("second_right_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_NEXT),0 ));

        b.putParcelable("third_icon",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));
        //b.putString("third_text","clock");//此字段也可以不发，这样会默认显示计时器，当要显示文字时b.putString("third_text","扬声器接听");

        notification.extras.putBundle("flyme.miniheadsup",b);

        //Send notification
        mNM.notify(6, notification);
    }

    public void testMiniHeadsupCallAnswer(View view){
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(), 0))
                .build();

        Bundle b = new Bundle();
        b.putInt("first_color", Color.BLACK);
        b.putInt("second_left_color", Color.BLACK);
        b.putInt("second_right_color", Color.BLACK);
        b.putParcelable("first_icon",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));
        b.putString("first_text","魅族客服");

        b.putParcelable("second_left_value",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_refuse));
        b.putParcelable("second_right_value",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));

        b.putParcelable("second_left_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_PREV),0 ));
        b.putParcelable("second_right_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_NEXT),0 ));

        //b.putParcelable("third_icon",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));
        b.putString("third_text","扬声器接听");

        b.putBoolean("auto_snooze",true);

        notification.extras.putBundle("flyme.miniheadsup",b);

        mNM.cancel(6);

        //Send notification
        mNM.notify(7, notification);

        testMiniHeadsupCallAnswer2(null);
        testMiniHeadsupCallAnswer2(null);
        testMiniHeadsupCallAnswer2(null);
    }


    public void testMiniHeadsupCallAnswer2(View view){
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(), 0))
                .build();

        Bundle b = new Bundle();
        b.putInt("first_color", Color.BLACK);
        b.putInt("second_left_color", Color.BLACK);
        b.putInt("second_right_color", Color.BLACK);
        b.putParcelable("first_icon",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));
        b.putString("first_text","魅族客服");

        b.putParcelable("second_left_value",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_refuse));
        b.putParcelable("second_right_value",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));

        b.putParcelable("second_left_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_PREV),0 ));
        b.putParcelable("second_right_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_NEXT),0 ));

        //b.putParcelable("third_icon",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));
        b.putString("third_text","扬声器接听");

        b.putBoolean("auto_snooze",true);

        notification.extras.putBundle("flyme.miniheadsup",b);

        //Send notification
        mNM.notify(7, notification);
    }


    public void testMiniHeadsupIconColor(View view){
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(), 0))
                .build();

        Bundle b = new Bundle();
        b.putInt("first_color", Color.RED);
        b.putInt("second_left_color", Color.BLACK);
        b.putInt("second_right_color", Color.BLACK);
        b.putParcelable("first_icon",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));
        b.putString("first_text","魅族客服");

        b.putParcelable("second_left_value",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_refuse));
        b.putParcelable("second_right_value",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));

        b.putParcelable("second_left_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_PREV),0 ));
        b.putParcelable("second_right_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_NEXT),0 ));

        b.putBoolean("auto_snooze",true);

        notification.extras.putBundle("flyme.miniheadsup",b);

        //Send notification
        mNM.notify(6, notification);
    }

    public void testMiniHeadsupText(View view){
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(), 0))
                .build();
        RemoteViews remoteViews1 = new RemoteViews(getPackageName(), R.layout.test_incoming_notification_headsup);
        notification.headsUpContentView = remoteViews1;

        Bundle b = new Bundle();
        b.putInt("first_color", Color.BLACK);
        b.putInt("second_left_color", Color.BLACK);
        b.putInt("second_right_color", Color.BLACK);
        b.putParcelable("first_icon",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));
        b.putString("first_text","新闹钟");

        b.putString("second_left_value","推迟");
        b.putString("second_right_value","关闭");

        b.putParcelable("second_left_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_PREV),0 ));
        b.putParcelable("second_right_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_NEXT),0 ));

        notification.extras.putBundle("flyme.miniheadsup",b);

        //Send notification
        mNM.notify(7, notification);
    }

    public void testMiniHeadsupText2(View view){
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(), 0))
                .build();

        Bundle b = new Bundle();
        b.putInt("first_color", Color.BLACK);
        b.putInt("second_left_color", Color.BLACK);
        b.putInt("second_right_color", Color.BLACK);
        b.putParcelable("first_icon",BitmapFactory.decodeResource(getResources(),R.drawable.notice_mini_answer));
        b.putString("first_text","新闹钟");

        b.putString("second_left_value","推迟");
        //b.putString("second_right_value","关闭");

        b.putParcelable("second_left_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_PREV),0 ));
        b.putParcelable("second_right_action",PendingIntent.getBroadcast(this,0,new Intent(ACTION_UPDATE_CUSTOMCONTENT_NEXT),0 ));

        notification.extras.putBundle("flyme.miniheadsup",b);

        //Send notification
        mNM.notify(7, notification);
    }


    private void lockActivityInRecents(){
        Intent intent = new Intent();
        intent.setClassName("com.android.systemui","com.flyme.systemui.recents.RecentsRemoteService");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    IRecentsRemoteService.Stub.asInterface(service).lockPkgTemporarily(getPackageName(), 1 /*1:lock 0:unlock*/);
                    unbindService(this);//这里不是必须，应用自己视情况断开连接
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }
}
