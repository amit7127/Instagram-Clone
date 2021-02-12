package com.android.amit.instaclone.view.acountsettings

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository
import com.android.amit.instaclone.util.StringUtils.capitalizeWords
import com.google.android.material.snackbar.Snackbar

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/06/2020
 * Description:
 */
class AccountSettingsViewModel : ViewModel() {
    var fullName: String = ""
    var userName: String = ""
    var userBio: String = ""
    var profileImageUri: Uri = Uri.EMPTY
    private var mUserDetailsModel = UserDetailsModel()
    private var repo: Repository = Repository()

    @ExperimentalStdlibApi
    fun setUserInfo(userDetailsModel: UserDetailsModel?) {
        if (userDetailsModel != null) {
            this.fullName = userDetailsModel.fullName.capitalizeWords()
            this.userName = userDetailsModel.userName.capitalizeWords()
            this.userBio = userDetailsModel.bio
            profileImageUri = Uri.parse(userDetailsModel.image)
            this.mUserDetailsModel = userDetailsModel
        }
    }

    /**
     * fetch user details
     */
    fun getUserDetails(): MutableLiveData<Resource<UserDetailsModel>> {
        return repo.getUserDetails(repo.getCurrentUserId())
    }

    /**
     * Save user entered data
     */
    fun saveUserData(view: View, context: Context): LiveData<Resource<Unit>> {
        var result: MutableLiveData<Resource<Unit>> =
            MutableLiveData()

        if (validateEnteredData(view, context)) {
            mUserDetailsModel.fullName = this.fullName
            mUserDetailsModel.userName = this.userName
            mUserDetailsModel.bio = this.userBio

            val uriString = profileImageUri.toString()
            if (uriString.contains("http")) {
                result = repo.saveUserInFirebase(mUserDetailsModel)
            } else {
                result = repo.saveUserProfileWithImage(mUserDetailsModel, profileImageUri)
            }
        }
        return result
    }

    /**
     * validate form
     */
    private fun validateEnteredData(mView: View, context: Context): Boolean {
        when {
            TextUtils.isEmpty(mUserDetailsModel.fullName) -> {
                Snackbar.make(
                    mView,
                    context.getString(R.string.full_name_validation_string),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(mUserDetailsModel.userName) -> {
                Snackbar.make(
                    mView,
                    context.getString(R.string.user_name_validation_string),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(mUserDetailsModel.bio) -> {
                Snackbar.make(
                    mView,
                    context.getString(R.string.user_bio_validation_string),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }
            else -> return true
        }
    }

    /**
     * Set image in db
     */
    fun setImage(uri: Uri) {
        profileImageUri = uri
    }
}