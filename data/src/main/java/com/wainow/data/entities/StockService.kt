package com.wainow.data.entities

import com.google.gson.GsonBuilder
import com.wainow.data.api.FinnhubAPIService
import com.wainow.data.api.WainowAPIService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

abstract class StockService{
    /*
    * Wainow it's my github account which contains list stock's name
     */
    companion object{
        const val FINNHUB_URL = "https://finnhub.io/"
        const val WAINOW_URL = "https://wainow.github.io/"
    }
    /*
    * Here i've created methods which contains ScalarsConverterLibrary
    * That was need only for testing some errors
     */
    private fun getGson() = GsonBuilder().setLenient().create()

    private fun getRetrofit(URL: String): Retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(getGson()))
        .build()

    private fun getScalarRetrofit(URL: String): Retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    fun getFINNHUBService(): FinnhubAPIService = getRetrofit(FINNHUB_URL).create(FinnhubAPIService::class.java)
    fun getScalarFINNHUBService(): FinnhubAPIService = getScalarRetrofit(FINNHUB_URL).create(FinnhubAPIService::class.java)
    fun getWainowService(): WainowAPIService = getRetrofit(WAINOW_URL).create(WainowAPIService::class.java)
}