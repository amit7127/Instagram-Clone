package com.android.amit.instaclone.view.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.repo.Repository

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Main view model
 */

class MAinActivityViewModel : ViewModel() {
    private var homeText = MutableLiveData<String>()
    private var repo = Repository()

    /**
     * initialize data
     */
    init {
        homeText.value = "Home"
    }

    /**
     * get notification count from firebase db
     */
    fun getNotificationCount(): MutableLiveData<Resource<Int>> {
        return repo.getNotificationCount()
    }
}