package com.abinash.calorietracker.api

import com.abinash.calorietracker.di.api.DaggerApiComponent
import javax.inject.Inject

class ApiClient {


    @Inject
    lateinit var api: Api

    init {
        DaggerApiComponent.create().inject(this)
    }

    companion object {
        const val baseUrl = "http://164.92.99.165:7000/"
    }

}