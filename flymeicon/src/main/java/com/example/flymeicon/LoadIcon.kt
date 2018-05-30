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


class LoadIcon : AppCompatActivity() {

    internal var path = "/sdcard/Customize/.FlymeIcon"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frame = findViewById<ViewGroup>(R.id.frame)

        val imageView = ImageView(this)

        imageView.setImageResource(R.mipmap.ic_launcher);

        imageView.setOnClickListener {

        }
    }



}
