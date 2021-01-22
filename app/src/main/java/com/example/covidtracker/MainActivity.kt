package com.example.covidtracker

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blongho.country_data.World
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var navigationView: BottomNavigationView
    lateinit var globalFragment: Fragment
    lateinit var countriesFragment:Fragment
    lateinit var notificationFragment:Fragment
    lateinit var fragmentManager:FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationView = findViewById(R.id.bottomNavigation)

        World.init(applicationContext)
        initFragments()
        Log.d("DEBUG", "INITIALIZING")
        navigationView.setOnNavigationItemSelectedListener(this)
    }

    fun initFragments() {
        globalFragment = GlobalFragment()
        countriesFragment = CountriesFragment()
        notificationFragment = NotificationFragment()

        Log.d("DEBUG", "Setting default as global but the active is now ")

        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame, globalFragment, "GlobalFragment").commit()
    }

    fun loadGlobalFragment() {
        fragmentManager.beginTransaction().replace(R.id.frame, globalFragment).commit()
    }

    fun loadCountriesFragment() {
        fragmentManager.beginTransaction().replace(R.id.frame, countriesFragment).commit()
    }

    fun loadNotificationFragment() {
        fragmentManager.beginTransaction().replace(R.id.frame, notificationFragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_global -> {
                loadGlobalFragment()
                return true
            }
            R.id.nav_countries -> {
                loadCountriesFragment()
                return true
            }
            R.id.nav_notification -> {
                loadNotificationFragment()
                return true
            }
        }

        return false
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        Log.d("DEBUG", "Id = ${navigationView.selectedItemId}");
        bundle.putInt("SelectedItemId", navigationView.selectedItemId);
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val selectedItemId = savedInstanceState.getInt("SelectedItemId")
        navigationView.selectedItemId = selectedItemId
    }
}