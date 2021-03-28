package com.wainow.data.api
import com.wainow.domain.entities.*

import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET;
import retrofit2.http.Query;

interface FinnhubAPIService {

    companion object{
        const val FINNHUB_DEFAULT_TOKEN = "c0m00gf48v6p8fvj10q0"
        const val FINNHUB_SANDBOX_TOKEN = "sandbox_c0m00gf48v6p8fvj10qg"
    }

    @GET("/api/v1/search?")
    fun searchStock(
        @Query("q") q: String = "",
        @Query("token") token: String = FINNHUB_DEFAULT_TOKEN
    ) : Flowable<SearchResult>

    @GET("/api/v1/stock/profile2?")
    fun getCompanyProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String = FINNHUB_DEFAULT_TOKEN
    ) : Flowable<CompanyProfile>

    @GET("/api/v1/quote?")
    fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String = FINNHUB_DEFAULT_TOKEN
    ) : Flowable<Quote>

    @GET("/api/v1/stock/candle?")
    fun getCompanyCandles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String,
        @Query("from") from: Long,
        @Query("to") to: Long = System.currentTimeMillis() / 1000L,
        @Query("token") token: String = FINNHUB_DEFAULT_TOKEN
    ): Flowable<CandlesInfo>

    @GET("/api/v1/company-news?")
    fun getCompanyNews(
        @Query("symbol") symbol: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("token") token: String = FINNHUB_DEFAULT_TOKEN
    ): Flowable<List<CompanyNewsItem>>
}