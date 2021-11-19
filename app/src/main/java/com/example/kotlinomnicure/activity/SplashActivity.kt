package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivitySplashBinding
import com.example.kotlinomnicure.interfaces.OnNetConnectedListener
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.gson.Gson
import omnicurekotlin.example.com.userEndpoints.model.RedirectRequest
import omnicurekotlin.example.com.userEndpoints.model.User
import omnicurekotlin.example.com.userEndpoints.model.VersionInfoResponse

open class SplashActivity : BaseActivity(), OnNetConnectedListener {
    private val TAG = SplashActivity::class.java.simpleName
    protected var binding: ActivitySplashBinding? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var viewModel: SplashViewModel? = null
    private var isConnectedCalled = false
    private var strEmail: String? = null
    private  var strPassword:String? = null
    private  var strId:String? = null
    private var launchingRunnable = Runnable {
        if (!isConnectedCalled) {
            //                if (!TextUtils.isEmpty(strEmail) && !TextUtils.isEmpty(strPassword)) {
            //                    RedirectPageNavigation();
            //                } else {
            //                    launchLoginScreen();
            //                }
            val isInternet: Boolean = UtilityMethods().isInternetConnected(this)
            if (isInternet) {
                if (!TextUtils.isEmpty(strEmail) && !TextUtils.isEmpty(strPassword)) {
                    Log.e(TAG, "run: RedirectPageNavigation")
                    redirectPageNavigation()
                } else {
                    Log.e(TAG, "run: launchLoginScreen")
                    launchLoginScreen()
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance()
        strEmail = PrefUtility().getStringInPref(this, Constants.redirectValidation.EMAIL, "")
        strPassword = PrefUtility().getStringInPref(this, Constants.redirectValidation.PASSWORD, "")
        strId = java.lang.String.valueOf(PrefUtility().getLongInPref(this, Constants.redirectValidation.ID, 0))
        Log.d(TAG, "getStringInPrefValue:  " + SystemClock.elapsedRealtime() + "----" + strPassword + "----" + strId)
        getAppConfig()
        val encrypted: String? = AESUtils().encryptData("text to encrypt",
            "056f1e1b03bd587161d7d4bacc1c1296a726081a668929bb8d43403238df64bb")
        println("encryptedValue $encrypted")
        println("decryptedValue " + AESUtils().decryptData(
                "7f7bab6c9725142150ed6cf33408d06b262bdf5a2811d3339a7f92a335f2df0346000c87981ca8c8ff8c768b6a1ed3a0841407ceca3c17c32a03fb83e2133c8062238571384a3278af10e685a1a7ffe6c687742ea44360c4f9563af12d6b6f241b767a4f46f592578c93bef34697b7ed69813284e154344e10b374dbf58f381e15ce980937ba1bc2aebc7bca70976f6589b36538fee796fa471cc36356c16759c2d98be6b1a6a15bdc6e893442ce977001e265369733e00aab30f6730e93545380e140d4256c5f4fa2e0c8bea23ebfe855788b6988fd2f8e00c47cbddb51a1559b71fb9de8fbbca42f0e6123709758c0b3eb7db17dff6d3a1ffa49cde9f6e9bb8f506b58ae998c0b4dd491ec77d92654ac8a203708079470c7d9927c588a9fa830f39fa3d0ef422d45cb93e5c933c70975031afb84926a3d9e35c1e6cc90ff801595b3fd6f3a0deec94ea763d019cf7b0196195633cfb14935d372aaac37166268a6ce72cfbc0eaa3ed89c03af87a6621f0a280e0df582aa9beaddf9fe80b76b385cafcfb2a99a275b1bcda287c464a9285daea5e48506bc2f70e48356741a7a4192b8f191f2ffffa3ae4c167e23a7e2fd3b1cf1145728c5819000903d1978e2dcab66f95e5a9e898c6ffd655b2597dc5965855514f9f656abb6a1af70ceae8dc16e83f6b03fc17876cfda41debc110fa6ef6faa95ce9d623b178e257a34f01e41d9f39b5cff1b2e38441b5a10536936f13e4bd8540a9f6aed4a3f09ebaf77363afe724273068099541108ff270fe5e4b2668adb4fe835b93f9d22f96ffa154a3f0a3d978796d9d4fc1157dd0d9e08312438d9cb11298625775612ab6fda593f71f329c13e264d2bcf2ae1a5cf7ed394deb03477a75de456e7b06195cf5370db43c1dd4b385e6cdb5697c25a3416080927a422985aa46410da4625cd8021ceda1c63a0a04f467aa9cd645ccef47c12667215ef1a69ad135e7dd7d221855937b0045481b337287981dbed38ef34b9d118f8bfb026ce82322ceccbcc2355379ab4662458f972780c0dd24c08c7a2f9cbd4d358fe164b9456d540ea9f9d428c9b3230302073a51676f4a77728e7526fb8d394bdd6be44c2ff3535b19b55f9af9966",
                "056f1e1b03bd587161d7d4bacc1c1296a726081a668929bb8d43403238df64bb"))
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume:FIREBASE_REFRESH_TOKEN-->" + PrefUtility().getStringInPref(this,
                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN, ""))
        if (!TextUtils.isEmpty(PrefUtility().getStringInPref(this,
                    Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN, ""))) {
            getFirebaseIdToken()
        }
        val URIdata = intent.data
        if (URIdata != null && URIdata.scheme == "https") {
            val oobCode = URIdata.getQueryParameter("oobCode")
            verifyOOB(oobCode)
        } else {
            val isInternet: Boolean = UtilityMethods().isInternetConnected(this@SplashActivity)
            if (isInternet) {
                launchpad()
            }
            //            launchapp();
        }
    }

    private fun verifyOOB(oobCode: String?) {
        if (oobCode != null) {
            println("username $oobCode")
            mFirebaseAuth!!.verifyPasswordResetCode(oobCode)
                .addOnSuccessListener {
                    Log.i(TAG, "oob code verified")
                    val intent = Intent(this, ChangePasswordActivity::class.java)
                    intent.putExtra(Constants.IntentKeyConstants.OOB_CODE, oobCode)
                    startActivity(intent)
                }.addOnFailureListener {
                    Log.i(TAG, "oob code expired ")
                    Toast.makeText(this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                    launchpad()
                }
        } else {
            Toast.makeText(this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
            launchpad()
        }
    }

    private fun launchpad() {
        val isInternet: Boolean = UtilityMethods().isInternetConnected(this@SplashActivity)
        if (isInternet) {
            mHandler?.postDelayed(launchingRunnable, 2L * Constants.SPLASH_SCREEN_TIME)
        }
//        mHandler.postDelayed(launchingRunnable, 2L * Constants.SPLASH_SCREEN_TIME);
    }

    private fun launchLoginScreen() {
        val userID: Long? = PrefUtility().getProviderId(this)
        if (userID == -1L) {
//            Intent intent = new Intent(this, SignupActivity.class);
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            val phone: String? =
                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.USER_MOBILE_NO, "")
            //            signInFirebase(phone);
            val intent = Intent(this, MyDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun signUpFirebase(phone: String) {
        if (!UtilityMethods().isInternetConnected(this)) {
            return
        }
        val email = phone.trim { it <= ' ' } + "@omnicure.com"
        val pwd = phone.trim { it <= ' ' }
        Log.d(TAG, "signUp: $email")
        //mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
        mFirebaseAuth!!.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                val user = mFirebaseAuth!!.currentUser
                Log.d(TAG, "onComplete: " + user!!.displayName)
                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName(pwd)
                    .build()
                user.updateProfile(profile)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                //                    Toast.makeText(SplashActivity.this, "createUserWithEmailAndPassword failed.",
                //                            Toast.LENGTH_SHORT).show();
            }
            signInFirebase(phone)
            // ...
        }
    }

    private fun signInFirebase(phone: String) {
        val email = phone.trim { it <= ' ' } + "@omnicure.com"
        val pwd = phone.trim { it <= ' ' }
        Log.d(TAG, "signIn: $email")
        mFirebaseAuth?.signInWithEmailAndPassword(email, pwd)?.addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = mFirebaseAuth!!.currentUser
                Log.d(TAG, "onComplete: " + user!!.displayName)
                val intent = Intent(this, MyDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(this, getString(R.string.signin_with_email_password_failed),Toast.LENGTH_SHORT).show()
            }

            // ...
        }
    }

    /*String connectionStatus = "";
    @Override
    public void onConnectionChanged(Intent intent) {
        boolean isConnected = UtilityMethods.isInternetConnected();
        if(isConnected){
            if(!TextUtils.isEmpty(connectionStatus) && connectionStatus.equalsIgnoreCase(Constants.ConnectionStatus.Connected.toString())){
                return;
            }
            connectionStatus = Constants.ConnectionStatus.Connected.toString();
            boolean isDestroyed = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed = isDestroyed();
            }
            if(customDialog != null && customDialog.isShowing() && !isDestroyed){
                customDialog.dismiss();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchLoginScreen();
                }
            },Constants.SPLASH_SCREEN_TIME);
        }else{
            if(!TextUtils.isEmpty(connectionStatus) && connectionStatus.equalsIgnoreCase(Constants.ConnectionStatus.Disconnected.toString())){
                return;
            }
            connectionStatus = Constants.ConnectionStatus.Disconnected.toString();
            String message = getString(R.string.no_internet_dialog_message);
            customDialog = UtilityMethods.showDialog(this,getString(R.string.no_internet_dialog_title),message,false,-1,null,-1,null,-1);
        }
    }*/

    /*String connectionStatus = "";
    @Override
    public void onConnectionChanged(Intent intent) {
        boolean isConnected = UtilityMethods.isInternetConnected();
        if(isConnected){
            if(!TextUtils.isEmpty(connectionStatus) && connectionStatus.equalsIgnoreCase(Constants.ConnectionStatus.Connected.toString())){
                return;
            }
            connectionStatus = Constants.ConnectionStatus.Connected.toString();
            boolean isDestroyed = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed = isDestroyed();
            }
            if(customDialog != null && customDialog.isShowing() && !isDestroyed){
                customDialog.dismiss();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchLoginScreen();
                }
            },Constants.SPLASH_SCREEN_TIME);
        }else{
            if(!TextUtils.isEmpty(connectionStatus) && connectionStatus.equalsIgnoreCase(Constants.ConnectionStatus.Disconnected.toString())){
                return;
            }
            connectionStatus = Constants.ConnectionStatus.Disconnected.toString();
            String message = getString(R.string.no_internet_dialog_message);
            customDialog = UtilityMethods.showDialog(this,getString(R.string.no_internet_dialog_title),message,false,-1,null,-1,null,-1);
        }
    }*/
    override fun onConnected() {
        isConnectedCalled = true
        Log.i(TAG, "onConnected: $strEmail $strPassword")
        mHandler?.postDelayed({
            if (!TextUtils.isEmpty(strEmail) && !TextUtils.isEmpty(strPassword)) {
                Log.e(TAG, "run:onConnected RedirectPageNavigation")
                redirectPageNavigation()
            } else {
                Log.e(TAG, "run:onConnected launchLoginScreen")
                launchLoginScreen()
            }
        }, Constants.SPLASH_SCREEN_TIME.toLong())
    }

    private fun getAppConfig() {
        if (!UtilityMethods().isInternetConnected(this)) {
            return
        }
        viewModel?.getVersionInfo()?.observe(this) { versionInfoResponse ->
            Log.i(TAG, "getversioninfo response " + Gson().toJson(versionInfoResponse))
            if (versionInfoResponse?.getStatus() != null && versionInfoResponse.getStatus()!!) {
                onSuccessVersionInfoAPI(versionInfoResponse)
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    versionInfoResponse?.getErrorMessage(), Constants.API.getVersionInfo)
                Log.i(TAG, "getAppConfig Error : $errMsg")
            }
        }
    }

    private fun onSuccessVersionInfoAPI(response: VersionInfoResponse) {
        Log.i(TAG, "onSuccessVersionInfoAPI: Response " + Gson().toJson(response.getAppConfig()))
        if (response.getAppConfig() != null) {
//            if (response.getAppConfig().get(Constants.VersionInfoKeys.AUTO_LOGOUT_TIME) != null) {
            if (response.getAppConfig()?.getLogoutServerTimerinMilli() != null) {
//                String serverTimer = response.getAppConfig().get(Constants.VersionInfoKeys.AUTO_LOGOUT_TIME).toString();
                val serverTimer: String = response.getAppConfig()?.getLogoutServerTimerinMilli()!!
                Log.i(TAG, "Auto Logout Server Time : $serverTimer")
                PrefUtility().saveLongInPref(this,
                    Constants.SharedPrefConstants.AUTO_LOGOUT_TIME, serverTimer.toLong())
            }
            //            if (response.getAppConfig().get(Constants.VersionInfoKeys.HEALTH_MONITOR_TIMER) != null) {
            if (response.getAppConfig()?.getLogoutAppTimerinMilli() != null) {
//                String appTimer = response.getAppConfig().get(Constants.VersionInfoKeys.HEALTH_MONITOR_TIMER).toString();
                val appTimer: String = response.getAppConfig()?.getLogoutAppTimerinMilli()!!
                Log.i(TAG, "Health monitoring timer : $appTimer")
                PrefUtility().saveLongInPref(this,
                    Constants.SharedPrefConstants.HEALTH_MONITOR_TIMER, appTimer.toLong())
            }
            if (response.getAesKey() != null) {
                val aesKey: String = response.getAesKey()!!
                Log.i(TAG, "Health monitoring aesKey : $aesKey")
                PrefUtility().saveStringInPref(this, Constants.SharedPrefConstants.AES_API_KEY, aesKey)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mHandler?.removeCallbacks(launchingRunnable)
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }


    private fun redirectPageNavigation() {
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.containerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.containerLayout, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        val redirectRequest = RedirectRequest()
        redirectRequest.setEmail(strEmail)
        redirectRequest.setPassword(strPassword)
        viewModel?.redirectPage(redirectRequest)?.observe(this) { commonResponse ->
            Log.d(TAG, "Redirect Request: " + Gson().toJson(redirectRequest))
            Log.d(TAG, "Redirect Response: " + Gson().toJson(commonResponse))
            dismissProgressBar()
            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                launchLoginScreen()
                Log.d(TAG, "redirectPage 1$commonResponse")
            } else {
                var errorId = 0
                if (commonResponse.getErrorId() != null) {
                    errorId = commonResponse.getErrorId()!!
                }
                if (commonResponse.getUser() != null) {
                    redirectPage(commonResponse.getUser()!!, errorId)
                    Log.d(TAG, "redirectPage 2$commonResponse")
                    return@observe
                }
                val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse.getErrorMessage(),
                    Constants.API.register)
                //                UtilityMethods.showErrorSnackBar(binding.containerLayout, errMsg, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(binding?.containerLayout, this, CustomSnackBar.WARNING,
                    errMsg!!, CustomSnackBar.TOP, 3000, 0)?.show()
            }
        }
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {
//            UtilityMethods.showErrorSnackBar(binding.containerLayout, errMsg, Snackbar.LENGTH_LONG);
            errMsg?.let {
                CustomSnackBar.make(binding?.containerLayout, this, CustomSnackBar.WARNING,
                    it, CustomSnackBar.TOP, 3000, 0)?.show()
            }
            return false
        }
        return true
    }

    private fun redirectPage(provider: User, errorId: Int) {
        if (errorId == 101) {
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.getId())
            intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO, provider.getPhone())
            intent.putExtra(Constants.IntentKeyConstants.COUNTRY_CODE, provider.getCountryCode())
            intent.putExtra(Constants.IntentKeyConstants.FROM_PAGE, "splash")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (errorId == 102) {
            val intent = Intent(this, EmailOTPActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PROVIDER_EMAIL, provider.getEmail())
            intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.getId())
            intent.putExtra(Constants.IntentKeyConstants.FROM_PAGE, "splash")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (errorId == 103) {
            val intent = Intent(this, RegistrationSuccessActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}
