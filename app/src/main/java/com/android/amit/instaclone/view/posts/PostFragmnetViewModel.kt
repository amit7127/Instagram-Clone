package com.android.amit.instaclone.view.posts

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.repo.Repository
import com.google.android.material.snackbar.Snackbar

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Posts fragment view model
 */
class PostFragmnetViewModel : ViewModel() {
    var mPostComment: String = ""
    var mPostPictureUri: Uri = Uri.EMPTY
    var repo: Repository = Repository()

    /**
     * upload post picture
     */
    fun setPostPicture(postPicUri: Uri) {
        mPostPictureUri = postPicUri
    }

    /**
     * publish new post
     */
    fun post(view: View, context: Context): LiveData<Resource<Unit>> {

        var result: MutableLiveData<Resource<Unit>> = MutableLiveData()

        if (validateData(view, context)) {
            result = repo.postWithImage(mPostPictureUri, mPostComment)
        }
        return result
    }

    /**
     * Post form validation
     */
    private fun validateData(view: View, context: Context): Boolean {
        return when {
            TextUtils.isEmpty(mPostComment) -> {
                Snackbar.make(
                    view,
                    context.getString(R.string.add_comment_error_message),
                    Snackbar.LENGTH_SHORT
                ).show()
                false
            }
            else -> true
        }
    }
}