package wen.jie.currencyconversion.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import wen.jie.currencyconversion.R
import wen.jie.currencyconversion.home.model.CurrencyListData

class DropDownMenuAdapter constructor(context: Context?, data: List<Pair<String, String>>) : ArrayAdapter<Pair<String, String>>(context, android.R.layout.simple_spinner_dropdown_item, data) {
    private var currencyListData: List<Pair<String, String>> = data

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View{
        val view: ConstraintLayout = convertView as ConstraintLayout? ?: LayoutInflater.from(context).inflate(R.layout.layout_currency_list_cell, parent, false) as ConstraintLayout
        val currencyLabel = view.findViewById<AppCompatTextView>(R.id.currency_label)
        currencyLabel.text = currencyListData[position].second
        return view
    }
}