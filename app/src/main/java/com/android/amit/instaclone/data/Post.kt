package com.android.amit.instaclone.data

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/09/2020
 * Description:
 */
class Post {
    var postId: String = ""
    var description: String = ""
    var publisher: String = ""
    var postImage: String = ""

    constructor()

    constructor(postId: String, description: String, publisher: String, postImage: String) {
        this.postId = postId
        this.description = description
        this.publisher = publisher
        this.postImage = postImage
    }


}