package com.example.kotlinomnicure.interfaces

import android.content.Intent


interface OnInternetConnChangeListener {

    fun onConnectionChanged(intent: Intent?)
}
