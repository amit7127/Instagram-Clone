package com.android.amit.instaclone.view.comments

import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.CommentModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository
import com.google.android.material.snackbar.Snackbar

class CommentsFragmentViewModel: ViewModel() {
    var repo: Repository = Repository()
    var postImageUri : Uri = Uri.EMPTY
    var publisherProfileImageUri : Uri = Uri.EMPTY
    var commentString : String = ""
    var userId : String = ""

    private lateinit var mView: View

    fun setPostData(postImageUrl : String){
        postImageUri = Uri.parse(postImageUrl)
    }

    fun setUserData(userDetailsModel: UserDetailsModel?, view: View){
        if (userDetailsModel != null) {
            publisherProfileImageUri = Uri.parse(userDetailsModel.image)
            userId = userDetailsModel.userId
        }
    }

    fun getUserData(): MutableLiveData<Resource<UserDetailsModel>> {
        return repo.getUserDetails(repo.getCurrentUserId())
    }

    fun postComment(postId: String) : MutableLiveData<Resource<Unit>>{
        var result: MutableLiveData<Resource<Unit>> = MutableLiveData<Resource<Unit>>()
        if (validateEteredData()) {
            var commentModel = CommentModel(userId, commentString)

            result = repo.postComment(postId, commentModel)
        }
        return result
    }

    fun clearComment(){
        commentString = ""
    }

    fun validateEteredData(): Boolean {
        when {
            TextUtils.isEmpty(commentString) -> {
                Snackbar.make(
                    mView,
                    "Full name required",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(userId) -> {
                Snackbar.make(
                    mView,
                    "Unable to fetch user info, please try again later",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            else -> return true
        }
    }
}