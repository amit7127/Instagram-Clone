package com.android.amit.instaclone.view.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.amit.instaclone.MainActivity
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.ActivitySigninBinding
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {

    lateinit var signinBinding: ActivitySigninBinding
    lateinit var viewModel: SignInViewModel

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this@SigninActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signinBinding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
        signinBinding.signInActivity = this

        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        signinBinding.signInViewMdel = viewModel
    }

    fun onSignUpClicked() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    fun onSignInClicked() {
        viewModel.signInUser(this, signinBinding.root).observe(this, Observer {
            when (it.status) {
                Status.statusLoading -> {
                    //isLoading = View.VISIBLE
                    progressbar_signin.visibility = View.VISIBLE
                }

                Status.statusSuccess -> {
                    Snackbar.make(signinBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    progressbar_signin.visibility = View.GONE

                    val intent = Intent(this@SigninActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                else -> {
                    Snackbar.make(signinBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                    progressbar_signin.visibility = View.GONE
                }
            }
        })
    }
}
