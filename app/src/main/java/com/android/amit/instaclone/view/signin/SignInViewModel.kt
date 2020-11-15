package com.android.amit.instaclone.view.signin

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.repo.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : SignIn view model
 */
class SignInViewModel : ViewModel() {
    var email: String = ""
    var passWord: String = ""

    private lateinit var mContext: Context
    private lateinit var mView: View

    /**
     * Sign-in user
     */
    fun signInUser(ctx: Context, view: View): LiveData<Resource<FirebaseUser>> {
        mContext = ctx
        mView = view

        var result: MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()

        if (validateEnteredData()) {
            val repo = Repository()

            result = repo.loginUserWithEmailAndPassword(email, passWord)
        }

        return result
    }

    /**
     * validate username and password field
     */
    private fun validateEnteredData(): Boolean {
        when {
            TextUtils.isEmpty(email) -> {
                Snackbar.make(
                    mView,
                    Resources.getSystem().getString(R.string.email_field_required),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }

            TextUtils.isEmpty(passWord) -> {
                Snackbar.make(
                    mView,
                    Resources.getSystem().getString(R.string.password_field_required),
                    Snackbar.LENGTH_SHORT
                ).show()
                return false
            }
            else -> return true
        }
    }
}