package com.wainow.island.ui.list.common

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.wainow.data.repository.MyPositionalDataSource
import com.wainow.domain.entities.CompanyProfile
import com.wainow.island.entities.StockListViewModel
import com.wainow.island.ui.list.ViewModelErrorEvent
import com.wainow.island.ui.list.ViewModelFinallyEvent
import java.util.concurrent.Executors

class CommonStockListViewModel(application: Application) : StockListViewModel(application){
    var livePagedList: MutableLiveData<PagedList<CompanyProfile>> = MutableLiveData()
    private lateinit var dataSource: MyPositionalDataSource
    // boolean var which shows where exactly (onRange or onInitial methods) take place error
    var isInitialError = true

    init {
        setData()
    }


    /*
    * resultCallback: returns
    * if successful -> "" String
    * else -> "exceptionName" String
    *
    * finishCallback: return isInitialError boolean
    */
    fun setData(){
        dataSource = MyPositionalDataSource(sharedPreferences,
            resultCallback = {
                if(it == "") clear()
                postViewModelErrorEvent(ViewModelErrorEvent(it))
            },
            finishCallback = {
                isInitialError = it
                postViewModelFinallyEvent(ViewModelFinallyEvent(it))
            }
        )
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()
        val pagedList = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(MainThreadExecutor())
            .build()
        livePagedList.value = pagedList
    }

    fun retry(){ dataSource.retry() }
}