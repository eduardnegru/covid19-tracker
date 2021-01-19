package com.example.covidtracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : Fragment() {
    // TODO: Rename and change types of parameters

    val SHARED_PREFERENCES: String = "SHARED_PREFERENCES"
    val KEY_COUNTRY: String = "KEY_COUNTRY"
    val SHARED_PREFERENCES_ID: String = "covid_notification_id"

    private var param1: String? = null
    private var param2: String? = null

    private var countriesDropdown: Spinner? = null
    private var createNotificationButton: Button? = null
    private var cancelNotificationButton: Button? = null
    private var timePicker: TimePicker? = null
    private var scheduler: JobScheduler? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        scheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_notification, container, false)

        countriesDropdown = view.findViewById(R.id.countryDropdown)
        timePicker = view.findViewById(R.id.timePicker)
        timePicker?.setIs24HourView(true);
        createNotificationButton = view.findViewById(R.id.notification_create_button)
        cancelNotificationButton = view.findViewById(R.id.notification_cancel_button)

        val sharedPreferences = context?.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val id = sharedPreferences?.getInt(SHARED_PREFERENCES_ID, -1)
        val position = sharedPreferences?.getInt("covid_notification_position", -1)
        val hour = sharedPreferences?.getInt("covid_notification_hour", -1)
        val minute = sharedPreferences?.getInt("covid_notification_minute", -1)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hour != null) {
                timePicker?.hour = hour
            }
            if (minute != null) {
                timePicker?.minute = minute
            }
        }


        val alarmAlreadyRuns = id != -1 && id != null
        cancelNotificationButton?.isEnabled = alarmAlreadyRuns;
        createNotificationButton?.isEnabled = !alarmAlreadyRuns

        populateCountriesDropdown(position)

        return view
    }

    private fun cancelAlarm() {
        val sharedPreferences = context?.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val id = sharedPreferences?.getInt(SHARED_PREFERENCES_ID, -1)

        if (id != -1 && id != null) {
            val editor: SharedPreferences.Editor? = sharedPreferences.edit()
            editor!!.remove(SHARED_PREFERENCES_ID)
            editor.apply()

            val notificationIntent = Intent(context, NotificationManager::class.java)
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, 0)
            alarmManager?.cancel(pendingIntent)

            Toast.makeText(context, "Alarm canceled", Toast.LENGTH_SHORT).show()
            createNotificationButton?.isEnabled = true
            cancelNotificationButton?.isEnabled = false
        } else {
            Toast.makeText(context, "No alarm is running", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun populateCountriesDropdown(initialPosition: Int?) {
        val URL_STATS_GLOBAL = "https://api.covid19api.com/countries"

        var stringRequest = StringRequest(Request.Method.GET, URL_STATS_GLOBAL, Response.Listener{ response ->
            handleResponse(response, initialPosition)
        }, Response.ErrorListener { error ->
            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
        })

        var requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun handleResponse(response: String, initialPosition: Int?) {
        val countriesArray = JSONArray(response)
        val countries = mutableListOf<Country>()

        val current: Locale = Locale.getDefault()
        var defaultCountryIndex = 0

        for (i in 0 until countriesArray.length()) {
            var countryObject: JSONObject = countriesArray.getJSONObject(i)
            val countryName = countryObject.getString("Country")
            val countryCode = countryObject.getString("ISO2")
            countries.add(i, Country(countryName, countryCode))
        }

        countries.sortBy { country -> country.name }

        for (i in 0 until countries.size)
            if (countries[i].code.toLowerCase(Locale.ROOT) == current.country.toLowerCase(Locale.ROOT)) {
                defaultCountryIndex = i
                break
            }

        val adapter = context?.let { DropdownAdapter(it, ArrayList(countries)) }
        countriesDropdown?.adapter = adapter

        if (initialPosition != null && initialPosition >= 0)
            countriesDropdown?.setSelection(initialPosition)
        else
            countriesDropdown?.setSelection(defaultCountryIndex)

        var i = 1

        createNotificationButton?.setOnClickListener {_ ->
            run {
                val sharedPreferences = context?.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                val storageNotificationId = sharedPreferences?.getInt(SHARED_PREFERENCES_ID, -1)

                if (storageNotificationId != -1) {
                    Log.d("COVID_TAG",  "Alarm already exists")
                    Toast.makeText(context, "Alarm already exists. Please clear it before setting new one.", Toast.LENGTH_LONG).show()
                    return@run
                }

                val hour: Int
                val minute: Int

                if (Build.VERSION.SDK_INT >= 23) {
                    hour = timePicker?.hour ?: 18
                    minute = timePicker?.minute ?: 0
                } else {
                    hour = timePicker?.getCurrentHour() ?: 18
                    minute = timePicker?.getCurrentMinute() ?: 0
                }

                Log.d("COVID_TAG",  "$hour $minute")
                val position: Int = countriesDropdown?.selectedItemPosition as Int
                val country: Country = countries[position]
                val notificationIntent = Intent(context, NotificationManager::class.java)

                Log.d("COVID_TAG",  "Country $position ${country.code}")

                notificationIntent.putExtra("country_code", country.code)
                notificationIntent.putExtra("country_name", country.name)
                notificationIntent.putExtra("hour", hour)
                notificationIntent.putExtra("minute", minute)

                val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                val pendingIntent = PendingIntent.getBroadcast(context, i++, notificationIntent, 0)

                val calendar: Calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)


                alarmManager?.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                )

                val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
                editor?.putInt("covid_notification_id", id)
                editor?.putInt("covid_notification_position", position)
                editor?.putInt("covid_notification_hour", hour)
                editor?.putInt("covid_notification_minute", minute)
                editor?.apply()

                Toast.makeText(context, "Started service",Toast.LENGTH_SHORT).show()
                createNotificationButton?.isEnabled = false
                cancelNotificationButton?.isEnabled = true
            }
        }

        cancelNotificationButton?.setOnClickListener { _ ->
            run {
                cancelAlarm()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}