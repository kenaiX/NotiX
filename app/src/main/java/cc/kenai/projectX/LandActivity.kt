package cc.kenai.projectX

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

/**
 * Created by yujunqing on 2017/10/20.
 */
class LandActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val it = Intent(this, Class.forName("cc.kenai.projectX.LandTestService"))
        startService(it)

        Handler().postDelayed({ finish() }, 1000)


    }
}