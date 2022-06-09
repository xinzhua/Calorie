package com.abinash.calorietracker.view.dailylimit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.abinash.calorietracker.R
import com.abinash.calorietracker.databinding.ActivityDailyLimitBinding
import com.abinash.calorietracker.util.MUtil
import java.util.*

class DailyLimitActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDailyLimitBinding
    private lateinit var userId: String

    private lateinit var viewModel: DailyLimitVM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyLimitBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(DailyLimitVM::class.java)
        if (intent.hasExtra("uid")) {
            userId = intent.getStringExtra("uid").toString()
            binding.tvUserId.setText(userId)
        } else {
            finish()
        }
        binding.icBack.setOnClickListener { onBackPressed() }
        binding.btnPublishEntry.setOnClickListener {
            if (Objects.requireNonNull(binding.tvDailyLimit.text).toString()
                    .isEmpty()
            ) return@setOnClickListener
            viewModel.resetLimit(
                userId,
                Objects.requireNonNull(binding.tvDailyLimit.text).toString().toInt(),
                MUtil.getToken(applicationContext)
            )
        }

        observe()


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

        viewModel.done.observe(this) {
            MUtil.validateSession(this@DailyLimitActivity, viewModel.response.value)
        }

        viewModel.response.observe(this) {
            if (it.success == true) {
                Toast.makeText(
                    applicationContext,
                    R.string.dailyLimitConfirmation,
                    Toast.LENGTH_SHORT
                ).show()
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