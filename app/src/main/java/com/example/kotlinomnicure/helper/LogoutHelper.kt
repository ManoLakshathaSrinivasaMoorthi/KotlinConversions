package com.example.kotlinomnicure.helper

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.kotlinomnicure.activity.BaseActivity
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.LoginActivity
import com.example.kotlinomnicure.activity.NotificationActivity
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.mvp.omnicure.kotlinactivity.requestbodys.LogoutRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class LogoutHelper(notificationActivity: NotificationActivity, nothing: Nothing?) {
    private val TAG = LogoutHelper::class.java.simpleName
    private var context: Context? = null
    private var activity: BaseActivity? = null
    private var rootView: View? = null

    fun LogoutHelper(context: Context?, rootView: View?) {
        this.context = context
        this.rootView = rootView
    }

    /**
     * Logout the application by triggered by "logout" API
     */
    fun doLogout() {
        try {
            if (context is BaseActivity) {
                activity = context as BaseActivity?
            }
            if (activity == null) {
                rootView?.let {
                    UtilityMethods().showErrorSnackBar(it,
                        context!!.getString(R.string.logout_error),
                        Snackbar.LENGTH_LONG)
                }
                return
            }
            if (!context?.let { UtilityMethods().isInternetConnected(it) }!!) {
                rootView?.let { UtilityMethods().showInternetError(it, Snackbar.LENGTH_LONG) }
                return
            }
            activity!!.showProgressBar(activity!!.getString(R.string.logout_pb_msg))
            //            String token = PrefUtility.getStringInPref(context, Constants.SharedPrefConstants.TOKEN, "");\
            val userId: Long? = PrefUtility().getProviderId(context!!)
            val token: String? = PrefUtility().getToken(context!!)

            // Passing values in body for API request
            /*HashMap<String,String> bodyValues = new HashMap<>();
            bodyValues.put("userId", String.valueOf(userId));
            bodyValues.put("token",token);*/

            //sending body through data class
            val requestBody = LogoutRequestBody(token, userId)

//            Call<CommonResponse> call = ApiClient.getApi(false, false).doLogout(userId, token);
            val call: Call<CommonResponse> = ApiClient().getApi(true, true).doLogout(requestBody)
            call.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {


                    if (response.isSuccessful()) {
                        activity!!.dismissProgressBar()
                        //On successful logout - Signout from the firebase
                        onSuccessLogout()
                        // To disable/stop showing push notification to the user, when session expired
                        // If false - notfication can be displayed
                        // If true - Notification will not be displayed
                        PrefUtility().saveBooleanInPref(activity!!,
                            Constants.SharedPrefConstants.DISABLE_NOTIFICATION,
                            false)

                        //Launching the login activity
                        launchLoginActivity()
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {}
            })


        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * On successful logout - Signout from the firebase
     */
    private fun onSuccessLogout() {
        val topic: String? = UtilityMethods().getFCMTopic()
        if (topic != null) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener {

                    // Signing out the user from the firebase
                    FirebaseAuth.getInstance().signOut()
                }
        }
    }

    /**
     * Launching the login activity
     */
    private fun launchLoginActivity() {
        val mobile: String? =
            activity?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.USER_MOBILE_NO, "") }

        val email: String? =
            context?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.EMAIL, "") }
        val password: String? =
            context?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.PASSWORD, "") }
        context?.let { PrefUtility().clearAllData(it) }
        val finerprintstate: Boolean = context?.let {
            PrefUtility().getBooleanInPref(it,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
        } == true
        if (finerprintstate) {
            context?.let { PrefUtility().saveStringInPref(it, Constants.SharedPrefConstants.PASSWORD, password) }
            context?.let { PrefUtility().saveStringInPref(it, Constants.SharedPrefConstants.EMAIL, email) }
        } else {
        }
        context?.let { PrefUtility().clearRedirectValidation(it) }
        val intent = Intent(context, LoginActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity?.startActivity(intent)
        activity?.finish()
    }
}