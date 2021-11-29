package com.example.kotlinomnicure.utils

import android.os.Environment
import android.provider.Settings.Global.getString
import com.example.kotlinomnicure.OmnicureApp
import com.example.kotlinomnicure.R
import java.io.File

public  interface Constants {
 companion object {
     var APP_NAME = "omnicure"
     var SPLASH_SCREEN_TIME = 1000
     var SNACKBAR_LENGTH_LONG = 3000
     var SNACKBAR_LENGTH_MEDIUM = 2000
     var SNACKBAR_LENGTH_SHORT = 1000
     var RIDER_IMAGE_COMPRESSION_RATIO = 100
     var MIN_AGE = 18
     var COUNTRY_STD_CODE = "+1"
     var PHONE_PATTERN = "\\d+"
     var PROFILE_IMG_DIR = "Profile"
     var IMAGE_MAX_SIZE = 100

     var HEALTH_MONITOR_TIMER = 2 * 1000 * 60 //2 minutes

     var AUTO_LOGOUT_TIME = 30 //in minutes

     var AUTO_LOGOUT_TIME_IN_MILLIS = 30 * 1000 * 60 //in minutes

     var KB = 1024
     var MB = KB * 1024
     var VIDEO_MAX_SIZE = 50
     var US_COUNTRY_CODE = "+1"
     var THIRTEE_MINUTES_IN_MILIS = 1000 * 60 * 30

     var COUNTRY_FLAG_DIR = "flags"
     var FILE_PROVIDER_NAME = "fileprovider001"
     var PATIENT_FCM_TOPIC_EXT_TEST = "ext_test_patient_fcm_topic"
     var PATIENT_FCM_TOPIC = "patient_fcm_topic"
     var PATIENT_FCM_TOPIC_TEST = "test_patient_fcm_topic"

     //    String FEEDBACK_URL = "https://app.smartsheet.com/b/form/6de80d6a1c04455d91f529635af6d76f";
     //    String FEEDBACK_URL = "https://docs.google.com/forms/d/e/1FAIpQLSd_1ScLtbfGTZU9spYiiMdZIW2rEnixoR2YSqN_CjR_zErD0A/viewform";
     var HOSPITAL_TYPE_HOME = "Home"
     var USER_AGREEMENT_LINK = "https://www.omnicuremd.com/privacy.html"
     var API_ERROR = "Something went wrong, Please Try Again"
 }

    enum class APIErrorType {
        Exception, ServerError, NoDataError, SocketTimeoutException, UnknownError
    }

    enum class Gender {
        Male, Female, Others
    }

    enum class ProviderRole {
        BD, RD, BOTH
    }

    enum class API {
        register, signin, verifyOTP, getHospital, addPatient, getProviders, updateProvider, acceptInvite, getDocBoxPatientList, logout, getVersionInfo, startCall, invite, dischargePatient, getProviderById, getAthenaDeviceList, resetAcuityScore, getPatientHistory, addAppointment, getAppointmentList, registrationPhoneOTP, registrationEmailOTP, getPatientDetails, getHandOffList, submitHandOffAll, transferPatient, transferFetchingData, patientSignUp, forgotPassword, changePassword, getTeam, checkPassword, updateNotification
    }

    enum class OsType {
        Website, IPHONE, ANDROID
    }

    enum class ProviderStatus {
        Active, OffLine, Suspend, Terminate, VerifyPending, Rejected, AutoLock, Approved
    }


    enum class ConnectionStatus {
        Connected, Disconnected
    }

    enum class CurrentActivity {
        SplashAct, LoginAct, RegAct, HomeAct, ChatAct, OTPAct, PatientAct
    }

    enum class MessageStatus {
        Sent, Delivered, Read
    }

    enum class PatientCondition {
        Alert, Voice, Pain, Unresponsive
    }

    enum class CovidPositive {
        Yes, No, Pending
    }

    enum class AcuityLevel {
        Low, Medium, High, NA
    }

    enum class AcuityLevelColor {
        GREEN, AMBER, RED
    }

    enum class ChangeLanguage {
        English, Spanish
    }

    enum class BpOption {
        ResetAcuity, TransferPatient, NA
    }

    enum class RpOption {
        HandoffPatient, CompleteConsultation, NA
    }

    enum class UrgencyLevel {
        Urgent, NotUrgent, NA
    }

    enum class ConsultTime {
        Fifteen, Thirty, OverThirtyMin, OneHour, OverOneHour, NA
    }

    enum class PatientStatus {
        //        Active, Pending, Discharged, Patient, Invited, Handoff, HomeCare, New, HandoffPending, NA, Completed, Exit
        Active, Pending, Discharged, Patient, Invited, Handoff, HomeCare, New, HandoffPending, NA, Completed, Exit, Unassigned
    }

    enum class Month {
        Jan, Feb, March, April, May, June, July, Aug, Sep, Oct, Nov, Dec
    }

    enum class AppointmentStatus {
        New, Patient, Completed
    }

    enum class OTPChannel {
        SMS, EMAIL
    }

    interface KeyHardcodeToken {
        companion object {
            const val HARD_CODE_TOKEN = "jhguyg398798574bjhb8y7987y"
            const val LCP_TYPE_HOME = "H"
        }
    }

    interface IntentKeyConstants {
        companion object {
            const val PREVIOUS_ACTIVITY = "previous_activity"
            const val PROVIDER_ID = "provider_id"
            const val PDF_URL = "pdf_url"
            const val IMAGE_URL = "image_url"
            const val VIDEO_URL = "video_url"
            const val FILE_EXTENSION = "file_extension"
            const val SHOW_TERMS_BUTTON = "show_terms_button"
            const val SELECTED_HOSPITAL = "selected_hospital"
            const val MOBILE_NO = "mobile_no"
            const val IS_INVITE_NOTIFICATION = "is_invite_notification"
            const val CURRENT_TAB = "current_tab"
            const val IS_PATIENT_URGENT = "is_patient_urgent"
            const val INVITATION = "invitation"
            const val COMPLETED = "completed"
            const val PROVIDER_EMAIL = "provider_email"
            const val FIRST_NAME = "first_name"
            const val LAST_NAME = "last_name"
            const val DOB = "dateOfBirth"
            const val GENDER = "gender"
            const val WARD = "ward"
            const val IS_RELATIVE = "is_relative"
            const val REL_FNAME = "rel_fname"
            const val REL_LNAME = "rel_lname"
            const val RELATION = "relation"
            val PASSWORD: String? =  OmnicureApp().getAppContext()?.getString(R.string.pass)
            const val PATIENT_ID = "patientId"
            const val TEAM_NAME = "teamName"
            const val FEEDBACK_URL = "feedbackForm"
            const val DOCBOX_ID = "docbox_id"
            const val DOCBOX_MANAGER_ID = "docbox_manager_id"
            const val EMAIL = "email"
            const val OTP_TOKEN = "otp_token"
            const val TARGET_PAGE = "target_page"
            const val PHONE_NO = "phone_No"
            const val COUNTRY_CODE = "country_code"
            const val FROM_PAGE = "from_page"
            const val DOC_BOX_ID = "docBoxId"
            const val OOB_CODE = "oobCode"
            const val IMAGE = "image"
            const val VIDEO = "video"
            const val SCREEN_TYPE = "screenType"
            const val SCREEN_CENSUS = "census"
            const val SCREEN_CENSUS_START_CONSULT = "start_consult"
            const val SCREEN_DASHBOARD = "fromDashboard"
            const val HOSPITAL_ID = "hospitalID"
            const val HOSPITAL_NAME = "hospitalName"
            const val HOSPITAL_ADDRESS = "hospitalAddress"
            const val REFRESH_PAGE = "refresh_page"
            const val AUDIT_ID = "audit_id"
            const val PATIENT_OBJECT = "patient_obj"
            const val MESSAGE_OBJECT = "message_obj"
            const val PATIENT_HISTORY = "patient_history"
            const val PATIENT_STATUS = "patientstatus"
            const val STR_UID = "uiid"
            const val MESSAGE_CHILD = "m_child"
            const val CONSULT_PROVIDER = "consult_provider"
        }
    }

    interface SharedPrefConstants {
        companion object {
            const val PROVIDER_OBJECT = "provider_object"
            const val USER_MOBILE_NO = "user_mobile_no"
            const val USER_ID = "user_id"
            const val USER_ID_PRIMARY = "user_id_primary"
            const val R_PROVIDER_TYPE = "remoteProviderType"
            val PASSWORD: String? = OmnicureApp().getAppContext()?.getString(R.string.pass)
            val DUMMYPASSWORD: String? = OmnicureApp().getAppContext()?.getString(R.string.dummy_pass)
            const val FINGERPRINTFLAG = "finerprint"
            const val MUTILPLECLICK = "MUTILPLECLICK"
            const val ENCRYPTIONKEY = "encryptionKey"
            const val LOCKFP = "LOCKFP"
            const val LOCKFPemailchange = "LOCKFPemailchange"
            const val TOKEN = "token"
            const val NAME = "name"
            const val FIRST_NAME = "first_name"
            const val LAST_NAME = "last_name"
            const val HOSPITAL_NAME = "hospital_name"
            const val HOSPITAL_ID = "hospital_id"
            const val ROLE = "role"
            const val PROVIDER_STATUS = "provider_status"
            const val FCM_TOKEN = "fcm_token"
            const val PROFILE_IMG_URL = "profile_img_url"
            const val EMAIL = "email"
            const val APP_ACTIVE_TIME = "app_active_time"
            const val AUTO_LOGOUT_TIME = "auto_logout_time"
            const val HEALTH_MONITOR_TIMER = "health_monitor_timer"
            const val IS_ERROR = "is_error"
            const val SESSION_TIMEOUT_MESSAGE = "session_timeout_message"
            const val SESSION_TIMEOUT_TITLE = "session_timeout_title"
            const val RESET_ACUITY_PATIENT_MAP = "reset_acuity_patient_map"
            const val LCP_TYPE = "local_care_provider_type"
            const val LOGOUT_FLAG = "logout_flag"
            const val TUTORIAL_URL = "tutorial_url"
            const val FEEDBACK_URL = "feedback_url"
            const val FIREBASE_UID = "firebase_uid"
            const val FIREBASE_IDTOKEN = "firebase_idtoken"
            const val FIREBASE_REFRESH_TOKEN = "firebase_refresh_token"
            const val RP_ID_NOTIFICATION = "rp_id_notification"
            const val CHANGE_LANGUAGE = "change_language"
            const val BP_REGION_ADDRESS = "region_address"

            // Custom alerts
            const val ALERT_NOTIFTY_ID = "notifi_id"
            const val ALERT_ACUITY = "alert_acuity"
            const val ALERT_ACUITY_STATUS = "alert_acuity_status"
            const val ALERT_MOBILE_ACUITY_STATUS = "alert_mobile_acuity_status"
            const val ALERT_ECONSULT = "alert_econsult"
            const val ALERT_CENSUS = "alert_cencus"
            const val ALERT_HANDOFF = "alert_handoff"
            const val ALERT_PATIENT_ADDED = "alert_patient_added"

            //AES
            const val AES_KEY = "AES_Key"
            const val AES_API_KEY = "AES_API_Key"

            //AGORA app id
            const val AGORA_APP_ID = "agora_appid"
            const val AGORA_CERTIFICATE = "agora_certificate"

            // For move to chat - Urgent messasges
            const val MOVE_TO_CHAT_ID = "move_to_chat_id"
            const val LOGIN_TIME = "login_time"

            // To disable/stop showing push notification to the user, when session expired
            const val DISABLE_NOTIFICATION = "disable_notification"
        }
    }

    interface ValidateKey {
        companion object {
            const val ValKeyZero = 0
            const val ValKeyOne = 1
        }
    }

    interface redirectValidation {
        companion object {
            const val EMAIL = "tempEmail"
            val PASSWORD: String? = OmnicureApp().getAppContext()?.getString(R.string.temppass)
            const val ID = "tempId"
        }
    }

    interface FileStorageConstants {
        companion object {
            const val USER = "user"
        }
    }

    interface ActivityRequestCodes {
        companion object {
            const val ADD_PATIENT_REQ_CODE = 101
            const val FILTER_REQ_CODE = 102
            const val IMAGE_REQ_CODE = 103
        }
    }

    interface PermissionCondes {
        companion object {
            const val CAMERA_STORAGE_PERMISSION_CODE = 201
            const val STORAGE_PERMISSION_CODE = 202
            const val ONLY_STORAGE_PERMISSION_CODE = 203
            const val PHONE_CALL_PERMISSION = 204
            const val STORAGE_PERMISSION_CODE_DOCUMENT = 205
            const val STORAGE_PERMISSION_CODE_VIDEO = 206
        }
    }

    interface FCMMessageType {
        companion object {
            const val NEW_MESSAGE = "new_message"
            const val VIDEO_CALL = "video_call"
            const val AUDIO_CALL = "audio_call"
            const val INVITATION = "Invitation"
            const val PATIENT_ASSIGNED = "Patient Assigned"
            const val INVITATION_DELETE = "InvitationDelete"
            const val INVITATION_ACCEPT = "InvitationAccept"
            const val DISCHARGED = "Discharged"
            const val CALLER_BUSY = "CallerBusy"
            const val CALLER_REJECT = "CallerReject"
            const val CALLER_NOT_ANSWER = "CallerNotAnswer"
            const val SOS = "SOS"
            const val SOS_DISMISS = "sos_dismiss"
            const val SOS_ATTEND = "sos_attend"
            const val HANDOFF_INITIATED = "Handoff Initiated"
            const val PATIENT_TRANSFER = "Patient transferred"
            const val SYSTEM_ALERT = "System Alert"
            const val DENIED_USER = "Denied User"
            const val LOGOUT_EXCEEDED = "Logout"
            const val LOCKED_USER = "Locked User"
            val PASSWORD_LOCK: String? = OmnicureApp().getAppContext()?.getString(R.string.lockpass)
            const val TEMP_LOCK = "Temporary lock"
        }
    }

    interface ImageCaptureConstants {
        companion object {
            const val PICKFILE_REQUEST_CODE = 1
            const val START_CAMERA_REQUEST_CODE = 2
            const val SOURCE = "source"
            const val OPEN_INTENT_PREFERENCE = "selectContent"
            const val IMAGE_BASE_PATH_EXTRA = "ImageBasePath"
            const val OPEN_CAMERA = 4
            const val OPEN_MEDIA = 5
            const val SCANNED_RESULT = "scannedResult"
            val IMAGE_PATH = Environment
                .getExternalStorageDirectory().path + File.separator + Constants.APP_NAME
            const val SELECTED_BITMAP = "selectedBitmap"
        }
    }

    interface VersionInfoKeys {
        companion object {
            const val AUTO_LOGOUT_TIME = "logoutServerTimerinMilli"
            const val HEALTH_MONITOR_TIMER = "logoutAppTimerinMilli"
        }
    }

    interface NotificationIds {
        companion object {
            const val MSG_NOTIFICATION_ID = 1001
            const val NOTIFICATION_ID = 1002
            const val DISCHARGE_NOTIFICATION_ID = 1003
        }
    }
    /*enum MessageType{
        newMessage,videoCall,Invitation,InvitationAccept,Discharged
    }*/
}
