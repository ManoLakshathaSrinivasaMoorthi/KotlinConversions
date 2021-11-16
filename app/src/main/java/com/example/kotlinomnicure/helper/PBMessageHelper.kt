package com.example.kotlinomnicure.helper

import android.content.Context
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.utils.Constants

class PBMessageHelper {
    fun getMessage(context: Context, apiName: String): String {
        return if (apiName.equals(Constants.API.signin.toString(), ignoreCase = true)) {
            context.getString(R.string.signin_pb_text)
        } else if (apiName.equals(Constants.API.register.toString(), ignoreCase = true)) {
            context.getString(R.string.register_pb_text)
        } else if (apiName.equals(Constants.API.addPatient.toString(), ignoreCase = true)) {
            context.getString(R.string.adding_patient_pb_text)
        } else if (apiName.equals(Constants.API.getHospital.toString(), ignoreCase = true)) {
            context.getString(R.string.fetch_directory_pb_text)
        } else if (apiName.equals(Constants.API.updateProvider.toString(), ignoreCase = true)) {
            context.getString(R.string.update_status_pb_text)
        } else if (apiName.equals(Constants.API.verifyOTP.toString(), ignoreCase = true)) {
            context.getString(R.string.verify_otp_pb_text)
        } else if (apiName.equals(Constants.API.acceptInvite.toString(), ignoreCase = true)) {
            context.getString(R.string.sending_invite_pb_text)
        } else if (apiName.equals(Constants.API.addAppointment.toString(), ignoreCase = true)) {
            context.getString(R.string.add_patient_pb_msg)
        } else if (apiName.equals(Constants.API.getPatientDetails.toString(), ignoreCase = true)) {
            context.getString(R.string.fetch_patient_details)
        } else if (apiName.equals(Constants.API.getHandOffList.toString(), ignoreCase = true)) {
            context.getString(R.string.fetch_patient_details)
        } else if (apiName.equals(Constants.API.forgotPassword.toString(), ignoreCase = true)) {
            context.getString(R.string.email_address_verify)
        } else if (apiName.equals(Constants.API.changePassword.toString(), ignoreCase = true)) {
            context.getString(R.string.changing_password)
        } else if (apiName.equals(Constants.API.getTeam.toString(), ignoreCase = true)) {
            context.getString(R.string.get_team)
        } else {
            context.getString(R.string.please_wait)
        }
    }

}
