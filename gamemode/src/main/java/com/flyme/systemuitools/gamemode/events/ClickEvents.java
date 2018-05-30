package com.flyme.systemuitools.gamemode.events;

import com.flyme.systemuitools.gamemode.model.AppInfo;

public class ClickEvents {
    public static class OnAppsClick {
        public AppInfo info;

        public OnAppsClick(AppInfo info) {
            this.info = info;
        }
    }
}
