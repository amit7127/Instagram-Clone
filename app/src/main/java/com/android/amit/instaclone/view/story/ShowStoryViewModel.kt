package com.android.amit.instaclone.view.story

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.StoryModel
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository

class ShowStoryViewModel : ViewModel() {

    var repo: Repository = Repository()

    fun fetchStoryDetails(userId: String): MutableLiveData<Resource<List<StoryModel>>> {
        return repo.getStoryListForTheUser(userId)
    }

    fun getUserData(userId: String): MutableLiveData<Resource<UserDetailsModel>> {
        return repo.getUserDetails(userId)
    }

    fun setStorySeen(storyId: String){
        repo.setStorySeen(storyId)
    }
}