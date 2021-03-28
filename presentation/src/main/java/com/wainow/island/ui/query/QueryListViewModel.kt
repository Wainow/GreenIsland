package com.wainow.island.ui.query

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.entities.StockListViewModel
import com.wainow.island.ui.list.ViewModelErrorEvent
import com.wainow.island.ui.list.ViewModelFinallyEvent

class QueryListViewModel(application: Application) : StockListViewModel(application) {
    // LiveList which contains result of query
    var liveList : MutableLiveData<ArrayList<CompanyProfile>> = MutableLiveData()
    // LiveQueryList which contains query history
    var liveQueryList : MutableLiveData<ArrayList<String>> = MutableLiveData()
    // Boolean which shows whether the search window is currently open
    var isOpen: Boolean = false
    // Boolean which indicate the first query
    val isFirstQuery: Boolean = true

    init {
        checkAndSetData()
    }

    private fun checkAndSetData(){
        if(liveQueryList.value == null){
            liveQueryList.value = ArrayList()
        }
        try {
            setData()
        } catch (e: NoSuchElementException){
            Log.d(TAG, e.printStackTrace().toString())
        }
    }

    fun setData(query: String = liveQueryList.value!!.last()){
        // Adding new query in liveQueryList for history
        liveQueryList.value?.add(query)
        // Getting favorite stocks from sharedPreferences
        sharedPreferences.getFavorite().let{ favoriteNames ->
            Log.d(TAG, "QueryViewModel: getData for list $favoriteNames")
            // Make search request from server
            searchStocks(query, favoriteNames)
        }
    }

    /*
    * In this method we make search request on server and perform callback after response:
    *
    * tryCallback: performs if the response is successful
    * errorCallback: performs if the response is not successful
    * finallyCallback: performs after every response doesn't matter successful or not
     */
    private fun searchStocks(query: String, favoriteNames: ArrayList<String>){
        subscriber.searchStock(compositeDisposable, query, favoriteNames = favoriteNames,
            tryCallback = {
                Log.d(TAG, "QueryViewModel: tryCallback: result: $it")
                liveList.value = it as ArrayList<CompanyProfile>
                isErrorEvent = false
            }, errorCallback = {
                Log.d(TAG, "QueryViewModel: errorCallback: error: $it")
                // Post this throwable error into viewModelEvent
                postViewModelErrorEvent(ViewModelErrorEvent(it))
                isErrorEvent = true
            }, finallyCallback = {
                Log.d(TAG, "QueryViewModel: finallyCallback")
                // Post error boolean into viewModelEvent
                postViewModelFinallyEvent(ViewModelFinallyEvent(isErrorEvent))
            }
        )
    }
}