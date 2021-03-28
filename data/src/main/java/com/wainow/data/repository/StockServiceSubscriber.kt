package com.wainow.data.repository

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.wainow.data.entities.StockService
import com.wainow.domain.entities.*
import com.wainow.domain.entities.Stock.Companion.TAG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.CompositeException
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException
import kotlin.jvm.javaClass

class StockServiceSubscriber: StockService(){
    private val observer = StockServiceObserver()
    @SuppressLint("CheckResult")
    fun getList(
        compositeDisposable: CompositeDisposable,
        favoriteNames: List<String>,
        startIndex: Long,
        tryCallback: (List<*>) -> Unit,
        errorCallback: (Throwable) -> Unit,
        finallyCallback: () -> Unit
        ){
        try {
            compositeDisposable.add(observer.getStockList(startIndex, favoriteNames)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    Log.d(TAG, it.printStackTrace().toString())
                    errorCallback(it)
                    finallyCallback()
                    return@onErrorReturn null
                }
                .subscribe({
                    Log.d(TAG, it.toString())
                    tryCallback(it)
                    finallyCallback()
                }, {
                    errorCallback(it)
                    finallyCallback()
                })
            )
        } catch (e: Exception){
            errorCallback(e)
            finallyCallback()
        }
    }

    fun getFavoriteList(
        compositeDisposable: CompositeDisposable,
        favoriteNames: List<String>,
        startIndex: Long = 0,
        tryCallback: (List<CompanyProfile>) -> Unit = {},
        errorCallback: (Throwable) -> Unit = {},
        finallyCallback: () -> Unit = {}
    ){
        try{
            compositeDisposable.add(
                observer.setFavoriteList(favoriteNames, startIndex)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn {
                        Log.d(TAG, it.printStackTrace().toString())
                        errorCallback(it)
                        finallyCallback()
                        return@onErrorReturn null
                    }
                    .subscribe({
                        tryCallback(it)
                        finallyCallback()
                    }, {
                        errorCallback(it)
                        finallyCallback()
                    })
            )
        } catch (e: Exception){
            errorCallback(e)
            finallyCallback()
        }
    }

    fun searchStock(
        compositeDisposable: CompositeDisposable,
        query: String,
        favoriteNames: List<String>,
        tryCallback: (List<CompanyProfile?>) -> Unit = {},
        errorCallback: (String) -> Unit = {},
        finallyCallback: () -> Unit = {}
    ){
        Log.d(TAG, "StockServiceSubscriber: searchStock")
        try{
            compositeDisposable.add(
                observer.searchStock(query, favoriteNames)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.d(TAG, "StockServiceSubscriber: searchStock: try: $it")
                        tryCallback(it)
                        finallyCallback()
                    }, {
                        Log.d(TAG, "StockServiceSubscriber: searchStock: error: $it")
                        errorCallback(it.javaClass.simpleName)
                        finallyCallback()
                    })
            )
        } catch (e: Exception){
            errorCallback(e.javaClass.simpleName)
            finallyCallback()
        }
    }

    fun getCandles(
        compositeDisposable: CompositeDisposable,
        symbol: String,
        resolution: String,
        from: Long,
        tryCallback: (CandlesInfo) -> Unit = {},
        errorCallback: (String) -> Unit = {},
        finallyCallback: () -> Unit = {}
    ){
        try{
            compositeDisposable.add(
                observer.getCandles(symbol, resolution, from)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tryCallback(it)
                        finallyCallback()
                    }, {
                        Log.d(TAG, "StockServiceSubscriber: getCandles: error: $it")
                        errorCallback(it.javaClass.simpleName)
                        finallyCallback()
                    })
            )
        } catch (e: Exception){
            errorCallback(e.javaClass.simpleName)
            finallyCallback()
        }
    }

    fun getNews(
        compositeDisposable: CompositeDisposable,
        symbol: String,
        from: String,
        to: String,
        tryCallback: (List<CompanyNewsItem>) -> Unit = {},
        errorCallback: (String) -> Unit = {},
        finallyCallback: () -> Unit = {}
    ){
        try{
            compositeDisposable.add(
                observer.getNews(symbol, from, to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tryCallback(it)
                        finallyCallback()
                    }, {
                        Log.d(TAG, "StockServiceSubscriber: getNews: error: ${it.message}")
                        errorCallback(it.javaClass.simpleName)
                        finallyCallback()
                    })
            )
        } catch (e: Exception){
            errorCallback(e.javaClass.simpleName)
            finallyCallback()
        }
    }
}