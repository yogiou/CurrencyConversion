package wen.jie.currencyconversion.home.viewmodel

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test

import org.junit.Before
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import wen.jie.currencyconversion.helper.rxBus.RxBus
import wen.jie.currencyconversion.home.model.CurrencyListApi
import wen.jie.currencyconversion.home.model.CurrencyListData
import wen.jie.currencyconversion.home.model.CurrencyLiveApi
import wen.jie.currencyconversion.home.model.CurrencyLiveData
import java.util.*

class CurrencyConversionFragmentViewModelTest {
    @Mock
    private lateinit var rxBus: RxBus

    @Mock
    private lateinit var currencyListApi: CurrencyListApi

    @Mock
    private lateinit var currencyLiveApi: CurrencyLiveApi

    @InjectMocks
    private lateinit var currencyConversionFragmentViewModel: CurrencyConversionFragmentViewModel

    @Mock
    private lateinit var currencyListData: CurrencyListData

    @Mock
    private lateinit var currencyLiveData: CurrencyLiveData

    @Before
    fun before() {
        RxJavaPlugins.setIoSchedulerHandler { _ -> Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler{ _ -> Schedulers.trampoline() }
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getCurrencyListDataSuccess() {
        given(currencyListApi.getCurrencyList()).willAnswer { Observable.just(currencyListData) }
        currencyConversionFragmentViewModel.getCurrencyListData()
        verify(rxBus, times(3)).send(any())
    }

    @Test
    fun getCurrencyListDataError() {
        val error = Throwable()
        Mockito.`when`(currencyListApi.getCurrencyList()).thenReturn( Observable.error(error) )
        currencyConversionFragmentViewModel.getCurrencyListData()
        verify(rxBus, times(3)).send(any())
    }

    @Test
    fun getCurrencyLiveDataSuccess() {
        given(currencyLiveApi.getCurrencyLiveData()).willAnswer { Observable.just(currencyLiveData) }
        currencyConversionFragmentViewModel.getCurrencyLiveData()
        verify(rxBus, times(3)).send(any())
    }

    @Test
    fun getCurrencyLiveDataFail() {
        val error = Throwable()
        Mockito.`when`(currencyLiveApi.getCurrencyLiveData()).thenReturn( Observable.error(error) )
        currencyConversionFragmentViewModel.getCurrencyLiveData()
        verify(rxBus, times(3)).send(any())
    }

    @Test
    fun getCurrencyInfoCase1() {
        val lastTimeFetched = -1L
        val currentTime = Calendar.getInstance().timeInMillis
        currencyConversionFragmentViewModel.getCurrencyInfo(lastTimeFetched, currentTime, currencyLiveData, currencyListData)
        verify(rxBus, times(6)).send(any())
    }

    @Test
    fun getCurrencyInfoCase2() {
        val lastTimeFetched = Calendar.getInstance().timeInMillis
        val currentTime = Calendar.getInstance().timeInMillis
        currencyConversionFragmentViewModel.getCurrencyInfo(lastTimeFetched, currentTime, currencyLiveData, currencyListData)
        verify(rxBus, times(2)).send(any())
    }

    @Test
    fun getCurrencyInfoCase3() {
        val lastTimeFetched = Calendar.getInstance().timeInMillis
        val currentTime = Calendar.getInstance().timeInMillis
        currencyConversionFragmentViewModel.getCurrencyInfo(lastTimeFetched, currentTime, null, currencyListData)
        verify(rxBus, times(4)).send(any())
    }

    @Test
    fun getCurrencyInfoCase4() {
        val lastTimeFetched = Calendar.getInstance().timeInMillis
        val currentTime = Calendar.getInstance().timeInMillis
        currencyConversionFragmentViewModel.getCurrencyInfo(lastTimeFetched, currentTime, currencyLiveData, null)
        verify(rxBus, times(4)).send(any())
    }

    @Test
    fun getCurrencyInfoCase5() {
        val lastTimeFetched = Calendar.getInstance().timeInMillis
        val currentTime = Calendar.getInstance().timeInMillis
        currencyConversionFragmentViewModel.getCurrencyInfo(lastTimeFetched, currentTime, null, null)
        verify(rxBus, times(6)).send(any())
    }

    @Test
    fun calculateExchangeRateList() {
        currencyConversionFragmentViewModel.calculateExchangeRateList("HKD", "1.0", currencyLiveData)
        verify(rxBus, times(1)).send(any())
    }
}