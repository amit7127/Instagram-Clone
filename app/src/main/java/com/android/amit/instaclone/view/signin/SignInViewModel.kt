package com.android.amit.instaclone.view.signin

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.repo.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: May/29/2020
 * Description:
 */
class SignInViewModel : ViewModel() {
    var email: String = ""
    var passWord: String = ""

    private lateinit var mContext: Context
    private lateinit var mView: View

    fun signInUser(ctx: Context, view: View): LiveData<Resource<FirebaseUser>> {
        mContext = ctx
        mView = view

        var result: MutableLiveData<Resource<FirebaseUser>> =
            MutableLiveData<Resource<FirebaseUser>>()

        if (validateEteredData()) {
            val repo = Repository()

            result = repo.loginUserWithEmailAndPassword(email, passWord)
        }

        return result
    }

    fun validateEteredData(): Boolean {
        when {
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