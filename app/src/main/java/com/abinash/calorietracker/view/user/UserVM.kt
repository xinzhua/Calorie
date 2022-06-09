package com.abinash.calorietracker.view.user

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abinash.calorietracker.R
import com.abinash.calorietracker.api.ApiClient
import com.abinash.calorietracker.models.MResponse
import com.abinash.calorietracker.models.MResponseError
import com.abinash.calorietracker.util.MUtil
import com.abinash.calorietracker.util.PrefManager
import com.abinash.calorietracker.util.TimeUtil
import com.abinash.calorietracker.view.login.LoginActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class UserVM : ViewModel() {


    private val client = ApiClient()
    private val disposables = CompositeDisposable()


    val pickedDate = MutableLiveData<String>()

    val done = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val responseError = MutableLiveData<MResponseError>()
    val response = MutableLiveData<MResponse>()


    private var fromDate: Long = 0
    private var toDate: Long = 0

    fun init(context: Context) {
        val calendar = Calendar.getInstance()
        fromDate = TimeUtil.getStartTime(calendar)
        toDate = TimeUtil.getEndTime(calendar)
        refreshEntries(context)
    }

    /**
     * Pick two dates ( from date - to date )
     */
    fun pickDateRange(supportFragmentManager: FragmentManager, context: Context) {
        val datePicker =
            MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select date range").build()
        datePicker.addOnPositiveButtonClickListener { selection: Pair<Long, Long> ->
            fromDate = selection.first
            toDate = selection.second
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis = fromDate
            val cal2 = Calendar.getInstance()
            cal2.timeInMillis = toDate
            fromDate = TimeUtil.getStartTime(cal1)
            toDate = TimeUtil.getEndTime(cal2)
            pickedDate.value = datePicker.headerText
            refreshEntries(context)
        }


        datePicker.show(supportFragmentManager, "")
    }


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
     * Refresh the current entries from the server
     */
    fun refreshEntries(context: Context) {
        loading.value = true
        disposables.add(
            client.api.getEntries(MUtil.getToken(context), fromDate, toDate)
                .subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(object : DisposableSingleObserver<MResponse>() {
                    override fun onSuccess(value: MResponse?) {
                        loading.value = false
                        if (value != null) {
                            done.value = true
                            if (value.success == true) {
                                response.value = value
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