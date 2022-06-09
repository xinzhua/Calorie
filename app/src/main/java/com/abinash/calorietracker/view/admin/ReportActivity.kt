package com.abinash.calorietracker.view.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abinash.calorietracker.adapters.EntryAdapter
import com.abinash.calorietracker.databinding.ActivityReportBinding
import com.abinash.calorietracker.models.MStats
import com.abinash.calorietracker.util.MUtil
import com.abinash.calorietracker.util.SpacesItemDecorationLinear

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private lateinit var entryAdapter: EntryAdapter

    private lateinit var viewModel: AdminVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        entryAdapter=EntryAdapter(arrayListOf(), applicationContext)
        viewModel = ViewModelProvider(this).get(AdminVM::class.java)

        observe()

        viewModel.getRecentStats(applicationContext)
        viewModel.refreshEntries(applicationContext)
        binding.icLogOut.setOnClickListener {
            viewModel.showLogOutDialog(this)
        }
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
    }

    private fun observe() {
        viewModel.currentStats.observe(this) {
            binding.statsLayout.visibility = View.VISIBLE
            binding.statsLayout2.visibility = View.VISIBLE
            populateRecentStats(it)
        }
        viewModel.statsError.observe(this) {
            Toast.makeText(
                applicationContext,
                it.message,
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.done.observe(this) {
            MUtil.validateSession(this, viewModel.response.value)
        }

        viewModel.response.observe(this) {
            val entries = it.mEntries
            if (entries!!.size > 0) {
                entryAdapter = EntryAdapter(entries, applicationContext)
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

    /**
     * Fills the textview with following info
     */
    private fun populateRecentStats(currentStats: MStats) {
        binding.tvEntryThisWeek.text = currentStats.last7DayEntryCount.toString()
        binding.tvEntryLastWeek.text = currentStats.lastWeekEntryCount.toString()
        binding.tvAvgCalorieThisWeek.text = currentStats.last7DayCalCount.toString()
        binding.tvAvgCalorieLastWeek.text = currentStats.lastWeekCalCount.toString()
    }

}