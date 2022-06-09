package com.abinash.calorietracker.view.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.abinash.calorietracker.R
import com.abinash.calorietracker.databinding.ActivityLoginBinding
import com.abinash.calorietracker.util.PrefManager
import com.abinash.calorietracker.view.admin.ReportActivity
import com.abinash.calorietracker.view.signup.SignupActivity
import com.abinash.calorietracker.view.user.UserHomeActivity
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(LoginVM::class.java)

        observe()

        binding.btnLogin.setOnClickListener {
            if (!viewModel.isValidEmail(Objects.requireNonNull(binding.emailField.text).toString())) {
                binding.emailField.error = getString(R.string.validEmailPrompt)
                return@setOnClickListener
            }
            if (Objects.requireNonNull(binding.passwordField.text).toString().isEmpty() || binding.passwordField.text.toString().length < 6) { binding.passwordField.error = getString(R.string.validPasswordPrompt)
                return@setOnClickListener
            }
            viewModel.login(binding.emailField.text.toString(), binding.passwordField.text.toString())
        }
        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            finish()
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
                if (it.admin == true) {
                    startActivity(Intent(this@LoginActivity, ReportActivity::class.java))
                } else {
                    startActivity(Intent(this@LoginActivity, UserHomeActivity::class.java))
                }
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