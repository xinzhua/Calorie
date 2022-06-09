package com.abinash.calorietracker.view.entryscreen

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.abinash.calorietracker.R
import com.abinash.calorietracker.api.ApiClient
import com.abinash.calorietracker.databinding.ActivityEntryBinding
import com.abinash.calorietracker.util.MUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import java.util.*

class EntryActivity : AppCompatActivity() {


    private lateinit var binding: ActivityEntryBinding

    private lateinit var viewModel: EntryScreenVM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(EntryScreenVM::class.java)

        viewModel.init(intent, applicationContext)

        observe()

        binding.tvTime.setOnClickListener {
            viewModel.selectTime(applicationContext, supportFragmentManager)
        }
        /**
         * Starts an intent to capture a photo, compress it and then crop it
         */
        binding.photoEntry.setOnClickListener {
            ImagePicker.with(this@EntryActivity)
                .crop(1080f, 540f)
                .compress(1024)
                .maxResultSize(1080, 540)
                .start()
        }
        binding.btnPublishEntry.setOnClickListener {
            if (Objects.requireNonNull(binding.nameField.text).toString().isEmpty()) {
                binding.nameField.error = "Please enter valid name!"
                return@setOnClickListener
            }
            if (Objects.requireNonNull(binding.calorieField.text).toString()
                    .isEmpty() || Objects.requireNonNull(
                    binding.calorieField.text
                ).toString().toInt() > 10000
            ) {
                binding.calorieField.error = "Please enter valid value for calorie!"
                return@setOnClickListener
            }
            viewModel.updateEntry(
                Objects.requireNonNull(binding.nameField.text).toString(),
                Objects.requireNonNull(binding.calorieField.text).toString().toInt()
            )

            viewModel.uploadImage(applicationContext)
        }
        binding.icBack.setOnClickListener { onBackPressed() }

        binding.btnDeleteEntry.setOnClickListener {
            viewModel.showDeleteDialog(applicationContext)
        }

        binding.btnDailyLimit.setOnClickListener {
            viewModel.navigateDailyLimit(applicationContext)
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (data.data != null) {
                    viewModel.photoUri.value=data.data
                }
            }
        }
    }

    private fun observe() {

        viewModel.loading.observe(this) {
            if(it){
                binding.mProgress.visibility=View.VISIBLE
            }else{
                binding.mProgress.visibility=View.GONE
            }
        }
        viewModel.deleteResponse.observe(this) {
            if (it.success == true) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.entrySavedConfirmation),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.deleteResponseError.observe(this) {
            Toast.makeText(
                applicationContext,
                it.message,
                Toast.LENGTH_SHORT
            ).show()
        }
        viewModel.deleteDone.observe(this) {

            MUtil.validateSession(this, viewModel.deleteResponse.value)
        }
        viewModel.publishResponse.observe(this) {
            if (it.success == true) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.entrySavedConfirmation),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.publishResponseError.observe(this) {
            Toast.makeText(
                applicationContext,
                it.message,
                Toast.LENGTH_SHORT
            ).show()
        }
        viewModel.publishDone.observe(this) {

            MUtil.validateSession(this, viewModel.deleteResponse.value)
        }
        viewModel.entryTime.observe(this) {
            binding.tvTime.setText(DateFormat.format("hh:mm aaa", it.time))
        }
        viewModel.pickedTime.observe(this) {
            binding.tvTime.setText(it)
        }

        viewModel.photoUri.observe(this) {
            binding.photoEntry.setImageURI(it)
        }

        viewModel.calEntry.observe(this) {
            binding.nameField.setText(it.foodName)
            binding.calorieField.setText(it.calorie.toString())
            if (it.entryImg != null && it.entryImg!!.isNotEmpty()) {
                Picasso.get().load(ApiClient.baseUrl + "media/" + it.entryImg)
                    .into(
                        binding.photoEntry
                    )
            }
            binding.tvTitle.text = it.foodName
        }


        viewModel.editMode.observe(this) {
            if (it) {
                binding.btnDeleteEntry.visibility = View.VISIBLE
            } else {
                binding.btnDeleteEntry.visibility = View.GONE
            }
        }

        viewModel.isAdmin.observe(this) {
            /**
             * If a regular user is accessing this screen then the button to change the daily limit is
             * removed
             */
            if(it) {
                binding.btnDailyLimit.visibility = View.GONE
            }
        }

    }

}