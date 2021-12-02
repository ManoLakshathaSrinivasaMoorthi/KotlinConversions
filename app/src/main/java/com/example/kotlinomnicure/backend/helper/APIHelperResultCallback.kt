package com.example.kotlinomnicure.backend.helper

interface APIHelperResultCallback {
    fun isInternetConnected(): Boolean
    fun showLoader()
    fun hideLoader()
    fun onError(errMsg: String?)
}