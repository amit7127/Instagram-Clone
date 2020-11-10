package com.android.amit.instaclone.view.acountsettings

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
import com.android.amit.instaclone.databinding.FragmentAccountSettingsBinding
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.signin.SigninActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_account_settings.*

class AccountSettingsFragment : Fragment() {

    private lateinit var accountSettingBinding: FragmentAccountSettingsBinding
    lateinit var viewModel: AccountSettingsViewModel

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        accountSettingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_account_settings, container, false)
        viewModel = ViewModelProvider(this).get(AccountSettingsViewModel::class.java)

        accountSettingBinding.accountSettings = this
        accountSettingBinding.viewModel = viewModel

        //initialize the data
        initData()

        //Return view
        return accountSettingBinding.root
    }

    /**
     * Call API for user details
     */
    @ExperimentalStdlibApi
    private fun initData() {
        viewModel.getUserDetails().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusLoading -> {
                    account_settings_progress_bar.visibility = View.VISIBLE
                }

                Status.statusSuccess -> {
                    viewModel.setUserInfo(it.data)
                    accountSettingBinding.invalidateAll()
                    account_settings_progress_bar.visibility = View.GONE
                }

                else -> {
                    account_settings_progress_bar.visibility = View.GONE
                    Snackbar.make(
                        accountSettingBinding.root,
                        it.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    /**
     * Logout button clicked
     */
    fun onLogoutClicked() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, SigninActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    /**
     * Save profile button clicked
     */
    fun onSaveProfileClicked() {
        viewModel.saveUserData(accountSettingBinding.root).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.statusLoading -> {
                    account_settings_progress_bar.visibility = View.VISIBLE
                }
                Status.statusSuccess -> {
                    Snackbar.make(
                        accountSettingBinding.root,
                        it.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    account_settings_progress_bar.visibility = View.GONE
                    onFragmentClosed()
                }
                else -> {
                    account_settings_progress_bar.visibility = View.GONE
                    Snackbar.make(
                        accountSettingBinding.root,
                        it.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    /**
     * close button clicked
     */
    fun onFragmentClosed() {
        view?.findNavController()?.popBackStack()
    }

    /**
     * change user profile pic
     */
    fun onChangeImageClicked() {
        context?.let { CropImage.activity().setAspectRatio(1, 1).start(it, this) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //get data from image picker activity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            viewModel.setImage(result.uri)
            accountSettingBinding.invalidateAll()
        }
    }
}
