package com.example.kotlinomnicure.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityResetPasswordBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.ResetPasswordViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.ResetPasswordRequest

class ResetPasswordActivity : BaseActivity() {

    private val TAG = ResetPasswordActivity::class.java.simpleName
    private var binding: ActivityResetPasswordBinding? = null
    private var viewModel: ResetPasswordViewModel? = null
    private var strEmail = ""
    private var strFirstName = ""
    private  var strLastName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_reset_password)
        viewModel = ViewModelProvider(this).get(ResetPasswordViewModel::class.java)
        initToolbar()
        initViews()
        initClickListener()
//        Toast.makeText(this, "RESETPASSWORD", Toast.LENGTH_SHORT).show();
    }

    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        binding?.toolbar?.title = getString(R.string.change_password)
        binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)
    }

    private fun initViews() {
        strEmail = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "").toString()
        strFirstName = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FIRST_NAME, "").toString()
        strLastName = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.LAST_NAME, "")


//        Log.d(TAG, "Reset Email" + strEmail);
        binding?.edtOldPassword?.addTextChangedListener(ValidationTextWatcher(binding?.edtOldPassword!!))
        binding?.edtNewPassword?.addTextChangedListener(ValidationTextWatcher(binding?.edtNewPassword!!))
        binding?.edtConfirmPassword?.addTextChangedListener(ValidationTextWatcher(binding?.edtConfirmPassword!!))
        binding?.btnSubmit?.isEnabled = false
        binding?.edtOldPassword?.setTypeface(Typeface.DEFAULT)
        binding?.edtNewPassword?.setTypeface(Typeface.DEFAULT)
        binding?.edtConfirmPassword?.setTypeface(Typeface.DEFAULT)
        binding?.btnSubmit?.setOnClickListener {
            doResetPassword()
            //                resetPasswordFirebase();
        }
    }

    private fun initClickListener() {
        binding?.oldPasswordInfo?.setOnClickListener {
            strLastName?.let { it1 ->
                ValidationUtil().showPasswordValidationDialog(
                    this, binding?.edtOldPassword?.text.toString(), strFirstName, it1, strEmail)
            }
        }
        binding?.newPasswordInfo?.setOnClickListener {
            strLastName?.let { it1 ->
                ValidationUtil().showPasswordValidationDialog(this, binding?.edtNewPassword?.text.toString(),
                    strFirstName, it1, strEmail)
            }
        }
        binding?.confirmPasswordInfo?.setOnClickListener {
            strLastName?.let { it1 ->
                ValidationUtil().showPasswordValidationDialog(
                    this, binding?.edtConfirmPassword?.text.toString(),
                    strFirstName, it1, strEmail
                )
            }
        }
        binding?.edtOldPassword?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding?.edtOldPassword?.getErrorMessage().equals("")) {
                    binding?.llOldPassword?.background = resources.getDrawable(R.drawable.border_black_edittext_bg)
                }
            } else {
                binding?.llOldPassword?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
                binding?.edtOldPassword?.addTextChangedListener(GenericTextWatcher(binding?.edtOldPassword!!))
                checkOldPassword(true)
            }
        }
        binding?.edtNewPassword?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding?.edtNewPassword?.getErrorMessage().equals("")) {
                    binding?.llNewPassword?.background =
                        resources.getDrawable(R.drawable.border_black_edittext_bg)
                }
            } else {
                binding?.llNewPassword?.background =
                    resources.getDrawable(R.drawable.ash_border_drawable_bg)
                binding?.edtNewPassword?.addTextChangedListener(GenericTextWatcher(binding!!.edtNewPassword))
                checkNewPassword(true)
            }
        }
        binding?.edtConfirmPassword?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding?.edtConfirmPassword?.getErrorMessage().equals("")) {
                    binding?.llConfirmPassword?.background =
                        resources.getDrawable(R.drawable.border_black_edittext_bg)
                }
            } else {
                binding?.llConfirmPassword?.background =
                    resources.getDrawable(R.drawable.ash_border_drawable_bg)
                binding?.edtConfirmPassword?.addTextChangedListener(GenericTextWatcher(binding?.edtConfirmPassword!!))
                checkConfirmPassword(true)
            }
        }
        binding?.oldPasswordVisibility?.setOnClickListener {
            if (binding?.edtOldPassword?.inputType === InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding?.edtOldPassword?.inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding?.edtOldPassword?.setTypeface(Typeface.DEFAULT)
                binding?.oldPasswordVisibility?.setImageResource(R.drawable.ic_visibility)
                if (binding?.edtOldPassword?.text != null) binding?.edtOldPassword?.text?.length?.let { it1 ->
                    binding?.edtOldPassword?.setSelection(it1)
                }
            } else {
                binding?.edtOldPassword?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding?.edtOldPassword?.setTypeface(Typeface.DEFAULT)
                binding?.oldPasswordVisibility?.setImageResource(R.drawable.ic_visibility_off)
                if (binding?.edtOldPassword?.text != null) binding?.edtOldPassword?.text?.length?.let { it1 ->
                    binding?.edtOldPassword?.setSelection(it1)
                }
            }
        }
        binding?.newPasswordVisibility?.setOnClickListener {
            binding?.edtNewPassword?.let { it1 ->
                ValidationUtil().passwordVisibility(it1, binding?.newPasswordVisibility!!)
            }
        }
        binding?.confirmPasswordVisibility?.setOnClickListener {
            binding?.edtConfirmPassword?.let { it1 ->
                ValidationUtil().passwordVisibility(it1, binding?.confirmPasswordVisibility!!)
            }
        }
    }

    fun checkOldPassword(showError: Boolean) {
        if (strLastName?.let {
                binding?.let { it1 ->
                    ValidationUtil().checkPasswordValidation(binding?.edtOldPassword?.text.toString(),
                        it1,
                        strFirstName, it, strEmail)
                }
            } != null || binding?.edtOldPassword?.text.toString() == binding?.edtNewPassword?.text.toString()
        ) {
            if (showError) {
                if (binding?.edtOldPassword?.text.toString() == binding?.edtNewPassword?.text.toString()
                ) {
                    binding?.edtNewPassword?.setErrorMessage(getString(R.string.password_match))
                    binding?.llNewPassword?.background = resources.getDrawable(R.drawable.error_edittext_bg)
                    return
                }
                strLastName?.let { it ->
                    binding?.let { it1 ->
                        ValidationUtil().checkPasswordValidation(
                            binding?.edtOldPassword?.text.toString(), it1,
                            strFirstName, it, strEmail)?.let {
                            binding?.edtOldPassword?.setErrorMessage(it)
                        }
                    }
                }
                binding?.llOldPassword?.background = resources.getDrawable(R.drawable.error_edittext_bg)
                binding?.oldPasswordInfo?.visibility = View.VISIBLE
            }
            binding?.oldPasswordVerified?.visibility = View.GONE
        } else {
            if (binding?.edtOldPassword?.text.toString() != binding?.edtNewPassword?.text.toString()
            ) {
                binding?.edtNewPassword?.setErrorMessage("")
                binding?.llNewPassword?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
            }
            binding?.edtOldPassword?.setErrorMessage("")
            binding?.llOldPassword?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
            binding?.oldPasswordInfo?.visibility = View.GONE
            binding?.oldPasswordVerified?.visibility = View.VISIBLE
        }
    }

    fun checkNewPassword(showError: Boolean) {
        when {
            strLastName?.let {
                binding?.let { it1 ->
                    ValidationUtil().checkPasswordValidation(binding?.edtNewPassword?.text.toString(),
                        it1,
                        strFirstName,
                        it, strEmail)
                }
            } != null -> {
                if (showError) {
                    binding?.let {
                        ValidationUtil().checkPasswordValidation(binding?.edtNewPassword?.text.toString(),
                            it, strFirstName, strLastName!!, strEmail)?.let {
                            binding?.edtNewPassword?.setErrorMessage(
                                it
                            )
                        }
                    }
                    binding?.llNewPassword?.background = resources.getDrawable(R.drawable.error_edittext_bg)
                    binding?.newPasswordInfo?.visibility = View.VISIBLE
                }
                binding?.newPasswordVerified?.visibility = View.GONE
            }
            binding?.edtOldPassword?.text.toString() == binding?.edtNewPassword?.text.toString() -> {
                if (showError) {
                    binding?.edtNewPassword?.setErrorMessage(getString(R.string.password_match))
                    binding?.llNewPassword?.background = resources.getDrawable(R.drawable.error_edittext_bg)
                    binding?.newPasswordInfo?.visibility = View.VISIBLE
                }
                binding?.newPasswordVerified?.visibility = View.GONE
            }
            else -> {
                binding?.edtNewPassword?.setErrorMessage("")
                binding?.llNewPassword?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
                binding?.newPasswordInfo?.visibility = View.GONE
                binding?.newPasswordVerified?.visibility = View.VISIBLE
            }
        }
    }

    fun checkConfirmPassword(showError: Boolean) {
        if (binding?.edtConfirmPassword?.text.toString().isNotEmpty()) {
            when {
                strLastName?.let {
                    binding?.let { it1 ->
                        ValidationUtil().checkPasswordValidation(
                            binding?.edtConfirmPassword?.text.toString(), it1,
                            strFirstName, it, strEmail
                        )
                    }
                } != null -> {
                    if (showError) {
                        binding?.let {
                            ValidationUtil().checkPasswordValidation(
                                binding?.edtConfirmPassword?.text.toString(), it,
                                strFirstName, strLastName!!, strEmail
                            )?.let {
                                binding?.edtConfirmPassword?.setErrorMessage(
                                    it
                                )
                            }
                        }
                        binding?.llConfirmPassword?.background = resources.getDrawable(R.drawable.error_edittext_bg)
                        binding?.confirmPasswordInfo?.visibility = View.VISIBLE
                    }
                    binding?.confirmPasswordVerified?.visibility = View.GONE
                }
                binding?.edtNewPassword?.text.toString() != binding?.edtConfirmPassword?.text.toString() -> {
                    if (showError) {
                        binding?.edtConfirmPassword?.setErrorMessage(getString(R.string.passwords_do_not_match))
                        binding?.llConfirmPassword?.background = resources.getDrawable(R.drawable.error_edittext_bg)
                        binding?.confirmPasswordInfo?.visibility = View.VISIBLE
                    }
                    binding?.confirmPasswordVerified?.visibility = View.GONE
                }
                else -> {
                    binding?.edtConfirmPassword?.setErrorMessage("")
                    binding?.llConfirmPassword?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
                    binding?.confirmPasswordInfo?.visibility = View.GONE
                    binding?.confirmPasswordVerified?.visibility = View.VISIBLE
                }
            }
        }
    }

    fun resetPasswordFirebase() {
        binding?.btnSubmit?.let { handleMultipleClick(it) }
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)!!) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.changePassword.toString()))
        val user = FirebaseAuth.getInstance().currentUser
        val email: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

//        System.out.println("emailvalue " + email + " " + binding.edtOldPassword.getText().toString());
        val credential = email?.let { EmailAuthProvider.getCredential(it, binding?.edtOldPassword?.text.toString()) }

        // Prompt the user to re-provide their sign-in credentials
        credential?.let {
            user?.reauthenticate(it)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.updatePassword(binding?.edtNewPassword?.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    reSignIn(user, binding?.edtNewPassword?.text.toString())
                                    //                                        Log.d(TAG, "Password updated");
                                } else {
                                    dismissProgressBar()
                                    CustomSnackBar.make(binding?.idContainerLayout,
                                        this,
                                        CustomSnackBar.WARNING,
                                        getString(R.string.password_not_updated),
                                        CustomSnackBar.TOP,
                                        3000,
                                        0
                                    )?.show()
                                    //                                        Log.d(TAG, "Error password not updated");
                                }
                            }
                    } else {
                        CustomSnackBar.make(
                            binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                            getString(R.string.authentication_failed_password), CustomSnackBar.TOP, 3000, 0)?.show()
                        //                            Log.d(TAG, "Error auth failed");
                    }
                }
        }
    }

    private fun reSignIn(user: FirebaseUser?, newPassword: String?) {
        val email: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        val credential = email?.let {
            EmailAuthProvider.getCredential(it, newPassword!!)
        }
        credential?.let {
            user!!.reauthenticate(it)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.getIdToken(true)
                            .addOnCompleteListener { task ->
                                dismissProgressBar()
                                if (task.isSuccessful) {
                                    val idToken = task.result!!.token
                                    //                                                System.out.println("getNewToken " + idToken);
                                    PrefUtility().saveStringInPref(this,
                                        Constants.SharedPrefConstants.FIREBASE_IDTOKEN, idToken)
                                    val show = CustomSnackBar.make(binding?.idContainerLayout, this,
                                        CustomSnackBar.SUCCESS, getString(R.string.password_changed_successfully),
                                        CustomSnackBar.TOP, 3000, 2)?.show()
                                    //finger print
                                    val finerprintstate: Boolean = PrefUtility().getBooleanInPref(this,
                                        Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                                    if (finerprintstate) {
                                        EncUtil().generateKey(this)
                                        val encryptpassword: String? = EncUtil().encrypt(this,
                                            binding?.edtNewPassword?.text.toString())
                                        PrefUtility().saveStringInPref(this,
                                            Constants.SharedPrefConstants.PASSWORD,
                                            encryptpassword
                                        )
                                    } else {
                                    }
                                    //   PrefUtility.saveBooleanInPref(ResetPasswordActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
                                    // PrefUtility.saveStringInPref(ResetPasswordActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
                                } else {
                                    CustomSnackBar.make(
                                        binding?.idContainerLayout,
                                        this@ResetPasswordActivity,
                                        CustomSnackBar.SUCCESS,
                                        getString(R.string.password_changed_successfully),
                                        CustomSnackBar.TOP,
                                        3000,
                                        0
                                    )?.show()
                                    //finger print
                                    //                                            PrefUtility.saveBooleanInPref(ResetPasswordActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
                                    //                                            PrefUtility.saveStringInPref(ResetPasswordActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
                                    val finerprintstate1: Boolean = PrefUtility().getBooleanInPref(this,
                                        Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                                    if (finerprintstate1) {
                                        EncUtil().generateKey(this)
                                        val encryptpassword1: String? = EncUtil().encrypt(this,
                                            binding?.edtNewPassword?.text.toString()
                                        )
                                        PrefUtility().saveStringInPref(this, Constants.SharedPrefConstants.PASSWORD,
                                            encryptpassword1)
                                    } else {
                                    }
                                    val intent =
                                        Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                            }
                    }
                }
        }
    }

    private fun doResetPassword() {
        binding?.btnSubmit?.let { handleMultipleClick(it) }
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)!!) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.changePassword.toString()))
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider
            .getCredential(strEmail, binding?.edtOldPassword?.text.toString())

        // Prompt the user to re-provide their sign-in credentials
        user!!.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding?.btnSubmit?.isEnabled = false
                    //                            System.out.println("user verify success");
                    callApi()
                } else {
                    dismissProgressBar()
                    CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                        getString(R.string.authentication_failed_password), CustomSnackBar.TOP, 3000, 0)?.show()
                    //                            Log.d(TAG, "Error auth failed");
                }
            }
    }

    private fun callApi() {
        val resetPasswordRequest = ResetPasswordRequest()
        if (!TextUtils.isEmpty(strEmail)) {
            resetPasswordRequest.setEmail(strEmail)
        }
        resetPasswordRequest.setOldPassword(binding?.edtOldPassword?.text.toString())
        resetPasswordRequest.setPasswordNew(binding?.edtNewPassword?.text.toString())
        resetPasswordRequest.setConfirmPassword(binding?.edtConfirmPassword?.text.toString())
        //        Log.d(TAG, "ResetPassword Request" + new Gson().toJson(resetPasswordRequest));
        viewModel?.resetPassword(resetPasswordRequest)?.observe(this) { commonResponse ->
//            Log.d(TAG, "ResetPassword Response" + new Gson().toJson(commonResponse));
            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                val user = FirebaseAuth.getInstance().currentUser
                reSignIn(user, binding?.edtNewPassword?.text.toString())
            } else {
                dismissProgressBar()
                binding?.btnSubmit?.isEnabled = true
                val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse?.getErrorMessage(),
                    Constants.API.register)
                CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                    errMsg, CustomSnackBar.TOP, 3000, 0)?.show()
            }
        }
    }

    fun onResetPasswordSuccess(response: CommonResponse?) {
        CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.SUCCESS,
            getString(R.string.password_changed_successfully), CustomSnackBar.TOP, 3000, 2)?.show()

//        final AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this, R.style.CustomAlertDialog);
//        ViewGroup viewGroup = findViewById(android.R.id.content);
//        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_alert_dialog, viewGroup, false);
//        TextView alertTitle = dialogView.findViewById(R.id.alertTitle);
//        TextView alertMsg = dialogView.findViewById(R.id.alertMessage);
//        alertTitle.setText(getString(R.string.success));
//        alertMsg.setText(getString(R.string.password_changed_successfully));
//        Button buttonOk = dialogView.findViewById(R.id.buttonOk);
//        builder.setView(dialogView);
//
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.setCancelable(false);
//        alertDialog.setCanceledOnTouchOutside(false);
//        buttonOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                launchLoginActivity();
//                alertDialog.dismiss();
//                new LogoutHelper(ResetPasswordActivity.this, binding.getRoot()).doLogout();
//
//            }
//        });
//        alertDialog.show();
    }

    private fun launchLoginActivity() {
        if (binding == null) {
            return
        }
        val mobile: String? = PrefUtility().getStringInPref(applicationContext,
            Constants.SharedPrefConstants.USER_MOBILE_NO, "")
        PrefUtility().clearAllData(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO, mobile)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                getString(R.string.error_changing_password), CustomSnackBar.TOP, 3000, 0)?.show()
            binding?.idContainerLayout?.let { UtilityMethods().showErrorSnackBar(it, errMsg, Snackbar.LENGTH_LONG) }
            return false
        }
        return true
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    fun checkButton() {
        val validOldPass: Boolean = strLastName?.let {
            ValidationUtil().checkPasswordValid(
                binding?.edtOldPassword?.text.toString(),
                strFirstName, it, strEmail
            )
        } == true
        val validNewPass: Boolean = strLastName?.let {
            ValidationUtil().checkPasswordValid(
                binding?.edtNewPassword?.text.toString(),
                strFirstName, it, strEmail
            )
        } == true
        val validConfirmPass: Boolean = strLastName?.let {
            ValidationUtil().checkPasswordValid(binding?.edtConfirmPassword?.text.toString(),
                strFirstName, it, strEmail)
        } == true
        binding?.btnSubmit?.isEnabled = (validOldPass && validNewPass && validConfirmPass && binding?.edtNewPassword?.text
            .toString() == binding?.edtConfirmPassword?.text.toString()
                && binding?.edtOldPassword?.text.toString() != binding?.edtNewPassword?.text.toString())
    }

    private class GenericTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.edtOldPassword -> ResetPasswordActivity().checkOldPassword(true)
                R.id.edtNewPassword ->ResetPasswordActivity(). checkNewPassword(true)
                R.id.edtConfirmPassword ->ResetPasswordActivity(). checkConfirmPassword(true)
            }
        }

        init {
            this.view = view
        }
    }

    private class ValidationTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.edtOldPassword -> ResetPasswordActivity().checkOldPassword(false)
                R.id.edtNewPassword -> ResetPasswordActivity().checkNewPassword(false)
                R.id.edtConfirmPassword ->ResetPasswordActivity(). checkConfirmPassword(false)
            }
           ResetPasswordActivity().checkButton()
        }

        init {
            this.view = view
        }
    }

}