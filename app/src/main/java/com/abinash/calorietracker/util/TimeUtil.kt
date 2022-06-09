package com.abinash.calorietracker.util

import com.abinash.calorietracker.R
import android.annotation.SuppressLint
import android.content.Context
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class TimeUtil(var context: Context) {
    @SuppressLint("StringFormatInvalid")
    fun timeAgo(millis: Long): String {
        val diff = Date().time - millis
        val r = context.resources
        val prefix = r.getString(R.string.time_ago_prefix)
        val suffix = r.getString(R.string.time_ago_suffix)
        val seconds = (abs(diff) / 1000).toDouble()
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val years = days / 365
        val words: String = if (seconds < 45) {
            r.getString(R.string.time_ago_seconds, Math.round(seconds))
        } else if (seconds < 90) {
            r.getString(R.string.time_ago_minute, 1)
        } else if (minutes < 45) {
            r.getString(R.string.time_ago_minutes, Math.round(minutes))
        } else if (minutes < 90) {
            r.getString(R.string.time_ago_hour, 1)
        } else if (hours < 24) {
            r.getString(R.string.time_ago_hours, Math.round(hours))
        } else if (hours < 42) {
            r.getString(R.string.time_ago_day, 1)
        } else if (days < 30) {
            r.getString(R.string.time_ago_days, Math.round(days))
        } else if (days < 45) {
            r.getString(R.string.time_ago_month, 1)
        } else if (days < 365) {
            r.getString(R.string.time_ago_months, Math.round(days / 30))
        } else if (years < 1.5) {
            r.getString(R.string.time_ago_year, 1)
        } else {
            r.getString(R.string.time_ago_years, Math.round(years))
        }
        val sb = StringBuilder()
        if (prefix.isNotEmpty()) {
            sb.append(prefix).append(" ")
        }
        sb.append(words)
        if (suffix.isNotEmpty()) {
            sb.append(" ").append(suffix)
        }
        return sb.toString().trim { it <= ' ' }
    }

    companion object {
        /**
         *
         * @param calendar
         * @return the start time (millis) of a particular day which is 12 AM
         */
        fun getStartTime(calendar: Calendar): Long {
            val newCalendar = Calendar.getInstance()
            newCalendar[calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DATE], 0, 0] =
                0
            return newCalendar.timeInMillis
        }

        /**
         *
         * @param calendar
         * @return the end time (millis) of a particular day which is 11:59 PM
         */
        fun getEndTime(calendar: Calendar): Long {
            val newCalendar = Calendar.getInstance()
            newCalendar[calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DATE], 23, 59] =
                59
            return newCalendar.timeInMillis
        }

        /**
         * Formats the current date to make it more human friendly
         * @param calendar
         * @return
         */
        @SuppressLint("SimpleDateFormat")
        fun getCurrentDay(calendar: Calendar): String {
            if (calendar[Calendar.DATE] == Calendar.getInstance()[Calendar.DATE]) {
                return "Today"
            }
            if (calendar[Calendar.DATE] == Calendar.getInstance()[Calendar.DATE] - 1) {
                return "Yesterday"
            }
            val format = SimpleDateFormat("MMMM dd")
            return format.format(calendar.time)
        }

        /**
         * Format time(millis) to a time such as 11:20 PM
         * @param millis
         * @return
         */
        @SuppressLint("SimpleDateFormat")
        fun formatTime(millis: Long): String {
            val date = Date(millis)
            val format = SimpleDateFormat("hh:mm aaa")
            return format.format(date)
        }
    }
}