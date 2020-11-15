package com.android.amit.instaclone.view.story

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.StoryModel
import com.android.amit.instaclone.repo.Repository
import com.android.amit.instaclone.util.Constants

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Add Story view model
 */
class AddStoryViewModel : ViewModel() {
    var repo: Repository = Repository()

    /**
     * Post story
     */
    fun postStory(imageUri: Uri): MutableLiveData<Resource<Unit>> {
        val story = StoryModel()
        story.timeStart = System.currentTimeMillis()
        story.timeEnd = System.currentTimeMillis() + Constants.STORY_END_INTERVAL
        story.userId = repo.getCurrentUserId()

        return repo.postStory(imageUri, story)
    }
}