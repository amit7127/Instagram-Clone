package com.android.amit.instaclone.view.story

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.FragmentAddStoryBinding
import com.android.amit.instaclone.util.Status
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_add_story.*

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Add Story fragment
 */
class AddStoryFragment : Fragment() {

    lateinit var binding: FragmentAddStoryBinding
    lateinit var viewModel: AddStoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_story, container, false)
        viewModel = ViewModelProvider(this).get(AddStoryViewModel::class.java)

        init()

        return binding.root
    }

    /**
     * Initialize the view
     */
    private fun init() {
        context?.let { CropImage.activity().start(it, this) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            processCroppedImage(data)
        } else {
            onFragmentClosed()
        }
    }

    /**
     * upload image task
     */
    private fun processCroppedImage(data: Intent) {
        val result = CropImage.getActivityResult(data)
        viewModel.postStory(result.uri).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.statusLoading -> {
                    add_story_progressbar.visibility = View.VISIBLE
                }
                Status.statusSuccess -> {
                    Toast.makeText(
                        context,
                        "You story uploaded successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    add_story_progressbar.visibility = View.GONE
                    onFragmentClosed()
                }

                else -> {
                    add_story_progressbar.visibility = View.GONE

                    Toast.makeText(
                        context,
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    /**
     * Close fragment after operation complete
     */
    private fun onFragmentClosed() {
        view?.findNavController()?.popBackStack()
    }
}