package com.flyme.systemuitools.gamemode.events;

import com.flyme.systemuitools.gamemode.model.AppInfo;

import java.util.List;

public class ConfigChangeEvents {
    public static class OnSaveQuickAppsConfig {
        public AppInfo[] list;

        public OnSaveQuickAppsConfig(AppInfo[] list) {
            this.list = list;
        }
    }

    public static class OnQuickAppsConfigChanged {
    }

    public static class PhoneConfigChange{

    }
}
