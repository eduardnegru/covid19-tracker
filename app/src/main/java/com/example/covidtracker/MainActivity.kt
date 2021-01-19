package com.example.covidtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blongho.country_data.World
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var navigationView: BottomNavigationView
    lateinit var globalFragment: Fragment
    lateinit var countriesFragment:Fragment
    lateinit var notificationFragment:Fragment
    lateinit var activeFragment:Fragment
    lateinit var fragmentManager:FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationView = findViewById(R.id.bottomNavigation)

        World.init(applicationContext)
        initFragments()

        navigationView.setOnNavigationItemSelectedListener(this)
    }

    fun initFragments() {
        globalFragment = GlobalFragment()
        countriesFragment = CountriesFragment()
        notificationFragment = NotificationFragment()
        activeFragment = globalFragment
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.frame, globalFragment, "GlobalFragment").commit()
        fragmentManager.beginTransaction().add(R.id.frame, countriesFragment, "CountriesFragment").hide(countriesFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, notificationFragment, "NotificationFragment").hide(notificationFragment).commit()
    }

    fun loadGlobalFragment() {
        fragmentManager.beginTransaction().hide(activeFragment).show(globalFragment).commit()
        activeFragment = globalFragment
    }

    fun loadCountriesFragment() {
        fragmentManager.beginTransaction().hide(activeFragment).show(countriesFragment).commit()
        activeFragment = countriesFragment
    }

    fun loadNotificationFragment() {
        fragmentManager.beginTransaction().hide(activeFragment).show(notificationFragment).commit()
        activeFragment = notificationFragment
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
}