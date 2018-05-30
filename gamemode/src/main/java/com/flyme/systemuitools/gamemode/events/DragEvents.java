package com.flyme.systemuitools.gamemode.events;

import android.view.View;

import com.flyme.systemuitools.gamemode.model.AppInfo;
import com.flyme.systemuitools.gamemode.utils.Dragable;

public class DragEvents {

    public static class StartDrag {
        public View dragView;
        public AppInfo dragInfo;
        public Dragable parent;

        public StartDrag(View dragView, AppInfo dragInfo, Dragable parent) {
            this.dragView = dragView;
            this.parent = parent;
            this.dragInfo = dragInfo;
        }
    }

    public static class StopDragCompleted {
    }
}
