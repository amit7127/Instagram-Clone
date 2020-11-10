package com.android.amit.instaclone.view.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.util.StringUtils.capitalizeWords
import java.util.*
import kotlin.collections.ArrayList

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Profile fragment view model
 */
class ProfileFragmentViewModel : ViewModel() {
    var mFollower: Int = 0
    var mFollowing: Int = 0
    var mPost: Int = 0
    var mFullName = "N/A"
    var mBio = "N/A"
    var mProfileImage: Uri = Uri.EMPTY
    var isEditProfile: Boolean = false
    var mEditButtonText = Status.follow

    var repo: Repository = Repository()
    var id = repo.getCurrentUserId()

    fun getUserData(userId: String?): MutableLiveData<Resource<UserDetailsModel>>? {
        if (userId != null) {
            id = userId
        } else {
            isEditProfile = true
        }
        return id.let { repo.getUserDetails(it) }
    }

    @ExperimentalStdlibApi
    fun setUserDetails(userDetailsModel: UserDetailsModel?) {
        if (userDetailsModel != null) {
            mFollower = userDetailsModel.Follower.size
            mFollowing = userDetailsModel.Following.size
            mFullName = userDetailsModel.fullName.capitalizeWords()
            mBio = userDetailsModel.bio
            mProfileImage = Uri.parse(userDetailsModel.image)

            if (!isEditProfile) {
                setStatus(userDetailsModel)
            }
        }
    }

    /**
     * set follow button functionality
     */
    private fun setStatus(item: UserDetailsModel) {
        mEditButtonText = if (item.Follower.containsKey(repo.getCurrentUserId())) {
            Status.following
        } else {
            Status.follow
        }
    }

    /**
     * follow/un-follow
     */
    fun setFollowStatus(userId: String, currentStatus: String): MutableLiveData<Resource<Unit>> {
        return repo.follow(userId, currentStatus)
    }

    /**
     * get posts images
     */
    fun getPostsImages(): MutableLiveData<Resource<ArrayList<Post>>> {
        return repo.getUserPosts(id)
    }

    /**
     * get saved posts images
     */
    fun getSavedPostsImages(idList: ArrayList<String>): MutableLiveData<Resource<ArrayList<Post>>> {
        return repo.getPostsFromIds(idList)
    }

    /**
     * get saved lists
     */
    fun getSavedList(): MutableLiveData<Resource<HashMap<String, Boolean>>> {
        return repo.getSavedList()
    }

    /**
     * set posts count
     */
    fun setPostsCount(postsCount: Int) {
        mPost = postsCount
    }

    /**
     * add new notification
     */
    fun addNotification(notification: Notification, tergetUserId: String) {
        repo.addNotification(notification, tergetUserId)
    }
}