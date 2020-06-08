package com.android.amit.instaclone.view.acountsettings

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    var mUserDetailsModel = UserDetailsModel()
    var repo: Repository = Repository()

    @ExperimentalStdlibApi
    fun setUserInfo(userDetailsModel: UserDetailsModel?) {
        if (userDetailsModel != null) {
            this.fullName = userDetailsModel.fullName.capitalizeWords()
            this.userName = userDetailsModel.userName.capitalizeWords()
            this.userBio = userDetailsModel.bio
            this.mUserDetailsModel = userDetailsModel
        }
    }

    fun getUserDetails(): MutableLiveData<Resource<UserDetailsModel>> {
        return repo.getUserDetails(repo.getCurrentUserId())
    }

    fun saveUserData(view: View): LiveData<Resource<Unit>> {
        var result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()

        if (validateEteredData(view)) {
            result = repo.saveUserInFirebase(mUserDetailsModel)
        }
        return result
    }

    fun validateEteredData(mView: View): Boolean {
        when {
            TextUtils.isEmpty(mUserDetailsModel.fullName) -> {
                Snackbar.make(
                    mView,
                    "Full name required",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(mUserDetailsModel.userName) -> {
                Snackbar.make(
                    mView,
                    "User name required",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(mUserDetailsModel.bio) -> {
                Snackbar.make(
                    mView,
                    "User Bio required",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            else -> return true
        }
    }
}