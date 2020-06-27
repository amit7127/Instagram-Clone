package com.android.amit.instaclone.view.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.util.StringUtils.capitalizeWords

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: April/24/2020
 * Description:
 */
class ProfileFragmentViewModel : ViewModel() {
    var mFollower: Int = 0
    var mFollowing: Int = 0
    var mPost: Int = 0
    var mFullName = "N/A"
    var mBio = "N/A"
    var mProfileImage = Uri.EMPTY
    var isEditProfile : Boolean = false
    var mEditButtonText = Status.follow

    var repo: Repository = Repository()
    var id = repo.getCurrentUserId()

    fun getUserData(userId: String?): MutableLiveData<Resource<UserDetailsModel>>? {
        if (userId != null) {
            id = userId
        } else{
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

            if (!isEditProfile){
                setStatus(userDetailsModel)
            }
        }
    }

    fun setStatus(item: UserDetailsModel) {
        if (item.Follower.containsKey(repo.getCurrentUserId())) {
            mEditButtonText = Status.following
        } else {
            mEditButtonText = Status.follow
        }
    }

    fun setFollowStatus(userId: String, currentStatus: String): MutableLiveData<Resource<Unit>> {
        return repo.follow(userId, currentStatus)
    }

    fun getPostsImages(): MutableLiveData<Resource<ArrayList<Post>>> {
        return repo.getUserPosts(id)
    }

    fun setPostsCount(postsCount: Int){
        mPost = postsCount
    }
}