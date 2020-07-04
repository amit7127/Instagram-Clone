package com.android.amit.instaclone.view.users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository

class UsersListViewModel : ViewModel() {
    var repo: Repository = Repository()

    fun getUsersIdList(postId: String): MutableLiveData<Resource<ArrayList<String>>> {
        return repo.getLikedUsersList(postId)
    }

    fun getFollowingIdList(userId: String?): MutableLiveData<Resource<ArrayList<String>>> {
        var tempUserId = userId
        if (tempUserId == null)
            tempUserId = repo.getCurrentUserId()
        return repo.getFollowingUserList(tempUserId)
    }

    fun getFollowerIdList(userId: String?): MutableLiveData<Resource<ArrayList<String>>> {
        var tempUserId = userId
        if (tempUserId == null)
            tempUserId = repo.getCurrentUserId()
        return repo.getFollowerUserList(tempUserId)
    }

    fun getUsersFromIdList(usersIdList: ArrayList<String>): MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> {
        return repo.getUsersListFromIDList(usersIdList.toHashSet())
    }
}