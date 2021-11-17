package com.example.kotlinomnicure.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityTermsAndConditionsBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.viewmodel.TermsAndConditionsViewModel
import com.google.gson.Gson

class TermsAndConditionsActivity : BaseActivity() {

    private val TAG = TermsAndConditionsActivity::class.java.simpleName
    protected var binding: ActivityTermsAndConditionsBinding? = null
    protected var viewModel: TermsAndConditionsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_and_conditions)
        viewModel = ViewModelProviders.of(this).get(TermsAndConditionsViewModel::class.java)
        setView()
    }

    private fun setView() {
        if (intent.getBooleanExtra("isSelected", false)) {
            binding?.agreeBtn?.setEnabled(true)
        } else {
            binding?.agreeBtn?.setEnabled(false)
        }
        if (intent.getBooleanExtra(Constants.IntentKeyConstants.SHOW_TERMS_BUTTON, true)) {
            binding?.agreeBtn?.setVisibility(View.VISIBLE)
        } else {
            binding?.agreeBtn?.setVisibility(View.GONE)
        }
        binding?.idBackButton?.setOnClickListener { view -> finish() }
        binding?.agreeBtn?.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.putExtra("agree", true)
            setResult(RESULT_OK, intent)
            finish()
        })
        binding?.scrollView?.getViewTreeObserver()?.addOnScrollChangedListener(OnScrollChangedListener {
                if (binding?.scrollView?.getChildAt(0)?.getBottom()?.minus(120)!!
                    <= binding!!.scrollView.getHeight() + binding!!.scrollView.getScrollY()
                ) {
                    binding?.agreeBtn?.setEnabled(true)
                    //scroll view is at bottom
                } else {
                    //scroll view is not at bottom
                }
            })
        getTermsAndConditions()
    }

    private fun getTermsAndConditions() {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        showProgressBar()
        viewModel?.getTerms()?.observe(this) { termsAndConditionsResponse ->
            dismissProgressBar()
            Log.d(TAG, "remote provider data" + termsAndConditionsResponse?.getConfiguration())
            Log.d(TAG, "remote provider data>>" + Gson().toJson(termsAndConditionsResponse))
            if (termsAndConditionsResponse != null && termsAndConditionsResponse.getStatus() != null && termsAndConditionsResponse.getStatus()!!) {
                if (termsAndConditionsResponse.getConfiguration() != null) {
                    binding?.agreementText?.setText(Html.fromHtml(
                        termsAndConditionsResponse.getConfiguration()!!.getValue()))
                    if (binding?.agreementText != null) {
                        binding?.agreementText?.setMovementMethod(LinkMovementMethod.getInstance())
                    }
                }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this@TermsAndConditionsActivity,
                    java.lang.String.valueOf(termsAndConditionsResponse?.getErrorId()),
                    Constants.API.getHospital)
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                    getString(R.string.api_error), CustomSnackBar.TOP, 3000, 0
                )?.show()
            }
        }
    }


}
