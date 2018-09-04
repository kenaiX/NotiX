package com.example.recentscardtest

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.view.animation.PathInterpolator


class RecentsView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    val AFFILIATION_OFFSET = 0.295f//卡片间相对距离，可以控制显示卡片的个数
    var FITST_CARD = 0f
    val FITST_CARD_OFFSET = 0.07f
    val INIT_OFFSET_ONE_CARD = -0.13f
    val INIT_OFFSET_MULTI_CARD = 0.02f

    val mCard = Array(10, { ImageView(context) })
    val mPosition = Array(10, { AFFILIATION_OFFSET * it })
    var mWidth: Int = 0
    val mOriginRect = RectF()
    val scale = 0.64f
    var mDownP = 0f
    var mCurP = 0f
    val margeLeft = 40

    val screens = arrayOf(R.drawable.screenshot_2, R.drawable.screenshot_3, R.drawable.screenshot_4, R.drawable.screenshot_5)

    val interpolatorForCard: PathInterpolator

    init {
        var index = 0
        mCard.forEach {
            it.scaleX = scale
            it.scaleY = scale
            it.scaleType = ImageView.ScaleType.FIT_XY
            it.setImageResource(screens[index++ % 4])

            val mViewBounds = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, 24 * 3f / scale)
                }
            }
            it.outlineProvider = mViewBounds
            it.clipToOutline = true
            it.translationY = -21f

            addView(it)
        }

        val path = SvgPathParser().parsePath("M0,0 C0.153709311,0.0320159124 0.252385836,0.0519739561 0.296029576,0.059874131 C0.39660517,0.07807983 0.493718716,0.109928396 0.597149439,0.202892835 C0.700580162,0.295857275 0.832764483,0.5 0.892745035,0.694055608 C0.952725588,0.888111216 0.962279666,0.912615252 1,1")
        interpolatorForCard = PathInterpolator(path)

        background = resources.getDrawable(R.drawable.sc)
        //background = WallpaperManager.getInstance(context).drawable
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                animator?.cancel()
                mDownP = screenXToCurveProgressFake(event.x.toInt())
            }
            MotionEvent.ACTION_MOVE -> {
                val p = screenXToCurveProgressFake(event.x.toInt())
                updateCardPosition(mCurP + mDownP - p)
            }
            MotionEvent.ACTION_UP -> {
                val p = screenXToCurveProgressFake(event.x.toInt())
                mCurP += mDownP - p

                val toP = calP(mCurP)
                val ani = ObjectAnimator.ofFloat(mCurP, toP).setDuration(200 + 200 * Math.abs(toP - mCurP).toLong())
                ani.addUpdateListener {
                    mCurP = it.animatedValue as Float
                    updateCardPosition(mCurP)
                }
                ani.start()
                animator = ani
            }
        }
        return true
    }

    fun calP(p:Float):Float{
       return (p /AFFILIATION_OFFSET).toInt()*AFFILIATION_OFFSET
    }

    var animator: Animator? = null


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mOriginRect.set(w * (0.5f - scale / 2), h * (0.5f - scale / 2), w * (0.5f + scale / 2), h * (0.5f + scale / 2))
        updateCardPosition(0f)
    }

    fun updateCardPosition(position: Float) {
        for (i in mCard.indices) {
            val offset = mPosition[i] - position
            if (offset >= 0 - AFFILIATION_OFFSET && offset < 1f) {
                mCard[i].visibility = View.VISIBLE
                val toScale = scale * curveProgressToScale(offset)
                mCard[i].scaleX = toScale
                mCard[i].scaleY = toScale


                val to = curveProgressToScreenX(offset) + margeLeft + (toScale - scale) / 2 * mWidth
                mCard[i].translationX = to - mOriginRect.left
                mCard[i].translationZ = curveProgressToScreenZ(offset)

                //mCard[i].alpha = 0.5f
            } else {
                mCard[i].visibility = View.INVISIBLE
            }
        }
    }


    var interpolatorForZ = PathInterpolator(0.7f, 0.2f, 0f, 1f)
    fun curveProgressToScreenZ(p: Float): Float {
        var p = p
        if (p < 0) {
            p = 0f
        } else if (p > 1) {
            p = 1f
        }

        val minZ = 16f * 3
        val maxZ = 20f * 3
        return Math.max(minZ, minZ + interpolatorForZ.getInterpolation(p) * (maxZ - minZ))
    }

    fun curveProgressToScale(p: Float): Float {
        var p = p
        if (p > 1) p = 1f

        val del = p - AFFILIATION_OFFSET * 2
        val compare = java.lang.Float.compare(del, 0f)
        return if (compare == 0) {
            1f
        } else {
            1f + del * 0.07f
        }
    }

    fun curveProgressToScreenX(p: Float): Float {
        var p = p
        val real: Float
        if (p < 0) {
            p = 0f
        } else if (p > 1) {
            p = 1f
        }
        real = ProgressToReal(p)
        return real * mWidth
    }

    var testInterpplater = PathInterpolator(1f, 0f, 0.80f, 0.94f)//for land

    fun ProgressToReal(p: Float): Float {
        return interpolatorForCard.getInterpolation(p)
    }

    fun screenXToCurveProgressFake(screenX: Int): Float {
        val real = (screenX - 0).toFloat() / mWidth
        return real * 0.50f
    }
}