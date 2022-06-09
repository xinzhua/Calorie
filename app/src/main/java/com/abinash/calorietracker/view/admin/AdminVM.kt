package com.abinash.calorietracker.view.admin

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abinash.calorietracker.R
import com.abinash.calorietracker.api.ApiClient
import com.abinash.calorietracker.models.MResponse
import com.abinash.calorietracker.models.MResponseError
import com.abinash.calorietracker.models.MStats
import com.abinash.calorietracker.util.MUtil
import com.abinash.calorietracker.util.PrefManager
import com.abinash.calorietracker.view.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class AdminVM : ViewModel() {


    private val client = ApiClient()
    private val disposables = CompositeDisposable()

    val currentStats = MutableLiveData<MStats>()
    val statsError = MutableLiveData<MResponseError>()

    val done = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val responseError = MutableLiveData<MResponseError>()
    val response = MutableLiveData<MResponse>()


    fun showLogOutDialog(context: Activity) {
        val builder =
            MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.logOutPrompt))
                .setPositiveButton(context.getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                    PrefManager.with(context)
                        ?.writeBoolean(context.getString(R.string.loggedIn), true)
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    context.finish()
                }
                .setNegativeButton(context.getString(R.string.no)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
        builder.show()
    }

    /**
     * Get recent stats from the API
     * 1. Entry this week
     * 2. Entries last week
     * 3. Avg calorie per user this week
     * 4. Avg calorie per user last week
     */
    fun getRecentStats(context: Context) {
        loading.value = true
        disposables.add(
            client.api.getRecentStats(MUtil.getToken(context))
                .subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(object : DisposableSingleObserver<MResponse>() {
                    override fun onSuccess(value: MResponse?) {
                        loading.value = false
                        if (value != null) {
                            currentStats.value = value.stats
                        } else {
                            statsError.value = MResponseError("Unknown Error")
                        }
                    }

                    override fun onError(e: Throwable?) {
                        if (e != null) {
                            statsError.value = MResponseError(e.message)
                        }
                    }

                })
        )
    }

    /**
     * Refresh the current entries from the server
     */
    fun refreshEntries(context: Context) {
        loading.value = true
        disposables.add(
            client.api.getAdminEntries(MUtil.getToken(context))
                .subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(object : DisposableSingleObserver<MResponse>() {
                    override fun onSuccess(value: MResponse?) {
                        loading.value = false
                        if (value != null) {
                            done.value = true
                            if (value.success == true) {
                                response.value=value
                            } else {
                                responseError.value = MResponseError(value.message)
                            }

                            response.value = value
                        } else {
                            responseError.value = MResponseError("Unknown Error")
                        }
                    }

                    override fun onError(e: Throwable?) {
                        if (e != null) {
                            responseError.value = MResponseError(e.message)
                        }
                    }

                })
        )
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}