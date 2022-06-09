package com.abinash.calorietracker.di.api

import com.abinash.calorietracker.api.ApiClient
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(api: ApiClient)
}