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
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Search users list adapter
 */
class UserSerchAdapter(
    private val usersList: ArrayList<UserDetailsModel>,
    private val searchListener: UserSearchListener
) :
    RecyclerView.Adapter<UserSerchAdapter.UserSearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProfileListItemBinding.inflate(inflater, parent, false)
        return UserSearchViewHolder(binding, searchListener)
    }

    override fun getItemCount(): Int = usersList.size

    override fun onBindViewHolder(holder: UserSearchViewHolder, position: Int) {
        holder.bind(usersList[position])
    }

    class UserSearchViewHolder(
        val binding: ProfileListItemBinding,
        private val searchListener: UserSearchListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserDetailsModel) {
            binding.userModel = item
            binding.repoListener = searchListener
            binding.adapter = this
            binding.executePendingBindings()
        }

        /**
         * get follow un-follow status
         */
        fun getStatus(item: UserDetailsModel): String {
            val repo = Repository()
            return if (item.follower.containsKey(repo.getCurrentUserId())) {
                Status.Following
            } else {
                Status.Follow
            }
        }
    }

    interface UserSearchListener {
        //Follow button clicked
        fun onFollowButtonClicked(userId: String, view: View)

        //on profile selected
        fun onProfileClicked(userId: String)
    }
}