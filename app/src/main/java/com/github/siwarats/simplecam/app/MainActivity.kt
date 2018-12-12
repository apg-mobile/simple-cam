package com.github.siwarats.simplecam.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.github.siwarats.simplecam.const.PreviewMode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val menus = mutableListOf(
        Pair("Square", "Preview in the square mode with correct orientation."),
        Pair("Center Inside", "Preview center inside in a container with correct orientation."),
        Pair("Center Crop", "Preview center crop in a container with correct orientation."),
        Pair("ZXing", "Implement ZXing Scanner")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lvMain?.adapter = MainAdapter()
        lvMain?.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> SimplePreviewActivity.createIntent(this, PreviewMode.SQUARE)
                    .also { startActivity(it) }
                1 -> SimplePreviewActivity.createIntent(this, PreviewMode.CENTER_INSIDE)
                    .also { startActivity(it) }
                2 -> SimplePreviewActivity.createIntent(this, PreviewMode.CENTER_CROP)
                    .also { startActivity(it) }
                3 -> ZXingActivity.createIntent(this)
                    .also { startActivity(it) }
            }
        }
    }

    inner class MainAdapter : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val vh = convertView?.tag
                ?.let { it as? ViewHolder }
                ?: ViewHolder(
                    LayoutInflater
                        .from(this@MainActivity)
                        .inflate(android.R.layout.simple_list_item_2, null, false)
                )

            val item = getItem(position)
            vh.tvTitle?.text = item.first
            vh.tvSubTitle?.text = item.second

            return vh.view
        }

        override fun getItem(position: Int): Pair<String, String> {
            return menus[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return menus.size
        }

        inner class ViewHolder(val view: View) {
            val tvTitle = view.findViewById<TextView?>(android.R.id.text1)
            val tvSubTitle = view.findViewById<TextView?>(android.R.id.text2)

            init {
                view.tag = this
            }
        }
    }
}
