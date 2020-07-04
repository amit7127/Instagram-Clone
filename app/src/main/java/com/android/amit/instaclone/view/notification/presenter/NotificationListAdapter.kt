package com.android.amit.instaclone.view.notification.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.NotificationListItemBinding

class NotificationListAdapter(
    private val notificationList: ArrayList<Notification>,
    private val userList: HashMap<String, UserDetailsModel>,
    private val notificationListener: NotificationListHandler
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
        var notification = notificationList[position]
        var user = userList[notification.publisherId]
        user?.let {
            holder.bind(
                it, notification, notificationListener
            )
        }

    }

    class NotificationListViewHolder(private val notificationBinding: NotificationListItemBinding) :
        RecyclerView.ViewHolder(notificationBinding.root) {

        fun bind(user: UserDetailsModel, notification: Notification, notificationListener: NotificationListHandler) {

            notificationBinding.apply {
                this.user = user
                this.notification = notification
                this.listener = notificationListener
                executePendingBindings()
            }
        }
    }

    interface NotificationListHandler{
        fun onNotificationClicked(notification: Notification)
    }
}