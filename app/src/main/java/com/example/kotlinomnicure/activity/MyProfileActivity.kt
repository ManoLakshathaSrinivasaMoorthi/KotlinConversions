package com.example.kotlinomnicure.activity

import android.Manifest

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.hardware.fingerprint.FingerprintManager
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.text.Html
import android.text.TextUtils
import android.view.*
import android.view.WindowManager.BadTokenException
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HomeViewModel
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityProfileBinding
import com.example.kotlinomnicure.helper.LogoutHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.URL
import java.security.KeyStore
import java.util.*
import java.util.concurrent.Executor
import javax.crypto.Cipher

class MyProfileActivity :BaseActivity(){
    // Variables
    private val TAG = MyProfileActivity::class.java.simpleName
    protected var binding: ActivityProfileBinding? = null
    protected var viewModel: HomeViewModel? = null
    protected var navHeaderView: View? = null
    var role: String? = null
    var biometricPrompt: BiometricPrompt? = null
    var dialogView: View? = null
    var editTextPassword: EditText? = null
    var alertDialog: AlertDialog? = null
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    var fingerprintonoff: Switch? = null
    var menufingerprint: LinearLayout? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var currentUser: Provider? = null
    private var strFeedbackForm = ""
    private var changeLanguage = ""
    private val keyguardManager: KeyguardManager? = null
    private val keyStore: KeyStore? = null
    private val cipher: Cipher? = null
    private val KEY_NAME = "AndroidKey"
    private var ctx:Activity=MyProfileActivity()

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Databinding and view model intialization
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        //Firebase intialization
        mFirebaseAuth = FirebaseAuth.getInstance()
        // Getting the current user from firebase
        mFirebaseUser = mFirebaseAuth!!.currentUser
        // Toolbar initialization
        initToolbar()
        //Setting up the view for the activity
        setView()

//        showPasswordPopup();
    }

    /**
     * Toolbar initialization
     */
    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding?.toolbar?.setNavigationIcon(R.drawable.btn_back_white)
        strFeedbackForm =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FEEDBACK_URL, "").toString()


        // If changeLanguage is null, Save english as a default locale in shared preference
        if (PrefUtility().getStringInPref(this,
                Constants.SharedPrefConstants.CHANGE_LANGUAGE,
                "") == null || PrefUtility().getStringInPref(this,
                Constants.SharedPrefConstants.CHANGE_LANGUAGE,
                "")?.isEmpty() == true
        )
                {
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.CHANGE_LANGUAGE,
                "en")
        }

        // Get the language from shared preference
        changeLanguage =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.CHANGE_LANGUAGE, "").toString()

    }

    /**
     * Setting up the view for the activity
     */
    private fun setView() {
        navHeaderView = binding?.menuItems
        role = PrefUtility().getRole(this)
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            val menuMyConsults = navHeaderView!!.findViewById<TextView>(R.id.menuTxtMyConsults)

            menuMyConsults.setText(getString(R.string.my_econsult_title))

            // Role and designation text setup
            val strDesignation: String? =
                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
            val strRole: String? =
                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
            if (strRole == "RD") {
                if (strDesignation != "BD") {
                    binding?.idDesignation?.setVisibility(View.VISIBLE)
                    binding?.idDesignation?.setText(strDesignation)
                }
            } else {
                binding?.idDesignation?.setVisibility(View.GONE)
            }

            val menuHandOffPatients = navHeaderView!!.findViewById<TextView>(R.id.menuTxtHandOffPatient)
            val imgHandOffPatients = navHeaderView!!.findViewById<ImageView>(R.id.icHandOffPatient)
            menuHandOffPatients.setText(getString(R.string.my_virtual_teams))
            imgHandOffPatients.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_virtual_teams))
        }
        val menuHandOffPatients =
            navHeaderView!!.findViewById<LinearLayout>(R.id.menuHandoffPatients)
        val menuVirtualTeam = navHeaderView!!.findViewById<LinearLayout>(R.id.menuVirtualTeam)
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            menuVirtualTeam.visibility = View.VISIBLE
            menuHandOffPatients.visibility = View.GONE
        } else {
            menuVirtualTeam.visibility = View.GONE
            menuHandOffPatients.visibility = View.VISIBLE
        }
        // Setting up the version name based on the environment
        setVersion()
        // Handling the header view with options
        handleDrawerHeaderView()
    }

    /**
     * Handling the header view with options
     */
    private fun handleDrawerHeaderView() {
        val name: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        val hospitalName: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.HOSPITAL_NAME, "")
        //My eConsults
        val menuMyConsultsView = navHeaderView!!.findViewById<LinearLayout>(R.id.menuMyConsults)
        //Handoff Patients
        val menuHandOffPatients =
            navHeaderView!!.findViewById<LinearLayout>(R.id.menuHandoffPatients)
        //Training Materials
        val menuTrainingMaterials =
            navHeaderView!!.findViewById<LinearLayout>(R.id.menuTrainingMaterials)
        // Terms
        val menuEulaTerms = navHeaderView!!.findViewById<LinearLayout>(R.id.menuEulaTerms)
        //System alerts
        val menuSystemAlert = navHeaderView!!.findViewById<LinearLayout>(R.id.menuSystemAlert)
        //ContactAdmin
        val menuContactAdmin = navHeaderView!!.findViewById<LinearLayout>(R.id.menuContactAdmin)
        //VirtualTeam
        val menuVirtualTeam = navHeaderView!!.findViewById<LinearLayout>(R.id.menuVirtualTeam)
        //Change Password
        val menuChangePassword = navHeaderView!!.findViewById<LinearLayout>(R.id.menuChangePassword)
        // Change Language
        val menuChangeLanguage = navHeaderView!!.findViewById<LinearLayout>(R.id.menuChangeLanguage)
        //Custom notification
        val menuCustomNotification =
            navHeaderView!!.findViewById<LinearLayout>(R.id.menuCustomNotification)
        //Feedback
        val menuFeedbackView = navHeaderView!!.findViewById<LinearLayout>(R.id.menuFeedback)
        //Signout
        val menuSignoutView = navHeaderView!!.findViewById<LinearLayout>(R.id.menuSignout)
        // Setting up Name, Email, Role & Image of the user
        menufingerprint = navHeaderView!!.findViewById(R.id.menufingerprint)
        fingerprintonoff = navHeaderView!!.findViewById(R.id.fingerprintonoff)
        val imageURL: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.PROFILE_IMG_URL, "")
        val email: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")

        if (!TextUtils.isEmpty(name)) {
            binding?.idName?.setText(name)
        }
        // Hospital Name
        if (!TextUtils.isEmpty(hospitalName)) {
            binding?.idHospitalName?.setText(hospitalName)
        }
        // Display the imageview based on the image URL received
        if (!TextUtils.isEmpty(imageURL)) {
            binding?.idProfileImg?.setVisibility(View.VISIBLE)
            binding?.defaultImageView?.setVisibility(View.GONE)
            binding?.cameraIcon?.setVisibility(View.GONE)
            //AsyncTask methodology for downloading the image and displaying it to the user with handling
            if (imageURL != null) {
                ImageLoader(this, imageURL).execute()
            }
        } else {
            binding?.idProfileImg?.setVisibility(View.GONE)
            binding?.defaultImageView?.setVisibility(View.VISIBLE)
            binding?.cameraIcon?.setVisibility(View.VISIBLE)
            binding?.defaultImageView?.setText(name?.let { UtilityMethods().getNameText(it) })
        }
        // Email
        if (!TextUtils.isEmpty(email)) {
            binding?.idEmail?.setVisibility(View.VISIBLE)
            binding?.idEmail?.setText(email)
        } else {
            binding?.idEmail?.setVisibility(View.GONE)
        }
        // Profile image click listener
        binding?.idImageLayout?.setOnClickListener { view ->
            // Handling the multi click event
            handleMultipleClick(view)
            //Select image alert dialog with "Take photo" & "Select Image" options
            selectImage()
        }

        // Role text setup
        if (!TextUtils.isEmpty(role)) {
            if (role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
                binding?.idRole?.setText(R.string.bedside_provider)
                binding?.idHospitalName?.visibility = View.VISIBLE
            } else if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                binding?.idRole?.setText(Html.fromHtml("<b>" + getString(R.string.remote_provider).toString() + "</b>"))
                binding?.idHospitalName?.visibility = View.GONE
            }
        }
        // Setting up the home provideras role
        if (getCurrentUser() != null) {
            if (!TextUtils.isEmpty(getCurrentUser()?.getLcpType())) {
                if (getCurrentUser()?.getLcpType()
                        .equals(Constants.KeyHardcodeToken.LCP_TYPE_HOME,ignoreCase = true)
                ) {
                    binding?.idHospitalName?.setVisibility(View.GONE)
                    binding?.idRole?.setText("Home Provider")
                }
            }
        }
        // My eConsults click listener
        menuMyConsultsView.setOnClickListener { view: View ->

            // Handling the multi click event
            handleMultipleClick(view)
            startActivity(Intent(this, HomeActivity::class.java))
        }
        // Change language click listener
        menuChangeLanguage.setOnClickListener { view: View ->

            // Handling the multi click event
            handleMultipleClick(view)
            //Change language bottom sheet dialog
            changeLanguageDialog(this)
        }
        // Custom notification click listener
        menuCustomNotification.setOnClickListener { view: View ->

            // Handling the multi click event
            handleMultipleClick(view)
            //Change language bottom sheet dialog
            startActivity(Intent(this, CustomNotificationActivity::class.java))
        }
        // Change password click listener
        menuChangePassword.setOnClickListener { view: View ->

            // Handling the multi click event
            handleMultipleClick(view)
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
        // Handoff patient click listener
        menuHandOffPatients.setOnClickListener { view: View ->

            // Handling the multi click event
            handleMultipleClick(view)
            startActivity(Intent(this, HandOffPatientsActivity::class.java))
        }
        // Training materials click listener
        menuTrainingMaterials.setOnClickListener { view: View ->

            // Intent based on the URI received from the shared preference
            val url: String? =
                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TUTORIAL_URL, "")
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        // EULA click listener
        menuEulaTerms.setOnClickListener { view: View ->

            val intent = Intent(this, TermsAndConditionsActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.SHOW_TERMS_BUTTON, false)
            startActivity(intent)
        }
        // System alert click listener
        menuSystemAlert.setOnClickListener { view: View ->

            startActivity(Intent(this, SystemAlertActivity::class.java))
        }

        // Virtual team click listener
        menuVirtualTeam.setOnClickListener { view: View ->

            startActivity(Intent(this, MyVirtualTeamsActivity::class.java))
        }
        //Contact admin click listener
        menuContactAdmin.setOnClickListener { view: View ->

            startActivity(Intent(this, ContactAdminActivity::class.java))
        }
        // Feedback view click listener
        menuFeedbackView.setOnClickListener { view: View ->

            //            Uri uri = Uri.parse(Constants.FEEDBACK_URL); // missing 'http://' will cause crashed
            val uri =
                Uri.parse(strFeedbackForm) // missing 'http://' will cause crashed
            // Intent based on the URI received from "strFeedbackForm"
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        // Signout click listener
        menuSignoutView.setOnClickListener { view: View ->

            // Redirecting to logout helper class to do logout handlings
            LogoutHelper(this, binding?.getRoot()).doLogout()
        }


        //check fp
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //generateKey();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //need to check

                checkfingerprintabove29()
            } else {
                belowversionV9()
            }
        }
        val finerprintstate: Boolean = PrefUtility().getBooleanInPref(this,
            Constants.SharedPrefConstants.FINGERPRINTFLAG,
            false)
        if (finerprintstate) {
            fingerprintonoff!!.setChecked(true)
        } else {
            fingerprintonoff!!.setChecked(false)
        }
        fingerprintonoff!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {

            } else {

                PrefUtility().saveBooleanInPref(this,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    false)
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.PASSWORD,
                    "")
                CustomSnackBar.make(binding?.container,
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.authdisable),
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()

            }
        })
    }

    fun checkfingerprintabove29() {
        val biometricManager = BiometricManager.from(this)
        menufingerprint!!.visibility = View.VISIBLE
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this, Constants.SharedPrefConstants.PASSWORD, "")
            menufingerprint!!.visibility = View.GONE
        } else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD,
                "")
            menufingerprint!!.visibility = View.GONE
        } else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            menufingerprint!!.visibility = View.VISIBLE
        }
    }

    fun belowversionV9() {
        menufingerprint!!.visibility = View.VISIBLE
        val fingerprintManager2 =
            getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager?
        if (fingerprintManager2 == null) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD,
                "")
            menufingerprint!!.visibility = View.GONE
        } else if (!fingerprintManager2.isHardwareDetected) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD, "")
            menufingerprint!!.visibility = View.GONE
        } else if (!fingerprintManager2.hasEnrolledFingerprints()) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD,
                "")
            menufingerprint!!.visibility = View.GONE
        } else {
            menufingerprint!!.visibility = View.VISIBLE
        }
    }

    private fun showPasswordPopup() {
//        Log.d(TAG, "showPasswordPopup : ");
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(R.id.content)
        dialogView = LayoutInflater.from(getApplicationContext())
            .inflate(R.layout.password_dialog, viewGroup, false)
        val btnContinue = dialogView!!.findViewById<Button>(R.id.buttonContinue)
        btnContinue.text = "Enable"
        val btnSignOut = dialogView!!.findViewById<Button>(R.id.buttonSignOut)
        btnSignOut.text = "Cancel"
        editTextPassword = dialogView!!.findViewById(R.id.editTextPassword)
        val passwordVisibility = dialogView!!.findViewById<ImageView>(R.id.password_visibility)
        passwordVisibility.setImageResource(R.drawable.ic_visibility)
        editTextPassword!!.setTypeface(Typeface.DEFAULT)
        builder.setView(dialogView)
        passwordVisibility.setOnClickListener {
            ValidationUtil().passwordVisibility(editTextPassword!!,
                passwordVisibility)
        }
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        if (!UtilityMethods().isDemoTestServer() && !UtilityMethods().isQaTestServer() && !UtilityMethods().isNetccnAutoTestServer()) {
            alertDialog!!.window!!.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE)
        }
        btnContinue.setOnClickListener {
            handleMultipleClick(btnContinue)

            if (ValidationUtil().checkEdittext(editTextPassword) == true) {
                checkPasswordApi(editTextPassword!!.getText().toString())
            }


        }
        btnSignOut.setOnClickListener {
            handleMultipleClick(btnSignOut)
            alertDialog!!.dismiss()
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD,
                "")
            fingerprintonoff!!.isChecked = false


        }
        alertDialog!!.show()
    }

    private fun checkPasswordApi(password: String) {
        val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        val strEmail: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        viewModel?.checkPassword(strEmail, password, token)?.observe(this) { commonResponse ->
            dismissProgressBar()
            //            Log.i(TAG, "Check password response " + commonResponse);
            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                EncUtil().generateKey(this)
                val encryptpassword: String? = EncUtil().encrypt(this, password)
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.PASSWORD,
                    encryptpassword)
                val decrypt: String? = EncUtil().decrypt(this, encryptpassword)
                //                System.out.println("decrypt" == decrypt);
                alertDialog!!.dismiss()
                val auth = "Touch ID has been successfully enabled"

                PrefUtility().saveBooleanInPref(this,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    true)
                fingerprintonoff!!.isChecked = true
                //finish();
                //Do nothing
                showPasswordPopupfingerprint()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    commonResponse.getErrorMessage(),
                    Constants.API.getDocBoxPatientList)

                if (commonResponse.getErrorId() === 0) {
                    CustomSnackBar.make(binding?.container,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                } else if (commonResponse.getErrorId() === 106) {
                    //finger print
                    PrefUtility().saveBooleanInPref(this,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false)
                    PrefUtility().saveStringInPref(this, Constants.SharedPrefConstants.PASSWORD, "")
                    fingerprintonoff!!.isChecked = false
                    alertDialog!!.dismiss()
                    val intent =
                        Intent(this, NotificationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    //                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.user_locked_title));
                    intent.putExtra("remoteTitle", getString(R.string.user_locked_title))
                    intent.putExtra("remoteMessage", commonResponse.getErrorMessage())
                    intent.putExtra("messageType", Constants.FCMMessageType.TEMP_LOCK)
                    startActivity(intent)
                } else {
                    CustomSnackBar.make(binding?.container,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                    //  alertDialog.dismiss();
                }
            }
        }
    }


    private fun showPasswordPopupfingerprint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  generateKey();
            biometric()

        }
        if (!UtilityMethods().isDemoTestServer() && !UtilityMethods().isQaTestServer() && !UtilityMethods().isNetccnAutoTestServer()) {
            alertDialog!!.window!!.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE)
        }


    }

    private fun getMainThreadExecutor(): Executor? {
        return MainThreadExecutor()
    }

    fun biometric() {
        //   Executor executor = Executors.newSingleThreadExecutor();
        val activity: FragmentActivity = this
        biometricPrompt = BiometricPrompt(activity,
            getMainThreadExecutor()!!, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_HW_NOT_PRESENT) {
                        /**
                         * The device does not have a biometric sensor.
                         */
                        /**
                         * The device does not have a biometric sensor.
                         */
                        menufingerprint!!.visibility = View.GONE
                    } else if (errorCode == BiometricPrompt.ERROR_HW_UNAVAILABLE) {
                        /**
                         * The hardware is unavailable. Try again later.
                         */
                        /**
                         * The hardware is unavailable. Try again later.
                         */
//                    binding.signinAuth.setVisibility(View.GONE);
//                    binding.signinBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    } else if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS) {
                        /**
                         * The user does not have any biometrics enrolled.
                         */
                        /**
                         * The user does not have any biometrics enrolled.
                         */
                        menufingerprint!!.visibility = View.GONE
                    } else if (errorCode == BiometricPrompt.ERROR_LOCKOUT_PERMANENT) {
                        /**
                         * The operation was canceled because ERROR_LOCKOUT occurred too many times.
                         * Biometric authentication is disabled until the user unlocks with strong authentication
                         * (PIN/Pattern/Password)
                         */
                        /**
                         * The operation was canceled because ERROR_LOCKOUT occurred too many times.
                         * Biometric authentication is disabled until the user unlocks with strong authentication
                         * (PIN/Pattern/Password)
                         */
                    } else if (errorCode == BiometricPrompt.ERROR_VENDOR) {
                        /**
                         * Hardware vendors may extend this list if there are conditions that do not fall under one of
                         * the above categories. Vendors are responsible for providing error strings for these errors.
                         * These messages are typically reserved for internal operations such as enrollment, but may be
                         * used to express vendor errors not otherwise covered. Applications are expected to show the
                         * error message string if they happen, but are advised not to rely on the message id since they
                         * will be device and vendor-specific
                         */
                        /**
                         * Hardware vendors may extend this list if there are conditions that do not fall under one of
                         * the above categories. Vendors are responsible for providing error strings for these errors.
                         * These messages are typically reserved for internal operations such as enrollment, but may be
                         * used to express vendor errors not otherwise covered. Applications are expected to show the
                         * error message string if they happen, but are advised not to rely on the message id since they
                         * will be device and vendor-specific
                         */
                    } else if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                        /**
                         * The operation was canceled because the API is locked out due to too many attempts.
                         * This occurs after 5 failed attempts, and lasts for 30 seconds.
                         */
                        /**
                         * The operation was canceled because the API is locked out due to too many attempts.
                         * This occurs after 5 failed attempts, and lasts for 30 seconds.
                         */
                        CustomSnackBar.make(binding?.container,
                            ctx,
                            CustomSnackBar.WARNING,
                            getString(R.string.Authfaildnew),
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()
                        PrefUtility().saveBooleanInPref(ctx,
                            Constants.SharedPrefConstants.FINGERPRINTFLAG,
                            false)
                        PrefUtility().saveStringInPref(ctx,
                            Constants.SharedPrefConstants.PASSWORD,
                            "")
                        stopAuth()
                        fingerprintonoff!!.isChecked = false
                        handleMultipleClicknew(fingerprintonoff)
                    } else if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // user clicked negative button
                        PrefUtility().saveBooleanInPref(ctx,
                            Constants.SharedPrefConstants.FINGERPRINTFLAG,
                            false)
                        PrefUtility().saveStringInPref(ctx,
                            Constants.SharedPrefConstants.PASSWORD, "")
                        fingerprintonoff!!.isChecked = false
                    } else if (errorCode == BiometricPrompt.ERROR_TIMEOUT) {
                        /**
                         * Error state returned when the current request has been running too long. This is intended to
                         * prevent programs from waiting for the biometric sensor indefinitely. The timeout is platform
                         * and sensor-specific, but is generally on the order of 30 seconds.
                         */
                        /**
                         * Error state returned when the current request has been running too long. This is intended to
                         * prevent programs from waiting for the biometric sensor indefinitely. The timeout is platform
                         * and sensor-specific, but is generally on the order of 30 seconds.
                         */
                        CustomSnackBar.make(dialogView,
                            ctx,
                            CustomSnackBar.WARNING,
                            getString(R.string.fpdisable),
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()
                        PrefUtility().saveBooleanInPref(ctx, Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                        PrefUtility().saveStringInPref(ctx,
                            Constants.SharedPrefConstants.PASSWORD, "")
                        stopAuth()
                        fingerprintonoff!!.isChecked = false
                        handleMultipleClicknew(fingerprintonoff)
                    } else if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        // User canceled the operation
                        PrefUtility().saveBooleanInPref(ctx, Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                        PrefUtility().saveStringInPref(ctx, Constants.SharedPrefConstants.PASSWORD, "")
                        fingerprintonoff!!.isChecked = false

                        // you can either show the dialog again here

                        // or use alternate authentication (e.g. a password) - recommended way
                    } else if (errorCode == BiometricPrompt.ERROR_CANCELED) {
                        // you can either show the dialog again here
                        CustomSnackBar.make(binding?.container,
                            ctx,
                            CustomSnackBar.WARNING,
                            getString(R.string.fpdisablenew),
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()
                        PrefUtility().saveBooleanInPref(ctx,
                            Constants.SharedPrefConstants.FINGERPRINTFLAG,
                            false)
                        PrefUtility().saveStringInPref(ctx,
                            Constants.SharedPrefConstants.PASSWORD,
                            "")
                        fingerprintonoff!!.isChecked = false

                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    val password: String? = PrefUtility().getStringInPref(ctx,
                        Constants.SharedPrefConstants.PASSWORD,
                        "")
                    EncUtil().generateKey(activity)
                    val decryptpassword: String? = EncUtil().decrypt(ctx, password)
                    if (decryptpassword != null) {
                        checkPasswordApifingerprint(decryptpassword)
                    }
                    super.onAuthenticationSucceeded(result)

                }

                override fun onAuthenticationFailed() {
                    val finerprintstate: Boolean =
                        PrefUtility().getBooleanInPref(ctx, Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                    if (finerprintstate) {
                        CustomSnackBar.make(binding?.container,
                            ctx,
                            CustomSnackBar.WARNING,
                            getString(R.string.Authfaild),
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()


                    }
                    super.onAuthenticationFailed()

                }
            })
        val promptInfo = PromptInfo.Builder()
            .setTitle("Authentication Required")
            .setNegativeButtonText("Cancel")
            .build()
        biometricPrompt!!.authenticate(promptInfo)
    }

    fun stopAuth() {
        if (biometricPrompt != null) biometricPrompt!!.cancelAuthentication()
    }

    private fun checkPasswordApifingerprint(password1: String) {
        //  showProgressBar(dialogview.getContext().getString(R.string.password_verify));
        val token: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.TOKEN,
            "")
        val strEmail: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.EMAIL,
            "")
        viewModel?.checkPassword(strEmail, password1, token)?.observe(this) { commonResponse ->

            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                EncUtil().generateKey(this)
                val encryptpassword: String? = EncUtil().encrypt(this, password1)
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.PASSWORD,
                    encryptpassword)
                PrefUtility().saveBooleanInPref(this,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    true)
                CustomSnackBar.make(binding?.container,
                    this,
                    CustomSnackBar.SUCCESS,
                    getString(R.string.auth),
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()



            } else {
                val errMsg: String?= ErrorMessages().getErrorMessage(this,
                    commonResponse.getErrorMessage(),
                    Constants.API.getDocBoxPatientList)

                if (commonResponse.getErrorId() === 0) {
                    CustomSnackBar.make(binding?.container,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                    fingerprintonoff!!.isChecked = false
                } else if (commonResponse.getErrorId() === 106) {
                    //finger print
                    val errMsg1: String = getString(R.string.temporarily_locked)
                    CustomSnackBar.make(binding?.container,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                    PrefUtility().saveBooleanInPref(this,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false)
                    PrefUtility().saveStringInPref(this,
                        Constants.SharedPrefConstants.PASSWORD, "")
                    fingerprintonoff!!.isChecked = false

                    val intent = Intent(this, NotificationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    intent.putExtra("remoteTitle", getString(R.string.user_locked_title))
                    intent.putExtra("remoteMessage", commonResponse.getErrorMessage())
                    intent.putExtra("messageType", Constants.FCMMessageType.TEMP_LOCK)
                    startActivity(intent)
                } else {
                    CustomSnackBar.make(binding?.container,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                    fingerprintonoff!!.isChecked = false

                }



            }
        }
    }

    /**
     * Select image alert dialog with "Take photo" & "Select Image" options
     */
    private fun selectImage() {
        val items =
            arrayOf<CharSequence>(getString(R.string.take_photo), getString(R.string.select_image))
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.add_edit_image)
        builder.setItems(items) { dialog: DialogInterface?, item: Int ->
            val intent = Intent(this,
                ImageCaptureActivity::class.java)
            if (items[item] == getString(R.string.take_photo)) {
                if (checkSelfPermissions()) {
                    GeeksMediaPicker.with(this)
                        .setEnableCompression(true)
                        .startCamera { mediaStoreData ->
                            val filePath: String = mediaStoreData.getMedia_path()
                            val file = File(filePath)
                            //                                Log.e("file path", filePath);
                            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                            storeImgInFirebase(myBitmap)
                            null
                        }
                } else {
                    requestPermission()
                }


            } else if (items[item] == getString(R.string.select_image)) {
                if (checkSelfPermissionsStorage()) {
                    intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                        Constants.ImageCaptureConstants.OPEN_MEDIA)
                    startActivityForResult(intent,
                        Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE)
                } else {
                    requestStoragePermission()
                }
            }
        }
        builder.show()
    }


    private fun getCurrentUser(): Provider? {
        if (currentUser == null) {
            currentUser = PrefUtility().getProviderObject(this)
        }
        return currentUser
    }

    /**
     * Request code handling based on the result of the intent for image capture and browse
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //        System.out.println("result code " + resultCode);
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.ImageCaptureConstants.START_CAMERA_REQUEST_CODE -> {
                    try {
                        //Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        var uri: Uri? = null
                        if (data != null) {
                            uri =
                                data.extras!!.getParcelable(Constants.ImageCaptureConstants.SCANNED_RESULT)
                        }
                        //Getting the bitmap from the URI
                        var imageBitmap = getBitmapFromUri(uri)
                        //Getting the resized bitmap
                        imageBitmap = getResizedBitmap(imageBitmap, Constants.IMAGE_MAX_SIZE)
                        //Getting the rotated bitmap from URI

                        //Making square image
                        val dimension = Math.min(imageBitmap!!.width, imageBitmap.height)
                        imageBitmap =
                            ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension)
                        //                        Log.e(TAG, "onActivityResult: Camera" + imageBitmap);
                        // Storing the image bitmap in firebase
                        storeImgInFirebase(imageBitmap)
                    } catch (e: Exception) {
//                        Log.e(TAG, "Exception:", e.getCause());
                    }
                }
                Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE -> {
                    try {
                        //Getting the bitmap data from the uri
                        var uri: Uri? = null
                        if (data != null) {
                            uri = data.data
                        }
                        var imageBitmap = getBitmapFromUri(uri)
                        imageBitmap = getResizedBitmap(imageBitmap, Constants.IMAGE_MAX_SIZE)

                        //Making square image
                        val dimension = Math.min(imageBitmap!!.width, imageBitmap.height)
                        imageBitmap =
                            ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension)
                        // Storing the image bitmap in firebase via file pick frorm the gallery
                        storeImgInFirebase(imageBitmap)
                    } catch (e: Exception) {
//
                    }
                }
            }
        }
    }

    /**
     * Storing the image bitmap in firebase
     *
     * @param imageBitmap
     */
    private fun storeImgInFirebase(imageBitmap: Bitmap?) {
        binding?.idProfileImagePb?.setVisibility(View.VISIBLE)
        val baos = ByteArrayOutputStream()
        imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val dataArr = baos.toByteArray()
        // File name generation
        // String fileName = "image_" + System.currentTimeMillis();
        val fcm: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FIREBASE_UID, "")
        val fileName = "$fcm.jpg"

        val key: String = PrefUtility().getProviderId(this).toString() + ""
        val storageReference = FirebaseStorage.getInstance()
            .getReference(mFirebaseUser!!.uid)
            .child(key) //.child(dir);
            .child(fileName)
        storageReference.putBytes(dataArr).addOnCompleteListener(this,
            OnCompleteListener<UploadTask.TaskSnapshot> { task ->
                if (task.isSuccessful) {
                    task.result!!.metadata!!.reference!!.downloadUrl
                        .addOnCompleteListener(this,
                            OnCompleteListener<Uri> { task ->
                                if (task.isSuccessful) {
                                    val imageURL = task.result.toString()
                                    //Set provider data with updated image
                                    PrefUtility().saveStringInPref(this,
                                        Constants.SharedPrefConstants.PROFILE_IMG_URL,
                                        imageURL)
                                    val provider = Provider()
                                    val providerId: Long? =
                                        PrefUtility().getProviderId(this)
                                    val status: String? =
                                        PrefUtility().getProviderStatus(this)
                                    provider.setId(providerId)
                                    provider.setStatus(status)
                                    provider.setProfilePicUrl(imageURL)
                                    // Updating the profile image
                                    updateProfileImage(provider, imageBitmap)

                                }
                            })
                } else {

                }
            })
    }

    /**
     * Updating the profile image by triggering the "updateProviderStatus" API call with provider data & Bitmap
     *
     * @param provider
     * @param bitmap
     */
    private fun updateProfileImage(provider: Provider, bitmap: Bitmap?) {
        if (!UtilityMethods().isInternetConnected(this)!!) {
//            UtilityMethods.showInternetError(binding.container, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.container,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            binding?.idProfileImagePb?.visibility = View.GONE
            return
        }
        //Triggering the "updateProviderStatus" API call
        viewModel?.updateProviderStatus(provider)?.observe(this) { commonResponse ->
//            Log.i(TAG, "updateProfileImage: ");
            binding?.idProfileImagePb?.visibility = View.GONE
            if (commonResponse?.status != null && commonResponse.status!!) {
                binding!!.idProfileImg.visibility = View.VISIBLE
                findViewById<TextView>(R.id.default_image_view).setVisibility(View.GONE)
                findViewById<ImageView>(R.id.cameraIcon).setVisibility(View.GONE)
                binding!!.idProfileImg.setImageBitmap(bitmap)
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    commonResponse!!.getErrorMessage(),
                    Constants.API.updateProvider)

                CustomSnackBar.make(binding!!.container,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)!!
                    .show()
            }
        }
    }




    /**
     * Getting the resized bitmap
     *
     * @param image
     * @param maxSize
     * @return
     */
    fun getResizedBitmap(image: Bitmap?, maxSize: Int): Bitmap? {
        return try {
            var width = image!!.width
            var height = image.height
            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 0) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            Bitmap.createScaledBitmap(image, width, height, true)
        } catch (e: Exception) {

            image
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val perms: MutableMap<String, Int> = HashMap()
        // Initialize the map with both permissions
        perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
        perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
        perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
        if (grantResults.size > 0) {
            for (i in permissions.indices) {
                perms[permissions[i]] = grantResults[i]
            }
            if (requestCode == Constants.PermissionCondes.CAMERA_STORAGE_PERMISSION_CODE && PackageManager.PERMISSION_GRANTED == perms[Manifest.permission.CAMERA]) {
                GeeksMediaPicker.with(this)
                    .setEnableCompression(true)
                    .startCamera { mediaStoreData ->
                        val filePath: String = mediaStoreData.getMedia_path()
                        val file = File(filePath)

                        val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                        storeImgInFirebase(myBitmap)
                        null
                    }
            } else if (requestCode == ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
                && PackageManager.PERMISSION_GRANTED == perms[Manifest.permission.READ_EXTERNAL_STORAGE]
            ) {
                val intent = Intent(this, ImageCaptureActivity::class.java)
                intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                    Constants.ImageCaptureConstants.OPEN_MEDIA)
                startActivityForResult(intent,
                    Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE)
            }
        } else {
            val permissonErr: String = getString(R.string.permission_denied)
            CustomSnackBar.make(binding?.container,
                this,
                CustomSnackBar.WARNING,
                permissonErr,
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)).toString())
            ) {
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            requestPermission()
                            requestStoragePermission()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }
            }
            else {
                Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
            }
        }
    }

    /**
     * Getting the bitmap from the URI
     *
     * @param uri
     * @return
     */
    private fun getBitmapFromUri(uri: Uri?): Bitmap? {
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        try {
            parcelFileDescriptor = uri?.let { getContentResolver().openFileDescriptor(it, "r") }
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            return BitmapFactory.decodeFileDescriptor(fileDescriptor)
        } catch (e: FileNotFoundException) {

        } finally {
            try {
                parcelFileDescriptor?.close()
            } catch (e: IOException) {

            }
        }
        return null
    }

    /**
     * Rotate the image with defined input angle
     *
     * @param source
     * @param angle
     * @return
     */
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
            matrix, true)
    }

    /**
     * Getting the rotated bitmap from URI & bitmap
     *
     * @param photoPath
     * @param bitmap
     * @return
     */
    fun getRotatedBitmap(photoPath: String?, bitmap: Bitmap): Bitmap? {
        var rotatedBitmap: Bitmap? = null
        try {
            val ei = ExifInterface(photoPath!!)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)
            rotatedBitmap =
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> bitmap
                }
        } catch (e: Exception) {

        }
        return rotatedBitmap ?: bitmap
    }

    /**
     * Adding the back button
     */
    protected override fun addBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
            getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        }
    }

    /**
     * Setting up the version name based on the environment
     */
    private fun setVersion() {
        binding?.idVersion?.setText(getVersion())
    }

    /**
     * Handling the multi click event
     *
     * @param item
     */
    private fun handleMultipleClick(item: View) {
        item.isEnabled = false
        mHandler?.postDelayed({ item.isEnabled = true }, 500)
    }

    private fun handleMultipleClicknew(item: View?) {
        item!!.isEnabled = false
        PrefUtility().saveBooleanInPref(this,
            Constants.SharedPrefConstants.MUTILPLECLICK,
            true)
        CustomSnackBar.make(binding?.container,
            this,
            CustomSnackBar.WARNING,
            getString(R.string.fpdisable),
            CustomSnackBar.TOP,
            3000,
            0)?.show()
        mHandler?.postDelayed({ item.isEnabled = true }, 30000)
        mHandler?.postDelayed({
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.MUTILPLECLICK,
                false)
        }, 30000)
    }

    /**
     * Change language bottom sheet dialog
     *
     * @param context
     */
    private fun changeLanguageDialog(context: Context) {
        val dialog = Dialog(context, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.change_language_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlEnglish = dialog.findViewById<View>(R.id.rlEnglish) as RelativeLayout
        val rlSpanish = dialog.findViewById<View>(R.id.rlSpanish) as RelativeLayout
        val imgEnglish = dialog.findViewById<View>(R.id.imgEnglish) as ImageView
        val imgSpanish = dialog.findViewById<View>(R.id.imgSpanish) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        changeLanguage =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.CHANGE_LANGUAGE, "").toString()
        when (changeLanguage) {
            "en" -> {
                imgSpanish.visibility = View.GONE
                imgEnglish.visibility = View.VISIBLE
            }
            "es" -> {
                imgEnglish.visibility = View.GONE
                imgSpanish.visibility = View.VISIBLE
            }
            else -> {
            }
        }

        // Cancel button click listener
        imgCancel.setOnClickListener { dialog.dismiss() }
        // Selecting the english as locale click listener
        rlEnglish.setOnClickListener { //Saving the english locale string to share preference
            changeLanguage = "en"
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.CHANGE_LANGUAGE,
                changeLanguage)
            //Setting the locale to english
            val locale = Locale(changeLanguage)
            Locale.setDefault(locale)
            runOnUiThread { setLocale(locale) }
            dialog.dismiss()
        }

        // Selecting the spanish as locale click listener
        rlSpanish.setOnClickListener { //Saving the spanish locale string to share preference
            changeLanguage = "es"
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.CHANGE_LANGUAGE,
                changeLanguage)
            //Setting the locale to spanish
            val locale = Locale(changeLanguage)
            Locale.setDefault(locale)
            runOnUiThread { setLocale(locale) }
            dialog.dismiss()
        }
        // Show the dialog
        if (!(context is Activity && context.isFinishing)) {
            try {
                dialog.show()
            } catch (e: BadTokenException) {

            }
        }
    }

    /**
     * Setting Locale
     *
     * @param locale
     */
    private fun setLocale(locale: Locale) {
        val resources: Resources = getResources()
        val configuration = resources.configuration
        val dm = resources.displayMetrics

        configuration.locale = locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale)
        }
        resources.updateConfiguration(configuration, dm)
        // Update the locale by refreshing the activity
        finish()
        startActivity(getIntent())
        overridePendingTransition(0, 0)
    }

    fun checkSelfPermissions(): Boolean {
        return checkSelfPermission(Manifest.permission.CAMERA,
            Constants.PermissionCondes.CAMERA_STORAGE_PERMISSION_CODE)
    }

    fun checkSelfPermissionsStorage(): Boolean {
        return checkSelfPermissionGrantedCheck(Manifest.permission.READ_EXTERNAL_STORAGE,
            ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)
    }

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {

        if (ContextCompat.checkSelfPermission(this,
                permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission),
                requestCode)
            return false
        }
        return true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
            Constants.PermissionCondes.CAMERA_STORAGE_PERMISSION_CODE)
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)
    }


    private class MainThreadExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(r: Runnable) {
            handler.post(r)
        }
    }

    /**
     * AsyncTask methodology for downloading the image and displaying it to the user with handling
     */
    private class ImageLoader internal constructor(activity: MyProfileActivity, imageURL: String) :
        AsyncTask<Void?, Void?, Bitmap?>() {
        var activityReference: WeakReference<MyProfileActivity>
        var imageURL: String
        var imageProgressBar: ProgressBar
        var imageView: ImageView
        var cameraIcon: ImageView
        var defaultImgView: TextView
        override fun onPreExecute() {
            super.onPreExecute()
            // Loader enabled
            imageProgressBar.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            defaultImgView.visibility = View.VISIBLE
            cameraIcon.visibility = View.VISIBLE
            val name: String? = activityReference.get()?.let {
                PrefUtility().getStringInPref(it,
                    Constants.SharedPrefConstants.NAME,
                    "")
            }
            defaultImgView.setText(name?.let { UtilityMethods().getNameText(it) })
        }



        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            // Populating the image view with the image bitmap
            val myProfileActivity = activityReference.get()
            if (myProfileActivity != null) {
                imageProgressBar.visibility = View.GONE
                if (bitmap != null) {
                    imageView.visibility = View.VISIBLE
                    defaultImgView.visibility = View.GONE
                    cameraIcon.visibility = View.GONE
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.visibility = View.GONE
                    defaultImgView.visibility = View.VISIBLE
                    cameraIcon.visibility = View.VISIBLE
                    val name: String? = PrefUtility().getStringInPref(myProfileActivity,
                        Constants.SharedPrefConstants.NAME,
                        "")
                    defaultImgView.setText(name?.let { UtilityMethods().getNameText(it) })
                }
            }
        }

        override fun onCancelled() {
            super.onCancelled()
        }

        init {
            activityReference = WeakReference(activity)
            this.imageURL = imageURL
            imageView = activityReference.get()!!.binding?.idProfileImg!!
            cameraIcon = activityReference.get()!!.binding?.cameraIcon!!
            imageProgressBar = activityReference.get()!!.binding?.idProfileImagePb!!
            defaultImgView = activityReference.get()!!.binding?.defaultImageView!!
        }

        override fun doInBackground(vararg params: Void?): Bitmap? {
            var bitmap: Bitmap?
            try {
                val connection = URL(imageURL).openConnection()
                connection.connectTimeout = 10000
                bitmap = BitmapFactory.decodeStream(connection.getInputStream())
            } catch (e: Exception) {
                bitmap = null

            }
            return bitmap
        }
    }


}
