package com.abinash.calorietracker.models

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class DocsData {
    @SerializedName("data")
    val calEntries = ArrayList<CalEntry>()

    @SerializedName("day")
    val day: Long? = null

    @SerializedName("totalCal")
    val totalCal: Int? = null

    @SerializedName("limit")
    val limit: Int? = null
}