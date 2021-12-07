package com.example.kotlinomnicure.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.NotificationActivity
import com.example.kotlinomnicure.customview.CustomProgressDialog
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class FingerprintHandlerforpopup : FingerprintManager.AuthenticationCallback {
    private val TAG = FingerprintHandlerforpopup::class.java.simpleName
    private var context: Context? = null
    private val mFirebaseAuth: FirebaseAuth? = null
    private var progressDialog: CustomProgressDialog? = null
    var signInApiFlag = false
    protected var mHandler: Handler? = null
    var mainlayout: RelativeLayout? = null
    private var viewModel: LoginViewModel? = null
    private val fcmToken: String? = null
    private val strFeedbackForm = ""
    var dialogview: View? = null
    var alertDialog: AlertDialog? = null
    var activity: Activity? = null
    var activity1 = context as Activity?
    var redirection: String? = null
    private var cancellationSignal: CancellationSignal? = null
    var i = 0
    var fp: Switch? = null
    constructor(context: Context?, dialogview: View?, alertDialog: AlertDialog?, activity: Activity?, redirection: String?, fp: Switch?
    ) {
        this.context = context
        this.dialogview = dialogview
        this.alertDialog = alertDialog
        this.activity = activity
        this.redirection = redirection
        this.fp = fp
    }

    fun startAuth(
        fingerprintManager: FingerprintManager,
        cryptoObject: FingerprintManager.CryptoObject?
    ) {
        cancellationSignal = CancellationSignal()
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    fun stopAuth() {
        if (cancellationSignal != null && !cancellationSignal!!.isCanceled) {
            cancellationSignal!!.cancel()
        }
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        update("There was an Auth Error. $errString", false)
//        PrefUtility.saveBooleanInPref(dialogview.getContext(), Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
//        PrefUtility.saveStringInPref(dialogview.getContext(), Constants.SharedPrefConstants.PASSWORD, "");
//        //PrefUtility.saveBooleanInPref(dialogview.getContext(), Constants.SharedPrefConstants.LOCKFP, true);
////            Intent intent = new Intent(context, LoginActivity.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////            context.startActivity(intent);
//        stopAuth();
        //CustomSnackBar.make(dialogview, (FragmentActivity)activity,CustomSnackBar.WARNING, context.getString(R.string.Authfaildnew), CustomSnackBar.TOP,3000,0).show();


//        PrefUtility.saveBooleanInPref(dialogview.getContext(), Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
//        PrefUtility.saveStringInPref(dialogview.getContext(), Constants.SharedPrefConstants.PASSWORD, "");
//        PrefUtility.saveBooleanInPref(dialogview.getContext(), Constants.SharedPrefConstants.LOCKFP, true);
//
//        stopAuth();
//                    Intent intent = new Intent(context, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            context.startActivity(intent);
        //alertDialog.dismiss();
    }

    override fun onAuthenticationFailed(){

        update("Auth Failed. ", false)

        if (i == 3) {
            i = 0
            PrefUtility().saveBooleanInPref(
                dialogview!!.context,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false
            )
            PrefUtility().saveStringInPref(
                dialogview!!.context,
                Constants.SharedPrefConstants.PASSWORD,
                ""
            )
            //PrefUtility.saveBooleanInPref(dialogview.getContext(), Constants.SharedPrefConstants.LOCKFP, true);
//            Intent intent = new Intent(context, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            context.startActivity(intent);
            stopAuth()
            CustomSnackBar.make(
                dialogview,
                activity as FragmentActivity?,
                CustomSnackBar.WARNING,
                context!!.getString(R.string.Authfaildnew),
                CustomSnackBar.TOP,
                3000,
                0
            )!!
                .show()
            if (redirection == "myprofile") {
                fp!!.isChecked = false
                alertDialog!!.dismiss()
            }

            //alertDialog.dismiss();
        } else {
            val finerprintstate: Boolean = PrefUtility().getBooleanInPref(
                dialogview!!.context,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false
            )
            if (finerprintstate) {
                CustomSnackBar.make(
                    dialogview,
                    activity as FragmentActivity?,
                    CustomSnackBar.WARNING,
                    context!!.getString(R.string.Authfaild),
                    CustomSnackBar.TOP,
                    3000,
                    0
                )!!
                    .show()

                // diaalog("Authentication Failed Please Try Again");
            }
        }
        i++

    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
        update("Error: $helpString", false)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        update("You can now access the app.", true)
        //   CustomSnackBar.make(dialogview, (Activity)context,CustomSnackBar.SUCCESS,context.getString(R.string.auth),CustomSnackBar.TOP,3000,0).show();
        val password: String? = PrefUtility().getStringInPref(
            dialogview!!.context,
            Constants.SharedPrefConstants.PASSWORD,
            ""
        )
        EncUtil().generateKey(activity)
        viewModel =
            ViewModelProviders.of((activity as FragmentActivity?)!!).get(LoginViewModel::class.java)
        val decryptpassword: String? = EncUtil().decrypt(dialogview!!.context, password)
        decryptpassword?.let { checkPasswordApifingerprint(it) }
    }

    private fun checkPasswordApifingerprint(password: String) {
        //  showProgressBar(dialogview.getContext().getString(R.string.password_verify));
        val token: String? = PrefUtility().getStringInPref(
            dialogview!!.context, Constants.SharedPrefConstants.TOKEN, "")
        val strEmail: String? = PrefUtility().getStringInPref(
            dialogview!!.context,
            Constants.SharedPrefConstants.EMAIL,
            ""
        )
        strEmail?.let {
            token?.let { it1 ->
                viewModel?.checkPassword(it, password, it1)
                   ?.observe((activity as FragmentActivity?)!!) { commonResponse ->
                        // dismissProgressBar();
                        //            Log.i(TAG, "Check password response " + commonResponse);
                        if (commonResponse != null && commonResponse?.getStatus() != null && commonResponse.getStatus()) {
                            EncUtil().generateKey(dialogview!!.context)
                            val encryptpassword: String? =
                                EncUtil().encrypt(dialogview!!.context, password)
                            PrefUtility().saveStringInPref(
                                dialogview!!.context,
                                Constants.SharedPrefConstants.PASSWORD,
                                encryptpassword
                            )
                            PrefUtility().saveBooleanInPref(
                                dialogview!!.context,
                                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                                true
                            )
                            CustomSnackBar.make(
                                dialogview,
                                activity as FragmentActivity?,
                                CustomSnackBar.SUCCESS,
                                context!!.getString(R.string.auth),
                                CustomSnackBar.TOP,
                                3000,
                                0
                            )?.show()
                            if (redirection == "notification") {
                                handledisable()
                                activity!!.finish()
                            } else if (redirection == "myprofile") {
                                handledisable()
                            }


                            //Do nothing
                            //  new LogoutHelper(dialogview.getContext(), null).doLogout();
                        } else {
                            //  PrefUtility.saveBooleanInPref(dialogview.getContext(), Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
                            // PrefUtility.saveStringInPref(dialogview.getContext(), Constants.SharedPrefConstants.PASSWORD, "");
                            val errMsg: String? = ErrorMessages().getErrorMessage(
                                dialogview!!.context,
                                commonResponse.getErrorMessage(),
                                Constants.API.getDocBoxPatientList
                            )
                            // Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                            //                UtilityMethods.showErrorSnackBar(dialogView, errMsg, Snackbar.LENGTH_LONG);
                            if (commonResponse.getErrorId() === 0) {
                                CustomSnackBar.make(
                                    dialogview,
                                    activity as FragmentActivity?,
                                    CustomSnackBar.WARNING,
                                    errMsg,
                                    CustomSnackBar.TOP,
                                    3000,
                                    0
                                )?.show()
                                if (redirection == "myprofile") {
                                    fp!!.isChecked = false
                                    handledisable()
                                }
                            } else if (commonResponse.getErrorId() === 106) {
                                //finger print
                                val errMsg1 = context!!.getString(R.string.temporarily_locked)
                                CustomSnackBar.make(
                                    dialogview,
                                    activity as FragmentActivity?,
                                    CustomSnackBar.WARNING,
                                    errMsg,
                                    CustomSnackBar.TOP,
                                    3000,
                                    0
                                )?.show()
                                PrefUtility().saveBooleanInPref(
                                    dialogview!!.context,
                                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                                    false
                                )
                                PrefUtility().saveStringInPref(
                                    dialogview!!.context,
                                    Constants.SharedPrefConstants.PASSWORD,
                                    ""
                                )
                                if (redirection == "myprofile") {
                                    fp!!.isChecked = false
                                }
                                handledisable()
                                //                    Intent intent = new Intent(context, LoginActivity.class);
                                //                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //                    context.startActivity(intent);
                                val intent = Intent(context, NotificationActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                //                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.user_locked_title));
                                intent.putExtra(
                                    "remoteTitle",
                                    context!!.getString(R.string.user_locked_title)
                                )
                                intent.putExtra("remoteMessage", commonResponse.getErrorMessage())
                                intent.putExtra("messageType", Constants.FCMMessageType.TEMP_LOCK)
                                context!!.startActivity(intent)
                            } else {
                                CustomSnackBar.make(
                                    dialogview,
                                    activity as FragmentActivity?,
                                    CustomSnackBar.WARNING,
                                    errMsg,
                                    CustomSnackBar.TOP,
                                    3000,
                                    0
                                )?.show()
                                if (redirection == "myprofile") {
                                    fp!!.isChecked = false
                                    handledisable()
                                }

                                //  alertDialog.dismiss();
                            }


                            //    new LogoutHelper(NotificationActivity.this, null).doLogout();
                        }
                    }
            }
        }
    }

    private fun handledisable() {
        mHandler = Handler()
        mHandler!!.postDelayed({
            if (alertDialog != null) {
                alertDialog!!.dismiss()
            }
            //Do something after 100ms
        }, 1000)
    }

    fun dismissProgressBar() {
        try {
            var isDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if ((context as Activity?)!!.isDestroyed) {
                    isDestroyed = true
                }
            }
            if (!(context as Activity?)!!.isFinishing && !isDestroyed && progressDialog != null && progressDialog!!.isShowing()) {
                progressDialog?.dismiss()
            }
            progressDialog = null
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    fun showProgressBar(text: String?) {
        dismissProgressBar()
        try {
            progressDialog = CustomProgressDialog((context as Activity?)!!)
            progressDialog?.setText(text)
            progressDialog?.setCancelable(false)
            var isDestyoed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestyoed = (context as Activity?)!!.isDestroyed
            }
            if (!(context as Activity?)!!.isFinishing && !isDestyoed) {
                if (progressDialog != null && !progressDialog!!.isShowing()) {
                    progressDialog?.show()
                }
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }


    private fun update(s: String, b: Boolean) {
        val paraLabel = dialogview!!.findViewById<View>(R.id.paraLabel) as TextView
        val imageView = dialogview!!.findViewById<View>(R.id.fingerprintImage) as ImageView
        mainlayout = dialogview!!.findViewById<View>(R.id.mainlayout) as RelativeLayout
        paraLabel.text = s
        if (b == false) {
            paraLabel.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        } else {
            paraLabel.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
            imageView.setImageResource(R.mipmap.ic_launcher)
        }
    }


}
