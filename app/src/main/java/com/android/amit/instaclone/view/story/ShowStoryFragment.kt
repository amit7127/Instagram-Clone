package com.android.amit.instaclone.view.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.StoryModel
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.FragmentShowStoryBinding
import com.android.amit.instaclone.repo.Repository
import com.android.amit.instaclone.util.Constants
import com.android.amit.instaclone.util.Status
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.fragment_show_story.*

class ShowStoryFragment : Fragment(), StoriesProgressView.StoriesListener {

    lateinit var storyBinding: FragmentShowStoryBinding
    lateinit var viewModel: ShowStoryViewModel
    var mStoryCount = 0
    var mUserId: String = ""
    var mStoryList = ArrayList<StoryModel>()
    var currentStory = StoryModel()
    var mStoryUser = UserDetailsModel()
    var isDataChanged = true
    var isOwnStory = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storyBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_show_story, container, false)
        viewModel = ViewModelProvider(this).get(ShowStoryViewModel::class.java)

        storyBinding.activity = this
        storyBinding.user = mStoryUser

        initView()

        return storyBinding.root
    }

    //Initialize
    private fun initView() {
        if (arguments?.getString(Constants.USER_ID_TAG) != null) {
            mUserId = arguments?.getString(Constants.USER_ID_TAG)!!
            getStories(mUserId)
            getUserDetails(mUserId)

            isOwnStory = mUserId == Repository().getCurrentUserId()
        }
    }

    // Get stories for a specific user
    private fun getStories(userId: String) {
        isDataChanged = true
        viewModel.fetchStoryDetails(userId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    story_loader.visibility = View.GONE
                    mStoryList.clear()
                    if (it.data != null) {
                        mStoryList.addAll(it.data!!)
                    }
                    currentStory = mStoryList[mStoryCount]
                    storyBinding.invalidateAll()

                    if (isDataChanged) {
                        setStoryView()
                        isDataChanged = false
                    }
                }

                Status.statusLoading -> {
                    story_loader.visibility = View.VISIBLE
                }

                else -> {
                    story_loader.visibility = View.GONE
                }
            }
        })
    }

    //Get user details from user id
    private fun getUserDetails(userId: String) {
        viewModel.getUserData(userId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    mStoryUser.image = it.data!!.image
                    mStoryUser.fullName = it.data!!.fullName
                    storyBinding.invalidateAll()
                }
            }
        })
    }

    //Set dtory view with progree dialog
    private fun setStoryView() {
        story_progress_view.setStoriesCount(mStoryList.size)
        story_progress_view.setStoryDuration(10000L)
        story_progress_view.setStoriesListener(this)
        story_progress_view.startStories()

        viewModel.setStorySeen(currentStory.storyId)
    }

    override fun onComplete() {
        view?.findNavController()?.popBackStack()
    }

    override fun onPrev() {
        currentStory = mStoryList[--mStoryCount]
        setSeen(currentStory.storyId)
        storyBinding.invalidateAll()
    }

    override fun onNext() {
        currentStory = mStoryList[++mStoryCount]
        setSeen(currentStory.storyId)
        storyBinding.invalidateAll()
    }

    //Next button clicked
    fun onNextClicked() {
        story_progress_view.skip()
    }

    //Previous button clicked
    fun onPreviousClicked() {
        story_progress_view.reverse()
    }

    //set story as seen by current user
    fun setSeen(storyId: String) {
        viewModel.setStorySeen(storyId)
    }

    //Delete the story by story id
    fun deleteStory(storyId: String) {
        viewModel.deleteStory(storyId)
        story_progress_view.skip()
    }

    //show seen user list
    fun viewerList(storyId: String) {
        var bundle = Bundle()
        bundle.putString(Constants.STORY_ID_TAG, storyId)
        bundle.putString(Constants.PURPOSE, Constants.VIEW_COLUMN)
        view?.findNavController()
            ?.navigate(R.id.action_showStoryFragment_to_usersListFragment, bundle)
    }

    override fun onPause() {
        super.onPause()
        story_progress_view.pause()
    }

    override fun onResume() {
        super.onResume()
        story_progress_view.resume()
    }
}