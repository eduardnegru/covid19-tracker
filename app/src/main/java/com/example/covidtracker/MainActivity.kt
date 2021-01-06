package com.example.covidtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var navigationView: BottomNavigationView
    lateinit var globalFragment: Fragment
    lateinit var countriesFragment:Fragment
    lateinit var fragmentManager:FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationView = findViewById(R.id.bottomNavigation)

        initFragments()

        navigationView.setOnNavigationItemSelectedListener(this)
    }

    fun initFragments() {
        globalFragment = HomeFragment()
        countriesFragment = StatsFragment()
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.frame, globalFragment, "HomeFragment").commit()
        fragmentManager.beginTransaction().add(R.id.frame, countriesFragment, "StatsFragment").hide(countriesFragment).commit()
    }

    fun loadHomeFragment() {
        fragmentManager.beginTransaction().hide(countriesFragment).show(globalFragment).commit()
    }

    fun loadStatsFragment() {
        fragmentManager.beginTransaction().hide(globalFragment).show(countriesFragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                loadHomeFragment()
                return true
            }
            R.id.nav_stats -> {
                loadStatsFragment()
                return true
            }
        }

        return false
    }
}