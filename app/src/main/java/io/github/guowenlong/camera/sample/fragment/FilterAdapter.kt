package io.github.guowenlong.camera.sample.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.guowenlong.camera.sample.R


/**
 * Description: 滤镜adapter
 * Author:      郭文龙
 * Date:        2022/5/16 18:24
 * Gmail:       guowenlong20000@sina.com
 */
abstract class FilterAdapter(var list: MutableList<String> = mutableListOf()) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    abstract fun onItemClickListener(position: Int, title: String)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        val holder: RecyclerView.ViewHolder = ViewHolder(view)
        return holder as ViewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list[position]
        holder.title.setOnClickListener { onItemClickListener(position, list[position]) }
    }

    override fun getItemCount() = list.size
}