package com.android.amit.instaclone.view.comments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.FragmentCommentsBinding
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.home.HomeViewModel
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

        return commentFragmentBinding.root
    }

    private fun initDataFetching() {
        if (arguments?.getString("postImageUrl") != null) {
            postImageString = arguments?.getString("postImageUrl")!!

            viewModel.setPostData(postImageString!!)
        }
        if (arguments?.getString("postId") != null) {
            postId = arguments?.getString("postId")!!
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