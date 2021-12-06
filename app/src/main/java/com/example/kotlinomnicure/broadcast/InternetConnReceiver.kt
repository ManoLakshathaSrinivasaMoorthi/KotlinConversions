package com.example.kotlinomnicure.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.kotlinomnicure.interfaces.OnInternetConnChangeListener
import com.example.kotlinomnicure.activity.BaseActivity

class InternetConnReceiver(baseActivity: BaseActivity) : BroadcastReceiver() {

    private val TAG = InternetConnReceiver::class.java.simpleName
    private var listener: OnInternetConnChangeListener? = null

    fun InternetConnReceiver(context: Context?) {
        if (context is BaseActivity) {
            listener = context
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(TAG, "onReceive: of InternetConnReceiver $context")
        listener?.onConnectionChanged(intent) ?: Log.i(TAG, "onReceive: listener is null")
    }
}


