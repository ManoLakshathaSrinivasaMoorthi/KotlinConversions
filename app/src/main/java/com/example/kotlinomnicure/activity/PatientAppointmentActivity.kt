package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityPatientAppointmentBinding
import com.example.kotlinomnicure.helper.MobileNumberFormatter
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.utils.ValidationUtil
import com.example.kotlinomnicure.viewmodel.AppointmentViewModel
import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import java.util.*

class PatientAppointmentActivity : BaseActivity() {
    private val TAG = PatientAppointmentActivity::class.java.simpleName
    var relation = ArrayList<String>()
    private var binding: ActivityPatientAppointmentBinding? = null
    private var viewModel: AppointmentViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_appointment)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        setOnClickListener()
        binding?.addRelativeCheckBox?.setOnClickListener {
            if (binding?.addRelativeCheckBox?.isChecked == true) {
                binding?.addRelativeContainer?.visibility = View.VISIBLE
            } else {
                binding?.addRelativeContainer?.visibility = View.GONE
            }
        }
        relation.add(getString(R.string.relation_to_patient))
        relation.add(getString(R.string.parent))
        relation.add(getString(R.string.spouse))
        relation.add(getString(R.string.sibbling))
        relation.add(getString(R.string.relative))
        relation.add(getString(R.string.son_daughter))
        val relativeAdapter = ArrayAdapter(this, R.layout.spinner_custom_text, relation)
        binding?.idSpinnerRelative?.adapter = relativeAdapter
        binding?.idSpinnerRelative?.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
                try {
                    val spinnerText = view as TextView
                    val providerType = relation[position]
                    binding!!.idSpinnerRelative.tag = position
                    if (spinnerText != null) {
                        spinnerText.maxLines = 1
                        if (position != 0) {
                            UtilityMethods().setTextViewColor(PatientAppointmentActivity(), spinnerText, R.color.white)
                            binding?.idSpinnerRelative?.let {
                                UtilityMethods().setDrawableBackground(this, it, R.drawable.spinner_drawable_selected)
                            }
                        } else {
                            UtilityMethods().setTextViewColor(PatientAppointmentActivity(), spinnerText, R.color.gray_500)
                            binding?.idSpinnerRelative?.let {
                                UtilityMethods().setDrawableBackground(this, it, R.drawable.spinner_drawable)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        binding?.editTextPassword?.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action === MotionEvent.ACTION_DOWN) {
                if (event.rawX >= binding?.editTextPassword!!.right - binding?.editTextPassword!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    if (binding?.editTextPassword?.inputType === InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        binding?.editTextPassword?.inputType = InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding?.editTextPassword?.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.drawable.ic_visibility, 0)
                        binding?.editTextPassword?.setSelection(binding!!.editTextPassword.text.length)
                    } else {
                        binding?.editTextPassword?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding?.editTextPassword?.setCompoundDrawablesWithIntrinsicBounds(
                                0, 0, R.drawable.ic_visibility_off, 0)
                        binding?.editTextPassword?.setSelection(binding!!.editTextPassword.text.length)
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun setOnClickListener() {
        binding?.idBackButton?.setOnClickListener { finish() }
        binding?.idDob?.setOnClickListener { selectDOB() }
        binding?.idPhoneNumber?.addTextChangedListener(object : TextWatcher {
            val FIRST_SEP_LENGTH = 4
            val SECOND_SEP_LENGTH = 8
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence == null || charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    return
                } else {
                    val str = charSequence.toString()
                    MobileNumberFormatter().formatMobileNumber(str, binding?.idPhoneNumber!!, FIRST_SEP_LENGTH,
                            SECOND_SEP_LENGTH)
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' }.isEmpty()) {
                    binding!!.countryStdCode.visibility = View.GONE
                } else {
                    binding!!.countryStdCode.visibility = View.VISIBLE
                }
            }
        })
        try {
            val agreementTxt = binding?.agreementCheckBox?.text.toString()
            val ss = SpannableString(agreementTxt)
            val drawableBG = binding?.agreementCheckBox?.background
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    Log.d(TAG, "onClick: of user agreement")
                    binding?.agreementCheckBox?.isEnabled = false
                    binding?.agreementCheckBox?.background = resources.getDrawable(R.drawable.transparent_bg)
                    binding?.agreementCheckBox?.isEnabled = true
                    val uri = Uri.parse(Constants.USER_AGREEMENT_LINK)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }

            }
            ss.setSpan(clickableSpan, agreementTxt.indexOf(getString(R.string.agreement)), agreementTxt.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(binding?.agreementCheckBox?.currentTextColor?.let { ForegroundColorSpan(it) },
                    agreementTxt.indexOf(getString(R.string.agreement)), agreementTxt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding?.agreementCheckBox?.text = ss
            binding?.agreementCheckBox?.movementMethod = LinkMovementMethod.getInstance()
            binding?.agreementCheckBox?.setOnCheckedChangeListener { _, isCheked ->
                binding?.agreementCheckBox?.background = drawableBG
                val color = if (isCheked) R.color.white else R.color.gray_500
                ss.setSpan(ForegroundColorSpan(resources.getColor(color)),
                        agreementTxt.indexOf(getString(R.string.agreement)), agreementTxt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding?.agreementCheckBox?.text = ss
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding?.submitBtn?.setOnClickListener { onClickSubmit() }
    }

    @SuppressLint("SetTextI18n")
    private fun selectDOB() {
        binding!!.idDob.isEnabled = false
        val mcurrentDate = Calendar.getInstance()
        var mYear = mcurrentDate[Calendar.YEAR]
        var mMonth = mcurrentDate[Calendar.MONTH]
        var mDay = mcurrentDate[Calendar.DAY_OF_MONTH]
        if (binding?.idDob?.tag != null && binding?.idDob?.tag.toString().isNotEmpty()) {
            val dob = binding!!.idDob.tag.toString()
            val dobArr = dob.split("/".toRegex()).toTypedArray()
            mMonth = dobArr[0].toInt() - 1
            mDay = dobArr[1].toInt()
            mYear = dobArr[2].toInt()
        }
        val mDatePicker = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, { datepicker, selectedyear, selectedmonth, selectedday ->
            val selectedDate = Calendar.getInstance()
            selectedDate[selectedyear, selectedmonth] = selectedday
            binding!!.idDob.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_calendar, 0)
            binding!!.idDob.setText((selectedmonth + 1).toString() + "/" + selectedday + "/" + selectedyear)
            binding!!.idDob.tag = (selectedmonth + 1).toString() + "/" + selectedday + "/" + selectedyear
            binding!!.idDob.isEnabled = true
        }, mYear, mMonth, mDay)
        mDatePicker.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDatePicker.datePicker.maxDate = System.currentTimeMillis()
        mDatePicker.show()
        mDatePicker.setOnDismissListener { binding!!.idDob.isEnabled = true }
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {
//            UtilityMethods.showErrorSnackBar(binding.getRoot(), errMsg, Snackbar.LENGTH_LONG);
            errMsg?.let {
                CustomSnackBar.make(binding?.root, this, CustomSnackBar.WARNING, it,
                        CustomSnackBar.TOP, 3000, 0)
            }!!
                .show()
            return false
        }
        return true
    }

    private fun createAppointmentObject(): Appointment {
        val appointment = Appointment()
        appointment.setFname(binding?.idFirstName?.text.toString().trim())
        appointment.setLname(binding?.idLastName?.text.toString().trim())
        if (!TextUtils.isEmpty(binding?.idEmailId?.text.toString().trim())) {
            appointment.setEmail(binding?.idEmailId?.text.toString().trim())
        } else {
            appointment.setEmail(null)
        }
        appointment.setDob(getDOBLongFormat(binding?.idDob?.text.toString()))
        val dob: List<String> = binding?.idDob?.text.toString().split("/")
        val month: String? = UtilityMethods().getMonthName(dob[0].toInt())
        val day = dob[1].toInt()
        val year = dob[2].toInt()
        appointment.setDobMonth(month)
        appointment.setDobDay(day)
        appointment.setDobYear(year)
        appointment.setPhone(binding?.idPhoneNumber?.text.toString().trim().replace("-", ""))
        //        appointment.setNote(binding.messageTxt.getText().toString().trim());
        if (binding?.radioGrp?.checkedRadioButtonId === R.id.radioBtnMale) {
            appointment.setGender(Constants.Gender.Male.toString())
        } else if (binding?.radioGrp?.checkedRadioButtonId === R.id.radioBtnFemale) {
            appointment.setGender(Constants.Gender.Female.toString())
        }
        return appointment
    }

    private fun onClickSubmit() {
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.root, this@PatientAppointmentActivity, CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000,
                    0
            )!!
                .show()
            return
        }
        //        showProgressBar(PBMessageHelper.getMessage(this, Constants.API.addAppointment.toString()));
        val appointment = createAppointmentObject()
        val intentVital = Intent(this, PatientAppointmentActivityInfo::class.java)
        val bundle = Bundle()
        bundle.putString(Constants.IntentKeyConstants.FIRST_NAME, appointment.getFname())
        bundle.putString(Constants.IntentKeyConstants.LAST_NAME, appointment.getLname())
        if (!TextUtils.isEmpty(appointment.getEmail())) {
            bundle.putString(Constants.IntentKeyConstants.EMAIL, appointment.getEmail())
        } else {
            bundle.putString(Constants.IntentKeyConstants.EMAIL, null)
        }
        bundle.putString(Constants.IntentKeyConstants.DOB, binding?.idDob?.text.toString())
        bundle.putString(Constants.IntentKeyConstants.GENDER, appointment.getGender())
        bundle.putString(Constants.IntentKeyConstants.MOBILE_NO, appointment.getPhone())
        bundle.putString(
                Constants.IntentKeyConstants.PASSWORD,
                binding?.editTextPassword?.text.toString()
        )
        binding?.addRelativeCheckBox?.isChecked?.let {
            bundle.putBoolean(Constants.IntentKeyConstants.IS_RELATIVE, it)
        }
        if (!binding?.idRelFirstName?.text.toString().contentEquals("")) bundle.putString(
                Constants.IntentKeyConstants.REL_FNAME,
                binding?.idRelFirstName?.text.toString()
        )
        if (!binding?.idRelLastName?.text.toString().contentEquals("")) bundle.putString(
                Constants.IntentKeyConstants.REL_LNAME,
                binding?.idRelLastName?.text.toString()
        )
        if (binding!!.idSpinnerRelative.selectedItemPosition !== 0) {
            bundle.putString(
                    Constants.IntentKeyConstants.RELATION,
                    binding?.idSpinnerRelative?.selectedItem.toString()
            )
        }
        intentVital.putExtras(bundle)
        startActivityForResult(intentVital, 201, bundle)

//        viewModel.addAppointment(UtilityMethods.getPatientAppointmentToken(),appointment).observe(this, new Observer<CommonResponse>() {
//            @Override
//            public void onChanged(CommonResponse commonResponse) {
//                dismissProgressBar();
//                if(commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()){
//                    onAppointmentSuccess(commonResponse);
//                }else{
//                    String errMsg = ErrorMessages.getErrorMessage(PatientAppointmentActivity.this,commonResponse.getErrorMessage(), Constants.API.addAppointment);
//                    UtilityMethods.showErrorSnackBar(binding.getRoot(),errMsg,Snackbar.LENGTH_LONG);
//                }
//            }
//        });
    }


    private fun getDOBLongFormat(date: String): Long {
        val d = date.split("/".toRegex()).toTypedArray()
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.clear()
        calendar[Calendar.DAY_OF_MONTH] = d[1].toInt()
        calendar[Calendar.MONTH] = d[0].toInt() - 1
        calendar[Calendar.YEAR] = d[2].toInt()
        val formatDate = calendar.time
        Log.i(TAG, "getDateInLongFormat: $formatDate")
        Log.i(TAG, "getDateInLongFormat in long: " + formatDate.time)
        return formatDate.time
    }
}