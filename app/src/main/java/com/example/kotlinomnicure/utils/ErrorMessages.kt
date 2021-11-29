package com.example.kotlinomnicure.utils

import android.content.Context
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.*

class ErrorMessages {
    fun getErrorMessage(`object`: Any, errMsg: String?, api: Constants.API?): String? {
        return if (errMsg == null) {
            ""
        }/* else if (`object` is RegistrationActivity) {
            val context: Context = (`object` as RegistrationActivity).getApplicationContext()
            com.mvp.omnicure.utils.ErrorMessages.getRegActivityErrorMsg(context, api, errMsg)
        }*/ else if (`object` is OTPActivity) {
            val context = `object`.applicationContext
            return getOTPActivityErrorMsg(context, api!!, errMsg)
        } else if (`object` is EmailOTPActivity) {
            val context = `object`.applicationContext
           return getOTPActivityErrorMsg(context, api!!, errMsg)
        } else if (`object` is LoginActivity) {
            val context = `object`.applicationContext
        return  getLoginActivityErrorMsg(context, api!!, errMsg)
        } else if (`object` is AddPatientActivity) {
            val context = `object`.applicationContext
          return  getAddPatientActivityErrorMsg(context, api!!, errMsg)
        } else if (`object` is HomeActivity) {
            val context = `object`.applicationContext
          return   getHomeActivityErrorMsg(context, api!!, errMsg)
        } /*else if (`object` is ChatActivity) {
            val context: Context = (`object` as ChatActivity).getApplicationContext()
            com.mvp.omnicure.utils.ErrorMessages.getChatActivityErrorMsg(context, api, errMsg)
        } */else if (`object` is SplashActivity) {
            val context = `object`.applicationContext
           return getSplashActivityErrorMsg(context, api!!, errMsg)
        } /*else if (`object` is CallActivity) {
            val context: Context = (`object` as CallActivity).getApplicationContext()
            com.mvp.omnicure.utils.ErrorMessages.getCallActivityErrorMsg(context, api, errMsg)
        } */else if (`object` is PatientAppointmentActivity) {
            val context = `object`.applicationContext
             return getAppointmentActivityErrorMsg(context, api!!, errMsg)
        } /*else if (`object` is HandOffPatientsActivity) {
            val context: Context = (`object` as HandOffPatientsActivity).getApplicationContext()
            com.mvp.omnicure.utils.ErrorMessages.getHandOffPatientsErrorMsg(context, api, errMsg)
        } else if (`object` is TransferPatientActivity) {
            val context: Context = (`object` as TransferPatientActivity).getApplicationContext()
            com.mvp.omnicure.utils.ErrorMessages.getTransferPatientErrorMsg(context, api, errMsg)
        } else if (`object` is NotificationActivity) {
            val context: Context = (`object` as NotificationActivity).getApplicationContext()
            com.mvp.omnicure.utils.ErrorMessages.getTransferPatientErrorMsg(context, api, errMsg)
        } */else {
            errMsg
        }
    }


    private fun getRegActivityErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        val b = (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
            Constants.APIErrorType.Exception.toString(),
            ignoreCase = true
        )
                || errMsg.equals(Constants.APIErrorType.ServerError.toString(), ignoreCase = true))
        when (api) {
            Constants.API.register -> if (b) {
                return context.resources.getString(R.string.signup_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.getHospital -> if (b) {
                return context.resources.getString(R.string.hospital_list_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.getProviders -> if (b) {
                return context.getString(R.string.directry_list_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }

        }
        return errMsg
    }

    private fun getOTPActivityErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        val b = (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
            Constants.APIErrorType.Exception.toString(),
            ignoreCase = true
        )
                || errMsg.equals(Constants.APIErrorType.ServerError.toString(), ignoreCase = true))
        when (api) {
            Constants.API.verifyOTP -> if (b) {
                return context.resources.getString(R.string.otp_verify_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.registrationPhoneOTP, Constants.API.registrationEmailOTP -> if (b) {
                return context.getString(R.string.resend_otp_errmsg)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }

    private fun getLoginActivityErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        if (api == Constants.API.signin) {
            if (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
                    Constants.APIErrorType.Exception.toString(),
                    ignoreCase = true
                )
                    || errMsg.equals(
                    Constants.APIErrorType.ServerError.toString(),
                    ignoreCase = true
                )) {
                return context.getString(R.string.login_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }

    private fun getAddPatientActivityErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        val b = (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
            Constants.APIErrorType.Exception.toString(),
            ignoreCase = true
        )
                || errMsg.equals(Constants.APIErrorType.ServerError.toString(), ignoreCase = true))
        when (api) {
            Constants.API.addPatient -> if (b) {
                return context.resources.getString(R.string.add_patient_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.getDocBoxPatientList -> if (b) {
                return context.resources.getString(R.string.doc_box_patient_list_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.getAthenaDeviceList -> if (b) {
                return context.resources.getString(R.string.athena_list_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.getAppointmentList -> if (b) {
                return context.getString(R.string.appointment_list_err_msg)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.addAppointment -> if (b) {
                return context.getString(R.string.appointment_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }

    private fun getHomeActivityErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        val b = (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
            Constants.APIErrorType.Exception.toString(),
            ignoreCase = true
        )
                || errMsg.equals(Constants.APIErrorType.ServerError.toString(), ignoreCase = true))
        when (api) {
            Constants.API.updateProvider -> if (b) {
                return context.getString(R.string.update_provider_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.logout -> if (b) {
                return context.getString(R.string.logout_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.resetAcuityScore -> if (b) {
                return context.getString(R.string.reset_acuity_score_error_msg)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }

    private fun getChatActivityErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        val b = (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
            Constants.APIErrorType.Exception.toString(),
            ignoreCase = true
        )
                || errMsg.equals(Constants.APIErrorType.ServerError.toString(), ignoreCase = true))
        when (api) {
            Constants.API.getProviders -> if (b) {
                return context.getString(R.string.directry_list_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.startCall -> if (b) {
                return context.getString(R.string.start_call_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.invite -> if (b) {
                return context.getString(R.string.invite_provider_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.dischargePatient -> if (b) {
                val role: String? = PrefUtility().getRole(context)
                return if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                    context.getString(R.string.consultation_complete_err_mgs)
                } else {
                    context.getString(R.string.patient_discharge_err_msg)
                }
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
            Constants.API.getPatientHistory -> if (b) {
                return context.getString(R.string.patient_chat_history_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )
            ) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }

    private fun getSplashActivityErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        if (api == Constants.API.getVersionInfo) {
            if (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
                    Constants.APIErrorType.Exception.toString(),
                    ignoreCase = true
                )
                    || errMsg.equals(
                    Constants.APIErrorType.ServerError.toString(),
                    ignoreCase = true
                )) {
                return context.getString(R.string.verison_info_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }

    private fun getCallActivityErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        if (api.equals(Constants.API.getProviderById)) {
            if (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
                    Constants.APIErrorType.Exception.toString(),
                    ignoreCase = true
                )
                    || errMsg.equals(
                    Constants.APIErrorType.ServerError.toString(),
                    ignoreCase = true
                )) {
                return context.getString(R.string.verison_info_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }

    private fun getAppointmentActivityErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        if (api == Constants.API.addAppointment) {
            if (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
                    Constants.APIErrorType.Exception.toString(),
                    ignoreCase = true
                )
                    || errMsg.equals(
                    Constants.APIErrorType.ServerError.toString(),
                    ignoreCase = true
                )) {
                return context.getString(R.string.appointment_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }

    private fun getHandOffPatientsErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        if (api == Constants.API.submitHandOffAll) {
            if (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
                    Constants.APIErrorType.Exception.toString(),
                    ignoreCase = true
                )
                    || errMsg.equals(
                    Constants.APIErrorType.ServerError.toString(),
                    ignoreCase = true
                )) {
                return context.getString(R.string.api_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }

    private fun getTransferPatientErrorMsg(context: Context, api: Constants.API, errMsg: String): String {
        if (api == Constants.API.transferPatient) {
            if (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
                    Constants.APIErrorType.Exception.toString(),
                    ignoreCase = true
                )
                    || errMsg.equals(
                    Constants.APIErrorType.ServerError.toString(),
                    ignoreCase = true
                )) {
                return context.getString(R.string.api_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )) {
                return context.resources.getString(R.string.api_error)
            }
        }
        if (api == Constants.API.transferFetchingData) {
            if (errMsg.trim { it <= ' ' }.isEmpty() || errMsg.equals(
                    Constants.APIErrorType.Exception.toString(),
                    ignoreCase = true
                )
                    || errMsg.equals(
                    Constants.APIErrorType.ServerError.toString(),
                    ignoreCase = true
                )) {
                return context.getString(R.string.api_error)
            } else if (errMsg.equals(
                    Constants.APIErrorType.SocketTimeoutException.toString(),
                    ignoreCase = true
                )) {
                return context.resources.getString(R.string.api_error)
            }
        }
        return errMsg
    }
}
