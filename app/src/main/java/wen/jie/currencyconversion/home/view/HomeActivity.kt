package wen.jie.currencyconversion.home.view

import android.content.Context
import android.os.Bundle
import wen.jie.currencyconversion.AbstractBaseActivity
import wen.jie.currencyconversion.R
import wen.jie.currencyconversion.utils.SharedPreferencesHelper


class HomeActivity : AbstractBaseActivity() {
    companion object {
        private const val TAG = "HomeActivity"
        lateinit var appContext: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_home)
        appContext = applicationContext
    }

    override fun init() {
        val newFragment = CurrencyConversionFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, newFragment).commit()
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_activity_home
    }

    override fun showLoading(show: Boolean) { }

    override fun onDestroy() {
        super.onDestroy()
        SharedPreferencesHelper.clear(appContext)
    }
}
