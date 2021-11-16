package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinomnicure.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        onClickSignUp()
    }

    private fun onClickSignUp() {
       // Log.i(LoginActivity.TAG, "onClickSignUp: ")
     //   handleMultipleClick(binding.idSignupText)
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

}