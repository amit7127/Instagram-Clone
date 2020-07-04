package com.android.amit.instaclone.view.home.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.LikeModel
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.databinding.PostListItemBinding
import com.android.amit.instaclone.repo.Repository
import com.android.amit.instaclone.view.home.HomeViewModel

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/11/2020
 * Description:
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
        return PostsListViewHolder(binding, postListener)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: PostsListViewHolder, position: Int) {
        var like = LikeModel()
        var post = postsList[position]
        var isSaved: Boolean = false
        if (likesList.get(post.postId) != null)
            like = likesList.getValue(post.postId)

        if (savedList.containsKey(post.postId))
            isSaved = true

        holder.apply {
            bind(post, like, commentsList[post.postId], isSaved)
        }
    }

    class PostsListViewHolder(val binding: PostListItemBinding, val postListener: PostListener) :
        RecyclerView.ViewHolder(binding.root) {

        var commentsCount : Int? = 0
        var isSaved : Boolean = false

        fun bind(item: PostListItem, like: LikeModel, commentsCount: Int?, isSaved: Boolean) {
            this.commentsCount = commentsCount
            this.isSaved = isSaved
            binding.apply {
                postListItem = item
                holder = this@PostsListViewHolder
                likeModel = like
                executePendingBindings()
            }
        }

        fun onCommentClicked(postId: String, postImageUrlString : String){
            postListener.onCommentButtonClicked(postId, postImageUrlString)
        }

        fun likeButtonClicked(postId: String, oldStatusIsLike: Boolean) {
            postListener.onLikeButtonClicked(postId, oldStatusIsLike)
        }

        fun onSaveButtonClicked(postId: String, oldStatus: Boolean){
            postListener.onSaveButtonClicked(postId, oldStatus)
        }

        fun onLikeTextClicked(postId: String){
            postListener.onLikeTextClicked(postId)
        }
    }

    interface PostListener {
        fun onLikeButtonClicked(postId: String, oldStatusIsLike: Boolean)
        fun onCommentButtonClicked(postId: String, postImageUrlString : String)
        fun onSaveButtonClicked(postId: String, oldStatus: Boolean)
        fun onLikeTextClicked(postId: String)
    }
}