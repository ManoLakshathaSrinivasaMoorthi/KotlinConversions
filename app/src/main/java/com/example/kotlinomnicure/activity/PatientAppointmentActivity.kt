package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
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
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.dailytasksamplepoc.R

import com.example.dailytasksamplepoc.databinding.ActivityPatientAppointmentBinding
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.AppointmentViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.EmailcheckViewModel
import com.example.kotlinomnicure.helper.MobileNumberFormatter
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import com.mvp.omnicure.kotlinactivity.utils.ValidationUtil
import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import omnicurekotlin.example.com.userEndpoints.model.Provider
import java.lang.Exception
import java.util.*

class PatientAppointmentActivity : BaseActivity() {

    //Declare the variables
    private val TAG = PatientAppointmentActivity::class.java.simpleName
    var relation = ArrayList<String>()
    private var binding: ActivityPatientAppointmentBinding? = null
    private var viewModel: AppointmentViewModel? = null

    //on create method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_appointment)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        setOnClickListener()
        binding?.addRelativeCheckBox?.setOnClickListener(View.OnClickListener {
            if (binding!!.addRelativeCheckBox.isChecked()) {
                binding!!.addRelativeContainer.setVisibility(View.VISIBLE)
            } else {
                binding!!.addRelativeContainer.setVisibility(View.GONE)
            }
        })
        relation.add(getString(R.string.relation_to_patient))
        relation.add(getString(R.string.parent))
        relation.add(getString(R.string.spouse))
        relation.add(getString(R.string.sibbling))
        relation.add(getString(R.string.relative))
        relation.add(getString(R.string.son_daughter))
        val relativeAdapter = ArrayAdapter(this, R.layout.spinner_custom_text, relation)
        binding?.idSpinnerRelative?.setAdapter(relativeAdapter)
        binding?.idSpinnerRelative?.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long) {
                try {
                    val spinnerText = view as TextView
                    val providerType = relation[position]
                    binding!!.idSpinnerRelative.setTag(position)
                    if (spinnerText != null) {
                        spinnerText.maxLines = 1
                        if (position != 0) {
                            UtilityMethods().setTextViewColor(
                                this,
                                spinnerText,
                                R.color.white
                            )
                            UtilityMethods().setDrawableBackground(
                                this,
                                binding!!.idSpinnerRelative,
                                R.drawable.spinner_drawable_selected
                            )
                        } else {
                            UtilityMethods().setTextViewColor(
                                this,
                                spinnerText,
                                R.color.gray_500
                            )
                            UtilityMethods().setDrawableBackground(
                                this,
                                binding!!.idSpinnerRelative,
                                R.drawable.spinner_drawable
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:", e.cause)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
        binding?.editTextPassword?.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2
            if (event.getAction() === MotionEvent.ACTION_DOWN) {
                if (event.getRawX() >= binding?.editTextPassword!!.getRight() - binding?.editTextPassword!!.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    if (binding!!.editTextPassword.getInputType() === InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        binding!!.editTextPassword.setInputType(
                            InputType.TYPE_CLASS_TEXT or
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD
                        )
                        binding!!.editTextPassword.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_visibility,
                            0
                        )
                        binding!!.editTextPassword.setSelection(
                            binding!!.editTextPassword.getText().length
                        )
                    } else {
                        binding!!.editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        binding!!.editTextPassword.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_visibility_off,
                            0
                        )
                        binding!!.editTextPassword.setSelection(
                            binding!!.editTextPassword.getText().length
                        )
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun setOnClickListener() {
        binding?.idBackButton?.setOnClickListener { view -> finish() }
        binding?.idDob?.setOnClickListener { view -> selectDOB() }
        binding?.idPhoneNumber?.addTextChangedListener(object : TextWatcher {
            val FIRST_SEP_LENGTH = 4
            val SECOND_SEP_LENGTH = 8
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence == null || charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    return
                } else {
                    val str = charSequence.toString()
                    MobileNumberFormatter().formatMobileNumber(
                        str,
                        binding!!.idPhoneNumber,
                        FIRST_SEP_LENGTH,
                        SECOND_SEP_LENGTH
                    )
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable == null || editable.toString().trim { it <= ' ' }.isEmpty()) {
                    binding!!.countryStdCode.setVisibility(View.GONE)
                } else {
                    binding!!.countryStdCode.setVisibility(View.VISIBLE)
                }
            }
        })
        try {
            val agreementTxt: String = binding?.agreementCheckBox?.getText().toString()
            val ss = SpannableString(agreementTxt)
            val drawableBG: Drawable? = binding?.agreementCheckBox?.getBackground()
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    Log.d(TAG, "onClick: of user agreement")
                    binding!!.agreementCheckBox.setEnabled(false)
                    binding!!.agreementCheckBox.setBackground(resources.getDrawable(R.drawable.transparent_bg))
                    binding!!.agreementCheckBox.setEnabled(true)
                    val uri = Uri.parse(Constants.USER_AGREEMENT_LINK)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                }
            }
            ss.setSpan(
                clickableSpan,
                agreementTxt.indexOf(getString(R.string.agreement)),
                agreementTxt.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                binding?.agreementCheckBox?.getCurrentTextColor()?.let { ForegroundColorSpan(it) },
                agreementTxt.indexOf(getString(R.string.agreement)),
                agreementTxt.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding?.agreementCheckBox?.setText(ss)
            binding?.agreementCheckBox?.setMovementMethod(LinkMovementMethod.getInstance())
            binding?.agreementCheckBox?.setOnCheckedChangeListener { compoundButton, isCheked ->
                binding!!.agreementCheckBox.setBackground(drawableBG)
                val color: Int = if (isCheked) R.color.white else R.color.gray_500
                ss.setSpan(
                    ForegroundColorSpan(resources.getColor(color)),
                    agreementTxt.indexOf(getString(R.string.agreement)),
                    agreementTxt.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding!!.agreementCheckBox.setText(ss)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
        binding?.submitBtn?.setOnClickListener { view -> onClickSubmit() }
    }

    private fun selectDOB() {
        binding?.idDob?.setEnabled(false)
        val mcurrentDate = Calendar.getInstance()
        var mYear = mcurrentDate[Calendar.YEAR]
        var mMonth = mcurrentDate[Calendar.MONTH]
        var mDay = mcurrentDate[Calendar.DAY_OF_MONTH]
        if (binding?.idDob?.getTag() != null && !binding?.idDob?.getTag().toString().isEmpty()) {
            val dob: String = binding?.idDob!!.getTag().toString()
            val dobArr = dob.split("/").toTypedArray()
            mMonth = dobArr[0].toInt() - 1
            mDay = dobArr[1].toInt()
            mYear = dobArr[2].toInt()
        }
        val mDatePicker = DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog,
            { datepicker, selectedyear, selectedmonth, selectedday ->
                val selectedDate = Calendar.getInstance()
                selectedDate[selectedyear, selectedmonth] = selectedday
                binding?.idDob?.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_calendar,
                    0
                )
                binding?.idDob?.setText((selectedmonth + 1).toString() + "/" + selectedday + "/" + selectedyear)
                binding?.idDob?.setTag((selectedmonth + 1).toString() + "/" + selectedday + "/" + selectedyear)
                binding?.idDob?.setEnabled(true)
            }, mYear, mMonth, mDay
        )
        mDatePicker.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDatePicker.datePicker.maxDate = System.currentTimeMillis()
        mDatePicker.show()
        mDatePicker.setOnDismissListener { binding?.idDob?.setEnabled(true) }
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {

            if (errMsg != null) {
                CustomSnackBar.make(
                    binding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
            }
            return false
        }
        return true
    }

    private fun createAppointmentObject(): Appointment {
        val appointment = Appointment()
        appointment.setFname(binding?.idFirstName?.getText().toString().trim())
        appointment.setLname(binding?.idLastName?.getText().toString().trim())
        if (!TextUtils.isEmpty(binding?.idEmailId?.getText().toString().trim())) {
            appointment.setEmail(binding?.idEmailId?.getText().toString().trim())
        } else {
            appointment.setEmail(null)
        }
        appointment.setDob(getDOBLongFormat(binding?.idDob?.getText().toString()))
        val dob: List<String> = binding?.idDob?.getText().toString().split("/")
        val month: String? = UtilityMethods().getMonthName(dob[0].toInt())
        val day = dob[1].toInt()
        val year = dob[2].toInt()
        appointment.setDobMonth(month)
        appointment.setDobDay(day)
        appointment.setDobYear(year)
        appointment.setPhone(binding?.idPhoneNumber?.getText().toString().trim().replace("-", ""))

        if (binding?.radioGrp?.getCheckedRadioButtonId() === R.id.radioBtnMale) {
            appointment.setGender(Constants.Gender.Male.toString())
        } else if (binding?.radioGrp?.getCheckedRadioButtonId() === R.id.radioBtnFemale) {
            appointment.setGender(Constants.Gender.Female.toString())
        }
        return appointment
    }

    private fun onClickSubmit() {
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(
                binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }

        val provider: Provider = createPatientObject()
        Log.d("PROVIDER", Gson().toJson(provider))
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.register.toString()))
        EmailcheckViewModel().emailcheckProvider(provider).observe(this) { commonResponse ->
            dismissProgressBar()
            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {

                onemailchecksuccess()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    commonResponse?.getErrorMessage(),
                    Constants.API.register
                )

                if (errMsg != null) {
                    CustomSnackBar.make(
                        binding?.idContainerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
        }


    }

    private fun createPatientObject(): Provider {
        val provider = Provider()
        provider.setEmail(binding?.idEmailId?.getText().toString().lowercase(Locale.getDefault()).trim())

        provider.setCountryCode("+1")

        provider.setPhone(binding?.idPhoneNumber?.getText().toString().trim().replace("-", ""))
        return provider
    }

    private fun onemailchecksuccess() {
        val appointment: Appointment = createAppointmentObject()
        val intentVital = Intent(
            this,
            PatientAppointmentActivityInfo::class.java
        )
        val bundle = Bundle()
        bundle.putString(Constants.IntentKeyConstants.FIRST_NAME, appointment.getFname())
        bundle.putString(Constants.IntentKeyConstants.LAST_NAME, appointment.getLname())
        if (!TextUtils.isEmpty(appointment.getEmail())) {
            bundle.putString(Constants.IntentKeyConstants.EMAIL, appointment.getEmail())
        } else {
            bundle.putString(Constants.IntentKeyConstants.EMAIL, null)
        }
        bundle.putString(Constants.IntentKeyConstants.DOB, binding?.idDob?.getText().toString())
        bundle.putString(Constants.IntentKeyConstants.GENDER, appointment.getGender())
        bundle.putString(Constants.IntentKeyConstants.MOBILE_NO, appointment.getPhone())
        bundle.putString(
            Constants.IntentKeyConstants.PASSWORD,
            binding?.editTextPassword?.getText().toString()
        )
        binding?.addRelativeCheckBox?.let {
            bundle.putBoolean(
                Constants.IntentKeyConstants.IS_RELATIVE,
                it.isChecked()
            )
        }
        if (!binding?.idRelFirstName?.getText().toString().equals("",ignoreCase = true)) bundle.putString(
            Constants.IntentKeyConstants.REL_FNAME,
            binding?.idRelFirstName?.getText().toString()
        )
        if (!binding?.idRelLastName?.getText().toString().equals("",ignoreCase = true)) bundle.putString(
            Constants.IntentKeyConstants.REL_LNAME,
            binding?.idRelLastName?.getText().toString()
        )
        if (binding?.idSpinnerRelative?.getSelectedItemPosition() !== 0) {
            bundle.putString(
                Constants.IntentKeyConstants.RELATION,
                binding?.idSpinnerRelative?.getSelectedItem().toString()
            )
        }
        intentVital.putExtras(bundle)
        startActivityForResult(intentVital, 201, bundle)
    }


    private fun getDOBLongFormat(date: String): Long {
        val d = date.split("/").toTypedArray()
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