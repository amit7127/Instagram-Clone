package com.android.amit.instaclone.view.story

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.StoryModel
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository

class ShowStoryViewModel : ViewModel() {

    var repo: Repository = Repository()

    // Get stories for a specific user
    fun fetchStoryDetails(userId: String): MutableLiveData<Resource<List<StoryModel>>> {
        return repo.getStoryListForTheUser(userId)
    }

    //Get user details from user id
    fun getUserData(userId: String): MutableLiveData<Resource<UserDetailsModel>> {
        return repo.getUserDetails(userId)
    }

    //set story as seen by current user
    fun setStorySeen(storyId: String) {
        repo.setStorySeen(storyId)
    }

    //Delete the story by story id
    fun deleteStory(storyId: String) {
        repo.deleteStory(storyId)
    }
}