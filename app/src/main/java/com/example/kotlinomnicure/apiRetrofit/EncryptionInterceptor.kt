package com.example.kotlinomnicure.apiRetrofit

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.kotlinomnicure.utils.AESUtils
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.Buffer
import java.io.IOException
import java.lang.Exception
import java.util.logging.Level
import java.util.logging.Level.parse

class EncryptionInterceptor(appContext: Context) : Interceptor {

    private val TAG = "EncryptionInterceptor"
    private var context: Context? = null



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
//        context = context.getApplicationContext();
        var request: Request = chain.request()
        //        String url = request.url().url().toString();
//        Log.d(TAG, "intercept: url "+url);
//        url = AESUtils.encryptData(url,"E78F5DF838969053C89DB80649AD3A200C93F0108CA2ED7C04499A956FEFD63A");
//        Log.d(TAG, "intercept: url encry "+url);
        val body = request.body

//        Log.e(TAG, "intercept: url body "+new Gson().toJson(body));
        val mediaType: Level = parse("text/plain; charset=utf-8")
        val `object` = JsonObject()
        val buffer = Buffer()
        if (body != null) {
            Log.e(TAG, "Encryptintercept: body-->$body")
            body.writeTo(buffer)
            val requestString = buffer.readUtf8()
            Log.e(TAG, "Encryptintercept: requestString-->$requestString")
            var aesKey: String? = null
            try {
                aesKey = context?.let {
                    PrefUtility().getStringInPref(
                        it,
                        Constants.SharedPrefConstants.AES_API_KEY,
                        ""
                    )
                }
                Log.e(TAG, "Encryptintercept: aesKey-->$aesKey")
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            }
            //            String encryptedData = AESUtils.encryptData(requestString, "E78F5DF838969053C89DB80649AD3A200C93F0108CA2ED7C04499A956FEFD63A");
            val encryptedData: String? = aesKey?.let { AESUtils().encryptData(requestString, it) }
            //            String encryptedData = AESUtilsCBC.encryptData(requestString, aesKey);
            encryptedData?.let { Log.e("encryptedData-->", it) }
            `object`.addProperty("encryptedValue", encryptedData)
            Log.d(
                TAG,
                "url Endpoint req " + request.url.toString()
                    .substring(request.url.toString().lastIndexOf('/') + 1).replace("}", "")
            )
            Log.d(TAG, "intercept: reqStr $requestString")
            Log.d(TAG, "intercept: encryptedString $encryptedData")
            val decryptedData: String? = encryptedData?.let { aesKey?.let { it1 ->
                AESUtils().decryptData(it,
                    it1
                )
            } }
            //            String decryptedData = AESUtilsCBC.decryptData(encryptedData, aesKey);
            Log.d(TAG, "intercept: decryptedString $decryptedData")
            Log.d(TAG, "intercept: final " + Gson().toJson(`object`))
            val json = Gson().toJson(`object`)
            val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
            request = request.newBuilder() //                    .url(url)
                .header(
                    "Content-Type",
                    body.contentType().toString()
                ) //                    .header("Content-Type", String.valueOf(body.contentType()))
                .header("Content-Length", body.contentLength().toString())
                .method(request.method, requestBody)
                .build()
            Log.d(TAG, "intercept: req $request")
        }
        buffer.close()
        return chain.proceed(request)
    }
}
