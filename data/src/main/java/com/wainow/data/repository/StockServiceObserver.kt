package com.wainow.data.repository

import android.util.Log
import com.wainow.data.entities.StockService
import com.wainow.domain.entities.*
import com.wainow.domain.entities.Stock.Companion.TAG
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiFunction
import retrofit2.HttpException
import java.net.InetAddress
import java.net.UnknownHostException


class StockServiceObserver: StockService() {
    private val fService = getFINNHUBService()
    private val wService = getWainowService()

    fun getStockList(startIndex: Long, favoriteNames: List<String>): Flowable<List<CompanyProfile>> {
        return try {
            wService
                .getStockList()
                .flatMap { itemList -> Flowable.fromIterable(itemList) }
                .skip(startIndex)
                .take(10)
                .flatMap { item ->
                    Flowable.zip(
                        fService.getCompanyProfile(item.symbol),
                        fService.getQuote(item.symbol),
                        BiFunction { company, quote ->
                            return@BiFunction setCurrency(company, quote)
                        }
                    )
                }
                .filter { item -> item.currency == "USD" }
                .map { item -> setFavoriteItems(item, favoriteNames) }
                .buffer(10)
        } catch (e: HttpException){
            Flowable.error(e)
        }
    }

    private fun setCurrency(company: CompanyProfile, quote: Quote): CompanyProfile{
        Log.d(TAG, "StockServiceObserver: setCurrency for company $company")
        with(company){
            price = quote.c
            open = quote.pc
        }
        return company
    }

    private fun setFavoriteItems(company: CompanyProfile, favoriteNames: List<String>): CompanyProfile{
        if(favoriteNames.any{it == company.ticker}) company.isFavorite = true
        return company
    }

    /*
    * In this method i could use switchMap() for precise stock list
    * But actually if i would use switchMap() then after adding 4 stock value we would see: queue is full !?? exception
    * Therefore a flatMap() is a better solution than switchMap() for this task
     */
    fun setFavoriteList(list: List<String>, startIndex: Long): Flowable<List<CompanyProfile>> {
        return try {
            Flowable.fromIterable(list)
                .switchMap { item -> isInternetAvailable(item) }
                .skip(startIndex)
                .take(10)
                .flatMap { item ->
                    Flowable.zip(
                        fService.getCompanyProfile(item),
                        fService.getQuote(item),
                        BiFunction { company, quote ->
                            return@BiFunction setCurrency(company, quote)
                        }
                    )
                }
                .map { item ->
                    Log.d(TAG, "StockServiceObserver: setFavoriteList item: $item")
                    setFavoriteItems(item, list) }
                .buffer(10)
        } catch (e: HttpException){
            Flowable.error(e)
        }
    }

    fun searchStock(query: String, favoriteNames: List<String>): Flowable<List<CompanyProfile?>>{
        return try {
            fService
                .searchStock(query)
                .map { item ->
                    Log.d(TAG, "StockServiceObserver: searchStock result: ${item.result}")
                    item.result
                }
                .flatMap { itemList -> Flowable.fromIterable(itemList) }
                .filter { item -> !item.symbol.contains(".") }
                .take(5)
                .flatMap { item ->
                    Flowable.zip(
                        fService.getCompanyProfile(item.symbol),
                        fService.getQuote(item.symbol),
                        BiFunction { company, quote ->
                            return@BiFunction setCurrency(company, quote)
                        }
                    )
                }
                .filter { item -> item.currency == "USD" }
                .map { item -> setFavoriteItems(item, favoriteNames) }
                .buffer(5)
                .switchIfEmpty(Flowable.just(mutableListOf()))
        } catch (e: HttpException){
            Flowable.error(e)
        }
    }

    fun getCandles(symbol: String, resolution: String, from: Long): Flowable<CandlesInfo>{
        return try {
            fService
                .getCompanyCandles(symbol, resolution, from)
        } catch (e: HttpException){
            Flowable.error(e)
        }
    }

    fun getNews(symbol: String, from: String, to: String): Flowable<List<CompanyNewsItem>>{
        return try {
            fService
                .getCompanyNews(symbol, from, to)
        } catch (e: HttpException){
            Flowable.error(e)
        }
    }
    /*
    * I had some exceptions with work on favorite list when internet isn't available
    * That method doesn't allow get info from finnhub site if internet is gone
    * But actually i've using that only for favorite list
    * Because i don't have those exceptions on other lists
     */
    private fun isInternetAvailable(item: String): Flowable<String> {
        try {
            val address: InetAddress = InetAddress.getByName("www.google.com")
            if(!address.equals("")) return Flowable.just(item)
        } catch (e: UnknownHostException) {
            Log.d(TAG, "FavoriteStockViewModel: isInternetAvailable: false")
        }
        return Flowable.error(UnknownHostException())
    }
}