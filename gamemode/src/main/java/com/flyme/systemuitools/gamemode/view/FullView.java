package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class FullView extends FrameLayout {

    Callback mCallback;

    public interface Callback {
        void onVisibilityChanged(boolean show);
    }

    public FullView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVisibilityChangedCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility == VISIBLE) {
            mCallback.onVisibilityChanged(true);
        } else {
            mCallback.onVisibilityChanged(false);
        }
    }
}
