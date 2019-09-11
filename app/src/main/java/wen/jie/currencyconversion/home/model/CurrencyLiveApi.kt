package wen.jie.currencyconversion.home.model

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import wen.jie.currencyconversion.Config

interface CurrencyLiveApi {
    companion object {
        private const val SUFFIX = "live"

        var BASE_URL = Config.BASE_API_URL
    }

    // Get currency list
    @Headers("Content-Type: application/json")
    @GET(SUFFIX)
    fun getCurrencyLiveData(
        @Query("access_key") access_key: String = Config.ACCESS_KEY
    ): Observable<CurrencyLiveData>
}