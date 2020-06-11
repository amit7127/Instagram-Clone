package com.android.amit.instaclone.view.home.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.databinding.PostListItemBinding

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/11/2020
 * Description:
 */
class PostsListAdapter(private val postsList: ArrayList<PostListItem>) :
    RecyclerView.Adapter<PostsListAdapter.PostsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostListItemBinding.inflate(inflater, parent, false)
        return PostsListViewHolder(binding)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: PostsListViewHolder, position: Int) {
        holder.apply {
            bind(postsList[position])
        }
    }

    class PostsListViewHolder(val binding: PostListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostListItem) {
            binding.apply {
                postListItem = item
                executePendingBindings()
            }
        }
    }
}