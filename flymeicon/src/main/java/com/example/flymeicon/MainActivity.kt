package com.example.flymeicon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView

import java.io.File
import android.R.attr.bitmap
import android.graphics.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    internal var path = "/sdcard/Customize/.FlymeIcon"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frame = findViewById<ViewGroup>(R.id.frame)

        val imageView = ImageView(this)
        val imageView2 = ImageView(this)

        frame.addView(imageView)
        frame.addView(imageView2)
        frame.setBackgroundColor(Color.BLACK)

        Thread({
            deal(null, null)
            finish()
        }).start()

        deal(imageView, imageView2)
    }

    fun deal(im1: ImageView?, im2: ImageView?) {
        val f = File(path)
        val files = f.listFiles()
        for (i in 0..files.size - 1) {
            val bitmap = BitmapFactory.decodeFile(files[i].toString())
            //im1.setImageBitmap(bitmap)
            var out = dealWithBitmap(bitmap);
            //im2.setImageBitmap(out)
            saveMyBitmap(files[i].name, out)
        }
    }

    fun dealWithBitmap(bitmap: Bitmap): Bitmap {
        //生成直角图
        val copy = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        var cavans = Canvas(copy);
        val matrix = Matrix()
        matrix.setScale(1.1f, 1.1f, (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat());
        cavans.drawBitmap(bitmap, matrix, null)
        cavans.drawBitmap(bitmap, 0f, 0f, null)

        val result = Bitmap.createBitmap((bitmap.width * 0.75f).toInt(), (bitmap.height * 0.75f).toInt(), bitmap.config)
        cavans = Canvas(result);

        val color = Color.BLACK
        val paint = Paint()
        val rect = Rect(8, 8, result.width - 8, result.height - 8)
        val rectF = RectF(rect)
        paint.setAntiAlias(true)
        paint.setColor(color)
        cavans.drawRoundRect(rectF, 4f, 4f, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))

        matrix.setScale(0.75f, 0.75f);
        cavans.drawBitmap(copy, matrix, paint)


        return result
    }

    fun saveMyBitmap(bitName: String, mBitmap: Bitmap) {
        val f = File("/sdcard/zzz2/$bitName.png")
        try {
            f.createNewFile()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            //            DebugMessage.put("在保存图片时出错："+e.toString());
        }

        var fOut: FileOutputStream? = null
        try {
            fOut = FileOutputStream(f)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
        try {
            fOut!!.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            fOut!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}
