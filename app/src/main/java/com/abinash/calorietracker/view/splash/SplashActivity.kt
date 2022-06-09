package com.abinash.calorietracker.view.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.abinash.calorietracker.util.PrefManager
import android.annotation.SuppressLint
import android.os.Handler
import com.abinash.calorietracker.R
import com.abinash.calorietracker.databinding.ActivitySplashBinding
import com.abinash.calorietracker.view.admin.ReportActivity
import com.abinash.calorietracker.view.login.LoginActivity
import com.abinash.calorietracker.view.user.UserHomeActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    var binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        Handler().postDelayed({
            if (PrefManager.with(applicationContext)!!
                    .readBoolean(getString(R.string.loggedIn), false)
            ) {
                if (PrefManager.with(applicationContext)!!
                        .readBoolean(getString(R.string.userMode), false)
                ) {
                    startActivity(Intent(this@SplashActivity, UserHomeActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, ReportActivity::class.java))
                }
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}