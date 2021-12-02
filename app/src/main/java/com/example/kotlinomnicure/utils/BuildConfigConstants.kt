package com.example.kotlinomnicure.utils

import androidx.databinding.library.BuildConfig
import java.lang.Exception

class BuildConfigConstants {

    private val TAG = "BuildConfig"
    private var credential: GoogleCredential? = null
    private var BACKEND_APP_NAME = "omnicure"
    private var BACKEND_ROOT_URL = "https://omnicure.appspot.com/_ah/api/"
    private var BASE_URL = "omnicure.appspot.com"

    static
    {
        if (BuildConfig.SERVER.equalsIgnoreCase("test")) {

//            BACKEND_APP_NAME = "omnicure-backend";
//            BACKEND_ROOT_URL = "https://omnicure-backend.appspot.com/_ah/api/";
//            BASE_URL = "omnicure-backend.appspot.com";

            BACKEND_APP_NAME = "dev-omnicure"
            BACKEND_ROOT_URL = "https://dev-omnicure.appspot.com/_ah/api/"
            BASE_URL = "dev-omnicure.appspot.com"

        } else if (BuildConfig.SERVER.equalsIgnoreCase("exttesting")) {
            BACKEND_APP_NAME = "exttesting"
            BACKEND_ROOT_URL = "https://exttesting.appspot.com/_ah/api/"
            BASE_URL = "exttesting.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("ext_test")) {
            BACKEND_APP_NAME = "omnicure-ext-test"
            BACKEND_ROOT_URL = "https://omnicure-ext-test.appspot.com/_ah/api/"
            BASE_URL = "omnicure-ext-test.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("production")) {
            BACKEND_APP_NAME = "omnicure"
            BACKEND_ROOT_URL = "https://omnicure.appspot.com/_ah/api/"
            BASE_URL = "omnicure.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("demo")) {
            BACKEND_APP_NAME = "omnicure-demo"
            BACKEND_ROOT_URL = "https://omnicure-demo.appspot.com/_ah/api/"
            BASE_URL = "omnicure-demo.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("qa")) {
            BACKEND_APP_NAME = "omnicure-qa"
            BACKEND_ROOT_URL = "https://omnicure-qa.appspot.com/_ah/api/"
            BASE_URL = "omnicure-qa.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("omnicurepilot")) {
            BACKEND_APP_NAME = "omnicurepilot"
            BACKEND_ROOT_URL = "https://omnicurepilot.appspot.com/_ah/api/"
            BASE_URL = "omnicurepilot.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccn")) {
            BACKEND_APP_NAME = "omnicure-netccn"
            BACKEND_ROOT_URL = "https://omnicure-netccn.appspot.com/_ah/api/"
            BASE_URL = "omnicure-netccn.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("Omnicure-netccnDEV2")) {
            BACKEND_APP_NAME = "omnicure-netccndev2"
            BACKEND_ROOT_URL = "https://omnicure-netccndev2.appspot.com/_ah/api/"
            BASE_URL = "omnicure-netccndev2.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("Omnicure-netccnDEV3")) {
            BACKEND_APP_NAME = "fine-method-317003"
            BACKEND_ROOT_URL = "https://fine-method-317003.appspot.com/_ah/api/"
            BASE_URL = "fine-method-317003.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccndev")) {
            BACKEND_APP_NAME = "netccndev"
            BACKEND_ROOT_URL = "https://omnicure-netccndev.appspot.com/_ah/api/"
            BASE_URL = "omnicure-netccn.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccnautotest")) {
            BACKEND_APP_NAME = "netccnautotest"
            BACKEND_ROOT_URL = "https://omnicure-netccnautotest.appspot.com/_ah/api/"
            BASE_URL = "omnicure-netccn.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("qa_omnicure")) {
            BACKEND_APP_NAME = "qa-omnicure"
            BACKEND_ROOT_URL = "https://qa-omnicure.appspot.com/_ah/api/"
            BASE_URL = "qa-omnicure.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("staging_iam")) {
            BACKEND_APP_NAME = "omnicure-staging"
            BACKEND_ROOT_URL = "https://omnicure-staging.appspot.com/_ah/api/"
            BASE_URL = "omnicure-staging.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("omnicure_test")) {
            BACKEND_APP_NAME = "omnicure_test"
            BACKEND_ROOT_URL = "https://omnicure-test.appspot.com/_ah/api/"
            BASE_URL = "omnicure-test.appspot.com"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccnsectest")) {
            BACKEND_APP_NAME = "netccnsectest"
            BACKEND_ROOT_URL = "https://omnicure-netccnsectest.appspot.com/_ah/api/"
            BASE_URL = "omnicure-netccnsectest.appspot.com"
        }
    }

    fun authorize(): GoogleCredential? {
        // load client secrets
        if (credential == null) {
            try {
                val httpTransport: HttpTransport = AndroidHttp.newCompatibleTransport()
                val jsonFactory: JsonFactory = AndroidJsonFactory()
                credential = Builder().setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .build()
            } catch (e: Exception) {
//                Log.e(TAG, "Exception:", e.getCause());
            }
        }
        return credential
    }

    fun getBackendAppName(): String? {
        return BACKEND_APP_NAME
    }

    fun getBackendRootUrl(): String? {
        return BACKEND_ROOT_URL
    }

    fun getBaseUrl(): String? {
        return BASE_URL
    }
}

