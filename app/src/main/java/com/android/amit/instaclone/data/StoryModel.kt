package com.android.amit.instaclone.data

class StoryModel {
    var imageUrl: String = ""
    var timeStart: Long = 0
    var timeEnd: Long = 0
    var storyId: String = ""
    var userId: String = ""

    constructor()

    constructor(imageUrl: String, timeStart: Long, timeEnd: Long, storyId: String, userId: String) {
        this.imageUrl = imageUrl
        this.timeStart = timeStart
        this.timeEnd = timeEnd
        this.storyId = storyId
        this.userId = userId
    }

    constructor(userId: String) {
        this.userId = userId
    }
}