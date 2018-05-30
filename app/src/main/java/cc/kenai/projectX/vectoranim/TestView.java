package cc.kenai.projectX.vectoranim;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


public class TestView extends View{
    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    AnimatedVectorDrawable b1,b2,f1,f2;

    void startAnim(){

    }

    void setupQs(Drawable... d){
        dealDrawableForXmode(b1);
        dealDrawableForXmode(f1);
    }

    void dealDrawableForXmode(Drawable drawable){
        ((VectorDrawable)drawable).setTintMode(PorterDuff.Mode.DST_OUT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        b1.draw(canvas);
        b2.draw(canvas);
        b1.draw(canvas);
        f2.draw(canvas);
    }
}
