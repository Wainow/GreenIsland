package com.wainow.island.adapter.holder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wainow.domain.entities.CompanyNewsItem
import com.wainow.island.R
import java.text.SimpleDateFormat

class NewsItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val newsText: TextView = view.findViewById(R.id.news_tv)
    private val newsImage: ImageView = view.findViewById(R.id.news_iv)
    private val newsDate: TextView = view.findViewById(R.id.news_date_tv)

    fun bindTo(item: CompanyNewsItem, position: Int) {
        newsText.text = item.headline
        newsDate.text = getDateString(item.datetime.toLong())
        Glide
            .with(itemView.context)
            .load(item.image)
            .error(R.drawable.ic_happy)
            .into(newsImage)
    }

    companion object{
        fun create(
            parent: ViewGroup
        ): NewsItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.news_item, parent, false)
            return NewsItemViewHolder(view)
        }
        const val DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z"
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateString(datetime: Long): String {
        val sdf = SimpleDateFormat(DATE_FORMAT)
        val date = java.util.Date(datetime * 1000)
        return sdf.format(date)
    }
}