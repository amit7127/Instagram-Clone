package com.android.amit.instaclone.view.comments

import android.content.res.Resources
import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.CommentModel
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository
import com.google.android.material.snackbar.Snackbar

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Comments page view model
 */
class CommentsFragmentViewModel : ViewModel() {
    var repo: Repository = Repository()
    var postImageUri: Uri = Uri.EMPTY
    var publisherProfileImageUri: Uri = Uri.EMPTY
    var commentString: String = ""
    var userId: String = ""

    /**
     * set post data
     */
    fun setPostData(postImageUrl: String) {
        postImageUri = Uri.parse(postImageUrl)
    }

    /**
     * set user data
     */
    fun setUserData(userDetailsModel: UserDetailsModel?) {
        if (userDetailsModel != null) {
            publisherProfileImageUri = Uri.parse(userDetailsModel.image)
            userId = userDetailsModel.userId
        }
    }

    /**
     * fetch comment for the current post
     */
    fun getComments(postId: String): MutableLiveData<Resource<ArrayList<CommentModel>>> {
        return repo.getListOfComments(postId)
    }

    /**
     * get commented users map
     */
    fun getUsersMap(comments: ArrayList<CommentModel>): MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> {
        val usersIdList = HashSet<String>()
        for (comment in comments) {
            usersIdList.add(comment.publisher)
        }

        return repo.getUsersListFromIDList(usersIdList)
    }

    /**
     * get current user details
     */
    fun getUserData(): MutableLiveData<Resource<UserDetailsModel>> {
        return repo.getUserDetails(repo.getCurrentUserId())
    }

    /**
     * post new comment
     */
    fun postComment(postId: String, mView: View): MutableLiveData<Resource<Unit>> {
        var result: MutableLiveData<Resource<Unit>> = MutableLiveData()
        if (validateEnteredData(mView)) {
            val commentModel = CommentModel(userId, commentString)

            result = repo.postComment(postId, commentModel)
        }
        return result
    }

    /**
     * clear comment box entry
     */
    fun clearComment() {
        commentString = ""
    }

    /**
     * Validate comment form data
     */
    private fun validateEnteredData(mView: View): Boolean {
        when {
            TextUtils.isEmpty(commentString) -> {
                Snackbar.make(
                    mView,
                    Resources.getSystem().getString(R.string.comment_field_validation_string),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(userId) -> {
                Snackbar.make(
                    mView,
                    Resources.getSystem().getString(R.string.unable_to_fetch_user_details),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }
            else -> return true
        }
    }

    /**
     * Add notification for new comment
     */
    fun addNotification(notification: Notification, tergetUserId: String) {
        repo.addNotification(notification, tergetUserId)
    }
}