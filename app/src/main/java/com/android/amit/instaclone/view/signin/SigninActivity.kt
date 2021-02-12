package com.android.amit.instaclone.view.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.ActivitySigninBinding
import com.android.amit.instaclone.util.Status
import com.android.amit.instaclone.view.main.MainActivity
import com.android.amit.instaclone.view.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {

    private lateinit var signInBinding: ActivitySigninBinding
    lateinit var viewModel: SignInViewModel

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signInBinding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
        signInBinding.signInActivity = this

        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        signInBinding.signInViewMdel = viewModel
    }

    /**
     * on sign-up button clicked
     */
    fun onSignUpClicked() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    /**
     * on sign-in button clicked
     */
    fun onSignInClicked() {
        viewModel.signInUser(signInBinding.root, this).observe(this, {
            when (it.status) {
                Status.statusLoading -> {
                    //isLoading = View.VISIBLE
                    progressbar_signin.visibility = View.VISIBLE
                }

                Status.statusSuccess -> {
                    Snackbar.make(signInBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                    progressbar_signin.visibility = View.GONE

                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                else -> {
                    Snackbar.make(signInBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                    progressbar_signin.visibility = View.GONE
                }
            }
        })
    }
}
