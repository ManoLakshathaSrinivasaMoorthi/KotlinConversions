package com.example.kotlinomnicure.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityResetSuccessBinding

class ResetSuccessActivity : AppCompatActivity() {

    private val TAG = SignupActivity::class.java.simpleName
    private var binding: ActivityResetSuccessBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_success)
        setView()
    }

    private fun setView() {
        binding?.signinBtn?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ResetSuccessActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this@ResetSuccessActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
