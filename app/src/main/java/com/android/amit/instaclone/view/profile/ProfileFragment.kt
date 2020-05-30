package com.android.amit.instaclone.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.amit.instaclone.MainActivity
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.FragmentProfileBinding

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    lateinit var profileBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        profileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        profileBinding.profile = this

        return profileBinding.root;
    }

    fun onEditProfileClicked() {
        view?.findNavController()?.navigate(R.id.action_profileFragment_to_acountSettingsActivity)
    }

}
