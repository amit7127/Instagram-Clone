package com.android.amit.instaclone.util

import android.app.Application
import timber.log.Timber

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : App controller
 */
class ApplicationController : Application() {

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        //Initialize timber for logging
        Timber.plant(Timber.DebugTree())
    }


    companion object {
        lateinit var mInstance: ApplicationController
            private set
    }


}