package com.android.amit.instaclone.view.posts

import android.app.Activity
import android.content.Intent
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
import com.android.amit.instaclone.databinding.FragmentPostsBinding
import com.android.amit.instaclone.util.Status
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_account_settings.*
import kotlinx.android.synthetic.main.fragment_posts.*

/**
 * A simple [Fragment] subclass.
 * Use the [PostsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostsFragment : Fragment() {

    lateinit var postsBinding: FragmentPostsBinding
    lateinit var viewModel: PostFragmnetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        postsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_posts, container, false)
        viewModel = ViewModelProvider(this).get(PostFragmnetViewModel::class.java)

        postsBinding.postFragment = this
        postsBinding.viewModel = viewModel

        initGallery()

        return postsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun initGallery() {
        context?.let { CropImage.activity().setAspectRatio(2, 1).start(it, this) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            viewModel.setPostPicture(result.uri)
            postsBinding.invalidateAll()
        } else {
            onFragmentClosed()
        }
    }

    fun onPostButtonClicked() {
        viewModel.post(postsBinding.root).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusLoading -> {
                    posts_progressbar.visibility = View.VISIBLE
                }
                Status.statusSuccess -> {
                    Snackbar.make(
                        postsBinding.root,
                        it.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    posts_progressbar.visibility = View.GONE
                    onFragmentClosed()
                }
                else -> {
                    posts_progressbar.visibility = View.GONE
                    Snackbar.make(
                        postsBinding.root,
                        it.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    fun onFragmentClosed() {
        view?.findNavController()?.popBackStack()
    }
}
