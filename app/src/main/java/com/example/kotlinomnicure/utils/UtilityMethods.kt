package com.example.kotlinomnicure.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.ActivityConsultChart
import com.example.kotlinomnicure.customview.CustomDialog
import com.example.kotlinomnicure.model.ConsultProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Patient
import java.util.*

@Suppress("DEPRECATION")
class UtilityMethods {

    private val TAG = UtilityMethods::class.java.simpleName
    private var snackbar: Snackbar? = null


    fun isInternetConnected(application: Context): Boolean {
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

//        ConnectivityManager cm =
//                (ConnectivityManager)application.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();

//        return  isConnected;
    }
    fun getNameText(name: String): String {
        if (TextUtils.isEmpty(name)) {
            return ""
        }
        val nameArr = name.trim { it <= ' ' }.split(" ").toTypedArray()
        return if (nameArr.size == 1) {
            nameArr[0].substring(0, 1).toUpperCase(Locale.ROOT)
        } else {
            (nameArr[0].substring(0, 1) + nameArr[nameArr.size - 1].substring(0, 1)).toUpperCase(
                Locale.ROOT
            )
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
            e.printStackTrace()
        }
    }

    fun isValidEmail(target: String): Boolean {
        val emailPatter = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-.]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        return if (target == null) {
            false
        } else target.matches(emailPatter)
    }
    private fun <T> Comparable<T>.matches(regex: T): Boolean {
        return true
    }

    fun setDrawableBackground(
        patientAppointmentActivity: AdapterView.OnItemSelectedListener,
        idSpinnerRelative: Spinner,
        spinnerDrawableSelected: Any
    ) {

    }



    fun displayPatientStatusComponent(
        activityCensusConsultChart: Activity,
        statusStub: RelativeLayout?,
        valueOf: Boolean,
        equals: Boolean,
        valueOf1: Any
    ) {

    }

    fun getSpo2TextColor(toInt: Int): Int {
   return 0
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

    fun showDialog(
        activityConsultChart: ActivityConsultChart,
        title: String,
        message: String,
        b: Boolean,
        send: Int,
        onClickListener: View.OnClickListener,
        cancel: Int,
        onClickListener1: View.OnClickListener?,
        red: Int,
        b1: Boolean
    ): CustomDialog? {
return null
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
            val heartRate: Double? = provider.getHeartRate()
            val lowBP: Double? = provider.getArterialBloodPressureDiastolic()
            val highBP: Double? = provider.getArterialBloodPressureSystolic()
            val spo2: Double? = provider.getSpO2()
            val respRate: Double? = provider.getRespiratoryRate()
            val fiO2: Double? = provider.getFio2()
            val temperature: Double? = provider.getTemperature()
            val isOxygenSupplement: Boolean = provider.getOxygenSupplement() == true
            val patientCondition: Constants.PatientCondition? = provider.getPatientCondition()
            val docBoxPatientId: String? = provider.getDocBoxPatientId()
            val isDocboxDataAvail =
                if (heartRate != null || lowBP != null || highBP != null || spo2 != null || respRate != null || fiO2 != null || isOxygenSupplement != null && isOxygenSupplement
                    || patientCondition != null /*&& patientCondition.equals(Constants.PatientCondition.AVPU)*/) true else false
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

//                    if (lowBP != null && highBP != null) {
//                        txtInvBP.setText(highBP.intValue() + "/" + lowBP.intValue());
//                    } else if (lowBP != null) {
//                        txtInvBP.setText(String.valueOf(lowBP.intValue()));
//                    } else if (highBP != null) {
//                        txtInvBP.setText(String.valueOf(highBP.intValue()));
//                    } else {
//                        txtInvBP.setText("-");
//                    }
                    if (spo2 != null) {
                        val color: Int = UtilityMethods().getSpo2TextColor(spo2.toInt())
                        val spo2Value = spo2.toInt().toString() + " %"
                        val builder: SpannableStringBuilder? = TextSpanBuilder().getSubscriptString(
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
                        if (patientCondition.equals(Constants.PatientCondition.Alert)) {
                            txtAvpu.text = "A"
                        } else if (patientCondition.equals(Constants.PatientCondition.Voice)) {
                            txtAvpu.text = "V"
                        } else if (patientCondition.equals(Constants.PatientCondition.Pain)) {
                            txtAvpu.text = "P"
                        } else if (patientCondition.equals(Constants.PatientCondition.Unresponsive)) {
                            txtAvpu.text = "U"
                        }
                    } else {
                        txtAvpu.text = "-"
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
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
            val heartRate = patient.getHeartRate()
            val lowBP = patient.getArterialBloodPressureDiastolic()
            val highBP = patient.getArterialBloodPressureSystolic()
            val spo2 = patient.getSpO2()
            val respRate = patient.getRespiratoryRate()
            val fiO2 = patient.getFio2()
            val temperature = patient.getTemperature()
            val isOxygenSupplement = patient.getOxygenSupplement()
            val patientCondition = patient.getPatientCondition()
            val docBoxPatientId = patient.getDocBoxPatientId()
            val isDocboxDataAvail =
                if (heartRate != null || lowBP != null || highBP != null || spo2 != null || respRate != null || fiO2 != null || isOxygenSupplement != null && isOxygenSupplement
                    || patientCondition != null /*&& patientCondition.equals(Constants.PatientCondition.AVPU)*/) true else false
            Log.d(TAG, "if " + (!TextUtils.isEmpty(docBoxPatientId) || isDocboxDataAvail))
            Log.d(TAG, "isDocboxDataAvail : $isDocboxDataAvail")
            if (!TextUtils.isEmpty(docBoxPatientId) || isDocboxDataAvail) {
                try {
                    if (heartRate != null && heartRate != 0.0) {
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

                    /*     if (lowBP != null && highBP != null) {
                        txtInvBP.setText(highBP.intValue() + "/" + lowBP.intValue());
                    } else if (lowBP != null) {
                        txtInvBP.setText(String.valueOf(lowBP.intValue()));
                    } else if (highBP != null) {
                        txtInvBP.setText(String.valueOf(highBP.intValue()));
                    } else {
                        txtInvBP.setText("-");
                    }*/if (spo2 != null && spo2 != 0.0) {
                        val color: Int = UtilityMethods().getSpo2TextColor(spo2.toInt())
                        val spo2Value = spo2.toInt().toString() + " %"
                        val builder: SpannableStringBuilder? = TextSpanBuilder().getSubscriptString(
                            spo2Value, spo2Value.length - 1,
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
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
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
        } catch (e: java.lang.IllegalStateException) {
            Log.e(TAG, "exception infalting view" + e.message)
        }
    }

    fun checkPermission(context: Context?, permissions: Array<String?>): Boolean {
        for (i in permissions.indices) {
            val result = ContextCompat.checkSelfPermission(context!!, permissions[i]!!)
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}




