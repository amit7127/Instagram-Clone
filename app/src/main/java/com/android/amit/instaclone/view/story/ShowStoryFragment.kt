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
import com.android.amit.instaclone.util.Constants
import com.android.amit.instaclone.util.Status
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.fragment_show_story.*

class ShowStoryFragment : Fragment(), StoriesProgressView.StoriesListener {

    lateinit var storyBinding: FragmentShowStoryBinding
    lateinit var viewModel: ShowStoryViewModel
    var storyCount = 0
    var userId: String = ""
    var storyList = ArrayList<StoryModel>()
    var currentStory = StoryModel()
    var mStoryUser = UserDetailsModel()
    var isDataChanged = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storyBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_show_story, container, false)
        viewModel = ViewModelProvider(this).get(ShowStoryViewModel::class.java)

        storyBinding.activity = this
        storyBinding.story = currentStory
        storyBinding.user = mStoryUser

        initView()

        return storyBinding.root
    }

    private fun initView() {
        if (arguments?.getString(Constants.USER_ID_TAG) != null) {
            userId = arguments?.getString(Constants.USER_ID_TAG)!!
            getStories(userId)
            getUserDetails(userId)
        }
    }

    private fun getStories(userId: String) {
        isDataChanged = true
        viewModel.fetchStoryDetails(userId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    story_loader.visibility = View.GONE
                    storyList.clear()
                    if (it.data != null) {
                        storyList.addAll(it.data!!)
                    }
                    if (isDataChanged) {
                        setStoryView()
                        isDataChanged = false
                    }
                    currentStory = storyList[storyCount]
                    storyBinding.invalidateAll()
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

    private fun setStoryView() {
        story_progress_view.setStoriesCount(storyList.size)
        story_progress_view.setStoryDuration(10000L)
        story_progress_view.setStoriesListener(this)
        story_progress_view.startStories()
    }

    override fun onComplete() {
        view?.findNavController()?.popBackStack()
    }

    override fun onPrev() {
        currentStory = storyList[--storyCount]
        setSeen(currentStory.storyId)
        storyBinding.invalidateAll()
    }

    override fun onNext() {
        currentStory = storyList[++storyCount]
        setSeen(currentStory.storyId)
        storyBinding.invalidateAll()
    }

    fun onNextClicked() {
        story_progress_view.skip()
    }

    fun onPreviousClicked() {
        story_progress_view.reverse()
    }

    fun setSeen(storyId: String) {
        viewModel.setStorySeen(storyId)
    }
}