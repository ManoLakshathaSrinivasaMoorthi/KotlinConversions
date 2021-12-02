package com.example.kotlinomnicure.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.interfaces.OnListItemClickListener

class HomeActivity : AppCompatActivity(), OnListItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
}