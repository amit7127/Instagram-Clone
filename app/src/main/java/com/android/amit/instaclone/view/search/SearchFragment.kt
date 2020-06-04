package com.android.amit.instaclone.view.search

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
import com.android.amit.instaclone.data.UserDetailsModel
import com.android.amit.instaclone.databinding.FragmentSearchBinding
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.search.presenter.UserSerchAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var viewModel: SearchFragmentViewModel
    var listOfUsers = arrayListOf<UserDetailsModel>()
    lateinit var adapter: UserSerchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        viewModel = ViewModelProvider(this).get(SearchFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.data = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView =
            binding.homeRecyclerView // In xml we have given id rv_movie_list to RecyclerView

        val layoutManager =
            LinearLayoutManager(context) // you can use getContext() instead of "this"

        recyclerView.layoutManager = layoutManager
        adapter = UserSerchAdapter(listOfUsers)
        recyclerView.adapter = adapter
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        viewModel.searchUserByQuerry(s.toString().toLowerCase(Locale.getDefault()))
            .observe(this, Observer {
                when (it.status) {
                    Status.statusLoading -> {
                        serach_fragment_progressbar.visibility = View.VISIBLE
                    }
                    Status.statusSuccess -> {
                        serach_fragment_progressbar.visibility = View.GONE
                        listOfUsers.clear()
                        if (it.data != null) {
                            listOfUsers.addAll(it.data!!)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    else -> {
                        serach_fragment_progressbar.visibility = View.GONE
                    }
                }
            })
    }
}
