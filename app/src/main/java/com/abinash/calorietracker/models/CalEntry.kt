package com.abinash.calorietracker.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CalEntry : Serializable {
    @SerializedName("_id")
    var id: String? = null

    @SerializedName("entry_img")
    var entryImg: String? = null

    @SerializedName("food_name")
    var foodName: String? = null

    @SerializedName("calorie")
    var calorie: Int? = null

    @SerializedName("entry_date")
    var entryDate: Long? = null

    @SerializedName("uid")
    var uid: String? = null

    @SerializedName("createdAt")
    var createdAt: String? = null

    @SerializedName("updatedAt")
    var updatedAt: String? = null

    @SerializedName("__v")
    var v: Int? = null
}