package com.android.amit.instaclone.data

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/14/2020
 * Description:
 */
class LikeModel {
    var isLikes: Boolean = false
    var likesCount: Int = 0

    constructor()
    constructor(isLikes: Boolean, likesCount: Int) {
        this.isLikes = isLikes
        this.likesCount = likesCount
    }
}