package com.flyme.systemui.colorful;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flyme.systemui.dynamic.R;

public class ColorfulDemo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colorful_main);
        bindViews();
    }

    public void startAnimation(View view) {
        mTv.setTranslationX(0);
        mTv.setAlpha(1f);
        mTv.animate().alpha(0f).translationX(500).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
        }).start();

    }

    // Content View Elements

    private ColorfulFrameLayout mFrame;
    private TextView mTv;

    // End Of Content View Elements

    private void bindViews() {

        mFrame = (ColorfulFrameLayout) findViewById(R.id.frame);
        mTv = (TextView) findViewById(R.id.tv);

        //ColorfulHelper.register(mFrame,mTv);
    }

}
