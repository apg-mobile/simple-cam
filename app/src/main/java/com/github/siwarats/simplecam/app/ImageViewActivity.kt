package com.github.siwarats.simplecam.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageView = ImageView(this)
        setContentView(imageView)

        Glide.with(imageView)
            .load(intent!!.getStringExtra("path"))
            .into(imageView)
    }

    companion object {

        fun createIntent(context: Context?, path: String) = Intent(context, ImageViewActivity::class.java)
            .apply {
                putExtra("path", path)
            }
    }
}