package com.android.amit.instaclone.view.profile.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.databinding.PostImageProfileListItemBinding

class UploadedPostImagesAdapter(
    private val postList: ArrayList<Post>,
    private val postListener: PostImageHandler) :
    RecyclerView.Adapter<UploadedPostImagesAdapter.PostImagesHolder>() {

    class PostImagesHolder(val binding: PostImageProfileListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, listener: PostImageHandler) {
            binding.apply {
                this.post = post
                this.listener = listener
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImagesHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostImageProfileListItemBinding.inflate(inflater, parent, false)
        return PostImagesHolder(binding)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: PostImagesHolder, position: Int) {
        holder.apply {
            bind(postList[position], postListener)
        }
    }

    interface PostImageHandler{
        fun onPostClicked(postId: String)
    }
}