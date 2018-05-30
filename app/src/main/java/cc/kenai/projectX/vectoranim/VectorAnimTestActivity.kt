package cc.kenai.projectX.vectoranim

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.Canvas.FULL_COLOR_LAYER_SAVE_FLAG
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import cc.kenai.projectX.R
import com.airbnb.lottie.LottieAnimationView

class VectorAnimTestActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val main = FrameLayout(this)
        main.setBackgroundResource(R.mipmap.screenshot)

        val stage = StageFrameLayout(this)

        val parent = Colorfulcontainer(this)

        val im = ImageView(this)
        val drawable = resources.getDrawable(R.drawable.guide_test) as AnimatedVectorDrawable;
        drawable.setTint(Color.WHITE)

        var a :VectorDrawable;

        //im.invalidate()

        im.setImageDrawable(drawable)
        drawable.start()

        drawable.level = 10000

        /*val im = LottieAnimationView(this)
        im.setAnimation("rorate.json");
        im.loop(false);

        val im2 = LottieAnimationView(this)
        im2.setAnimation("rorate2.json");
        im2.loop(false);

        val animator = ObjectAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { valueAnimator ->
            val f = valueAnimator.animatedValue as Float

            im.progress = f
            im2.progress = f
        }
        animator.duration = 3000
        animator.repeatCount = -1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()*/

        /*im.setOnClickListener {
            im.playAnimation(); }*/


        main.addView(stage, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        stage.addView(parent)

        addColorfulChild(stage, im)
        //addColorfulChild(stage,im2)

        //parent.addView(im)
        setContentView(main)

    }

    class Test(context: Context):LottieAnimationView(context){
        override fun draw(canvas: Canvas?) {
            canvas!!.saveLayer(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(),null)
            super.draw(canvas)
            canvas.restore()
        }
    }


    fun addColorfulChild(parent: ViewGroup, child: View) {
        val container = Colorfulcontainer(parent.context)
        container.addView(child)
        parent.addView(container)
    }

    class Colorfulcontainer(context: Context) : FrameLayout(context) {
        val mode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        /*override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
            val paint = Paint()
            paint.xfermode = mode
            canvas!!.saveLayer(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)
            val result = super.drawChild(canvas, child, drawingTime)
            canvas.restore()
            return result
        }*/
    }

    class StageFrameLayout(context: Context) : FrameLayout(context) {


        override fun dispatchDraw(canvas: Canvas?) {
            canvas!!.saveLayer(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), null)
            //设置蒙板颜色
            canvas.drawARGB(120, 0, 0, 0)


            //--------------------------------------------------------------------------------------
            val paint = Paint()
            paint.xfermode = mode
            canvas!!.saveLayer(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(),paint)

            super.dispatchDraw(canvas)

//            //测试效果用
            /*val ovalPaint = Paint()
            ovalPaint.color = Color.argb(220,255,255,255)
            canvas.drawOval(200f,200f,600f,600f,ovalPaint)
            canvas.drawBitmap(BitmapFactory.decodeResource(context.resources,R.mipmap.ic_launcher),0f,0f,null)*/
//
//            val ovalPaint2 = Paint()
//            ovalPaint2.color = Color.argb(230,255,255,255)
//            canvas.drawOval(200f,700f,600f,1100f,ovalPaint2)
//
//            val ovalPaint3 = Paint()
//            ovalPaint3.color = Color.argb(240,255,255,255)
//            canvas.drawOval(200f,1200f,600f,1600f,ovalPaint3)


            canvas.restore()
            //--------------------------------------------------------------------------------------

//            //测试效果用
//            ovalPaint.color = Color.argb(180,255,255,255)
//            canvas.drawOval(200f,200f,600f,600f,ovalPaint)
//
//            ovalPaint2.color = Color.argb(180,255,255,255)
//            canvas.drawOval(200f,700f,600f,1100f,ovalPaint2)
//
//            ovalPaint3.color = Color.argb(180,255,255,255)
//            canvas.drawOval(200f,1200f,600f,1600f,ovalPaint3)
            //super.dispatchDraw(canvas)
            canvas.restore()
        }

        val mode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        //val mode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        /*override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
            *//*canvas!!.saveLayer(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(),null)
            super.drawChild(canvas, child, drawingTime)
            canvas.restore()
            val ovalPaint3 = Paint()
            ovalPaint3.color = Color.argb(50,20,50,180)
            canvas.drawOval(200f,1200f,600f,1600f,ovalPaint3)*//*

            canvas!!.saveLayer(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), null)
            //设置蒙板颜色
            canvas.drawARGB(255, 0, 0, 0)


            //--------------------------------------------------------------------------------------
            val paint = Paint()
            paint.xfermode = mode
            //canvas!!.saveLayer(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(),paint)

            super.drawChild(canvas, child, drawingTime)

//            //测试效果用
            val ovalPaint = Paint()
            ovalPaint.color = Color.argb(220,255,255,255)
            canvas.drawOval(200f,200f,600f,600f,ovalPaint)
            canvas.drawBitmap(BitmapFactory.decodeResource(context.resources,R.mipmap.ic_launcher),0f,0f,null)
//
//            val ovalPaint2 = Paint()
//            ovalPaint2.color = Color.argb(230,255,255,255)
//            canvas.drawOval(200f,700f,600f,1100f,ovalPaint2)
//
//            val ovalPaint3 = Paint()
//            ovalPaint3.color = Color.argb(240,255,255,255)
//            canvas.drawOval(200f,1200f,600f,1600f,ovalPaint3)


            //canvas.restore()
            //--------------------------------------------------------------------------------------

            //super.drawChild(canvas, child, drawingTime)

//            //测试效果用
//            ovalPaint.color = Color.argb(180,255,255,255)
//            canvas.drawOval(200f,200f,600f,600f,ovalPaint)
//
//            ovalPaint2.color = Color.argb(180,255,255,255)
//            canvas.drawOval(200f,700f,600f,1100f,ovalPaint2)
//
//            ovalPaint3.color = Color.argb(180,255,255,255)
//            canvas.drawOval(200f,1200f,600f,1600f,ovalPaint3)

            canvas.restore()
            return true
        }*/

        fun test(canvas:Canvas){
            //--------------------------------------------------------------------------------------
            val paint = Paint()
            paint.color = Color.argb(255,20,50,180)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            canvas!!.saveLayer(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(),paint)

            //super.drawChild(canvas, child, drawingTime)

            //测试效果用
            val ovalPaint = Paint()
            ovalPaint.color = Color.BLUE
            canvas.drawOval(600f,200f,1000f,600f,ovalPaint)

            val ovalPaint2 = Paint()
            ovalPaint2.color = Color.BLUE
            canvas.drawOval(600f,700f,1000f,1100f,ovalPaint2)

            val ovalPaint3 = Paint()
            ovalPaint3.color =Color.BLUE
            canvas.drawOval(600f,1200f,1000f,1600f,ovalPaint3)


            canvas.restore()
            //--------------------------------------------------------------------------------------
        }
    }


}