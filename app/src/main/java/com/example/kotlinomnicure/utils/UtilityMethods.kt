package com.example.kotlinomnicure.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Html
import android.text.InputFilter
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.customview.CustomDialog
import com.example.kotlinomnicure.model.ConsultProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Patient

import java.lang.Exception
import java.lang.IllegalStateException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.*

class UtilityMethods {
    private val TAG = UtilityMethods::class.java.simpleName
    private var snackbar: Snackbar? = null

    fun getPatientAppointmentToken(): String? {
        return "jhguyg398798574bjhb8y7987y"
    }

    fun showErrorSnackBar(view: View, message: String?, lengthType: Int) {
        try {
            val snackbar = Snackbar.make(view, message!!, lengthType)
            snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))
            snackbar.setTextColor(view.context.resources.getColor(R.color.red))
            snackbar.show()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun showInternetError(view: View, lengthType: Int) {
        try {
            val message = view.resources.getString(R.string.no_internet_connectivity)
            val snackbar = Snackbar.make(view, message, lengthType)
            snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))
            snackbar.setTextColor(view.context.resources.getColor(R.color.red))
            snackbar.show()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun showInternetErrorInfinte(view: View) {
        try {
            val message = view.resources.getString(R.string.no_internet_connectivity)
            snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
            snackbar!!.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.red))
            snackbar!!.setTextColor(view.context.resources.getColor(R.color.white))
            snackbar!!.show()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun dismissSnackbar() {
        if (snackbar != null && snackbar!!.isShown) {
            snackbar!!.dismiss()
        }
    }


    fun showMessageSnackBar(view: View, message: String?) {
        try {
            val snackbar = Snackbar.make(view, message!!, Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))
            snackbar.setTextColor(view.context.resources.getColor(R.color.gray_800))
            snackbar.show()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }


    fun isValidEmail(target: String?): Boolean {
        val emailPatter = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-.]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        return target?.matches(emailPatter.toRegex()) ?: false

    }

    fun getPasswordFormat(): String {
        return "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    }

    fun isValidPassword(target: String?): Boolean {
        val passwordPatter = getPasswordFormat()
        return target?.matches(passwordPatter.toRegex()) ?: false
    }

    fun isInternetConnected(application: Context): Boolean? {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw)
            actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH))
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo
            nwInfo != null && nwInfo.isConnected
        }


    }

    fun isInternetConnected_(context: Context?): Boolean {
        if (!isNetworkConnected(context)) {
            return false
        }
        var inetAddress: InetAddress? = null
        try {
            val future = Executors.newSingleThreadExecutor().submit(
                Callable {
                    try {
                        return@Callable InetAddress.getByName("google.com")
                    } catch (e: UnknownHostException) {
                        return@Callable null
                    } catch (e: Exception) {
                        return@Callable null
                    }
                })
            inetAddress = future[1000, TimeUnit.MILLISECONDS]
            future.cancel(true)
        } catch (e: InterruptedException) {
            Log.e(TAG, "Exception:", e.cause)
            // solution code from sonarqube
            Thread.currentThread().interrupt()
        } catch (e: ExecutionException) {
            Log.e(TAG, "Exception:", e.cause)
        } catch (e: TimeoutException) {
            Log.i(TAG, "isInternetConnected: false - > Timeout Exception ")
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }

        return inetAddress != null
    }

    private fun isNetworkConnected(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    fun formatMobileNo(userNumber: String?): String? {
        var userNumber = userNumber
        if (userNumber == null) {
            return ""
        }
        userNumber = userNumber.trim { it <= ' ' }
        if (userNumber.startsWith("0")) { //Remove 0 from user number
            userNumber = userNumber.substring(1)
        }
        if (userNumber.startsWith(Constants.US_COUNTRY_CODE) && userNumber.length == 12) {
            userNumber = userNumber.replace(Constants.US_COUNTRY_CODE, "")
        }
        if (userNumber.length == 11 && userNumber.startsWith("1")) {
            userNumber = userNumber.substring(1)
        }
        if (userNumber.startsWith("+")) { //Remove + from user number
            userNumber = userNumber.substring(1)
        }
        return userNumber
    }

    fun setDrawableBackground(mContext: Context, view: View, drawable: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.background = ContextCompat.getDrawable(mContext, drawable)
            } else {
                view.setBackgroundDrawable(mContext.resources.getDrawable(drawable))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun setButtonBackground(mContext: Context, view: Button, drawable: Int) {
        try {
            view.background = ContextCompat.getDrawable(mContext, drawable)
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun setColorBackground(mContext: Context, view: View, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, color))
        } else {
            view.setBackgroundColor(mContext.resources.getColor(color))
        }
    }

    fun setTextViewColor(mContext: Context, view: View, color: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (view as TextView).setTextColor(ContextCompat.getColor(mContext, color))
            } else {
                (view as TextView).setTextColor(mContext.resources.getColor(color))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun setRadioBtnTextColor(mContext: Context, view: View, color: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (view as RadioButton).setTextColor(ContextCompat.getColor(mContext, color))
            } else {
                (view as RadioButton).setTextColor(mContext.resources.getColor(color))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun setRadioBtnColor(mContext: Context, view: View, color: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (view as RadioButton).highlightColor = ContextCompat.getColor(mContext, color)
            } else {
                (view as RadioButton).highlightColor = mContext.resources.getColor(color)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun pxToDp(context: Context, px: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun setNameEditTextFilter(text: EditText) {
        val blockCharacterSet = "!@#$%^&*()-_+=.,><?':;[]{}"
        val filter =
            InputFilter { source, start, end, dest, dstart, dend ->
                if (source != null && blockCharacterSet.contains("" + source)) {
                    ""
                } else null
            }
        text.filters = arrayOf(filter)
    }

    fun setPhonnEditTextFilter(text: EditText) {
        val blockCharacterSet = "01234567890"
        val filter =
            InputFilter { source, start, end, dest, dstart, dend ->
                if (source != null && blockCharacterSet.contains("" + source)) {
                    ""
                } else null
            }
        text.filters = arrayOf(filter)
    }

    fun checkPermission(context: Context?, permissions: Array<String?>): Boolean {
        for (i in permissions.indices) {
            val result = ContextCompat.checkSelfPermission(
                context!!,
                permissions[i]!!
            )
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun getNameText(name: String): String? {
        if (TextUtils.isEmpty(name)) {
            return ""
        }
        val nameArr = name.trim { it <= ' ' }.split(" ").toTypedArray()
        return if (nameArr.size == 1) {
            nameArr[0].substring(0, 1).toUpperCase()
        } else {
            (nameArr[0].substring(0, 1) + nameArr[nameArr.size - 1].substring(0, 1)).toUpperCase()
        }
    }


    fun showDialog(
        ctx: Context, title: String?,
        message: String?, isCancelable: Boolean, positiveText: Int,
        positiveListener: View.OnClickListener?, negativeText: Int,
        negativeListener: View.OnClickListener?, positiveButtonColor: Int, showTitle: Boolean?
    ): CustomDialog? {
        val dialog = CustomDialog(ctx)
        if (title != null && !TextUtils.isEmpty(title)) {
            dialog.setTitle(title)
        } else {
            dialog.setTitle("")
        }
        dialog.setShowTitle(showTitle)
        if (positiveButtonColor != -1) {
            dialog.positiveButtonColor = positiveButtonColor
        }
        dialog.setMessage(message)
        dialog.setCancelable(isCancelable)
        dialog.setCanceledOnTouchOutside(isCancelable)
        if (positiveText != -1) {
            dialog.setPositiveButton(positiveText, positiveListener)
        }
        if (negativeText != -1) {
            dialog.setNegativeButton(negativeText, negativeListener)
        }
        var isDestroyed = false
        val isFinished = (ctx as Activity).isFinishing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            isDestroyed = ctx.isDestroyed
        }
        try {
            if (isDestroyed) {
                dialog.dismiss()
            }
            if (!dialog.isShowing && !isFinished && !isDestroyed) {
                dialog.show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
        return dialog
    }

    fun getSpo2TextColor(value: Int): Int {
        return if (value >= 94) {
            R.color.acuity_low_border
        } else if (value >= 85 && value < 94) {
            R.color.acuity_med_border
        } else {
            R.color.acuity_high_border
        }
    }

    fun displayPatientStatusComponent(
        context: Context,
        stub: RelativeLayout,
        urgency: Boolean?,
        pending: Boolean,
        acuityLevel: Constants.AcuityLevel?
    ) {
        var urgency = urgency
        try {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val inflatedView: View =
                inflater.inflate(R.layout.patient_status_component, stub, false)
            stub.removeAllViews()
            stub.addView(inflatedView)
            val urgencyLayout = inflatedView.findViewById<View>(R.id.urgentLayout) as RelativeLayout
            val pendingLayout =
                inflatedView.findViewById<View>(R.id.pendingLayout) as RelativeLayout
            val txtAcuityValue = inflatedView.findViewById<View>(R.id.txtAcuityValue) as TextView
            if (urgency == null) {
                urgency = false
            }
            if (urgency) {
                urgencyLayout.visibility = View.VISIBLE
            }
            if (pending) {
                pendingLayout.visibility = View.VISIBLE
            }
            if (acuityLevel != null) {
                if (acuityLevel == Constants.AcuityLevel.Low) {
                    txtAcuityValue.text = context.resources.getString(R.string.patient_low)
                    txtAcuityValue.setBackgroundResource(R.drawable.acuity_level_low)
                    txtAcuityValue.setTextColor(context.resources.getColor(R.color.acuity_low_border))
                } else if (acuityLevel == Constants.AcuityLevel.Medium) {
                    txtAcuityValue.text = context.resources.getString(R.string.patient_med)
                    txtAcuityValue.setBackgroundResource(R.drawable.acuity_level_med)
                    txtAcuityValue.setTextColor(context.resources.getColor(R.color.acuity_med_border))
                } else if (acuityLevel == Constants.AcuityLevel.High) {
                    txtAcuityValue.text = context.resources.getString(R.string.patient_high)
                    txtAcuityValue.setBackgroundResource(R.drawable.acuity_level_high)
                    txtAcuityValue.setTextColor(context.resources.getColor(R.color.acuity_high_border))
                } else if (acuityLevel == Constants.AcuityLevel.NA) {
                    txtAcuityValue.text = context.resources.getString(R.string.patient_na)
                    txtAcuityValue.setBackgroundResource(R.drawable.acuity_level_na)
                    txtAcuityValue.setTextColor(txtAcuityValue.resources.getColor(R.color.gray_text))
                }
            } else {
                txtAcuityValue.text = context.resources.getString(R.string.patient_na)
                txtAcuityValue.setBackgroundResource(R.drawable.acuity_level_na)
                txtAcuityValue.setTextColor(txtAcuityValue.resources.getColor(R.color.gray_text))
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "exception infalting view" + e.message)
        }
    }


    fun displayVitals(context: Context, stub: ViewStub, provider: ConsultProvider) {
        try {
            stub.layoutResource = R.layout.include_chart_view
            val inflatedView = stub.inflate()
            val txtHR = inflatedView.findViewById<View>(R.id.txtHR) as TextView
            val txtInvBP = inflatedView.findViewById<View>(R.id.txtInvBP) as TextView
            val txtRR = inflatedView.findViewById<View>(R.id.txtRR) as TextView
            val txtSPO2 = inflatedView.findViewById<View>(R.id.txtSPO2) as TextView
            val txtNonInvBP = inflatedView.findViewById<View>(R.id.txtNonInvBP) as TextView
            val txtTemp = inflatedView.findViewById<View>(R.id.txtTemp) as TextView
            val txtAvpu = inflatedView.findViewById<View>(R.id.txtAvpu) as TextView
            val txtSupl = inflatedView.findViewById<View>(R.id.txtSupl) as TextView
            val txtFi02 = inflatedView.findViewById<View>(R.id.txtFi02) as TextView
            val heartRate = provider.heartRate
            val lowBP = provider.arterialBloodPressureDiastolic
            val highBP = provider.arterialBloodPressureSystolic
            val spo2 = provider.spO2
            val respRate = provider.respiratoryRate
            val fiO2 = provider.fio2
            val temperature = provider.temperature
            val isOxygenSupplement = provider.oxygenSupplement
            val patientCondition = provider.patientCondition
            val docBoxPatientId = provider.docBoxPatientId
            val isDocboxDataAvail =
                (heartRate != null || lowBP != null || highBP != null || spo2 != null || respRate != null || fiO2 != null || isOxygenSupplement != null && isOxygenSupplement
                        || patientCondition != null)
            if (!TextUtils.isEmpty(docBoxPatientId) || isDocboxDataAvail) {
                try {
                    if (heartRate != null) {
                        txtHR.text = heartRate.toInt().toString()
                    } else {
                        txtHR.text = "-"
                    }
                    var lb = "-"
                    var hb = "-"
                    if (lowBP != null) {
                        lb = lowBP.toInt().toString()
                    }
                    if (highBP != null) {
                        hb = highBP.toInt().toString()
                    }
                    txtInvBP.text = "$hb / $lb"


                    if (spo2 != null) {
                        val color = UtilityMethods().getSpo2TextColor(spo2.toInt())
                        val spo2Value = spo2.toInt().toString() + " %"
                        val builder = TextSpanBuilder().getSubscriptString(
                            spo2Value,
                            spo2Value.length - 1,
                            spo2Value.length
                        )
                        txtSPO2.setTextColor(context.resources.getColor(color))
                        txtSPO2.text = builder
                    } else {
                        txtSPO2.text = "-"
                    }
                    if (respRate != null) {
                        txtRR.text = respRate.toInt().toString()
                    } else {
                        txtRR.text = "-"
                    }
                    if (fiO2 != null) {
                        txtFi02.text = fiO2.toInt().toString() + "%"
                    } else {
                        txtFi02.text = "-"
                    }
                    if (temperature != null) {
//                    txtTemp.setText(String.valueOf(temperature + "°F"));
                        txtTemp.text = temperature.toString()
                    } else {
                        txtTemp.text = "-"
                    }
                    val labelColor = context.resources.getColor(R.color.text_gray)
                    val сolorString = String.format("%X", labelColor).substring(2)
                    if (isOxygenSupplement != null && isOxygenSupplement) {
                        val strYes =
                            String.format("<b>Y</b><font color='#%s'>/N</font>", сolorString)
                        txtSupl.text = Html.fromHtml(strYes)
                    } else {
                        val strNo =
                            String.format("<font color='#%s'>Y/</font><b>N</b>", сolorString)
                        txtSupl.text = Html.fromHtml(strNo)
                    }
                    if (patientCondition != null) {
                        if (patientCondition == Constants.PatientCondition.Alert) {
                            txtAvpu.text = "A"
                        } else if (patientCondition == Constants.PatientCondition.Voice) {
                            txtAvpu.text = "V"
                        } else if (patientCondition == Constants.PatientCondition.Pain) {
                            txtAvpu.text = "P"
                        } else if (patientCondition == Constants.PatientCondition.Unresponsive) {
                            txtAvpu.text = "U"
                        }
                    } else {
                        txtAvpu.text = "-"
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:", e.cause)
                }
            } else {
                txtHR.text = "-"
                txtInvBP.text = "-"
                txtNonInvBP.text = "-"
                txtRR.text = "-"
                txtSPO2.text = "-"
                txtFi02.text = "-"
                txtTemp.text = "-"
                txtAvpu.text = "-"
                txtSupl.text = "-"
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "exception infalting view" + e.message)
        }
    }

    fun displayCensusVitals(context: Context, stub: ViewStub, patient: Patient) {
        try {
            stub.layoutResource = R.layout.include_chart_view
            val inflatedView = stub.inflate()
            val txtHR = inflatedView.findViewById<View>(R.id.txtHR) as TextView
            val txtInvBP = inflatedView.findViewById<View>(R.id.txtInvBP) as TextView
            val txtRR = inflatedView.findViewById<View>(R.id.txtRR) as TextView
            val txtSPO2 = inflatedView.findViewById<View>(R.id.txtSPO2) as TextView
            val txtNonInvBP = inflatedView.findViewById<View>(R.id.txtNonInvBP) as TextView
            val txtTemp = inflatedView.findViewById<View>(R.id.txtTemp) as TextView
            val txtAvpu = inflatedView.findViewById<View>(R.id.txtAvpu) as TextView
            val txtSupl = inflatedView.findViewById<View>(R.id.txtSupl) as TextView
            val txtFi02 = inflatedView.findViewById<View>(R.id.txtFi02) as TextView
            Log.d(TAG, "Patient displayCensusVitals Values  : " + Gson().toJson(patient))
            val heartRate = patient.heartRate
            val lowBP = patient.arterialBloodPressureDiastolic
            val highBP = patient.arterialBloodPressureSystolic
            val spo2 = patient.spO2
            val respRate = patient.respiratoryRate
            val fiO2 = patient.fio2
            val temperature = patient.temperature
            val isOxygenSupplement = patient.oxygenSupplement
            val patientCondition = patient.patientCondition
            val docBoxPatientId = patient.docBoxPatientId
            val isDocboxDataAvail =
                (heartRate != null || lowBP != null || highBP != null || spo2 != null || respRate != null || fiO2 != null || isOxygenSupplement != null && isOxygenSupplement
                        || patientCondition != null)
            Log.d(TAG, "if " + (!TextUtils.isEmpty(docBoxPatientId) || isDocboxDataAvail))
            Log.d(TAG, "isDocboxDataAvail : $isDocboxDataAvail")
            if (!TextUtils.isEmpty(docBoxPatientId) || isDocboxDataAvail) {
                try {
                    if (heartRate != null) {
                        txtHR.text = heartRate.toInt().toString()
                    } else {
                        txtHR.text = "-"
                    }
                    var lb = "-"
                    var hb = "-"
                    if (lowBP != null && lowBP != 0.0) {
                        lb = lowBP.toInt().toString()
                    }
                    if (highBP != null && highBP != 0.0) {
                        hb = highBP.toInt().toString()
                    }
                    txtInvBP.text = "$hb / $lb"
                   if (spo2 != null && spo2 != 0.0) {
                        val color = UtilityMethods().getSpo2TextColor(spo2.toInt())
                        val spo2Value = spo2.toInt().toString() + " %"
                        val builder = TextSpanBuilder().getSubscriptString(
                            spo2Value,
                            spo2Value.length - 1,
                            spo2Value.length
                        )
                        txtSPO2.setTextColor(context.resources.getColor(color))
                        txtSPO2.text = builder
                    } else {
                        txtSPO2.text = "-"
                    }
                    if (respRate != null && respRate != 0.0) {
                        txtRR.text = respRate.toInt().toString()
                    } else {
                        txtRR.text = "-"
                    }
                    if (fiO2 != null && fiO2 != 0.0) {
                        txtFi02.text = fiO2.toInt().toString() + "%"
                    } else {
                        txtFi02.text = "-"
                    }
                    if (temperature != null && temperature != 0.0) {
//                    txtTemp.setText(String.valueOf(temperature + "°F"));
                        txtTemp.text = temperature.toString()
                    } else {
                        txtTemp.text = "-"
                    }
                    val labelColor = context.resources.getColor(R.color.text_gray)
                    val сolorString = String.format("%X", labelColor).substring(2)
                    if (isOxygenSupplement != null && isOxygenSupplement) {
                        val strYes =
                            String.format("<b>Y</b><font color='#%s'>/N</font>", сolorString)
                        txtSupl.text = Html.fromHtml(strYes)
                    } else {
                        val strNo =
                            String.format("<font color='#%s'>Y/</font><b>N</b>", сolorString)
                        txtSupl.text = Html.fromHtml(strNo)
                    }
                    if (patientCondition != null) {
                        if (patientCondition == Constants.PatientCondition.Alert.toString()) {
                            txtAvpu.text = "A"
                        } else if (patientCondition == Constants.PatientCondition.Voice.toString()) {
                            txtAvpu.text = "V"
                        } else if (patientCondition == Constants.PatientCondition.Pain.toString()) {
                            txtAvpu.text = "P"
                        } else if (patientCondition == Constants.PatientCondition.Unresponsive.toString()) {
                            txtAvpu.text = "U"
                        }
                    } else {
                        txtAvpu.text = "-"
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:", e.cause)
                }
            } else {
                txtHR.text = "-"
                txtInvBP.text = "-"
                txtNonInvBP.text = "-"
                txtRR.text = "-"
                txtSPO2.text = "-"
                txtFi02.text = "-"
                txtTemp.text = "-"
                txtAvpu.text = "-"
                txtSupl.text = "-"
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "exception infalting view" + e.message)
        }
    }

    fun isDocboxPatient(provider: ConsultProvider): Boolean {
        val heartRate = provider.heartRate
        val lowBP = provider.arterialBloodPressureDiastolic
        val highBP = provider.arterialBloodPressureSystolic
        val spo2 = provider.spO2
        val respRate = provider.respiratoryRate
        val docBoxPatientId = provider.docBoxPatientId
        val isDocboxDataAvail =
            heartRate != null || lowBP != null || highBP != null || spo2 != null || respRate != null
        return !TextUtils.isEmpty(docBoxPatientId) || isDocboxDataAvail
    }

    fun hasSoftNavBar(): Boolean {
        val id = Resources.getSystem().getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && Resources.getSystem().getBoolean(id)
    }

    fun getSoftNavBarHeight(): Int {
        val id = Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android")
        return if (id > 0) {
            Resources.getSystem().getDimensionPixelSize(id)
        } else 0
    }

    fun isTestServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("dev-omnicure", ignoreCase = true)
    }

    fun isProductionServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("omnicure", ignoreCase = true)
    }

    fun isExternalTestServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("omnicure-ext-test", ignoreCase = true)
    }

    fun isDemoTestServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("omnicure-demo", ignoreCase = true)
    }

    fun isQaTestServer(): Boolean {
        return if (BuildConfigConstants.getBackendAppName()
                .equals("qa-omnicure", ignoreCase = true)
        ) true else false
    }

    fun isPilotServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("omnicurepilot", ignoreCase = true)
    }

    fun isNetccnAutoTestServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("netccnautotest", ignoreCase = true)
    }

    fun isStagingServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("omnicure-staging", ignoreCase = true)
    }

    fun isNetccnDevServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("netccndev", ignoreCase = true)
    }

    fun isOmnicureTestServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("omnicure_test", ignoreCase = true)
    }

    fun isNetccnSecTestServer(): Boolean {
        return BuildConfigConstants.getBackendAppName()
                .equals("netccnsectest", ignoreCase = true)
    }

    fun getMonthName(month: Int): String? {
        when (month) {
            1 -> {
                return Constants.Month.Jan.toString()
            }
            2 -> {
                return Constants.Month.Feb.toString()
            }
            3 -> {
                return Constants.Month.March.toString()
            }
            4 -> {
                return Constants.Month.April.toString()
            }
            5 -> {
                return Constants.Month.May.toString()
            }
            6 -> {
                return Constants.Month.June.toString()
            }
            7 -> {
                return Constants.Month.July.toString()
            }
            8 -> {
                return Constants.Month.Aug.toString()
            }
            9 -> {
                return Constants.Month.Sep.toString()
            }
            10 -> {
                return Constants.Month.Oct.toString()
            }
            11 -> {
                return Constants.Month.Nov.toString()
            }
            12 -> {
                return Constants.Month.Dec.toString()
            }
        }
        return ""
    }

    fun getFCMTopic(): String? {
        return if (isTestServer()) {
            Constants.PATIENT_FCM_TOPIC_TEST
        } else if (isDemoTestServer()) {
            Constants.PATIENT_FCM_TOPIC_TEST
        } else if (isQaTestServer()) {
            Constants.PATIENT_FCM_TOPIC_TEST
        } else if (isExternalTestServer()) {
            Constants.PATIENT_FCM_TOPIC_EXT_TEST
        } else if (isProductionServer()) {
            Constants.PATIENT_FCM_TOPIC
        } else {
            Constants.PATIENT_FCM_TOPIC_TEST
        }
    }
}