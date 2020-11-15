package com.android.amit.instaclone.view.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Search users fragment view model
 */
class SearchFragmentViewModel : ViewModel() {
    var searchQuerry: String = ""
    var repo: Repository = Repository()

    /**
     * search user by string
     */
    fun searchUserByQuery(query: String): MutableLiveData<Resource<ArrayList<UserDetailsModel>>> {
        return repo.getUsers(query)
    }

    /**
     * set follow status
     */
    fun setFollowStatus(userId: String, currentStatus: String): MutableLiveData<Resource<Unit>> {
        return repo.follow(userId, currentStatus)
    }

    /**
     * add new notification to db
     */
    fun addNotification(notification: Notification, tergetUserId: String) {
        repo.addNotification(notification, tergetUserId)
    }
}