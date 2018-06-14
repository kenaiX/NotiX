package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyme.systemuitools.gamemode.events.UIEvents;
import com.hwangjr.rxbus.RxBus;

public class GameModeWindow extends RelativeLayout {

    public GameModeWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.get().post(new UIEvents.CloseProView());
            }
        });
    }
}
