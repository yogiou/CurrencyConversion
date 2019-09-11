package wen.jie.currencyconversion.home.model

import com.squareup.moshi.Json

data class CurrencyLiveData(
    @field:Json(name = "success")
    val success: Boolean,
    @field:Json(name = "terms")
    val terms: String,
    @field:Json(name = "privacy")
    val privacy: String,
    @field:Json(name = "timestamp")
    val timestamp: Long,
    @field:Json(name = "source")
    val source: String,
    @field:Json(name = "quotes")
    val quotes: Map<String, Float>
)