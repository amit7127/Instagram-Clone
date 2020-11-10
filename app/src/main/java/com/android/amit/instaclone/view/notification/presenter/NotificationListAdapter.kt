package com.android.amit.instaclone.view.notification.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.NotificationListItemBinding
/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Notifications list adapter
 */
class NotificationListAdapter(
    private val notificationList: ArrayList<Notification>,
    private val userList: HashMap<String, UserDetailsModel>,
    private val notificationListener: NotificationListHandler,
    private val postList: ArrayList<Post>
) : RecyclerView.Adapter<NotificationListAdapter.NotificationListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotificationListItemBinding.inflate(inflater, parent, false)
        return NotificationListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        val notification = notificationList[position]
        val user = userList[notification.publisherId]
        var post: Post? = null
        if (notification.isPost) {
            post = postList.find { it.postId == notification.postId }
        }
        user?.let {
            holder.bind(
                it, notification, notificationListener, post
            )
        }

    }

    class NotificationListViewHolder(private val notificationBinding: NotificationListItemBinding) :
        RecyclerView.ViewHolder(notificationBinding.root) {

        fun bind(
            user: UserDetailsModel,
            notification: Notification,
            notificationListener: NotificationListHandler,
            post: Post?
        ) {

            notificationBinding.apply {
                this.user = user
                this.notification = notification
                this.listener = notificationListener
                if (post != null) {
                    this.post = post
                }
                executePendingBindings()
            }
        }
    }

    /**
     * interface to handle notification actions
     */
    interface NotificationListHandler {
        //notification clicked listener
        fun onNotificationClicked(notification: Notification)
    }
}