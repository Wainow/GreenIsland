package com.wainow.island.entities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wainow.data.repository.FavoriteSharedPreferences
import com.wainow.data.repository.StockServiceSubscriber
import com.wainow.island.ui.list.ViewModelErrorEvent
import com.wainow.island.ui.list.ViewModelFinallyEvent
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class StockListViewModel(application: Application) : AndroidViewModel(application) {
    val compositeDisposable = CompositeDisposable()
    var subscriber = StockServiceSubscriber()
    val sharedPreferences = FavoriteSharedPreferences(application.baseContext)
    // Showing the result of the last request (error or not)
    var isErrorEvent = false

    private val observableFinallyEvents = MutableLiveData<ViewModelFinallyEvent>()
    private val observableErrorEvents = MutableLiveData<ViewModelErrorEvent>()

    // Methods for tracking and executing events from viewModel through viewModelEvent
    fun observeViewModelErrorEvents(): LiveData<ViewModelErrorEvent> = observableErrorEvents
    fun observeViewModelFinallyEvents(): LiveData<ViewModelFinallyEvent> = observableFinallyEvents

    // Methods for post values into viewModelEvent
    fun postViewModelErrorEvent(event: ViewModelErrorEvent) { observableErrorEvents.postValue(event) }
    fun postViewModelFinallyEvent(event: ViewModelFinallyEvent) { observableFinallyEvents.postValue(event) }

    fun clear(){ compositeDisposable.dispose() }
}