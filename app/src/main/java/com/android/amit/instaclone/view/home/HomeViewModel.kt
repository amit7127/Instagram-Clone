package com.android.amit.instaclone.view.home

import android.util.Log
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
        log("Amit")
        repo.likeUnlikePost(postId, oldStatusIsLike)
    }

    fun getLikesList(postsList: ArrayList<PostListItem>): MutableLiveData<Resource<HashMap<String, LikeModel>>> {
        return repo.getLikesList(postsList)
    }

    fun log (log: String){
        Log.i("Log", log)
    }
}