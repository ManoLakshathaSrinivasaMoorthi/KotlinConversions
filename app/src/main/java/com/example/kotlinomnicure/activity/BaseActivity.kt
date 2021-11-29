package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.broadcast.InternetConnReceiver
import com.example.kotlinomnicure.customview.CustomDialog
import com.example.dailytasksamplepoc.kotlinomnicure.customview.CustomProgressDialog
import com.example.kotlinomnicure.interfaces.OnInternetConnChangeListener
import com.example.kotlinomnicure.interfaces.OnNetConnectedListener
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import omnicurekotlin.example.com.providerEndpoints.model.Members
import java.io.File
import java.io.IOException

open class BaseActivity : AppCompatActivity(), OnInternetConnChangeListener {
    private val TAG = BaseActivity::class.java.simpleName
    protected var mHandler: Handler? = null
    var toast: Toast? = null
    private var progressDialog: CustomProgressDialog? = null
    private var activity: Activity? = null
    private var internetConnReceiver: InternetConnReceiver? = null
    private var netConnectedListener: OnNetConnectedListener? = null
    private var internetDialog: CustomDialog? = null
    private var autoLogoutDialog: CustomDialog? = null


    fun deleteCache(context: Context) {
        try {
            val cache = context.cacheDir

            deleteDir(cache)
            //            if (appDir.exists()) {
//                String[] children = appDir.list();
//                for (String s : children) {
//                    if (!s.equals("lib")) {
//                        deleteDir(new File(appDir, s));
//                        Log.e("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
//                    }
//                }
//            }
        } catch (e: Exception) {
        }
    }


    fun deleteExternalCache(context: Context) {
        try {
            val cache = context.externalCacheDir
            //            File appDir = new File(cache.getParent());
            deleteExternalCacheDir(cache)
            //            if (appDir.exists()) {
//                String[] children = appDir.list();
//                for (String s : children) {
//                    if (!s.equals("lib")) {
//                        deleteDir(new File(appDir, s));
//                        Log.e("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
//                    }
//                }
//            }
        } catch (e: Exception) {
        }
    }


    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    open fun addMandatoryText(tv: TextView) {
        val labelColor = resources.getColor(R.color.error_color)
        val colorString = String.format("%X", labelColor).substring(2)
        val htmlText = String.format(tv.text.toString() + "<font color='#%s'>*</font>", colorString)
        tv.text = Html.fromHtml(htmlText)
    }


    fun deleteExternalCacheDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteExternalCacheDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
        }


        // True time initialization
        AsyncTask.execute { //TODO your background code
            try {
             //   TrueTime.build().initialize()
            } catch (e: IOException) {
                Log.e(TAG, "Error initializing true time " + e.message)
                e.printStackTrace()
            }
        }
        activity = this
        mHandler = Handler()
        // Registering the internet broadcast
    //    registerInternetBroadcast()
    }

    /**
     * Getting the time from the True time
     * @return
     */
    fun getTime(): Long {
        var time = 0L
        try {
          //  time = TrueTime.now().getTime()
           // Log.i(TAG, "current time $time")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error getting current time " + e.message)
            time = System.currentTimeMillis()
        }
        return time
    }

    /**
     * Close the activity if "android.R.id.home" is cliked
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                run { finish() }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Hide key board
     */
    open fun hideSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun showSoftKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    protected fun addToolbar(toolbar: Toolbar?) {
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE)
            setSupportActionBar(toolbar)
        }
    }

    /**
     * Add back button in action bar
     */
    protected fun addBackButton() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    /**
     * Showing the progress bar - loader with text
     * @param text
     */
    fun showProgressBar(text: String?) {
        dismissProgressBar()
        try {
            progressDialog = CustomProgressDialog(this)
            progressDialog!!.setText(text)
            progressDialog!!.setCancelable(false)
            var isDestyoed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestyoed = isDestroyed
            }
            if (!isFinishing && !isDestyoed) {
                if (progressDialog != null && !progressDialog!!.isShowing) {
                    progressDialog!!.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }




    fun showProgressBar() {
        dismissProgressBar()
        try {
            progressDialog = CustomProgressDialog(this)
            progressDialog!!.setCancelable(false)
            var isDestyoed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestyoed = isDestroyed
            }
            if (!isFinishing && !isDestyoed) {
                if (progressDialog != null && !progressDialog!!.isShowing) {
                    progressDialog!!.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Showing the team member's dialog
     * @param context
     * @param members
     * @param consultProvider
     */
    fun showTeamMembersDialog(
        context: Context?,
        members: List<Members?>?,
        consultProvider: ConsultProvider
    ) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        val inflater = this.layoutInflater
        val mDialogView: View = inflater.inflate(R.layout.contact_team_dialog, null)
        dialog.setContentView(mDialogView)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val close = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val membersRecycler = dialog.findViewById<View>(R.id.membersRecycler) as RecyclerView
        val grpAudio = dialog.findViewById<View>(R.id.grpAudioCall) as Button
        val grpVideo = dialog.findViewById<View>(R.id.grpVideoCall) as Button

        // Start call method is triggered

        //Close click listener
        close.setOnClickListener { dialog.dismiss() }
        // Setting the members adapter

        dialog.show()
    }




    fun dismissProgressBar() {
        try {
            var isDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (isDestroyed()) {
                    isDestroyed = true
                }
            }
            if (!isFinishing && !isDestroyed && progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            progressDialog = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Setting the title in action bar
     * @param str
     */
    protected fun setTitle(str: String?) {
        if (supportActionBar != null) {
            supportActionBar!!.title = str
        }
    }

    /**
     * Unregister internet broadcast receiver
     */
    override fun onDestroy() {
        super.onDestroy()
//        unregisterBroadcastReceiver()
        //        deleteCache(getApplicationContext());
//        deleteExternalCache(getApplicationContext());
      //  Log.i(TAG, "onDestroy: " + getCurrentActivityName())
    }


    private fun unregisterBroadcastReceiver() {
//        unregisterReceiver(internetConnReceiver)
    }

    override fun onResume() {
        super.onResume()
     //   Log.d(TAG, "onResume: " + getCurrentActivityName())
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        //Toast.makeText(this,getCurrentActivityName(),Toast.LENGTH_LONG).show();
     //   Log.d(TAG, "onStart: " + getCurrentActivityName())
    }

    override fun onStop() {
        super.onStop()
      //  Log.d(TAG, "onStop: " + getCurrentActivityName())
    }

    /**
     * Get the current activity name
     * @return
     */
  /*  private fun getCurrentActivityName(): String {
        if (activity is SplashActivity) {
            return SplashActivity::class.java.simpleName
        } else if (activity is LoginActivity) {
            return LoginActivity::class.java.simpleName
        } else if (activity is RegistrationActivity) {
            return RegistrationActivity::class.java.getSimpleName()
        } else if (activity is OTPActivity) {
            return OTPActivity::class.java.simpleName
        } else if (activity is HomeActivity) {
            return HomeActivity::class.java.simpleName
        } else if (activity is ChatActivity) {
            return ChatActivity::class.java.simpleName
        } else if (activity is AddPatientActivity) {
            return AddPatientActivity::class.java.simpleName
        } else if (activity is AddPatientVitalsActivity) {
            return AddPatientVitalsActivity::class.java.simpleName
        } else if (activity is ResetPasswordActivity) {
            return ResetPasswordActivity::class.java.getSimpleName()
        } else if (activity is TeamGroupChatActivity) {
            return TeamGroupChatActivity::class.java.getSimpleName()
        } else if (activity is TrainingMaterialActivity) {
            return TrainingMaterialActivity::class.java.getSimpleName()
        } else if (activity is SystemAlertActivity) {
            return SystemAlertActivity::class.java.getSimpleName()
        } else if (activity is GroupCallActivity) {
            return GroupCallActivity::class.java.getSimpleName()
        } else if (activity is PatientDetailActivity) {
            return PatientDetailActivity::class.java.simpleName
        } else if (activity is TransferPatientActivity) {
            return TransferPatientActivity::class.java.simpleName
        } else if (activity is MyVirtualTeamsActivity) {
            return MyVirtualTeamsActivity::class.java.getSimpleName()
        } else if (activity is HandOffPatientsActivity) {
            return HandOffPatientsActivity::class.java.simpleName
        } else if (activity is RemoteHandOffActivity) {
            return RemoteHandOffActivity::class.java.simpleName
        } else if (activity is FilterActivity) {
            return FilterActivity::class.java.getSimpleName()
        } else if (activity is RegistrationSuccessActivity) {
            return RegistrationSuccessActivity::class.java.simpleName
        } else if (activity is InitVideoCallActivity) {
            return InitVideoCallActivity::class.java.getSimpleName()
        } else if (activity is RingingActivity) {
            return RingingActivity::class.java.getSimpleName()
        } else if (activity is NetworkTestActivity) {
            return NetworkTestActivity::class.java.getSimpleName()
        } else if (activity is CallActivity) {
            return CallActivity::class.java.getSimpleName()
        } else if (activity is CallOptionsActivity) {
            return CallOptionsActivity::class.java.getSimpleName()
        } else if (activity is SettingsActivity) {
            return SettingsActivity::class.java.getSimpleName()
        }
        return activity!!.javaClass.simpleName
    }*/

    /**
     * Internet Connection change listener
     * @param intent
     */
    override fun onConnectionChanged(intent: Intent?) {
        val isConnected: Boolean = activity?.let { UtilityMethods().isInternetConnected(it) } == true
        Log.i(TAG, "onConnectionChanged: is Internet connected...$isConnected")
        if (activity is SplashActivity) {
            netConnectedListener = this as SplashActivity
        }
        if (isConnected) {
            // Dismiss internet dialog if connected
            dismissInternetDialog()
            if (netConnectedListener != null) {
                netConnectedListener!!.onConnected()
            }
        } else {
            // Showing the internet dialog if no internet connection found
          //  showInternetDialog()
        }
    }



    /**
     * Show auto logout dialog
     */


    /**
     * Clearing the prefenrence values
     */


    /**
     * Dismiss internet dialog if connected
     */
    private fun dismissInternetDialog() {
        try {
            var isDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed = isDestroyed()
            }
            if (internetDialog != null && internetDialog!!.isShowing && !isDestroyed && !isFinishing) {
                internetDialog!!.dismiss()
                internetDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
