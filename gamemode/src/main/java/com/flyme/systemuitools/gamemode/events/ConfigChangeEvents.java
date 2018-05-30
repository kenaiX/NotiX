package com.flyme.systemuitools.gamemode.events;

import com.flyme.systemuitools.gamemode.model.AppInfo;

import java.util.List;

public class ConfigChangeEvents {
    public static class QuickAppsConfigChange {
        public AppInfo[] list;

        public QuickAppsConfigChange(AppInfo[] list) {
            this.list = list;
        }
    }
    public static class PhoneConfigChange{

    }
}
