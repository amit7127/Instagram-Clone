package com.android.amit.instaclone.view.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.FragmentUsersListBinding
import com.android.amit.instaclone.util.Constants
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.search.presenter.UserSerchAdapter

class UsersListFragment : Fragment(), UserSerchAdapter.UserSearchListener {

    lateinit var purpose: String
    lateinit var userListBinding: FragmentUsersListBinding
    lateinit var viewModel: UsersListViewModel
    lateinit var adapter: UserSerchAdapter
    var listOfUsers = arrayListOf<UserDetailsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments?.getString(Constants.PURPOSE) != null) {
            this.purpose = arguments?.getString(Constants.PURPOSE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_users_list, container, false)
        viewModel = ViewModelProvider(this).get(UsersListViewModel::class.java)

        init()

        return userListBinding.root
    }

    //Init views
    private fun init() {

        val recyclerView: RecyclerView =
            userListBinding.userListRecyclerView // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            LinearLayoutManager(context) // you can use getContext() instead of "this"

        recyclerView.layoutManager = layoutManager
        adapter = UserSerchAdapter(listOfUsers, this)
        recyclerView.adapter = adapter

        userListBinding.invalidateAll()

        //Find out the purpose for the users list
        when (purpose) {
            Constants.LIKES_TAG -> {
                setLikesData()
            }

            Constants.FOLLOWERS_TAG -> {
                setFollowersData()
            }

            Constants.FOLLOWING_TAG -> {
                setFollowingData()
            }

            Constants.VIEW_COLUMN -> {
                setStoryViews()
            }
        }
    }

    //get Story view
    private fun setStoryViews() {
        if (arguments?.getString(Constants.STORY_ID_TAG) != null) {
            val storyId = arguments?.getString(Constants.STORY_ID_TAG)
            storyId?.let { it ->
                viewModel.getStory(it).observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Status.statusSuccess -> {
                            setUserList(ArrayList(it.data!!.seen.keys))
                        }
                    }
                })
            }

        }
    }

    //get following user ids
    private fun setFollowingData() {
        var userId: String? = null
        if (arguments?.getString(Constants.USER_ID_TAG) != null)
            userId = arguments?.getString(Constants.USER_ID_TAG)
        viewModel.getFollowingIdList(userId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    it.data?.let { it1 ->
                        setUserList(it1)
                    }
                }
            }
        })
    }

    //get follower users ids
    private fun setFollowersData() {
        var userId: String? = null
        if (arguments?.getString(Constants.USER_ID_TAG) != null)
            userId = arguments?.getString(Constants.USER_ID_TAG)

        viewModel.getFollowerIdList(userId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    it.data?.let { it1 ->
                        setUserList(it1)
                    }
                }
            }
        })
    }

    //get likes user ids
    private fun setLikesData() {
        if (arguments?.getString(Constants.POST_ID_TAG) != null) {
            val postId = arguments?.getString(Constants.POST_ID_TAG)!!

            viewModel.getUsersIdList(postId).observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.statusSuccess -> {
                        it.data?.let { it1 ->
                            setUserList(it1)
                        }
                    }
                }
            })

        }
    }

    //set users list from ids
    private fun setUserList(userIdList: ArrayList<String>) {
        viewModel.getUsersFromIdList(userIdList).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    listOfUsers.clear()
                    if (it.data != null) {
                        listOfUsers.addAll(it.data!!.values)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onFollowButtonClicked(userId: String, view: View) {

    }

    override fun onProfileClicked(userId: String) {
        var bundle = bundleOf(Constants.USER_ID_TAG to userId)
        view?.findNavController()
            ?.navigate(R.id.action_usersListFragment_to_profileFragment, bundle)
    }
}