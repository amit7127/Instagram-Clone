package com.android.amit.instaclone.view.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.CommentModel
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.FragmentCommentsBinding
import com.android.amit.instaclone.util.Constants
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.comments.presenter.CommnetsListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_comments.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommentsFragment : Fragment() {

    lateinit var commentFragmentBinding: FragmentCommentsBinding
    lateinit var viewModel: CommentsFragmentViewModel
    var postId: String? = null
    var postImageString: String? = null
    var commentsList = ArrayList<CommentModel>()
    var usersMap = HashMap<String, UserDetailsModel>()
    lateinit var adapter: CommnetsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        commentFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_comments, container, false)
        viewModel = ViewModelProvider(this).get(CommentsFragmentViewModel::class.java)

        commentFragmentBinding.viewModel = viewModel
        commentFragmentBinding.fragment = this

        initDataFetching()

        initCommentsList()

        return commentFragmentBinding.root
    }

    private fun initCommentsList() {
        val recyclerView: RecyclerView =
            commentFragmentBinding.commentsRecyclerView // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            LinearLayoutManager(context) // you can use getContext() instead of "this"
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        adapter = CommnetsListAdapter(commentsList, usersMap)
        recyclerView.adapter = adapter

        viewModel.getComments(postId!!).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusLoading -> {
                    load_comment_loader.visibility = View.VISIBLE
                }
                Status.statusSuccess -> {
                    if (it.data != null) {
                        commentsList.clear()
                        commentsList.addAll(it.data!!)
                        adapter.notifyDataSetChanged()
                        viewModel.getUsersMap(commentsList)
                            .observe(viewLifecycleOwner, Observer { result ->
                                when (result.status) {
                                    Status.statusSuccess -> {
                                        if (result.data != null) {
                                            usersMap.putAll(result.data!!)
                                            adapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                            })
                    }

                    load_comment_loader.visibility = View.GONE
                }

                else -> {
                    load_comment_loader.visibility = View.GONE
                    it.message?.let { it1 ->
                        Snackbar.make(commentFragmentBinding.root, it1, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

    private fun initDataFetching() {
        if (arguments?.getString(Constants.POST_IMAGE_URL_ID_TAG) != null) {
            postImageString = arguments?.getString(Constants.POST_IMAGE_URL_ID_TAG)!!

            viewModel.setPostData(postImageString!!)
        }
        if (arguments?.getString(Constants.POST_ID_TAG) != null) {
            postId = arguments?.getString(Constants.POST_ID_TAG)!!
        }

        viewModel.getUserData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    viewModel.setUserData(it.data, commentFragmentBinding.root)
                    commentFragmentBinding.invalidateAll()
                }
            }
        })
    }

    fun onPostCommentClicked() {
        if (postId != null) {
            viewModel.postComment(postId!!).observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.statusLoading -> {
                        publish_comment_progress_bar.visibility = View.VISIBLE
                        publish_button_comment_fragment.visibility = View.GONE
                    }
                    Status.statusSuccess -> {
                        publish_comment_progress_bar.visibility = View.GONE
                        publish_button_comment_fragment.visibility = View.VISIBLE
                        viewModel.clearComment()
                        commentFragmentBinding.invalidateAll()
                    }

                    else -> {
                        publish_comment_progress_bar.visibility = View.GONE
                        it.message?.let { it1 ->
                            Snackbar.make(commentFragmentBinding.root, it1, Snackbar.LENGTH_SHORT)
                                .show()
                        }
                        publish_button_comment_fragment.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

}