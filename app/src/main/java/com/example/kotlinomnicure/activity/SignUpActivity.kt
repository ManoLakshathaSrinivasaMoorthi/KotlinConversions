package com.mvp.omnicure.kotlinactivity.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivitySignupBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.BaseActivity
import com.example.dailytasksamplepoc.kotlinomnicure.activity.LoginActivity
import com.example.dailytasksamplepoc.kotlinomnicure.activity.PatientAppointmentActivity
import com.example.dailytasksamplepoc.kotlinomnicure.utils.Buildconfic
import com.example.kotlinomnicure.utils.Constants


class SignUpActivity : BaseActivity(){

    //Declare the variables
    private val TAG = SignUpActivity::class.java.simpleName
    private var binding: ActivitySignupBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        initView()
    }

    //Initialise the Views
    private fun initView() {

        //appname
        binding!!.idWelcomeText.text =
            String.format(getString(R.string.welcome_to_omnicurenow), Buildconfic.value())


        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        val alreadySignupTxt = String.format(
            getString(R.string.already_have_an_account) + "<font color='#%s'><b>" + " " + getString(
                R.string.log_in
            ) + "</b></font>", сolorString
        )
        binding!!.alreadySigninText.text = Html.fromHtml(alreadySignupTxt)
        setOnClickListeners()
    }

    //set click listeners
    private fun setOnClickListeners() {
        binding!!.remoteProviderContainer.setOnClickListener {

            handleMultipleClick(binding!!.remoteProviderContainer)
            val intent = Intent(
                this,
                ActivityLocalCareProviderSignUpFirst::class.java
            )
            intent.putExtra(Constants.SharedPrefConstants.ROLE, "RP")
            startActivity(intent)
        }
        binding!!.localCareProviderContainer.setOnClickListener {
            handleMultipleClick(binding!!.localCareProviderContainer)
            val intent = Intent(
                this,
                ActivityLocalCareProviderSignUpFirst::class.java
            )
            intent.putExtra(Constants.SharedPrefConstants.ROLE, "BP")
            startActivity(intent)
        }
        binding!!.patientContainer.setOnClickListener {
            handleMultipleClick(binding!!.patientContainer)
            val intent = Intent(this, PatientAppointmentActivity::class.java)
            startActivity(intent)
        }
        binding!!.alreadySigninText.setOnClickListener {
            handleMultipleClick(binding!!.alreadySigninText)
            Log.d(TAG, "onClick: of already signin ")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    //On multiple click ,button should be disable for 5seconds
    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed(Runnable { view.isEnabled = true }, 500)
    }

}