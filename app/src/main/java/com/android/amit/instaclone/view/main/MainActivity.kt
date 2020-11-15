package com.android.amit.instaclone.view.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.ActivityMainBinding
import com.android.amit.instaclone.util.Status
import kotlinx.android.synthetic.main.activity_main.*

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Main activity that contains all the navigation fragment, post login
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    lateinit var viewModel: MAinActivityViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MAinActivityViewModel::class.java)

        navController = Navigation.findNavController(this, R.id.nav_container)
        NavigationUI.setupWithNavController(nav_view, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomNav()
                R.id.searchFragment -> showBottomNav()
                R.id.notificationFragment -> showBottomNav()
                R.id.profileFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
        setNotificationBadge()
    }

    /**
     * show bottomNav
     */
    private fun showBottomNav() {
        nav_view.visibility = View.VISIBLE
    }

    /**
     * hide bottomNav
     */
    private fun hideBottomNav() {
        nav_view.visibility = View.GONE
    }

    /**
     * set total number notification badge
     */
    private fun setNotificationBadge() {
        val navigationView = binding.navView
        viewModel.getNotificationCount().observe(this, {
            when (it.status) {
                Status.statusSuccess -> {
                    if (it.data != null && it.data!! > 0) {
                        val badge =
                            navigationView.getOrCreateBadge(R.id.notificationFragment) // previously showBadge
                        badge.number = it.data!!
                    } else {
                        navigationView.removeBadge(R.id.notificationFragment)
                    }
                }
            }
        })
    }
}
