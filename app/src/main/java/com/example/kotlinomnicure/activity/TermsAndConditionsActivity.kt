package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
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
    private var binding: ActivityTermsAndConditionsBinding? = null
    private var viewModel: TermsAndConditionsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_and_conditions)
        viewModel = ViewModelProvider(this).get(TermsAndConditionsViewModel::class.java)
        setView()
    }

    private fun setView() {
        binding?.agreeBtn?.isEnabled = intent.getBooleanExtra("isSelected", false)
        if (intent.getBooleanExtra(Constants.IntentKeyConstants.SHOW_TERMS_BUTTON, true)) {
            binding?.agreeBtn?.visibility = View.VISIBLE
        } else {
            binding?.agreeBtn?.visibility = View.GONE
        }
        binding?.idBackButton?.setOnClickListener { finish() }
        binding?.agreeBtn?.setOnClickListener {
            val intent = Intent()
            intent.putExtra("agree", true)
            setResult(RESULT_OK, intent)
            finish()
        }
        binding?.scrollView?.viewTreeObserver?.addOnScrollChangedListener {
            if (binding?.scrollView?.getChildAt(0)?.bottom?.minus(120)!!
                <= binding!!.scrollView.height + binding!!.scrollView.scrollY
            ) {
                binding?.agreeBtn?.isEnabled = true
                //scroll view is at bottom
            } else {
                //scroll view is not at bottom
            }
        }
        getTermsAndConditions()
    }

    private fun getTermsAndConditions() {
        if (!UtilityMethods().isInternetConnected(this)!!) {
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
            if (termsAndConditionsResponse?.getStatus() != null && termsAndConditionsResponse.getStatus()!!) {
                if (termsAndConditionsResponse.getConfiguration() != null) {
                    binding?.agreementText?.text = Html.fromHtml(
                        termsAndConditionsResponse.getConfiguration()!!.getValue())
                    if (binding?.agreementText != null) {
                        binding?.agreementText?.movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this, java.lang.String.valueOf(termsAndConditionsResponse?.getErrorId()),
                    Constants.API.getHospital)
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                    getString(R.string.api_error), CustomSnackBar.TOP, 3000, 0
                )?.show()
            }
        }
    }


}
