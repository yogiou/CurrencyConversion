package wen.jie.currencyconversion.stores

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import wen.jie.currencyconversion.home.model.CurrencyListData
import wen.jie.currencyconversion.home.model.CurrencyLiveData
import wen.jie.currencyconversion.home.view.HomeActivity
import wen.jie.currencyconversion.utils.SharedPreferencesConstant.KEY_CURRENCY_LIST_DATA
import wen.jie.currencyconversion.utils.SharedPreferencesConstant.KEY_CURRENCY_LIVE_DATA
import wen.jie.currencyconversion.utils.SharedPreferencesConstant.KEY_LAST_TIME_FETCHED
import wen.jie.currencyconversion.utils.SharedPreferencesHelper
import wen.jie.currencyconversion.utils.edit
import wen.jie.currencyconversion.utils.setValue
import java.util.*

class CurrencyStore() {
    companion object {
        private val context: Context
            get() = HomeActivity.appContext

        private val moshiBuilder = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()

        var lastTimeFetched: Long
            set (lastTimeFetched) {
                SharedPreferencesHelper.defaultPrefs(context).setValue(KEY_LAST_TIME_FETCHED, lastTimeFetched)
            }
            get() {
                return SharedPreferencesHelper.defaultPrefs(context).getLong(KEY_LAST_TIME_FETCHED, -1)
            }

        var currencyListData: CurrencyListData?
            set (currencyListData) {
                currencyListData?.let {
                    val value: String = moshiBuilder.adapter(CurrencyListData::class.java).toJson(it)

                    SharedPreferencesHelper.defaultPrefs(context).setValue(KEY_CURRENCY_LIST_DATA, value)
                } ?: SharedPreferencesHelper.defaultPrefs(context).edit {
                    it.remove(KEY_CURRENCY_LIST_DATA)
                }

            }
            get() {
                val result = SharedPreferencesHelper.defaultPrefs(context).getString(KEY_CURRENCY_LIST_DATA, null)

                result?.let {
                    return moshiBuilder.adapter(CurrencyListData::class.java).fromJson(it)
                }

                return null
            }

        var currencyLiveData: CurrencyLiveData?
            set (currencyListData) {
                currencyListData?.let {
                    val value: String = moshiBuilder.adapter(CurrencyLiveData::class.java).toJson(it)

                    SharedPreferencesHelper.defaultPrefs(context).setValue(KEY_CURRENCY_LIVE_DATA, value)
                } ?: SharedPreferencesHelper.defaultPrefs(context).edit {
                    it.remove(KEY_CURRENCY_LIVE_DATA)
                }
            }
            get() {
                val result = SharedPreferencesHelper.defaultPrefs(context).getString(KEY_CURRENCY_LIVE_DATA, null)

                result?.let {
                    return moshiBuilder.adapter(CurrencyLiveData::class.java).fromJson(it)
                }

                return null
            }
    }
}