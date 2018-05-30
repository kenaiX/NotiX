package com.example.flymeicon.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class BimapUtils {
    //需要测试一下是否需要动态创建
    private static final PorterDuffXfermode sFlymeIconMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private static final Paint sFlymeIconPaint = new Paint();

    static {
        sFlymeIconPaint.setAntiAlias(true);
        sFlymeIconPaint.setColor(Color.RED);
    }

    public static final Bitmap toRound(Bitmap src, int padding, float round) {
        if (src.getPixel(padding + 1, padding + 1) == Color.TRANSPARENT) {
            Log.d("@@@@", "no need to round");
            return src;
        } else {
            long start = System.currentTimeMillis();
            Bitmap dst = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
            //把直角图处理成圆角
            Canvas cavans = new Canvas(dst);
            RectF rect = new RectF(padding, padding, dst.getWidth() - padding, dst.getHeight() - padding);
            sFlymeIconPaint.setXfermode(null);
            cavans.drawRoundRect(rect, round, round, sFlymeIconPaint);
            sFlymeIconPaint.setXfermode(sFlymeIconMode);
            cavans.drawBitmap(src, 0f, 0f, sFlymeIconPaint);
            src.recycle();
            Log.d("@@@@", "round spend time = " + (System.currentTimeMillis() - start));
            return dst;
        }
    }
}
