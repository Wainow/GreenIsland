package com.wainow.domain.entities

interface Stock{
    companion object{
        const val TAG: String = "StockLogs"
    }
}

data class SearchResult(
    val count: Long,
    val result: List<CompanyInfo>
    ) : Stock

data class CompanyInfo(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)

data class CompanyProfile(
    val currency: String,
    val name: String,
    val ticker: String,
    val logo: String,
    var price: Double,
    var open: Double,
    var isFavorite: Boolean = false
    ) : Stock

data class Quote(
    val c: Double,
    val pc: Double,
)

data class CandlesInfo(
    val c: List<Double>,
    val h: List<Double>,
    val l: List<Double>,
    val o: List<Double>,
    val v: List<Double>,
    val t: List<Double>,
    val s: String,
)

data class CompanyNewsItem(
    val category: String,
    val datetime: Double,
    val headline: String,
    val id: Double,
    var image: String,
    var related: String,
    var source: String,
    var summary: String,
    var url: String
)