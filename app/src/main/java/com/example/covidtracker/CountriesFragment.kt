package com.example.covidtracker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class CountriesFragment : Fragment() {
    lateinit var searchTxt : EditText
    lateinit var sortButton : ImageButton
    lateinit var countryRv : RecyclerView

    lateinit var countries : ArrayList<Country>
    lateinit var dropdownAdapter: DropdownAdapter2

    val COUNTRY_URL : String = "https://api.covid19api.com/summary"

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {

        val view : View = inflater.inflate(R.layout.fragment_country, container, false)

        searchTxt = view.findViewById(R.id.searchTxt)
        sortButton = view.findViewById(R.id.sortButton)
        countryRv = view.findViewById(R.id.countryRv)

        loadData()
        searchTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                dropdownAdapter.getFilter().filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        val sortMenu : PopupMenu = PopupMenu(context, sortButton)
        sortMenu.menu.add(Menu.NONE, 0, 0, "Ascending")
        sortMenu.menu.add(Menu.NONE, 1, 1, "Descending")
        sortMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                if (item.itemId == 0) {
                    Collections.sort(countries, SortCountryUp())
                    dropdownAdapter.notifyDataSetChanged()
                } else if (item.itemId == 1) {
                    Collections.sort(countries, SortCountryDown())
                    dropdownAdapter.notifyDataSetChanged()
                }
                return false
            }
        })

        sortButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                sortMenu.show()
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val stringRequest = StringRequest(Request.Method.GET, COUNTRY_URL, { response ->
            handleResponse(response)
        }, { error ->
            handleError(error)
        })

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    private fun handleError(error: VolleyError) {
        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
    }

    private fun handleResponse(response: String) {
        Log.d("sosm", response)

        countries = ArrayList<Country>()
        countries.clear()

        val json = JSONObject(response)
        val jsonArray = json.getJSONArray("Countries")

        val gsonBuilder : GsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("dd/MM/yyyy hh::mm a")
        val gson = gsonBuilder.create()

        for (i in 0 until jsonArray.length()) {
            val name = jsonArray.getJSONObject(i).getString("Country")
            val code = jsonArray.getJSONObject(i).getString("CountryCode")
            val newConfirmed =  jsonArray.getJSONObject(i).getString("NewConfirmed")
            val totalConfirmed = jsonArray.getJSONObject(i).getString("TotalConfirmed")
            val newDeaths  = jsonArray.getJSONObject(i).getString("NewDeaths")
            val totalDeaths = jsonArray.getJSONObject(i).getString("TotalDeaths")
            val newRecovered = jsonArray.getJSONObject(i).getString("NewRecovered")
            val totalRecovered = jsonArray.getJSONObject(i).getString("TotalRecovered")
            val date = jsonArray.getJSONObject(i).getString("Date")
            val country = Country(name, code, "", newConfirmed, totalConfirmed, newDeaths, totalDeaths, newRecovered, totalRecovered, date)

            countries.add(country)
        }


        dropdownAdapter = DropdownAdapter2(context!!, countries, countries)

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        countryRv.layoutManager = llm
        countryRv.adapter = dropdownAdapter


    }

    class SortCountryUp : Comparator<Country> {
        override fun compare(o1: Country, o2: Country): Int {
            return o1.name.compareTo(o2.name)
        }
    }

    class SortCountryDown : Comparator<Country> {
        override fun compare(o1: Country, o2: Country): Int {
            return o2.name.compareTo(o1.name)
        }
    }

}