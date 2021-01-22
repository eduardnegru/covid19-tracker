package com.example.covidtracker

import android.graphics.drawable.Drawable
import java.io.Serializable

class Country (var name: String, var code: String, var slug : String,
               var newConfirmed : String, var totalConfirmed : String,
               var newDeaths : String, var totalDeaths : String,
               var newRecovered : String, var totalRecovered : String,
               var date : String) {


    constructor(name : String, code : String) :
            this(name, code, "", "", "", "", "", "", "", "") {
        this.name = name
        this.code = code
    }

    override fun toString(): String {
        return "Country(name='$name', code='$code', slug='$slug', newConfirmed='$newConfirmed', totalConfirmed='$totalConfirmed', newDeaths='$newDeaths', totalDeaths='$totalDeaths', newRecovered='$newRecovered', totalRecovered='$totalRecovered', date='$date')"
    }

}