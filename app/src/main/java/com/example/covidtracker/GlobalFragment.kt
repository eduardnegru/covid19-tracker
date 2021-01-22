package com.example.covidtracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*


class GlobalFragment : Fragment() {
    private var spinner: LoadingSpinner? = null

    lateinit var textViewNewCases: TextView
    lateinit var textViewTotalConfirmed: TextView
    lateinit var textViewNewDeaths: TextView
    lateinit var textViewTotalDeaths: TextView
    lateinit var textViewNewRecovered: TextView
    lateinit var textViewTotalRecovered: TextView
    lateinit var pullToRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        spinner = LoadingSpinner(activity as MainActivity)

        textViewNewCases = view.findViewById(R.id.text_new_cases)
        textViewTotalConfirmed = view.findViewById(R.id.text_total_confirmed)
        textViewNewDeaths = view.findViewById(R.id.text_new_deaths)
        textViewTotalDeaths = view.findViewById(R.id.text_total_deaths)
        textViewNewRecovered = view.findViewById(R.id.text_new_recovered)
        textViewTotalRecovered = view.findViewById(R.id.text_total_recovered)

        pullToRefresh = view.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            loadData(false)
            pullToRefresh.isRefreshing = false
        }

        loadData(true)

        return view
    }

    private fun loadData(addSpinner: Boolean = true) {

        if (addSpinner)
            spinner?.start()

        val URL_STATS_GLOBAL = "https://api.covid19api.com/summary"
        val stringRequest = StringRequest(Request.Method.GET, URL_STATS_GLOBAL, { response ->
            handleResponse(response, addSpinner)
        }, {error ->
            if (addSpinner)
                spinner?.stop()
            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
        })

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    // format large numbers by adding commas for thousands
    private fun formatNumber(number: Long): String {
        return NumberFormat.getInstance(Locale.US).format(number)
    }

    private fun handleResponse(response: String, addSpinner: Boolean) {
        val json = JSONObject(response)
        val globalStatsJson = json.getJSONObject("Global")
        val newConfirmed = globalStatsJson.getLong("NewConfirmed")
        val totalConfirmed = globalStatsJson.getLong("TotalConfirmed")
        val newDeaths = globalStatsJson.getLong("NewDeaths")
        val totalDeaths = globalStatsJson.getLong("TotalDeaths")
        val newRecovered = globalStatsJson.getLong("NewRecovered")
        val totalRecovered = globalStatsJson.getLong("TotalRecovered")

        textViewNewCases.text = formatNumber(newConfirmed)
        textViewTotalConfirmed.text = formatNumber(totalConfirmed)
        textViewNewDeaths.text = formatNumber(newDeaths)
        textViewTotalDeaths.text = formatNumber(totalDeaths)
        textViewNewRecovered.text = formatNumber(newRecovered)
        textViewTotalRecovered.text = formatNumber(totalRecovered)

        if (addSpinner)
            spinner?.stop()
    }
}