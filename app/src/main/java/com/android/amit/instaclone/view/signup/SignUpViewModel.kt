package com.android.amit.instaclone.view.signup

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private lateinit var mContext: Context
    private lateinit var mView: View

    fun createUser(ctx: Context, view: View): LiveData<Resource<FirebaseUser>> {
        mContext = ctx
        mView = view

        var result: MutableLiveData<Resource<FirebaseUser>> =
            MutableLiveData<Resource<FirebaseUser>>()

        if (validateEteredData()) {
            val repo = Repository()

            result = repo.createUSerWithEmailAndPassword(email, passWord)
        }

        return result
    }

    fun saveUserData(): LiveData<Resource<Unit>> {
        var result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()

        val repo = Repository()

        var userDetailsModel = UserDetailsModel(
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

    fun validateEteredData(): Boolean {
        when {
            TextUtils.isEmpty(fullName) -> {
                Snackbar.make(
                    mView,
                    "Full name required",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(userName) -> {
                Snackbar.make(
                    mView,
                    "User name required",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(email) -> {
                Snackbar.make(
                    mView,
                    "Email requied",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(passWord) -> {
                Snackbar.make(
                    mView,
                    "Password required",
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            else -> return true
        }
    }
}