package com.example.kotlinomnicure.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.kotlinomnicure.R


class AppointmentSuccessActivity : AppCompatActivity() {
    private val TAG: kotlin.String? =
        com.mvp.omnicure.activity.AppointmentSuccessActivity::class.java.getSimpleName()
    private var binding: ActivityAppointmentSuccessBinding? = null

    protected open fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_success)
        initView()
    }

    private open fun initView() {
        binding.idBackButton.setOnClickListener({ view -> finish() })
        binding.doneBtn.setOnClickListener(object : android.view.View.OnClickListener {
            open fun onClick(v: android.view.View?) {
                var intent: Intent? =
                    Intent(this, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        })
    }
}