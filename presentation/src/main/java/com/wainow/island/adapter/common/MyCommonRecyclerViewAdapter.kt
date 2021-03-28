package com.wainow.island.adapter.common

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock
import com.wainow.island.R
import com.wainow.island.adapter.holder.ErrorViewHolder
import com.wainow.island.adapter.holder.StockItemViewHolder
import com.wainow.island.adapter.StockRecyclerViewAdapter


class MyCommonRecyclerViewAdapter(
    private val retryCallback: () -> Unit,
    private val changeFavoriteCallback: (Int, CompanyProfile) -> Unit,
    )
    : StockRecyclerViewAdapter, PagedListAdapter<CompanyProfile, RecyclerView.ViewHolder>(UserDiffCallback) {
    override var isLoading: Boolean = true
    override var isError: Boolean = false
    override var exceptionName: String = ""

    companion object {
        val UserDiffCallback = object : DiffUtil.ItemCallback<CompanyProfile>() {
            override fun areItemsTheSame(oldItem: CompanyProfile, newItem: CompanyProfile): Boolean {
                return oldItem.ticker == newItem.ticker
            }

            override fun areContentsTheSame(oldItem: CompanyProfile, newItem: CompanyProfile): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.stock_item -> StockItemViewHolder.create(parent, changeFavoriteCallback)
            R.layout.stock_error -> ErrorViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d(Stock.TAG, "MyCommonRecyclerViewAdapter: onBindViewHolder: position: $position, isError: $isError, exceptionName: $exceptionName")
        when(getItemViewType(position)) {
            R.layout.stock_item -> getItem(position)?.let { (holder as StockItemViewHolder).bindTo(it, position, false) }
            R.layout.stock_error -> (holder as ErrorViewHolder).bindTo(isError, exceptionName)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading && position == itemCount - 1) {
            R.layout.stock_error
        } else {
            R.layout.stock_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (isError) 1 else 0
    }
}