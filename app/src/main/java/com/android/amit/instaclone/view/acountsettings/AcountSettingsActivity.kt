package com.android.amit.instaclone.view.acountsettings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.ActivityAcountSettingsBinding
import com.android.amit.instaclone.view.signin.SigninActivity
import com.google.firebase.auth.FirebaseAuth

class AcountSettingsActivity : AppCompatActivity() {

    lateinit var accountSettingsBinding: ActivityAcountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountSettingsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_acount_settings)
        accountSettingsBinding.accountSettings = this
    }

    fun onLogoutClicked() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@AcountSettingsActivity, SigninActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
