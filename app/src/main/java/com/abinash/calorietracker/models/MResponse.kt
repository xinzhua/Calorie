package com.abinash.calorietracker.models

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class MResponse {
    @SerializedName("success")
    var success: Boolean = false

    @SerializedName("authenticationFailed")
    var authenticationfailed: Boolean = false

    @SerializedName("message")
    var message: String? = null

    @SerializedName("fileurl")
    var fileurl: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("isAdmin")
    var admin: Boolean = false

    @SerializedName("stats")
    var stats: MStats? = null

    @SerializedName("mentries")
    var mEntries: ArrayList<CalEntry>? = null

    @SerializedName("data")
    var data: ArrayList<DocsData>? = null

}