package com.android.amit.instaclone.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class UserDetailsModel {
    var userId: String = ""
    var fullName: String = ""
    var userName: String = ""
    var email: String = ""
    var bio: String = ""
    var image: String = ""
    var Follower: Map<String, Boolean> = HashMap()
    var Following: Map<String, Boolean> = HashMap()

    constructor()

    constructor(
        userId: String,
        fullName: String,
        userName: String,
        email: String,
        bio: String,
        image: String,
        Follower: Map<String, Boolean>,
        Following: Map<String, Boolean>
    ) {
        this.userId = userId
        this.fullName = fullName
        this.userName = userName
        this.email = email
        this.bio = bio
        this.image = image
        this.Follower = Follower
        this.Following = Following
    }
}