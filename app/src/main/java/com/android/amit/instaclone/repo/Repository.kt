package com.android.amit.instaclone.repo

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.amit.instaclone.data.FieldName
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.util.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

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
        var userId = getCurrentUserId()
        userDetails.userId = userId
        userDetails.fullName = userDetails.fullName.toLowerCase(Locale.getDefault())
        userDetails.userName = userDetails.userName.toLowerCase(Locale.getDefault())

        val result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()
        val resouce = Resource<Unit>()
        result.value = resouce.loading()

        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
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
            val userId = getCurrentUserId()
            val userRef: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
            val query =
                userRef.orderByChild(FieldName.FULL_NAME_COLUM_NAME).startAt(nameQuery).endAt(
                    nameQuery + "\uf8ff"
                )

            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    users.clear()
                    for (snapShot in dataSnapShot.children) {
                        val user = snapShot.getValue(UserDetailsModel::class.java)
                        if (user != null && !user.userId.equals(userId)) {
                            users.add(user)
                        }
                    }
                    result.value = resouce.success(users)
                }
            })
        }
        return result
    }

    fun follow(followUserId: String, status: String): MutableLiveData<Resource<Unit>> {

        val result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()
        val resouce = Resource<Unit>()
        result.value = resouce.loading()

        val userId = getCurrentUserId()
        if (status.equals(Status.follow)) {
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME).child(userId)
                .child(FieldName.FOLLOWING_COLUMN_NAME).child(followUserId).setValue(true)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
                            .child(followUserId)
                            .child(FieldName.FOLLOWER_COLUMN_NAME).child(userId).setValue(true)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    result.value = resouce.success(null)
                                } else {
                                    result.value = resouce.error("Unable to update status")
                                }
                            }
                    } else {
                        result.value = resouce.error("Unable to update status")
                    }
                }
        } else if (status.equals(Status.following)) {
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME).child(userId)
                .child(FieldName.FOLLOWING_COLUMN_NAME).child(followUserId).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
                            .child(followUserId)
                            .child(FieldName.FOLLOWER_COLUMN_NAME).child(userId).removeValue()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    result.value = resouce.success(null)
                                } else {
                                    result.value = resouce.error("Unable to update status")
                                }
                            }
                    } else {
                        result.value = resouce.error("Unable to update status")
                    }
                }
        }
        return result
    }

    fun getUserDetails(userId: String): MutableLiveData<Resource<UserDetailsModel>> {

        var result = MutableLiveData<Resource<UserDetailsModel>>()
        val resouce = Resource<UserDetailsModel>()

        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME).child(userId)
        result.value = resouce.loading()
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    val user = dataSnapShot.getValue(UserDetailsModel::class.java)
                    result.value = resouce.success(user)
                } else {
                    result.value = resouce.error("No user exists")
                }
            }
        })
        return result
    }

    fun saveUserProfileWithImage(userDetails: UserDetailsModel, profilePictureUri : Uri) : MutableLiveData<Resource<Unit>>{

        var result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()
        val resouce = Resource<Unit>()
        result.value = resouce.loading()

        var  firebaseStorage : StorageReference = FirebaseStorage.getInstance().getReference().child("Profile Images")

        var uploadTask = firebaseStorage.putFile(profilePictureUri)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    result.value = resouce.error("Unable to update")
                    throw it
                }
            }
            firebaseStorage.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                userDetails.image = downloadUri.toString()
                //result.value = saveUserInFirebase(userDetails).value

                userDetails.fullName = userDetails.fullName.toLowerCase(Locale.getDefault())
                userDetails.userName = userDetails.userName.toLowerCase(Locale.getDefault())

                val userRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
                userRef.child(userDetails.userId).setValue(userDetails).addOnCompleteListener {
                    if (it.isSuccessful) {
                        result.value = resouce.success(null)
                    } else {
                        result.value = resouce.error("Failed to save data")
                        FirebaseAuth.getInstance().signOut()
                    }
                }
            } else {
                result.value = resouce.error("Unable to update")
            }
        }

        return result
    }
}