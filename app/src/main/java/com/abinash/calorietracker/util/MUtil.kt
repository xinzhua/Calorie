package com.abinash.calorietracker.util

import com.abinash.calorietracker.R
import android.content.Intent
import com.abinash.calorietracker.models.MResponse
import android.app.Activity
import com.abinash.calorietracker.view.login.LoginActivity
import android.content.Context

object MUtil {
    /**
     * Gets the current token
     * @param context
     * @return
     */
    fun getToken(context: Context): String {
        return context.getString(R.string.bearerTokenSuffix) + " " + PrefManager.with(
            context
        )!!
            .read(context.getString(R.string.userToken))
    }

    /**
     * Check if current user is admin or not
     * @param context
     * @return true for admin and false for regular user
     */
    fun isAdminMode(context: Context): Boolean {
        return !PrefManager.with(context)!!
            .readBoolean(context.getString(R.string.userMode))
    }

    /**
     * Checks if the current session is valid or not based on the response received from the server
     * @param context
     * @param response
     * If the session is deemed invalid then it will logs out the current users and returns to the login screen
     */
    fun validateSession(context: Activity, response: MResponse?) {
        if (response?.authenticationfailed == true) {
            PrefManager.with(context)!!
                .writeBoolean(context.getString(R.string.loggedIn), false)
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            context.finish()
        }
    }
}