package com.flyme.systemui.dynamic;

public class DynamicNotificationModel {
    private DynamicContent content;
    private DynamicContent bigcontent;
    private DynamicContent headsupcontent;

    public DynamicContent getContent() {
        return content;
    }

    public void setContent(DynamicContent content) {
        this.content = content;
    }

    public DynamicContent getBigcontent() {
        return bigcontent;
    }

    public void setBigcontent(DynamicContent bigcontent) {
        this.bigcontent = bigcontent;
    }

    public DynamicContent getHeadsupcontent() {
        return headsupcontent;
    }

    public void setHeadsupcontent(DynamicContent headsupcontent) {
        this.headsupcontent = headsupcontent;
    }

    public static class DynamicContent {
        private int[] anim;
        private String[] banner;

        public int[] getAnim() {
            return anim;
        }

        public void setAnim(int[] anim) {
            this.anim = anim;
        }

        public String[] getBanner() {
            return banner;
        }

        public void setBanner(String[] banner) {
            this.banner = banner;
        }
    }
}
