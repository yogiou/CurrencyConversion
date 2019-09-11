package wen.jie.currencyconversion.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import wen.jie.currencyconversion.BaseViewHolder
import wen.jie.currencyconversion.R
import wen.jie.currencyconversion.home.model.CurrencyLiveData

class CurrencyConversionListAdapter constructor(data: List<Pair<String, Float>>, c: Context?, sourceCurrency: String) : RecyclerView.Adapter<BaseViewHolder>() {
    var currencyLiveData: List<Pair<String, Float>> = data

    private var mInflater: LayoutInflater = LayoutInflater.from(c)
    private val source: String = sourceCurrency
    private val context: Context? = c

    companion object {
        const val TAG = "CurrencyConversionListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = mInflater.inflate(R.layout.layout_currency_rate_cell, parent, false)
        return CurrencyListViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        val data: Pair<String, Float> = currencyLiveData[position]

        val holder = viewHolder as CurrencyListViewHolder

        holder.currencyLabel.text = context?.resources?.getString(R.string.currency_value, String.format("%.2f", data.second), data.first.replaceFirst(source, ""))
    }

    override fun getItemCount(): Int {
        return currencyLiveData.size
    }

    inner class CurrencyListViewHolder internal constructor(itemView: View) : BaseViewHolder(itemView) {
        internal var currencyLabel: AppCompatTextView = itemView.findViewById(R.id.currency_label)
    }
}