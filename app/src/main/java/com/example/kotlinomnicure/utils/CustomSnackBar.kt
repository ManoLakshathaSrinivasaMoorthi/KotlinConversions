package com.example.kotlinomnicure.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.kotlinomnicure.R

class CustomSnackBar {
    private lateinit var patientStatus: String
    private lateinit var patientName: String
    private var patientId: Long = 0

    /**
     * This method auto dismiss snackbar
     */
    var handler: Handler? = null
    private val TAG = javaClass.name
    private val mContext: Context? = null
    private var activity: Activity? = null

    /**
     * This method returns the snackbar view set by setLayout
     * This method must be invoke after setLayout in order to avoid null pointer
     */
    private var snackBarView: View? = null
    private var rootView: View? = null
    private var isFocusable = false
    private var asDropDown = false
    private var fillScreen = false
    private var popupWindow: PopupWindow? = null
    private var showSnackBar = false
    var gravity = TOP

    /**
     * Set this delay for showing notification snackbar
     * By defauly delay is 1500
     *
     * @param delay
     */
    var delay = 1500

    /**
     * Set this to auto dismiss  notification snackbar
     * By defauly duration is 0
     */
    var duration = 0
    var intentClick = 0
    private var customSnackBarType = 0
    private var textMessage: TextView? = null
    private var imgClose: ImageView? = null
    private var animationStyle: Int? = null
    private var layout = 0

    //Constructors
    constructor()
    constructor(view: View?, activity: Activity?) {
        // create the popup window
        this.activity = activity
        rootView = view
    }

    private fun setCustomSnackBarLayout(type: Int) {
        customSnackBarType = type
        var result = 0
        when (customSnackBarType) {
            1 -> result = R.layout.custom_snackbar_layout
            2 -> result = R.layout.custom_snackbar_error_layout
        }
        layout = result
    }

    private fun setSnackBarText(text: String) {
        when (customSnackBarType) {
            1 -> {
                textMessage = snackBarView!!.findViewById(R.id.tVSuccess)
                textMessage!!.text = text
            }
            2 -> {
                textMessage = snackBarView!!.findViewById(R.id.tVWarning)
                textMessage!!.text = text
            }
        }
    }


    fun setPatientDetails(id:Long,name:String,status:String)
    {
        if (instance == null) {
            instance =  CustomSnackBar();
        }
        instance!!.patientId = id;
        instance!!.patientName = name;
        instance!!.patientStatus = status;
    }
    fun isAsDropDown(): Boolean {
        return asDropDown
    }

    fun setAsDropDown(asDropDown: Boolean) {
        this.asDropDown = asDropDown
    }

    fun isFillScreen(): Boolean {
        return fillScreen
    }

    fun setFillScreen(fillScreen: Boolean) {
        this.fillScreen = fillScreen
    }

    /**
     * Hide close icon if duration is already set
     */
    private fun setCancelButton(clickIntent: Int) {
        imgClose!!.setOnClickListener {
            handler!!.removeCallbacksAndMessages(null)
            popupWindow!!.dismiss()
            dismissSnackBar(clickIntent)
            instance!!.activity!!.window.statusBarColor =
                instance!!.activity!!.resources.getColor(R.color.statusbar_color)
        }
    }

    /**
     * Initialize the snackbar view
     *
     * @param layout
     */
    fun setLayout(layout: Int) {
        if (activity != null) {
            val inflater =
                activity!!.baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            snackBarView = inflater.inflate(layout, null)
            imgClose = snackBarView!!.findViewById(R.id.imgClose)
        }
    }

    fun dismissSnackBar(clickIntent: Int) {
        try {
            popupWindow!!.dismiss()
            showSnackBar = false
            asDropDown = false
            // Giving intent to respect activity based on the type of clickIntent
            when (clickIntent) {
                1 -> {
                    /* val intent = Intent(instance!!.activity, HomeActivity::class.java)
                     //                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                     instance!!.activity!!.startActivity(intent)
                     instance!!.activity!!.finish()*/
                }
                2 -> {
                    //  LogoutHelper(instance!!.activity, instance!!.rootView).doLogout()
                }
                3 -> {
                    /*val intent = Intent(instance!!.activity, HomeActivity::class.java)
                    intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "completed")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    instance!!.activity!!.startActivity(intent)
                    instance!!.activity!!.finish()*/
                }
                4 -> {

                    instance!!.activity!!.setResult(Activity.RESULT_OK)
                    instance!!.activity!!.finish()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    /**
     * This method create a new popup window
     * This method must be called after setLayout
     * focusable default value is false
     */
    fun show() {
        if (activity != null) {

            showSnackBar = true
            //Changing status bar color as per snack bar type
            if (customSnackBarType == 1) {
                instance!!.activity!!.window.statusBarColor =
                    instance!!.activity!!.resources.getColor(R.color.successBar)
            } else {
                instance!!.activity!!.window.statusBarColor =
                    instance!!.activity!!.resources.getColor(R.color.acuity_high_border)
            }
            val width = LinearLayout.LayoutParams.MATCH_PARENT
            var height = LinearLayout.LayoutParams.WRAP_CONTENT
            if (fillScreen) {
                height = LinearLayout.LayoutParams.MATCH_PARENT
            }
            popupWindow = PopupWindow(snackBarView, width, height, isFocusable)
            if (animationStyle != null) {
                popupWindow!!.animationStyle = animationStyle!!
            }
            rootView!!.post {
                if (asDropDown) {
                    popupWindow!!.showAsDropDown(rootView, 0, 0)
                } else {
                    popupWindow!!.showAtLocation(rootView, gravity, 0, 0)
                }
            }
            autoDismiss(duration)
            //            }
        }
    }

    private fun setAnimationstyle() {
        /* if (gravity == TOP) animationStyle = R.style.topAnimation
         //        else if (gravity == BOTTOM)
 //            animationStyle = R.style.bottomAnimation;*/
    }

    fun setCustomAnimationStyle(customAnimationStyle: Int) {
        animationStyle = customAnimationStyle
    }

    private fun autoDismiss(duration: Int) {
        if (duration > 0) {
            handler = Handler()
            handler!!.postDelayed({
                dismissSnackBar(intentClick)
                // Default status bar color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    instance!!.activity!!.window.statusBarColor =
                        instance!!.activity!!.resources.getColor(R.color.statusbar_color)
                }
            }, duration.toLong())
        }
    }

    interface SnackBarListener {
        fun onViewClickListener(view: View?)
    }

    companion object {
        var TOP = Gravity.TOP
        var BOTTOM = Gravity.BOTTOM
        var SUCCESS = 1
        var WARNING = 2
        private var instance: CustomSnackBar? = null
        fun make(
            view: View?,
            activity: Activity?,
            snackBarType: Int,
            message: String,
            position: Int,
            clickIntent: Int
        ): CustomSnackBar? {
            if (instance == null) {
                instance = CustomSnackBar()
            } else {
                if (instance!!.showSnackBar) {
                    instance!!.dismissSnackBar(clickIntent)
                }
            }
            instance!!.rootView = view

            instance!!.activity = activity
            instance!!.setCustomSnackBarLayout(snackBarType)
            instance!!.setLayout(instance!!.layout)
            instance!!.setSnackBarText(message)
            instance!!.duration = 0
            instance!!.intentClick = clickIntent
            instance!!.gravity = position
            instance!!.setCancelButton(clickIntent)
            instance!!.setAnimationstyle()
            instance!!.fillScreen = false
            instance!!.asDropDown = false
            return instance
        }

        /**
         * This constructor is used for auto dismiss
         */
        fun make(
            view: View?,
            activity: FragmentActivity?.() -> Unit,
            snackBarType: Int,
            message: String?,
            position: Int,
            duration: Int,
            clickIntent: Int
        ): CustomSnackBar? {
            if (instance == null) {
                instance = CustomSnackBar()
            } else {
                if (instance!!.showSnackBar) {
                    instance!!.dismissSnackBar(clickIntent)
                    instance!!.handler!!.removeCallbacksAndMessages(null)
                }
            }
            instance!!.rootView = view
            instance!!.activity = activity
            instance!!.setCustomSnackBarLayout(snackBarType)
            instance!!.setLayout(instance!!.layout)
            if (message != null) {
                instance!!.setSnackBarText(message)
            }
            instance!!.duration = duration
            instance!!.intentClick = clickIntent
            instance!!.gravity = position
            instance!!.setCancelButton(clickIntent)
            instance!!.setAnimationstyle()
            instance!!.fillScreen = false
            instance!!.asDropDown = false
            return instance
        }

        fun getInstance(): CustomSnackBar? {
            if (instance == null) {
                instance = CustomSnackBar()
            }
            return instance
        }
    }
}

