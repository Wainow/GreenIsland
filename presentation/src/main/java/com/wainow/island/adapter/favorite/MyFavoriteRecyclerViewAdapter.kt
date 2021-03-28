package com.wainow.island.adapter.favorite

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.R
import com.wainow.island.adapter.holder.ErrorViewHolder
import com.wainow.island.adapter.holder.StockItemViewHolder
import com.wainow.island.adapter.StockRecyclerViewAdapter

class MyFavoriteRecyclerViewAdapter(
    var list: ArrayList<CompanyProfile>,
    private var isFavoriteList: Boolean,
    private val retryCallback: () -> Unit,
    private val changeFavoriteCallback: (Int, CompanyProfile) -> Unit,
): StockRecyclerViewAdapter, RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override var isError: Boolean = false
    override var isLoading: Boolean = true
    override var exceptionName: String = ""
    override var isEmptyResponse = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when (viewType) {
            R.layout.stock_item -> StockItemViewHolder.create(parent, changeFavoriteCallback)
            R.layout.stock_error -> ErrorViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d(TAG, "MyFavoriteRecyclerViewAdapter: onBindViewHolder: position: $position, isError: $isError isEmpty: $isEmptyResponse, exceptionName: $exceptionName")
        when(getItemViewType(position)) {
            R.layout.stock_item -> (holder as StockItemViewHolder).bindTo(list[position], position, isFavoriteList)
            R.layout.stock_error -> {
                if(isEmptyResponse)
                    (holder as ErrorViewHolder).bindTo(true, ErrorViewHolder.ERROR_NO_FOUND)
                else
                    (holder as ErrorViewHolder).bindTo(isError, exceptionName)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        //Log.d(TAG, "MyFavoriteRecyclerViewAdapter: getItemViewType: position: $position, isLoading: $isLoading")
        return if (isLoading && position == itemCount - 1 || list.isEmpty() && !isLoading) {
            R.layout.stock_error
        } else {
            R.layout.stock_item
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "MyFavoriteRecyclerViewAdapter: getItemCount: ${if(isError) 1 else
            list.size + if(isLoading) 1 else 0}")
        return if(isError) 1 else
            list.size + if(isLoading) 1 else 0
    }
}