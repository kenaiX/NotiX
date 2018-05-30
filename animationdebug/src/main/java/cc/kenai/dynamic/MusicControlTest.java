package cc.kenai.dynamic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by yujunqing on 2017/12/19.
 */

public class MusicControlTest {
    public static void inject(Context context){
        Toast.makeText(context,"load",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
            intent.setAction(XIAMI_MUSIC_PAUSE_ACTION);//暂停
            Log.i("@@@@","xiami MusicActions...暂停/播放");
        intent.setComponent(new ComponentName("fm.xiami.main","fm.xiami.main.service.PlayService"));
        context.startService(intent);
    }


    //虾米音乐　下一首歌的广播
    public final static String XIAMI_MUSIC_NEXT_ACTION = "fm.xiami.main.business.notification.ACTION_NOTIFICATION_PLAYNEXT";
    //虾米音乐　暂停/播放　广播
    public final static String XIAMI_MUSIC_PAUSE_ACTION = "fm.xiami.main.business.notification.ACTION_NOTIFICATION_PLAYPAUSE";
    //虾米音乐　上一首歌的广播
    public final static String XIAMI_MUSIC_PREVIOUS_ACTION = "fm.xiami.main.business.notification.ACTION_NOTIFICATION_PLAYPREV";
    //虾米音乐　桌面歌词的广播
    public final static String XIAMI_MUSIC_DESKTOP_LYRIC_ACTION = "fm.xiami.main.business.notification.ACTION_NOTIFICATION_DESKTOP_LYRIC_TOGGLE";
    //虾米音乐　关闭通知的广播
    public final static String XIAMI_MUSIC_NOTIFICATION_CLOSE_ACTION = "fm.xiami.main.business.notification.ACTION_NOTIFICATION_CLOSE";

    /*private void xiamiMusicActions(String action) {
        Intent intent = new Intent();
        if (TextUtils.equals(action, PLAY_OR_PAUSE)) {
            intent.setAction(XIAMI_MUSIC_PAUSE_ACTION);//暂停
            Log.i("@@@@","xiami MusicActions...暂停/播放");
        } else if (TextUtils.equals(action, NEXT)) {
            intent.setAction(XIAMI_MUSIC_NEXT_ACTION);//下一首歌
        } else if (TextUtils.equals(action, PREVIOUS)) {
            intent.setAction(XIAMI_MUSIC_PREVIOUS_ACTION);//上一首歌
        }
        intent.setComponent(new ComponentName("fm.xiami.main","fm.xiami.main.service.PlayService"));
        mContext.startService(intent);
    }*/

}
