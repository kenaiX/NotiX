package com.flyme.systemui.dynamic;

import android.app.Notification;

import java.util.ArrayList;

public class DynamicNotificationBuilder {
    public final static long TYPE_ANIMATION = 0;
    public final static long TYPE_DRAWABLE = 1;
    public final static long TYPE_ANIMATOR = 2;
    private final static String DYNAMIC_FLAG = "dynamic_notification";

    private Notification noti;
    private ArrayList<AnimationItem> headsupAnimations = new ArrayList<>();
    private ArrayList<AnimationItem> bigAnimations = new ArrayList<>();
    private ArrayList<AnimationItem> normalAnimations = new ArrayList<>();

    public DynamicNotificationBuilder(Notification noti) {
        this.noti = noti;
    }

    public Notification build() {
        if (noti == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean needSplit = false;
        if (headsupAnimations.size() > 0) {
            needSplit = true;
            sb.append("'headsupcontent':{'anim':[");
            for (int i = 0, n = headsupAnimations.size(); i < n; i++) {
                AnimationItem item = headsupAnimations.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(item.type).append(",").append(item.viewId).append(",").append(item.animId);
            }
            sb.append("]}");
        }

        if (bigAnimations.size() > 0) {
            if (needSplit) {
                sb.append(",");
            } else {
                needSplit = true;
            }
            sb.append("'bigcontent':{'anim':[");
            for (int i = 0, n = bigAnimations.size(); i < n; i++) {
                AnimationItem item = bigAnimations.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(item.type).append(",").append(item.viewId).append(",").append(item.animId);
            }
            sb.append("]}");
        }

        if (normalAnimations.size() > 0) {
            if (needSplit) {
                sb.append(",");
            }
            sb.append("'content':{'anim':[");
            for (int i = 0, n = normalAnimations.size(); i < n; i++) {
                AnimationItem item = normalAnimations.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(item.type).append(",").append(item.viewId).append(",").append(item.animId);
            }
            sb.append("]}");
        }

        sb.append("}");
        noti.extras.putString(DYNAMIC_FLAG, sb.toString());

        return noti;
    }

    public DynamicNotificationBuilder appendHeadsupAnim(AnimationItem anim) {
        headsupAnimations.add(anim);
        return this;
    }

    public DynamicNotificationBuilder appendBigAnim(AnimationItem anim) {
        bigAnimations.add(anim);
        return this;
    }

    public DynamicNotificationBuilder appendNormalAnim(AnimationItem anim) {
        normalAnimations.add(anim);
        return this;
    }

    public static class AnimationItem {
        public final long type;
        public final long viewId;
        public final long animId;

        public AnimationItem(long type, long viewId, long animId) {
            this(type, viewId, animId, false);
        }

        public AnimationItem(long type, long viewId, long animId, boolean repeat) {
            this.type = repeat ? type + (1 << 31) : type;
            this.viewId = viewId;
            this.animId = animId;
        }
    }
}
