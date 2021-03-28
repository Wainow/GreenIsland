package com.wainow.island.ui.item

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import com.bumptech.glide.load.engine.Resource
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.wainow.domain.entities.Stock
import com.wainow.island.R
import com.wainow.island.custom.CustomMarkerView
import java.util.ArrayList


/*
* This class need for simplification in setting LineChart class
* Methods and parameters in this class using only for
* Appearance improvements and set data for this delightful chart
*/
class ItemLineChartHelper(
    private val context: Context,
    private val chart: LineChart,
    private val resources: Resources
) {
    private val listPurpleColors: ArrayList<Int> = arrayListOf(
        resources.getColor(R.color.colorLineBackground),
        resources.getColor(R.color.colorLineActually),
        resources.getColor(R.color.colorLineForeground)
    )

    companion object{
        const val LINE_CHART_COUNT = 3
    }

    /*
    * Entry class contains values inside itself
    * First chart is max stock value at define period
    * Second chart is the closed value of stock in this period
    * For third chart i calculate derivative and show it in chart
     */
    fun setLineData(closeList: List<Double>, highList: List<Double>, timeList: List<Double>){
        Log.d(Stock.TAG, "ItemViewModel: setLineData")
        val valuesLists = listOf<ArrayList<Entry>>(
            ArrayList(),
            ArrayList(),
            ArrayList()
        )

        for(i in 1 until closeList.size-1){
            val derivative = closeList[i + 1] - closeList[i - 1]
            val time = timeList[i].toFloat()
            valuesLists[0].add(Entry(time, highList[i].toFloat()))
            valuesLists[1].add(Entry(time, closeList[i].toFloat()))
            valuesLists[2].add(Entry(time, derivative.toFloat()))
        }
        val dataSets = ArrayList<LineDataSet>()
        repeat(LINE_CHART_COUNT) {
            dataSets.add(LineDataSet(valuesLists[it], ItemViewModel.LIST_DESC[it]))
            setDataSet(dataSets[it], listPurpleColors[it])
        }
        val data = LineData(
            dataSets as List<ILineDataSet>?
        )
        setChart(chart, data)
        setAxis(chart.xAxis)
        setAxis(chart.axisRight)
        setAxis(chart.axisLeft)
    }


    /*
    * In this method i just set my dataSet view
     */
    private fun setDataSet(dataSet: LineDataSet, backgroundColor: Int, isDrawValues: Boolean = true){
        with(dataSet){
            lineWidth = 3f
            circleRadius = 5f
            circleHoleRadius = 2.5f
            color = backgroundColor
            setCircleColor(backgroundColor)
            setValueTextColors(listOf(Color.WHITE))
            highLightColor = backgroundColor
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            setDrawFilled(true)
            fillAlpha = 255
            isHighlightEnabled = isDrawValues
            setDrawHighlightIndicators(isDrawValues)
            setDrawValues(false)
            fillColor = backgroundColor
        }
    }

    /*
    * In this method i just set my lineChart view
     */
    private fun setChart(chart: LineChart, source: LineData){
        val mv = CustomMarkerView(context, R.layout.price_item)
        with(chart){
            highlightValues(null)
            data = source
            legend.textColor = resources.getColor(R.color.colorBackgroundTab)
            legend.yEntrySpace = 5f
            legend.form = Legend.LegendForm.CIRCLE
            description.textColor = Color.WHITE
            setDrawGridBackground(false)
            setDrawBorders(false)
            setBorderWidth(0f)
            marker = mv
            setViewPortOffsets(0f, 150f, 0f, 0f)
            description.isEnabled = false
            post { invalidate() }
        }

        /*
        * Here i am trying to hide bound values because it isn't looks good in smartphone screen
        * For example you can delete this part and look on it: value indication just comes out from phone screen
        * But this part actually not the best solution, honestly i didn't have enough time to set up this correctly
         */
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if(h?.x == chart
                        .lineData
                        .dataSets[h?.dataSetIndex!!]
                        .getEntryForIndex(0)
                        .x
                    ||
                    h.x == chart
                        .lineData
                        .dataSets[h.dataSetIndex]
                        .getEntryForIndex(chart
                            .lineData.dataSets[h.dataSetIndex]
                            .entryCount-1)
                        .x)
                    chart.post { chart.highlightValues(null) }
                else
                    chart.post{ chart.highlightValue(h) }
            }

            override fun onNothingSelected() {}
        })
    }

    private fun setAxis(axisBase: AxisBase){
        with(axisBase){
            textColor = resources.getColor(R.color.colorBackground)
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }
    }
}