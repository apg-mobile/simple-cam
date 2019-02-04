package com.github.siwarats.simplecam.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_tracking_image_list.*
import kotlinx.android.synthetic.main.listitem_tracking_image.view.*

class GalleryActivity : AppCompatActivity() {

    private val items = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_image_list)

        BarcodeStorageController().getImages(this)
            .map { it.path }
            .also { items.addAll(it) }

        rcvImages?.adapter = Adapter()
        rcvImages?.layoutManager = GridLayoutManager(this, 3)
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(this@GalleryActivity)
                    .inflate(R.layout.listitem_tracking_image, p0, false)
            )
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(vh: ViewHolder, position: Int) {
            Glide.with(vh.imgView)
                .load(items[position])
                .into(vh.imgView)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgView = view.imgView

            init {
                imgView.setOnClickListener { _ ->
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        ImageViewActivity.createIntent(this@GalleryActivity, items[pos])
                            .also { startActivity(it) }
                    }
                }
            }
        }
    }

    companion object {
        fun createIntent(context: Context?) = Intent(context, GalleryActivity::class.java)
    }
}