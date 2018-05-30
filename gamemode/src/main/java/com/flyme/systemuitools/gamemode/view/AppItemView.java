package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyme.systemuitools.R;

public class AppItemView extends RelativeLayout {

    public AppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    TextView titleView;
    public ImageView iconView;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleView = (TextView) findViewById(R.id.title);
        iconView = (ImageView) findViewById(R.id.icon);
    }

    void bindIcon(Drawable d) {
        iconView.setImageDrawable(d);
    }

    void bindTitle(CharSequence s) {
        titleView.setText(s);
    }


}