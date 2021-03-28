package com.wainow.island.adapter.news

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wainow.domain.entities.CompanyNewsItem
import com.wainow.island.R
import com.wainow.island.adapter.holder.ErrorViewHolder
import com.wainow.island.adapter.holder.NewsItemViewHolder

class MyNewsRecyclerViewAdapter(
    val list: ArrayList<CompanyNewsItem> = ArrayList(),
    private val retryCallback: () -> Unit,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isError: Boolean = false
    var isLoading: Boolean = true
    var exceptionName: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when (viewType) {
            R.layout.news_item -> NewsItemViewHolder.create(parent)
            R.layout.stock_error -> ErrorViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Log.d(TAG, "MyFavoriteRecyclerViewAdapter: onBindViewHolder: position: $position, isError: $isError")
        when(getItemViewType(position)) {
            R.layout.news_item -> (holder as NewsItemViewHolder).bindTo(list[position], position)
            R.layout.stock_error -> (holder as ErrorViewHolder).bindTo(isError, exceptionName)
        }
    }

    override fun getItemViewType(position: Int): Int {
        //Log.d(TAG, "MyNewsRecyclerViewAdapter: getItemViewType: position: $position, isLoading: $isLoading")
        return if (isLoading && position == itemCount - 1) {
            R.layout.stock_error
        } else {
            R.layout.news_item
        }
    }

    override fun getItemCount(): Int {
        //Log.d(TAG, "getItemCount: ${if(isError) 1 else
        //    list.size + if(isLoading) 1 else 0}")
        return if(isError) 1 else
            list.size + if(isLoading) 1 else 0
    }
}