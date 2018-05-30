package com.flyme.systemui.colorful;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yujunqing on 2016/12/21.
 */

public class ColorfulHelper {


    public static void register(ColorImpl parent, View child){
        parent.register(child);
    }
}
