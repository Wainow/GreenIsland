package com.wainow.data.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.paging.PositionalDataSource
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock.Companion.TAG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.CompositeException
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.coroutineContext

@Suppress("UNCHECKED_CAST")
class MyPositionalDataSource(
    private val sharedPreferences: FavoriteSharedPreferences,
    private val resultCallback: (String) -> Unit,
    private val finishCallback: (Boolean) -> Unit
): PositionalDataSource<CompanyProfile>(){

    private var retryCompletable: Completable? = null
    private var compositeDisposable = CompositeDisposable()
    private var subscriber = StockServiceSubscriber()

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<CompanyProfile>
    ) {
        Log.d(
            TAG, "loadInitial, requestedStartPosition = " + params.requestedStartPosition +
                    ", requestedLoadSize = " + params.requestedLoadSize
        )
        try {
            sharedPreferences.getFavorite()?.let { list ->
                subscriber.getList(compositeDisposable, list, params.requestedStartPosition.toLong(), {
                    Log.d(TAG, "loadInitial: success")
                    setRetry(null)
                    if(!it.contains(null)) {
                        callback.onResult(it as MutableList<CompanyProfile>, 0)
                    }
                }, {
                    Log.d(TAG, "loadInitial: error: $it")
                    setRetry { loadInitial(params, callback) }
                    val exceptionName: String = if(it is CompositeException)
                        it.exceptions.first().javaClass.simpleName
                    else
                        it.javaClass.simpleName
                    resultCallback(exceptionName)
                }, {
                    finishCallback(true)
                })
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<CompanyProfile>) {
        Log.d(
            TAG,
            "loadRange, startPosition = " + params.startPosition + ", loadSize = " + params.loadSize
        )
        try {
            sharedPreferences.getFavorite()?.let { list ->
                subscriber.getList(compositeDisposable, list, params.startPosition.toLong(), {
                    Log.d(TAG, "loadRange: success")
                    setRetry(null)
                    callback.onResult(it as MutableList<CompanyProfile>)
                }, {
                    Log.d(TAG, "loadRange: error: $it")
                    setRetry { loadRange(params, callback) }
                    val exceptionName: String = if(it is CompositeException)
                        it.exceptions.first().javaClass.simpleName
                    else
                        it.javaClass.simpleName
                    resultCallback(exceptionName)
                }, {
                    finishCallback(false)
                })
            }
        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) {
            null
        } else {
            Completable.fromAction(action)
        }
    }

    fun retry() {
        Log.d(TAG, "DataSource: retry")
        try {
            if (retryCompletable != null) {
                compositeDisposable.add(retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn {
                        val error = it.javaClass.simpleName
                        Log.d(TAG, error)
                        resultCallback(error)
                        finishCallback(true)
                    }
                    .subscribe({
                        resultCallback("")
                        finishCallback(true)
                    }, { throwable ->
                        val error = throwable.javaClass.simpleName
                        Log.d(TAG, error)
                        resultCallback(error)
                        finishCallback(true)
                    })
                )
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun clear(){
        compositeDisposable.dispose()
    }
}