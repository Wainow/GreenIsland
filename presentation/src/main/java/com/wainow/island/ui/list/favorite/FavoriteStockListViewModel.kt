package com.wainow.island.ui.list.favorite

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.entities.StockListViewModel
import com.wainow.island.ui.list.ViewModelErrorEvent
import com.wainow.island.ui.list.ViewModelFinallyEvent
import io.reactivex.rxjava3.exceptions.CompositeException


class FavoriteStockListViewModel(application: Application) : StockListViewModel(application) {
    var liveList : MutableLiveData<ArrayList<CompanyProfile>> = MutableLiveData()

    init {
        setData()
    }

    fun setData(){
        sharedPreferences.getFavorite().let { favoriteNames ->
            Log.d(TAG, "FavoriteStockViewModel: getData for list $favoriteNames")
            if(favoriteNames.isNotEmpty()) {
                try {
                    getFavoriteList(favoriteNames)
                } catch (e: Exception){
                    clear()
                    e.printStackTrace()
                    postViewModelFinallyEvent(ViewModelFinallyEvent(isErrorEvent))
                }
            } else {
                liveList.value = ArrayList()
                postViewModelFinallyEvent(ViewModelFinallyEvent(false))
            }
        }
    }

    /*
    * In this method we make request on server and perform callback after response:
    *
    * tryCallback: performs if the response is successful
    * errorCallback: performs if the response is not successful
    * finallyCallback: performs after every response doesn't matter successful or not
     */
    private fun getFavoriteList(favoriteNames: List<String>){
        subscriber.getFavoriteList(compositeDisposable, favoriteNames = favoriteNames,
            tryCallback = {
                Log.d(TAG, "FavoriteStockViewModel: tryCallback: result: $it")
                liveList.value = it as ArrayList<CompanyProfile>
                isErrorEvent = false
            }, errorCallback = {
                Log.d(TAG, "FavoriteStockViewModel: errorCallback: error: $it")
                isErrorEvent = true
                postViewModelErrorEvent(ViewModelErrorEvent(getExceptionName(it)))
            }, finallyCallback = {
                Log.d(TAG, "FavoriteStockViewModel: finallyCallback")
                postViewModelFinallyEvent(ViewModelFinallyEvent(isErrorEvent))
            }
        )
    }

    private fun getExceptionName(e: Throwable): String{
        return if (e is CompositeException)
            e.exceptions.first().javaClass.simpleName
        else
            e.javaClass.simpleName
    }
}