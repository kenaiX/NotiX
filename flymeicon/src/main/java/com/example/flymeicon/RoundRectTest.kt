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
import com.example.flymeicon.utils.BimapUtils
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class RoundRectTest : AppCompatActivity() {

    internal var path = "/sdcard/Customize/.FlymeIcon"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frame = findViewById<ViewGroup>(R.id.frame)

        val imageView = ImageView(this)
        val imageView2 = ImageView(this)
        val imageView3 = ImageView(this)

        frame.addView(imageView)
        frame.addView(imageView2)
        frame.addView(imageView3)
        frame.setBackgroundColor(Color.BLACK)

        imageView.setImageBitmap(BitmapFactory.decodeStream(assets.open("xhdpi.png")));

        imageView2.setImageBitmap(BimapUtils.toRound(BitmapFactory.decodeStream(assets.open("xhdpi.png")),7,20f));

        imageView3.setImageBitmap(BimapUtils.toRound(BitmapFactory.decodeStream(assets.open("xhdpi.png")),20,20f));

    }
}
