package com.android.amit.instaclone

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import butterknife.BindView
import butterknife.ButterKnife
import com.android.amit.instaclone.viewmodel.MAinActivityViewModel
import com.android.amit.instaclone.viewmodel.MAinActivityViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    @BindView(R.id.nav_view)
    lateinit var navView: BottomNavigationView

    lateinit var navController : NavController

    private val onNavigationItemClickListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
            viewModel.onPageChange(it.itemId)
            return@OnNavigationItemSelectedListener true
        }

    lateinit var viewModel: MAinActivityViewModel
    lateinit var viewModelFactory: MAinActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        //navView.setOnNavigationItemSelectedListener(onNavigationItemClickListener)
//        viewModelFactory = MAinActivityViewModelFactory()
//        viewModel = ViewModelProvider(this, viewModelFactory).get(MAinActivityViewModel::class.java)
//        viewModel.getData().observe(this, Observer {
//            //textView.setText(it)
//        })

        navController = Navigation.findNavController(this, R.id.nav_container)
        NavigationUI.setupWithNavController(navView, navController)
    }
}
