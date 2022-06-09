package com.abinash.calorietracker.view.dailylimit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abinash.calorietracker.api.ApiClient
import com.abinash.calorietracker.models.MResponse
import com.abinash.calorietracker.models.MResponseError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DailyLimitVM : ViewModel() {


    private val client = ApiClient()
    private val disposables = CompositeDisposable()


    val loading = MutableLiveData<Boolean>()
    val done = MutableLiveData<Boolean>()
    val responseError = MutableLiveData<MResponseError>()
    val response = MutableLiveData<MResponse>()


    /**
     * Calls the REST API to change the daily limit of the user defined by uid
     * @param uid - User Id of the user whose limit is being altered
     * @param limit - New daily calorie limit of that particular user
     */
    fun resetLimit(uid: String?, limit: Int, token: String) {
        loading.value = true
        disposables.add(
            client.api.resetLimit(token, uid, limit)
                .subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(object : DisposableSingleObserver<MResponse>() {
                    override fun onSuccess(value: MResponse?) {
                        loading.value = false
                        if(value!=null) {
                            done.value = true
                            response.value = value
                        }else{
                            responseError.value= MResponseError("Unknown Error")
                        }
                    }
                    override fun onError(e: Throwable?) {
                        if (e != null) {
                            responseError.value= MResponseError(e.message)
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