package com.example.flymeicon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView

import java.io.File
import android.R.attr.bitmap
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.widget.Toast
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class RefreshIconActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        sendBroadcast(Intent("com.flyme.iconevent.check"))
        Toast.makeText(this,"开始刷新",Toast.LENGTH_LONG).show()
    }
}
