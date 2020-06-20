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
    private val homeViewModel : HomeViewModel
) :
    RecyclerView.Adapter<PostsListAdapter.PostsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostListItemBinding.inflate(inflater, parent, false)
        return PostsListViewHolder(binding, postListener, homeViewModel)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: PostsListViewHolder, position: Int) {
        var like = LikeModel()
        var post = postsList[position]
        if (likesList.get(post.postId) != null)
            like = likesList.getValue(post.postId)
        holder.apply {
            bind(post, like)
        }
    }

    class PostsListViewHolder(val binding: PostListItemBinding, val postListener: PostListener, val homeViewModel : HomeViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostListItem, like: LikeModel) {
            binding.apply {
                postListItem = item
                holder = this@PostsListViewHolder
                likeModel = like
                viewModel = homeViewModel
                executePendingBindings()
            }
        }

//        fun isLiked(imageView: ImageView, likesMap: HashMap<String, Boolean>): Int {
//            val repo = Repository()
//            val currentUserId = repo.getCurrentUserId()
//            if (likesMap.containsKey(currentUserId)) {
//                return R.drawable.heart_clicked
//            } else {
//                return R.drawable.heart_not_clicked
//            }
//        }

        fun onLikeClicked(postId: String, isLiked: Boolean) {
            postListener.onLikeButtonClicked(postId, isLiked)
        }

        fun onCommentClicked(postId: String, postImageUrlString : String){
            postListener.onCommentButtonClicked(postId, postImageUrlString)
        }
    }

    interface PostListener {
        fun onLikeButtonClicked(postId: String, oldStatusIsLike: Boolean)
        fun onCommentButtonClicked(postId: String, postImageUrlString : String)
    }
}