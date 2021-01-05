package com.example.covidtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var navigationView: BottomNavigationView
    lateinit var homeFragment: Fragment
    lateinit var statsFragment:Fragment
    lateinit var fragmentManager:FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationView = findViewById(R.id.bottomNavigation)

        initFragments()

        navigationView.setOnNavigationItemSelectedListener(this)
    }

    fun initFragments() {
        homeFragment = HomeFragment()
        statsFragment = StatsFragment()
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.frame, homeFragment, "HomeFragment").commit()
        fragmentManager.beginTransaction().add(R.id.frame, statsFragment, "StatsFragment").hide(statsFragment).commit()
    }

    fun loadHomeFragment() {
        fragmentManager.beginTransaction().hide(statsFragment).show(homeFragment).commit()
    }

    fun loadStatsFragment() {
        fragmentManager.beginTransaction().hide(homeFragment).show(statsFragment).commit()
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