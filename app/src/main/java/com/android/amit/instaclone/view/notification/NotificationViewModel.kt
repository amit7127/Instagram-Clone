package com.android.amit.instaclone.view.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository

class NotificationViewModel : ViewModel() {
    val repo = Repository()

    fun getNotificationList(): MutableLiveData<Resource<ArrayList<Notification>>> {
        return repo.getNotifications()
    }

    fun getUsersFromIdList(usersIdList: ArrayList<String>): MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> {
        return repo.getUsersListFromIDList(usersIdList.toHashSet())
    }

    fun markNotificationAsRead(notification: Notification){
        repo.markReadNotification(notification)
    }

    fun getPostList(postIdList: ArrayList<String>): MutableLiveData<Resource<ArrayList<Post>>> {
        return repo.getPostsFromIds(postIdList)
    }
}
