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
data class UserDetailsModel(var userId : String, var fullName: String,
                            var userName: String, var email: String, var bio: String, var image: String) {
}