package wen.jie.currencyconversion.home.model

import wen.jie.currencyconversion.services.ApiFactory

class CurrencyListApiService {
    fun create() : CurrencyListApi {
        return ApiFactory.createService(CurrencyListApi.BASE_URL, CurrencyListApi::class.java)
    }
}