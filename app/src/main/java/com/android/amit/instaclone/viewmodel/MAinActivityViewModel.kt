package com.android.amit.instaclone.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.R

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: April/23/2020
 * Description:
 */

class MAinActivityViewModel : ViewModel() {
    private var homeText = MutableLiveData<String>()

    init {
        homeText.value = "Home"
    }

    fun getData(): MutableLiveData<String> {
       return homeText
    }

    fun onPageChange(id: Int) {

    }
}