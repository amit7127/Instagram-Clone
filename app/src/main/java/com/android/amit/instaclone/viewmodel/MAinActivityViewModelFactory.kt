package com.android.amit.instaclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: April/23/2020
 * Description:
 */
class MAinActivityViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.newInstance()
    }

}