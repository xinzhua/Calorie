package com.abinash.calorietracker.view.entryscreen

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abinash.calorietracker.R
import com.abinash.calorietracker.api.ApiClient
import com.abinash.calorietracker.models.CalEntry
import com.abinash.calorietracker.models.MResponse
import com.abinash.calorietracker.models.MResponseError
import com.abinash.calorietracker.util.MUtil
import com.abinash.calorietracker.view.dailylimit.DailyLimitActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*


class EntryScreenVM : ViewModel() {

    //Calorie Entry
    val calEntry = MutableLiveData<CalEntry>()
    val editMode = MutableLiveData<Boolean>()
    val isAdmin = MutableLiveData<Boolean>()
    val photoUri = MutableLiveData<Uri>()
    private val selectedTime = MutableLiveData<Long>()
    val entryTime = MutableLiveData<Calendar>()
    val pickedTime = MutableLiveData<String>()

    private val client = ApiClient()
    private val disposables = CompositeDisposable()

    val loading = MutableLiveData<Boolean>()


    val publishDone = MutableLiveData<Boolean>()
    val publishResponseError = MutableLiveData<MResponseError>()
    val publishResponse = MutableLiveData<MResponse>()


    val deleteDone = MutableLiveData<Boolean>()
    val deleteResponseError = MutableLiveData<MResponseError>()
    val deleteResponse = MutableLiveData<MResponse>()

    fun selectTime(context: Context, supportFragmentManager: FragmentManager) {
        val timePicker =
            MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()
        timePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            val newCalendar = Calendar.getInstance()
            newCalendar[calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DATE], timePicker.hour] =
                timePicker.minute
            selectedTime.value = newCalendar.timeInMillis
            if (timePicker.hour < 12) {
                pickedTime.value = String.format(
                    context.getString(R.string.am_format),
                    timePicker.hour,
                    timePicker.minute
                )
            } else {
                pickedTime.value = String.format(
                    context.getString(R.string.pm_format),
                    timePicker.hour - 12,
                    timePicker.minute
                )
            }
        }
        timePicker.show(supportFragmentManager, context.getString(R.string.timePickerTitle))
    }

    fun navigateDailyLimit(context: Context) {
        val intent = Intent(context, DailyLimitActivity::class.java)
        intent.putExtra("uid", calEntry.value?.uid)
        context.startActivity(intent)
    }

    fun showDeleteDialog(context: Context) {
        val builder =
            MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.deleteEntryPrompt))
                .setPositiveButton(context.getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                    deleteEntry(
                        context
                    )
                }
                .setNegativeButton(context.getString(R.string.no)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                .setMessage("Are you sure you want to delete this entry? Once delete it cannot be recovered!")
        builder.show()
    }

    fun init(intent: Intent, context: Context) {
        if (intent.hasExtra(context.getString(R.string.editMode))) {
            editMode.value = intent.getBooleanExtra(context.getString(R.string.editMode), false)
            calEntry.value =
                intent.getSerializableExtra(context.getString(R.string.calEntry)) as CalEntry
            entryTime.value = Calendar.getInstance()
            selectedTime.value = calEntry.value!!.entryDate
        } else {
            calEntry.value = CalEntry()
            val calendar = Calendar.getInstance()
            selectedTime.value = calendar.timeInMillis
            entryTime.value = calendar
        }

        /**
         * If a regular user is accessing this screen then the button to change the daily limit is
         * removed
         */
        isAdmin.value = MUtil.isAdminMode(context)
    }

    /**
     * Uploads the image to the server and receives a url string as response
     */
    fun uploadImage(context: Context) {
        if (photoUri.value != null) {
            loading.value = true


            val file = File(photoUri.value!!.path.toString())
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            disposables.add(
                client.api.uploadImage(body)
                    .subscribeOn(Schedulers.newThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribeWith(object : DisposableSingleObserver<MResponse>() {
                        override fun onSuccess(value: MResponse?) {
                            if (value != null) {
                                if (value.success == true) {
                                    calEntry.value!!.entryImg = value.fileurl
                                }
                            }
                            publishEntry(context)
                        }

                        override fun onError(e: Throwable?) {
                            loading.value = false
                            if (e != null) {
                                publishResponseError.value = MResponseError(e.message)
                            }
                        }

                    })
            )
        } else {
            publishEntry(context)
        }
    }

    /**
     * If the entry is being edited, this function will populate the fields with the existing entry
     */
    fun updateEntry(foodName: String, calorie: Int) {
        calEntry.value!!.foodName = foodName
        calEntry.value!!.calorie = calorie
        calEntry.value!!.entryDate = selectedTime.value
    }

    /**
     * This function will either update an existing entry or create a new entry
     */
    private fun publishEntry(context: Context) {
        loading.value = true
        val call = if (editMode.value == true) {
            client.api.editEntry(
                MUtil.getToken(
                    context
                ), calEntry.value?.id, calEntry.value
            )
        } else {
            client.api.postEntry(
                MUtil.getToken(
                    context
                ), calEntry.value
            )
        }
        disposables.add(
            call
                .subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(object : DisposableSingleObserver<MResponse>() {
                    override fun onSuccess(value: MResponse?) {
                        loading.value = false
                        if (value != null) {
                            publishDone.value = true
                            publishResponse.value = value
                        } else {
                            publishResponseError.value = MResponseError("Unknown Error")
                        }

                    }

                    override fun onError(e: Throwable?) {
                        loading.value = false
                        if (e != null) {
                            publishResponseError.value = MResponseError(e.message)
                        }
                    }

                })
        )
    }

    /**
     * This function will delete current entry
     */
    private fun deleteEntry(context: Context) {
        loading.value = true
        val call = client.api.deleteEntry(
            MUtil.getToken(
                context
            ), calEntry.value?.id
        )
        disposables.add(
            call
                .subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(object : DisposableSingleObserver<MResponse>() {
                    override fun onSuccess(value: MResponse?) {
                        loading.value = false
                        if (value != null) {
                            publishDone.value = true
                            publishResponse.value = value
                        } else {
                            publishResponseError.value = MResponseError("Unknown Error")
                        }

                    }

                    override fun onError(e: Throwable?) {
                        loading.value = false
                        if (e != null) {
                            publishResponseError.value = MResponseError(e.message)
                        }
                    }

                })
        )

    }


}