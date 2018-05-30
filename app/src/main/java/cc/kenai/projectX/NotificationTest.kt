package cc.kenai.projectX

import android.app.*
import android.os.Bundle
import android.widget.Button
import android.content.Context.NOTIFICATION_SERVICE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast


/**
 * Created by yujunqing on 2017/10/28.
 */
class NotificationTest : Activity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val button = Button(this)
        setContentView(button)

        button.setOnClickListener {

            val intent = Intent()
            intent.putExtra("MainUI_User_Last_Msg_Type", 436207665)
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val builder = Notification.Builder(this@NotificationTest)
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("红包测试")
                    .setContentText("[微信红包]")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this@NotificationTest, 0, intent, 0))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
            val notification = builder.build()
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancelAll()
            manager.notify((Math.random() * 1000).toInt(), notification)

        }

        /*registerReceiver(object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val replyBundle = RemoteInput.getResultsFromIntent(intent)
                if (replyBundle != null) {
                    // 根据key拿到回复的内容
                    val reply = replyBundle!!.getString("test_wechat_key")
                    Toast.makeText(baseContext,reply,Toast.LENGTH_SHORT).show()
                }
            }

        }, IntentFilter("test_wechat_replay"))*/
    }
}