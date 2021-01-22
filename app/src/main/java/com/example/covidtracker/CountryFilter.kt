package com.example.covidtracker

import android.widget.Filter
import java.util.*
import kotlin.collections.ArrayList

class CountryFilter(var adapter: DropdownAdapter, var countryList: ArrayList<Country>) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var results : FilterResults = FilterResults()
        if (constraint != null && constraint.length > 0) {
            val newConstaint : CharSequence = constraint.toString().toUpperCase(Locale.getDefault())
            val filteredCountries : ArrayList<Country> = ArrayList<Country>()
            for (i in countryList.indices) {
                if (countryList[i].name.toUpperCase(Locale.getDefault()).contains(newConstaint)) {
                    filteredCountries.add(countryList[i])
                }
            }
            results.count = filteredCountries.size
            results.values = filteredCountries
        }
        else {
            results.count = countryList.size
            results.values = countryList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.countries = results!!.values as java.util.ArrayList<Country>
        adapter.notifyDataSetChanged()
    }

}