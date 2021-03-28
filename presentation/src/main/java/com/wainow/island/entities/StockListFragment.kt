package com.wainow.island.entities

import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import com.wainow.domain.entities.CompanyProfile
import com.wainow.island.adapter.StockRecyclerViewAdapter
import com.wainow.island.ui.list.ViewModelErrorEvent
import com.wainow.island.ui.list.ViewModelFinallyEvent

abstract class StockListFragment: StockFragment() {
    lateinit var pd: ProgressBar
    lateinit var adapter: StockRecyclerViewAdapter

    /*
    * Some additional and comfortable
    * for use methods in stock lists fragments
     */
    abstract override fun initView(view: View)
    abstract override fun retry()
    abstract fun setRecycler(view: View)
    abstract fun changeFavorite(i: Int, c: CompanyProfile)
    abstract fun setPDVisibility(visibility: Int)
    abstract fun setSwipeRefresh(isRefresh: Boolean)
    abstract fun changeStarsInFavoriteFragment(i: Int, c: CompanyProfile)
    abstract fun changeStarsInCommonFragment(i: Int, c: CompanyProfile)

    /*
    * In every stock list fragment i setting observable events for viewModel
    * For use it in ViewModelEvent class
    * Also in one fragment i need to add some additional observables in viewModel
    * Therefore i was made it open for it
     */
    open fun setViewModelObservableEvents(listViewModel: StockListViewModel){
        listViewModel.observeViewModelErrorEvents().observe(viewLifecycleOwner, Observer {
            val event = it.takeUnless { it == null || it.handled } ?: return@Observer
            handleErrorViewModelAction(event)
        })
        listViewModel.observeViewModelFinallyEvents().observe(viewLifecycleOwner, Observer {
            val event = it.takeUnless { it == null || it.handled } ?: return@Observer
            handleFinallyViewModelAction(event)
        })
    }

    private fun handleErrorViewModelAction(event: ViewModelErrorEvent) { event.handle(this) }
    private fun handleFinallyViewModelAction(event: ViewModelFinallyEvent) { event.handle(this) }
}