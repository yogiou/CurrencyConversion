package wen.jie.currencyconversion.home.model

import wen.jie.currencyconversion.services.ApiFactory

class CurrencyLiveApiService {
    fun create() : CurrencyLiveApi {
        return ApiFactory.createService(CurrencyLiveApi.BASE_URL, CurrencyLiveApi::class.java)
    }
}