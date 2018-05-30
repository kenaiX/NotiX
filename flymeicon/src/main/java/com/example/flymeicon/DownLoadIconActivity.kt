package com.example.flymeicon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView

import android.R.attr.bitmap
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import java.io.*


class DownLoadIconActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initList()

        val it = Intent("com.flyme.iconevent.check.test")

        var sb = StringBuilder()

        Thread({
            for(i in 0 until pkgList.size){
                sb.append(pkgList[i]).append(";")
            }
            it.putExtra("test0",sb.toString())
            sendBroadcast(it)
            runOnUiThread({finish()})
        }).start()
    }
    val pkgList = ArrayList<String>();


    fun initList() {
        val inputStream = assets.open("list.txt")
        var reader = BufferedReader(InputStreamReader(inputStream))
        var b = true
        while (b) {
            val str = reader.readLine();
            if (str.isNullOrEmpty()) {
                b = false
            } else {
                pkgList.add(str)
            }
        }
    }
}
