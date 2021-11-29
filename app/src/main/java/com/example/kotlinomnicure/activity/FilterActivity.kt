package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityFilterBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility

class FilterActivity : AppCompatActivity() {


  //Declare the variables
    private var status: String? = "All"
    private var binding: ActivityFilterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialise the variables
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter)
        val lcpType: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.LCP_TYPE, "")
        status = intent.getStringExtra("status")
        if (lcpType == "H") {
            binding!!.radioAssigned.setVisibility(View.VISIBLE)
        } else {
            binding!!.radioAssigned.setVisibility(View.GONE)
        }
        resetRadioButton()
        initToolbar()
        binding!!.saveBtn.setOnClickListener { view ->
            val selectedId: Int = binding!!.radioGroup.getCheckedRadioButtonId()
            val radioButton: RadioButton = findViewById(selectedId)
            val intent = Intent()
            intent.putExtra("save", "true")
            intent.putExtra("status", radioButton.text.toString())
            this.setResult(RESULT_OK, intent)
            finish()
        }
    }

    //Reset Radio Button
    private fun resetRadioButton() {
        if (status == null) {
            status = "All"
        }
        if ("Active".equals(status, ignoreCase = true)) {
            binding?.radioActive?.setChecked(true)
        } else if ("Pending".equals(status, ignoreCase = true)) {
            binding?.radioPending?.setChecked(true)
        } else if ("HomeCare".equals(status, ignoreCase = true)) {
            binding?.radioAssigned?.setChecked(true)
        } else {
            binding?.radioAll?.setChecked(true)
        }
    }

    //Initialise variables
    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        binding?.toolbar?.setTitle(getString(R.string.refine_messages))
        binding?.toolbar?.setNavigationIcon(R.drawable.cross)
        binding?.toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        binding?.toolbar?.setNavigationOnClickListener { view -> finish() }
    }
}