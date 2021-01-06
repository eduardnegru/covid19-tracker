package com.example.covidtracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        var stringRequest = StringRequest(Request.Method.GET, URL_STATS_GLOBAL, Response.Listener{response ->
            handleResponse(response, addSpinner)
        }, Response.ErrorListener {error ->
            if (addSpinner)
                spinner?.stop()
            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
        })

        var requestQueue = Volley.newRequestQueue(context)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}