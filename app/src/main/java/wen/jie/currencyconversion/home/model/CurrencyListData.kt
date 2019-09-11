package wen.jie.currencyconversion.home.model

import com.squareup.moshi.Json
import java.io.Serializable

data class CurrencyListData(
    @field:Json(name = "success")
    val success: Boolean,
    @field:Json(name = "terms")
    val terms: String,
    @field:Json(name = "privacy")
    val privacy: String,
    @field:Json(name = "currencies")
    val currencies: Map<String, String>
) : Serializable