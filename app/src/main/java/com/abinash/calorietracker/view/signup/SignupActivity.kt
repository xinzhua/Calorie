package com.abinash.calorietracker.view.signup

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.abinash.calorietracker.R
import com.abinash.calorietracker.databinding.ActivitySignupBinding
import com.abinash.calorietracker.util.PrefManager
import com.abinash.calorietracker.view.user.UserHomeActivity
import com.abinash.calorietracker.view.login.LoginActivity
import java.util.*

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private lateinit var viewModel: SignUpVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(SignUpVM::class.java)

        observe()
        binding.icBack.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }
        binding.btnSignup.setOnClickListener {
            if (!viewModel.isValidEmail(Objects.requireNonNull(binding.emailField.text).toString())) {
                binding.emailField.error = getString(R.string.validEmailPrompt)
                return@setOnClickListener
            }
            if (Objects.requireNonNull(binding.passwordField.text).toString()
                    .isEmpty() || binding.passwordField.text.toString().length < 6
            ) {
                binding.passwordField.error = getString(R.string.validPasswordPrompt)
                return@setOnClickListener
            }
            if (Objects.requireNonNull(binding.nameField.text).toString().isEmpty()) {
                binding.passwordField.error = getString(R.string.validNamePrompt)
                return@setOnClickListener
            }
            viewModel.registerUser(
                binding.emailField.text.toString(),
                binding.nameField.text.toString(),
                binding.passwordField.text.toString()
            )
        }
    }


    /**
     * Observer View Model
     */
    private fun observe() {
        viewModel.loading.observe(this) {
            if (it) {
                binding.mProgress.visibility = View.VISIBLE
            } else {
                binding.mProgress.visibility = View.GONE
            }
        }
        viewModel.response.observe(this) {
            if (it.success == true) {
                PrefManager.with(applicationContext)!!
                    .writeBoolean(getString(R.string.loggedIn), true)
                PrefManager.with(applicationContext)!!
                    .writeBoolean(getString(R.string.userMode), it.admin)
                PrefManager.with(applicationContext)!!
                    .write(getString(R.string.userToken), it.token)
                startActivity(Intent(this@SignupActivity, UserHomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.responseError.observe(this) {
            Toast.makeText(
                applicationContext,
                it.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }



}