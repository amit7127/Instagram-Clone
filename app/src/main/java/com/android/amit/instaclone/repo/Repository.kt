package com.android.amit.instaclone.repo

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.amit.instaclone.data.*
import com.android.amit.instaclone.util.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    fun saveUserProfileWithImage(
        userDetails: UserDetailsModel,
        profilePictureUri: Uri
    ): MutableLiveData<Resource<Unit>> {

        var result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()
        val resouce = Resource<Unit>()
        result.value = resouce.loading()

        var firebaseStorage: StorageReference =
            FirebaseStorage.getInstance().getReference().child("Profile Images")
                .child(userDetails.userId + "jpg")

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

    fun postWithImage(profilePictureUri: Uri, comment: String): MutableLiveData<Resource<Unit>> {
        var result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()
        val resouce = Resource<Unit>()
        result.value = resouce.loading()

        var firebaseStorage: StorageReference =
            FirebaseStorage.getInstance().getReference().child("Posts Pictures")
                .child(System.currentTimeMillis().toString() + ".jpg")

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

                val userRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child(FieldName.POST_TABLE_NAME)

                val uId = getCurrentUserId()
                val postId = userRef.push().key

                var post = Post(postId!!, comment, uId, downloadUri.toString())

                userRef.child(postId).setValue(post).addOnCompleteListener {
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

    fun getPostsList(): MutableLiveData<Resource<ArrayList<PostListItem>>> {
        val posts = arrayListOf<PostListItem>()

        val result: MutableLiveData<Resource<ArrayList<PostListItem>>> =
            MutableLiveData<Resource<ArrayList<PostListItem>>>()
        val resouce = Resource<ArrayList<PostListItem>>()
        result.value = resouce.loading()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.POST_TABLE_NAME)
        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)

        val currentUserReference = userRef.child(getCurrentUserId())

        currentUserReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(userDataSnapshot: DataSnapshot) {

                if (userDataSnapshot.exists()) {
                    val currentUserDetails = userDataSnapshot.getValue(UserDetailsModel::class.java)

                    postsRef.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            result.value = resouce.error("Unable to fetch data")
                        }

                        override fun onDataChange(dataSnapShot: DataSnapshot) {
                            for (snapShot in dataSnapShot.children) {
                                val post = snapShot.getValue(PostListItem::class.java)
                                if (post != null) {
                                    posts.clear()
                                    val userId = post.publisher
                                    if (currentUserDetails != null && (currentUserDetails.Following.containsKey(
                                            userId
                                        ) || currentUserDetails.userId == userId)
                                    ) {
                                        userRef.child(userId)
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onCancelled(p0: DatabaseError) {
                                                    post.publisherImageUrl = ""
                                                    post.publisherUserName = "N/A"
                                                    post.publisherFullName = "N/A"
                                                }

                                                override fun onDataChange(dataSnapShot: DataSnapshot) {
                                                    if (dataSnapShot.exists()) {
                                                        val userDetails =
                                                            dataSnapShot.getValue(UserDetailsModel::class.java)
                                                        if (userDetails != null) {
                                                            post.publisherImageUrl =
                                                                userDetails.image
                                                            post.publisherUserName =
                                                                userDetails.userName
                                                            post.publisherFullName =
                                                                userDetails.fullName
                                                        } else {
                                                            post.publisherImageUrl = ""
                                                            post.publisherUserName = "N/A"
                                                            post.publisherFullName = "N/A"
                                                        }
                                                    } else {
                                                        post.publisherImageUrl = ""
                                                        post.publisherUserName = "N/A"
                                                        post.publisherFullName = "N/A"
                                                    }
                                                    posts.add(post)
                                                    posts.reverse()
                                                    result.value = resouce.success(posts)
                                                }
                                            })
                                    }
                                }
                            }
                            result.value = resouce.success(posts)
                        }
                    })
                }
            }

        })
        return result
    }

    /**
     * get likes list with likes count and isLike by user
     */
    fun getLikesList(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, LikeModel>>> {
        val result: MutableLiveData<Resource<HashMap<String, LikeModel>>> =
            MutableLiveData<Resource<HashMap<String, LikeModel>>>()
        val resouce = Resource<HashMap<String, LikeModel>>()
        result.value = resouce.loading()

        val likesMap = HashMap<String, LikeModel>()
        val userId = getCurrentUserId()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.LIKES_TABLE_NAME)

        for (post in postsList) {
            postsRef.child(post.postId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val likeModel = LikeModel()
                    if (p0.child(userId).exists()) {
                        likeModel.isLikes = true
                    }

                    likeModel.likesCount = p0.childrenCount.toInt()
                    likesMap.put(post.postId, likeModel)
                    result.value = resouce.success(likesMap)
                }

            })
        }
        return result;
    }

    /**
     * get comments list with likes count and isLike by user
     */
    fun getCommentsList(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, Int>>> {
        val result: MutableLiveData<Resource<HashMap<String, Int>>> =
            MutableLiveData<Resource<HashMap<String, Int>>>()
        val resouce = Resource<HashMap<String, Int>>()
        result.value = resouce.loading()

        val commentsMap = HashMap<String, Int>()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.COMMENT_TABLE_NAME)

        for (post in postsList) {
            postsRef.child(post.postId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists() && p0.childrenCount > 0) {
                        commentsMap.put(post.postId, p0.childrenCount.toInt())
                    } else {
                        commentsMap.put(post.postId, 0)
                    }

                    result.value = resouce.success(commentsMap)
                }

            })
        }
        return result;
    }

    /**
     * to like or unlike a post
     */
    fun likeUnlikePost(postId: String, oldStatusIsLike: Boolean) {
        val userId = getCurrentUserId()
        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.LIKES_TABLE_NAME).child(postId)
        if (!oldStatusIsLike) {
            postsRef.child(userId).setValue(true)
        } else {
            postsRef.child(userId).removeValue()
        }
    }

    /**
     * To post a comment
     */
    fun postComment(postId: String, comment: CommentModel): MutableLiveData<Resource<Unit>> {
        var result: MutableLiveData<Resource<Unit>> =
            MutableLiveData<Resource<Unit>>()
        val resouce = Resource<Unit>()
        result.value = resouce.loading()

        val commentRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.COMMENT_TABLE_NAME)
        commentRef.child(postId).push().setValue(comment).addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = resouce.success(null)
            } else {
                result.value = resouce.error("Unable to Post Comment")
            }
        }
        return result
    }

    /**
     * get comments list for a particular post
     */
    fun getListOfComments(postId: String): MutableLiveData<Resource<ArrayList<CommentModel>>> {
        val result: MutableLiveData<Resource<ArrayList<CommentModel>>> =
            MutableLiveData<Resource<ArrayList<CommentModel>>>()
        val resouce = Resource<ArrayList<CommentModel>>()
        result.value = resouce.loading()

        val commentsList = ArrayList<CommentModel>()

        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.COMMENT_TABLE_NAME)
                .child(postId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                commentsList.clear()
                for (snapShot in dataSnapShot.children) {
                    val comment = snapShot.getValue(CommentModel::class.java)
                    comment?.let { commentsList.add(it) }
                }
                result.value = resouce.success(commentsList)
            }
        })
        return result
    }

    /**
     * get userDetails map from users id list
     */
    fun getUsersListFromIDList(userList: HashSet<String>): MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> {

        val result: MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> =
            MutableLiveData<Resource<HashMap<String, UserDetailsModel>>>()
        val resouce = Resource<HashMap<String, UserDetailsModel>>()
        result.value = resouce.loading()

        val usersMap = HashMap<String, UserDetailsModel>()

        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)

        for (userId in userList) {
            userRef.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        val userDetails = dataSnapShot.getValue(UserDetailsModel::class.java)

                        if (userDetails != null) {
                            usersMap[userDetails.userId] = userDetails
                            result.value = resouce.success(usersMap)
                        }
                    }
                }
            })
        }
        return result
    }

    fun getUserPosts(userId: String): MutableLiveData<Resource<ArrayList<Post>>> {
        val posts = ArrayList<Post>()

        val result: MutableLiveData<Resource<ArrayList<Post>>> =
            MutableLiveData()
        val resouce = Resource<ArrayList<Post>>()
        result.value = resouce.loading()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.POST_TABLE_NAME)
        val query = postsRef.orderByChild(FieldName.PUBLISHER_COLUMN_NAME).equalTo(userId)

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()){
                    posts.clear()
                    for (snapShot in dataSnapShot.children) {
                        val post = snapShot.getValue(Post::class.java)
                        if (post != null) {
                            posts.add(post)
                        }
                    }
                    result.value = resouce.success(posts)
                }
            }

        })
        return result
    }
}