package com.example.covidtracker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList


class DropdownAdapter2(var context: Context, var countries: ArrayList<Country>, var filterList: ArrayList<Country>) :
    RecyclerView.Adapter<DropdownAdapter2.HolderCountry>(), Filterable {

    var filter : CountryFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCountry {
        val view : View = LayoutInflater.from(context).inflate(R.layout.country_row, parent, false)
        return HolderCountry(view)
    }

    override fun onBindViewHolder(holder: HolderCountry, position: Int) {
        val country : Country = countries.get(position)
        Log.d("countries", countries.toString())
        Log.d("country", country.toString())
        Log.d("position", position.toString())
        holder.countryView.text = country.name
        holder.casesView.text = country.totalConfirmed
        holder.todayCasesView.text = country.newConfirmed
        holder.deathsView.text = country.totalDeaths
        holder.todayDeathsView.text = country.newDeaths
        holder.recoveredView.text = country.totalRecovered
        holder.todayRecoveredView.text = country.newRecovered
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    class HolderCountry(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var countryView : TextView = itemView.findViewById(R.id.countryTv);
        var casesView : TextView = itemView.findViewById(R.id.casesTv);
        var todayCasesView : TextView = itemView.findViewById(R.id.todayCasesTv);
        var deathsView : TextView = itemView.findViewById(R.id.totalDeathsTv);
        var todayDeathsView : TextView = itemView.findViewById(R.id.todayDeathsTv);
        var recoveredView : TextView = itemView.findViewById(R.id.totalRecoveredTv);
        var todayRecoveredView : TextView = itemView.findViewById(R.id.todayRecoveredTv);
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CountryFilter(this, filterList)
        }
        return filter as CountryFilter
    }


}

private operator fun CharSequence.invoke(name: String) {

}
