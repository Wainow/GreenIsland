package com.wainow.island.ui.item

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.wainow.data.repository.StockServiceSubscriber
import com.wainow.domain.entities.CandlesInfo
import com.wainow.domain.entities.CompanyNewsItem
import com.wainow.domain.entities.Stock
import com.wainow.island.R
import com.wainow.island.custom.CustomMarkerView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*


class ItemViewModel : ViewModel() {
    var liveCandleList: MutableLiveData<CandlesInfo> = MutableLiveData()
    var liveSymbol: MutableLiveData<String> = MutableLiveData()
    var liveValue: MutableLiveData<Double> = MutableLiveData()
    var liveNews: MutableLiveData<List<CompanyNewsItem>> = MutableLiveData()
    var liveError: MutableLiveData<String> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()
    private var subscriber = StockServiceSubscriber()

    companion object{
        @SuppressLint("SimpleDateFormat")
        fun timeToString(time: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val date = Date(time * 1000)
            return sdf.format(date)
        }
        private val UNIX_YEAR = Pair(31536000, "M")
        private val UNIX_MONTH = Pair(2505600, "D")
        private val UNIX_WEEK = Pair(604800, "15")
        private val UNIX_DAY = Pair(86400, "1")
        private const val FIRST_DESC = "Highest stock value"
        private const val SECOND_DESC = "Closed stock value"
        private const val THIRD_DESC = "Derivative stock value"
        val LIST_UNIX = listOf(UNIX_YEAR, UNIX_MONTH, UNIX_WEEK, UNIX_DAY)
        val LIST_PARAMETERS = listOf("Year", "Month", "Week", "Day")
        val LIST_DESC = listOf(FIRST_DESC, SECOND_DESC, THIRD_DESC)
    }

    fun setData() {
        Log.d(Stock.TAG, "CompanyInfoViewModel: setData")
        setCandleData()
        setNewsData()
    }
    /*
    * LineChart custom library perfectly handle with errors
    * Because of that, i have no need to handle this here
     */
    fun setCandleData(unixData: Pair<Int, String> = Pair(31536000, "M")){
        Log.d(Stock.TAG, "CompanyInfoViewModel: setCandleData")
        liveSymbol.value?.let { symbol ->
            subscriber.getCandles(
                compositeDisposable,
                symbol,
                unixData.second,
                (System.currentTimeMillis() / 1000L) - unixData.first.toLong(),
                tryCallback = {
                    Log.d(Stock.TAG, "CompanyInfoViewModel: tryCallback: result: $it")
                    liveCandleList.value = it
                }, errorCallback = {
                    Log.d(Stock.TAG, "CompanyInfoViewModel: errorCallback: error: $it")
                }, finallyCallback = {
                    Log.d(Stock.TAG, "CompanyInfoViewModel: finallyCallback")
                })
        }
    }
    /*
    * I also left finallyCallback for adding new features if necessary
     */
    private fun setNewsData(){
        Log.d(Stock.TAG, "CompanyInfoViewModel: setNewsData")
        val unixCurrentTime = System.currentTimeMillis() / 1000L
        val unixWeekAgoTime = unixCurrentTime.minus(UNIX_WEEK.first)
        val currentTime = timeToString(unixCurrentTime)
        val weekAgoTime = timeToString(unixWeekAgoTime)
        liveSymbol.value?.let { symbol ->
            subscriber.getNews(compositeDisposable, symbol, weekAgoTime, currentTime,
                tryCallback = {
                    Log.d(Stock.TAG, "CompanyInfoViewModel: tryCallback: result: $it")
                    liveNews.value = it
                }, errorCallback = {
                    Log.d(Stock.TAG, "CompanyInfoViewModel: errorCallback: error: $it")
                    liveError.value = it
                }, finallyCallback = {
                    Log.d(Stock.TAG, "CompanyInfoViewModel: finallyCallback")
                })
        }
    }
}