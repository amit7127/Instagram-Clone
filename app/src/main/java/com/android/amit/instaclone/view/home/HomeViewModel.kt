package com.android.amit.instaclone.view.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.*
import com.android.amit.instaclone.repo.Repository
import java.util.*

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/10/2020
 * Description:
 */
class HomeViewModel : ViewModel() {
    var repo: Repository = Repository()

    //Get posts for current user
    fun getPosts(): MutableLiveData<Resource<ArrayList<PostListItem>>> {
        return repo.getPostsList()
    }

    //Like button clicked
    fun likeButtonClicked(postId: String, oldStatusIsLike: Boolean) {
        repo.likeUnlikePost(postId, oldStatusIsLike)
    }

    //Likes list
    fun getLikesList(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, LikeModel>>> {
        return repo.getLikesList(postsList)
    }

    //Get comment count
    fun getCommentsCount(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, Int>>> {
        return repo.getCommentsList(postsList)
    }

    //Get saved list
    fun getSavedList(): MutableLiveData<Resource<HashMap<String, Boolean>>> {
        return repo.getSavedList()
    }

    //On saved button clicked
    fun savedClicked(postId: String, oldStatus: Boolean) {
        repo.saveClicked(postId, oldStatus)
    }

    //get unread notification
    fun addNotification(notification: Notification, tergetUserId: String) {
        repo.addNotification(notification, tergetUserId)
    }

    //Get following users list
    fun getFollowingUsersList(): MutableLiveData<Resource<ArrayList<String>>> {
        return repo.getFollowingUserList(repo.getCurrentUserId())
    }

    //Get stories
    fun getStories(followingList: ArrayList<String>): MutableLiveData<Resource<ArrayList<StoryModel>>> {
        return repo.getStories(followingList)
    }

    //Get users map from story list
    fun getUsersMap(stories: ArrayList<StoryModel>): MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> {
        val idList = stories.map { it.userId }
        return repo.getUsersListFromIDList(idList.toHashSet())
    }
}