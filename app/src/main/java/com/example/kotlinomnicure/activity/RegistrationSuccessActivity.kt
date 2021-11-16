package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityRegistrationSuccessBinding
import com.example.kotlinomnicure.utils.StringUtil

class RegistrationSuccessActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegistrationSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_registration_success)
        StringUtil().stripUnderlines(binding.textTitle.text as Spannable)
        setView()
    }

    private fun setView() {
     binding.alreadySigninText.setOnClickListener {
         val intent = Intent(this, LoginActivity::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
         startActivity(intent) }
    }
    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}