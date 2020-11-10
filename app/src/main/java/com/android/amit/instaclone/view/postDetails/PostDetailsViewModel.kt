package com.android.amit.instaclone.view.postDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.LikeModel
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.repo.Repository
import java.util.*

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Post details page view model
 */
class PostDetailsViewModel : ViewModel() {
    var repo: Repository = Repository()

    /**
     * fetch post details from postId
     */
    fun getPostDetails(postId: String): MutableLiveData<Resource<PostListItem>> {
        return repo.getPostFromId(postId)
    }

    /**
     * like button clicked
     */
    fun likeButtonClicked(postId: String, oldStatusIsLike: Boolean) {
        repo.likeUnlikePost(postId, oldStatusIsLike)
    }

    /**
     * get likes list
     */
    fun getLikesList(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, LikeModel>>> {
        return repo.getLikesList(postsList)
    }

    /**
     * fetch comments count
     */
    fun getCommentsCount(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, Int>>> {
        return repo.getCommentsList(postsList)
    }

    /**
     * get saved posts list
     */
    fun getSavedList(): MutableLiveData<Resource<HashMap<String, Boolean>>> {
        return repo.getSavedList()
    }

    /**
     * on saved button clicked
     */
    fun savedClicked(postId: String, oldStatus: Boolean) {
        repo.saveClicked(postId, oldStatus)
    }

    /**
     * add new notification
     */
    fun addNotification(notification: Notification, targetUserId: String) {
        repo.addNotification(notification, targetUserId)
    }
}