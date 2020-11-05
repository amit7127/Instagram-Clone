package com.android.amit.instaclone.util

import android.app.Application
import timber.log.Timber

class ApplicationController : Application() {

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        Timber.plant(Timber.DebugTree())
    }


    companion object {
        lateinit var mInstance: ApplicationController
            private set
    }


}