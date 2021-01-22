package com.example.covidtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.math.abs


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)

class NotificationManager : BroadcastReceiver() {
    var mNotifyManager: NotificationManager? = null

    // Notification channel ID.
    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    fun createNotificationChannel(context: Context?) {

        // Define notification manager object.
        mNotifyManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("COVID_TAG", "Running service")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("COVID_TAG", "Building notification")
            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID, "Job Service notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifications from Job Service"
            mNotifyManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun handleResponse(response: String, countryName: String, context: Context?) {
        val countriesArray = JSONArray(response)
        var currentDate: String? = null
        var previousDate: String? = null
        var confirmed = 0
        var deaths = 0
        var recovered = 0
        var active = 0
        var sign = 1

        for (i in 0 until countriesArray.length()) {
            val countryStat = countriesArray[i] as JSONObject
            currentDate = countryStat["Date"] as String

            if (previousDate != null && currentDate != previousDate) {
                sign = -1
            }

            confirmed += sign * countryStat["Confirmed"] as Int
            deaths += sign * countryStat["Deaths"] as Int
            recovered += sign * countryStat["Recovered"] as Int
            active += sign * countryStat["Active"] as Int
            previousDate = currentDate

        }

        confirmed = abs(confirmed)
        deaths = abs(deaths)
        recovered = abs(recovered)
        active = abs(active)

        val contentPendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context!!, PRIMARY_CHANNEL_ID)
                .setContentTitle("Covid19 updates for $countryName")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_job_running)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("In the last 24h, there have been registered $confirmed new cases, $deaths new deaths, $recovered new recoveries. There are $active active cases now."))
                .setAutoCancel(true)

        mNotifyManager?.notify(0, builder.build())
    }

    private fun padLeft(value: Int): String {
        return if (value / 10 == 0)
            "0${value}"
        else
            "$value"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val bundle = intent!!.extras
            val countryCode = bundle!!.getString("country_code")
            val countryName = bundle.getString("country_name", "the selected country")
            val hour = bundle.getInt("hour", 18)
            val minute = bundle.getInt("minute", 0)

            val calendar = Calendar.getInstance()

            calendar.add(Calendar.DAY_OF_YEAR, -2)

            val year = padLeft(calendar.get(Calendar.YEAR))
            val day = padLeft(calendar.get(Calendar.DAY_OF_MONTH))
            val month = padLeft(calendar.get(Calendar.MONTH) + 1)

            Log.d("COVID_TAG", "Data c=$countryCode h=$hour m=$minute")

            createNotificationChannel(context)

            val URL_STATS_GLOBAL = "https://api.covid19api.com/live/country/$countryCode/status/confirmed/date/${year}-${month}-${day}T00:00:00Z"

            Log.d("COVID_TAG", "Request URL = $URL_STATS_GLOBAL")

            var stringRequest = StringRequest(Request.Method.GET, URL_STATS_GLOBAL, Response.Listener{ response ->
                handleResponse(response, countryName, context)
            }, Response.ErrorListener { error ->
                Toast.makeText(context, "Cannot perform request. Please check your internet connectio", Toast.LENGTH_SHORT).show()
            })

            var requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
          } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}