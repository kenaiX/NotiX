package com.flyme.systemui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flyme.systemui.dynamic.R;

import java.lang.ref.WeakReference;
import java.util.List;


public class MiscTest extends Activity {

    @Override
    protected void onResume() {
        super.onResume();


        final TextView tx = new TextView(this);
        tx.setText("test");

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
