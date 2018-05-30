package cc.kenai.projectX.multi

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_TASK_ON_HOME
import android.os.Bundle
import android.widget.Toast

/**
 * Created by yujunqing on 2017/10/21.
 */
class MultiActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val it = Intent()
        it.setComponent(ComponentName("com.android.settings", "com.android.settings.Settings"))
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        application.startActivity(it)
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this, "test1", Toast.LENGTH_SHORT).show()
    }
}

class MultiActivity2 : Activity() {

    override fun onResume() {
        super.onResume()
        Toast.makeText(this, "test2", Toast.LENGTH_SHORT).show()
    }
}

class MultiActivity3 : Activity() {
}

class MultiActivity4 : Activity() {
}

class MultiActivity5 : Activity() {
}

class MultiActivity6 : Activity() {
}

class MultiActivity7 : Activity() {
}

class MultiActivity8 : Activity() {
}

class MultiActivity9 : Activity() {
}

class MultiActivity10 : Activity() {
}
