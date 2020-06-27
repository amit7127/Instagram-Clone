package com.android.amit.instaclone.view.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.LikeModel
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.repo.Repository
import java.util.*

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/10/2020
 * Description:
 */
class HomeViewModel : ViewModel() {
    var repo: Repository = Repository()
    fun getPosts(): MutableLiveData<Resource<ArrayList<PostListItem>>> {
        return repo.getPostsList()
    }

    fun likeButtonClicked(postId: String, oldStatusIsLike: Boolean) {
        repo.likeUnlikePost(postId, oldStatusIsLike)
    }

    fun getLikesList(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, LikeModel>>> {
        return repo.getLikesList(postsList)
    }

    fun getCommentsCount(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, Int>>> {
        return repo.getCommentsList(postsList)
    }

    fun getSavedList(): MutableLiveData<Resource<HashMap<String, Boolean>>> {
        return repo.getSavedList()
    }

    fun savedClicked(postId: String, oldStatus: Boolean) {
        repo.saveClicked(postId, oldStatus)
    }
}