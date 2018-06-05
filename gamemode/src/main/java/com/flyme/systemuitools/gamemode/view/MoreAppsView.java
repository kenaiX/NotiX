package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.flyme.systemuitools.R;
import com.flyme.systemuitools.gamemode.ProViewControler;
import com.flyme.systemuitools.gamemode.events.ClickEvents;
import com.flyme.systemuitools.gamemode.events.DragEvents;
import com.flyme.systemuitools.gamemode.model.AppInfo;
import com.flyme.systemuitools.gamemode.utils.Dragable;
import com.hwangjr.rxbus.RxBus;

import java.util.List;

public class MoreAppsView extends RelativeLayout implements Dragable {
    public MoreAppsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private  static int COLUMNS = 6;

    ListView mListView;
    View summaryView;


    AppInfo[] mApps;
    ListAdapter adapter = new ListAdapter();

    boolean mNeedSummary;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mListView = (ListView) findViewById(R.id.moreAppslist);
        summaryView = findViewById(R.id.moreapps_summary);
        mNeedSummary = getContext().getSharedPreferences("game_mode", Context.MODE_PRIVATE).getBoolean("need_moreapp_summary",true);
    }

    public void init(List<AppInfo> list,int everyLine) {
        mApps = new AppInfo[list.size() + QuickAppsView.NUM_APPS];
        COLUMNS = everyLine;
        for (int i = 0, n = list.size(); i < n; i++) {
            mApps[i] = list.get(i);
        }
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void setSummary(boolean show){
        if(show && mNeedSummary){
            summaryView.setVisibility(VISIBLE);
        }else{
            if(!show && mNeedSummary){
                getContext().getSharedPreferences("game_mode", Context.MODE_PRIVATE).edit().putBoolean("need_moreapp_summary",false).apply();
                mNeedSummary = false;
                //summaryView.setVisibility(INVISIBLE);
            }else {
                summaryView.setVisibility(GONE);
            }
        }
    }

    @Override
    public Rect canAccept() {
        int index = 0;
        for (int i = 0; i < mApps.length; i++) {
            if (mApps[i] == null) {
                index = i;
                break;
            }
        }

        boolean toStart = false;
        out : for (int i = 0, n = mListView.getChildCount(); i < n; i++) {
            ViewGroup line = (ViewGroup) mListView.getChildAt(i);
            for (int j = 0, m = line.getChildCount(); j < m; j++) {
                AppItemView child = (AppItemView) line.getChildAt(j);
                int id = child.getId();
                if (index < id) {
                    toStart = true;
                    break out;
                }
                if (id == index) {
                    int[] position = new int[2];
                    child.iconView.getLocationInWindow(position);
                    return new Rect(position[0], position[1], position[0] + child.iconView.getWidth(), position[1] + child.iconView.getHeight());
                }
            }
        }

        return new Rect(0,0,0,0);
    }

    @Override
    public boolean onAcceptedCompleted(MotionEvent event, AppInfo info) {
        int index = 0;
        AppInfo childInfo = new AppInfo(info);
        for (int i = 0; i < mApps.length; i++) {
            if (mApps[i] == null) {
                mApps[i] = childInfo;
                adapter.notifyDataSetChanged();
                index = i;
                break;
            }
        }

        for (int i = 0, n = mListView.getChildCount(); i < n; i++) {
            ViewGroup line = (ViewGroup) mListView.getChildAt(i);
            for (int j = 0, m = line.getChildCount(); j < m; j++) {
                AppItemView child = (AppItemView) line.getChildAt(j);
                if (child.getId() == index) {
                    child.setTag(childInfo);
                    child.bindIcon(childInfo.getIcon());
                    child.bindTitle(childInfo.getLabel());
                    return true;
                }
            }
        }
        return true;
    }

    private void addInfoToList(AppInfo childInfo) {
        for (int i = mApps.length - QuickAppsView.NUM_APPS; i < mApps.length; i++) {
            if (mApps[i] == null) {
                mApps[i] = childInfo;
                return;
            }
        }
    }

    private int getListRealSize() {
        for (int i = mApps.length - 1; i > -1; i--) {
            if (mApps[i] != null) {
                return i + 1;
            }
        }
        return 0;
    }

    @Override
    public void onRemovedCompleted(View originView) {
        AppInfo[] temp = new AppInfo[mApps.length];
        int index = 0;
        for(AppInfo info :mApps){
            if(info!=null){
                temp[index++] = info;
            }
        }
        mApps = temp;
        adapter.notifyDataSetChanged();
    }

    class ListAdapter extends BaseAdapter {


        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int getCount() {
            return (int) Math.ceil(getListRealSize() / (float) COLUMNS);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Line view;
            if (convertView == null) {
                view = new Line(getContext());
                if(ProViewControler.sLand) {
                    view.setMinimumHeight(mListView.getMeasuredHeight()/3);
                }else{
                    view.setMinimumHeight(mListView.getMeasuredHeight()/5);
                }
            } else {
                view = (Line) convertView;
            }
            view.bindInfo(mApps, position * COLUMNS, position * COLUMNS + COLUMNS);
            return view;
        }
    }

    class Line extends LinearLayout implements View.OnLongClickListener, View.OnClickListener {
        public Line(Context context) {
            super(context);
        }

        public void bindInfo(AppInfo[] list, int start, int stop) {
            if (getChildCount() == 0) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                for (int i = 0; i < MoreAppsView.COLUMNS; i++) {
                    AppItemView item = (AppItemView) inflater.inflate(R.layout.game_pro_moreapps_item_layout, null);
                    item.setOnClickListener(this);
                    item.setOnLongClickListener(this);
                    LayoutParams layout = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    layout.weight = 1f;
                    addView(item, layout);
                }
            }

            for (int i = 0, n = getChildCount(); i < n; i++) {
                AppItemView item = (AppItemView) getChildAt(i);
                if (getListRealSize() <= i + start) {
                    item.setTag(null);
                    item.bindIcon(null);
                    item.bindTitle(null);
                } else {
                    AppInfo appInfo = list[i + start];
                    if (appInfo != null) {
                        item.setTag(appInfo);
                        item.bindIcon(appInfo.getIcon());
                        item.bindTitle(appInfo.getLabel());
                    } else {
                        item.setTag(null);
                        item.bindIcon(null);
                        item.bindTitle(null);
                    }
                    item.setId(i + start);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                RxBus.get().post(new ClickEvents.OnAppsClick((AppInfo) (v.getTag())));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getTag() != null) {
                AppItemView appView = (AppItemView) v;

                RxBus.get().post(new DragEvents.StartDrag(appView, (AppInfo) v.getTag(), MoreAppsView.this));

                mApps[appView.getId()] = null;

                appView.setTag(null);
                appView.bindTitle(null);
                appView.bindIcon(null);
                return true;
            }
            return false;
        }
    }
}



