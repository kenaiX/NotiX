package com.flyme.systemui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flyme.systemui.dynamic.R;

import java.lang.ref.WeakReference;
import java.util.List;


public class MiscTest extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();


        final TextView tx = new TextView(this);
        tx.setText("test");


        getWindow().getDecorView().setBackgroundColor(Color.rgb(230,230,230));

        setContentView(tx);

        final Animator animator = AnimatorInflater.loadAnimator(this, R.animator.dynamic_headsup_on_answer_test);
        animator.setTarget(tx);
        final AnimatorListenerAdapter listenerAdapter = new AnimatorListenerAdapter() {
            final WeakReference<View> target = new WeakReference<View>(tx);
            boolean isCancel = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                isCancel = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isCancel && target.get() != null && target.get().isShown()) {
                    Animator a = AnimatorInflater.loadAnimator(getBaseContext(), R.animator.dynamic_headsup_on_answer_test);
                    a.setTarget(target.get());
                    a.addListener(this);
                    a.start();
                    Log.w("@@@@", "restart animator");
                }
            }
        };
        animator.addListener(listenerAdapter);
        animator.start();



    }
}
