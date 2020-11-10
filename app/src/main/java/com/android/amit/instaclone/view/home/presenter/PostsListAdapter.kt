package com.android.amit.instaclone.view.home.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.LikeModel
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.databinding.PostListItemBinding

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Posts list adapter
 */
class PostsListAdapter(
    private val postsList: ArrayList<PostListItem>,
    private val postListener: PostListener,
    private val likesList: HashMap<String, LikeModel>,
    private val commentsList: HashMap<String, Int>,
    private val savedList: HashMap<String, Boolean>
) :
    RecyclerView.Adapter<PostsListAdapter.PostsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostListItemBinding.inflate(inflater, parent, false)
        return PostsListViewHolder(binding)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: PostsListViewHolder, position: Int) {
        var like = LikeModel()
        val post = postsList[position]
        var isSaved = false
        if (likesList[post.postId] != null)
            like = likesList.getValue(post.postId)

        if (savedList.containsKey(post.postId))
            isSaved = true

        holder.apply {
            bind(post, like, commentsList[post.postId], isSaved, postListener)
        }
    }

    class PostsListViewHolder(val binding: PostListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var commentsCount: Int? = 0
        var isSaved: Boolean = false

        fun bind(
            item: PostListItem,
            like: LikeModel,
            commentsCount: Int?,
            isSaved: Boolean,
            postListener: PostListener
        ) {
            this.commentsCount = commentsCount
            this.isSaved = isSaved
            binding.apply {
                postListItem = item
                holder = this@PostsListViewHolder
                likeModel = like
                listener = postListener
                executePendingBindings()
            }
        }
    }

    interface PostListener {
        // on like button clicked
        fun onLikeButtonClicked(postId: String, oldStatusIsLike: Boolean, publisherId: String)

        //on comments button clicked event
        fun onCommentButtonClicked(postId: String, postImageUrlString: String, publisherId: String)

        //on saved button clicked
        fun onSaveButtonClicked(postId: String, oldStatus: Boolean)

        // on show liked user list button clicked
        fun onLikeTextClicked(postId: String)

        //on profile button clicked
        fun onProfileClicked(userId: String)
    }
}