package com.example.kotlinomnicure.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.kotlinomnicure.R

import com.example.kotlinomnicure.activity.LoginActivity

import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.example.kotlinomnicure.activity.BaseActivity
import com.mvp.omnicure.kotlinactivity.requestbodys.LogoutRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutHelper(activity: Activity, root: View?) {

    //Variables
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
                    UtilityMethods().showErrorSnackBar(it, context?.getString(R.string.logout_error),
                        Snackbar.LENGTH_LONG)
                }
                return
            }
            if (!context?.let { UtilityMethods().isInternetConnected(it) }!!) {
                rootView?.let { UtilityMethods().showInternetError(it, Snackbar.LENGTH_LONG) }
                return
            }
            activity?.showProgressBar(activity?.getString(R.string.logout_pb_msg))

            val userId: Long? = PrefUtility().getProviderId(context!!)
            val token: String? = PrefUtility().getToken(context!!)



            //sending body through data class
            val requestBody = LogoutRequestBody(token, userId)


            val call: Call<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?>? =
                ApiClient().getApi(true, true)?.doLogout(requestBody)
            call?.enqueue(object : Callback<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?> {



                override fun onResponse(
                    call: Call<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?>,
                    response: Response<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?>,
                ) {
                    if (response.isSuccessful) {
                        activity!!.dismissProgressBar()
                        onSuccessLogout()
                        PrefUtility().saveBooleanInPref(activity!!, Constants.SharedPrefConstants.DISABLE_NOTIFICATION, false)
                        launchLoginActivity()
                    }
                }

                override fun onFailure(
                    call: Call<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?>,
                    t: Throwable,
                ) {

                }
            })

            /* new Thread(new Runnable() {
                String errMsg = "";

                @Override
                public void run() {
                    try {
                        Long userId = PrefUtility.getProviderId(context);
                        String token = PrefUtility.getToken(context);
                        Log.i(TAG, "LOGOUT TOKEN----" + userId + "-----" + token);

                        final CommonResponse commonResponse = EndPointBuilder.getLoginEndpoints()
                                .logout(userId, token)
                                .execute();
                        Log.i(TAG, "LOGOUT RESPONSE" + commonResponse);
                        if (commonResponse != null && commonResponse.getStatus() != null) {
                            activity.dismissProgressBar();
                            //On successful logout - Signout from the firebase
                            onSuccessLogout();
                            //Launching the login activity
                            launchLoginActivity();
                        } else if (commonResponse != null && !TextUtils.isEmpty(commonResponse.getErrorMessage())) {
                            errMsg = commonResponse.getErrorMessage();
                        } else {
                            errMsg = activity.getString(R.string.logout_error);
                        }
                    } catch (SocketTimeoutException e) {
                        errMsg = activity.getString(R.string.lost_internet_retry);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception:", e.getCause());
                        System.out.println("jshdfjhsdkf ");
                        errMsg = activity.getString(R.string.logout_error);
                    }
                    if (!TextUtils.isEmpty(errMsg)) {
                        activity.dismissProgressBar();
                        if (rootView != null) {
                            UtilityMethods.showErrorSnackBar(rootView, errMsg, Snackbar.LENGTH_LONG);
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, errMsg,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            }).start();*/
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * On successful logout - Signout from the firebase
     */
    private fun onSuccessLogout() {
        val topic: String? = UtilityMethods().getFCMTopic()
        topic?.let {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(it)
                .addOnCompleteListener { //                        if (task.isSuccessful()) {
    //                            // Sign in success, update UI with the signed-in user's information
    ////                            Log.d(TAG, "topic subscribe:success");
    //
    //                        } else {
    //                            // If sign in fails, display a message to the user.
    ////                            Log.w(TAG, "topic subscribe:failure", task.getException());
    //
    //                        }
    //                        new Handler(Looper.getMainLooper()).post(new Runnable() {
    //                            @Override
    //                            public void run() {
    //                                launchLoginActivity();
    //                            }
    //                        });
                    // Signing out the user from the firebase
                    FirebaseAuth.getInstance().signOut()
                }
        }
    }

    /**
     * Launching the login activity
     */
    private fun launchLoginActivity() {
        val mobile: String? = activity?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.USER_MOBILE_NO, "") }
        NotificationHelper(context!!).clearAllNotification()
        val email: String? = PrefUtility().getStringInPref(context!!, Constants.SharedPrefConstants.EMAIL, "")
        val password: String? = PrefUtility().getStringInPref(context!!, Constants.SharedPrefConstants.PASSWORD, "")
        PrefUtility().clearAllData(context!!)
        val finerprintstate: Boolean = PrefUtility().getBooleanInPref(context!!, Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
        if (finerprintstate) {
            PrefUtility().saveStringInPref(context!!, Constants.SharedPrefConstants.PASSWORD, password)
            PrefUtility().saveStringInPref(context!!, Constants.SharedPrefConstants.EMAIL, email)
        } else {
        }
        PrefUtility().clearRedirectValidation(context!!)
        val intent = Intent(context, LoginActivity::class.java)
        //        intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO,mobile);
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity?.startActivity(intent)
        activity?.finish()
    }

}
