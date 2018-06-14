package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flyme.systemuitools.R;

public class RedPointFrameLayout extends FrameLayout {
    TextView mText;
    View mRedPoint;

    public RedPointFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void toggleGameRedPoint(boolean show) {
        mRedPoint.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void setTitle(String s) {
        mText.setText(s);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mText = (TextView) findViewById(R.id.text);
        mRedPoint = findViewById(R.id.redpoint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int textLeft = (getMeasuredWidth() - mText.getMeasuredWidth()) / 2;
        int textTop = (getMeasuredHeight() - mText.getMeasuredHeight()) / 2;
        int textRight = textLeft + mText.getMeasuredWidth();
        int textBottom = textTop + mText.getMeasuredHeight();

        mText.layout(textLeft, textTop, textRight, textBottom);

        mRedPoint.layout(textRight, textTop - mRedPoint.getMeasuredHeight(), textRight + mRedPoint.getMeasuredWidth(), textTop);
    }
}
