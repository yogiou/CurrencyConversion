package wen.jie.currencyconversion.home.viewmodel

import wen.jie.currencyconversion.helper.rxBus.rxBusEvent.SupportedRxBusEventKeys
import wen.jie.currencyconversion.utils.BaseErrorHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import wen.jie.currencyconversion.AbstractViewModel
import wen.jie.currencyconversion.home.model.*
import wen.jie.currencyconversion.home.viewmodel.CurrencyConversionFragmentViewModelRxEvent.Companion.REFRESH_CURRENCY_LIST_SUCCESS
import wen.jie.currencyconversion.stores.CurrencyStore
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class CurrencyConversionFragmentViewModel : AbstractViewModel() {
    companion object {
        private const val FETCH_PERIOD = 60 * 30 * 1000L // 30 mins
    }

    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val currencyLiveApi: CurrencyLiveApi = CurrencyLiveApiService().create()
    private val currencyListApi: CurrencyListApi = CurrencyListApiService().create()

    override fun getEventType(): Class<*> {
        return CurrencyConversionFragmentViewModelRxEvent::class.java
    }

    private fun postEvent(key: String, value: Any) {
        post(CurrencyConversionFragmentViewModelRxEvent(key, value))
    }

    fun getCurrencyListData() {
        currencyListApi.getCurrencyList()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                post(CurrencyConversionFragmentViewModelRxEvent(SupportedRxBusEventKeys.SHOW_LOADING, true))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                post(CurrencyConversionFragmentViewModelRxEvent(SupportedRxBusEventKeys.SHOW_LOADING, false))
            }
            .subscribeBy(
                onNext = { currencyListData ->
                    postEvent(CurrencyConversionFragmentViewModelRxEvent.GET_CURRENCY_LIST_SUCCESS, currencyListData)
                },
                onError = { error ->
                    post(CurrencyConversionFragmentViewModelRxEvent(BaseErrorHelper.EVENT_KEY_BASE_ERROR, error))
                }
            ).addTo(compositeDisposable)
    }

    fun getCurrencyLiveData() {
        currencyLiveApi.getCurrencyLiveData()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                post(CurrencyConversionFragmentViewModelRxEvent(SupportedRxBusEventKeys.SHOW_LOADING, true))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                post(CurrencyConversionFragmentViewModelRxEvent(SupportedRxBusEventKeys.SHOW_LOADING, false))
            }
            .subscribeBy(
                onNext = { currencyLiveData ->
                    postEvent(CurrencyConversionFragmentViewModelRxEvent.GET_CURRENCY_LIVE_SUCCESS, currencyLiveData)
                },
                onError = { error ->
                    post(CurrencyConversionFragmentViewModelRxEvent(BaseErrorHelper.EVENT_KEY_BASE_ERROR, error))
                }
            ).addTo(compositeDisposable)
    }

    fun storeCurrencyLiveData(currencyLiveData: CurrencyLiveData) {
        CurrencyStore.lastTimeFetched = Calendar.getInstance().timeInMillis
        CurrencyStore.currencyLiveData = currencyLiveData
    }

    fun storeCurrencyListData(currencyListData: CurrencyListData) {
        CurrencyStore.currencyListData = currencyListData
    }

    fun getCurrencyInfo(lastTimeFetched: Long, currentTime: Long, currencyLiveData: CurrencyLiveData?, currencyListData: CurrencyListData?) {
        if (lastTimeFetched == -1L || currentTime - lastTimeFetched >= FETCH_PERIOD) {
            getCurrencyLiveData()
            getCurrencyListData()
        } else {
            currencyLiveData?.let {
                postEvent(CurrencyConversionFragmentViewModelRxEvent.GET_CURRENCY_LIVE_SUCCESS, it)
            } ?: run {
                getCurrencyLiveData()
            }

            currencyListData?.let {
                postEvent(CurrencyConversionFragmentViewModelRxEvent.GET_CURRENCY_LIST_SUCCESS, it)
            } ?: run {
                getCurrencyListData()
            }
        }
    }

    fun calculateExchangeRateList(currency: String, amount: String, currencyLiveData: CurrencyLiveData) {
        Observable.just(calculateExchangeRate(currency, amount, currencyLiveData))
            .subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe{ result ->
                postEvent(REFRESH_CURRENCY_LIST_SUCCESS, result)
            }?.addTo(compositeDisposable)
    }

    fun calculateExchangeRate(currency: String, amount: String, currencyLiveData: CurrencyLiveData): Map<String, Float> {
        val amountValue = amount.toFloatOrNull()?: 0.0f

        val rate = getExchangeRate(currency, currencyLiveData)

        val result: HashMap<String, Float> = HashMap()

        currencyLiveData.quotes.forEach {(key, value) ->
            result[key] = value / rate * amountValue
        }

        return result
    }

    private fun getExchangeRate(currency: String, currencyLiveData: CurrencyLiveData?): Float {
        currencyLiveData?.let {
            val source = it.source
            val rateMaps = it.quotes
            if (rateMaps.containsKey(source + currency)) {
                return rateMaps[source + currency]?: 0.0f
            }
        }

        return 0.0f
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun refresh() {
        Observable.timer(FETCH_PERIOD, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .repeat()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                postEvent(CurrencyConversionFragmentViewModelRxEvent.REFRESH_EVERY_PERIOD, true)
            }
            .addTo(compositeDisposable)
    }
}