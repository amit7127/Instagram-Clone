package com.android.amit.instaclone.view.search.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.ProfileListItemBinding
import com.android.amit.instaclone.repo.Repository
import com.android.amit.instaclone.util.Status

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/02/2020
 * Description:
 */
class UserSerchAdapter(
    private val usersList: ArrayList<UserDetailsModel>,
    private val searchListener: UserSerchAdapter.UserSearchListener
) :
    RecyclerView.Adapter<UserSerchAdapter.UserSearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProfileListItemBinding.inflate(inflater)
        return UserSearchViewHolder(binding, searchListener)
    }

    override fun getItemCount(): Int = usersList.size

    override fun onBindViewHolder(holder: UserSearchViewHolder, position: Int) {
        holder.bind(usersList[position])
    }

    class UserSearchViewHolder(
        val binding: ProfileListItemBinding,
        val searchListener: UserSerchAdapter.UserSearchListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserDetailsModel) {
            binding.userModel = item
            binding.repoListener = searchListener
            binding.adapter = this
            binding.executePendingBindings()
        }

        fun getStatus(item: UserDetailsModel): String {
            val repo = Repository()
            if (item.Follower.containsKey(repo.getCurrentUserId())) {
                return Status.following
            } else {
                return Status.follow
            }
        }
    }

    interface UserSearchListener {
        fun onFollowButtonClicked(userId: String, view: View)
        fun onProfileClicked(userId: String)
    }
}