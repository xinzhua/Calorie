package com.abinash.calorietracker.view.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat

import org.junit.Test

class LoginVMTest {

    @Test
    fun `validtest`() {
        val viewModel:LoginVM?=null
        val test= viewModel?.isValidEmail("sddd")
        assertThat(test).isNull()
    }
}