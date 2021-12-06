package com.example.kotlinomnicure.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.BaseActivity
import com.example.kotlinomnicure.activity.LoginActivity
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception

class LogoutHelp {
    private val TAG = LogoutHelp::class.java.simpleName
    private var context: Context? = null
    private var activity: BaseActivity? = null
    private var rootView: View? = null

    fun LogoutHelp(context: Context?, rootView: View?) {
        this.context = context
        this.rootView = rootView
    }

    fun doLogout() {
        try {
            if (context is BaseActivity) {
                activity = context as BaseActivity?
            }
            if (activity == null) {
                rootView?.let {
                    UtilityMethods().showErrorSnackBar(it, context?.getString(R.string.logout_error), Snackbar.LENGTH_LONG)
                }
                return
            }
            if (!context?.let { UtilityMethods().isInternetConnected(it) }!!) {
                rootView?.let { UtilityMethods().showInternetError(it, Snackbar.LENGTH_LONG) }
                return
            }
            activity?.showProgressBar(activity?.getString(R.string.loading))
            val userId: Long? = PrefUtility().getProviderId(context!!)
            val token: String? = PrefUtility().getToken(context!!)

//            Log.i(TAG, "Logout API Called...");
            /*new Thread(new Runnable() {
                String errMsg = "";

                @Override
                public void run() {
                    try {
                        final CommonResponse commonResponse = EndPointBuilder.getLoginEndpoints()
                                .logout(userId, token)
                                .execute();
                        if (commonResponse != null && commonResponse.getStatus() != null) {
                            activity.dismissProgressBar();
                            onSuccessLogout();
                        } else if (commonResponse != null && !TextUtils.isEmpty(commonResponse.getErrorMessage())) {
                            errMsg = commonResponse.getErrorMessage();
                        } else {
                            errMsg = activity.getString(R.string.logout_error);
                        }
                    } catch (SocketTimeoutException e) {
                        errMsg = activity.getString(R.string.lost_internet_retry);
                    } catch (Exception e) {
                        errMsg = activity.getString(R.string.logout_error);
                    }
                    if (!TextUtils.isEmpty(errMsg)) {
                        activity.dismissProgressBar();
                        if (rootView != null) {
                            UtilityMethods.showInternetError(rootView, Snackbar.LENGTH_LONG);
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, activity.getString(R.string.no_internet_connectivity),
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
                    Handler(Looper.getMainLooper()).post { launchLoginActivity() }
                }
        }
    }

    private fun launchLoginActivity() {
        val mobile: String? = activity?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.USER_MOBILE_NO, "") }
     //   context as Activity?. let { NotificationHelper(it).clearAllNotification() }
        PrefUtility().clearAllData(context as Activity)
        val intent = Intent(context, LoginActivity::class.java)
        //        intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO,mobile);
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity?.startActivity(intent)
        activity?.finish()
    }

}
