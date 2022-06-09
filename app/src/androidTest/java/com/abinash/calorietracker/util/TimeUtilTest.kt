package com.abinash.calorietracker.util

import android.content.Context
import com.google.common.truth.Truth.assertThat

import org.junit.Test
import java.util.*

class TimeUtilTest {

    @Test
    fun `start_time_should_be_first_ms_of_day` () {
        val timeago=TimeUtil.getStartTime(Calendar.getInstance())
        assertThat(timeago).isEqualTo("1654712100326")
    }

    @Test
    fun `end_time_should_be_last_ms_of_day` () {
        val timeago=TimeUtil.getEndTime(Calendar.getInstance())
        assertThat(timeago).isEqualTo("1654798499328")
    }

    @Test
    fun `today_test` () {
        val timeago=TimeUtil.getCurrentDay(Calendar.getInstance())
        assertThat(timeago).isEqualTo("Today")
    }

    @Test
    fun `yesterday_test` () {
        val cal=Calendar.getInstance();
        cal.timeInMillis=1654625700000;
        val timeago=TimeUtil.getCurrentDay(cal)
        assertThat(timeago).isEqualTo("Yesterday")
    }

}