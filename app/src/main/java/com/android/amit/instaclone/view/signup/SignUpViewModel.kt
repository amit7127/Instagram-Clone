package com.android.amit.instaclone.view.signup

import android.content.res.Resources
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: April/27/2020
 * Description:
 */
class SignUpViewModel : ViewModel() {
    var fullName: String = ""
    var userName: String = ""
    var email: String = ""
    var passWord: String = ""

    /**
     * create user
     */
    fun createUser(view: View): LiveData<Resource<FirebaseUser>> {

        var result: MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()

        if (validateEnteredData(view)) {
            val repo = Repository()

            result = repo.createUSerWithEmailAndPassword(email, passWord)
        }

        return result
    }

    /**
     * save user
     */
    fun saveUserData(): LiveData<Resource<Unit>> {
        val result: LiveData<Resource<Unit>>
        val repo = Repository()

        val userDetailsModel = UserDetailsModel(
            "",
            fullName,
            userName,
            email,
            "IT user's BIO",
            "https://firebasestorage.googleapis.com/v0/b/insta-clone-42122.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=c39ad3dc-6007-43c8-a1cb-8afe52f17c89",
            HashMap(),
            HashMap()
        )

        result = repo.saveUserInFirebase(userDetailsModel)
        return result
    }

    /**
     * validate data
     */
    private fun validateEnteredData(view: View): Boolean {
        when {
            TextUtils.isEmpty(fullName) -> {
                Snackbar.make(
                    view,
                    Resources.getSystem().getString(R.string.full_name_field_required),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(userName) -> {
                Snackbar.make(
                    view,
                    Resources.getSystem().getString(R.string.user_name_field_required),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(email) -> {
                Snackbar.make(
                    view,
                    Resources.getSystem().getString(R.string.email_field_required),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(passWord) -> {
                Snackbar.make(
                    view,
                    Resources.getSystem().getString(R.string.password_field_required),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            else -> return true
        }
    }
}