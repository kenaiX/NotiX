package cc.kenai.projectX.uimode

import android.app.Activity
import android.app.UiModeManager
import android.content.Context

class UIModeTestActivity : Activity() {
    override fun onResume() {
        super.onResume()
        (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).enableCarMode(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).disableCarMode(0)
    }

}