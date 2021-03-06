package com.android.amit.instaclone.view.home.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.StoryModel
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.AddStoryListItemBinding
import com.android.amit.instaclone.databinding.StoryListItemBinding
import com.android.amit.instaclone.repo.Repository

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Story list adapter
 */
class StoryListAdapter(
    private val listener: StoryListHandler,
    private val storyList: ArrayList<StoryModel>,
    private val userMap: HashMap<String, UserDetailsModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val repo = Repository()

    /***
     * Enum class for recyclerview Cell type
     */
    enum class StoryType {
        ADD_STORY,
        STORY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //Check story type and according to that return view holder
        return when (viewType) {
            StoryType.ADD_STORY.ordinal -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = AddStoryListItemBinding.inflate(inflater, parent, false)
                AddStoryViewHolder(binding)
            }
            StoryType.STORY.ordinal -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = StoryListItemBinding.inflate(inflater, parent, false)
                StoryViewHolder(binding)
            }
            else -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = StoryListItemBinding.inflate(inflater, parent, false)
                StoryViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            StoryType.ADD_STORY.ordinal -> {
                val story = storyList[position]
                val user = userMap[story.userId]
                val addViewHolder = holder as AddStoryViewHolder
                addViewHolder.apply {
                    bind(listener, user)
                }
            }

            StoryType.STORY.ordinal -> {
                val story = storyList[position]
                val user = userMap[story.userId]
                val storyViewHolder = holder as StoryViewHolder
                storyViewHolder.apply {
                    bind(listener, story, user)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> StoryType.ADD_STORY.ordinal
            else -> StoryType.STORY.ordinal
        }
    }

    /**
     * Add new story holder class and properties
     */
    class AddStoryViewHolder(val binding: AddStoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: StoryListHandler, user: UserDetailsModel?) {
            binding.apply {
                this.listener = listener
                this.user = user
            }
        }
    }

    /**
     * Existing user story holder class and its properties
     */
    class StoryViewHolder(val binding: StoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: StoryListHandler, story: StoryModel, user: UserDetailsModel?) {
            binding.apply {
                this.listener = listener
                this.isSeen = story.seen.containsKey(Repository().getCurrentUserId())
                this.user = user
            }
        }
    }

    /**
     * Interface to handle all story list related events
     */
    interface StoryListHandler {
        //Click register for new story
        fun onAddStoryClicked()

        //Click registered for showing story
        fun onShowStoryClicked(userId: String)
    }
}