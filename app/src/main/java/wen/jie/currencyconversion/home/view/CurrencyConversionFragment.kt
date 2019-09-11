package wen.jie.currencyconversion.home.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_fragment_currency_conversion.*
import wen.jie.currencyconversion.AbstractBaseFragment
import wen.jie.currencyconversion.AbstractViewModel
import wen.jie.currencyconversion.R
import wen.jie.currencyconversion.home.model.CurrencyListData
import wen.jie.currencyconversion.home.model.CurrencyLiveData
import wen.jie.currencyconversion.home.viewmodel.CurrencyConversionFragmentViewModel
import wen.jie.currencyconversion.home.viewmodel.CurrencyConversionFragmentViewModelRxEvent
import wen.jie.currencyconversion.stores.CurrencyStore
import java.util.*

class CurrencyConversionFragment : AbstractBaseFragment(), AdapterView.OnItemSelectedListener, TextWatcher {
    private var currencyLiveData: List<Pair<String, Float>>? = null
    private var currencyListData: List<Pair<String, String>>? = null
    private var currencyListAdapter: CurrencyConversionListAdapter? = null
    private var dropDownMenuAdapter: DropDownMenuAdapter? = null
    private var selectedCurrency: String? = null

    companion object {
        private const val TAG = "CurrencyConversionFragment"
    }

    interface CurrencyConversionFragmentObserver {
        // if we would like to interact from fragment to activity, we can communicate through this interface by adding new functions
    }

    private var viewModel: CurrencyConversionFragmentViewModel? = null
    override val compositeDisposable: CompositeDisposable
        get() = super.compositeDisposable

    override fun getViewModel(): AbstractViewModel? {
        viewModel = ViewModelProviders.of(this).get(CurrencyConversionFragmentViewModel::class.java)
        return viewModel
    }

    override fun getFragmentTag(): String {
        return TAG
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_fragment_currency_conversion
    }

    override fun init(view: View) {
        amount_edit_text.addTextChangedListener(this)
        rates_list.layoutManager = LinearLayoutManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.refresh()
    }

    override fun onResume() {
        super.onResume()
        viewModel?.getCurrencyInfo(CurrencyStore.lastTimeFetched, Calendar.getInstance().timeInMillis, CurrencyStore.currencyLiveData, CurrencyStore.currencyListData)
    }

    override fun childHandleRxEvent(key: String, value: Any?) {
        super.childHandleRxEvent(key, value)

        when (key) {
            CurrencyConversionFragmentViewModelRxEvent.GET_CURRENCY_LIVE_SUCCESS -> {
                value?.let {
                    val currencyLiveData: CurrencyLiveData = value as CurrencyLiveData
                    viewModel?.storeCurrencyLiveData(currencyLiveData)
                    setUpRecyclerView(currencyLiveData)
                }
            }
            CurrencyConversionFragmentViewModelRxEvent.GET_CURRENCY_LIST_SUCCESS -> {
                value?.let {
                    val currencyListData: CurrencyListData = value as CurrencyListData
                    viewModel?.storeCurrencyListData(currencyListData)
                    setUpDropdownMenu(currencyListData)
                }
            }
            CurrencyConversionFragmentViewModelRxEvent.REFRESH_CURRENCY_LIST_SUCCESS -> {
                value?.let {
                    if (value is Map<*, *>) {
                        refreshRecyclerView(value.toList() as List<Pair<String, Float>>)
                    }
                }
            }
            CurrencyConversionFragmentViewModelRxEvent.REFRESH_EVERY_PERIOD -> {
                value?.let {
                    if (value as Boolean) {
                        viewModel?.getCurrencyLiveData()
                        viewModel?.getCurrencyListData()
                    }
                }
            }
            else -> {

            }
        }
    }

    private fun setUpDropdownMenu(currencyListData: CurrencyListData) {
        val data = currencyListData.currencies.toList()
        this.currencyListData = data
        dropDownMenuAdapter = DropDownMenuAdapter(context, data)
        dropDownMenuAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropDownMenuAdapter?.let {
            currency_list.adapter = it
        }

        currency_list.onItemSelectedListener = this
    }

    private fun refreshRecyclerView(currencyList: List<Pair<String, Float>>) {
        currencyListAdapter?.let {
            it.currencyLiveData = currencyList
            it.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclerView(currencyLiveData: CurrencyLiveData) {
        val data = viewModel?.calculateExchangeRate(selectedCurrency?: currencyLiveData.source, amount_edit_text.text.toString(), currencyLiveData)?.toList()
        data?.let {
            this.currencyLiveData = it
            currencyListAdapter = CurrencyConversionListAdapter(it, context, currencyLiveData.source)

            currencyListAdapter?.let { adapter ->
                rates_list.adapter = adapter
            }
        }
    }

    override fun showLoading(show: Boolean) {
        if (show) {
            progress_bar?.visibility = View.VISIBLE
        } else {
            progress_bar?.visibility = View.GONE
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currencyListData?.let {
            selectedCurrency = it[position].first
            CurrencyStore.currencyLiveData?.let { currencyLiveData ->
                viewModel?.calculateExchangeRateList(it[position].first, if (!amount_edit_text.text.isNullOrEmpty()) amount_edit_text.text.toString() else resources.getString(R.string.amount_place_holder), currencyLiveData)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun afterTextChanged(s: Editable?) {
        selectedCurrency?.let {
            CurrencyStore.currencyLiveData?.let { currencyLiveData ->
                viewModel?.calculateExchangeRateList(it, if (!s.isNullOrEmpty()) s.toString() else resources.getString(R.string.amount_place_holder), currencyLiveData)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
}