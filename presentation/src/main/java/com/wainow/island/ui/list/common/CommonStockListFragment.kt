package com.wainow.island.ui.list.common

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.wainow.data.repository.FavoriteSharedPreferences
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.R
import com.wainow.island.adapter.common.MyCommonRecyclerViewAdapter
import com.wainow.island.entities.StockListFragment
import com.wainow.island.entities.StockListViewModel
import com.wainow.island.ui.list.favorite.FavoriteStockListFragment
import org.koin.android.ext.android.inject


@Suppress("UNCHECKED_CAST")
class CommonStockListFragment : StockListFragment(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var sharedPreferences: FavoriteSharedPreferences
    val viewModel: CommonStockListViewModel by lazy { ViewModelProviders.of(this).get(
        CommonStockListViewModel::class.java
    ) }

    companion object {
        @JvmStatic
        fun newInstance() = CommonStockListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stock_list, container, false)
        initView(view)
        setViewModelObservableEvents(viewModel)
        setRecycler(recyclerView)
        return view
    }

    override fun initView(view: View) {
        Log.d(TAG, "CommonStockFragment : initView")
        pd = view.findViewById(R.id.pd_list)
        recyclerView = view.findViewById(R.id.list)
        swipeRefreshLayout = view.findViewById(R.id.swipe_container)
        swipeRefreshLayout.setOnRefreshListener(this)
        sharedPreferences = FavoriteSharedPreferences(requireContext())
    }

    override fun setViewModelObservableEvents(listViewModel: StockListViewModel) {
        super.setViewModelObservableEvents(listViewModel)
        with(listViewModel as CommonStockListViewModel){
            clear()
            setData()
        }
    }

    override fun setRecycler(view: View){
        Log.d(TAG, "CommonStockFragment : setRecycler")
        if(view is RecyclerView) {
            adapter = MyCommonRecyclerViewAdapter(
                retryCallback = { retry() },
                changeFavoriteCallback = { i, c -> changeFavorite(i, c) },
            )
            setPDVisibility(if (!viewModel.isErrorEvent && viewModel.isInitialError) View.VISIBLE else View.GONE)
            with(adapter as MyCommonRecyclerViewAdapter){
                isError = viewModel.isErrorEvent
                submitList(viewModel.livePagedList.value)
                view.adapter = this
            }
        }
    }

    override fun retry(){
        Log.d(TAG, "CommonStockFragment : retry")
        if(viewModel.isInitialError) setPDVisibility(View.VISIBLE)
        setAdapterError(false)
        adapter.notifyDataSetChanged()
        viewModel.retry()
    }

    override fun onRefresh(){
        Log.d(TAG, "CommonStockFragment : OnRefresh")
        viewModel.clear()
        viewModel.setData()
        setRecycler(recyclerView)
    }

    override fun changeFavorite(i: Int, c: CompanyProfile){
        Log.d(TAG, "CommonStockFragment : changeFavorite")
        try {
            changeStarsInFavoriteFragment(i, c)
        } catch (e: UninitializedPropertyAccessException){
            Snackbar.make(recyclerView, R.string.snack_bar_message, Snackbar.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    override fun changeStarsInCommonFragment(i: Int, c: CompanyProfile) {}
    override fun changeStarsInFavoriteFragment(i: Int, c: CompanyProfile) {
        val fragment: FavoriteStockListFragment by inject()
        fragment.viewModel.setData()
        fragment.adapter.isLoading = true
        fragment.adapter.notifyDataSetChanged()
    }

    private fun setAdapterError(isError: Boolean){
        adapter.isError = isError
        viewModel.isErrorEvent = isError
    }

    fun getPagedList() = viewModel.livePagedList.value
    override fun setPDVisibility(visibility: Int){ pd.visibility = visibility }
    override fun setSwipeRefresh(isRefresh: Boolean){ swipeRefreshLayout.isRefreshing = isRefresh }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }
}