package com.android.amit.instaclone.view.search

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.amit.instaclone.data.Resource
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.repo.Repository

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/03/2020
 * Description:
 */
class SearchFragmentViewModel : ViewModel() {
    var searchQuerry: String = ""

    fun searchUserByQuerry(querry: String): MutableLiveData<Resource<ArrayList<UserDetailsModel>>> {
        var result = MutableLiveData<Resource<ArrayList<UserDetailsModel>>>()

//        if (!TextUtils.isEmpty(querry)) {
            val repo = Repository()
            result = repo.getUsers(querry)
//        }
        return result
    }
}