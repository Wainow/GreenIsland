package com.wainow.island.ui.list.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.MainActivity
import com.wainow.island.R
import com.wainow.island.adapter.favorite.MyFavoriteRecyclerViewAdapter
import com.wainow.island.entities.StockListFragment
import com.wainow.island.entities.StockListViewModel
import com.wainow.island.ui.list.common.CommonStockListFragment
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class FavoriteStockListFragment : StockListFragment(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    val viewModel: FavoriteStockListViewModel by lazy {ViewModelProviders.of(this).get(
        FavoriteStockListViewModel::class.java
    )}
    private val fragment = this
    private val favoriteModule = module {
        single { fragment }
    }

    companion object{
        @JvmStatic fun newInstance() = FavoriteStockListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        startKoin {
            androidContext(this@FavoriteStockListFragment.requireContext())
            modules(listOf(favoriteModule))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_stock, container, false)
        initView(view)
        setRecycler(recyclerView)
        setViewModelObservableEvents(viewModel)
        return view
    }

    override fun initView(view: View){
        recyclerView = view.findViewById(R.id.favorite_list)
        swipeRefreshLayout = view.findViewById(R.id.favorite_swipe_container)
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun setViewModelObservableEvents(listViewModel: StockListViewModel){
        super.setViewModelObservableEvents(listViewModel)
        with(listViewModel as FavoriteStockListViewModel){
            liveList.observe(viewLifecycleOwner) {
                setRecycler(recyclerView)
            }
            setData()
        }
    }

    override fun onRefresh(){
        Log.d(TAG, "FavoriteStockFragment : OnRefresh")
        adapter.isLoading = true
        adapter.notifyDataSetChanged()
        viewModel.setData()
    }

    override fun setRecycler(view: View){
        if (view is RecyclerView) {
            val currentList = viewModel.liveList.value
            Log.d(TAG, "FavoriteStockFragment : setRecycler: CURRENT LIST: $currentList")
            adapter = MyFavoriteRecyclerViewAdapter(
                currentList ?: ArrayList(),
                true,
                { retry() },
                { i: Int, c: CompanyProfile -> changeFavorite(i, c) },
            )
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = adapter as MyFavoriteRecyclerViewAdapter
            if(currentList != null) adapter.isLoading = false
            adapter.notifyDataSetChanged()
        }
    }

    override fun retry(){
        adapter.isError = false
        adapter.notifyDataSetChanged()
        viewModel.setData()
    }

    override fun changeFavorite(i: Int, c: CompanyProfile){
        Log.d(TAG, "FavoriteStockFragment : changeFavorite")
        try {
            changeStarsInFavoriteFragment(i, c)
            changeStarsInCommonFragment(i, c)
        } catch (e: Exception){
            Snackbar.make(recyclerView, R.string.snack_bar_message, Snackbar.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    override fun changeStarsInFavoriteFragment(i: Int, c: CompanyProfile) {
        // Deleting stock from favorite list
        with(adapter as MyFavoriteRecyclerViewAdapter){
            list.removeAt(i)
            notifyDataSetChanged()
        }
    }

    override fun changeStarsInCommonFragment(i: Int, c: CompanyProfile){
        // Getting a fragment that contains a list with all the stocks
        val fragment =
            (activity as MainActivity).sectionsPagerAdapter.listFragment[0] as CommonStockListFragment
        // Change favorite-star-icon in item from that fragment
        fragment.viewModel.livePagedList.value?.last { it.ticker == c.ticker }?.isFavorite =
            false
        // Update adapter of that fragment
        fragment.adapter.notifyDataSetChanged()
    }

    override fun setPDVisibility(visibility: Int){}
    override fun setSwipeRefresh(isRefresh: Boolean){ swipeRefreshLayout.isRefreshing = isRefresh }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
        stopKoin()
    }
}