package com.android.amit.instaclone.view.users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.StoryModel
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository
/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Users view model
 */
class UsersListViewModel : ViewModel() {
    var repo: Repository = Repository()

    /**
     * get user list from Firebase
     */
    fun getUsersIdList(postId: String): MutableLiveData<Resource<ArrayList<String>>> {
        return repo.getLikedUsersList(postId)
    }

    /**
     * get following list of the provided user id
     */
    fun getFollowingIdList(userId: String?): MutableLiveData<Resource<ArrayList<String>>> {
        var tempUserId = userId
        if (tempUserId == null)
            tempUserId = repo.getCurrentUserId()
        return repo.getFollowingUserList(tempUserId)
    }

    /**
     * get follower list of the provided user id
     */
    fun getFollowerIdList(userId: String?): MutableLiveData<Resource<ArrayList<String>>> {
        var tempUserId = userId
        if (tempUserId == null)
            tempUserId = repo.getCurrentUserId()
        return repo.getFollowerUserList(tempUserId)
    }

    /**
     * get users models list from the id list
     */
    fun getUsersFromIdList(usersIdList: ArrayList<String>): MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> {
        return repo.getUsersListFromIDList(usersIdList.toHashSet())
    }

    /**
     * get story data from the story id
     */
    fun getStory(storyId: String): MutableLiveData<Resource<StoryModel>> {
        return repo.getStoryById(storyId)
    }
}