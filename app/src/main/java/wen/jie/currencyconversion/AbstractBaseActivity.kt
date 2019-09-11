package wen.jie.currencyconversion

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import wen.jie.currencyconversion.helper.rxBus.rxBusEvent.SupportedRxBusEventKeys
import wen.jie.currencyconversion.helper.rxBus.RxEvent
import wen.jie.currencyconversion.utils.BaseErrorHandler
import wen.jie.currencyconversion.utils.BaseErrorHelper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.apache.commons.lang3.StringUtils


abstract class AbstractBaseActivity : AppCompatActivity() {
    internal open val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var baseErrorHandler: BaseErrorHandler = BaseErrorHandler(object : BaseErrorHandler.HandlerListener {
        override fun getContext(): Context? {
            return this@AbstractBaseActivity.baseContext
        }

        override fun getFragmentManager(): FragmentManager {
            return getFragmentManager()
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        init()
        registerRxBus()
    }

    internal abstract fun init()

    override fun onDestroy() {
        showLoading(false)
        compositeDisposable.clear()
        super.onDestroy()
    }

    internal abstract fun getLayoutId(): Int

    internal open fun getViewModel(): AbstractViewModel? {
        return null
    }

    internal open fun registerRxBus() {
        val viewModel = getViewModel()
        viewModel?.let { vm ->
            vm.getObservable()?.subscribeBy {
                    rxEvent -> handleRxEvent(rxEvent)
            }?.addTo(compositeDisposable)
        }
    }

    private fun handleRxEvent(rxEvent: RxEvent<*, *>) {
        val key: Any = rxEvent.key as Any
        if (key is String) {
            handleSharedSupportKey(key, rxEvent.value)
        }
    }

    internal open fun handleSharedSupportKey(key: String, value: Any?) {
        when {
            StringUtils.equalsIgnoreCase(SupportedRxBusEventKeys.SHOW_LOADING, key) -> {
                showLoading(value as Boolean)
            }
            StringUtils.equalsIgnoreCase(BaseErrorHelper.EVENT_KEY_BASE_ERROR, key) -> {
                when (value) {
                    is Int -> handleBaseErrorByCode(value)
                    is Throwable -> handleBaseError(value)
                }
            }
            else -> {
                childHandleRxEvent(key, value)
            }
        }
    }

    abstract fun showLoading(show: Boolean)

    private fun handleBaseError(error: Throwable) {
        baseErrorHandler.handleBaseError(error)
    }

    private fun handleBaseErrorByCode(error: Int) {
        baseErrorHandler.handleBaseError(error)
    }

    internal open fun childHandleRxEvent(key: String, value: Any?) {
        // override by sub classes
    }
}