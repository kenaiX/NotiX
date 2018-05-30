package cc.kenai.projectX.drivemode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cc.kenai.projectX.R;

public class DriveModeTest extends Activity{
    Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        Button button = new Button(this);
        button.setText("test dialog");
        setContentView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });
    }
    AlertDialog alertDialog;

    void showPop(){
        View contentView = View.inflate(mContext, R.layout.drivemode_popwindow, null);
        LinearLayout listLayout = (LinearLayout) contentView.findViewById(R.id.drive_mode_list);


        ImageView mPopWindow = new ImageView(mContext);
        mPopWindow.setImageResource(R.mipmap.ic_launcher);

        listLayout.addView(mPopWindow);

        mPopWindow = new ImageView(mContext);
        mPopWindow.setImageResource(R.mipmap.ic_launcher);
        listLayout.addView(mPopWindow);

        int height = 600;

        FrameLayout fm = new FrameLayout(mContext);
        fm.setBackgroundColor(Color.GRAY);
        fm.addView(new ImageView(mContext), ViewGroup.LayoutParams.MATCH_PARENT, height);
        fm.setPadding(54, 36, 54, 36);

        alertDialog = new AlertDialog.Builder(mContext,R.style.dirvemode_dialog).create();
        alertDialog.setView(fm, 0, 0, 0, 0);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //mPopWindow = null;
            }
        });
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
            //if (!isLandscape) {
                lp.gravity = Gravity.BOTTOM;
                lp.y = 12*3;
            //}
            lp.width = 340*3;
        alertDialog.getWindow().setAttributes(lp);
    }
    void hidePop(){

    }
}
