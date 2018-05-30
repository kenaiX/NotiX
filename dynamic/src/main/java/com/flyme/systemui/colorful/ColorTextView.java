package com.flyme.systemui.colorful;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yujunqing on 2016/12/21.
 */

public class ColorTextView extends TextView {
    private static Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    public ColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint p = getPaint();
        p.setXfermode(sXfermode);
        super.draw(canvas);
    }
}
