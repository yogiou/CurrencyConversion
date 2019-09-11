package wen.jie.currencyconversion.home.viewmodel

import wen.jie.currencyconversion.helper.rxBus.RxEvent

class CurrencyConversionFragmentViewModelRxEvent<T>(k: String, v: T) : RxEvent<String, T>(k, v) {
    companion object {
        const val GET_CURRENCY_LIST_SUCCESS = "GET_CURRENCY_LIST_SUCCESS"
        const val GET_CURRENCY_LIVE_SUCCESS = "GET_CURRENCY_LIVE_SUCCESS"
        const val REFRESH_CURRENCY_LIST_SUCCESS = "REFRESH_CURRENCY_LIST_SUCCESS"
        const val REFRESH_EVERY_PERIOD = "REFRESH_EVERY_PERIOD"
    }
}