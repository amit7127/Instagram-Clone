package com.android.amit.instaclone.view.postDetails

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
import com.android.amit.instaclone.data.LikeModel
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.databinding.FragmentPostDetailsBinding
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.home.presenter.PostsListAdapter
import kotlinx.android.synthetic.main.fragment_post_details.*


/**
 * A simple [Fragment] subclass.
 * Use the [PostDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostDetailsFragment : Fragment(), PostsListAdapter.PostListener {

    private var postId: String = ""

    lateinit var postDetailsBinding: FragmentPostDetailsBinding
    lateinit var viewModel: PostDetailsViewModel
    lateinit var adapter: PostsListAdapter
    var postList = ArrayList<PostListItem>()
    var likesList = HashMap<String, LikeModel>()
    var mCommentsList = HashMap<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments?.getString("postId") != null) {
            postId = arguments?.getString("postId")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        postDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_details, container, false)
        viewModel = ViewModelProvider(this).get(PostDetailsViewModel::class.java)

        return postDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView =
            postDetailsBinding.postDetailsRecyclerview // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            LinearLayoutManager(context) // you can use getContext() instead of "this"

        recyclerView.layoutManager = layoutManager
        adapter = PostsListAdapter(postList, this, likesList, mCommentsList)
        recyclerView.adapter = adapter

        viewModel.getPostDetails(postId).observe(viewLifecycleOwner, Observer {
            when(it.status){
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
                    }
                    post_details_progressbar.visibility = View.GONE
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
                        postDetailsBinding.invalidateAll()
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
                        postDetailsBinding.invalidateAll()
                    }
                }
            }
        })
    }

    override fun onLikeButtonClicked(postId: String, oldStatusIsLike: Boolean) {
        viewModel.likeButtonClicked(postId, oldStatusIsLike)
    }

    override fun onCommentButtonClicked(postId: String, postImageUrlString: String) {
        var bundle = Bundle()
        bundle.putString("postImageUrl", postImageUrlString)
        bundle.putString("postId", postId)
        view?.findNavController()?.navigate(R.id.action_postDetailsFragment_to_commentsFragment, bundle)
    }
}