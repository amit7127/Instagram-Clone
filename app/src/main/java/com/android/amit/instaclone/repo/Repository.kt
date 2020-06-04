package com.android.amit.instaclone.repo

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

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

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = resouce.success(it.result?.user)
            } else {
                result.value = resouce.error(it.exception?.message)
                mAuth.signOut()
            }
        }
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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = resouce.success(it.result?.user)
            } else {
                result.value = resouce.error(it.exception?.message)
                mAuth.signOut()
            }
        }
        return result
    }

    fun saveUserInFirebase(
        userDetails: UserDetailsModel
    ): MutableLiveData<Resource<Unit>> {
        var userId = FirebaseAuth.getInstance().currentUser!!.uid
        userDetails.userId = userId
        userDetails.fullName = userDetails.fullName.toLowerCase(Locale.getDefault())
        userDetails.userName = userDetails.userName.toLowerCase(Locale.getDefault())

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


    fun getUsers(nameQuery: String): MutableLiveData<Resource<ArrayList<UserDetailsModel>>> {

        val users = arrayListOf<UserDetailsModel>()

        val result: MutableLiveData<Resource<ArrayList<UserDetailsModel>>> =
            MutableLiveData<Resource<ArrayList<UserDetailsModel>>>()
        val resouce = Resource<ArrayList<UserDetailsModel>>()
        result.value = resouce.loading()

        if (TextUtils.isEmpty(nameQuery)) {
            result.value = resouce.success(null)
        } else {

            val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
            val query =
                userRef.orderByChild("fullName").startAt(nameQuery).endAt(
                    nameQuery + "\uf8ff"
                )

            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    for (snapShot in dataSnapShot.children) {
                        val user = snapShot.getValue(UserDetailsModel::class.java)
                        if (user != null) {
                            users.add(user)
                        }
                    }
                    result.value = resouce.success(users)
                }
            })
        }
        return result
    }
}