package com.wainow.island

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.wainow.island.adapter.SectionsPagerAdapter
import com.wainow.island.ui.query.QueryListFragment
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.adapter.favorite.MyFavoriteRecyclerViewAdapter
import com.wainow.island.ui.query.QueryListViewModel

class MainActivity : AppCompatActivity() {
    lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var searchView: SearchView
    private val queryViewModel by lazy {
        ViewModelProviders.of(this).get(
            QueryListViewModel::class.java
        )}

    companion object{
        const val COLOR_HINT = "#474747"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        setViewPager(viewPager, tabs)
        setSearchViewColors(searchView)
        setPagerVisibility()
        setSearchViewClicks(searchView)
    }

    private fun initView(){
        viewPager = findViewById(R.id.view_pager)
        searchView = findViewById(R.id.search_view)
        tabs = findViewById(R.id.tabs)
    }

    private fun setViewPager(viewPager: ViewPager, tabs: TabLayout){
        sectionsPagerAdapter = SectionsPagerAdapter(
            this,
            supportFragmentManager,
        )
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 1
        tabs.setupWithViewPager(viewPager)
    }

    private fun setSearchViewColors(searchView: SearchView){
        val txtSearch =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        val clearSearch =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        txtSearch.setHintTextColor(Color.parseColor(COLOR_HINT))
        txtSearch.setTextColor(Color.WHITE)
        clearSearch.setImageResource(R.drawable.ic_clear)
        searchView.maxWidth = Integer.MAX_VALUE
    }

    private fun setSearchViewClicks(searchView: SearchView){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG, "MainActivity: onQueryTextSubmit: $query")
                query?.let { setQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "MainActivity: onQueryTextChange: $newText")
                return true
            }
        })
        searchView.setOnCloseListener(object : android.widget.SearchView.OnCloseListener,
            SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                closeQuery()
                return false
            }
        })
    }

    private fun setQuery(query: String){
        if(!queryViewModel.isOpen) {
            Log.d(TAG, "MainActivity: setQuery: isNotOpen")
            if(queryViewModel.isFirstQuery){
                openQuery(QueryListFragment.newInstance(query))
            } else
                openQuery()
        } else {
            Log.d(TAG, "MainActivity: setQuery: isOpen")
            try {
                refreshQuery(query)
            } catch (e: NullPointerException){
                e.printStackTrace()
            }
        }
    }

    private fun setPagerVisibility(){
        if(queryViewModel.isOpen){
            tabs.visibility = View.GONE
            viewPager.visibility = View.GONE
        } else{
            tabs.visibility = View.VISIBLE
            viewPager.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        closeQuery()
    }

    private fun closeQuery(){
        Log.d(TAG, "MainActivity: closeQuery")
        queryViewModel.isOpen = false
        setPagerVisibility()
        getQueryActivity()?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }
    }

    private fun openQuery(queryFragment: QueryListFragment? = getQueryActivity()){
        if (queryFragment != null) {
            queryViewModel.isOpen = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, queryFragment, "queryFragment")
                .addToBackStack(javaClass.simpleName)
                .commit()
            setPagerVisibility()
        }
    }

    private fun getQueryActivity() = supportFragmentManager.findFragmentByTag("queryFragment") as QueryListFragment?

    private fun refreshQuery(query: String){
        getQueryActivity()?.let {
            with(it.adapter as MyFavoriteRecyclerViewAdapter) {
                isEmptyResponse = false
                isLoading = true
                list = ArrayList()
                with(queryViewModel) {
                    liveQueryList.value?.add(query)
                    setData(query)
                }
                notifyDataSetChanged()
            }
        }
    }
}