package com.android.amit.instaclone.data

import com.google.firebase.database.IgnoreExtraProperties

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: April/30/2020
 * Description:
 */

@IgnoreExtraProperties
class UserDetailsModel {
    var userId: String = ""
    var fullName: String = ""
    var userName: String = ""
    var email: String = ""
    var bio: String = ""
    var image: String = ""

    constructor()

    constructor(
        userId: String,
        fullName: String,
        userName: String,
        email: String,
        bio: String,
        image: String
    ) {
        this.userId = userId
        this.fullName = fullName
        this.userName = userName
        this.email = email
        this.bio = bio
        this.image = image
    }


}