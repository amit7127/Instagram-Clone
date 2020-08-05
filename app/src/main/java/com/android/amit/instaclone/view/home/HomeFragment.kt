package com.android.amit.instaclone.view.home

import android.os.Bundle
import android.util.Log
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
import com.android.amit.instaclone.data.*
import com.android.amit.instaclone.databinding.FragmentHomeBinding
import com.android.amit.instaclone.util.Constants
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.home.presenter.PostsListAdapter
import com.android.amit.instaclone.view.home.presenter.StoryListAdapter
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), PostsListAdapter.PostListener, StoryListAdapter.StoryListHandler {

    lateinit var homeBinding: FragmentHomeBinding
    lateinit var viewModel: HomeViewModel
    lateinit var adapter: PostsListAdapter
    lateinit var storyAdapter: StoryListAdapter
    var listOfPosts = arrayListOf<PostListItem>()
    var likesList = HashMap<String, LikeModel>()
    var mCommentsList = HashMap<String, Int>()
    var mSavedList = HashMap<String, Boolean>()
    var mStoryList = ArrayList<StoryModel>()
    var mUserMap = HashMap<String, UserDetailsModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView =
            homeBinding.homeRecyclerView // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            LinearLayoutManager(context) // you can use getContext() instead of "this"

        recyclerView.layoutManager = layoutManager
        adapter = PostsListAdapter(listOfPosts, this, likesList, mCommentsList, mSavedList)
        recyclerView.adapter = adapter

        init()

        initStoryList()
    }

    /**
     * initialize story list
     */
    private fun initStoryList() {
        val storyRecyclerView: RecyclerView =
            homeBinding.recyclerViewStory // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            ) // you can use getContext() instead of "this"

        storyRecyclerView.layoutManager = layoutManager

        storyRecyclerView.layoutManager = layoutManager
        storyAdapter = StoryListAdapter(this, mStoryList, mUserMap)
        storyRecyclerView.adapter = storyAdapter

        viewModel.getFollowingUsersList().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    viewModel.getStories(it.data!!).observe(viewLifecycleOwner, Observer { result ->
                        when (result.status) {
                            Status.statusSuccess -> {
                                mStoryList.clear()
                                if (result.data != null) {
                                    mStoryList.addAll(result.data!!)
                                }

                                getUserMapForStory(mStoryList)

                                storyAdapter.notifyDataSetChanged()
                                homeBinding.invalidateAll()
                            }
                        }
                    })
                }
            }
        })

    }

    /**
     * get user map from Story List
     */
    private fun getUserMapForStory(mStoryList: ArrayList<StoryModel>) {
        viewModel.getUsersMap(mStoryList).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    mUserMap.clear()
                    mUserMap.putAll(it.data!!)
                    storyAdapter.notifyDataSetChanged()
                    homeBinding.invalidateAll()
                }
            }
        })
    }

    //Initialize posts
    private fun init() {
        viewModel.getPosts().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusLoading -> {
                    Log.i("Data", "Loading")
                    home_fragment_progressbar.visibility = View.VISIBLE
                }

                Status.statusSuccess -> {
                    Log.i("Data", "Success")
                    if (it.data != null) {
                        listOfPosts.clear()
                        listOfPosts.addAll(it.data!!)

                        getLikes(it.data!!)
                        getCommentsCount(it.data!!)
                        getSavedList()
                    }
                    adapter.notifyDataSetChanged()
                    home_fragment_progressbar.visibility = View.GONE
                }

                else -> {
                    Log.i("Data", "Error")
                    home_fragment_progressbar.visibility = View.GONE
                }
            }
        })
    }

    /**
     * get Likes list from post list
     */
    fun getLikes(postLists: ArrayList<PostListItem>) {
        viewModel.getLikesList(postLists).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    if (it.data != null) {
                        likesList.putAll(it.data!!)
                        adapter.notifyDataSetChanged()
                        homeBinding.invalidateAll()
                    }
                }
            }
        })
    }

    /**
     * Get comments count
     */
    fun getCommentsCount(postLists: ArrayList<PostListItem>) {
        viewModel.getCommentsCount(postLists).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    if (it.data != null) {
                        mCommentsList.putAll(it.data!!)
                        adapter.notifyDataSetChanged()
                        homeBinding.invalidateAll()
                    }
                }
            }
        })
    }

    fun getSavedList() {
        viewModel.getSavedList().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    mSavedList.clear()
                    if (it.data != null) {
                        mSavedList.putAll(it.data!!)
                    }
                    adapter.notifyDataSetChanged()
                    homeBinding.invalidateAll()
                }
            }
        })
    }

    override fun onLikeButtonClicked(
        postId: String,
        oldStatusIsLike: Boolean,
        publisherId: String
    ) {
        viewModel.likeButtonClicked(postId, oldStatusIsLike)

        if (!oldStatusIsLike)
            sendNotification(postId, publisherId)
    }

    override fun onCommentButtonClicked(
        postId: String,
        postImageUrlString: String,
        publisherId: String
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.POST_IMAGE_URL_ID_TAG, postImageUrlString)
        bundle.putString(Constants.POST_ID_TAG, postId)
        bundle.putString(Constants.PUBLISHER_ID_TAG, publisherId)
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_commentsFragment, bundle)
    }

    override fun onSaveButtonClicked(postId: String, oldStatus: Boolean) {
        viewModel.savedClicked(postId, oldStatus)
    }

    override fun onLikeTextClicked(postId: String) {
        val bundle = Bundle()
        bundle.putString(Constants.POST_ID_TAG, postId)
        bundle.putString(Constants.PURPOSE, Constants.LIKES_TAG)
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_usersListFragment, bundle)
    }

    private fun sendNotification(postId: String, publisherId: String) {
        val notification = Notification()
        notification.isPost = true
        notification.postId = postId
        notification.notificationText = getString(R.string.like_notification_text)

        publisherId.let { viewModel.addNotification(notification, it) }
    }

    override fun onAddStoryClicked() {
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_addStoryFragment)
    }

    override fun onShowStoryClicked(userId: String) {
        val bundle = Bundle()
        bundle.putString(Constants.USER_ID_TAG, userId)

        view?.findNavController()?.navigate(R.id.action_homeFragment_to_showStoryFragment, bundle)
    }
}
