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
import android.widget.TextView;

import java.util.ArrayList;


public class ColorfulFrameLayout extends FrameLayout implements ColorImpl{
    public ColorfulFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private static Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawColor(0xffff0000);
        canvas.saveLayer(0,0,getWidth(),getHeight(),null,Canvas.ALL_SAVE_FLAG);
        canvas.drawColor(0xff000000);

        for(View v:colorfulList){
            Paint p = ((TextView)v).getPaint();
            p.setXfermode(sXfermode);
            v.draw(canvas);
        }


        super.dispatchDraw(canvas);
        canvas.restore();
    }

    ArrayList<View> colorfulList = new ArrayList<>();
    @Override
    public void register(View child) {
        colorfulList.add(child);
    }
}
