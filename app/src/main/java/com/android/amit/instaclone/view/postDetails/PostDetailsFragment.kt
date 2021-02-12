package com.android.amit.instaclone.view.postDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.LikeModel
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.databinding.FragmentPostDetailsBinding
import com.android.amit.instaclone.util.Constants
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.home.presenter.PostsListAdapter
import kotlinx.android.synthetic.main.fragment_post_details.*

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Post details class
 */
class PostDetailsFragment : Fragment(), PostsListAdapter.PostListener {

    private var postId: String = ""

    private lateinit var postDetailsBinding: FragmentPostDetailsBinding
    lateinit var viewModel: PostDetailsViewModel
    lateinit var adapter: PostsListAdapter
    private var postList = ArrayList<PostListItem>()
    private var likesList = HashMap<String, LikeModel>()
    private var mCommentsList = HashMap<String, Int>()
    private var mSavedList = HashMap<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments?.getString(Constants.POST_ID_TAG) != null) {
            postId = arguments?.getString(Constants.POST_ID_TAG)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        postDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_details, container, false)
        viewModel = ViewModelProvider(this).get(PostDetailsViewModel::class.java)

        return postDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    /**
     * initialize the post data
     */
    private fun initViews() {
        val recyclerView: RecyclerView =
            postDetailsBinding.postDetailsRecyclerview

        val layoutManager =
            LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager
        adapter = PostsListAdapter(postList, this, likesList, mCommentsList, mSavedList)
        recyclerView.adapter = adapter

        viewModel.getPostDetails(postId).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.statusLoading -> {
                    post_details_progressbar.visibility = View.VISIBLE
                }

                Status.statusSuccess -> {
                    if (it.data != null) {
                        postList.clear()
                        postList.add(it.data!!)
                        adapter.notifyDataSetChanged()
                        getLikes(postList)
                        getCommentsCount(postList)
                        getSavedList()
                    }
                    post_details_progressbar.visibility = View.GONE
                }
            }
        })
    }

    /**
     * get Likes list from post list
     */
    private fun getLikes(postLists: ArrayList<PostListItem>) {
        viewModel.getLikesList(postLists).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.statusSuccess -> {
                    if (it.data != null) {
                        likesList.putAll(it.data!!)
                        adapter.notifyDataSetChanged()
                        postDetailsBinding.invalidateAll()
                    }
                }
            }
        })
    }

    /**
     * Get comments count
     */
    private fun getCommentsCount(postLists: ArrayList<PostListItem>) {
        viewModel.getCommentsCount(postLists).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.statusSuccess -> {
                    if (it.data != null) {
                        mCommentsList.putAll(it.data!!)
                        adapter.notifyDataSetChanged()
                        postDetailsBinding.invalidateAll()
                    }
                }
            }
        })
    }

    /**
     * get saved posts list
     */
    private fun getSavedList() {
        viewModel.getSavedList().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.statusSuccess -> {
                    mSavedList.clear()
                    if (it.data != null) {
                        mSavedList.putAll(it.data!!)
                    }
                    adapter.notifyDataSetChanged()
                    postDetailsBinding.invalidateAll()
                }
            }
        })
    }

    //On like button clicked
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
        view?.findNavController()
            ?.navigate(R.id.action_postDetailsFragment_to_commentsFragment, bundle)
    }

    override fun onSaveButtonClicked(postId: String, oldStatus: Boolean) {
        viewModel.savedClicked(postId, oldStatus)
    }

    override fun onLikeTextClicked(postId: String) {
        val bundle = Bundle()
        bundle.putString(Constants.POST_ID_TAG, postId)
        bundle.putString(Constants.PURPOSE, Constants.LIKES_TAG)
        view?.findNavController()
            ?.navigate(R.id.action_postDetailsFragment_to_usersListFragment, bundle)
    }

    override fun onProfileClicked(userId: String) {
        val bundle = Bundle()
        bundle.putString(Constants.USER_ID_TAG, userId)

        view?.findNavController()
            ?.navigate(R.id.action_postDetailsFragment_to_profileFragment, bundle)
    }

    //Send notification for new likes
    private fun sendNotification(postId: String, publisherId: String) {
        val notification = Notification()
        notification.isPost = true
        notification.postId = postId
        notification.notificationText = getString(R.string.like_notification_text)

        publisherId.let { viewModel.addNotification(notification, it) }
    }
}