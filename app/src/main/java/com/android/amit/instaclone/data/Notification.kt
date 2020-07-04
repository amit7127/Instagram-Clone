package com.android.amit.instaclone.data

class Notification {
    var isView: Boolean = false
    var postId: String = ""
    var notificationText: String = ""
    var publisherId: String = ""
    var isPost: Boolean = false
    var isFollow: Boolean = false
    var id: String = ""

    constructor()
    constructor(
        isView: Boolean,
        postId: String,
        notificationText: String,
        publisherId: String,
        isPost: Boolean,
        isFollow: Boolean,
        id: String
    ) {
        this.isView = isView
        this.postId = postId
        this.notificationText = notificationText
        this.publisherId = publisherId
        this.isPost = isPost
        this.isFollow = isFollow
        this.id = id
    }


}