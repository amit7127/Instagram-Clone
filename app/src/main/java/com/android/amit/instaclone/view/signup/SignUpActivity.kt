package com.android.amit.instaclone.view.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.amit.instaclone.MainActivity
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.ActivitySignUpBinding
import com.android.amit.instaclone.util.Status
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    lateinit var signUpBinding: ActivitySignUpBinding
    lateinit var viewModel: SignUpViewModel

    var isLoading: Int = View.GONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        signUpBinding.signUp = this

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        signUpBinding.viewModel = viewModel

    }

    /**
     * Signup button clicked event
     */
    fun onSignUpClicked() {
        //onBackPressed()
        viewModel.createUser(this, signUpBinding.root).observe(this, Observer {
            when (it.status) {
                Status.statusLoading -> {
                    //isLoading = View.VISIBLE
                    progressbar_signup.visibility = View.VISIBLE
                }

                Status.statusSuccess -> {
                    Snackbar.make(signUpBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
//                    progressbar_signup.visibility = View.GONE
                    viewModel.saveUserData().observe(this, Observer {
                        when (it.status) {
                            Status.statusSuccess -> {
                                Snackbar.make(
                                    signUpBinding.root,
                                    it.message.toString(),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                progressbar_signup.visibility = View.GONE

                                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                            else -> {
                                Snackbar.make(
                                    signUpBinding.root,
                                    it.message.toString(),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                progressbar_signup.visibility = View.GONE
                            }
                        }
                    })
                }

                else -> {
                    Snackbar.make(signUpBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                    progressbar_signup.visibility = View.GONE
                }
            }
        })
    }

    fun onSignInClicked(){
        onBackPressed()
    }
}
