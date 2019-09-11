package wen.jie.currencyconversion.home.model

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import wen.jie.currencyconversion.Config
import io.reactivex.Observable

interface CurrencyListApi {
    companion object {
        private const val SUFFIX = "list"

        var BASE_URL = Config.BASE_API_URL
    }

    // Get currency list
    @Headers("Content-Type: application/json")
    @GET(SUFFIX)
    fun getCurrencyList(
        @Query("access_key") access_key: String = Config.ACCESS_KEY
    ): Observable<CurrencyListData>
}