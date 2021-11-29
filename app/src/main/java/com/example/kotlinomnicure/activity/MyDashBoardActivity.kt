package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewStub
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.volley.toolbox.ImageLoader
import com.example.dailytasksamplepoc.R

import com.example.dailytasksamplepoc.databinding.ActivityDashboardBinding
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model.AddNotificationDataRequest
import com.example.dailytasksamplepoc.kotlinomnicure.model.ConsultProvider
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.CensusHospitalListViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.CensusWardListViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HomeViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.helper.NotificationHelper
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.activity.ActivityConsultChart
import com.mvp.omnicure.kotlinactivity.activity.ActivityPatientCensusHospital
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.URL
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

class MyDashBoardActivity : BaseActivity() {

    //variables
    private val TAG: String = MyDashBoardActivity::class.java.getSimpleName()
    protected var binding: ActivityDashboardBinding? = null
    var linearMyConsult: LinearLayout? = null
    var linearProviderDirectory: LinearLayout? = null
    var linearMyProfile: LinearLayout? = null
    var linearRequest: LinearLayout? = null
    var linearPendingConsults: LinearLayout? = null
    var linearPatientCensus: LinearLayout? = null
    var txtHospitalCount: TextView? = null
    var txt_active_count: TextView? = null
    var txt_pending_count: TextView? = null
    private var imageURL: String? = null
    private var strName: kotlin.String? = null
    private var strProfileName: String? = null
    private var strDashboardHospitalAddress: kotlin.String? = null
    private var viewModel: CensusHospitalListViewModel? = null
    private var wardListViewModel: CensusWardListViewModel? = null
    private var strHospitalID: Long? = null

    private var homeViewModel: HomeViewModel? = null

    // Firebase instance variables
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private var mProviderUid: String? = null
    private var acuityFirebasePath: String? = null
    private val eConsultFirebasePath: String? = null
    private val censusFirebasePath: String? = null
    private val handOffFirebasePath: String? = null
    private val newPatientFirebasePath: String? = null
    private val firebaseAcuity: String? = null
    private var uid: String? = null
    private var acuityMessagesRef: DatabaseReference? = null
    private var acuityQuery: Query? = null
    private val previousDataSnapshot: DataSnapshot? = null
    private val previuosAcuitySnapshot = false
    private var pendingPath: DatabaseReference? = null
    private var notifiProviderId: Long = 0
    private val random = SecureRandom()
    private val acuityHashMap: LinkedHashMap<Long, ConsultProvider?> =
        LinkedHashMap<Long, ConsultProvider?>()
    private val acuityModelArrayList: LinkedHashMap<Long, ConsultProvider?> =
        LinkedHashMap<Long, ConsultProvider?>()
    private val providerHashMap: LinkedHashMap<String?, ConsultProvider?> =
        LinkedHashMap<String?, ConsultProvider?>()
    private var itemCount: Long = -1
    private val providerFilteredListactive: MutableList<ConsultProvider?> =
        ArrayList<ConsultProvider?>()
    private val providerFilteredListpending: MutableList<ConsultProvider?> =
        ArrayList<ConsultProvider?>()
    private var messagesRef: DatabaseReference? = null
    var childEventListener: ChildEventListener? = null
    var childEventListenernewpending: ChildEventListener? = null
    private val filterPatientStatus: Constants.PatientStatus? = null
    private var strDesignation: String? = null
    private var mPath: String? = null
    private var mPathnewpendng: String? = null
    private val messagesRef1: DatabaseReference? = null
    private var mProviderUid1: String? = null

    var dbListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val gson = Gson()
            val str = gson.toJson(dataSnapshot.value)
            if (str == "true") {
                txt_pending_count!!.text = "New pending"
                txt_pending_count!!.setTextColor(resources.getColor(R.color.red))
            } else if (str == "false") {
                txt_pending_count!!.text = ""
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    //on create method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        viewModel = ViewModelProvider(this).get(CensusHospitalListViewModel::class.java)
        wardListViewModel = ViewModelProvider(this).get(CensusWardListViewModel::class.java)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        strName = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FIRST_NAME, "")
        //Setting custom notification listeners for Local In-App notifications
        setCustomNotificationListeners()
        setViews()
        onClickListener()

        notifiProviderId = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID_PRIMARY, -1)

        onConnectionChanged(Intent())


    }

    fun onClickListener() {
        linearMyConsult!!.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        linearProviderDirectory!!.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent =
                Intent(this, RemoteProviderDirectoryActivity::class.java)
            startActivity(intent)
        }
        binding!!.profileLayout.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
        }
        binding!!.imageLayout.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Setting up the custom notification listeners
     */
    private fun setCustomNotificationListeners() {


        // Initialize Firebase Auth
        uid = PrefUtility().getFireBaseUid(this)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth!!.currentUser
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        if (mFirebaseUser == null || uid == "") {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Provider Id
        mProviderUid = uid

        // Adding the provider acuity path
        acuityFirebasePath = "providers/$mProviderUid/active"

        acuityMessagesRef = mFirebaseDatabaseReference!!.child(acuityFirebasePath!!)
        acuityMessagesRef!!.keepSynced(true)
        acuityQuery = acuityMessagesRef!!.orderByChild("time")


    }

    fun setViews() {
        strProfileName = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        val strRole: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        val strDesignation: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        val stub = findViewById(R.id.layout_stub) as ViewStub
        val inflatedView: View
        if (strRole == "RD" && strDesignation == "MD/DO") {
            stub.layoutResource = R.layout.layout_dashboard_rp
            inflatedView = stub.inflate()
            linearMyProfile = inflatedView.findViewById<View>(R.id.linearMyProfile) as LinearLayout
            linearPendingConsults =
                inflatedView.findViewById<View>(R.id.linearPendingConsults) as LinearLayout
            linearPatientCensus =
                inflatedView.findViewById<View>(R.id.linearPatientCensus) as LinearLayout
            txtHospitalCount = inflatedView.findViewById<View>(R.id.txtHospitalCount) as TextView
            txt_active_count = inflatedView.findViewById<View>(R.id.txt_active_count) as TextView
            txt_pending_count = inflatedView.findViewById<View>(R.id.txt_pending_count) as TextView
            getHospitalList()
            getconsultlist(false)
            getconsultlistnewpending()
            linearPendingConsults!!.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "pending")
                startActivity(intent)


            })
            linearPatientCensus!!.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, ActivityPatientCensusHospital::class.java)
                startActivity(intent)
            })
            linearMyProfile!!.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            })
            binding?.profileLayout?.setVisibility(View.GONE)
        } else if (strRole == "RD" && strDesignation != "MD/DO") {
            stub.layoutResource = R.layout.layout_dashboard_rp_others
            inflatedView = stub.inflate()
            linearPatientCensus =
                inflatedView.findViewById<View>(R.id.linearPatientCensus) as LinearLayout
            txtHospitalCount = inflatedView.findViewById<View>(R.id.txtHospitalCount) as TextView
            txt_active_count = inflatedView.findViewById<View>(R.id.txt_active_count) as TextView
            getHospitalList()
            getconsultlist(false)
            linearPatientCensus!!.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, ActivityPatientCensusHospital::class.java)
                startActivity(intent)
            })
            binding?.profileLayout?.setVisibility(View.VISIBLE)
        } else {
            stub.layoutResource = R.layout.layout_dashboard_bp
            inflatedView = stub.inflate()
            strHospitalID = PrefUtility().getLongInPref(
                this,
                Constants.SharedPrefConstants.HOSPITAL_ID,
                0L
            )
            linearPatientCensus =
                inflatedView.findViewById<View>(R.id.linearPatientCensus) as LinearLayout
            txtHospitalCount = inflatedView.findViewById<View>(R.id.txtHospitalCount) as TextView
            txt_active_count = inflatedView.findViewById<View>(R.id.txt_active_count) as TextView
            getBPCensusWardList()
            getconsultlist(true)
            linearPatientCensus!!.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, ActivityPatientCensusWard::class.java)
                intent.putExtra(
                    Constants.IntentKeyConstants.SCREEN_TYPE,
                    Constants.IntentKeyConstants.SCREEN_DASHBOARD
                )
                val tempHopAddress: String? = PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.BP_REGION_ADDRESS,
                    ""
                )
                if (TextUtils.isEmpty(strDashboardHospitalAddress)) {
                    intent.putExtra(Constants.IntentKeyConstants.HOSPITAL_ADDRESS, "")
                } else {
                    intent.putExtra(Constants.IntentKeyConstants.HOSPITAL_ADDRESS, tempHopAddress)
                }

                startActivity(intent)


            })
            binding!!.profileLayout.setVisibility(View.VISIBLE)
        }
        linearMyConsult = inflatedView.findViewById<View>(R.id.linearMyConsult) as LinearLayout
        linearProviderDirectory =
            inflatedView.findViewById<View>(R.id.linearProviderDirectory) as LinearLayout
        linearRequest = inflatedView.findViewById<View>(R.id.linearRequest) as LinearLayout


        binding!!.txtName.setText(applicationContext.resources.getString(R.string.dash_hello) + " " + strName)
        val timeInMillis = System.currentTimeMillis()
        val dateString = SimpleDateFormat("EEEE, MMMM dd").format(Date(timeInMillis))
        binding!!.date.setText(dateString)
    }

    // new pending activity status change
    private fun getconsultlistnewpending() {
        uid = PrefUtility().getFireBaseUid(this)
        mProviderUid1 = uid.toString() //mFirebaseUser.getUid();
        mPathnewpendng = "providers/$mProviderUid1/newPendingRequest"
        pendingPath = mFirebaseDatabaseReference!!.child(mPathnewpendng!!)
        pendingPath!!.addValueEventListener(dbListener)
    }
    override fun onResume() {
        super.onResume()
        val strRole: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        val strDesignation: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        imageURL =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.PROFILE_IMG_URL, "")
        0
        if (!TextUtils.isEmpty(imageURL)) {
            binding!!.profilePic.setVisibility(View.VISIBLE)
            binding!!.defaultImageView.setVisibility(View.GONE)
            ImageLoader(this, imageURL).execute()
        } else {
            binding!!.profilePic.setVisibility(View.GONE)
            binding!!.defaultImageView.setVisibility(View.VISIBLE)
            if (!TextUtils.isEmpty(strProfileName)) {
                binding!!.defaultImageView.setText(UtilityMethods().getNameText(strProfileName))

            }
        }
        if (strRole == "RD") {
            getHospitalList()
        } else {
            getBPCensusWardList()
        }

        // Getting the provider notification details via API if the user resumes back to the application
        getProviderNotificationDetails(notifiProviderId)
        if (strRole == "RD" && strDesignation == "MD/DO") {
            getconsultlist(false)
        } else if (strRole == "RD" && strDesignation != "MD/DO") {
            getconsultlist(false)
        } else {
            getconsultlist(true)
        }


        //  Child add event listener register - for IAP notification acuity listener
        acuityMessagesRef!!.removeEventListener(acuityChildEventListener)
        acuityMessagesRef!!.addChildEventListener(acuityChildEventListener)
        messagesRef!!.addChildEventListener(childEventListener!!)
        strProfileName = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        if (strRole == "RD" && strDesignation == "MD/DO") {
            pendingPath!!.addValueEventListener(dbListener)
        }

        // To get the AES key anytime by calling the version Info API
        val aesKey: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.AES_API_KEY, "")

        if (aesKey == null || TextUtils.isEmpty(aesKey)) {
            getAppConfig()
        }

    }

    /**
     * Getting provider notification details
     *
     * @param providerId
     */
    private fun getProviderNotificationDetails(providerId: Long) {


        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(
                binding!!.dashboardcontainer,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )!!
                .show()
            return
        }
        //Triggering the "getProviderNotificationDetails" API call
        homeViewModel!!.getProviderNotificationDetails(providerId)!!
            .observe(this, { notificationResponse ->

                if (notificationResponse != null) {
                    if (notificationResponse.errorMessage != null && notificationResponse.errorMessage.equals(
                            "Notification is empty.",ignoreCase = true
                        )
                    ) {
                        val notificatonId: String? = PrefUtility().getStringInPref(
                            this,
                            Constants.SharedPrefConstants.ALERT_NOTIFTY_ID,
                            ""
                        )

                        // Adding the request object to call the "AddOrUpdateProviderNotification" API
                        val addNotificationDataRequest = AddNotificationDataRequest()
                        addNotificationDataRequest.id

                        addNotificationDataRequest.setUserId(providerId)
                        addNotificationDataRequest.setMobileAcuity(false)
                        addNotificationDataRequest.setWebAcuity(true)

                        // Calling the "AddOrUpdateProviderNotification" API with request object
                        addOrUpdateProviderNotification(addNotificationDataRequest)
                    } else {
                        try {
                            val gson = Gson()

                            // Saving the Id for addorupdateProviderNotification API
                            PrefUtility().saveStringInPref(
                                this,
                                Constants.SharedPrefConstants.ALERT_NOTIFTY_ID,
                                java.lang.String.valueOf(notificationResponse.notificationSettings.getId())
                            )

                            // Saving the current status of Acuity score and enable/disabling in shared preference
                            PrefUtility().saveStringInPref(
                                this,
                                Constants.SharedPrefConstants.ALERT_ACUITY,
                                java.lang.String.valueOf(
                                    notificationResponse.notificationRequests[0].getAcuity()
                                )
                            )

                            // Saving the current backend enabled/disabled status of the acuity
                            PrefUtility().saveStringInPref(
                                this,
                                Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                                java.lang.String.valueOf(
                                    notificationResponse.notificationRequests[0].getNotificationEnabled()
                                )
                            )

                            // Saving the current mobile acuity status in shared preference
                            PrefUtility().saveStringInPref(
                                this,
                                Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                                java.lang.String.valueOf(notificationResponse.notificationSettings.getMobileAcuity())
                            )
                        } catch (e: Exception) {

                        }
                    }
                } else {
                    val errMsg: String? = ErrorMessages().getErrorMessage(
                        this,
                        this.resources.getString(R.string.api_error),
                        Constants.API.updateProvider
                    )

                    CustomSnackBar.make(
                        binding!!.dashboardcontainer,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )!!
                        .show()
                }
            })
    }
    @SuppressLint("SetTextI18n")
    private fun getHospitalList() {

        val providerId: Long? = PrefUtility().getProviderId(this)
        if (providerId != null) {
            viewModel?.getHospitalList(providerId)?.observe(this) { response ->
                dismissProgressBar()
                if (response?.getHospitalList() != null && !response.getHospitalList()!!.isEmpty()) {

                    if (response.getHospitalList()!!.size > 1) {

                        txtHospitalCount!!.setText(
                            response.getHospitalList()!!.size
                                .toString() + " " + applicationContext.resources
                                .getString(R.string.dash_hospitals)
                        )
                    } else {

                        txtHospitalCount!!.setText(
                            response.getHospitalList()!!.size
                                .toString() + " " + applicationContext.resources
                                .getString(R.string.dash_hospital)
                        )
                    }
                } else {
                    txtHospitalCount!!.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler!!.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun getBPCensusWardList() {

        wardListViewModel!!.getWardList(strHospitalID!!)!!.observe(this, { response ->
            dismissProgressBar()
            if (response != null) {

                if (response.getHospital() != null) {
                    val hospital = response.getHospital()
                    strDashboardHospitalAddress = hospital!!.getSubRegionName()
                    // Saving hospital address
                    PrefUtility().saveStringInPref(
                        this,
                        Constants.SharedPrefConstants.BP_REGION_ADDRESS,
                        strDashboardHospitalAddress
                    )
                }

                if (response.getTotalPatientCount() != null) {
                    if (response.getTotalPatientCount()!!.equals(0)) {
                        txtHospitalCount!!.visibility = View.INVISIBLE
                    } else if (response.getTotalPatientCount()!!.equals(1)) {

                        txtHospitalCount!!.text = response.getTotalPatientCount()
                            .toString() + " " + applicationContext.resources.getString(R.string.dash_patient)
                    } else {
                        txtHospitalCount!!.text = response.getTotalPatientCount()
                            .toString() + " " + applicationContext.resources.getString(R.string.dash_patients)

                    }
                }

            }
        })
    }

    override fun onStop() {
        super.onStop()
        messagesRef!!.removeEventListener(childEventListener!!)
        if (pendingPath != null) {
            pendingPath!!.removeEventListener(dbListener)
        }
    }

    private fun getconsultlist(Staus: Boolean) {
        childEventListener = object : ChildEventListener {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun onChildAdded(dataSnapshot: DataSnapshot, previous: String?) {
                /*dataSnapshot object is annotated as NonNull so no need for this condition
                if (dataSnapshot == null) {
                    return;
                }*/
                //Log.d(TAG, "ChildEvent - onChildAdded: " + dataSnapshot.getValue(ConsultProvider.class) + " : prev " + previous);
                val key = dataSnapshot.key
                //            Log.d("DATASNAPSHOT" , new Gson().toJson(dataSnapshot));
//            dataSnapshot.exists();
//                System.out.println("patient_add " + dataSnapshot);
                if (dataSnapshot.value != null && !dataSnapshot.value.toString()
                        .equals("", ignoreCase = true)
                ) {
//                if (!dataSnapshot.getValue().toString().equalsIgnoreCase("")) {
                    val consultProvider = dataSnapshot.getValue(ConsultProvider::class.java)
                    providerHashMap[key] = consultProvider
                    //                    Log.e(TAG, "onChildAdded: itemCount->" + itemCount);
//                    Log.e(TAG, "onChildAdded: providerHashMap->" + providerHashMap.size());
                    if (itemCount <= providerHashMap.size) {
                        itemCount = providerHashMap.size.toLong()
                        if (Staus) {
                            consultstausbpactivecount(providerHashMap.values)
                        } else {
                            consultstaus(providerHashMap.values)
                        }

//                        if(pendingstatus==true){
//                            consultstauspending(providerHashMap.values());
//                        }
                    }
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val key = dataSnapshot.key
                //                Log.i(TAG, "patient changed " + dataSnapshot);
                if (dataSnapshot.value != null && !dataSnapshot.value.toString()
                        .equals("", ignoreCase = true)
                ) {
//                if (!dataSnapshot.getValue().toString().equalsIgnoreCase("")) {
                    val consultProvider = dataSnapshot.getValue(ConsultProvider::class.java)
                    providerHashMap[key] = consultProvider
                    //                    Log.e(TAG, "onChildAdded: itemCount->" + itemCount);
//                    Log.e(TAG, "onChildAdded: providerHashMap->" + providerHashMap.size());
                    if (itemCount <= providerHashMap.size) {
                        itemCount = providerHashMap.size.toLong()
                        if (Staus) {
                            consultstausbpactivecount(providerHashMap.values)
                        } else {
                            consultstaus(providerHashMap.values)
                        }
                        //                        if(pendingstatus==true){
//                            consultstauspending(providerHashMap.values());
//                        }
                    }
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
//                Log.d(TAG, "ChildEvent - onChildRemoved: " + dataSnapshot);
                providerHashMap.remove(dataSnapshot.key)
                if (Staus) {
                    consultstausbpactivecount(providerHashMap.values)
                } else {
                    consultstaus(providerHashMap.values)
                }
                //                if(pendingstatus==true){
//                    consultstauspending(providerHashMap.values());
//                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
//                Log.d(TAG, "ChildEvent - onChildMoved: " + dataSnapshot);
            }

            override fun onCancelled(databaseError: DatabaseError) {
//                Log.d(TAG, "ChildEvent - onCancelled: " + databaseError);
            }
        }
        uid = PrefUtility.getFireBaseUid(this)
        mProviderUid = uid.toString() //mFirebaseUser.getUid();
        mPath = "providers/$mProviderUid/active"
        messagesRef = mFirebaseDatabaseReference!!.child(mPath!!)
        messagesRef!!.addChildEventListener(childEventListener)
        messagesRef!!.keepSynced(true)
    }

    /**
     * Get version info API call
     */
    private fun getAppConfig() {
        if (!UtilityMethods().isInternetConnected(this)!!) {
            return
        }
        homeViewModel?.getVersionInfo(Constants.OsType.ANDROID.toString())
            ?.observe(this) { versionInfoResponse ->

            if (versionInfoResponse != null && versionInfoResponse.getStatus() != null && versionInfoResponse.getStatus()!!) {
                onSuccessVersionInfoAPI(versionInfoResponse)
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    versionInfoResponse?.getErrorMessage(),
                    Constants.API.getVersionInfo
                )

            }
        }
        fun onClickListener() {
            linearMyConsult!!.setOnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            linearProviderDirectory!!.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent =
                    Intent(this, RemoteProviderDirectoryActivity::class.java)
                startActivity(intent)
            })
            binding!!.profileLayout.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            })
            binding!!.imageLayout.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            })
        }

        fun getconsultlist(Staus: Boolean) {
            childEventListener = object : ChildEventListener {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onChildAdded(dataSnapshot: DataSnapshot, previous: String?) {

                    val key = dataSnapshot.key

                    if (dataSnapshot.value != null && !dataSnapshot.value.toString()
                            .equals("", ignoreCase = true)
                    ) {

                        val consultProvider: ConsultProvider? =
                            dataSnapshot.getValue(ConsultProvider::class.java)
                        providerHashMap[key] = consultProvider

                        if (itemCount <= providerHashMap.size) {
                            itemCount = providerHashMap.size.toLong()
                            if (Staus) {
                                consultstausbpactivecount(providerHashMap.values)
                            } else {
                                consultstaus(providerHashMap.values)
                            }


                        }
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    val key = dataSnapshot.key

                    if (dataSnapshot.value != null && !dataSnapshot.value.toString()
                            .equals("", ignoreCase = true)
                    ) {

                        val consultProvider: ConsultProvider? =
                            dataSnapshot.getValue(ConsultProvider::class.java)
                        providerHashMap[key] = consultProvider

                        if (itemCount <= providerHashMap.size) {
                            itemCount = providerHashMap.size.toLong()
                            if (Staus) {
                                consultstausbpactivecount(providerHashMap.values)
                            } else {
                                consultstaus(providerHashMap.values)
                            }

                        }
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                    providerHashMap.remove(dataSnapshot.key)
                    if (Staus) {
                        consultstausbpactivecount(providerHashMap.values)
                    } else {
                        consultstaus(providerHashMap.values)
                    }

                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            uid = PrefUtility().getFireBaseUid(this)
            mProviderUid = uid.toString() //mFirebaseUser.getUid();
            mPath = "providers/$mProviderUid/active"
            messagesRef = mFirebaseDatabaseReference!!.child(mPath!!)
            messagesRef!!.addChildEventListener(childEventListener as ChildEventListener)
            messagesRef!!.keepSynced(true)
        }

        // new pending activity status change
        fun getconsultlistnewpending() {
            uid = PrefUtility().getFireBaseUid(this)
            mProviderUid1 = uid.toString() //mFirebaseUser.getUid();
            mPathnewpendng = "providers/$mProviderUid1/newPendingRequest"
            pendingPath = mFirebaseDatabaseReference!!.child(mPathnewpendng!!)
            pendingPath!!.addValueEventListener(dbListener)
        }

        @SuppressLint("SetTextI18n")
        fun consultstausbpactivecount(providers: Collection<ConsultProvider?>?) {
            strDesignation = PrefUtility().getStringInPref(
                this,
                Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                ""
            )
            if (providers == null || providers.isEmpty()) {
                providerFilteredListactive.clear()
                txt_active_count!!.text = ""
                //Handling the visibilty of the tab - Active, Pending, Complete
                //Means there is no selected filter applied so resetted to default all section
                return
            }
            providerFilteredListactive.clear()
            for (consultProvider in providers) {
                if (consultProvider == null) {
                    continue
                }
                if (consultProvider.getPatientId() == null || TextUtils.isEmpty(consultProvider.getName())) {
                    continue
                }
                if (consultProvider.getUnread() > 0 && consultProvider.getStatus() != null && consultProvider.getStatus() !== Constants.PatientStatus.Pending && consultProvider.getStatus() !== Constants.PatientStatus.Invited && consultProvider.getStatus() !== Constants.PatientStatus.Completed && consultProvider.getStatus() !== Constants.PatientStatus.Discharged) {
                }
                var condition = false

                condition = consultProvider.getStatus() === Constants.PatientStatus.Active



                if (condition) {
                    providerFilteredListactive.add(consultProvider)
                    continue
                } else {
                }

            }
            if (providerFilteredListactive.isEmpty()) {
                txt_active_count!!.text = ""
                //Handling the visibilty of the tab - Active, Pending, Complete
                //Means there is no selected filter applied so resetted to default all section
                return
            }
            txt_active_count!!.text = providerFilteredListactive.size.toString() + " " + "active"

        }


        fun consultstaus(providers: Collection<ConsultProvider?>?) {
            strDesignation = PrefUtility().getStringInPref(
                this,
                Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                ""
            )
            if (providers == null || providers.isEmpty()) {
                providerFilteredListactive.clear()
                txt_active_count!!.text = ""
                //Handling the visibilty of the tab - Active, Pending, Complete
                //Means there is no selected filter applied so resetted to default all section
                return
            }
            providerFilteredListactive.clear()
            for (consultProvider in providers) {
                if (consultProvider == null) {
                    continue
                }
                if (consultProvider.getPatientId() == null || TextUtils.isEmpty(consultProvider.getName())) {
                    continue
                }
                if (consultProvider.getUnread() > 0 && consultProvider.getStatus() != null && consultProvider.getStatus() !== Constants.PatientStatus.Pending && consultProvider.getStatus() !== Constants.PatientStatus.Invited && consultProvider.getStatus() !== Constants.PatientStatus.Completed && consultProvider.getStatus() !== Constants.PatientStatus.Discharged) {
                }
                var condition = false
                if (strDesignation.equals("md/do", ignoreCase = true)) {
                    condition =
                        consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff
                }
                if (consultProvider.getStatus() === Constants.PatientStatus.Completed || consultProvider.getStatus() === Constants.PatientStatus.Discharged || condition) {
                    continue
                }
                if (filterPatientStatus != null && consultProvider.getStatus() !== filterPatientStatus) {
                    if (filterPatientStatus.equals(Constants.PatientStatus.Active)
                        && (consultProvider.getStatus().equals(Constants.PatientStatus.Active)
                                || consultProvider.getStatus()
                            .equals(Constants.PatientStatus.Patient)
                                || consultProvider.getStatus()
                            .equals(Constants.PatientStatus.HandoffPending) //                            || consultProvider.getStatus().equals(Constants.PatientStatus.Handoff)
                                )
                    ) {
                    } else {
                        continue
                    }
                }
                providerFilteredListactive.add(consultProvider)
            }
            if (providerFilteredListactive.isEmpty()) {
                txt_active_count!!.text = ""
                //Handling the visibilty of the tab - Active, Pending, Complete
                //Means there is no selected filter applied so resetted to default all section
                return
            }
            txt_active_count!!.text = providerFilteredListactive.size.toString() + " " + "active"

        }

        //count for pending patient
        fun consultstauspending(providers: Collection<ConsultProvider>?) {
            strDesignation =
                PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                    ""
                )
            if (providers == null || providers.isEmpty()) {
                providerFilteredListpending.clear()
                //Handling the visibilty of the tab - Active, Pending, Complete
                //Means there is no selected filter applied so resetted to default all section
                return
            }
            providerFilteredListpending.clear()
            for (consultProvider in providers) {
                if (consultProvider == null) {
                    continue
                }
                if (consultProvider.getPatientId() == null || TextUtils.isEmpty(consultProvider.getName())) {
                    continue
                }
                if (consultProvider.getUnread() > 0 && consultProvider.getStatus() != null && consultProvider.getStatus() !== Constants.PatientStatus.Pending && consultProvider.getStatus() !== Constants.PatientStatus.Invited && consultProvider.getStatus() !== Constants.PatientStatus.Completed && consultProvider.getStatus() !== Constants.PatientStatus.Discharged) {
                }
                if (consultProvider.getStatus() === Constants.PatientStatus.Pending || consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff) {
                } else {
                    continue
                }
                providerFilteredListpending.add(consultProvider)
            }
            if (providerFilteredListpending.isEmpty()) {
                //Handling the visibilty of the tab - Active, Pending, Complete
                //Means there is no selected filter applied so resetted to default all section
                return
            }

        }

        fun onDestroy() {

            // Removing listeners
            if (messagesRef != null) {
                messagesRef!!.removeEventListener(childEventListener!!)
            }
            if (acuityMessagesRef != null) {
                acuityMessagesRef!!.removeEventListener(acuityChildEventListener)
            }
            super.onDestroy()
        }

        @SuppressLint("SetTextI18n")
        fun getHospitalList() {
            val providerId: Long? = PrefUtility().getProviderId(this)
            viewModel.getHospitalList(providerId).observe(this) { response ->
                dismissProgressBar()
                if (response.getHospitalList() != null && !response.getHospitalList().isEmpty()) {

                    if (response.getHospitalList().size() > 1) {

                        txtHospitalCount!!.setText(
                            response.getHospitalList().size()
                                .toString() + " " + applicationContext.resources
                                .getString(R.string.dash_hospitals)
                        )
                    } else {
                        txtHospitalCount!!.setText(
                            response.getHospitalList().size()
                                .toString() + " " + applicationContext.resources
                                .getString(R.string.dash_hospital)
                        )
                    }
                } else {
                    txtHospitalCount!!.visibility = View.INVISIBLE
                }
            }
        }

        fun handleMultipleClick(view: View) {
            view.isEnabled = false
            mHandler!!.postDelayed({ view.isEnabled = true }, 500)
        }

        fun getBPCensusWardList() {


            strHospitalID?.let { wardListViewModel!!.getWardList(it) }!!.observe(this) { response ->
                dismissProgressBar()
                if (response != null) {

                    if (response.getHospital() != null) {
                        val hospital: Hospital = response.getHospital()!!
                        strDashboardHospitalAddress = hospital.getSubRegionName()
                        // Saving hospital address
                        PrefUtility().saveStringInPref(
                            this,
                            Constants.SharedPrefConstants.BP_REGION_ADDRESS,
                            strDashboardHospitalAddress
                        )
                    }
                    if (response.getTotalPatientCount() != null) {
                        if (response.getTotalPatientCount()!!.equals(0)) {
                            txtHospitalCount!!.visibility = View.INVISIBLE
                        } else if (response.getTotalPatientCount()!!.equals(1)) {

                            txtHospitalCount?.setText(
                                response.getTotalPatientCount()
                                    .toString() + " " + applicationContext.resources
                                    .getString(R.string.dash_patient)
                            )
                        } else {
                            txtHospitalCount?.setText(
                                response.getTotalPatientCount()
                                    .toString() + " " + applicationContext.resources
                                    .getString(R.string.dash_patients)
                            )

                        }
                    }

                }
            }
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 503) {
                if (resultCode == RESULT_OK) {
                    getBPCensusWardList()
                }
            }
        }

        class ImageLoader internal constructor(
            activity: MyDashBoardActivity,
            imageURL: String?
        ) :
            AsyncTask<Void?, Void?, Bitmap?>() {
            var activityReference: WeakReference<MyDashBoardActivity>
            var imageURL: String?

            @SuppressLint("StaticFieldLeak")
            var imageProgressBar: ProgressBar
            var imageView: ImageView
            var cameraIcon: ImageView? = null
            var defaultImgView: TextView
            override fun onPreExecute() {
                super.onPreExecute()
                imageProgressBar.visibility = View.VISIBLE
                imageView.visibility = View.GONE
                defaultImgView.visibility = View.VISIBLE
                val name: String? = activityReference.get()?.let {
                    PrefUtility().getStringInPref(
                        it,
                        Constants.SharedPrefConstants.NAME,
                        ""
                    )
                }
                defaultImgView.setText(name?.let { UtilityMethods().getNameText(it) })
            }

            override fun doInBackground(vararg p0: Void?): Bitmap? {
                var bitmap: Bitmap?
                try {
                    val connection = URL(imageURL).openConnection()
                    connection.connectTimeout = 10000
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream())
                } catch (e: Exception) {
                    bitmap = null

                }
                return bitmap
            }

            override fun onPostExecute(bitmap: Bitmap?) {
                super.onPostExecute(bitmap)
                val myDashboardActivity: MyDashBoardActivity? =
                    activityReference.get()
                if (myDashboardActivity != null) {
                    imageProgressBar.visibility = View.GONE
                    if (bitmap != null) {
                        imageView.visibility = View.VISIBLE
                        defaultImgView.visibility = View.GONE
                        imageView.setImageBitmap(bitmap)
                    } else {
                        imageView.visibility = View.GONE
                        defaultImgView.visibility = View.VISIBLE
                        val name: String? = PrefUtility().getStringInPref(
                            myDashboardActivity,
                            Constants.SharedPrefConstants.NAME,
                            ""
                        )
                        defaultImgView.setText(name?.let { UtilityMethods().getNameText(it) })
                    }
                }
            }

            override fun onCancelled() {
                super.onCancelled()
            }

            init {
                activityReference = WeakReference<MyDashBoardActivity>(activity)
                this.imageURL = imageURL
                imageView = activityReference.get().binding.profilePic
                imageProgressBar = activityReference.get()?.binding?.idProfileImagePb!!
                defaultImgView = activityReference.get()?.binding?.defaultImageView!!
            }


        }

        /**
         * Setting up the custom notification listeners
         */
        fun setCustomNotificationListeners() {


            // Initialize Firebase Auth
            uid = PrefUtility().getFireBaseUid(this)
            mFirebaseAuth = FirebaseAuth.getInstance()
            mFirebaseUser = mFirebaseAuth!!.currentUser
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
            if (mFirebaseUser == null || uid == "") {
                // Not signed in, launch the Sign In activity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }

            // Provider Id
            mProviderUid = uid

            // Adding the provider acuity path
            acuityFirebasePath = "providers/$mProviderUid/active"


            // Adding the child path to firebase database reference
            acuityMessagesRef = mFirebaseDatabaseReference!!.child(acuityFirebasePath!!)
            acuityMessagesRef!!.keepSynced(true)
            acuityQuery = acuityMessagesRef!!.orderByChild("time")


        }

        /**
         * Getting provider notification details
         *
         * @param providerId
         */
        fun getProviderNotificationDetails(providerId: Long) {


            if (!UtilityMethods().isInternetConnected(this)!!) {

                CustomSnackBar.make(
                    binding?.dashboardcontainer,
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
                return
            }
            //Triggering the "getProviderNotificationDetails" API call
            homeViewModel?.getProviderNotificationDetails(providerId)
                ?.observe(this) { notificationResponse ->

                    if (notificationResponse != null) {
                        if (notificationResponse.getErrorMessage() != null && notificationResponse.getErrorMessage()
                                .equalsIgnoreCase("Notification is empty.")
                        ) {
                            val notificatonId: String? = PrefUtility().getStringInPref(
                                this,
                                Constants.SharedPrefConstants.ALERT_NOTIFTY_ID,
                                ""
                            )

                            // Adding the request object to call the "AddOrUpdateProviderNotification" API
                            val addNotificationDataRequest = AddNotificationDataRequest()
                            addNotificationDataRequest.setId(notificatonId)

                            addNotificationDataRequest.setUserId(providerId)
                            addNotificationDataRequest.setMobileAcuity(false)
                            addNotificationDataRequest.setWebAcuity(true)

                            // Calling the "AddOrUpdateProviderNotification" API with request object
                            addOrUpdateProviderNotification(addNotificationDataRequest)
                        } else {
                            try {
                                val gson = Gson()
                                // Saving the Id for addorupdateProviderNotification API
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.ALERT_NOTIFTY_ID,
                                    java.lang.String.valueOf(
                                        notificationResponse.getNotificationSettings().getId()
                                    )
                                )

                                // Saving the current status of Acuity score and enable/disabling in shared preference
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.ALERT_ACUITY,
                                    java.lang.String.valueOf(
                                        notificationResponse.getNotificationRequests().get(0)
                                            .getAcuity()
                                    )
                                )

                                // Saving the current backend enabled/disabled status of the acuity
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                                    java.lang.String.valueOf(
                                        notificationResponse.getNotificationRequests().get(0)
                                            .getNotificationEnabled()
                                    )
                                )

                                // Saving the current mobile acuity status in shared preference
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                                    java.lang.String.valueOf(
                                        notificationResponse.getNotificationSettings()
                                            .getMobileAcuity()
                                    )
                                )
                            } catch (e: Exception) {

                            }
                        }
                    } else {
                        val errMsg: String? = ErrorMessages().getErrorMessage(
                            this,
                            this.resources.getString(R.string.api_error),
                            Constants.API.updateProvider
                        )

                        CustomSnackBar.make(
                            binding?.dashboardcontainer,
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

        /**
         * Add / Update provider notification Data
         *
         * @param addNotificationDataRequest
         */
        fun addOrUpdateProviderNotification(addNotificationDataRequest: AddNotificationDataRequest) {
            if (!UtilityMethods().isInternetConnected(this)!!) {

                CustomSnackBar.make(
                    binding!!.dashboardcontainer,
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
                return
            }
            val gson = Gson()

            //Triggering the "addOrUpdateProviderNotification" API call
            homeViewModel?.addOrUpdateProviderNotification(addNotificationDataRequest)
                ?.observe(this) { commonResponse ->
                    if (commonResponse != null && commonResponse.status != null && commonResponse.status) {


                        // Get the provider notification details
                        getProviderNotificationDetails(notifiProviderId)
                    } else {
                        val errMsg: String = ErrorMessages().getErrorMessage(
                            this,
                            commonResponse.errorMessage,
                            Constants.API.updateProvider
                        )!!

                        CustomSnackBar.make(
                            binding?.dashboardcontainer,
                            this,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0
                        )?.show()
                        dismissProgressBar()
                        finish()
                    }
                }
        }


        /**
         * Acuity child event listener
         */
        var acuityChildEventListener: ChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val key = snapshot.key


                // if the key starts with "-", thats the valid key of the snapshot and will not be adding other snapshots as it may not contain all the required data
                if (!key!!.startsWith("-")) {
                    val consultProviderAll: ConsultProvider? =
                        snapshot.getValue(ConsultProvider::class.java)
                    acuityHashMap[java.lang.Long.valueOf(key)] = consultProviderAll
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                val key = snapshot.key

                acuityModelArrayList[java.lang.Long.valueOf(key)] =
                    snapshot.getValue(ConsultProvider::class.java)
                // If the datasnapshot user is active(Accepted patient of the provider) or HandoffPending, the comparison of acuity score is done with pre-saved acutiy hash map list data
                for ((_, value) in acuityModelArrayList) {
                    if (value?.getStatus() == null) {
                        continue
                    }
                    if (value.getStatus().toString().equals("Active", ignoreCase = true) ||
                        value.getStatus().toString().equals("HandoffPending", ignoreCase = true)
                    ) {


                        // Looping the acuityHashMap for to check the current snapshot data(acuityModelArrayList) and its changes in the acuity score
                        for ((key1, value1) in acuityHashMap) {


                            // If the patient Id matches with the current acuity acuityModelArrayList, then the score comparison will be executed
                            if (java.lang.String.valueOf(value1.getPatientId()).equals(
                                    java.lang.String.valueOf(
                                        value.getPatientId()
                                    ), ignoreCase = true
                                )
                            ) {


                                // If the score matches - no changes need to be done
                                if (!value1?.getScore().toString()
                                        .equals(value.getScore().toString(), ignoreCase = true)
                                ) {

                                    val consultProvider: ConsultProvider? =
                                        snapshot.getValue(ConsultProvider::class.java)

                                    // Getting the role and designation details
                                    val role: String? = PrefUtility().getRole(this)
                                    val strRole: String? = PrefUtility().getStringInPref(
                                        this,
                                        Constants.SharedPrefConstants.ROLE,
                                        ""
                                    )
                                    val strDesignation: String = PrefUtility().getStringInPref(
                                        this,
                                        Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                                        ""
                                    )!!

                                    // If the role is RD and designation MD/DO
//                            if (strRole.equals("RD") && strDesignation.equals("MD/DO")) {
                                    if (role.equals(
                                            Constants.ProviderRole.RD.toString(),
                                            ignoreCase = true
                                        )
                                    ) {
                                        //Send acuity data if the user is Remote side provider with the current snapshot key to update the existing acuity hashmapvalue
                                        if (consultProvider != null) {
                                            getAcuityData(consultProvider, key1)
                                        }
                                    }
                                }
                                break
                            }
                        }
                    }
                }


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }


        /**
         * Getting the acuity details for In-App notifications
         *
         * @param acuityValues - Snapshot values
         * @param key          - Snapshot values key
         */
        fun getAcuityData(acuityValues: ConsultProvider, key: Long) {

            // Updating the current snapshot values in the specific key place and making sure that the current data is available for comparison
            acuityHashMap[key] = acuityValues


            /*
     *  For MI issue see
     *  https://www.androidcentral.com/how-fix-miui-push-notifications#:~:text=Open%20Settings%20from%20the%20home,to%20the%20Apps%20sub%2Dmenu.&text=Choose%20the%20apps%20that%20aren,always%20start%20in%20the%20background.
     * **/

            // Score based variables
            var score = ""
            var sharedPrefAcuityScore: String? = ""


            // Assingning values to score variable
            score = java.lang.String.valueOf(acuityValues.getScore())

            // Notification triggers only if the user enabled the notifications in the custom notifications page
            val checkAcuity: Boolean
            val checkMobileAcuity: Boolean

            // Getting the backend notification enabled values from the shared preference
            checkMobileAcuity = if (PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                    ""
                ) == null || PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                    ""
                )?.isEmpty() == true
            ) {
                true
            } else {
                java.lang.Boolean.parseBoolean(
                    PrefUtility().getStringInPref(
                        this,
                        Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                        ""
                    )
                )
            }
            // Getting value from shared preference for Acuity notification by the user handling
            checkAcuity = if (PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                    ""
                ) == null || PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                    ""
                )?.isEmpty() == true
            ) {
                true
            } else {
                java.lang.Boolean.parseBoolean(
                    PrefUtility().getStringInPref(
                        this,
                        Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                        ""
                    )
                )
            }


            // Backend acuity score
            if (PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.ALERT_ACUITY,
                    ""
                ) == null || PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.ALERT_ACUITY,
                    ""
                )?.isEmpty() == true
            ) {
                sharedPrefAcuityScore = java.lang.String.valueOf(Constants.AcuityLevel.High)
            } else {
                sharedPrefAcuityScore = java.lang.String.valueOf(
                    PrefUtility().getStringInPref(
                        this,
                        Constants.SharedPrefConstants.ALERT_ACUITY,
                        ""
                    )
                )
            }
            //In-App notification for acuity change - Only of  Acuity notification is enabled by the user and by the backend

            // checkMobileAcuity - enabled/disabled by the backend from API which is saved in the shared preference
            // checkAcuity - acuity managed by the user which is saved in the shared preference
            // score - Current snapshot acuity score of the patient
            //sharedPrefAcuityScore - Backend enable score which is saved in the shared preference

            // if checkMobileAcuity and checkAcuity is "true" - IAP notification is triggered
            // if any on of this values is "false" - No notification will be triggered
            if (checkMobileAcuity && checkAcuity && score.equals(
                    sharedPrefAcuityScore,
                    ignoreCase = true
                )
            ) {
                var scoreColor = ""
                // Based on the acuity score - The colors are defined
                // If score is - "High", color is - "RED"
                // If score is - "Medium", color is - "AMBER"
                // If score is - "Low", color is - "GREEN"
                if (score.equals(
                        java.lang.String.valueOf(Constants.AcuityLevel.High),
                        ignoreCase = true
                    )
                ) {
                    scoreColor = java.lang.String.valueOf(Constants.AcuityLevelColor.RED)
                } else if (score.equals(
                        java.lang.String.valueOf(Constants.AcuityLevel.Medium),
                        ignoreCase = true
                    )
                ) {
                    scoreColor = java.lang.String.valueOf(Constants.AcuityLevelColor.AMBER)
                } else if (score.equals(
                        java.lang.String.valueOf(Constants.AcuityLevel.Low),
                        ignoreCase = true
                    )
                ) {
                    scoreColor = java.lang.String.valueOf(Constants.AcuityLevelColor.GREEN)
                }


                // Triggering the notification for acuity based in the score color and its acuity values
                triggerInAppAlertForAcuityChange(
                    getString(R.string.acuity_message) + " " + scoreColor,
                    acuityValues
                )
            }
        }


        /**
         * In-App notification for acuity change only if,
         * The user enabled the acuity alerts in alert/notification settings
         * Else the user wont get notified even if the acuity level is modified
         *
         * @param message
         */
        fun triggerInAppAlertForAcuityChange(message: String, provider: ConsultProvider) {


            //Sending pending intent via notification helper class
            var strCompletedByName = ""
            if (provider.getCompleted_by() != null && !TextUtils.isEmpty(provider.getCompleted_by())) {
                strCompletedByName = provider.getCompleted_by()
            }
            // Intent to ActivityConsultChartRemote/ActivityConsultChart based on the patient status
            val intentConsultChart: Intent
            if (provider.getStatus() === Constants.PatientStatus.Discharged ||
                provider.getStatus() === Constants.PatientStatus.Exit
            ) {
                intentConsultChart =
                    Intent(this, ActivityConsultChartRemote::class.java)
            } else {
                intentConsultChart = Intent(this, ActivityConsultChart::class.java)
            }
            intentConsultChart.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intentConsultChart.putExtra("uid", provider.getPatientId())
            intentConsultChart.putExtra("providerNameType", strCompletedByName)
            intentConsultChart.putExtra(
                "completedTime",
                java.lang.String.valueOf(provider.getTime())
            )
            intentConsultChart.putExtra("status", provider.getStatus())
            intentConsultChart.putExtra("path", "consults/" + provider.getPatientId())
            intentConsultChart.putExtra("consultProviderId", "" + provider.getId())
            intentConsultChart.putExtra("consultProviderPatientId", "" + provider.getPatientId())
            intentConsultChart.putExtra("consultProviderText", provider.getText())
            intentConsultChart.putExtra("consultProviderName", provider.getName())
            intentConsultChart.putExtra("unreadMessageCount", provider.getUnread())
            intentConsultChart.putExtra("dob", provider.getDob())
            intentConsultChart.putExtra("gender", provider.getGender())
            intentConsultChart.putExtra("note", provider.getNote())
            intentConsultChart.putExtra("phone", provider.getPhone())
            intentConsultChart.putExtra("patientId", provider.getPatientsId())
            intentConsultChart.putExtra(
                Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                provider.getUrgent()
            )
            if (provider.getStatus() != null) {
                intentConsultChart.putExtra("status", provider.getStatus().toString())
                if (provider.getStatus() === Constants.PatientStatus.Invited ||
                    provider.getStatus() === Constants.PatientStatus.Handoff
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                    clearNotifications(provider.getPatientId().intValue())
                } else if (provider.getStatus() === Constants.PatientStatus.Discharged ||
                    provider.getStatus() === Constants.PatientStatus.Exit
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
                }
            }

            // This will create the notification template with the pending intent data for redirecting the user
            // multiple pending intent
            intentConsultChart.action = java.lang.Long.toString(System.currentTimeMillis())
            val pendingIntent = PendingIntent.getActivity(
                this, 1,
                intentConsultChart, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationHelper = NotificationHelper(applicationContext as Activity?)

//        Random random = new Random();
            val multipleNotification = random.nextInt(9999 - 1000) + 1000


            notificationHelper.sendNotification(
                pendingIntent,
                getString(R.string.acuity_notification_title),
                message,
                multipleNotification
            )
        }

        /**
         *
         * Clearing then notification with dedicated notification ID
         *
         * @param notificationId
         */
        fun clearNotifications(notificationId: Int) {
            NotificationHelper(this).clearNotification(notificationId)
        }


    }
}