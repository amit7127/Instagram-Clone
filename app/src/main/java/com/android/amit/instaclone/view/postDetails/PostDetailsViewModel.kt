package com.android.amit.instaclone.view.postDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.LikeModel
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.repo.Repository
import java.util.ArrayList
import java.util.HashMap

class PostDetailsViewModel : ViewModel() {
    var repo: Repository = Repository()
    fun getPostDetails(postId: String): MutableLiveData<Resource<PostListItem>> {
        return repo.getPostFromId(postId)
    }

    fun likeButtonClicked(postId: String, oldStatusIsLike: Boolean) {
        repo.likeUnlikePost(postId, oldStatusIsLike)
    }

    fun getLikesList(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, LikeModel>>> {
        return repo.getLikesList(postsList)
    }

    fun getCommentsCount(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, Int>>>{
        return repo.getCommentsList(postsList)
    }
}