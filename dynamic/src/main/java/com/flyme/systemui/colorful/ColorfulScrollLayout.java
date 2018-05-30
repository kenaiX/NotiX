package com.flyme.systemui.colorful;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;


public class ColorfulScrollLayout extends ScrollView{
    public ColorfulScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setClipBounds(new Rect(0,0,200,300));
    }
    private static Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

}
