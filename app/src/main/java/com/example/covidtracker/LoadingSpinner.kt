package com.example.covidtracker

import android.app.Activity
import android.app.AlertDialog

class LoadingSpinner (val activity: Activity) {

    var dialog: AlertDialog? = null

    fun start() {
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = activity.layoutInflater
        builder.setView(layoutInflater.inflate(R.layout.spinner, null))
        builder.setCancelable(true)

        dialog = builder.create()
        dialog?.show()
    }

    fun stop() {
        dialog?.dismiss()
    }
}