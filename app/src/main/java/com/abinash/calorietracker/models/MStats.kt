package com.abinash.calorietracker.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MStats : Serializable {
    @SerializedName("last7DayCalCount")
    val last7DayCalCount: Int? = null

    @SerializedName("lastWeekCalCount")
    val lastWeekCalCount: Int? = null

    @SerializedName("last7DayEntryCount")
    val last7DayEntryCount: Int? = null

    @SerializedName("lastWeekEntryCount")
    val lastWeekEntryCount: Int? = null
}