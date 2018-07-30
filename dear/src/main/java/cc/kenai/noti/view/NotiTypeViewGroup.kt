package cc.kenai.noti.view

import android.content.Context
import android.support.v7.widget.AppCompatCheckBox
import android.util.AttributeSet
import android.widget.LinearLayout
import cc.kenai.noti.R

class NotiTypeViewGroup(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    lateinit var mNotiCheckBox: AppCompatCheckBox
    lateinit var mLoopCheckBox: AppCompatCheckBox
    lateinit var mRingCheckBox: AppCompatCheckBox
    lateinit var mScreenCheckBox: AppCompatCheckBox

    override fun onFinishInflate() {
        super.onFinishInflate()
        mNotiCheckBox = findViewById(R.id.notitype_noti)
        mLoopCheckBox = findViewById(R.id.notitype_loop)
        mRingCheckBox = findViewById(R.id.notitype_ring)
        mScreenCheckBox = findViewById(R.id.notitype_screen)
    }

    fun setup(type: String) {
        mNotiCheckBox.isChecked = type.contains("noti")
        mLoopCheckBox.isChecked = type.contains("loop")
        mRingCheckBox.isChecked = type.contains("ring")
        mScreenCheckBox.isChecked = type.contains("screen")
    }

    fun getType(): String {
        val sb = StringBuilder()
        if (mNotiCheckBox.isChecked) {
            sb.append("noti|")
        }
        if (mLoopCheckBox.isChecked) {
            sb.append("loop|")
        }
        if (mRingCheckBox.isChecked) {
            sb.append("ring|")
        }
        if (mScreenCheckBox.isChecked) {
            sb.append("screen|")
        }
        if (sb.length > 0) {
            sb.delete(sb.length - 1, sb.length)
        }
        return sb.toString()
    }


}