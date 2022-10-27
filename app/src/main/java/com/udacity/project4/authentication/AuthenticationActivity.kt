package com.udacity.project4.authentication
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private val TAG = "AuthenticationActivity"
    private val viewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        observeAuthenticationState()
        binding.loginBtn.setOnClickListener { launchSignInFlow() }
    }


    private fun observeAuthenticationState() {

        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {

                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                    navigateToRemindersActivity()
                }
                else -> {
                    binding.loginBtn.setOnClickListener {
                        launchSignInFlow()
                    }
                }
            }
        })
    }

    private fun navigateToRemindersActivity() {
        startActivity(Intent(this, RemindersActivity::class.java))
        finish()
    }

    private fun launchSignInFlow() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        loginActivityResultLauncher.launch(intent)

    }

    private var loginActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, ": login successfully")

        } else {
            Log.d(TAG, ": failed ${IdpResponse.fromResultIntent(result.data)?.error?.message}")
        }
    }

}



