package com.android.amit.instaclone.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.Notification
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.databinding.FragmentProfileBinding
import com.android.amit.instaclone.util.Constants
import com.android.amit.instaclone.util.CustomTab
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.profile.presenter.UploadedPostImagesAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : User Profile fragment
 */
class ProfileFragment : Fragment(), UploadedPostImagesAdapter.PostImageHandler,
    CustomTab.CustomTabListener {

    private lateinit var profileBinding: FragmentProfileBinding
    lateinit var viewModel: ProfileFragmentViewModel
    private var postsList: ArrayList<Post> = ArrayList()
    private var savedPostsList: ArrayList<Post> = ArrayList()
    lateinit var adapter: UploadedPostImagesAdapter
    private lateinit var savedListAdapter: UploadedPostImagesAdapter
    var id: String? = null

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        profileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ViewModelProvider(this).get(ProfileFragmentViewModel::class.java)

        profileBinding.profile = this
        profileBinding.viewModel = viewModel
        profileBinding.tabListener = this
        init()
        return profileBinding.root
    }

    /**
     * initialize user details in corresponding fields
     */
    @ExperimentalStdlibApi
    private fun init() {
        if (arguments?.getString(Constants.USER_ID_TAG) != null) {
            id = arguments?.getString(Constants.USER_ID_TAG)!!
        }
        viewModel.getUserData(id)?.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.statusLoading -> {
                    profile_fragment_progress_bar.visibility = View.VISIBLE
                }
                Status.statusSuccess -> {
                    viewModel.setUserDetails(it.data)
                    profileBinding.invalidateAll()
                    profile_fragment_progress_bar.visibility = View.GONE
                }
                else -> {
                    profile_fragment_progress_bar.visibility = View.GONE
                    Snackbar.make(profileBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        })
        getUsersPostList()
        getSavedPostList()
    }

    /**
     * get posts for the current user
     */
    private fun getUsersPostList() {
        val recyclerView: RecyclerView =
            profileBinding.userPostImagesRv // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            GridLayoutManager(context, 3) // you can use getContext() instead of "this"

        recyclerView.layoutManager = layoutManager
        adapter = UploadedPostImagesAdapter(postsList, this)
        recyclerView.adapter = adapter

        viewModel.getPostsImages().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.statusSuccess -> {
                    postsList.clear()
                    if (it.data != null) {
                        postsList.addAll(it.data!!)
                        postsList.reverse()
                        viewModel.setPostsCount(postsList.size)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    /**
     * get saved posts list for the current user
     */
    private fun getSavedPostList() {
        val recyclerView: RecyclerView =
            profileBinding.savedPostImagesRv // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            GridLayoutManager(context, 3) // you can use getContext() instead of "this"

        recyclerView.layoutManager = layoutManager
        savedListAdapter = UploadedPostImagesAdapter(savedPostsList, this)
        recyclerView.adapter = savedListAdapter

        val mSavedList = HashMap<String, Boolean>()

        viewModel.getSavedList().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.statusSuccess -> {
                    mSavedList.clear()
                    if (it.data != null) {
                        mSavedList.putAll(it.data!!)

                        if (mSavedList.size > 0) {
                            viewModel.getSavedPostsImages(ArrayList(mSavedList.keys.toList()))
                                .observe(viewLifecycleOwner, { postList ->
                                    when (postList.status) {
                                        Status.statusSuccess -> {
                                            savedPostsList.clear()
                                            if (postList.data != null) {
                                                savedPostsList.addAll(postList.data!!)
                                                savedPostsList.reverse()
                                            }
                                            savedListAdapter.notifyDataSetChanged()
                                        }
                                    }
                                })
                        }
                    }
                }
            }
        })
    }

    /**
     *on edit profile button clicked
     */
    fun onEditProfileClicked(isEditProfile: Boolean, userId: String, buttonView: View) {
        if (isEditProfile)
            //Own profile
            view?.findNavController()
                ?.navigate(R.id.action_profileFragment_to_accountSettingsFragment)
        else {
            //Viewer profile
            onFollowButtonClicked(userId, buttonView)
        }
    }

    /**
     * Follow button clicked, change follow status in db
     */
    private fun onFollowButtonClicked(userId: String, view: View) {
        if (view is Button) {
            val status = view.text
            viewModel.setFollowStatus(userId, status.toString()).observe(this, {
                when (it.status) {
                    Status.statusSuccess -> {
                        Snackbar.make(
                            profileBinding.root,
                            it.message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                        if (status == Status.Follow) {
                            sendNotification(userId)
                        }
                    }
                    else -> {
                        Snackbar.make(
                            profileBinding.root,
                            it.message.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    override fun onPostClicked(postId: String) {
        val bundle = Bundle()
        bundle.putString(Constants.POST_ID_TAG, postId)
        view?.findNavController()
            ?.navigate(R.id.action_profileFragment_to_postDetailsFragment, bundle)
    }

    override fun onTabChanged(id: Int) {
        if (id == Constants.postsTab) {
            user_post_images_rv.visibility = View.VISIBLE
            saved_post_images_rv.visibility = View.GONE
        } else if (id == Constants.savedTab) {
            user_post_images_rv.visibility = View.GONE
            saved_post_images_rv.visibility = View.VISIBLE
        }
    }

    /**
     * get followers status and list
     */
    fun onFollowersClicked() {
        val bundle = Bundle()
        bundle.putString(Constants.PURPOSE, Constants.FOLLOWERS_TAG)
        view?.findNavController()
            ?.navigate(R.id.action_profileFragment_to_usersListFragment, bundle)
    }

    /**
     * get following user list
     */
    fun onFollowingClicked() {
        val bundle = Bundle()
        bundle.putString(Constants.PURPOSE, Constants.FOLLOWING_TAG)
        bundle.putString(Constants.USER_ID_TAG, id)
        view?.findNavController()
            ?.navigate(R.id.action_profileFragment_to_usersListFragment, bundle)
    }

    /**
     * send notification to the target user
     */
    private fun sendNotification(targetId: String) {
        val notification = Notification()
        notification.isFollow = true
        notification.notificationText = getString(R.string.follow_notification_text)

        targetId.let { viewModel.addNotification(notification, it) }
    }
}
