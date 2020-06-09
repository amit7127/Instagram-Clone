package com.android.amit.instaclone.view.posts

import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.repo.Repository
import com.google.android.material.snackbar.Snackbar

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/09/2020
 * Description:
 */
class PostFragmnetViewModel : ViewModel() {
    var mPostComment: String = ""
    var mPostPictureUri: Uri = Uri.EMPTY
    var repo: Repository = Repository()

    fun setPostPicture(postPicUri: Uri) {
        mPostPictureUri = postPicUri
    }

    fun post(view: View): LiveData<Resource<Unit>> {

        var result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()

        if (validateData(view)) {
            result = repo.postWithImage(mPostPictureUri, mPostComment)
        }
        return result
    }

    fun validateData(view: View): Boolean {
        when {
            TextUtils.isEmpty(mPostComment) -> {
                Snackbar.make(
                    view,
                    "Pleas add comment for this post",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            else -> return true
        }
    }
}