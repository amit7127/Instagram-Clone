package com.android.amit.instaclone.data

import com.android.amit.instaclone.util.Status

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: April/27/2020
 * Description:
 */
class Resource<T>() {
    lateinit var status: String
    var data: T? = null
    var message: String? = ""

    private constructor(status: String, data: T?, message: String?) : this() {
        this.status = status
        this.data = data
        this.message = message
    }

    fun success(data: T?): Resource<T> {
        return Resource(Status.statusSuccess, data, "Success")
    }

    fun error(message: String?): Resource<T> {
        return Resource(Status.statusError, null, message)
    }

    fun loading(): Resource<T> {
        return Resource(Status.statusLoading, null, "Loading wait")
    }
}