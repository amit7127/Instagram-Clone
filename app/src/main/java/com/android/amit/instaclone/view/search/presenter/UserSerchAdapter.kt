package com.android.amit.instaclone.view.search.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.ProfileListItemBinding

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/02/2020
 * Description:
 */
class UserSerchAdapter(private val usersList: ArrayList<UserDetailsModel>) :
    RecyclerView.Adapter<UserSerchAdapter.UserSearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProfileListItemBinding.inflate(inflater)
        return UserSearchViewHolder(binding)
    }

    override fun getItemCount(): Int = usersList.size

    override fun onBindViewHolder(holder: UserSearchViewHolder, position: Int) {
        holder.bind(usersList[position])
    }

    class UserSearchViewHolder(val binding: ProfileListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDetailsModel) {
            binding.userModel = item
            binding.executePendingBindings()
        }
    }
}