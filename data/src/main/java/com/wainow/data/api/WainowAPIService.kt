package com.wainow.data.api

import com.wainow.data.entities.WainowStock
import com.wainow.domain.entities.SearchResult
import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface WainowAPIService {
    @GET("/SnP500/constituents_json.json")
    fun getStockList() : Flowable<List<WainowStock>>
}