package com.wainow.island.ui.item

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.jaredrummler.materialspinner.MaterialSpinner
import com.wainow.domain.entities.Stock
import com.wainow.island.CompanyActivity
import com.wainow.island.R
import com.wainow.island.custom.CustomMarkerView
import com.wainow.island.ui.item.ItemViewModel.Companion.LIST_PARAMETERS
import com.wainow.island.ui.item.ItemViewModel.Companion.LIST_UNIX


class ItemCandlesFragment : Fragment() {
    /*
    * In this fragment i use two customs libraries
    * Material spinner: only for beauty look like of spinner
    * ItemLineChartHelper contains custom LineChart for present chart of stock value
     */
    private lateinit var chart: LineChart
    private lateinit var layout: ConstraintLayout
    private lateinit var price: TextView
    private lateinit var viewModel: ItemViewModel
    private lateinit var spinner: MaterialSpinner
    private lateinit var helper: ItemLineChartHelper

    companion object {
        fun newInstance() = ItemCandlesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.candles_fragment, container, false)
        initView(view)
        setSpinner(spinner)
        helper = buildHelper()!!
        return view
    }

    private fun initView(view: View) {
        layout = view.findViewById(R.id.candle_layout)
        chart = view.findViewById(R.id.graph_lc)
        price = view.findViewById(R.id.price_tv)
        spinner = view.findViewById(R.id.range_spinner)
    }

    private fun setSpinner(spinner: MaterialSpinner){
        spinner.setItems(LIST_PARAMETERS)
        spinner.setOnItemSelectedListener { _, position, _, _ ->
            viewModel.setCandleData(LIST_UNIX[position])
        }
    }

    private fun buildHelper() = context?.let { ItemLineChartHelper(it, chart, resources) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setViewModelObservableEvents(activity as CompanyActivity)
    }

    private fun setViewModelObservableEvents(activity: CompanyActivity){
        viewModel = ViewModelProvider(activity).get(ItemViewModel::class.java)
        viewModel.liveValue.observe(activity) { value ->
            Log.d(Stock.TAG, "CandlesFragment: viewModel: observe: $value")
            price.text = "$$value"
        }
        viewModel.liveSymbol.observe(activity) { symbol ->
            Log.d(Stock.TAG, "CandlesFragment: viewModel: observe: $symbol")
            viewModel.setData()
        }
        viewModel.liveCandleList.observe(activity) { list ->
            Log.d(Stock.TAG, "CandlesFragment: viewModel: observe: $list")
            helper.setLineData(closeList = list.c, highList = list.h, timeList = list.t)
        }
    }
}