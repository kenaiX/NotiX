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


class RoundRectXhdpi : AppCompatActivity() {

    internal var path = "/sdcard/Customize/.FlymeIcon/"
    internal var out_path = "/sdcard/zzz/"

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
        for (i in files.indices) {
            val bitmap = BitmapFactory.decodeFile(files[i].toString())
            if (bitmap == null) {
                continue
            }
            if (bitmap.getPixel(bitmap.width / 13, bitmap.height / 13) != Color.TRANSPARENT) {
                val out = dealWithBitmap(bitmap, 7, 4f)
                saveMyBitmap(files[i].name, out);
            } else {
                files[i].renameTo(File("$out_path${files[i].name}"))
            }
        }
    }

    fun dealWithBitmap(bitmap: Bitmap, padding: Int, round: Float): Bitmap {
        //生成直角图
        val copy = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        var cavans = Canvas(copy);
        val matrix = Matrix()
        matrix.setScale(1.2f, 1.2f, (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat());
        cavans.drawBitmap(bitmap, matrix, null)
        cavans.drawBitmap(bitmap, 0f, 0f, null)


        //把直角图处理成圆角
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        cavans = Canvas(result);
        val color = Color.BLACK
        val paint = Paint()
        val rect = Rect(padding, padding, result.width - padding, result.height - padding)
        paint.setAntiAlias(true)
        paint.setColor(color)
        cavans.drawRoundRect(RectF(rect), round, round, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))

        cavans.drawBitmap(copy, 0f, 0f, paint)
        return result
    }

    fun saveMyBitmap(bitName: String, mBitmap: Bitmap) {
        val f = File("$out_path$bitName")
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
