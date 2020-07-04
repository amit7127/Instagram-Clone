package com.android.amit.instaclone

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import butterknife.BindView
import butterknife.ButterKnife
import com.android.amit.instaclone.databinding.ActivityMainBinding
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.viewmodel.MAinActivityViewModel
import com.android.amit.instaclone.viewmodel.MAinActivityViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    @BindView(R.id.nav_view)
    lateinit var navView: BottomNavigationView

    lateinit var navController: NavController

    private val onNavigationItemClickListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
            viewModel.onPageChange(it.itemId)
            return@OnNavigationItemSelectedListener true
        }

    lateinit var viewModel: MAinActivityViewModel
    lateinit var viewModelFactory: MAinActivityViewModelFactory
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MAinActivityViewModel::class.java)

        ButterKnife.bind(this)

        navController = Navigation.findNavController(this, R.id.nav_container)
        NavigationUI.setupWithNavController(navView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomNav()
                R.id.searchFragment -> showBottomNav()
                R.id.notificationFragment -> showBottomNav()
                R.id.profileFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }

//        navView.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.nav_post -> {
//                    Log.i("Menu", "Post clicked")
//                    return@setOnNavigationItemSelectedListener false
//                }
//                else -> {
//                    Log.i("Menu", "Other clicked")
//                    return@setOnNavigationItemSelectedListener true
//                }
//            }
//        }

        setNotificationBadge()
    }

    private fun showBottomNav() {
        navView.visibility = View.VISIBLE

    }

    private fun hideBottomNav() {
        navView.visibility = View.GONE
    }

    private fun setNotificationBadge() {
        val navigationView = binding.navView
               this.viewModel.getNotificationCount().observe(this, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    if (it.data != null && it.data!! > 0) {
                        val badge = navigationView.getOrCreateBadge(R.id.notificationFragment) // previously showBadge
                        badge.number = it.data!!
                    } else {
                        navigationView.removeBadge(R.id.notificationFragment)
                    }
                }
            }
        })
    }
}
