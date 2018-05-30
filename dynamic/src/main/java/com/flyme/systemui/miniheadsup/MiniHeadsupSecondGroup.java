package com.flyme.systemui.miniheadsup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.flyme.systemui.dynamic.R;

public class MiniHeadsupSecondGroup extends FrameLayout {
    NinePatchDrawable mButtonBackground;
    ColorFilter defaultColorFilter = new PorterDuffColorFilter(Color.BLACK,
            PorterDuff.Mode.SRC_ATOP);

    public MiniHeadsupSecondGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mButtonBackground = (NinePatchDrawable) context.getDrawable(R.drawable.background);
        mButtonBackground.getPaint().setColor(Color.BLACK);
        mButtonBackground.getPaint().setFilterBitmap(true);
        mButtonBackground.getPaint().setColorFilter(defaultColorFilter);
    }

    public void setButtonColor(int color){
        mButtonBackground.getPaint().setColorFilter(new PorterDuffColorFilter(color,
                PorterDuff.Mode.SRC_ATOP));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mButtonBackground.setBounds(0,0,getWidth(),getHeight());
        mButtonBackground.draw(canvas);
        super.dispatchDraw(canvas);
    }
}
