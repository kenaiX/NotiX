package cc.kenai.dear

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import cc.kenai.dear.model.Record

class DearActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.text)
                .setText((applicationContext as DearApplication).dearServiceCount.toString())
    }
}
