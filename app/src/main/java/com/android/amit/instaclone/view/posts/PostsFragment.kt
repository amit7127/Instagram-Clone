package com.android.amit.instaclone.view.posts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.FragmentPostsBinding
import com.android.amit.instaclone.util.Status
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_posts.*

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Posts fragment
 */
class PostsFragment : Fragment() {

    private lateinit var postsBinding: FragmentPostsBinding
    lateinit var viewModel: PostFragmnetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        postsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_posts, container, false)
        viewModel = ViewModelProvider(this).get(PostFragmnetViewModel::class.java)

        postsBinding.postFragment = this
        postsBinding.viewModel = viewModel

        initGallery()

        return postsBinding.root
    }

    /**
     * initialize gallery for pick an image
     */
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

    /**
     * new post
     */
    fun onPostButtonClicked() {
        viewModel.post(postsBinding.root, requireContext()).observe(viewLifecycleOwner, {
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

    /**
     * handle custom backbutton
     */
    fun onFragmentClosed() {
        view?.findNavController()?.popBackStack()
    }
}
