package com.android.amit.instaclone.view.home

import android.os.Bundle
import android.util.Log
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
import com.android.amit.instaclone.data.LikeModel
import com.android.amit.instaclone.data.PostListItem
import com.android.amit.instaclone.databinding.FragmentHomeBinding
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.home.presenter.PostsListAdapter
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), PostsListAdapter.PostListener {

    lateinit var homeBinding: FragmentHomeBinding
    lateinit var viewModel: HomeViewModel
    lateinit var adapter: PostsListAdapter
    var listOfPosts = arrayListOf<PostListItem>()
    var likesList = HashMap<String, LikeModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView =
            homeBinding.homeRecyclerView // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            LinearLayoutManager(context) // you can use getContext() instead of "this"

        recyclerView.layoutManager = layoutManager
        adapter = PostsListAdapter(listOfPosts, this, likesList, viewModel)
        recyclerView.adapter = adapter

        init()
    }

    private fun init() {
        viewModel.getPosts().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusLoading -> {
                    Log.i("Data", "Loading")
                    home_fragment_progressbar.visibility = View.VISIBLE
                }

                Status.statusSuccess -> {
                    Log.i("Data", "Success")
                    if (it.data != null) {
                        listOfPosts.clear()
                        listOfPosts.addAll(it.data!!)

                        viewModel.getLikesList(it.data!!).observe(viewLifecycleOwner, Observer {
                            when (it.status) {
                                Status.statusSuccess -> {
                                    if (it.data != null) {
                                        likesList.putAll(it.data!!)
                                        adapter.notifyDataSetChanged()
                                        homeBinding.invalidateAll()
                                    }
                                }
                            }
                        })
                    }
                    adapter.notifyDataSetChanged()
                    home_fragment_progressbar.visibility = View.GONE
                }

                else -> {
                    Log.i("Data", "Error")
                    home_fragment_progressbar.visibility = View.GONE
                }
            }
        })

    }

    override fun onLikeButtonClicked(postId: String, oldStatusIsLike: Boolean) {
        viewModel.likeButtonClicked(postId, oldStatusIsLike)
    }
}
