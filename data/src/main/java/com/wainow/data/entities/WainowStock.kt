package com.wainow.data.entities

import com.google.gson.annotations.SerializedName

data class WainowStock(
    @SerializedName("Name")
    val name: String,
    @SerializedName("Sector")
    val sector: String,
    @SerializedName("Symbol")
    val symbol: String
)
