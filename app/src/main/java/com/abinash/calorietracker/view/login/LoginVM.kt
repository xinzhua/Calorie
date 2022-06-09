package com.abinash.calorietracker.view.login

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abinash.calorietracker.api.ApiClient
import com.abinash.calorietracker.models.MResponse
import com.abinash.calorietracker.models.MResponseError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class LoginVM : ViewModel() {


    private val client = ApiClient()
    private val disposables = CompositeDisposable()


    val loading = MutableLiveData<Boolean>()
    val responseError = MutableLiveData<MResponseError>()
    val response = MutableLiveData<MResponse>()


    /**
     * This function will call the login API
     * @param email - Email address of the user
     * @param password - Password
     */
    fun login(email: String?, password: String?) {
        loading.value = true
        disposables.add(
            client.api.authenticate(email, password)
                .subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(object : DisposableSingleObserver<MResponse>() {
                    override fun onSuccess(value: MResponse?) {
                        loading.value = false
                        if(value!=null) {
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


    /**
     * Checks if entered email is valid
     * @param target input email
     * @return true if valid, false if invalid
     */
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target.toString()).matches()
    }
}