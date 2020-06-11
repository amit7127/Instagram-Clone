package com.android.amit.instaclone.view.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.repo.Repository
import java.util.ArrayList

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
}