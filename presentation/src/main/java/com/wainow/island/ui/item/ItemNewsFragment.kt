package com.wainow.island.ui.item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wainow.domain.entities.CompanyNewsItem
import com.wainow.domain.entities.Stock
import com.wainow.island.R
import com.wainow.island.entities.StockFragment
import com.wainow.island.adapter.news.MyNewsRecyclerViewAdapter

class ItemNewsFragment : StockFragment() {
    private lateinit var adapter: MyNewsRecyclerViewAdapter
    private lateinit var viewModel: ItemViewModel

    companion object {
        fun newInstance() = ItemNewsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        initView(view)
        setRecycler(recyclerView)
        return view
    }

    override fun initView(view: View) {
        recyclerView = view.findViewById(R.id.news_list)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { it ->
            viewModel = ViewModelProvider(it).get(ItemViewModel::class.java)
            setViewModelObservableEvents(viewModel)
        }
    }

    private fun setRecycler(view: View, currentList: ArrayList<CompanyNewsItem>? = null){
        Log.d(Stock.TAG, "NewsFragment : setRecycler")
        if (view is RecyclerView) {
            Log.d(Stock.TAG, "NewsFragment : setRecycler: CURRENT LIST: $currentList")
            adapter = MyNewsRecyclerViewAdapter(
                currentList ?: (ArrayList()),
                retryCallback = { retry() }
            )
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = adapter
            adapter.notifyDataSetChanged()
        } else{
            Log.d(Stock.TAG, "NewsFragment : setRecycler ?")
        }
    }

    private fun setViewModelObservableEvents(viewModel: ItemViewModel) {
        activity?.let {
            viewModel.liveNews.observe(it) { list ->
                Log.d(Stock.TAG, "NewsFragment: viewModel: observe: $list")
                successNewsResponse(list)
            }
            viewModel.liveError.observe(it) { error ->
                Log.d(Stock.TAG, "NewsFragment: viewModel: error: $error")
                failureNewsResponse(error)
            }
        }
    }

    private fun successNewsResponse(list: List<CompanyNewsItem>?){
        if(list == null) {
            adapter.exceptionName = "No news found"
            adapter.isError = true
        }
        setRecycler(recyclerView, list as ArrayList<CompanyNewsItem>)
        adapter.isLoading = false
    }

    private fun failureNewsResponse(error: String){
        setRecycler(recyclerView, null)
        adapter.isError = true
        adapter.exceptionName = error
        adapter.notifyDataSetChanged()
    }

    override fun retry(){
        adapter.isError = false
        adapter.isLoading = true
        adapter.notifyDataSetChanged()
        viewModel.setData()
    }
}