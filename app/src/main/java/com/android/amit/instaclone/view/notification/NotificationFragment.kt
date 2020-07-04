package com.android.amit.instaclone.view.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.NotificationFragmentBinding
import com.android.amit.instaclone.util.Constants
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.notification.presenter.NotificationListAdapter

class NotificationFragment : Fragment(), NotificationListAdapter.NotificationListHandler {

    lateinit var notificationBinding: NotificationFragmentBinding
    private lateinit var viewModel: NotificationViewModel
    lateinit var adapter: NotificationListAdapter
    var notificationList = ArrayList<Notification>()
    var usersMap = HashMap<String, UserDetailsModel>()
    private var postList = ArrayList<Post>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationBinding =
            DataBindingUtil.inflate(inflater, R.layout.notification_fragment, container, false)
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        initNotification()
        return notificationBinding.root
    }

    private fun initNotification() {
        val recyclerView: RecyclerView =
            notificationBinding.notificationRecyclerView // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            LinearLayoutManager(context) // you can use getContext() instead of "this"
        recyclerView.layoutManager = layoutManager
        adapter = NotificationListAdapter(notificationList, usersMap, this, postList)
        recyclerView.adapter = adapter


        viewModel.getNotificationList().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    notificationList.clear()
                    if (it.data != null) {
                        notificationList.addAll(it.data!!)
                        var userList = it.data!!.map { it.publisherId }
                        getUserList(userList)
                        getPostList(it.data!!)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun getPostList(data: ArrayList<Notification>) {
        var postIdList = data.filter { it.isPost }.map { it.postId }
        viewModel.getPostList(postIdList as ArrayList<String>)
            .observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.statusSuccess -> {
                        postList.clear()
                        if (it.data != null && it.data!!.size > 0) {
                            this.postList.addAll(it.data!!)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            })
    }

    private fun getUserList(userList: List<String>) {
        viewModel.getUsersFromIdList(userList as ArrayList<String>)
            .observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.statusSuccess -> {
                        usersMap.clear()
                        if (it.data != null) {
                            usersMap.putAll(it.data!!)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }

    override fun onNotificationClicked(notification: Notification) {
        viewModel.markNotificationAsRead(notification)

        if (notification.isPost) {
            val bundle = Bundle()
            bundle.putString(Constants.POST_ID_TAG, notification.postId)
            view?.findNavController()
                ?.navigate(R.id.action_notificationFragment_to_postDetailsFragment, bundle)
        } else if (notification.isFollow) {
            val bundle = Bundle()
            bundle.putString(Constants.PURPOSE, Constants.FOLLOWERS_TAG)
            view?.findNavController()
                ?.navigate(R.id.action_notificationFragment_to_usersListFragment, bundle)
        }
    }

}
