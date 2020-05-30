package com.android.amit.instaclone.repo

import androidx.lifecycle.MutableLiveData
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: April/27/2020
 * Description:
 */
class Repository {
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createUSerWithEmailAndPassword(
        email: String,
        password: String
    ): MutableLiveData<Resource<FirebaseUser>> {

        val result: MutableLiveData<Resource<FirebaseUser>> =
            MutableLiveData<Resource<FirebaseUser>>()
        val resouce = Resource<FirebaseUser>()
        result.value = resouce.loading()

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
            {
                if (it.isSuccessful) {
                    result.value = resouce.success(it.result?.user)
                } else {
                    result.value = resouce.error(it.exception?.message)
                    mAuth.signOut()
                }
            })
        return result
    }

    fun loginUserWithEmailAndPassword(
        email: String,
        password: String
    ): MutableLiveData<Resource<FirebaseUser>> {

        val result: MutableLiveData<Resource<FirebaseUser>> =
            MutableLiveData<Resource<FirebaseUser>>()
        val resouce = Resource<FirebaseUser>()
        result.value = resouce.loading()

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
            {
                if (it.isSuccessful) {
                    result.value = resouce.success(it.result?.user)
                } else {
                    result.value = resouce.error(it.exception?.message)
                    mAuth.signOut()
                }
            })
        return result
    }

    fun saveUserInFirebase(
        userDetails: UserDetailsModel
    ): MutableLiveData<Resource<Unit>> {
        var userId = FirebaseAuth.getInstance().currentUser!!.uid
        userDetails.userId = userId

        val result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()
        val resouce = Resource<Unit>()
        result.value = resouce.loading()

        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.child(userId).setValue(userDetails).addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = resouce.success(null)
            } else {
                result.value = resouce.error("Failed to save data")
                FirebaseAuth.getInstance().signOut()
            }
        }
        return result
    }
}