package com.android.amit.instaclone.view.comments.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.CommentModel
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.CommentsListItemBinding

class CommnetsListAdapter(
    private val commentsList: ArrayList<CommentModel>,
    private val usersMap: HashMap<String, UserDetailsModel>
) : RecyclerView.Adapter<CommnetsListAdapter.CommnetsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommnetsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CommentsListItemBinding.inflate(inflater, parent, false)
        return CommnetsListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    override fun onBindViewHolder(holder: CommnetsListViewHolder, position: Int) {
        var comment = commentsList[position]
        var user = UserDetailsModel()
        if (usersMap[comment.publisher] != null) {
            user = usersMap[comment.publisher]!!
        }

        holder.apply {
            bind(comment, user)
        }
    }

    class CommnetsListViewHolder(val binding: CommentsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: CommentModel, user: UserDetailsModel) {
            binding.apply {
                commentItem = comment
                this.user = user
                executePendingBindings()
            }
        }
    }
}