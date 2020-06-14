package com.android.amit.instaclone.data

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/10/2020
 * Description:
 */
class PostListItem {
    var postId: String = ""
    var description: String = ""
    var publisher: String = ""
    var postImage: String = ""
    var publisherImageUrl = ""
    var publisherUserName = ""
    var publisherFullName = ""

    constructor()

    constructor(
        postId: String,
        description: String,
        publisher: String,
        postImage: String,
        publisherImageUrl: String,
        publisherUserName: String,
        publisherFullName: String
    ) {
        this.postId = postId
        this.description = description
        this.publisher = publisher
        this.postImage = postImage
        this.publisherImageUrl = publisherImageUrl
        this.publisherUserName = publisherUserName
        this.publisherFullName = publisherFullName
    }

}