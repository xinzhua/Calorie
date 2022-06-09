package com.abinash.calorietracker.adapters

import com.abinash.calorietracker.models.CalEntry
import com.abinash.calorietracker.R
import android.content.Intent
import com.abinash.calorietracker.util.PrefManager
import androidx.recyclerview.widget.RecyclerView
import com.abinash.calorietracker.util.TimeUtil
import com.abinash.calorietracker.view.entryscreen.EntryActivity
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.abinash.calorietracker.databinding.ItemEntryBinding

/**
 * Entry adapter for each entry
 */
class EntryAdapter(//List of entries
    private val mConfigList: List<CalEntry?>?, private val context: Context
) : RecyclerView.Adapter<EntryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            ItemEntryBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val calEntry = mConfigList!![i]
        if(calEntry!=null) {
            holder.binding.tvCalorie.text =
                String.format(context.getString(R.string.tvcalorie), calEntry.calorie)
            holder.binding.tvEntryName.text = calEntry.foodName
            holder.binding.tvUserId.text =
                String.format(context.getString(R.string.userFormat), calEntry.uid)
            if (PrefManager.with(context)!!
                    .readBoolean(context.getString(R.string.userMode), false)
            ) {
                holder.binding.tvUserId.visibility = View.GONE
            }
            if (PrefManager.with(context)!!
                    .readBoolean(context.getString(R.string.userMode), false)
            ) {
                holder.binding.tvEntryDate.text = TimeUtil.formatTime(calEntry.entryDate!!)
            } else {
                holder.binding.tvEntryDate.text = TimeUtil(context).timeAgo(calEntry.entryDate!!)
            }
            holder.binding.root.setOnClickListener {
                val intent = Intent(context, EntryActivity::class.java)
                intent.putExtra(context.getString(R.string.calEntry), calEntry)
                intent.putExtra(context.getString(R.string.editMode), true)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mConfigList!!.size
    }

    class MyViewHolder(var binding: ItemEntryBinding) : RecyclerView.ViewHolder(
        binding.root
    )

}