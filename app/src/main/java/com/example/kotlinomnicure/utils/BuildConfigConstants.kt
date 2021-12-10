package com.example.kotlinomnicure.utils


import okhttp3.OkHttpClient
import java.lang.Exception

class BuildConfigConstants {
    private const val TAG = "BuildConfig"
  //  private var credential: GoogleCredential? = null
    var backendAppName = "omnicure"
        private set
    var backendRootUrl = "https://omnicure.appspot.com/_ah/api/"
        private set
    var baseUrl = "omnicure.appspot.com"
        private set

  /*  fun authorize(): GoogleCredential? {
        // load client secrets
        if (credential == null) {
            try {
                val httpTransport: HttpTransport = AndroidHttp.newCompatibleTransport()
                val jsonFactory: JsonFactory = AndroidJsonFactory()
                credential = OkHttpClient.Builder().setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .build()
            } catch (e: Exception) {
//                Log.e(TAG, "Exception:", e.getCause());
            }
        }
        return credential
    }*/

    init {
        if (BuildConfig.SERVER.equalsIgnoreCase("test")) {


            backendAppName = "dev-omnicure"
            backendRootUrl = "https://dev-omnicure.appspot.com/_ah/api/"
            baseUrl = "dev-omnicure.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("exttesting")) {
            backendAppName = "exttesting"
            backendRootUrl = "https://exttesting.appspot.com/_ah/api/"
            baseUrl = "exttesting.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("ext_test")) {
            backendAppName = "omnicure-ext-test"
            backendRootUrl = "https://omnicure-ext-test.appspot.com/_ah/api/"
            baseUrl = "omnicure-ext-test.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("production")) {
            backendAppName = "omnicure"
            backendRootUrl = "https://omnicure.appspot.com/_ah/api/"
            baseUrl = "omnicure.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("demo")) {
            backendAppName = "omnicure-demo"
            backendRootUrl = "https://omnicure-demo.appspot.com/_ah/api/"
            baseUrl = "omnicure-demo.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("qa")) {
            backendAppName = "omnicure-qa"
            backendRootUrl = "https://omnicure-qa.appspot.com/_ah/api/"
            baseUrl = "omnicure-qa.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("omnicurepilot")) {
            backendAppName = "omnicurepilot"
            backendRootUrl = "https://omnicurepilot.appspot.com/_ah/api/"
            baseUrl = "omnicurepilot.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccn")) {
            backendAppName = "omnicure-netccn"
            backendRootUrl = "https://omnicure-netccn.appspot.com/_ah/api/"
            baseUrl = "omnicure-netccn.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("Omnicure-netccnDEV2")) {
            backendAppName = "omnicure-netccndev2"
            backendRootUrl = "https://omnicure-netccndev2.appspot.com/_ah/api/"
            baseUrl = "omnicure-netccndev2.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("Omnicure-netccnDEV3")) {
            backendAppName = "fine-method-317003"
            backendRootUrl = "https://fine-method-317003.appspot.com/_ah/api/"
            baseUrl = "fine-method-317003.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccndev")) {
            backendAppName = "netccndev"
            backendRootUrl = "https://omnicure-netccndev.appspot.com/_ah/api/"
            baseUrl = "omnicure-netccn.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccnautotest")) {
            backendAppName = "netccnautotest"
            backendRootUrl = "https://omnicure-netccnautotest.appspot.com/_ah/api/"
            baseUrl = "omnicure-netccn.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("qa_omnicure")) {
            backendAppName = "qa-omnicure"
            backendRootUrl = "https://qa-omnicure.appspot.com/_ah/api/"
            baseUrl = "qa-omnicure.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("staging_iam")) {
            backendAppName = "omnicure-staging"
            backendRootUrl = "https://omnicure-staging.appspot.com/_ah/api/"
            baseUrl = "omnicure-staging.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("omnicure_test")) {
            backendAppName = "omnicure_test"
            backendRootUrl = "https://omnicure-test.appspot.com/_ah/api/"
            baseUrl = "omnicure-test.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccnsectest")) {
            backendAppName = "netccnsectest"
            backendRootUrl = "https://omnicure-netccnsectest.appspot.com/_ah/api/"
            baseUrl = "omnicure-netccnsectest.appspot.com"
        }
    }
}