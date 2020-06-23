package com.android.amit.instaclone.view.profile.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.databinding.PostImageProfileListItemBinding

class UploadedPostImagesAdapter(private val postList: ArrayList<Post>) :
    RecyclerView.Adapter<UploadedPostImagesAdapter.PostImagesHolder>() {

    class PostImagesHolder(val binding: PostImageProfileListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                this.post = post
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
            bind(postList[position])
        }
    }
}