package com.example.covidtracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.blongho.country_data.World
import java.util.*


class DropdownAdapter(var context: Context, var countries: List<Country>) : BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.country_row, null)

        val flagView: ImageView = view.findViewById(R.id.flag)
        val nameView: TextView = view.findViewById(R.id.country)

        flagView.setImageResource(World.getFlagOf(countries[position].code.toLowerCase(Locale.ROOT)))
        nameView.text = countries[position].name

        return view
    }

    override fun getItem(position: Int): Any = Unit

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return countries.size
    }
}