package com.wainow.island.entities

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

abstract class StockFragment: Fragment(){
    /*
    * Some methods and values which have every fragments here
    * Except ItemCandlesFragment
     */
    lateinit var recyclerView: RecyclerView
    abstract fun initView(view: View)
    abstract fun retry()
}