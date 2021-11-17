package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityAppointmentSuccessBinding

class AppointmentSuccessActivity : AppCompatActivity() {
    private val TAG = AppointmentSuccessActivity::class.java.simpleName
    private var binding: ActivityAppointmentSuccessBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_success)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_success)
        initView()
    }

    private fun initView() {
        binding!!.idBackButton.setOnClickListener { finish() }

        binding!!.doneBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }
}