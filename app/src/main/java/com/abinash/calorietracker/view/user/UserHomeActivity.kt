package com.abinash.calorietracker.view.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abinash.calorietracker.R
import com.abinash.calorietracker.adapters.DayAdapter
import com.abinash.calorietracker.adapters.EntryAdapter
import com.abinash.calorietracker.databinding.ActivityUserHomeBinding
import com.abinash.calorietracker.util.MUtil
import com.abinash.calorietracker.util.PrefManager
import com.abinash.calorietracker.util.SpacesItemDecorationLinear
import com.abinash.calorietracker.view.entryscreen.EntryActivity
import com.abinash.calorietracker.view.login.LoginActivity

class UserHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserHomeBinding
    private lateinit var entryAdapter: DayAdapter

    private lateinit var viewModel: UserVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        if (!PrefManager.with(applicationContext)!!
                .readBoolean(getString(R.string.loggedIn), false)
        ) {
            startActivity(Intent(this@UserHomeActivity, LoginActivity::class.java))
            finish()
        }

        entryAdapter=DayAdapter(arrayListOf(), applicationContext)
        viewModel = ViewModelProvider(this).get(UserVM::class.java)
        viewModel.init(applicationContext)
        observe()
        binding.btnAddEntry.setOnClickListener {
            startActivity(
                Intent(
                    this@UserHomeActivity,
                    EntryActivity::class.java
                )
            )
        }
        binding.icSelectDate.setOnClickListener { viewModel.pickDateRange(supportFragmentManager, applicationContext) }


        binding.icLogOut.setOnClickListener {
            viewModel.showLogOutDialog(this)
        }
        binding.tvCurrentDate.setOnClickListener { viewModel.pickDateRange(supportFragmentManager, applicationContext) }
        binding.refreshing.setOnRefreshListener { viewModel.refreshEntries(applicationContext) }
    }


    override fun onResume() {
        super.onResume()
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        binding.docsList.layoutManager = manager
        if (binding.docsList.itemDecorationCount == 0) {
            binding.docsList.addItemDecoration(SpacesItemDecorationLinear(20, true))
        }
        binding.docsList.isNestedScrollingEnabled = true
        binding.docsList.adapter = entryAdapter
        viewModel.refreshEntries(applicationContext)

    }

    private fun observe() {


        viewModel.pickedDate.observe(this) {
            binding.tvCurrentDate.text = it
        }

        viewModel.done.observe(this) {
            MUtil.validateSession(this, viewModel.response.value)
        }

        viewModel.response.observe(this) {
            val entries = it.data
            if (entries!!.size > 0) {
                entryAdapter = DayAdapter(entries, applicationContext)
                binding.docsList.adapter = entryAdapter
                binding.notEntryLayout.root.visibility = View.GONE
                binding.docsList.visibility = View.VISIBLE
            } else {
                binding.notEntryLayout.root.visibility = View.VISIBLE
                binding.docsList.visibility = View.GONE
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