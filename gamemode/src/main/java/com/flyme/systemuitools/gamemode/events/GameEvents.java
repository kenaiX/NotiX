package com.flyme.systemuitools.gamemode.events;

public class GameEvents {
    public static class PluginInstalled {
        public final boolean succeed;

        public PluginInstalled(boolean succeed) {
            this.succeed = succeed;
        }
    }
}
