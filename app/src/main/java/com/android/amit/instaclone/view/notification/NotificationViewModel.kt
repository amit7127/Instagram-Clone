package com.android.amit.instaclone.view.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Notifications screen view model class
 */
class NotificationViewModel : ViewModel() {
    val repo = Repository()

    /**
     * fetch notification list
     */
    fun getNotificationList(): MutableLiveData<Resource<ArrayList<Notification>>> {
        return repo.getNotifications()
    }

    /**
     * get user data from the userId list
     */
    fun getUsersFromIdList(usersIdList: ArrayList<String>): MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> {
        return repo.getUsersListFromIDList(usersIdList.toHashSet())
    }

    /**
     * mark notification status as read
     */
    fun markNotificationAsRead(notification: Notification) {
        repo.markReadNotification(notification)
    }

    /**
     * get posts list
     */
    fun getPostList(postIdList: ArrayList<String>): MutableLiveData<Resource<ArrayList<Post>>> {
        return repo.getPostsFromIds(postIdList)
    }
}
