package com.abinash.calorietracker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abinash.calorietracker.models.DocsData
import com.abinash.calorietracker.R
import com.abinash.calorietracker.util.SpacesItemDecorationLinear
import com.abinash.calorietracker.util.TimeUtil
import com.abinash.calorietracker.databinding.ItemEntryParentBinding
import java.util.*

/**
 * Adapter for each day
 */
class DayAdapter(private val mConfigList: List<DocsData?>?, private val context: Context) :
    RecyclerView.Adapter<DayAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            ItemEntryParentBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
        val calEntry = mConfigList!![i]
        if (calEntry != null) {
            val manager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            viewHolder.binding.entriesList.layoutManager = manager
            if (viewHolder.binding.entriesList.itemDecorationCount == 0) {
                viewHolder.binding.entriesList.addItemDecoration(
                    SpacesItemDecorationLinear(
                        20,
                        false
                    )
                )
            }
            viewHolder.binding.entriesList.isNestedScrollingEnabled = true
            val entries = calEntry.calEntries
            if (entries.size > 0) {
                val entryAdapter = EntryAdapter(entries, context)
                viewHolder.binding.entriesList.adapter = entryAdapter
                val calPercent = calEntry.totalCal!! * 100 / calEntry.limit!!
                viewHolder.binding.tvCalorie.text = calEntry.totalCal.toString()
                viewHolder.binding.tvCalPercent.text = calPercent.toString()
                viewHolder.binding.tvLimit.text =
                    String.format(context.getString(R.string.calLimitFormat), calEntry.limit)
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = calEntry.day!!
                viewHolder.binding.tvEntryDate.text = TimeUtil.getCurrentDay(calendar)
                viewHolder.binding.calProgress.progress = calPercent
                viewHolder.binding.calProgress.trackColor = getMColorHalf(calPercent)
                viewHolder.binding.calProgress.setIndicatorColor(getMColor(calPercent))
                viewHolder.binding.tvCalPercent.setTextColor(getMColor(calPercent))
                viewHolder.binding.notEntryLayout.root.visibility = View.GONE
                viewHolder.binding.entriesList.visibility = View.VISIBLE
            } else {
                viewHolder.binding.notEntryLayout.root.visibility = View.VISIBLE
                viewHolder.binding.entriesList.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return mConfigList!!.size
    }

    class MyViewHolder(var binding: ItemEntryParentBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    /**
     *
     * @param percent
     * @return a color based on the calorie intake percent
     */
    private fun getMColor(percent: Int): Int {
        if (percent < 40) {
            return context.getColor(R.color.col_low)
        }
        return if (percent < 80) {
            context.getColor(R.color.col_mid)
        } else context.getColor(R.color.col_high)
    }

    /**
     *
     * @param percent
     * @return a color based on the calorie intake percent
     */
    private fun getMColorHalf(percent: Int): Int {
        if (percent < 40) {
            return context.getColor(R.color.col_low_half)
        }
        return if (percent < 80) {
            context.getColor(R.color.col_mid_half)
        } else context.getColor(R.color.col_high_half)
    }
}