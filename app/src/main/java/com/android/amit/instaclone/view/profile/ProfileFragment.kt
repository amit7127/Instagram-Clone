package com.android.amit.instaclone.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.amit.instaclone.R
import com.android.amit.instaclone.data.Post
import com.android.amit.instaclone.databinding.FragmentProfileBinding
import com.android.amit.instaclone.util.CustomTab
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.profile.presenter.UploadedPostImagesAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), UploadedPostImagesAdapter.PostImageHandler,
    CustomTab.CustomTabListener {

    lateinit var profileBinding: FragmentProfileBinding
    lateinit var viewModel: ProfileFragmentViewModel
    var postsList: ArrayList<Post> = ArrayList()
    lateinit var adapter: UploadedPostImagesAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        profileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ViewModelProvider(this).get(ProfileFragmentViewModel::class.java)

        profileBinding.profile = this
        profileBinding.viewModel = viewModel
        profileBinding.tabListener = this
        init()
        return profileBinding.root;
    }

    @ExperimentalStdlibApi
    private fun init() {
        var id: String? = null
        if (arguments?.getString("userId") != null) {
            id = arguments?.getString("userId")!!
        }
        viewModel.getUserData(id)?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusLoading -> {
                    profile_fragment_progress_bar.visibility = View.VISIBLE
                }
                Status.statusSuccess -> {
                    viewModel.setUserDetails(it.data)
                    profileBinding.invalidateAll()
                    profile_fragment_progress_bar.visibility = View.GONE
                }
                else -> {
                    profile_fragment_progress_bar.visibility = View.GONE
                    Snackbar.make(profileBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        })

        getUsersPostList()
    }

    fun getUsersPostList() {
        val recyclerView: RecyclerView =
            profileBinding.userPostImagesRv // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            GridLayoutManager(context, 3) // you can use getContext() instead of "this"

        recyclerView.layoutManager = layoutManager
        adapter = UploadedPostImagesAdapter(postsList, this)
        recyclerView.adapter = adapter

        viewModel.getPostsImages().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusSuccess -> {
                    postsList.clear()
                    if (it.data != null) {
                        postsList.addAll(it.data!!)
                        postsList.reverse()
                        viewModel.setPostsCount(postsList.size)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    fun onEditProfileClicked(isEditProfile: Boolean, userId: String, buttonView: View) {
        if (isEditProfile)
            view?.findNavController()
                ?.navigate(R.id.action_profileFragment_to_accountSettingsFragment)
        else {
            onFollowButtonClicked(userId, buttonView)
        }
    }

    fun onFollowButtonClicked(userId: String, view: View) {
        if (view is Button) {
            val status = view.text
            viewModel.setFollowStatus(userId, status.toString()).observe(this, Observer {
                when (it.status) {
                    Status.statusSuccess -> {
                        Snackbar.make(
                            profileBinding.root,
                            it.message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                    else -> {
                        Snackbar.make(
                            profileBinding.root,
                            it.message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            })
        }
    }

    override fun onPostClicked(postId: String) {
        var bundle = Bundle()
        bundle.putString("postId", postId)
        view?.findNavController()
            ?.navigate(R.id.action_profileFragment_to_postDetailsFragment, bundle)
    }

    override fun onTabChanged(id: Int) {
        Toast.makeText(context, id.toString(), Toast.LENGTH_LONG).show()
    }
}
