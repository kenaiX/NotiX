package cc.kenai.projectX.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends Activity {
    List<AppWidgetProviderInfo> list;
    private static final int APPWIDGET_HOST_ID = 0x200;

    private static final int REQUEST_ADD_WIDGET = 1;
    private static final int REQUEST_CREATE_WIDGET = 2;

    AppWidgetHost mAppWidgetHost;
    AppWidgetManager manager;
    MyWidgetHost myWidgetHostView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mAppWidgetHost = new AppWidgetHost(getApplication(), APPWIDGET_HOST_ID);
        manager = AppWidgetManager.getInstance(getBaseContext());
        myWidgetHostView = new MyWidgetHost(this);

        myWidgetHostView.setOnClickListener(new View.OnClickListener() {
            boolean isFirst = true;
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(isFirst) {
                    selectWidgets();
                }else{
                    findImageView(myWidgetHostView);
                }
                isFirst = false;

            }
        });
        setContentView(myWidgetHostView);
        mAppWidgetHost.startListening();

        createWidget();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findImageView(myWidgetHostView);
            }
        },500 );

    }

    int i =0;

    @SuppressLint("ResourceType")
    void findImageView(View v) {
        if (v instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                findImageView(((ViewGroup) v).getChildAt(i));
            }
        } else if (v instanceof ImageView) {
            ++i;
            if (v.getId() == 0x7f110277) { v.performClick();
                i = 0;
                return;
            }
        }
    }

    private void createWidget() {
        List<AppWidgetProviderInfo> installedProviders = manager.getInstalledProviders();
        for(int i =0;i<installedProviders.size();i++){
            AppWidgetProviderInfo appWidget = installedProviders.get(i);
            if(appWidget.loadLabel(getPackageManager()).contains("虾米4x1")){
                // 根据AppWidgetProviderInfo信息，创建HostView
                // 生成插件ID
                int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
                // 绑定ID
                boolean b = manager.bindAppWidgetIdIfAllowed(appWidgetId, appWidget.provider);

                View hostView = mAppWidgetHost.createView(this, appWidgetId, appWidget);
                // View view = hostView.findViewById(appWidget.autoAdvanceViewId);
                // ((Advanceable)view).fyiWillBeAdvancedByHostKThx();
                // 将HostView添加到桌面

                myWidgetHostView.addInScreen(hostView, appWidget.minWidth + 100,
                        appWidget.minHeight + 200);
                myWidgetHostView.requestLayout();
            }
        }
    }

    protected void selectWidgets() {
        int widgetId = mAppWidgetHost.allocateAppWidgetId();

        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        startActivityForResult(pickIntent, REQUEST_ADD_WIDGET);
    }

    private void createWidget(Intent data) {
        // 获取选择的widget的id
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        // 获取所选的Widget的AppWidgetProviderInfo信息
        AppWidgetProviderInfo appWidget = manager.getAppWidgetInfo(appWidgetId);

        // 根据AppWidgetProviderInfo信息，创建HostView

        View hostView = mAppWidgetHost.createView(this, appWidgetId, appWidget);
        // View view = hostView.findViewById(appWidget.autoAdvanceViewId);
        // ((Advanceable)view).fyiWillBeAdvancedByHostKThx();
        // 将HostView添加到桌面

        myWidgetHostView.addInScreen(hostView, appWidget.minWidth + 100,
                appWidget.minHeight + 200);

        myWidgetHostView.requestLayout();
    }

    // 添加选择的widget。需要判断其是否含有配置，如果有，需要首先进入配置

    private void addWidget(Intent data) {
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                -1);
        AppWidgetProviderInfo appWidget = manager.getAppWidgetInfo(appWidgetId);

        Log.d("AppWidget", "configure:" + appWidget.configure);

        if (appWidget.configure != null) {
            // 有配置，弹出配置
            Intent intent = new Intent(
                    AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidget.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            startActivityForResult(intent, REQUEST_CREATE_WIDGET);

        } else {
            // 没有配置，直接添加
            onActivityResult(REQUEST_CREATE_WIDGET, RESULT_OK, data);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_ADD_WIDGET:
                    addWidget(data);
                    break;
                case REQUEST_CREATE_WIDGET:
                    createWidget(data);
                    break;
                default:
                    break;
            }

        } else if (requestCode == REQUEST_CREATE_WIDGET
                && resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }
}