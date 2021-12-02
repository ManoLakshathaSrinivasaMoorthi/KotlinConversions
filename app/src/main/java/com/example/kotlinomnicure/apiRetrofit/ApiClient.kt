package com.example.kotlinomnicure.apiRetrofit

import android.util.Log
import com.example.kotlinomnicure.OmnicureApp

import com.example.kotlinomnicure.utils.PrefUtility

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {

    private val BASE_URL: String? = BuildConfigConstants().getBackendRootUrl()
    private val TAG = "ApiClient"
    private val AUTHORIZATION = "Authorization"
    private val UID = "uid"
    private var retrofit: Retrofit? = null
    private var loggingInterceptor: HttpLoggingInterceptor? = null


    private fun ApiClient() {}

    fun getApi(encrypt: Boolean, decrypt: Boolean): ApiEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()
        loggingInterceptor?.let { builder.addNetworkInterceptor(it) }
        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp().getAppContext()!!))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp().getAppContext()))
        }
        addInterceptors(builder)
        getRetrofit(builder)
        return retrofit!!.create(ApiEndpoints::class.java)
    }

    fun getApiUserEndpoints(encrypt: Boolean, decrypt: Boolean): UserEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()
        loggingInterceptor?.let { builder.addNetworkInterceptor(it) }
        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp().getAppContext()!!))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp().getAppContext()))
        }
        addInterceptors(builder)
        getRetrofit(builder)
        return retrofit!!.create(UserEndpoints::class.java)
    }

    fun getApiPatientEndpoints(encrypt: Boolean, decrypt: Boolean): PatientEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()
        loggingInterceptor?.let { builder.addNetworkInterceptor(it) }
        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp().getAppContext()!!))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp().getAppContext()))
        }
        addInterceptors(builder)
        getRetrofit(builder)
        return retrofit!!.create(PatientEndpoints::class.java)
    }

    fun getApiProviderEndpoints(encrypt: Boolean, decrypt: Boolean): ProviderEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()
        loggingInterceptor?.let { builder.addNetworkInterceptor(it) }
        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp().getAppContext()!!))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp().getAppContext()))
        }
        addInterceptors(builder)
        getRetrofit(builder)
        return retrofit!!.create(ProviderEndpoints::class.java)
    }

    fun getApiHospital(encrypt: Boolean, decrypt: Boolean): HospitalEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()
        loggingInterceptor?.let { builder.addNetworkInterceptor(it) }
        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp().getAppContext()!!))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp().getAppContext()))
        }
        addInterceptors(builder)
        getRetrofit(builder)
        return retrofit!!.create(HospitalEndpoints::class.java)
    }

    private fun getOkHttpBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .callTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
    }

    private fun getLoggingInterceptor() {
        if (loggingInterceptor == null) {
            loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor!!.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    private fun addInterceptors(builder: OkHttpClient.Builder) {
        builder.addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val request = chain.request()
            chain.proceed(
                OmnicureApp().getAppContext()?.let { PrefUtility().getFireBaseUid(it).toString() }?.let {
                    request.newBuilder()
                        .addHeader(
                            AUTHORIZATION,
                            OmnicureApp().getAppContext()?.let { PrefUtility().getHeaderIdToken(it) }!!
                        )
                        .addHeader(UID,
                            it
                        )
                }!!
                    .build()
            )
        })
    }

    private fun getRetrofit(builder: OkHttpClient.Builder) {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        retrofit = Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(builder.build())
            .build()
    }

    private fun getBaseUrl(): String? {
        val url = BASE_URL
        Log.d(TAG, "getBaseUrl: $url")
        return url
    }
}

