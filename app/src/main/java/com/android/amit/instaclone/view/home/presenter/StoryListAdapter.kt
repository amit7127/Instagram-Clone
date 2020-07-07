package com.android.amit.instaclone.view.home.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.data.StoryModel
import com.android.amit.instaclone.databinding.AddStoryListItemBinding
import com.android.amit.instaclone.databinding.StoryListItemBinding

class StoryListAdapter(
    private val listener: StoryListHandler,
    private val storyList: ArrayList<StoryModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /***
     * Enum class for recyclerview Cell type
     */
    enum class StoryType(viewType: Int) {
        ADD_STORY(0),
        STORY(1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
                val addViewHolder = holder as AddStoryViewHolder
                addViewHolder.apply {
                    bind(listener)
                }
            }

            StoryType.STORY.ordinal -> {
                val story = storyList[position]
                val storyViewHolder = holder as StoryViewHolder
                storyViewHolder.apply {
                    bind(story)
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

    class AddStoryViewHolder(val binding: AddStoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: StoryListHandler) {
            binding.apply {
                this.listener = listener
            }
        }
    }

    class StoryViewHolder(val binding: StoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryModel) {
            binding.apply {
                this.story = story
            }
        }
    }

    /**
     * Interface to handle all story list related events
     */
    interface StoryListHandler {
        fun onAddStoryClicked()
    }
}