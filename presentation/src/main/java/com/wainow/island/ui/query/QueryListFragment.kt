package com.wainow.island.ui.query

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.MainActivity
import com.wainow.island.R
import com.wainow.island.adapter.favorite.MyFavoriteRecyclerViewAdapter
import com.wainow.island.entities.StockListFragment
import com.wainow.island.ui.list.common.CommonStockListFragment
import com.wainow.island.ui.list.favorite.FavoriteStockListFragment
import kotlinx.android.synthetic.*
import org.koin.android.ext.android.inject

class QueryListFragment(
    var query: String = ""
) : StockListFragment() {
    val viewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            QueryListViewModel::class.java
        )}

    companion object {
        fun newInstance(query: String) = QueryListFragment(query)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        val view = inflater.inflate(R.layout.query_fragment, container, false)
        initView(view)
        setRecycler(recyclerView)
        checkAndSetData(query)
        setViewModelObservableEvents(viewModel)
        return view
    }

    override fun initView(view: View) {
        recyclerView = view.findViewById(R.id.query_list)
        viewModel.liveList.value = null
        viewModel.isOpen = true
    }

    override fun setRecycler(view : View){
        if (view is RecyclerView) {
            val currentList = viewModel.liveList.value
            Log.d(TAG, "QueryFragment : setRecycler: CURRENT LIST: $currentList")
            adapter = MyFavoriteRecyclerViewAdapter(
                currentList ?: ArrayList(),
                false,
                retryCallback = { retry() },
                changeFavoriteCallback = { i: Int, c: CompanyProfile -> changeFavorite(i, c) },
            )
            view.layoutManager = LinearLayoutManager(requireContext())
            view.adapter = adapter as MyFavoriteRecyclerViewAdapter
            adapter.isLoading = currentList == null
            adapter.notifyDataSetChanged()
        }
    }

    private fun checkAndSetData(query: String = ""){
        Log.d(TAG, "QueryFragment : checkAndSetData: query: $query")
        with(viewModel){
            if(query != "") setData(query)
            else setData()
        }
    }

    override fun changeFavorite(i: Int, c: CompanyProfile) {
        Log.d(TAG, "QueryFragment : changeFavorite")
        try {
            try {
                changeStarsInCommonFragment(i, c)
            } catch (e: NoSuchElementException) {
                Log.d(TAG, "QueryFragment: changeFavorite: ${e.printStackTrace()}")
            } finally {
                changeStarsInFavoriteFragment(i, c)
            }
        } catch (e: Exception){
            e.printStackTrace()
            Snackbar.make(recyclerView, R.string.snack_bar_message, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    override fun changeStarsInCommonFragment(i: Int, c: CompanyProfile){
        val fragmentStock = ((activity as MainActivity).sectionsPagerAdapter
            .listFragment[0] as CommonStockListFragment)
        with(fragmentStock.getPagedList()?.last { it.ticker == c.ticker }) {
            this?.isFavorite = this?.isFavorite != true
        }
        fragmentStock.adapter.notifyDataSetChanged()
    }

    override fun changeStarsInFavoriteFragment(i: Int, c: CompanyProfile){
        val fragmentFavorite: FavoriteStockListFragment by inject()
        with(fragmentFavorite){
            viewModel.setData()
            adapter.isLoading = true
            adapter.notifyDataSetChanged()
        }
    }

    override fun setPDVisibility(visibility: Int) {}
    override fun setSwipeRefresh(isRefresh: Boolean) {}

    override fun retry(){
        adapter.isError = false
        adapter.notifyDataSetChanged()
        viewModel.setData()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }

}