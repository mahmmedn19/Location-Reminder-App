package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private val viewModel by viewModels<AuthenticationViewModel>()

    private lateinit var binding: ActivityAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)


        // btn to sign in


        observeAuthenticationState()


    }
    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(this, Observer {
            when (it) {
                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                    val intent = Intent(this, RemindersActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    binding.btnSignIn.setOnClickListener { launchSignInFlow() }
                }
            }
        })
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
               Log.d(TAG,"Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!")
            } else {
                // Sign in failed
                Log.d(TAG,"\"Sign in unsuccessful ${response?.error?.errorCode}\"")
            }
        }
    }


    private fun launchSignInFlow() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_RESULT_CODE
        )
    }

    companion object {
        const val TAG = "AuthenticationActivity"
        const val SIGN_IN_RESULT_CODE = 1001
    }
}
