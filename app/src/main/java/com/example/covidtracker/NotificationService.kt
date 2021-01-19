package com.example.covidtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NotificationService : JobService() {
    var mNotifyManager: NotificationManager? = null

    // Notification channel ID.
    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    fun createNotificationChannel() {

        // Define notification manager object.
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    override fun onStartJob(params: JobParameters?): Boolean {
        val country: String? = params?.extras?.getString("country")

//        val URL_STATS_GLOBAL = "https://api.covid19api.com/summary"
//        var stringRequest = StringRequest(Request.Method.GET, URL_STATS_GLOBAL, Response.Listener{ response ->
//            handleResponse(response, addSpinner)
//        }, Response.ErrorListener { error ->
//            if (addSpinner)
//                spinner?.stop()
//            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
//        })
//
//        var requestQueue = Volley.newRequestQueue(context)
//        requestQueue.add(stringRequest)

        createNotificationChannel()

        val contentPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Job Service")
                .setContentText("Your Job ran to completion!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_job_running)
                .setAutoCancel(true)

        mNotifyManager?.notify(0, builder.build())
        return false
    }
}