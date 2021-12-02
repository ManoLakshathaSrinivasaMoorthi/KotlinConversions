package com.example.kotlinomnicure.activity


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityAppointmentSuccessBinding

class AppointmentSuccessActivity : AppCompatActivity() {
    private val TAG: kotlin.String? = AppointmentSuccessActivity::class.java.getSimpleName()
    private var binding: ActivityAppointmentSuccessBinding? = null
    private var context: Context =AppointmentSuccessActivity()

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_success)
        initView()
    }

    private fun initView() {
        binding?.idBackButton?.setOnClickListener { view -> finish() }
        binding?.doneBtn?.setOnClickListener(object : android.view.View.OnClickListener {
           override fun onClick(v: android.view.View?) {
                val intent: Intent = Intent(context, LoginActivity::class.java)
               intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })
    }
}