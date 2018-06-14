package com.flyme.systemuitools.gamemode.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyme.systemuitools.R;

public class AppItemView extends RelativeLayout {

    public ImageView mIconView;
    TextView mTitleView;

    public AppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView) findViewById(R.id.title);
        mIconView = (ImageView) findViewById(R.id.icon);
    }

    void bindIcon(Drawable d) {
        mIconView.setImageDrawable(d);
    }

    void bindTitle(CharSequence s) {
        mTitleView.setText(s);
    }


}