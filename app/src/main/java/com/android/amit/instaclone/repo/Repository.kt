package com.android.amit.instaclone.repo

import android.content.res.Resources
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.*
import com.android.amit.instaclone.util.Constants
import com.android.amit.instaclone.util.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Repository to get the data from the server
 */
class Repository {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * get current logged-in user id
     */
    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    /**
     * Create firebase user with provider email and password
     * @param email: String email
     * @param password: String password
     *
     * @return Firebase user object
     */
    fun createUSerWithEmailAndPassword(
        email: String,
        password: String
    ): MutableLiveData<Resource<FirebaseUser>> {

        val result: MutableLiveData<Resource<FirebaseUser>> =
            MutableLiveData()
        val resource = Resource<FirebaseUser>()
        result.value = resource.loading()

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = resource.success(it.result?.user)
            } else {
                result.value = resource.error(it.exception?.message)
                mAuth.signOut()
            }
        }
        return result
    }

    /**
     * login user with provided email and password
     * @param email: String email
     * @param password: String password
     *
     * @return: Firebase user object
     */
    fun loginUserWithEmailAndPassword(
        email: String,
        password: String
    ): MutableLiveData<Resource<FirebaseUser>> {

        val result: MutableLiveData<Resource<FirebaseUser>> =
            MutableLiveData()
        val resource = Resource<FirebaseUser>()
        result.value = resource.loading()

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = resource.success(it.result?.user)
            } else {
                result.value = resource.error(it.exception?.message)
                mAuth.signOut()
            }
        }
        return result
    }

    /**
     * Save user details in firebase database
     * @param userDetails : user details object
     *
     * @return unit object for complete/error
     */
    fun saveUserInFirebase(
        userDetails: UserDetailsModel
    ): MutableLiveData<Resource<Unit>> {
        val userId = getCurrentUserId()
        userDetails.userId = userId
        userDetails.fullName = userDetails.fullName.toLowerCase(Locale.getDefault())
        userDetails.userName = userDetails.userName.toLowerCase(Locale.getDefault())

        val result: MutableLiveData<Resource<Unit>> =
            MutableLiveData()
        val resource = Resource<Unit>()
        result.value = resource.loading()

        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
        userRef.child(userId).setValue(userDetails).addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = resource.success(null)
            } else {
                //Pick string from Resources.getSystem().getString(R.string.common_google_play_services_install_text)
                result.value =
                    resource.error(Resources.getSystem().getString(R.string.failed_to_save_data))
                FirebaseAuth.getInstance().signOut()
            }
        }
        return result
    }


    /**
     * Provides the list of users matching the search query
     * @param nameQuery : searched string
     *
     * @return List of UserDetails object
     */
    fun getUsers(nameQuery: String): MutableLiveData<Resource<ArrayList<UserDetailsModel>>> {
        val users = arrayListOf<UserDetailsModel>()

        val result: MutableLiveData<Resource<ArrayList<UserDetailsModel>>> =
            MutableLiveData()
        val resource = Resource<ArrayList<UserDetailsModel>>()
        result.value = resource.loading()

        if (TextUtils.isEmpty(nameQuery)) {
            result.value = resource.success(null)
        } else {
            val userId = getCurrentUserId()
            val userRef: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
            val query =
                userRef.orderByChild(FieldName.FULL_NAME_COLUMN_NAME).startAt(nameQuery).endAt(
                    nameQuery + "\uf8ff"
                )

            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Timber.e(p0.message)
                    result.value = resource.error(p0.message)
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    users.clear()
                    for (snapShot in dataSnapShot.children) {
                        val user = snapShot.getValue(UserDetailsModel::class.java)
                        if (user != null && user.userId != userId) {
                            users.add(user)
                        }
                    }
                    result.value = resource.success(users)
                }
            })
        }
        return result
    }

    /**
     * follow/un-follow a user
     * @param followUserId: user id to follow
     * @param status: follow/un-follow
     *
     * @return: result confirmation complete/error
     */
    fun follow(followUserId: String, status: String): MutableLiveData<Resource<Unit>> {

        val result: MutableLiveData<Resource<Unit>> =
            MutableLiveData()
        val resource = Resource<Unit>()
        result.value = resource.loading()

        val userId = getCurrentUserId()

        if (status == Status.follow) {
            //If status need to set to follow
            //First set the user id in the current user following list
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME).child(userId)
                .child(FieldName.FOLLOWING_COLUMN_NAME).child(followUserId).setValue(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Set the current user id in the user's followers list
                        FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
                            .child(followUserId)
                            .child(FieldName.FOLLOWER_COLUMN_NAME).child(userId).setValue(true)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    result.value = resource.success(null)
                                } else {
                                    result.value = resource.error(
                                        Resources.getSystem()
                                            .getString(R.string.failed_to_update_status)
                                    )
                                }
                            }
                    } else {
                        result.value = resource.error(
                            Resources.getSystem().getString(R.string.failed_to_update_status)
                        )
                    }
                }
        } else if (status == Status.following) {
            //If status need to set to un-follow
            //First remove the user id from the current user following list
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME).child(userId)
                .child(FieldName.FOLLOWING_COLUMN_NAME).child(followUserId).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Remove the current user id from the user's followers list
                        FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
                            .child(followUserId)
                            .child(FieldName.FOLLOWER_COLUMN_NAME).child(userId).removeValue()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    result.value = resource.success(null)
                                } else {
                                    result.value = resource.error(
                                        Resources.getSystem()
                                            .getString(R.string.failed_to_update_status)
                                    )
                                }
                            }
                    } else {
                        result.value = resource.error(
                            Resources.getSystem().getString(R.string.failed_to_update_status)
                        )
                    }
                }
        }
        return result
    }

    /**
     * get user details from the provided user id
     * @param userId:userId string
     *
     * @return : uerDetails object
     */
    fun getUserDetails(userId: String): MutableLiveData<Resource<UserDetailsModel>> {

        val result = MutableLiveData<Resource<UserDetailsModel>>()
        val resource = Resource<UserDetailsModel>()

        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME).child(userId)
        result.value = resource.loading()
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.e(p0.message)
                result.value = resource.error(p0.message)
            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    val user = dataSnapShot.getValue(UserDetailsModel::class.java)
                    result.value = resource.success(user)
                } else {
                    result.value = resource.error(
                        Resources.getSystem().getString(R.string.unable_to_fetch_user_details)
                    )
                }
            }
        })
        return result
    }

    /**
     * save/update user profile, with user profile image
     * @param userDetails: userDetails object
     * @param profilePictureUri: uri path for profile picture
     *
     * @return result confirmation complete/error
     */
    fun saveUserProfileWithImage(
        userDetails: UserDetailsModel,
        profilePictureUri: Uri
    ): MutableLiveData<Resource<Unit>> {

        val result: MutableLiveData<Resource<Unit>> =
            MutableLiveData()
        val resource = Resource<Unit>()
        result.value = resource.loading()

        val firebaseStorage: StorageReference =
            FirebaseStorage.getInstance().reference.child(FieldName.PROFILE_PICTURE_FOLDER)
                .child(userDetails.userId + "jpg")

        //Upload profile image to firebase storage
        val uploadTask = firebaseStorage.putFile(profilePictureUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    result.value = resource.error(
                        Resources.getSystem().getString(R.string.failed_to_upload_profile_image)
                    )
                    throw it
                }
            }
            firebaseStorage.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //After profile image uploaded successfully
                // Insert/update the row into userDetails table in firebase db
                val downloadUri = task.result
                userDetails.image = downloadUri.toString()

                userDetails.fullName = userDetails.fullName.toLowerCase(Locale.getDefault())
                userDetails.userName = userDetails.userName.toLowerCase(Locale.getDefault())

                val userRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)
                userRef.child(userDetails.userId).setValue(userDetails).addOnCompleteListener {
                    if (it.isSuccessful) {
                        result.value = resource.success(null)
                    } else {
                        result.value = resource.error(
                            Resources.getSystem().getString(R.string.failed_to_update_user_detail)
                        )
                        FirebaseAuth.getInstance().signOut()
                    }
                }
            } else {
                result.value = resource.error(
                    Resources.getSystem().getString(R.string.failed_to_update_user_detail)
                )
            }
        }

        return result
    }

    /**
     * @param postPictureUri: uri path of the post picture
     * @param comment: comment in string
     *
     * @return result confirmation complete/error
     */
    fun postWithImage(postPictureUri: Uri, comment: String): MutableLiveData<Resource<Unit>> {
        val result: MutableLiveData<Resource<Unit>> =
            MutableLiveData()
        val resource = Resource<Unit>()
        result.value = resource.loading()

        //Storage reference for post picture
        val firebaseStorage: StorageReference =
            FirebaseStorage.getInstance().reference.child(FieldName.POST_PICTURE_FOLDER)
                .child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask = firebaseStorage.putFile(postPictureUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    result.value = resource.error(
                        Resources.getSystem().getString(R.string.failed_to_upload_post_image)
                    )
                    throw it
                }
            }
            firebaseStorage.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //After post image successfully uploaded
                // insert the row into posts table
                val downloadUri = task.result

                val userRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child(FieldName.POST_TABLE_NAME)

                val uId = getCurrentUserId()
                val postId = userRef.push().key

                val post = Post(postId!!, comment, uId, downloadUri.toString())

                userRef.child(postId).setValue(post).addOnCompleteListener {
                    if (it.isSuccessful) {
                        result.value = resource.success(null)
                    } else {
                        result.value =
                            resource.error(Resources.getSystem().getString(R.string.unable_post))
                        FirebaseAuth.getInstance().signOut()
                    }
                }
            } else {
                result.value = resource.error(Resources.getSystem().getString(R.string.unable_post))
            }
        }
        return result
    }

    /**
     * get list of posts for the users which current user is following
     *
     * @return: List of Posts
     */
    fun getPostsList(): MutableLiveData<Resource<ArrayList<PostListItem>>> {
        val posts = arrayListOf<PostListItem>()

        val result: MutableLiveData<Resource<ArrayList<PostListItem>>> =
            MutableLiveData()
        val resource = Resource<ArrayList<PostListItem>>()
        result.value = resource.loading()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.POST_TABLE_NAME)
        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)

        val currentUserReference = userRef.child(getCurrentUserId())

        //First fetch current user data, for get the following list
        currentUserReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.e(p0.message)
                result.value = resource.error(
                    Resources.getSystem().getString(R.string.unable_to_fetch_user_details)
                )
            }

            override fun onDataChange(userDataSnapshot: DataSnapshot) {

                if (userDataSnapshot.exists()) {
                    val currentUserDetails = userDataSnapshot.getValue(UserDetailsModel::class.java)

                    //Get posts data
                    postsRef.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Timber.e(p0.message)
                            result.value = resource.error(
                                Resources.getSystem().getString(R.string.failed_to_fetch_post_data)
                            )
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
                                        //get user info for the above posts
                                        userRef.child(userId)
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onCancelled(p0: DatabaseError) {
                                                    //unable to fetch data, set default data
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
                                                    result.value = resource.success(posts)
                                                }
                                            })
                                    }
                                }
                            }
                            result.value = resource.success(posts)
                        }
                    })
                }
            }
        })
        return result
    }

    /**
     * get likes list with likes count and isLike by user
     *
     * @param postsList: list of posts
     *
     * @return: Map of postId and LikeModel
     */
    fun getLikesList(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, LikeModel>>> {
        val result: MutableLiveData<Resource<HashMap<String, LikeModel>>> =
            MutableLiveData()
        val resource = Resource<HashMap<String, LikeModel>>()
        result.value = resource.loading()

        val likesMap = HashMap<String, LikeModel>()
        val userId = getCurrentUserId()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.LIKES_TABLE_NAME)

        for (post in postsList) {
            postsRef.child(post.postId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Timber.e(p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val likeModel = LikeModel()
                    if (p0.child(userId).exists()) {
                        likeModel.isLikes = true
                    }

                    likeModel.likesCount = p0.childrenCount.toInt()
                    likesMap[post.postId] = likeModel
                    result.value = resource.success(likesMap)
                }

            })
        }
        return result
    }

    /**
     * get comments list with comments count from all users
     *
     * @param postsList: List of posts
     *
     * @return : Map of PostsId and comments count as integer
     */
    fun getCommentsList(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, Int>>> {
        val result: MutableLiveData<Resource<HashMap<String, Int>>> =
            MutableLiveData()
        val resource = Resource<HashMap<String, Int>>()
        result.value = resource.loading()

        val commentsMap = HashMap<String, Int>()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.COMMENT_TABLE_NAME)

        for (post in postsList) {
            postsRef.child(post.postId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Timber.e(p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists() && p0.childrenCount > 0) {
                        commentsMap[post.postId] = p0.childrenCount.toInt()
                    } else {
                        commentsMap[post.postId] = 0
                    }

                    result.value = resource.success(commentsMap)
                }

            })
        }
        return result
    }

    /**
     * to like or unlike a post
     *
     * @param postId: id of the post whick like status need to be changed
     * @param oldStatusIsLike: previous status like/unlike
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
     * @param postId: String post id for which comment is added
     * @param comment: Comments details with message and commenter info
     *
     * @return result confirmation complete/error
     */
    fun postComment(postId: String, comment: CommentModel): MutableLiveData<Resource<Unit>> {
        val result: MutableLiveData<Resource<Unit>> =
            MutableLiveData()
        val resource = Resource<Unit>()
        result.value = resource.loading()

        val commentRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.COMMENT_TABLE_NAME)
        commentRef.child(postId).push().setValue(comment).addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = resource.success(null)
            } else {
                result.value =
                    resource.error(Resources.getSystem().getString(R.string.unable_to_post_comment))
            }
        }
        return result
    }

    /**
     * get comments list for a particular post
     *
     * @param postId: String post id for which comments list need to be fetched
     *
     * @return: List of comments object
     */
    fun getListOfComments(postId: String): MutableLiveData<Resource<ArrayList<CommentModel>>> {
        val result: MutableLiveData<Resource<ArrayList<CommentModel>>> =
            MutableLiveData()
        val resource = Resource<ArrayList<CommentModel>>()
        result.value = resource.loading()

        val commentsList = ArrayList<CommentModel>()

        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.COMMENT_TABLE_NAME)
                .child(postId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.e(p0.message)
                result.value = resource.error(p0.message)
            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                commentsList.clear()
                for (snapShot in dataSnapShot.children) {
                    val comment = snapShot.getValue(CommentModel::class.java)
                    comment?.let { commentsList.add(it) }
                }
                result.value = resource.success(commentsList)
            }
        })
        return result
    }

    /**
     * get userDetails map from users id list
     *
     * @param userList set of usersId string
     *
     * @return: Map of userId String and UserDetails object
     */
    fun getUsersListFromIDList(userList: HashSet<String>): MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> {

        val result: MutableLiveData<Resource<HashMap<String, UserDetailsModel>>> =
            MutableLiveData()
        val resource = Resource<HashMap<String, UserDetailsModel>>()
        result.value = resource.loading()

        val usersMap = HashMap<String, UserDetailsModel>()
        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)

        for (userId in userList) {
            userRef.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Timber.e(p0.message)
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        val userDetails = dataSnapShot.getValue(UserDetailsModel::class.java)

                        if (userDetails != null) {
                            usersMap[userDetails.userId] = userDetails
                            result.value = resource.success(usersMap)
                        }
                    }
                }
            })
        }
        return result
    }

    /**
     * get user's posts
     *
     * @param userId: String userId
     *
     * @return: Lists of Posts
     */
    fun getUserPosts(userId: String): MutableLiveData<Resource<ArrayList<Post>>> {
        val posts = ArrayList<Post>()

        val result: MutableLiveData<Resource<ArrayList<Post>>> =
            MutableLiveData()
        val resource = Resource<ArrayList<Post>>()
        result.value = resource.loading()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.POST_TABLE_NAME)
        val query = postsRef.orderByChild(FieldName.PUBLISHER_COLUMN_NAME).equalTo(userId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.e(p0.message)
                result.value = resource.error(p0.message)
            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    posts.clear()
                    for (snapShot in dataSnapShot.children) {
                        val post = snapShot.getValue(Post::class.java)
                        if (post != null) {
                            posts.add(post)
                        }
                    }
                    result.value = resource.success(posts)
                }
            }

        })
        return result
    }

    /**
     * get post from the post id
     * @param postId: String id of the post
     *
     * @return: Post object with details
     */
    fun getPostFromId(postId: String): MutableLiveData<Resource<PostListItem>> {
        val result: MutableLiveData<Resource<PostListItem>> =
            MutableLiveData()
        val resource = Resource<PostListItem>()
        result.value = resource.loading()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.POST_TABLE_NAME).child(postId)

        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME)

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.e(p0.message)
                result.value = resource.error(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val post = p0.getValue(PostListItem::class.java)
                    if (post != null) {
                        val userId = post.publisher

                        userRef.child(userId)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    //if post does not exists
                                    Timber.e(p0.message)
                                    post.publisherImageUrl = ""
                                    post.publisherUserName = "N/A"
                                    post.publisherFullName = "N/A"
                                }

                                override fun onDataChange(dataSnapShot: DataSnapshot) {
                                    if (dataSnapShot.exists()) {
                                        //If posts exists then fetch the user details of the owner of the post
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
                                    result.value = resource.success(post)
                                }
                            })
                        result.value = resource.success(post)
                    }
                }
            }
        })
        return result
    }

    /**
     * Save post for the current user
     *
     * @param postId: String post id
     * @param oldStatus: boolean previous status for saved/not-saved
     */
    fun saveClicked(postId: String, oldStatus: Boolean) {
        val saveRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.SAVED_TABLE_NAME)
                .child(getCurrentUserId())
        if (oldStatus) {
            saveRef.child(postId).removeValue()
        } else {
            saveRef.child(postId).setValue(true)
        }
    }

    /**
     * get saved list for the current user
     *
     * @return: Map of postId string and saved status boolean
     */
    fun getSavedList(): MutableLiveData<Resource<HashMap<String, Boolean>>> {

        val result: MutableLiveData<Resource<HashMap<String, Boolean>>> =
            MutableLiveData()
        val resource = Resource<HashMap<String, Boolean>>()
        result.value = resource.loading()

        val savedMap = HashMap<String, Boolean>()
        val saveRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.SAVED_TABLE_NAME)
                .child(getCurrentUserId())
        saveRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.e(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                savedMap.clear()
                if (p0.exists()) {
                    savedMap.putAll(p0.getValue<HashMap<String, Boolean>>()!!)
                }

                result.value = resource.success(savedMap)
            }
        })

        return result
    }

    /**
     * get posts list from id list
     * @param idList: List of postId string
     *
     * @return: Lists of posts
     */
    fun getPostsFromIds(idList: ArrayList<String>): MutableLiveData<Resource<ArrayList<Post>>> {
        val result: MutableLiveData<Resource<ArrayList<Post>>> =
            MutableLiveData()
        val resource = Resource<ArrayList<Post>>()
        result.value = resource.loading()

        val postsList = ArrayList<Post>()

        val postsRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.POST_TABLE_NAME)
        for (postId in idList) {
            postsRef.child(postId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Timber.e(p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val post = p0.getValue(Post::class.java)
                        if (post != null) {
                            postsList.add(post)
                            result.value = resource.success(postsList)
                        }
                    }
                }
            })
        }

        return result
    }

    /**
     * get users id list for a post likes
     *
     * @param postId: string of the post
     *
     * @return: userId string list, who liked that posts
     */
    fun getLikedUsersList(postId: String): MutableLiveData<Resource<ArrayList<String>>> {
        val result: MutableLiveData<Resource<ArrayList<String>>> =
            MutableLiveData()
        val resource = Resource<ArrayList<String>>()
        result.value = resource.loading()

        val usersList = ArrayList<String>()

        val likesRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.LIKES_TABLE_NAME)
        likesRef.child(postId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.e(p0.message)
                result.value = resource.error(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                usersList.clear()
                if (p0.exists()) {
                    usersList.addAll(p0.getValue<HashMap<String, Boolean>>()!!.keys)
                    result.value = resource.success(usersList)
                }
            }
        })
        return result
    }

    /**
     * Get followers user id list
     *
     * @param userId: string user id
     *
     * @return: Lists of userId, who are follower of the above user
     */
    fun getFollowerUserList(userId: String): MutableLiveData<Resource<ArrayList<String>>> {
        val result: MutableLiveData<Resource<ArrayList<String>>> =
            MutableLiveData()
        val resource = Resource<ArrayList<String>>()
        result.value = resource.loading()

        val usersList = ArrayList<String>()

        val followerRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME).child(userId)
                .child(FieldName.FOLLOWER_COLUMN_NAME)
        followerRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.e(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                usersList.clear()
                if (p0.exists()) {
                    usersList.addAll(p0.getValue<HashMap<String, Boolean>>()!!.keys)
                    result.value = resource.success(usersList)
                }
            }

        })
        return result
    }

    /**
     * Get Following user id list
     * @param userId: string user id
     *
     * @return: Lists of userId, who are following the above user
     */
    fun getFollowingUserList(userId: String): MutableLiveData<Resource<ArrayList<String>>> {
        val result: MutableLiveData<Resource<ArrayList<String>>> =
            MutableLiveData()
        val resource = Resource<ArrayList<String>>()
        result.value = resource.loading()

        val usersList = ArrayList<String>()

        val followingRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.USER_TABLE_NAME).child(userId)
                .child(FieldName.FOLLOWING_COLUMN_NAME)
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.e(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                usersList.clear()
                if (p0.exists()) {
                    usersList.addAll(p0.getValue<HashMap<String, Boolean>>()!!.keys)
                }
                result.value = resource.success(usersList)
            }

        })
        return result
    }

    /**
     * Add notification to DB
     *
     * @param notification: notification object with all the details
     * @param targetUserId: userId string, of target user to show
     */
    fun addNotification(notification: Notification, targetUserId: String) {
        notification.publisherId = getCurrentUserId()
        val notificationRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.NOTIFICATION_TABLE_NAME)
                .child(targetUserId)
        val id = notificationRef.push().key
        if (id != null) {
            notification.id = id
            notificationRef.child(id).setValue(notification)
        }
    }

    /**
     * get notification list of the current user
     *
     * @return: List of notification objects
     */
    fun getNotifications(): MutableLiveData<Resource<ArrayList<Notification>>> {
        val result: MutableLiveData<Resource<ArrayList<Notification>>> =
            MutableLiveData()
        val resource = Resource<ArrayList<Notification>>()
        result.value = resource.loading()

        val notifications = ArrayList<Notification>()

        val notificationRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.NOTIFICATION_TABLE_NAME)
                .child(getCurrentUserId())
        notificationRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                notifications.clear()
                Timber.e(p0.message)
                result.value = resource.error(p0.message)
            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                notifications.clear()
                for (snapShot in dataSnapShot.children) {
                    val notification = snapShot.getValue(Notification::class.java)
                    if (notification != null) {
                        notifications.add(notification)
                    }
                }

                result.value = resource.success(notifications)
            }

        })
        return result
    }

    /**
     * Mark notification as read
     *
     * @param notification: notification object which needs to be marked
     */
    fun markReadNotification(notification: Notification) {
        FirebaseDatabase.getInstance().reference.child(FieldName.NOTIFICATION_TABLE_NAME)
            .child(getCurrentUserId()).child(notification.id).child(Constants.VIEW_COLUMN)
            .setValue(true)
    }

    /**
     * get number of unread notification
     *
     * @return : integer, unread notification count
     */
    fun getNotificationCount(): MutableLiveData<Resource<Int>> {
        val result = MutableLiveData<Resource<Int>>()
        val resource = Resource<Int>()
        result.value = resource.loading()

        val notificationRef =
            FirebaseDatabase.getInstance().reference.child(FieldName.NOTIFICATION_TABLE_NAME)
                .child(getCurrentUserId())

        notificationRef.orderByChild(Constants.VIEW_COLUMN).equalTo(false)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    result.value = resource.error(p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        result.value = resource.success(p0.children.count())
                    } else {
                        result.value = resource.success(0)
                    }
                }
            })
        return result
    }

    /**
     * get story list for following user list
     *
     * @param followingUserList: following user list
     *
     * @return lists of story objects
     */
    fun getStories(followingUserList: ArrayList<String>): MutableLiveData<Resource<ArrayList<StoryModel>>> {
        followingUserList.add(getCurrentUserId())

        val result = MutableLiveData<Resource<ArrayList<StoryModel>>>()
        val resource = Resource<ArrayList<StoryModel>>()
        result.value = resource.loading()

        val storyList = ArrayList<StoryModel>()

        val timeCurrent = System.currentTimeMillis()
        val currentUserId = getCurrentUserId()
        storyList.add(StoryModel(currentUserId))

        val storyReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.STORY_TABLE_NAME)

        for (id in followingUserList) {
            //dummy
            storyReference.orderByChild(Constants.USER_ID_TAG).equalTo(id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Timber.e(p0.message)
                    }

                    override fun onDataChange(dataSnapShot: DataSnapshot) {
                        if (dataSnapShot.exists()) {
                            for (snapShot in dataSnapShot.children) {
                                val story = snapShot.getValue(StoryModel::class.java)
                                if (story != null && timeCurrent > story.timeStart && timeCurrent < story.timeEnd) {
                                    var isPresent = false
                                    for (storyModel in storyList) {
                                        if (storyModel.storyId == story.storyId && !story.seen.containsKey(
                                                currentUserId
                                            )
                                        ) {
                                            isPresent = true
                                            storyList[storyList.indexOf(storyModel)] = story
                                        }
                                    }
                                    if (!isPresent)
                                        storyList.add(story)
                                    break
                                }
                            }
                        }
                        result.value = resource.success(storyList)
                    }
                })
        }
        return result
    }

    /**
     * Posts story to DB
     *
     * @param storyPictureUri: story picture Uri path
     * @param story: story object with details
     *
     * @return: success/error
     */
    fun postStory(storyPictureUri: Uri, story: StoryModel): MutableLiveData<Resource<Unit>> {
        val result: MutableLiveData<Resource<Unit>> =
            MutableLiveData()
        val resource = Resource<Unit>()
        result.value = resource.loading()

        //firebase storage reference
        val firebaseStorage: StorageReference =
            FirebaseStorage.getInstance().reference.child(FieldName.STORY_PICTURE_FOLDER)
                .child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask = firebaseStorage.putFile(storyPictureUri)

        //Start uploading task
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    result.value = resource.error(
                        Resources.getSystem().getString(R.string.failed_to_upload_status_image)
                    )
                    throw it
                }
            }
            firebaseStorage.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()

                val storyRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child(FieldName.STORY_TABLE_NAME)
                val storyId = storyRef.push().key.toString()

                story.storyId = storyId
                story.imageUrl = downloadUri

                storyRef.child(storyId).setValue(story).addOnCompleteListener {
                    if (it.isSuccessful) {
                        result.value = resource.success(null)
                    } else {
                        result.value = resource.error(
                            Resources.getSystem().getString(R.string.unable_to_share_status)
                        )
                    }
                }
            } else {
                result.value =
                    resource.error(Resources.getSystem().getString(R.string.unable_to_share_status))
            }
        }
        return result
    }

    /**
     * get story for the user id
     *
     * @param userId: string user id
     *
     * @return List of story object
     */
    fun getStoryListForTheUser(userId: String): MutableLiveData<Resource<List<StoryModel>>> {
        val result: MutableLiveData<Resource<List<StoryModel>>> =
            MutableLiveData()
        val resource = Resource<List<StoryModel>>()
        result.value = resource.loading()

        val storyList = ArrayList<StoryModel>()

        val timeCurrent = System.currentTimeMillis()
        val storyReference =
            FirebaseDatabase.getInstance().reference.child(FieldName.STORY_TABLE_NAME)

        storyReference.orderByChild(Constants.USER_ID_TAG).equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    result.value = resource.error(p0.message)
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        for (snapShot in dataSnapShot.children) {
                            val story = snapShot.getValue(StoryModel::class.java)
                            if (story != null && timeCurrent > story.timeStart && timeCurrent < story.timeEnd) {
                                var isPresent = false
                                for (storyModel in storyList) {
                                    if (storyModel.storyId == story.storyId) {
                                        isPresent = true
                                        storyList[storyList.indexOf(storyModel)] = story
                                    }
                                }
                                if (!isPresent)
                                    storyList.add(story)
                            }
                        }
                    }
                    result.value = resource.success(storyList)
                }
            })
        return result
    }

    /**
     * Set story seen as for current user
     *
     * @param storyId: string story id
     */
    fun setStorySeen(storyId: String) {
        val currentUserId = getCurrentUserId()

        FirebaseDatabase.getInstance().reference.child(FieldName.STORY_TABLE_NAME)
            .child(storyId)
            .child("seen").child(currentUserId).setValue(true)
    }

    /**
     * delete story by story id
     *
     * @param storyId: string story id
     */
    fun deleteStory(storyId: String) {
        FirebaseDatabase.getInstance().reference.child(FieldName.STORY_TABLE_NAME)
            .child(storyId).removeValue()
    }

    /**
     * Get story by story id
     *
     * @param storyId: string story id
     */
    fun getStoryById(storyId: String): MutableLiveData<Resource<StoryModel>> {
        val result: MutableLiveData<Resource<StoryModel>> =
            MutableLiveData()
        val resource = Resource<StoryModel>()
        result.value = resource.loading()

        FirebaseDatabase.getInstance().reference.child(FieldName.STORY_TABLE_NAME)
            .child(storyId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    result.value = resource.error(p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val story = p0.getValue(StoryModel::class.java)
                        result.value = resource.success(story)
                    }
                }
            })
        return result
    }
}