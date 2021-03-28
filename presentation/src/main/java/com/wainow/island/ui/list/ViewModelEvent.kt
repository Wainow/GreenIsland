package com.wainow.island.ui.list

import android.util.Log
import android.view.View
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.entities.StockListFragment
import com.wainow.island.ui.list.common.CommonStockListFragment
import com.wainow.island.ui.list.favorite.FavoriteStockListFragment
import com.wainow.island.ui.query.QueryListFragment

abstract class ViewModelEvent {
    var handled: Boolean = false

    open fun handle(listFragment: StockListFragment) {
        handled = true
    }
}

/*
* These two methods like talk between Fragments and their ViewModels
* Here the tasks that need to be performed by all fragments
* After the server responses are received or not
 */

class ViewModelErrorEvent(private val t: String): ViewModelEvent() {
    override fun handle(listFragment: StockListFragment) {
        Log.d(TAG,"ViewModelErrorEvent: $t")
        with(listFragment){
            /*
            * Joint task of all fragments at ErrorEvent
             */
            if (t == "") {
                adapter.isError = false
                /*
                * Only CommonStockFragment deal
                */
                if(this is CommonStockListFragment)
                    viewModel.isErrorEvent = false
            } else {
                adapter.exceptionName = t
                adapter.isError = true
                /*
                * Only CommonStockFragment deal
                */
                if(this is CommonStockListFragment)
                    viewModel.isErrorEvent = true
                /*
                * Only FavoriteStockFragment deal
                */
                if(this is FavoriteStockListFragment)
                    adapter.isLoading = true
                adapter.notifyDataSetChanged()
            }
        }
        handled = true
    }
}

class ViewModelFinallyEvent(private val isFirst: Boolean = false): ViewModelEvent() {

    override fun handle(listFragment: StockListFragment) {
        Log.d(TAG,"ViewModelFinallyEvent: $isFirst")
        with(listFragment){
            setSwipeRefresh(false)
            /*
            * Only FavoriteStockFragment and QueryFragment deals
             */
            if(this is FavoriteStockListFragment || this is QueryListFragment){
                if(!isFirst) {
                    setRecycler(recyclerView)
                    /*
                    * Only QueryFragment deal
                    */
                    if (this is QueryListFragment) {
                        val isEmptyResponse = viewModel.liveList.value?.isEmpty()
                        if (isEmptyResponse == true) {
                            adapter.isLoading = isEmptyResponse
                            adapter.isEmptyResponse = isEmptyResponse
                        }
                    }
                }
                else {
                    adapter.isError = true
                }
            }
            /*
            * Joint task of all fragments at FinallyEvent
             */
            adapter.notifyDataSetChanged()
            setPDVisibility(View.GONE)
        }
        handled = true
    }
}