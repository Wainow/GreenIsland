package com.wainow.island.adapter

import androidx.recyclerview.widget.RecyclerView
import com.wainow.domain.entities.CompanyProfile

interface StockRecyclerViewAdapter {
    /*
    * isLoading boolean means that user is actually waiting stock results
    * Because of that isLoading = true, by default
     */
    var isError: Boolean
    var isLoading: Boolean
    var exceptionName: String
    var isEmptyResponse: Boolean
        get() = false
        set(value) {}

    fun notifyDataSetChanged()
}