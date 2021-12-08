package com.example.kotlinomnicure.activity

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.LocationListAdapter
import com.example.kotlinomnicure.adapter.PatientListAdapter
import com.example.kotlinomnicure.customview.CircularImageView
import com.example.kotlinomnicure.customview.CustomDialog
import com.example.kotlinomnicure.databinding.ActivityHomeBinding
import com.example.kotlinomnicure.databinding.ItemConsultListBinding
import com.example.kotlinomnicure.helper.*
import com.example.kotlinomnicure.interfaces.OnListItemClickListener
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.videocall.openvcall.ui.CallActivity
import com.example.kotlinomnicure.viewholder.ConsultListViewHolder
import com.example.kotlinomnicure.viewmodel.ChatActivityViewModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import omnicurekotlin.example.com.providerEndpoints.HandOffAcceptRequest
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class HomeActivity(options: FirebaseRecyclerOptions<T>) : DrawerActivity(), NavigationView.OnNavigationItemSelectedListener, OnListItemClickListener {

    override val TAG = HomeActivity::class.java.simpleName
    var expandedPosition = -1
    private var containerParent: FrameLayout? = null
    private var listViewPos = -1
    private val providerHashMap = LinkedHashMap<String, ConsultProvider>()
    private val providerFilteredList: MutableList<ConsultProvider> = ArrayList()
    private var resetAcuityDialog: CustomDialog? = null
    private var strDesignation: String? = null
    private val unreadCountMap = HashMap<String, Any>()
    private var filterByDialog: Dialog? = null
    private var strGender: String? = null
    private var strPhone: String? = null
    private var strWard: String? = null

    // Firebase instance variables
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private var mFirebaseAdapter: FirebaseRecyclerAdapter<ConsultProvider, ConsultListViewHolder>? = null
    private var mProviderUid: String? = null
    private var mPath: String? = null
    private var messagesRef: DatabaseReference? = null
    private var query: Query? = null
    private var homeBinding: ActivityHomeBinding? = null
    private var searchQueryStr: String? = null
    private var filterPatientStatus: Constants.PatientStatus? = null
    private var acuityLevel: Constants.AcuityLevel = Constants.AcuityLevel.NA
    private var acuityLevelPending: Constants.AcuityLevel = Constants.AcuityLevel.NA
    private var patientStatusType: Constants.PatientStatus = Constants.PatientStatus.NA
    private var eConsultStatus: Constants.PatientStatus = Constants.PatientStatus.NA
    private var eConsultTime: Constants.ConsultTime = Constants.ConsultTime.NA
    private var urgencyLevelType: Constants.UrgencyLevel = Constants.UrgencyLevel.NA

    //private Constants.UrgencyLevel urgencyLevelTypePending = Constants.UrgencyLevel.NA;
    private var role: String? = null
    private val customDialog: CustomDialog? = null
    private val parser: SnapshotParser<ConsultProvider>? = null
    private var itemCount: Long = -1
    private var strFeedbackForm = ""
    private var currentUser: Provider? = null
    private var provider: ConsultProvider? = null
    private var locationListAdapter: LocationListAdapter? = null
    private val ALL_HOSPITAL_ID = UUID.randomUUID().hashCode().toLong()
    private var selectedHospitalId = ALL_HOSPITAL_ID
    private var selectedTab: Int = TAB.Active.ordinal
    private var mAdapter: PatientListAdapter? = null
    private var uid: String? = null
    var intentCensus = ""

    //  Event listener data snap shot for Active, Pending, Completed patients data updation based on Roles
    var valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val key = dataSnapshot.key

            if (dataSnapshot.value != null && !dataSnapshot.value.toString()
                    .equals("", ignoreCase = true)) {
                val consultProvider = dataSnapshot.getValue(ConsultProvider::class.java)
                providerHashMap[key!!] = consultProvider!!

                if (itemCount <= providerHashMap.size) {
                    itemCount = providerHashMap.size.toLong()
                    filterList(providerHashMap.values)
                    mAdapter!!.notifyDataSetChanged()
                }
            }
            if (selectedTab == TAB.Active.ordinal) {
                //Filter active screen by score
                filterByAcuityActive(acuityLevel)
            } else if (selectedTab == TAB.Pending.ordinal) {
                //Filtering by pending acuity
                filterByAcuityPending(acuityLevelPending)
            } else if (selectedTab == TAB.Patients.ordinal) {
                // Filtering by status type
                filterByStatusType(patientStatusType)
            }
        itemCount = dataSnapshot.childrenCount
            if (itemCount == 0L) {
                //Handling the visibilty of the tab - Active, Pending, Complete
                //Means there is no selected filter applied so resetted to default all section
                handleListVisibility(false)
            }

            query!!.removeEventListener(this)


        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    // Child event listener for new data added/changed/removed status
    var childEventListener: ChildEventListener = object : ChildEventListener {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        override fun onChildAdded(dataSnapshot: DataSnapshot, previous: String?) {

            val key = dataSnapshot.key

            if (dataSnapshot.value != null && !dataSnapshot.value.toString()
                    .equals("", ignoreCase = true)) {

                val consultProvider = dataSnapshot.getValue(ConsultProvider::class.java)
                providerHashMap[key!!] = consultProvider!!

                if (itemCount <= providerHashMap.size) {
                    itemCount = providerHashMap.size.toLong()
                    filterList(providerHashMap.values)
                    mAdapter!!.notifyDataSetChanged()
                }
            }



            // Filtering based on the acuity
            if (selectedTab == TAB.Active.ordinal) {
                //Filter active screen by score
                filterByAcuityActive(acuityLevel)
            } else if (selectedTab == TAB.Pending.ordinal) {
                //Filtering by pending acuity
                getnewpendingchange()
                filterByAcuityPending(acuityLevelPending)
            } else if (selectedTab == TAB.Patients.ordinal) {
                // Filtering by status type
                filterByStatusType(patientStatusType)
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            filterItem(dataSnapshot)

            if (selectedTab == TAB.Active.ordinal) {
                //Filter active screen by score
                filterByAcuityActive(acuityLevel)
            } else if (selectedTab == TAB.Pending.ordinal) {
                // Filter pending screen by score
                filterByAcuityPending(acuityLevelPending)
            } else if (selectedTab == TAB.Patients.ordinal) {
                // Filtering by status type
                filterByStatusType(patientStatusType)
            }


        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            providerHashMap.remove(dataSnapshot.key)
            filterList(providerHashMap.values)
            if (selectedTab == TAB.Active.ordinal) {
                //Filter active screen by score
                filterByAcuityActive(acuityLevel)
            } else if (selectedTab == TAB.Pending.ordinal) {
                //Filtering by pending acuity
                filterByAcuityPending(acuityLevelPending)
            } else if (selectedTab == TAB.Patients.ordinal) {
                // Filtering by status type
                filterByStatusType(patientStatusType)
            }
            mAdapter!!.notifyDataSetChanged()
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    private fun getnewpendingchange() {


        // new pending activity status change
        uid = PrefUtility().getFireBaseUid(this@HomeActivity)
        val mProviderUid1 = uid.toString() //mFirebaseUser.getUid();


        val mPathnewpendng = "providers/$mProviderUid1/newPendingRequest"
        mFirebaseDatabaseReference!!.child("providers").child(mProviderUid1)
            .child("newPendingRequest").setValue(false)


    }

    /**
     * Tab change/ click listener handling
     */
    var tabChangeListener =
        View.OnClickListener { view ->
            homeBinding!!.acuity.text = getString(R.string.acuity)
            when (view.id) {
                R.id.activeBtnLayout -> {
                    if (selectedTab == TAB.Active.ordinal) {
                        return@OnClickListener
                    }
                    homeBinding!!.activetab.visibility = View.VISIBLE
                    homeBinding!!.pendingtab.visibility = View.GONE
                    homeBinding!!.complettab.visibility = View.GONE


                    // Acuity filter click listener
                    homeBinding!!.acuityFilterView.setOnClickListener { //Handling multiple click event with view
                        handleMultipleClick(homeBinding!!.acuityFilterView)
                        //Showing the dialog with Filter by acuity
                        FilterByAcuityDialog(this@HomeActivity)
                    }
                    homeBinding!!.txtClearactive.setOnClickListener { v ->
                        handleMultipleClick(v)
                        // isActivePatient = false;
                        // filterByAcuityActive(Constants.AcuityLevel.NA);
                        filterdeselectactive()
                    }

                    // Filter view click listener
                    homeBinding!!.filterViewactive.setOnClickListener {
                        //Handling multiple click event with view
                        handleMultipleClick(homeBinding!!.filterViewactive)
                        if (role != null && role.equals(
                                Constants.ProviderRole.BD.toString(),
                                ignoreCase = true
                            )
                        ) {
                            // If the role is BP - Display Filter dialog based on -  Urgency, Status
                            FilterByDialog(this@HomeActivity)
                        } else {
                            // If the role is RP- Display Filter dialog based on -  Urgency, Status
                            FilterByRpDialog(this@HomeActivity)
                        }
                    }
                    // Active buttn handling
                    homeBinding!!.idBtnActive.setTextColor(resources.getColor(R.color.colorPrimary))
                    homeBinding!!.idBtnActive.background =
                        resources.getDrawable(R.drawable.tab_selected_rounded)
                    homeBinding!!.idBtnComplete.setTextColor(resources.getColor(R.color.white))
                    homeBinding!!.idBtnComplete.background =
                        resources.getDrawable(R.drawable.transparent_bg)
                    homeBinding!!.idBtnPending.setTextColor(resources.getColor(R.color.white))
                    homeBinding!!.idBtnPending.background =
                        resources.getDrawable(R.drawable.transparent_bg)
                    //                    homeBinding.idBtnActive.setTypeface(Typeface.DEFAULT_BOLD);
                    if (role != null && role.equals(
                            Constants.ProviderRole.BD.toString(),
                            ignoreCase = true)) {
                        homeBinding!!.fab.visibility = View.VISIBLE
                    } else {
                        homeBinding!!.fab.visibility = View.GONE
                    }
                    homeBinding!!.filterViewactive.visibility = View.VISIBLE
                    homeBinding!!.filterViewpending.visibility = View.GONE
                    selectedTab = TAB.Active.ordinal
                    // Filtering list with provider data
                    filterList(providerHashMap.values)
                    mAdapter!!.notifyDataSetChanged()

                    // this is to filter by score
                    filterByAcuityActive(acuityLevel)
                }
                R.id.pendingBtnLayout -> {
                    if (selectedTab === TAB.Pending.ordinal) {
                        return@OnClickListener
                    }
                    getnewpendingchange()

                    homeBinding!!.activetab.visibility = View.GONE
                    homeBinding!!.pendingtab.visibility = View.VISIBLE
                    homeBinding!!.complettab.visibility = View.GONE

                    // Acuity filter click listener
                    homeBinding!!.acuityFilterView.setOnClickListener { //Handling multiple click event with view
                        handleMultipleClick(homeBinding!!.acuityFilterView)
                        //Showing the dialog with Filter by acuity pending
                        FilterByAcuityPendingDialog(this@HomeActivity)
                    }
                    homeBinding!!.txtClearpending.setOnClickListener { v ->
                        handleMultipleClick(v)
                        // isActivePatient = false;
                        filterByAcuityPending(Constants.AcuityLevel.NA)
                        eConsultTime = Constants.ConsultTime.NA
                        //Filtering by pending acuity
                        filterByAcuityPending(acuityLevelPending)
                        filterdeselectpending()
                    }

                    // Filter view click listener
                    homeBinding!!.filterViewpending.setOnClickListener {
                        //Handling multiple click event with view
                        handleMultipleClick(homeBinding!!.filterViewpending)
                        // Filtering by time dialog
                        FilterByTimeDialog(this@HomeActivity)
                    }
                    homeBinding!!.idBtnPending.setTextColor(resources.getColor(R.color.colorPrimary))
                    homeBinding!!.idBtnPending.background =
                        resources.getDrawable(R.drawable.tab_selected_rounded)
                    homeBinding!!.idBtnActive.setTextColor(resources.getColor(R.color.white))
                    homeBinding!!.idBtnActive.background =
                        resources.getDrawable(R.drawable.transparent_bg)
                    homeBinding!!.idBtnComplete.setTextColor(resources.getColor(R.color.white))
                    homeBinding!!.idBtnComplete.background =
                        resources.getDrawable(R.drawable.transparent_bg)

                    homeBinding!!.fab.visibility = View.GONE
                    homeBinding!!.filterViewpending.visibility = View.VISIBLE
                    homeBinding!!.filterViewactive.visibility = View.GONE
                    selectedTab = TAB.Pending.ordinal
                    filterList(providerHashMap.values)
                    mAdapter!!.notifyDataSetChanged()
                    //Filtering by pending acuity
                    filterByAcuityPending(acuityLevelPending)
                }
                R.id.completedBtnLayout -> {
                    homeBinding!!.acuity.text = getString(R.string.type)
                    if (selectedTab.equals(TAB.Patients)) {
                        return@OnClickListener
                    }

                    homeBinding!!.activetab.visibility = View.GONE
                    homeBinding!!.pendingtab.visibility = View.GONE
                    homeBinding!!.complettab.visibility = View.VISIBLE


                    homeBinding!!.acuityFilterView.setOnClickListener {
                        handleMultipleClick(homeBinding!!.acuityFilterView)
                        FilterByType(this@HomeActivity)
                    }
                    homeBinding!!.txtClearcomplet.setOnClickListener { v ->
                        handleMultipleClick(v)

                        filterdeselectcomplet()

                    }
                    homeBinding!!.idBtnComplete.setTextColor(resources.getColor(R.color.colorPrimary))
                    homeBinding!!.idBtnComplete.background =
                        resources.getDrawable(R.drawable.tab_selected_rounded)
                    homeBinding!!.idBtnActive.setTextColor(resources.getColor(R.color.white))
                    homeBinding!!.idBtnActive.background =
                        resources.getDrawable(R.drawable.transparent_bg)
                    homeBinding!!.idBtnPending.setTextColor(resources.getColor(R.color.white))
                    homeBinding!!.idBtnPending.background =
                        resources.getDrawable(R.drawable.transparent_bg)
                    homeBinding!!.fab.visibility = View.GONE
                    homeBinding!!.filterViewactive.visibility = View.GONE
                    homeBinding!!.filterViewpending.visibility = View.GONE
                    selectedTab = TAB.Patients.ordinal
                    // used to restore data
                    filterList(providerHashMap.values)
                    mAdapter!!.notifyDataSetChanged()

                    // this is to filter by type
                    filterByStatusType(patientStatusType)
                }
            }
        }


    /**
     * Display Consult provider dialog
     *
     * @param context
     * @param provider
     */
    fun consultDetailsDialog(context: Context?, provider: ConsultProvider) {


        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.econsult_detail_dialog)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)

        //change animation
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialogforconsultDetails
        val txtPatientName = dialog.findViewById<View>(R.id.txtPatientName) as TextView
        val txtAge = dialog.findViewById<View>(R.id.txtAge) as TextView
        val txtLocation = dialog.findViewById<View>(R.id.txtLocation) as TextView
        val txtMRNNumber = dialog.findViewById<View>(R.id.txtMRNNumber) as TextView
        val txtComplaint = dialog.findViewById<View>(R.id.txtComplaint) as TextView
        val txtTimeZone = dialog.findViewById<View>(R.id.txtTimeZone) as TextView
        val txtMessage = dialog.findViewById<View>(R.id.txtMessage) as TextView
        val txteConsult = dialog.findViewById<View>(R.id.txteConsult) as TextView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        val imgMessageAlert = dialog.findViewById<View>(R.id.imgMessageAlert) as ImageView
        val llMessage = dialog.findViewById<View>(R.id.llMessage) as LinearLayout
        if (provider.getUnread() > 0) {
            imgMessageAlert.visibility = View.VISIBLE
        } else {
            imgMessageAlert.visibility = View.GONE
        }
        val mProviderUid: String? = PrefUtility().getFireBaseUid(this)
        val unreadMessage = mProviderUid?.let {
            mFirebaseDatabaseReference!!.child("providers")
                .child(it).child("active").child(java.lang.String.valueOf(provider.getId()))
        }
        val unreadMessageListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val consultProvider = snapshot.getValue(ConsultProvider::class.java)
                if (consultProvider != null) {
                    if (consultProvider.getUnread() > 0) {
                        imgMessageAlert.visibility = View.VISIBLE
                    } else {
                        imgMessageAlert.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        //        unreadMessage.addValueEventListener(unreadMessageListener);
        mFirebaseDatabaseReference!!.addValueEventListener(unreadMessageListener)
        dialog.setOnDismissListener { //                unreadMessage.removeEventListener(unreadMessageListener);
            mFirebaseDatabaseReference!!.removeEventListener(unreadMessageListener)
        }
        val stub = dialog.findViewById<View>(R.id.layout_stub_view) as ViewStub
        val statusStub = dialog.findViewById<View>(R.id.status_stub) as RelativeLayout
        UtilityMethods().displayVitals(this, stub, provider)
        UtilityMethods().displayPatientStatusComponent(
            this, statusStub, provider.getUrgent(),
            provider.getStatus() === Constants.PatientStatus.Pending, provider.getScore())
        txteConsult.setOnClickListener {
            val intentConsultChart = Intent(this@HomeActivity, ActivityConsultChart::class.java)
            intentConsultChart.putExtra("uid", provider.getPatientId())
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
                    clearNotifications(provider.getPatientId())
                } else if (provider.getStatus() === Constants.PatientStatus.Completed ||
                    provider.getStatus() === Constants.PatientStatus.Discharged
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
                }
            }
            startActivity(intentConsultChart)
            Handler().postDelayed({ dialog.dismiss() }, 1000)
        }
        llMessage.setOnClickListener {
            startChat(provider)
            Handler().postDelayed({ dialog.dismiss() }, 1000)
        }
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        calendar.timeInMillis = provider.getDob()!!
        val agee = year - calendar[Calendar.YEAR]
        val age = agee.toString()
        val dot = " <b>\u00b7</b> "
        val timeInMillis = provider.getDob()
        val dateString = SimpleDateFormat("MM-dd-yyyy").format(
            Date(
                timeInMillis!!
            )
        )
        val strDob = dot + dateString
        if (!TextUtils.isEmpty(provider.getGender())) {
            strGender = dot + provider.getGender()
        }
        if (provider.getGender()?.equals("Male") == true) {
            strGender = dot + "M"
        } else if (provider.getGender()?.equals("Female") == true) {
            strGender = dot + "F"
        }
        strPhone = if (!TextUtils.isEmpty(provider.getPhone()) &&
            !provider.getPhone()?.equals("null")!!) {
            dot + provider.getPhone()
        } else {
            ""
        }
        strWard = if (!TextUtils.isEmpty(provider.getWardName()) &&
            !provider.getWardName()?.equals("null")!!) {
            dot + provider.getWardName()
        } else {
            ""
        }
        val strComplaint = provider.getNote()
        var stringComplaint = strComplaint
        if (strComplaint!!.contains(":")) {
            stringComplaint = strComplaint.substring(strComplaint.indexOf(":") + 1)
        }
        txtPatientName.text = provider.getName()
        txtComplaint.movementMethod = ScrollingMovementMethod()
        txtComplaint.setOnTouchListener { v: View?, event: MotionEvent? ->
            txtComplaint.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        txtComplaint.isVerticalScrollBarEnabled = false
        txtComplaint.post {
            val lineCount = txtComplaint.lineCount
            if (lineCount > 3) {
                txtComplaint.isScrollbarFadingEnabled = false
                txtComplaint.isVerticalScrollBarEnabled = true
            }
            // Use lineCount here
        }
        //        txtComplaint.requestFocus();
        txtLocation.text = Html.fromHtml(provider.getHospital().toString() + strWard)
        txtComplaint.text = stringComplaint!!.trim { it <= ' ' }
        txtAge.text = Html.fromHtml(age + strGender + strDob + strPhone)
        if (!TextUtils.isEmpty(provider.getRecordNumber())) {
            txtMRNNumber.text = Html.fromHtml("MRN&nbsp;" + provider.getRecordNumber())
        } else {
            txtMRNNumber.text = "MRN "
        }
        if (provider.getSyncTime() != null) {
            txtTimeZone.setText(Utils().timestampToDate(provider.getSyncTime()!!))
            txtTimeZone.visibility = View.VISIBLE
        } else {
            txtTimeZone.text = " - "
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    /**
     * Filter active screen by score
     */
    fun filterByAcuityActive(level: Constants.AcuityLevel) {
        if (providerFilteredList == null) {
            return
        }
        if (level.equals(Constants.AcuityLevel.NA)) {
            //Filter for Urgency
            var lowScoreData: MutableList<ConsultProvider?> = ArrayList()
            for (i in providerFilteredList.indices) {
                val consultProvider = providerFilteredList[i]
                when (urgencyLevelType) {
                    Constants.UrgencyLevel. NA -> lowScoreData.add(consultProvider)
                    Constants.UrgencyLevel. Urgent -> if (consultProvider.getUrgent()!!) {
                        lowScoreData.add(consultProvider)
                    }
                    Constants.UrgencyLevel.NotUrgent -> if (!consultProvider.getUrgent()!!) {
                        lowScoreData.add(consultProvider)
                    }
                    else -> {
                    }
                }
            }
            val eConsultStatusList: List<ConsultProvider?> = ArrayList<ConsultProvider?>(lowScoreData)
            lowScoreData.clear()
            // filter for eConsult Status
            for (i in eConsultStatusList.indices) {
                val consultProvider = eConsultStatusList[i]
                when (eConsultStatus) {
                    Constants.PatientStatus.NA -> lowScoreData.add(consultProvider)
                    Constants.PatientStatus.Active -> if (consultProvider!!.getStatus() === Constants.PatientStatus.Active) {
                        lowScoreData.add(consultProvider)
                    }
                    Constants.PatientStatus.Pending -> if (consultProvider!!.getStatus() === Constants.PatientStatus.Pending) {
                        lowScoreData.add(consultProvider)
                    }
                }
            }

            //Filter for location
            val acuityUrgencyFilteredList: List<ConsultProvider?> = ArrayList<ConsultProvider?>(lowScoreData)
            if (selectedHospitalId > 0) {
                if (ALL_HOSPITAL_ID == selectedHospitalId) {
                    // skip filter hospital to add all hospital
                } else {
                    // This is to add selected hospital
                    lowScoreData.clear()
                    for (i in acuityUrgencyFilteredList.indices) {
                        val consultProvider = acuityUrgencyFilteredList[i]
                        if (selectedHospitalId == consultProvider!!.getHospitalId()) {
                            lowScoreData.add(consultProvider)
                        }
                    }
                }
            }

            // perform search
            lowScoreData = performSearch(lowScoreData) as MutableList<ConsultProvider?>
            acuityLevel = level

            // This is for ALL acuity filter option
            if (mAdapter != null) {
                // It will update urgent and non urgent data for all acuity level filter
                mAdapter!!.updateList(lowScoreData)
                mAdapter!!.notifyDataSetChanged()
                if (mAdapter!!.itemCount <= 0) {
                    // This is to show error message layout with correct message
                    showEmptyMessageForActiveTab()
                    homeBinding!!.noPatientLayout.visibility = View.VISIBLE
                } else {
                    homeBinding!!.messageRecyclerView.visibility = View.VISIBLE
                    homeBinding!!.noPatientLayout.visibility = View.GONE
                }
            }
            homeBinding!!.filterText.text = "All"
            return
        }
        var lowScoreData: MutableList<ConsultProvider?> = ArrayList()
        //Filter for Urgency
        for (i in providerFilteredList.indices) {
            val consultProvider = providerFilteredList[i]
            if (consultProvider.getScore() === level) {
                when (urgencyLevelType) {
                    Constants.UrgencyLevel.NA -> lowScoreData.add(consultProvider)
                    Constants.UrgencyLevel.Urgent -> if (consultProvider.getUrgent()!!) {
                        lowScoreData.add(consultProvider)
                    }
                    Constants.UrgencyLevel.NotUrgent -> if (!consultProvider.getUrgent()!!) {
                        lowScoreData.add(consultProvider)
                    }
                    else -> {
                    }
                }
            }
        }
        val eConsultStatusList: List<ConsultProvider?> = ArrayList<ConsultProvider?>(lowScoreData)
        lowScoreData.clear()
        //Filter for eConsult Status
        for (i in eConsultStatusList.indices) {
            val consultProvider = eConsultStatusList[i]
            when (eConsultStatus) {
                Constants.PatientStatus .NA-> lowScoreData.add(consultProvider)
                Constants.PatientStatus.Active -> if (consultProvider!!.getStatus() === Constants.PatientStatus.Active) {
                    lowScoreData.add(consultProvider)
                }
               Constants.PatientStatus.Pending -> if (consultProvider!!.getStatus() === Constants.PatientStatus.Pending) {
                    lowScoreData.add(consultProvider)
                }
            }
        }
        //Filter for Location
        val acuityUrgencyFilteredList: List<ConsultProvider?> = ArrayList<ConsultProvider?>(lowScoreData)
        if (selectedHospitalId > 0) {
            if (ALL_HOSPITAL_ID == selectedHospitalId) {
                // skip filter hospital to add all hospital
            } else {
                // This is to add selected hospital
                lowScoreData.clear()
                for (i in acuityUrgencyFilteredList.indices) {
                    val consultProvider = acuityUrgencyFilteredList[i]
                    if (selectedHospitalId == consultProvider!!.getHospitalId()) {
                        lowScoreData.add(consultProvider)
                    }
                }
            }
        }

        // perform search
        lowScoreData = performSearch(lowScoreData) as MutableList<ConsultProvider?>
        acuityLevel = level
        if (mAdapter != null) {
            mAdapter!!.updateList(lowScoreData)
            mAdapter!!.notifyDataSetChanged()
            if (mAdapter!!.itemCount <= 0) {
                // This is to show error message layout with correct message
                showEmptyMessageForActiveTab()
                homeBinding!!.noPatientLayout.visibility = View.VISIBLE
            } else {
                homeBinding!!.messageRecyclerView.visibility = View.VISIBLE
                homeBinding!!.noPatientLayout.visibility = View.GONE
            }
        }
        homeBinding!!.filterText.setText(java.lang.String.valueOf(level))
    }

    /**
     * Perform search operation for selected filter type
     *
     * @param lowScoreData original list
     */
    private fun performSearch(lowScoreData: MutableList<ConsultProvider?>): List<ConsultProvider?> {
        val searchQuery = homeBinding!!.searchEdittext.text.toString()
        if (!TextUtils.isEmpty(searchQuery)) {
            val tempOfFilterList: List<ConsultProvider?> = ArrayList<ConsultProvider?>(lowScoreData)
            lowScoreData.clear()
            for (provider in tempOfFilterList) {
                var firstName = provider!!.getFname()
                val lastName = provider.getLname()
                if (!TextUtils.isEmpty(provider.getName())) {
                    firstName = provider.getName()
                }
                if (firstName != null && firstName.trim { it <= ' ' }.toLowerCase()
                        .contains(searchQuery)
                    || lastName != null && lastName.trim { it <= ' ' }.toLowerCase()
                        .contains(searchQuery)
                ) {
                    lowScoreData.add(provider)
                }
            }
        }
        return lowScoreData
    }

    /**
     * Filtering by pending acuity
     *
     * @param level
     */
    fun filterByAcuityPending(level: Constants.AcuityLevel) {
        if (providerFilteredList == null) {
            return
        }
        if (level == Constants.AcuityLevel.NA) {
            // This is for ALL acuity option filter
            if (mAdapter != null) {
                val tempList: MutableList<ConsultProvider?> = ArrayList<ConsultProvider?>(providerFilteredList)
                val consultProviders = filterByTime(tempList)
                val searchResult = performSearch(consultProviders)
                acuityLevelPending = level
                mAdapter!!.updateList(searchResult as MutableList<ConsultProvider?>)
                mAdapter!!.notifyDataSetChanged()
                if (mAdapter!!.itemCount <= 0) {
                    showEmptyMessageForPendingTab()
                    homeBinding!!.noPatientLayout.visibility = View.VISIBLE
                } else {
                    homeBinding!!.messageRecyclerView.visibility = View.VISIBLE
                    homeBinding!!.noPatientLayout.visibility = View.GONE
                }
            }
            homeBinding!!.filterText.text = "All"
            return
        }
        val lowScoreData: MutableList<ConsultProvider?> = ArrayList()
        // filter low status data
        for (i in providerFilteredList.indices) {
            val consultProvider = providerFilteredList[i]
            if (consultProvider.getScore() == level) {
                lowScoreData.add(consultProvider)
            }
        }
        val consultProviders = filterByTime(lowScoreData)
        val searchResult = performSearch(consultProviders)
        acuityLevelPending = level
        if (mAdapter != null) {
            mAdapter!!.updateList(searchResult as MutableList<ConsultProvider?>)
            mAdapter!!.notifyDataSetChanged()
            if (mAdapter!!.itemCount <= 0) {
                showEmptyMessageForPendingTab()
                homeBinding!!.noPatientLayout.visibility = View.VISIBLE
            } else {
                homeBinding!!.messageRecyclerView.visibility = View.VISIBLE
                homeBinding!!.noPatientLayout.visibility = View.GONE
            }
        }
        homeBinding!!.filterText.setText(level.toString())
    }

    /**
     * Filtering by time with provider as input list
     *
     * @param providerList
     * @return
     */
    private fun filterByTime(providerList: MutableList<ConsultProvider?>): MutableList<ConsultProvider?> {
        if (eConsultTime === Constants.ConsultTime.NA) {
            // If it's all the we don't need to change the list
            return providerList
        } else {
            val timeList: List<ConsultProvider?> = ArrayList<ConsultProvider?>(providerList)
            providerList.clear()
            val currentTime = Calendar.getInstance()
            //Filter for time
            for (i in timeList.indices) {
                val consultProvider = timeList[i]
                var time = 0L
                if (consultProvider!!.getTime() != null && consultProvider.getTime()!! > 0) {
                    time = consultProvider.getTime()!!
                } else if (consultProvider.getJoiningTime() != null && consultProvider.getJoiningTime()!! > 0) {
                    time = consultProvider.getJoiningTime()!!
                }
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = time
                val duration = currentTime.time.time - calendar.time.time
                when (eConsultTime) {
                         Constants.ConsultTime. Fifteen -> {
                        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                        //                        Log.d(TAG, "min : " + diffInMinutes);
                        if (diffInMinutes <= 15) {
                            providerList.add(consultProvider)
                        }
                    }
                         Constants.ConsultTime.Thirty -> {
                        val diffInMin = TimeUnit.MILLISECONDS.toMinutes(duration)
                        //                        Log.d(TAG, "diffInMin : " + diffInMin);
                        if (diffInMin <= 30) {
                            providerList.add(consultProvider)
                        }
                    }
                         Constants.ConsultTime.OneHour -> {
                        val diffInHrs = TimeUnit.MILLISECONDS.toHours(duration)
                        //                        Log.d(TAG, "diffInHrs : " + diffInHrs);
                        if (diffInHrs <= 1) {
                            providerList.add(consultProvider)
                        }
                    }
                         Constants.ConsultTime.OverOneHour -> {
                        val diffInHours = TimeUnit.MILLISECONDS.toHours(duration)
                        //                        Log.d(TAG, "diffInHrs : " + diffInHours);
                        if (diffInHours > 1) {
                            providerList.add(consultProvider)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
        return providerList
    }

    /**
     * Filtering by status type
     *
     * @param patientStatus
     */
    fun filterByStatusType(patientStatus: Constants.PatientStatus) {
        if (providerFilteredList == null) {
            return
        }
        if (patientStatus == Constants.PatientStatus.NA) {
            patientStatusType = patientStatus
            if (mAdapter != null) {
                val tempArrayList: MutableList<ConsultProvider?> = ArrayList<ConsultProvider?>(providerFilteredList)
                val searchResult = performSearch(tempArrayList)
                mAdapter!!.updateList(searchResult as MutableList<ConsultProvider?>)
                mAdapter!!.notifyDataSetChanged()
                if (mAdapter!!.itemCount <= 0) {
                    showEmptyMessageForCompleteTab()
                    homeBinding!!.noPatientLayout.visibility = View.VISIBLE
                } else {
                    homeBinding!!.messageRecyclerView.visibility = View.VISIBLE
                    homeBinding!!.noPatientLayout.visibility = View.GONE
                }
            }
            homeBinding!!.filterText.text = "All"
            return
        }
        val patientStatusData: MutableList<ConsultProvider?> = ArrayList()
        // filter patient status data
        for (i in providerFilteredList.indices) {
            val consultProvider = providerFilteredList[i]
            if (consultProvider.getStatus() === patientStatus) {
                patientStatusData.add(consultProvider)
            }
        }
        val searchResult = performSearch(patientStatusData)
        patientStatusType = patientStatus
        if (mAdapter != null) {
            mAdapter!!.updateList(searchResult)
            mAdapter!!.notifyDataSetChanged()
            if (mAdapter!!.itemCount <= 0) {
                showEmptyMessageForCompleteTab()
                homeBinding!!.noPatientLayout.visibility = View.VISIBLE
            } else {
                homeBinding!!.messageRecyclerView.visibility = View.VISIBLE
                homeBinding!!.noPatientLayout.visibility = View.GONE
            }
        }
        when (patientStatusType) {
            Constants.PatientStatus.Completed -> homeBinding!!.filterText.text = resources.getString(R.string.completed)
            Constants.PatientStatus.Discharged -> homeBinding!!.filterText.text =
                java.lang.String.valueOf(Constants.PatientStatus.Completed)
        }
    }

    /**
     * This block to show error message based on user role and filter type chosen
     */
    private fun showEmptyMessageForActiveTab() {
        if (isCurrentUserIsBedSideUser()) {
            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA && eConsultStatus === Constants.PatientStatus.NA && TextUtils.isEmpty(
                    homeBinding!!.searchEdittext.text.toString())) {

                // This means there is no selected filter applied so resetted to default all section
                handleListVisibility(false)
            } else {
                showFilterErrorMessage()
            }
        } else if (isCurrentUserIsRemoteSideUser()) {
            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA && selectedHospitalId == ALL_HOSPITAL_ID && TextUtils.isEmpty(
                    homeBinding!!.searchEdittext.text.toString()
                )
            ) {

                // This means there is no selected filter applied so resetted to default all section
                handleListVisibility(false)
            } else {
                showFilterErrorMessage()
            }
        } else {
            //Handling the visibilty of the tab - Active, Pending, Complete
            //Means there is no selected filter applied so resetted to default all section
            handleListVisibility(false)
        }
    }

    /**
     * Showing the empty message for pending tab
     */
    private fun showEmptyMessageForPendingTab() {
        if (acuityLevelPending === Constants.AcuityLevel.NA && eConsultTime === Constants.ConsultTime.NA && TextUtils.isEmpty(
                homeBinding!!.searchEdittext.text.toString())) {

            //Handling the visibilty of the tab - Active, Pending, Complete
            //Means there is no selected filter applied so resetted to default all section
            handleListVisibility(false)
        } else {
            showFilterErrorMessage()
        }
    }

    /**
     * Showing the empty message for complete tab
     */
    private fun showEmptyMessageForCompleteTab() {
        if (patientStatusType === Constants.PatientStatus.NA
            && TextUtils.isEmpty(homeBinding!!.searchEdittext.text.toString())) {

            //Handling the visibilty of the tab - Active, Pending, Complete
            //Means there is no selected filter applied so resetted to default all section
            handleListVisibility(false)
        } else {
            showFilterErrorMessage()
        }
    }

    private fun showFilterErrorMessage() {
        homeBinding!!.noPatientTitle.visibility = View.VISIBLE
        homeBinding!!.noPatientText.text = resources.getString(R.string.no_results_for_filter)
    }

    private fun isCurrentUserIsBedSideUser(): Boolean {
        val role: String? = PrefUtility().getRole(this)
        return if (!TextUtils.isEmpty(role)) {
            role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)
        } else false
    }

    private fun isCurrentUserIsRemoteSideUser(): Boolean {
        val role: String? = PrefUtility().getRole(this)
        return if (!TextUtils.isEmpty(role)) {
            role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)
        } else false
    }

    /**
     * Showing the dialog with Filter by acuity
     *
     * @param context
     */
    fun FilterByAcuityDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_acuity_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlLow = dialog.findViewById<View>(R.id.rlLow) as RelativeLayout
        val rlMedium = dialog.findViewById<View>(R.id.rlMedium) as RelativeLayout
        val rlHigh = dialog.findViewById<View>(R.id.rlHigh) as RelativeLayout
        val rlAll = dialog.findViewById<View>(R.id.rlAll) as RelativeLayout
        val imgLowAcuity = dialog.findViewById<View>(R.id.imgLowAcuity) as ImageView
        val imgMediumAcuity = dialog.findViewById<View>(R.id.imgMediumAcuity) as ImageView
        val imgHighAcuity = dialog.findViewById<View>(R.id.imgHighAcuity) as ImageView
        val imgAcuityAll = dialog.findViewById<View>(R.id.imgAcuityAll) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        when (acuityLevel) {
            Constants.AcuityLevel .NA -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.VISIBLE
            }
            Constants.AcuityLevel .Low -> {
                imgLowAcuity.visibility = View.VISIBLE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.GONE
            }
            Constants.AcuityLevel .Medium -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.VISIBLE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.GONE
            }
            Constants.AcuityLevel .High -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.VISIBLE
                imgAcuityAll.visibility = View.GONE
            }
            else -> {
            }
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        //Filtering by low acuity
        rlLow.setOnClickListener { //Filter active screen by score - low
            filterByDialog?.dismiss()
            filterByAcuityActive(Constants.AcuityLevel.Low)
            dialog.dismiss()
            filterselectactive()
        }
        //Filtering by medium acuity
        rlMedium.setOnClickListener { //Filter active screen by score - medium
            filterByDialog?.dismiss()
            filterByAcuityActive(Constants.AcuityLevel.Medium)
            dialog.dismiss()
            filterselectactive()
        }
        // Filtering by high acuity
        rlHigh.setOnClickListener { //Filter active screen by score - High
            filterByDialog?.dismiss()
            filterByAcuityActive(Constants.AcuityLevel.High)
            dialog.dismiss()
            filterselectactive()
        }
        // Filtering by NA acuity
        rlAll.setOnClickListener {
            filterByDialog?.dismiss()
            acuityLevel = Constants.AcuityLevel.NA
            //Filter active screen by score - NA
            filterByAcuityActive(Constants.AcuityLevel.NA)
            homeBinding!!.filterText.text = "All"
            if (role != null && role.equals(
                    Constants.ProviderRole.BD.toString(),
                    ignoreCase = true)) {
                // If the role is BP - Display Filter dialog based on -  Urgency, Status
                if (urgencyLevelType === Constants.UrgencyLevel.NA && eConsultStatus === Constants.PatientStatus.NA) {
                    filterdeselectactive()
                }
            } else {
                // If the role is RP- Display Filter dialog based on -  Urgency, Status
                if (urgencyLevelType === Constants.UrgencyLevel.NA && selectedHospitalId == ALL_HOSPITAL_ID) {
                    filterdeselectactive()
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * Showing the dialog with Filter by acuity pending status
     *
     * @param context
     */
    fun FilterByAcuityPendingDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_acuity_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlLow = dialog.findViewById<View>(R.id.rlLow) as RelativeLayout
        val rlMedium = dialog.findViewById<View>(R.id.rlMedium) as RelativeLayout
        val rlHigh = dialog.findViewById<View>(R.id.rlHigh) as RelativeLayout
        val rlAll = dialog.findViewById<View>(R.id.rlAll) as RelativeLayout
        val imgLowAcuity = dialog.findViewById<View>(R.id.imgLowAcuity) as ImageView
        val imgMediumAcuity = dialog.findViewById<View>(R.id.imgMediumAcuity) as ImageView
        val imgHighAcuity = dialog.findViewById<View>(R.id.imgHighAcuity) as ImageView
        val imgAcuityAll = dialog.findViewById<View>(R.id.imgAcuityAll) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        when (acuityLevelPending) {
            Constants.AcuityLevel.NA -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.VISIBLE
            }
            Constants.AcuityLevel. Low -> {
                imgLowAcuity.visibility = View.VISIBLE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.GONE
            }
            Constants.AcuityLevel.Medium -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.VISIBLE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.GONE
            }
            Constants.AcuityLevel.High -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.VISIBLE
                imgAcuityAll.visibility = View.GONE
            }
            else -> {
            }
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        rlLow.setOnClickListener { //Filtering by pending acuity - Low
            filterByAcuityPending(Constants.AcuityLevel.Low)
            dialog.dismiss()
            filterselectpending()
        }
        rlMedium.setOnClickListener { //Filtering by pending acuity - Medium
            filterByAcuityPending(Constants.AcuityLevel.Medium)
            dialog.dismiss()
            filterselectpending()
        }
        rlHigh.setOnClickListener { //Filtering by pending acuity - High
            filterByAcuityPending(Constants.AcuityLevel.High)
            dialog.dismiss()
            filterselectpending()
        }
        rlAll.setOnClickListener {
            acuityLevelPending = Constants.AcuityLevel.NA
            //Filtering by pending acuity - NA
            filterByAcuityPending(Constants.AcuityLevel.NA)
            homeBinding!!.filterText.text = "All"
            dialog.dismiss()
            if (eConsultTime === Constants.ConsultTime.NA) {
                filterdeselectpending()
            } else {
            }
        }
        dialog.show()
    }

    /**
     * Display Filter dialog based on -  Urgency, Status
     *
     * @param context
     */
    fun FilterByDialog(context: Context?) {
        filterByDialog = Dialog(context!!, R.style.Theme_Dialog)
        filterByDialog?.setContentView(R.layout.filter_by_dialog)
        filterByDialog?.setCancelable(false)
        filterByDialog?.setCanceledOnTouchOutside(false)
        filterByDialog?.getWindow()!!.setGravity(Gravity.BOTTOM)
        filterByDialog?.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        filterByDialog?.getWindow()!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlUrgency = filterByDialog?.findViewById<View>(R.id.rlUrgency) as RelativeLayout
        val rleConsultStatus =
            filterByDialog?.findViewById<View>(R.id.rleConsultStatus) as RelativeLayout
        val imgCancel = filterByDialog?.findViewById<View>(R.id.imgCancel) as ImageButton
        rlUrgency.setOnClickListener { //Displaying Filter by urgency dialog
            FilterByUrgencyDialog(this)
        }
        rleConsultStatus.setOnClickListener { //Displaying Filter by status dialog
            FilterByStatusDialog(this)
        }
        imgCancel.setOnClickListener { filterByDialog?.dismiss() }
        filterByDialog?.show()
    }

    /**
     * Displaying the remote side filter options dialog
     *
     * @param context
     */
    fun FilterByRpDialog(context: Context?) {
        filterByDialog = Dialog(context!!, R.style.Theme_Dialog)
        filterByDialog?.setContentView(R.layout.filter_by_rp_dialog)
        filterByDialog?.setCancelable(false)
        filterByDialog?.setCanceledOnTouchOutside(false)
        filterByDialog?.getWindow()!!.setGravity(Gravity.BOTTOM)
        filterByDialog?.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        filterByDialog?.getWindow()!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlUrgency = filterByDialog?.findViewById<View>(R.id.rlUrgency) as RelativeLayout
        val rlLocation = filterByDialog?.findViewById<View>(R.id.rlLocation) as RelativeLayout
        val imgCancel = filterByDialog?.findViewById<View>(R.id.imgCancel) as ImageButton
        rlUrgency.setOnClickListener { v ->
            handleMultipleClick(v)
            //Displaying Filter by urgency dialog
            FilterByUrgencyDialog(this)
        }
        rlLocation.setOnClickListener { v ->
            handleMultipleClick(v)
            //Displaying Filter by location dialog
            FilterByLocationDialog(this)
        }
        imgCancel.setOnClickListener { filterByDialog!!.dismiss() }
        filterByDialog!!.show()
    }

    /**
     * Displaying Filter by urgency dialog
     *
     *
     */
    //filter methods active
    fun filterselectactive() {
        homeBinding!!.txtClearactive.visibility = View.VISIBLE
        homeBinding!!.filterViewactive.setImageDrawable(resources.getDrawable(R.drawable.ic_filter_selected))
    }

    fun filterdeselectactive() {
        homeBinding!!.txtClearactive.visibility = View.GONE
        homeBinding!!.filterViewactive.setImageDrawable(resources.getDrawable(R.drawable.ic_filter))
        urgencyLevelType = Constants.UrgencyLevel.NA
        selectedHospitalId = ALL_HOSPITAL_ID
        eConsultStatus = Constants.PatientStatus.NA
        filterByAcuityActive(Constants.AcuityLevel.NA)
    }

    //filter metods pending
    fun filterselectpending() {
        homeBinding!!.txtClearpending.visibility = View.VISIBLE
        homeBinding!!.filterViewpending.setImageDrawable(resources.getDrawable(R.drawable.ic_filter_selected))
    }

    fun filterdeselectpending() {
        homeBinding!!.txtClearpending.visibility = View.GONE
        homeBinding!!.filterViewpending.setImageDrawable(resources.getDrawable(R.drawable.ic_filter))
    }


    //filter methods complete
    fun filterselectcomplet() {
        homeBinding!!.txtClearcomplet.visibility = View.VISIBLE
    }

    fun filterdeselectcomplet() {
        filterByStatusType(Constants.PatientStatus.NA)
        homeBinding!!.txtClearcomplet.visibility = View.GONE
    }


    //end filter methods
    fun FilterByUrgencyDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_by_urgency_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val imgBack = dialog.findViewById<View>(R.id.imgBack) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val rlUrgent = dialog.findViewById<View>(R.id.rlUrgent) as RelativeLayout
        val rlNonUrgent = dialog.findViewById<View>(R.id.rlNonUrgent) as RelativeLayout
        val rlAll = dialog.findViewById<View>(R.id.rlAll) as RelativeLayout
        val imgUrgent = dialog.findViewById<View>(R.id.imgUrgent) as ImageView
        val imgNonUrgent = dialog.findViewById<View>(R.id.imgNonUrgent) as ImageView
        val imgAll = dialog.findViewById<View>(R.id.imgAll) as ImageView
        when (urgencyLevelType) {
            Constants.UrgencyLevel.NA -> {
                imgUrgent.visibility = View.GONE
                imgNonUrgent.visibility = View.GONE
                imgAll.visibility = View.VISIBLE
            }
            Constants.UrgencyLevel. Urgent -> {
                imgUrgent.visibility = View.VISIBLE
                imgNonUrgent.visibility = View.GONE
                imgAll.visibility = View.GONE
            }
            Constants.UrgencyLevel. NotUrgent -> {
                imgUrgent.visibility = View.GONE
                imgNonUrgent.visibility = View.VISIBLE
                imgAll.visibility = View.GONE
            }
            else -> {
            }
        }
        imgBack.setOnClickListener { dialog.dismiss() }
        imgCancel.setOnClickListener { dialog.dismiss() }
        rlUrgent.setOnClickListener {
            urgencyLevelType = Constants.UrgencyLevel.Urgent
            //Filter active screen by score
            filterByAcuityActive(acuityLevel)
            dialog.dismiss()
            filterByDialog!!.dismiss()
            filterselectactive()
        }
        rlNonUrgent.setOnClickListener {
            urgencyLevelType = Constants.UrgencyLevel.NotUrgent
            //Filter active screen by score
            filterByAcuityActive(acuityLevel)
            dialog.dismiss()
            filterByDialog!!.dismiss()
            filterselectactive()
        }
        rlAll.setOnClickListener {
            urgencyLevelType = Constants.UrgencyLevel.NA
            //Filter active screen by score
            filterByAcuityActive(acuityLevel)
            dialog.dismiss()
            if (role != null && role.equals(
                    Constants.ProviderRole.BD.toString(),
                    ignoreCase = true)) {
                // If the role is BP - Display Filter dialog based on -  Urgency, Status
                if (acuityLevel === Constants.AcuityLevel.NA && eConsultStatus === Constants.PatientStatus.NA) {
                    filterdeselectactive()
                }
            } else {
                // If the role is RP- Display Filter dialog based on -  Urgency, Status
                if (acuityLevel === Constants.AcuityLevel.NA && selectedHospitalId == ALL_HOSPITAL_ID) {
                    filterdeselectactive()
                }
            }
            filterByDialog!!.dismiss()
        }
        dialog.show()
    }

    /**
     * Displaying Filter by status dialog
     *
     * @param context
     */
    fun FilterByStatusDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_by_status_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val imgBack = dialog.findViewById<View>(R.id.imgBack) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val rlAccepted = dialog.findViewById<View>(R.id.rlAccepted) as RelativeLayout
        val rlPending = dialog.findViewById<View>(R.id.rlPending) as RelativeLayout
        val rlAll = dialog.findViewById<View>(R.id.rlAll) as RelativeLayout
        val imgAcceptedRP = dialog.findViewById<View>(R.id.imgAcceptedRP) as ImageView
        val imgPendingRP = dialog.findViewById<View>(R.id.imgPendingRP) as ImageView
        val imgAll = dialog.findViewById<View>(R.id.imgAll) as ImageView
        when (eConsultStatus) {
            Constants.PatientStatus.NA -> {
                imgAcceptedRP.visibility = View.GONE
                imgPendingRP.visibility = View.GONE
                imgAll.visibility = View.VISIBLE
            }
            Constants.PatientStatus.Active -> {
                imgAcceptedRP.visibility = View.VISIBLE
                imgPendingRP.visibility = View.GONE
                imgAll.visibility = View.GONE
            }
            Constants.PatientStatus. Pending -> {
                imgAcceptedRP.visibility = View.GONE
                imgPendingRP.visibility = View.VISIBLE
                imgAll.visibility = View.GONE
            }
            else -> {
            }
        }
        imgBack.setOnClickListener { dialog.dismiss() }
        imgCancel.setOnClickListener { dialog.dismiss() }
        rlAccepted.setOnClickListener {
            eConsultStatus = Constants.PatientStatus.Active
            //Filter active screen by score
            filterByAcuityActive(acuityLevel)
            dialog.dismiss()
            filterByDialog!!.dismiss()
            filterselectactive()
        }
        rlPending.setOnClickListener {
            eConsultStatus = Constants.PatientStatus.Pending
            //Filter active screen by score
            filterByAcuityActive(acuityLevel)
            dialog.dismiss()
            filterByDialog!!.dismiss()
            filterselectactive()
        }
        rlAll.setOnClickListener {
            eConsultStatus = Constants.PatientStatus.NA
            //Filter active screen by score
            filterByAcuityActive(acuityLevel)
            dialog.dismiss()
            filterByDialog!!.dismiss()
            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA) {
                filterdeselectactive()
            }
        }
        dialog.show()
    }

    /**
     * Displays the Filter by Location dialog
     *
     * @param context
     */
    fun FilterByLocationDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_by_location_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val imgBack = dialog.findViewById<View>(R.id.imgBack) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val txtTitle = dialog.findViewById<View>(R.id.txtTitle) as TextView
        txtTitle.text = getString(R.string.filter_by_location)

        // Setting up the location list adapter
        val recyclerviewLocation =
            dialog.findViewById<View>(R.id.recyclerviewLocation) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerviewLocation.layoutManager = linearLayoutManager
        if (providerFilteredList != null) {
            val hospitalList = HashMap<Long?, ConsultProvider?>()
            for (i in providerFilteredList.indices) {
                hospitalList[providerFilteredList[i].getHospitalId()] = providerFilteredList[i]
            }
            val allOption = ConsultProvider()
            allOption.setHospital("All")
            allOption.setHospitalId(ALL_HOSPITAL_ID)
            //hospitalList.put(allOptin.getHospitalId(), allOptin);
            locationListAdapter = LocationListAdapter(object : LocationListAdapter.HospitalRecyclerListener {


                override fun onItemSelected(hospital: ConsultProvider?) {
                    selectedHospitalId = hospital?.getHospitalId()!!
                    //Filter active screen by score
                    if (hospital.getName() == null) {
                        if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA) {
                            filterdeselectactive()
                        } else {
                        }
                    } else {
                        filterselectactive()
                    }
                    filterByAcuityActive(acuityLevel)
                    dialog.dismiss()
                    filterByDialog!!.dismiss()
                }
            }, hospitalList, selectedHospitalId)
            locationListAdapter!!.addItem(allOption)
            recyclerviewLocation.adapter = locationListAdapter
        }
        imgBack.setOnClickListener { dialog.dismiss() }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    /**
     * Displaying the Filter by time dialog
     *
     * @param context
     */
    fun FilterByTimeDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_by_time)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val rlFifteenMin = dialog.findViewById<View>(R.id.rlFifteenMin) as RelativeLayout
        val rlThirtyMin = dialog.findViewById<View>(R.id.rlThirtyMin) as RelativeLayout
        val rlOneHour = dialog.findViewById<View>(R.id.rlOneHour) as RelativeLayout
        val rlOverOneHour = dialog.findViewById<View>(R.id.rlOverOneHour) as RelativeLayout
        val rlAll = dialog.findViewById<View>(R.id.rlAll) as RelativeLayout
        val imgFifteenMin = dialog.findViewById<View>(R.id.imgFifteenMin) as ImageView
        val imgThirtyMin = dialog.findViewById<View>(R.id.imgThirtyMin) as ImageView
        val imgOneHour = dialog.findViewById<View>(R.id.imgOneHour) as ImageView
        val imgOverOneHour = dialog.findViewById<View>(R.id.imgOverOneHour) as ImageView
        val imgTimeAll = dialog.findViewById<View>(R.id.imgTimeAll) as ImageView
        when (eConsultTime) {
            Constants.ConsultTime.NA -> {
                imgFifteenMin.visibility = View.GONE
                imgThirtyMin.visibility = View.GONE
                imgOneHour.visibility = View.GONE
                imgOverOneHour.visibility = View.GONE
                imgTimeAll.visibility = View.VISIBLE
            }
            Constants.ConsultTime. Fifteen -> {
                imgFifteenMin.visibility = View.VISIBLE
                imgThirtyMin.visibility = View.GONE
                imgOneHour.visibility = View.GONE
                imgOverOneHour.visibility = View.GONE
                imgTimeAll.visibility = View.GONE
            }
            Constants.ConsultTime. Thirty -> {
                imgFifteenMin.visibility = View.GONE
                imgThirtyMin.visibility = View.VISIBLE
                imgOneHour.visibility = View.GONE
                imgOverOneHour.visibility = View.GONE
                imgTimeAll.visibility = View.GONE
            }
            Constants.ConsultTime.OneHour -> {
                imgFifteenMin.visibility = View.GONE
                imgThirtyMin.visibility = View.GONE
                imgOneHour.visibility = View.VISIBLE
                imgOverOneHour.visibility = View.GONE
                imgTimeAll.visibility = View.GONE
            }
            Constants.ConsultTime.OverOneHour -> {
                imgFifteenMin.visibility = View.GONE
                imgThirtyMin.visibility = View.GONE
                imgOneHour.visibility = View.GONE
                imgOverOneHour.visibility = View.VISIBLE
                imgTimeAll.visibility = View.GONE
            }
            else -> {
            }
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        rlFifteenMin.setOnClickListener {
            eConsultTime = Constants.ConsultTime.Fifteen
            //Filtering by pending acuity
            filterByAcuityPending(acuityLevelPending)
            dialog.dismiss()
            filterselectpending()
        }
        rlThirtyMin.setOnClickListener {
            eConsultTime = Constants.ConsultTime.Thirty
            //Filtering by pending acuity
            filterByAcuityPending(acuityLevelPending)
            dialog.dismiss()
            filterselectpending()
        }
        rlOneHour.setOnClickListener {
            eConsultTime = Constants.ConsultTime.OneHour
            //Filtering by pending acuity
            filterByAcuityPending(acuityLevelPending)
            dialog.dismiss()
            filterselectpending()
        }
        rlOverOneHour.setOnClickListener {
            eConsultTime = Constants.ConsultTime.OverOneHour
            //Filtering by pending acuity
            filterByAcuityPending(acuityLevelPending)
            dialog.dismiss()
            filterselectpending()
        }
        rlAll.setOnClickListener {
            eConsultTime = Constants.ConsultTime.NA
            //Filtering by pending acuity
            filterByAcuityPending(acuityLevelPending)
            dialog.dismiss()
            if (acuityLevelPending === Constants.AcuityLevel.NA) {
                filterdeselectpending()
            } else {
            }
        }
        dialog.show()
    }

    fun FilterByType(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_by_type_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val imgCompleted = dialog.findViewById<View>(R.id.imgCompleted) as ImageView
        val imgDischarged = dialog.findViewById<View>(R.id.imgDischarged) as ImageView
        val imgAll = dialog.findViewById<View>(R.id.imgAll) as ImageView
        val rlCompleted = dialog.findViewById<View>(R.id.rlCompleted) as RelativeLayout
        val rlDischarged = dialog.findViewById<View>(R.id.rlDischarged) as RelativeLayout
        val rlAll = dialog.findViewById<View>(R.id.rlAll) as RelativeLayout
        when (patientStatusType) {
            Constants.PatientStatus.NA -> {
                imgCompleted.visibility = View.GONE
                imgDischarged.visibility = View.GONE
                imgAll.visibility = View.VISIBLE
            }
            Constants.PatientStatus.Completed -> {
                imgCompleted.visibility = View.VISIBLE
                imgDischarged.visibility = View.GONE
                imgAll.visibility = View.GONE
            }
            Constants.PatientStatus.Discharged -> {
                imgCompleted.visibility = View.GONE
                imgDischarged.visibility = View.VISIBLE
                imgAll.visibility = View.GONE
            }
            else -> {
            }
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        rlCompleted.setOnClickListener { // Filtering by status type- Discharged
            filterByStatusType(Constants.PatientStatus.Completed)
            homeBinding!!.filterText.text = resources.getString(R.string.completed)
            dialog.dismiss()
            filterselectcomplet()
        }
        rlDischarged.setOnClickListener { // Filtering by status type - Exit
            filterByStatusType(Constants.PatientStatus.Discharged)
            homeBinding!!.filterText.text =
                java.lang.String.valueOf(Constants.PatientStatus.Discharged)
            dialog.dismiss()
            filterselectcomplet()
        }
        rlAll.setOnClickListener { // Filtering by status type - NA
            // filterByStatusType(Constants.PatientStatus.NA);
            patientStatusType = Constants.PatientStatus.NA
            homeBinding!!.filterText.text = "All"
            dialog.dismiss()
            filterdeselectcomplet()
        }
        dialog.show()
    }



    enum class TAB {
        Active, Patients, Pending
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PrefUtility().getRole(this)
        containerParent = findViewById(R.id.container)
        homeBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.activity_home, containerParent, true)

        drawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        intentCensus = intent.getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)!!

        homeBinding?.acuityFilterView?.setOnClickListener { //Handling multiple click event with view
            handleMultipleClick(homeBinding?.acuityFilterView!!)
            //Showing the dialog with Filter by acuity
            FilterByAcuityDialog(this)
        }

        // Setting up the view
        setView()

        // Pre clear all the notifications
        clearAllNotifications()

    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //Handling the notification click based on the intent
        if (intent != null) {
            handleNotification(intent)
        }
    }

    /**
     * Setting up the view
     */
    private fun setView() {
        binding!!.leftNavView.setNavigationItemSelectedListener(this)
        binding!!.rightNavView.setNavigationItemSelectedListener(this)
        // Handling the firebase listview setup
        setupFirebaseListView()
        //  Handling tab click listener
        handleTabButtons()
        //Initialzing the tool bar
        initToolbar()
        handleDrawerHeaderView()
        // Fetching the directory details
        fetchDirectory()
        //Handling the notification click based on the intent
        handleNotification(intent)
        homeBinding!!.activetab.visibility = View.VISIBLE
        //Designation based layout visibility handling
        strDesignation = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        if (strDesignation.equals("md/do", ignoreCase = true)) {
            homeBinding!!.pendingBtnLayout.visibility = View.VISIBLE
        } else {
            homeBinding!!.pendingBtnLayout.visibility = View.GONE
        }
        // Getting the bundle data intents
        val extras = intent.extras
        if (extras != null) {
            val select = extras.getString(Constants.IntentKeyConstants.TARGET_PAGE, "")
            if (select == "pending") {
                getnewpendingchange()
                if (homeBinding!!.pendingBtnLayout.visibility === View.VISIBLE) {
                    homeBinding!!.pendingBtnLayout.performClick()
                }
            } else if (select == "completed") {
                homeBinding!!.completedBtnLayout.performClick()
            } else if (select == "active") {
                homeBinding!!.activeBtnLayout.performClick()
            } else {
                homeBinding!!.activeBtnLayout.performClick()
            }
        } else {
            homeBinding!!.activeBtnLayout.performClick()
        }

        // Swipe refress layout handling
        homeBinding!!.swipeLayout.setOnRefreshListener {
            //Handling the firebase listview setup
            setupFirebaseListView()
            homeBinding!!.swipeLayout.isRefreshing = false


            // reset filter for active tab
            acuityLevel = Constants.AcuityLevel.NA
            urgencyLevelType = Constants.UrgencyLevel.NA
            eConsultStatus = Constants.PatientStatus.NA
            selectedHospitalId = ALL_HOSPITAL_ID

            // reset filter for pending tab
            acuityLevelPending = Constants.AcuityLevel.NA
            eConsultTime = Constants.ConsultTime.NA

            // reset filter for complete tab
            patientStatusType = Constants.PatientStatus.NA
            homeBinding!!.filterText.text = "All"


            //clear
            filterdeselectactive()
            filterdeselectcomplet()
            filterByAcuityPending(Constants.AcuityLevel.NA)
            eConsultTime = Constants.ConsultTime.NA
            //Filtering by pending acuity
            filterByAcuityPending(acuityLevelPending)
            filterdeselectpending()

           if (mAdapter != null) {
            mAdapter!!.updateList(providerFilteredList)
            mAdapter!!.notifyDataSetChanged()
            if (mAdapter!!.itemCount <= 0) {
                if (selectedTab == TAB.Active.ordinal) {
                    showEmptyMessageForActiveTab()
                } else if (selectedTab == TAB.Pending.ordinal) {
                    showEmptyMessageForPendingTab()
                } else if (selectedTab == TAB.Patients.ordinal) {
                    showEmptyMessageForCompleteTab()
                } else {
                    //Handling the visibilty of the tab - Active, Pending, Complete
                    //Means there is no selected filter applied so resetted to default all section
                    handleListVisibility(false)
                }
            } else {
                //Handling the visibilty of the tab - Active, Pending, Complete
                handleListVisibility(true)
            }
        }
            homeBinding!!.searchLayout.visibility = View.GONE
            homeBinding!!.searchEdittext.setText("")
            homeBinding!!.tabsLayout.visibility = View.VISIBLE
            //clear
        }

        // Click listener fot add patient task - Redirected to AddPatientActivity
        homeBinding!!.fab.setOnClickListener {
            handleMultipleClick(homeBinding!!.fab)
            val intent = Intent(this@HomeActivity, AddPatientActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.FROM_PAGE, "myConsults")
            startActivity(intent)
        }
        /**
         * Acuity filter view click listener
         */
        homeBinding!!.txtClearactive.setOnClickListener { v ->
            handleMultipleClick(v)
            // isActivePatient = false;
            // filterByAcuityActive(Constants.AcuityLevel.NA);
            filterdeselectactive()
        }
        homeBinding!!.txtClearpending.setOnClickListener { v ->
            handleMultipleClick(v)
            // isActivePatient = false;
            filterByAcuityPending(Constants.AcuityLevel.NA)
            eConsultTime = Constants.ConsultTime.NA
            //Filtering by pending acuity
            filterByAcuityPending(acuityLevelPending)
            filterdeselectpending()
        }
        homeBinding!!.txtClearcomplet.setOnClickListener { v ->
            handleMultipleClick(v)
            // isActivePatient = false;
            filterdeselectcomplet()
            //  filterByAcuityActive(Constants.AcuityLevel.NA);
        }


        homeBinding!!.filterViewactive.setOnClickListener {
            handleMultipleClick(homeBinding!!.filterViewactive)
            if (role != null && role.equals(
                    Constants.ProviderRole.BD.toString(),
                    ignoreCase = true
                )
            ) {
                if (selectedTab == TAB.Active.ordinal) {
                    FilterByDialog(this@HomeActivity)
                }
            } else {
                if (selectedTab == TAB.Active.ordinal) {
                    FilterByRpDialog(this@HomeActivity)
                } else {
                    FilterByTimeDialog(this@HomeActivity)
                }
            }
        }
        homeBinding!!.filterViewpending.setOnClickListener {
            handleMultipleClick(homeBinding!!.filterViewpending)
            if (role != null && role.equals(
                    Constants.ProviderRole.BD.toString(),
                    ignoreCase = true
                )
            ) {
                if (selectedTab == TAB.Active.ordinal) {
                    FilterByDialog(this@HomeActivity)
                }
            } else {
                if (selectedTab == TAB.Active.ordinal) {
                    FilterByRpDialog(this@HomeActivity)
                } else {
                    FilterByTimeDialog(this@HomeActivity)
                }
            }
        }
        homeBinding!!.searchIcon.setOnClickListener {
            searchQueryStr = ""
            homeBinding!!.searchLayout.visibility = View.VISIBLE
            homeBinding!!.tabsLayout.visibility = View.GONE
        }
        homeBinding!!.closeSearch.setOnClickListener { view ->
            searchQueryStr = ""
            if (TextUtils.isEmpty(homeBinding!!.searchEdittext.text.toString())) {
                homeBinding!!.searchLayout.visibility = View.GONE
                homeBinding!!.tabsLayout.visibility = View.VISIBLE
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
            } else if (homeBinding!!.searchEdittext.text.toString().length > 0) {
                homeBinding!!.searchEdittext.setText("")
                homeBinding!!.tabsLayout.visibility = View.GONE
            }
        }

        //Query listener
        setSearchTextWatcher()
    }

    /**
     * Query listener
     */
    private fun setSearchTextWatcher() {
        homeBinding!!.searchEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {

                // Perform search
                onQuerySearch(editable.toString())
            }
        })
    }

    /**
     * Getting the current user
     *
     * @return
     */
    private fun getCurrentUser(): Provider? {
        if (currentUser == null) {
            currentUser = PrefUtility().getProviderObject(this)
        }
        return currentUser
    }

    /**
     * Initialzing the tool bar
     */
    private fun initToolbar() {
        val pId: Long? = PrefUtility().getProviderId(this)
        setSupportActionBar(homeBinding!!.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        val role: String? = PrefUtility().getRole(this)
        if (role != null && role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
            homeBinding!!.fab.visibility = View.VISIBLE
        } else {
            homeBinding!!.fab.visibility = View.GONE
        }
        homeBinding!!.idNavigationIcon.setImageResource(R.drawable.ic_menu_icon)

        homeBinding!!.idNavigationIcon.setOnClickListener { view ->
            PrefUtility().getProviderId(this)?.let { getProviderDetailsById(it) }
            val intent = Intent(this, MyDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
        }
        strFeedbackForm =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FEEDBACK_URL, "")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_screen_menu, menu)
        val addPatientMenu = menu.findItem(R.id.add_patient_menu)
        val directoryMenu = menu.findItem(R.id.directory)
        addPatientMenu.isVisible = false
        directoryMenu.isVisible = false
        val role: String? = PrefUtility().getRole(this)
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            addPatientMenu.isVisible = false
            directoryMenu.isVisible = false
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (getCurrentUser() != null) {
            if (!TextUtils.isEmpty(getCurrentUser()!!.getLcpType())) {
                if (getCurrentUser()!!.getLcpType()
                        .equals(Constants.KeyHardcodeToken.LCP_TYPE_HOME,ignoreCase = true)
                ) {
                    menu.findItem(R.id.add_patient_menu).isVisible = false
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_patient_menu -> {
                handleMultipleClick(item)
                onClickAddPatient()
            }
            R.id.directory -> {
                onClickDirectory()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleDrawerHeaderView() {
        val profileImg: CircularImageView = navHeaderView!!.findViewById(R.id.id_profile_img)
        val defaultImgView: TextView = navHeaderView!!.findViewById(R.id.default_image_view)
        val cameraIcon: ImageView = navHeaderView!!.findViewById(R.id.cameraIcon)
        val imgLayout: FrameLayout = navHeaderView!!.findViewById(R.id.id_image_layout)
        val nameTxt: TextView = navHeaderView!!.findViewById(R.id.id_name)
        val hospitalTxt: TextView = navHeaderView!!.findViewById(R.id.id_hospital_name)
        val roleTxt: TextView = navHeaderView!!.findViewById(R.id.id_role)
        val emailTxt: TextView = navHeaderView!!.findViewById(R.id.id_email)
        val menuMyConsultsView: LinearLayout = navHeaderView!!.findViewById(R.id.menuMyConsults)
        val menuHandOffPatients: LinearLayout =
            navHeaderView!!.findViewById(R.id.menuHandoffPatients)
        val menuTrainingMaterials: LinearLayout =
            navHeaderView!!.findViewById(R.id.menuTrainingMaterials)
        val menuEulaTerms: LinearLayout = navHeaderView!!.findViewById(R.id.menuEulaTerms)
        val menuSystemAlert: LinearLayout = navHeaderView!!.findViewById(R.id.menuSystemAlert)
        val menuContactAdmin: LinearLayout = navHeaderView!!.findViewById(R.id.menuContactAdmin)
        val menuVirtualTeam: LinearLayout = navHeaderView!!.findViewById(R.id.menuVirtualTeam)
        val menuChangePassword: LinearLayout = navHeaderView!!.findViewById(R.id.menuChangePassword)
        val menuFeedbackView: LinearLayout = navHeaderView!!.findViewById(R.id.menuFeedback)
        val menuSignoutView: LinearLayout = navHeaderView!!.findViewById(R.id.menuSignout)

        val name: String = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        val hospitalName: String =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.HOSPITAL_NAME, "")
        val imageURL: String =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.PROFILE_IMG_URL, "")
        val email: String =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        if (!TextUtils.isEmpty(name)) {
            nameTxt.text = name
        }
        if (!TextUtils.isEmpty(hospitalName)) {
            hospitalTxt.text = hospitalName
        }
        if (!TextUtils.isEmpty(role)) {
            if (role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
                roleTxt.setText(R.string.bedside_provider)
                hospitalTxt.visibility = View.VISIBLE
            } else if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                roleTxt.setText(R.string.remote_provider)
                hospitalTxt.visibility = View.GONE
            }
        }
        if (getCurrentUser() != null) {
            if (!TextUtils.isEmpty(getCurrentUser()!!.getLcpType())) {
                if (getCurrentUser()!!.getLcpType()
                        .equals(Constants.KeyHardcodeToken.LCP_TYPE_HOME,ignoreCase = true)
                ) {
                    hospitalTxt.visibility = View.GONE
                    roleTxt.text = "Home Provider"
                }
            }
        }
        if (!TextUtils.isEmpty(imageURL)) {
            profileImg.setVisibility(View.VISIBLE)
            defaultImgView.visibility = View.GONE
            cameraIcon.visibility = View.GONE
       HomeActivity.ImageLoader(this@HomeActivity, imageURL)
                .execute()
        } else {
            profileImg.setVisibility(View.GONE)
            defaultImgView.visibility = View.VISIBLE
            cameraIcon.visibility = View.VISIBLE
            defaultImgView.setText(UtilityMethods().getNameText(name))
        }
        if (!TextUtils.isEmpty(email)) {
            emailTxt.visibility = View.VISIBLE
            emailTxt.text = email
        } else {
            emailTxt.visibility = View.GONE
        }
        imgLayout.setOnClickListener { view: View? -> selectImage() }


        menuMyConsultsView.setOnClickListener { view: View? ->

            drawerLayout!!.closeDrawers()
        }
        menuChangePassword.setOnClickListener { view: View? ->

            drawerLayout!!.closeDrawers()
            val intent = Intent(this@HomeActivity, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
        menuHandOffPatients.setOnClickListener { view: View? ->

            drawerLayout!!.closeDrawers()
            val intent = Intent(this@HomeActivity, HandOffPatientsActivity::class.java)
            startActivity(intent)
        }
        menuTrainingMaterials.setOnClickListener { view: View? ->

            drawerLayout!!.closeDrawers()

            val url: String = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TUTORIAL_URL, "")
            val uri =
                Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        menuEulaTerms.setOnClickListener { view: View? ->

            drawerLayout!!.closeDrawers()
            val intent = Intent(this@HomeActivity, TermsAndConditionsActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.SHOW_TERMS_BUTTON, false)
            startActivity(intent)
        }
        menuSystemAlert.setOnClickListener { view: View? ->

            drawerLayout!!.closeDrawers()
            val intent = Intent(this@HomeActivity, SystemAlertActivity::class.java)
            startActivity(intent)
        }
        menuVirtualTeam.setOnClickListener { view: View? ->

            drawerLayout!!.closeDrawers()
            val intent = Intent(this@HomeActivity, MyVirtualTeamsActivity::class.java)
            startActivity(intent)
        }
        menuContactAdmin.setOnClickListener { view: View? ->

            drawerLayout!!.closeDrawers()
            val intent = Intent(this@HomeActivity, ContactAdminActivity::class.java)
            startActivity(intent)
        }
        menuFeedbackView.setOnClickListener { view: View? ->
            drawerLayout!!.closeDrawers()

            val uri = Uri.parse(strFeedbackForm)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        menuSignoutView.setOnClickListener { view: View? ->

            drawerLayout!!.closeDrawers()
            handleMultipleClick(view!!)
            onClickLogout()
        }
    }

    /**
     * Handling the firebase listview setup and intialzation
     */
    private fun setupFirebaseListView() {
        val uid: String?= PrefUtility().getFireBaseUid(this)

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth!!.getCurrentUser()
        if (mFirebaseUser == null || uid == "") {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        mProviderUid = uid //mFirebaseUser.getUid();
        val mLinearLayoutManager = LinearLayoutManager(this)

        homeBinding!!.messageRecyclerView.layoutManager = mLinearLayoutManager
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        mPath = "providers/$mProviderUid/active"

        setFirebaseAdapter()
    }

    /**
     * Query search handling
     *
     * @param query
     */
    private fun onQuerySearch(query: String) {
        //searchQueryStr = query;
        //filterList(providerHashMap.values());
        if (mAdapter != null) {
            mAdapter!!.getFilter()!!.filter(query)
        }
        if (TextUtils.isEmpty(query)) {
            if (selectedTab == TAB.Active.ordinal) {
                //Filter active screen by score
                filterByAcuityActive(acuityLevel)
            } else if (selectedTab == TAB.Pending.ordinal) {
                //Filtering by pending acuity
                filterByAcuityPending(acuityLevelPending)
            } else if (selectedTab == TAB.Patients.ordinal) {
                // Filtering by status type
                filterByStatusType(patientStatusType)
            }
        }
        //mAdapter.notifyDataSetChanged();

    }

    private fun onClickStatusButton() {
        val providerId: Long? = PrefUtility().getProviderId(this)
        var status: String? = PrefUtility().getProviderStatus(this)
        status = if (TextUtils.isEmpty(status) || status.equals(
                Constants.ProviderStatus.OffLine.toString(),
                ignoreCase = true
            )
        ) {
            Constants.ProviderStatus.Active.toString()
        } else {
            Constants.ProviderStatus.OffLine.toString()
        }
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(
                binding!!.container,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        drawerLayout!!.closeDrawer(GravityCompat.START, true)
        val provider = Provider()
        provider.setStatus(status)
        provider.setId(providerId)
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.updateProvider.toString()))
        viewModel!!.updateProviderStatus(provider)!!.observe(this, { commonResponse ->
//            Log.i(TAG, "onClickStatusButton: ");
            dismissProgressBar()
            if (commonResponse != null && commonResponse.status != null && commonResponse.status!!) {
                val currentStatus = commonResponse.getProvider()!!.getStatus()
                PrefUtility().saveStringInPref(
                    this@HomeActivity,
                    Constants.SharedPrefConstants.PROVIDER_STATUS,
                    currentStatus
                )

                CustomSnackBar.make(
                    binding!!.drawerLayout,
                    this,
                    CustomSnackBar.SUCCESS,
                    getString(R.string.status_updated_successfully),
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
                updateProviderStatus()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this@HomeActivity,
                    commonResponse!!.getErrorMessage(),
                    Constants.API.updateProvider
                )

                CustomSnackBar.make(
                    binding!!.drawerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
            }
        })
    }

    // Migrated to MyProfileActivity
    private fun updateProfileImage(provider: Provider, bitmap: Bitmap) {
        val progressBar: ProgressBar = navHeaderView!!.findViewById(R.id.id_profile_image_pb)
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(
                binding!!.container,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            progressBar.visibility = View.GONE
            return
        }
        viewModel!!.updateProviderStatus(provider)!!.observe(this, { commonResponse ->
//            Log.i(TAG, "onClickStatusButton: ");
            progressBar.visibility = View.GONE
            if (commonResponse?.status != null && commonResponse.status!!) {
                val imageView: ImageView = navHeaderView!!.findViewById(R.id.id_profile_img)
                imageView.visibility = View.VISIBLE
                findViewById<ImageView>(R.id.default_image_view).setVisibility(View.GONE)
                findViewById<ImageView>(R.id.cameraIcon).setVisibility(View.GONE)
                imageView.setImageBitmap(bitmap)
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this@HomeActivity,
                    commonResponse!!.getErrorMessage(),
                    Constants.API.updateProvider
                )

                CustomSnackBar.make(
                    binding!!.drawerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
            }
        })
    }

    /**
     * Firebase adapter setup and intialization
     */
    private fun setFirebaseAdapter() {

        // Adding the child path to firebase database reference
        messagesRef = mFirebaseDatabaseReference!!.child(mPath!!)
        // Added this line for checking the bug 1629
        messagesRef!!.keepSynced(true)
        query = messagesRef!!.orderByChild("time")
        // Data update and child add event listenere register
        query!!.addListenerForSingleValueEvent(valueEventListener)
        messagesRef!!.addChildEventListener(childEventListener)
        Handler().postDelayed({
            if (providerFilteredList.size <= 0) {
                filterList(providerFilteredList)
            }
            //                System.out.println("items list adapter " + providerFilteredList);
        }, 1000)
        mAdapter = PatientListAdapter(this, providerFilteredList)

        // Handling the search result listener
        mAdapter!!.setOnSearchResultListener(object : PatientListAdapter.OnSearchResultListener {
            override fun onSearchResult(count: Int) {
                if (count <= 0) {
                    if (selectedTab == TAB.Active.ordinal) {
                        homeBinding!!.noPatientLayout.visibility = View.VISIBLE
                        showEmptyMessageForActiveTab()
                    } else if (selectedTab == TAB.Pending.ordinal) {
                        homeBinding!!.noPatientLayout.visibility = View.VISIBLE
                        showEmptyMessageForPendingTab()
                    } else if (selectedTab == TAB.Patients.ordinal) {
                        homeBinding!!.noPatientLayout.visibility = View.VISIBLE
                        showEmptyMessageForCompleteTab()
                    } else {
                        //Handling the visibilty of the tab - Active, Pending, Complete
                        //Means there is no selected filter applied so resetted to default all section
                        handleListVisibility(false)
                    }
                } else {
                    homeBinding!!.noPatientLayout.visibility = View.GONE
                    homeBinding!!.filterLayout.visibility = View.VISIBLE
                    homeBinding!!.messageRecyclerView.visibility = View.VISIBLE
                    homeBinding!!.noPatientTitle.visibility = View.GONE
                }
            }
        })
        homeBinding!!.messageRecyclerView.adapter = mAdapter
        if (true) {
            return
        }
        val options = FirebaseRecyclerOptions.Builder<ConsultProvider>()
            .setQuery(query!!, ConsultProvider::class.java)
            .build()

        // Setting up the view
        mFirebaseAdapter = object :
            FirebaseRecyclerAdapter<ConsultProvider, ConsultListViewHolder>(options) {
                override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ConsultListViewHolder {

                    val inflater = LayoutInflater.from(viewGroup.context)
                    val itemBinding: ItemConsultListBinding = DataBindingUtil.inflate(inflater, R.layout.item_consult_list, viewGroup, false)

                    val viewHolder = ConsultListViewHolder(itemBinding)
                    viewHolder.setOnClickListeners()
                    return viewHolder
                }


            override fun onBindViewHolder(holder: ConsultListViewHolder, position: Int, model: ConsultProvider) {
                holder.bind(model, position, filterPatientStatus, searchQueryStr)
                setMessageCount(model)
            }
        }

        // Firebase recyclerview adapter observer handling
        mFirebaseAdapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, totalItemCount: Int) {
                super.onItemRangeInserted(positionStart, totalItemCount)

            }
        })
        homeBinding!!.messageRecyclerView.adapter = mFirebaseAdapter
    }

    /**
     * Filtering the provider data item based on datasnopshot and updating the adapter
     *
     * @param dataSnapshot
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun filterItem(dataSnapshot: DataSnapshot) {
        val key = dataSnapshot.key
        val consultProvider = providerHashMap[key]
        if (consultProvider != null) {
            providerHashMap[key!!] = dataSnapshot.getValue(ConsultProvider::class.java)!!
            if (consultProvider === dataSnapshot.getValue(ConsultProvider::class.java)) {
                val id = mAdapter!!.getExpandedPatientId()
                if (consultProvider.getPatientId() != null && id != null && consultProvider.getPatientId()!!
                        .equals(id)
                ) {
                    filterList(providerHashMap.values)
                    val index = mAdapter!!.getItemIndex(id)
                    mAdapter!!.notifyItemChanged(index)
                }
            } else {
                filterList(providerHashMap.values)
                mAdapter!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * Getting provider details by ID
     *
     * @param uid
     */
    private fun getProviderDetailsById(uid: Long) {
        val id: Long? = PrefUtility().getProviderId(this@HomeActivity)
        val token: String?= PrefUtility().getToken(this@HomeActivity)
        if (id != null) {
            viewModel!!.getProviderById(id, token, uid)?.observe(this@HomeActivity, {

                if (it?.status != null && it.status!! && it.getProvider() != null) {
                    val provider = it.getProvider()


                    PrefUtility().saveStringInPref(
                        this@HomeActivity,
                        Constants.SharedPrefConstants.HOSPITAL_NAME,
                        provider!!.getHospital()
                    )
                    val hospitalTxt: TextView = navHeaderView!!.findViewById(R.id.id_hospital_name)
                    hospitalTxt.text = provider!!.getHospital()
                } else {
                    val errMsg: String? = ErrorMessages().getErrorMessage(
                        this@HomeActivity,
                        it!!.getErrorMessage(),
                        Constants.API.getProviderById)

                    CustomSnackBar.make(
                        binding!!.container,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            })
        }
    }


    /**
     * Handling the filter list with consult provider data
     *
     * @param providers
     */
    private fun filterList(providers: Collection<ConsultProvider>?) {
        if (providers == null || providers.isEmpty()) {
            providerFilteredList.clear()
            //Handling the visibilty of the tab - Active, Pending, Complete
            //Means there is no selected filter applied so resetted to default all section
            handleListVisibility(false)
            homeBinding!!.badgeCount.visibility = View.GONE
            return
        }
        providerFilteredList.clear()
        var badgeCount = 0
        for (consultProvider in providers) {
            if (consultProvider == null) {
                continue
            }
            if (consultProvider.getPatientId() == null || TextUtils.isEmpty(consultProvider.getName())) {
                continue
            }
            if (consultProvider.getUnread() > 0 && consultProvider.getStatus() != null && consultProvider.getStatus() !== Constants.PatientStatus.Pending && consultProvider.getStatus() !== Constants.PatientStatus.Invited && consultProvider.getStatus() !== Constants.PatientStatus.Completed && consultProvider.getStatus() !== Constants.PatientStatus.Discharged) {
                badgeCount++
            }
            if (selectedTab == TAB.Active.ordinal) {
                var condition = false
                if (strDesignation.equals("md/do", ignoreCase = true)) {
                    condition =
                        consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff
                }
                if (consultProvider.getStatus() === Constants.PatientStatus.Completed || consultProvider.getStatus() === Constants.PatientStatus.Discharged || condition) {
                    continue
                }
                if (filterPatientStatus != null && consultProvider.getStatus() !== filterPatientStatus) {
                    if (filterPatientStatus!!.equals(Constants.PatientStatus.Active)
                        && (consultProvider.getStatus()!!.equals(Constants.PatientStatus.Active)
                                || consultProvider.getStatus()!!
                            .equals(Constants.PatientStatus.Patient)
                                || consultProvider.getStatus()!!
                            .equals(Constants.PatientStatus.HandoffPending)
                                )
                    ) {
                    } else {
                        continue
                    }
                }
            } else if (selectedTab == TAB.Patients.ordinal) {
                if (consultProvider.getStatus() === Constants.PatientStatus.Active || consultProvider.getStatus() === Constants.PatientStatus.Pending || consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff || consultProvider.getStatus() === Constants.PatientStatus.HomeCare || consultProvider.getStatus() === Constants.PatientStatus.HandoffPending || consultProvider.getStatus() === Constants.PatientStatus.Patient) {
                    continue
                }
            } else if (selectedTab == TAB.Pending.ordinal) {
                if (consultProvider.getStatus() === Constants.PatientStatus.Pending || consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff) {
                } else {
                    continue
                }
            }
            if (searchQueryStr != null) {
                var firstName = consultProvider.getFname()
                val lastName = consultProvider.getLname()
                if (!TextUtils.isEmpty(consultProvider.getName())) {
                    firstName = consultProvider.getName()
                }
                var isFound = false
                if (firstName != null && firstName.trim { it <= ' ' }.toLowerCase().contains(
                        searchQueryStr!!.toLowerCase()
                    )
                    || lastName != null && lastName.trim { it <= ' ' }.toLowerCase().contains(
                        searchQueryStr!!.toLowerCase()
                    )
                ) {
                    isFound = true
                }
                if (!isFound) {
                    continue
                }
            }
            providerFilteredList.add(consultProvider)
        }
        if (providerFilteredList.isEmpty()) {
            //Handling the visibilty of the tab - Active, Pending, Complete
            //Means there is no selected filter applied so resetted to default all section
            handleListVisibility(false)
            homeBinding!!.badgeCount.visibility = View.GONE
            return
        }
        //Handling the visibilty of the tab - Active, Pending, Complete
        //Means there is no selected filter applied so resetted to default all section
        handleListVisibility(true)
        //Sorting the provider list by time
        Collections.sort(providerFilteredList, PatientSortByTime())

        // To refresh patient list adapter
        if (mAdapter != null) {
            mAdapter!!.updateList(providerFilteredList.toMutableList())
        }

        // Badge count visibiltiy
        if (badgeCount > 0) {
            homeBinding!!.badgeCount.visibility = View.VISIBLE
            val badgeCountStr = badgeCount.toString()
            if (badgeCountStr.length == 1) {
                homeBinding!!.badgeCount.setTextSize(12F)
            } else if (badgeCountStr.length > 1) {
                homeBinding!!.badgeCount.setTextSize(10F)
            } else if (badgeCountStr.length > 2) {
                homeBinding!!.badgeCount.setTextSize(7F)
            }
            homeBinding!!.badgeCount.text = badgeCountStr
        } else {
            homeBinding!!.badgeCount.visibility = View.GONE
        }
    }

    /**
     * Handling the visibilty of the tab - Active, Pending, Complete
     * Means there is no selected filter applied so resetted to default all section
     *
     * @param isVisible
     */
    private fun handleListVisibility(isVisible: Boolean) {
        var page = TAB.values().get(selectedTab).toString()
        if (selectedTab == TAB.Active.ordinal) {
            page = "Active"
        } else if (selectedTab == TAB.Pending.ordinal) {
            page = "Pending"
        } else if (selectedTab == TAB.Patients.ordinal) {
            page = "Complete"
        }
        homeBinding!!.noPatientText.text = getString(R.string.no_patient_found_new, page)
        if (isVisible) {
            homeBinding!!.messageRecyclerView.visibility = View.VISIBLE
            homeBinding!!.filterLayout.visibility = View.VISIBLE
            homeBinding!!.noPatientLayout.visibility = View.GONE

        } else {
            homeBinding!!.messageRecyclerView.visibility = View.GONE
            homeBinding!!.noPatientLayout.visibility = View.VISIBLE
            homeBinding!!.noPatientTitle.visibility = View.GONE
            homeBinding!!.filterLayout.visibility = View.GONE
            if (TextUtils.isEmpty(searchQueryStr) && filterPatientStatus == null) {

            }
        }
    }


    /**
     * Add patient click listener - Triggering to add patient activity
     */
    private fun onClickAddPatient() {
        val intent = Intent(this, AddPatientActivity::class.java)
        startActivityForResult(intent, Constants.ActivityRequestCodes.ADD_PATIENT_REQ_CODE)
    }

    private fun onClickDirectory() {
//        drawerLayout.openDrawer(GravityCompat.END);
    }

    /**
     * Triggering Chat activity based on the consult provider
     *
     * @param consultProvider
     */
    fun startChat(consultProvider: ConsultProvider?) {
        if (consultProvider?.getPatientId() == null) {

            CustomSnackBar.make(
                homeBinding!!.root,
                this,
                CustomSnackBar.WARNING,
                "Patient does not exist",
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }


        val intent = Intent(baseContext, ChatActivity::class.java)
        intent.putExtra("uid", consultProvider.getPatientId())
        intent.putExtra("path", "consults/" + consultProvider.getPatientId())
        intent.putExtra("consultProviderId", "" + consultProvider.getId())
        intent.putExtra("consultProviderPatientId", "" + consultProvider.getPatientId())
        intent.putExtra("consultProviderText", consultProvider.getText())
        intent.putExtra("consultProviderName", consultProvider.getName())
        intent.putExtra("dob", consultProvider.getDob())
        intent.putExtra("gender", consultProvider.getGender())
        intent.putExtra("note", consultProvider.getNote())
        intent.putExtra("phone", consultProvider.getPhone())
        intent.putExtra("patientId", consultProvider.getPatientsId())
        intent.putExtra("teamNameConsult", "Team " + consultProvider.getTeamName())
        //        Log.d(TAG, "strConsultTeamName : " + "Team " + consultProvider.getTeamName());
        intent.putExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT, consultProvider.getUrgent())
        if (consultProvider.getStatus() != null) {
            intent.putExtra("status", consultProvider.getStatus().toString())
            if (consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff) {
                intent.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                clearNotifications(consultProvider.getPatientId())
            } else if (consultProvider.getStatus() === Constants.PatientStatus.Completed || consultProvider.getStatus() === Constants.PatientStatus.Discharged) {
                intent.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
            }
        }
        startActivity(intent)

    }

    /**
     * Accept click listener - Triggering the "acceptInvite" API call
     *
     * @param viewHolder
     * @param consultProvider
     */
    fun acceptClick(
        viewHolder: PatientListAdapter.ConsultListViewHolder,
        consultProvider: ConsultProvider
    ) {
        showProgressBar()
        if (consultProvider.getStatus() === Constants.PatientStatus.Invited) {

            val providerId: Long? = PrefUtility().getProviderId(this@HomeActivity)
            val token: String = PrefUtility().getStringInPref(
                this@HomeActivity,
                Constants.SharedPrefConstants.TOKEN,
                ""
            )
            providerId?.let { viewModel!!.acceptInvite(it, token, consultProvider.getPatientId()!!) }!!
                .observe(this@HomeActivity, { listResponse ->

                    dismissProgressBar()
                    if (listResponse != null && listResponse.status != null && listResponse.status!!) {

                        consultProvider.setStatus(Constants.PatientStatus.Active)
                        //Opening the chart page based on the status of the provider
                        openChartPage(consultProvider)
                    } else {
                        dismissProgressBar()
                        viewHolder.itemBinding.inviteBtn.isEnabled = true
                        val errMsg: String? = ErrorMessages().getErrorMessage(
                            this@HomeActivity,
                            listResponse!!.getErrorMessage(),
                            Constants.API.acceptInvite
                        )

                        CustomSnackBar.make(
                            homeBinding!!.idRootLayout,
                            this,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0
                        )?.show()
                    }
                })
        } else {
            val uid: Long = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, 0)
            val handOffAcceptRequest = HandOffAcceptRequest()
            handOffAcceptRequest.setPatientId(consultProvider.getPatientId())
            handOffAcceptRequest.setProviderId(uid)

            viewModel!!.acceptRemoteHandoff(handOffAcceptRequest)!!
                .observe(this@HomeActivity, { listResponse ->

                    dismissProgressBar()
                    if (listResponse != null && listResponse.status != null && listResponse.status!!) {
//                    mHandler.postDelayed(() -> scrollListToTop(), 500);
                        consultProvider.setStatus(Constants.PatientStatus.Active)
                        //Opening the chart page based on the status of the provider
                        openChartPage(consultProvider)
                    } else {
                        dismissProgressBar()
                        viewHolder.itemBinding.inviteBtn.isEnabled = true
                        val errMsg: String? = ErrorMessages().getErrorMessage(
                            this@HomeActivity,
                            listResponse!!.getErrorMessage(),
                            Constants.API.acceptInvite
                        )
                        //                    UtilityMethods.showErrorSnackBar(homeBinding.idRootLayout, errMsg, Snackbar.LENGTH_LONG);
                        CustomSnackBar.make(
                            homeBinding!!.idRootLayout,
                            this,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0
                        )?.show()
                    }
                })
        }
    }

    /**
     * Reconsult click handling - Triggres "inviteProviderBroadCast" Api call
     *
     * @param viewHolder
     * @param consultProvider
     */
    fun reconsultClick(
        viewHolder: PatientListAdapter.ConsultListViewHolder?,
        consultProvider: ConsultProvider
    ) {
        showProgressBar("Inviting provider please wait.")
        val providerId: Long? = PrefUtility().getProviderId(this)
        val token: String = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        val chatViewModel: ChatActivityViewModel = ViewModelProvider(this).get(ChatActivityViewModel::class.java)
        chatViewModel.inviteProviderBroadCast(providerId, token, consultProvider.getPatientId())
            ?.observe(this,{
                fun onChanged(commonResponse: CommonResponse?) {
                    dismissProgressBar()

                    if (it != null && it.status != null && it.status!!) {
                        Log.d(TAG, "Response Broadcast ")
                    } else {

                        val errMsg: String? = ErrorMessages().getErrorMessage(
                            this,
                            if (commonResponse != null) commonResponse.getErrorMessage() else null,
                            Constants.API.invite
                        )
                        Toast.makeText(this@HomeActivity, errMsg, Toast.LENGTH_SHORT).show()
                    }

                }
            })
    }

    /**
     * On Back pressed handling
     */
    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else if (drawerLayout!!.isDrawerOpen(GravityCompat.END)) {
            drawerLayout!!.closeDrawer(GravityCompat.END)
        } else if (intentCensus != null && intentCensus == Constants.IntentKeyConstants.SCREEN_CENSUS_START_CONSULT) {
            startActivity(Intent(this, MyDashboardActivity::class.java))
            finish()
        } else {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onStop() {
        super.onStop()
    }

    /**
     * onPause life cycle handling
     */
    override fun onPause() {
        super.onPause()
        listViewPos =
            (homeBinding!!.messageRecyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
        listViewPos = listViewPos + 1
        //mFirebaseAdapter.stopListening();
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * onDestroy life cycle handling
     */
    override fun onDestroy() {
        super.onDestroy()
        try {

        } catch (e: Exception) {

        }
        // Remove child event listener
        if (childEventListener != null) {
            messagesRef!!.removeEventListener(childEventListener)
        }

    }

    // Migrated to MyProfileActivity
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        drawerLayout!!.closeDrawers()
        when (id) {
            R.id.id_my_consults -> {
            }
            R.id.id_hand_off_patient -> {

//                Log.i(TAG, "On click Handoff Patient: ");
                val intent = Intent(this@HomeActivity, HandOffPatientsActivity::class.java)
                startActivity(intent)
            }
            R.id.id_contact_admin -> {

//                Log.i(TAG, "On click Contact admin: ");
                val intent = Intent(this@HomeActivity, ContactAdminActivity::class.java)
                startActivity(intent)
            }
            R.id.id_training_materials -> {

//                Log.i(TAG, "On click Training material ");
                /* Intent intent = new Intent(HomeActivity.this, TrainingMaterialActivity.class);
                startActivity(intent);*/
                val url: String = PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.TUTORIAL_URL,
                    ""
                )
                val uri = Uri.parse(url) // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            R.id.id_system_alert -> {

//                Log.i(TAG, "On click System alert: ");
                val intent = Intent(this@HomeActivity, SystemAlertActivity::class.java)
                startActivity(intent)
            }
            R.id.id_feedback -> {


                val uri = Uri.parse(strFeedbackForm) // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            R.id.id_change_password -> {

                val intent = Intent(this@HomeActivity, ResetPasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.id_logout -> {


                handleMultipleClick(item)
                onClickLogout()
            }
        }
        return false
    }

    /**
     * Logout click handling
     */
    private fun onClickLogout() {

        LogoutHelper(this, binding!!.root).doLogout()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.ActivityRequestCodes.ADD_PATIENT_REQ_CODE -> {

                    //Scroll to top of the list
                    scrollListToTop()
                }
                Constants.ActivityRequestCodes.FILTER_REQ_CODE -> {
                    val status = data?.getStringExtra("status")
                    var statusHomeCare = ""
                    if (status != null && status.equals("Assigned", ignoreCase = true)) {
                        statusHomeCare = "HomeCare"
                    }
                    var save: String? = null
                    if (data != null) {
                        save = data.getStringExtra("save")
                    }
                    //                    Log.d(TAG, "onActivityResult: status " + status);
                    if ("true".equals(save, ignoreCase = true)) {
                        if ("Active".equals(status, ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.Active
                            //                            homeBinding.filterView.setText(R.string.active_in_caps);
                        } else if ("Pending".equals(status, ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.Pending
                            if (PrefUtility().getRole(this)
                                    .equals(Constants.ProviderRole.RD.toString(),ignoreCase = true)
                            ) {
                                filterPatientStatus = Constants.PatientStatus.Invited
                            }
                            //                            homeBinding.filterView.setText(R.string.pending_in_caps);
//                            homeBinding.filterView.setText(R.string.pending_in_caps);
                        } else if ("HomeCare".equals(statusHomeCare, ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.HomeCare
                            //                            homeBinding.filterView.setText(getString(R.string.assigned).toUpperCase());
                        } else if (getString(R.string.discharged).equals(status,ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.Completed
                            //                            homeBinding.filterView.setText(getString(R.string.discharged).toUpperCase());
                        } else if ("Patients".equals(status, ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.Patient
                            //                            homeBinding.filterView.setText(R.string.patients_in_caps);
                        } else {
                            filterPatientStatus = null
                            //                            homeBinding.filterView.setText(R.string.filter_in_caps);
                        }
                        filterList(providerHashMap.values)
                        mAdapter!!.notifyDataSetChanged()

                    }
                }
                Constants.ImageCaptureConstants.START_CAMERA_REQUEST_CODE -> {
                    try {
                        //Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        var uri: Uri? = null
                        if (data != null) {
                            uri =
                                data.extras!!.getParcelable(Constants.ImageCaptureConstants.SCANNED_RESULT)
                            var imageBitmap = getBitmapFromUri(uri)
                            imageBitmap = getResizedBitmap(imageBitmap!!, Constants.IMAGE_MAX_SIZE)
                            imageBitmap = getRotatedBitmap(uri!!.path, imageBitmap)
                            //Making square image
                            val dimension = Math.min(imageBitmap!!.width, imageBitmap.height)
                            imageBitmap =
                                ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension)
                            storeImgInFirebase(imageBitmap)
                        }
                    } catch (e: Exception) {

                    }
                }
                Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE -> {
                    try {
                        val uri = data?.data
                        var imageBitmap = getBitmapFromUri(uri)
                        imageBitmap = getResizedBitmap(imageBitmap!!, Constants.IMAGE_MAX_SIZE)
                        //imageBitmap = getRotatedBitmap(uri.getPath(),imageBitmap);
                        //Making square image
                        val dimension = imageBitmap?.width?.let { imageBitmap?.height?.let { it1 ->
                            Math.min(it,
                                it1
                            )
                        } }
                        imageBitmap =
                            dimension?.let {
                                ThumbnailUtils.extractThumbnail(imageBitmap,
                                    it, dimension)
                            }
                        if (imageBitmap != null) {
                            storeImgInFirebase(imageBitmap)
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    /**
     * This method will gets called 2 times
     * first when user comes in home screen we will fetch data in background
     * second once user click on directory navigation drawer we will show the directory which is already
     * loaded ,and fetch directory in background and refresh the directory
     */
    private fun fetchDirectory() {
        DirectoryListHelperOld(binding, viewModel, object : DirectoryListHelper.CallbackDirectory {


            override fun onClickProvierItem(provider: Provider?) {
                showProgressBar()

                val providerID: Long?= PrefUtility().getProviderId(this@HomeActivity)
                val token: String = PrefUtility().getStringInPref(
                    this@HomeActivity,
                    Constants.SharedPrefConstants.TOKEN,
                    "")
                providerID?.let {
                    viewModel!!.startCall(
                        it,
                        token,
                        provider?.getId()!!,
                        0L,
                        Constants.FCMMessageType.VIDEO_CALL)
                }!!
                    .observe(this@HomeActivity, { commonResponse ->
                        dismissProgressBar()
                        if (commonResponse?.status != null && commonResponse.status!!) {

                            val callScreen = Intent(this@HomeActivity, CallActivity::class.java)
                            callScreen.putExtra("providerName", provider?.getName())
                            callScreen.putExtra("providerId", provider?.getId())
                            callScreen.putExtra("providerHospitalName", provider?.getHospital())
                            callScreen.putExtra("profilePicUrl", provider?.getProfilePicUrl())
                            callScreen.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME, providerID.toString() + "-" + provider?.getId())
                            callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                            callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                                resources.getStringArray(R.array.encryption_mode_values)[0])
                            callScreen.putExtra("callType", "outgoing")
                            val gson = Gson()
                            val providerList: MutableList<Provider> = ArrayList()
                            if (provider != null) {
                                providerList.add(provider)
                            }
                            val selfProvider = Provider()
                            selfProvider.setId(providerID)
                            selfProvider.setName(
                                PrefUtility().getStringInPref(
                                    this@HomeActivity,
                                    Constants.SharedPrefConstants.NAME,
                                    ""))
                            selfProvider.setProfilePicUrl(
                                PrefUtility().getStringInPref(
                                    this@HomeActivity,
                                    Constants.SharedPrefConstants.PROFILE_IMG_URL,
                                    ""))
                            selfProvider.setHospital(
                                PrefUtility().getStringInPref(
                                    this@HomeActivity,
                                    Constants.SharedPrefConstants.HOSPITAL_NAME,
                                    ""))
                            selfProvider.setRole(
                                PrefUtility().getStringInPref(
                                    this@HomeActivity,
                                    Constants.SharedPrefConstants.ROLE,
                                    ""
                                )
                            )
                            providerList.add(selfProvider)
                            callScreen.putExtra("providerList", gson.toJson(providerList))
                            startActivity(callScreen)
                        } else {
                            val errMsg: String? = ErrorMessages().getErrorMessage(
                                this@HomeActivity,
                                commonResponse!!.getErrorMessage(),
                                Constants.API.startCall
                            )

                            CustomSnackBar.make(
                                containerParent,
                                this@HomeActivity,
                                CustomSnackBar.WARNING,
                                errMsg,
                                CustomSnackBar.TOP,
                                3000,
                                0
                            )?.show()
                        }
                    })
                TODO("Not yet implemented")
            }
        })
    }

    /**
     * Updating the provider status
     */
    fun updateProviderStatus() {

        val dotImg: ImageView = navHeaderView!!.findViewById(R.id.id_dot)
        val status: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.PROVIDER_STATUS, "")
        if (TextUtils.isEmpty(status) || status.equals(
                Constants.ProviderStatus.OffLine.toString(),
                ignoreCase = true)
        ) {
            //statusBtn.setText(R.string.go_online);
            dotImg.setImageResource(R.drawable.ic_grey_circle)
            homeBinding!!.idDot.setImageResource(R.drawable.ic_grey_circle)
        } else {
            //statusBtn.setText(R.string.go_offline);
            dotImg.setImageResource(R.drawable.ic_green_circle)
            homeBinding!!.idDot.setImageResource(R.drawable.ic_green_circle)
        }
    }

    // Migrated to MyProfileActivity
    private fun selectImage() {
        val items =
            arrayOf<CharSequence>(getString(R.string.take_photo), getString(R.string.select_image))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.add_edit_image)
        builder.setItems(items) { dialog: DialogInterface?, item: Int ->
            val intent = Intent(
                this@HomeActivity,
                ImageCaptureActivity::class.java
            )
            if (items[item] == getString(R.string.take_photo)) {

                val cameraPermission =
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                val storagePermission = ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (cameraPermission == PackageManager.PERMISSION_DENIED || storagePermission == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        Constants.PermissionCondes.CAMERA_STORAGE_PERMISSION_CODE
                    )
                    return@setItems
                }
                intent.putExtra(
                    Constants.ImageCaptureConstants.SOURCE,
                    HomeActivity::class.java.simpleName
                )
                intent.putExtra(
                    Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                    Constants.ImageCaptureConstants.OPEN_CAMERA
                )
                startActivityForResult(
                    intent,
                    Constants.ImageCaptureConstants.START_CAMERA_REQUEST_CODE
                )
            } else if (items[item] == getString(R.string.select_image)) {
                intent.putExtra(
                    Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                    Constants.ImageCaptureConstants.OPEN_MEDIA
                )
                startActivityForResult(
                    intent,
                    Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE
                )
            }
        }
        builder.show()
    }

    // Migrated to MyProfileActivity
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        try {
            parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            return BitmapFactory.decodeFileDescriptor(fileDescriptor)
        } catch (e: FileNotFoundException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } finally {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close()
                } catch (e: IOException) {
//                    Log.e(TAG, "Exception:", e.getCause());
                }
            }
        }
        return null
    }

    // Migrated to MyProfileActivity
    fun getRotatedBitmap(photoPath: String?, bitmap: Bitmap): Bitmap? {
        var rotatedBitmap: Bitmap? = null
        try {
            val ei = ExifInterface(photoPath!!)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            rotatedBitmap =
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> bitmap
                }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return rotatedBitmap ?: bitmap
    }

    // Migrated to MyProfileActivity
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    // Migrated to MyProfileActivity
    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        return try {
            var width = image.width
            var height = image.height
            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 0) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            Bitmap.createScaledBitmap(image, width, height, true)
        } catch (e: Exception) {
            //            Log.e(TAG, "Exception:", e.getCause());
            image
        }
    }

    // Migrated to MyProfileActivity
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.PermissionCondes.CAMERA_STORAGE_PERMISSION_CODE -> {
                val isGranted: Boolean = UtilityMethods().checkPermission(this, permissions)
                if (isGranted) {
                    val intent = Intent(this@HomeActivity, ImageCaptureActivity::class.java)
                    intent.putExtra(
                        Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                        Constants.ImageCaptureConstants.OPEN_CAMERA
                    )
                    startActivityForResult(intent, Constants.ActivityRequestCodes.IMAGE_REQ_CODE)
                } else {
                    val permissonErr = getString(R.string.permission_denied)
                    //                    UtilityMethods.showErrorSnackBar(binding.container, permissonErr, Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(
                        binding!!.container,
                        this@HomeActivity,
                        CustomSnackBar.WARNING,
                        permissonErr,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )!!.show()
                }
            }
        }
    }

    // Migrated to MyProfileActivity
    private fun storeImgInFirebase(imageBitmap: Bitmap) {
        val progressBar: ProgressBar = navHeaderView!!.findViewById(R.id.id_profile_image_pb)
        progressBar.visibility = View.VISIBLE
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val dataArr = baos.toByteArray()
        val fileName = "image_" + System.currentTimeMillis()
        val dir = Constants.PROFILE_IMG_DIR + File.separator + fileName
        val key: String = PrefUtility().getFireBaseUid(this@HomeActivity).toString() + ""
        val storageReference = FirebaseStorage.getInstance()
            .getReference(mFirebaseUser!!.uid)
            .child(key)
            .child(dir)
        //putProfileImageInStorage(storageReference,dataArr,key,fileName,"image");
        storageReference.putBytes(dataArr).addOnCompleteListener(
            this@HomeActivity
        ) { task ->
            if (task.isSuccessful) {
                task.result!!.metadata!!.reference!!.downloadUrl
                    .addOnCompleteListener(
                        this@HomeActivity
                    ) { task ->
                        if (task.isSuccessful) {
                            val imageURL = task.result.toString()
                            PrefUtility().saveStringInPref(
                                this@HomeActivity,
                                Constants.SharedPrefConstants.PROFILE_IMG_URL,
                                imageURL
                            )
                            val provider = Provider()
                            val providerId: Long?= PrefUtility().getProviderId(this@HomeActivity)
                            val status: String? = PrefUtility().getProviderStatus(this@HomeActivity)
                            provider.setId(providerId)
                            provider.setStatus(status)
                            provider.setProfilePicUrl(imageURL)
                            updateProfileImage(provider, imageBitmap)
                            //                                                        Log.i(TAG, "onComplete - imageURL : " + imageURL);
                        }
                    }
            } else {

            }
        }
    }

    /**
     * Handling the notification click based on the intent
     *
     * @param intent
     */
    private fun handleNotification(intent: Intent) {
        var intent = intent
        val userId: Long? = PrefUtility().getProviderId(this)
        // To login activity if user is null / -1
        if (userId == null || userId == -1L) {
            intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } // If the intent has "forward" - redirect to chat activity
        else if (intent.hasExtra("forward")
            && intent.getStringExtra("forward")
                .equals(ChatActivity::class.java.simpleName, ignoreCase = true)
        ) {
            val chatActIntent = Intent(this, ChatActivity::class.java)
            chatActIntent.putExtras(intent.extras!!)
            startActivity(chatActIntent)
        }
        // Getting the bundle data
        val extras = intent.extras
        if (extras != null) {
            val select = extras.getString(Constants.IntentKeyConstants.TARGET_PAGE, "")
            if (select == "pending") {
                getnewpendingchange()
                if (homeBinding!!.pendingBtnLayout.visibility === View.VISIBLE) {
                    homeBinding!!.pendingBtnLayout.performClick()
                }
            } else if (select == "completed") {
                homeBinding!!.completedBtnLayout.performClick()
            } else if (select == "active") {
                homeBinding!!.activeBtnLayout.performClick()
            }
        }
    }

    /**
     * Clearing then notification with dedicated notification ID
     *
     * @param notificationId
     */
    private fun clearNotifications(notificationId: Int) {
        NotificationHelper(this@HomeActivity).clearNotification(notificationId.toLong())
    }

    /**
     * Clearing all notfications
     */
    private fun clearAllNotifications() {
        NotificationHelper(this@HomeActivity).clearAllNotification()
    }

    /**
     * Handling multiple click event with view
     *
     * @param view
     */
    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    /**
     * Handling multiple click event with menu item
     *
     * @param item
     */
    private fun handleMultipleClick(item: MenuItem) {
        item.isEnabled = false
        mHandler?.postDelayed({ item.setEnabled(true) }, 500)
    }

    /**
     * Scroll to top of the list
     */
    private fun scrollListToTop() {

        if (homeBinding!!.messageRecyclerView != null && mAdapter!!.itemCount >= 0) {
            homeBinding!!.messageRecyclerView.scrollToPosition(0)
        }
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * Handling tab click listener
     */
    private fun handleTabButtons() {
        homeBinding!!.activeBtnLayout.setOnClickListener(tabChangeListener)
        homeBinding!!.completedBtnLayout.setOnClickListener(tabChangeListener)
        homeBinding!!.pendingBtnLayout.setOnClickListener(tabChangeListener)
    }

    /**
     * Chat view click listener
     *
     * @param position
     * @param provider
     */
    override fun onClickChatView(position: Int, provider: ConsultProvider?) {

        openChartPage(provider)
    }
    /**
     * Invite button click listener
     *
     * @param viewHolder
     */


    override fun onClickInviteBtn(viewHolder: PatientListAdapter.ConsultListViewHolder?) {
        viewHolder?.itemBinding?.inviteBtn?.isEnabled = false
        if (viewHolder != null) {
            acceptClick(viewHolder, viewHolder?.let { mAdapter!!.getItem(it?.adapterPosition) }!!)
        }
        clearNotifications(Constants.NotificationIds.NOTIFICATION_ID)
    }
    /**
     * Details button click listener
     *
     * @param viewHolder
     */

    override fun onClickDetailsButton(viewHolder: PatientListAdapter.ConsultListViewHolder?) {
        viewHolder?.itemBinding?.detailsBtn?.let { handleMultipleClick(it) }
        val provider = viewHolder?.adapterPosition?.let { mAdapter!!.getItem(it) }
        //Opening the chart page based on the status of the provider
        openChartPage(provider)
    }
    /**
     * Reconsult button click listener
     *
     * @param viewHolder
     */

    override fun onClickReconsultButton(viewHolder: PatientListAdapter.ConsultListViewHolder?) {
        viewHolder?.itemBinding?.reconsultBtn?.let { handleMultipleClick(it) }

        reconsultClick(viewHolder, viewHolder?.adapterPosition?.let { mAdapter!!.getItem(it) }!!)
    }

    /**
     * Arrow drop down click listener
     *
     * @param position
     */
    override fun onArrowDropDownClick(position: Int) {
        consultDetailsDialog(this, mAdapter!!.getItem(position)!!)
    }

    /**
     * Reset acuity click listener
     *
     * @param position
     */
    override fun onResetAcuityClick(position: Int) {
//        Log.i(TAG, "onResetAcuityClick: ");
        showAcuityResetConfirmDialog(position)
    }

    /**
     * Opening the chart page based on the status of the provider
     *
     * @param provider
     */
    private fun openChartPage(provider: ConsultProvider?) {
        var strCompletedByName: String? = ""
        if (provider!!.getCompleted_by() != null && !TextUtils.isEmpty(provider.getCompleted_by())) {
            strCompletedByName = provider.getCompleted_by()
        }
        val intentConsultChart: Intent
        intentConsultChart = if (provider.getStatus() === Constants.PatientStatus.Completed ||
            provider.getStatus() === Constants.PatientStatus.Discharged
        ) {
            Intent(this@HomeActivity, ActivityConsultChartRemote::class.java)
        } else {
            Intent(this@HomeActivity, ActivityConsultChart::class.java)
        }
        intentConsultChart.putExtra("uid", provider.getPatientId())
        intentConsultChart.putExtra("providerNameType", strCompletedByName)
        intentConsultChart.putExtra("completedTime", java.lang.String.valueOf(provider.getTime()))
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
                clearNotifications(provider.getPatientId())
            } else if (provider.getStatus() === Constants.PatientStatus.Completed ||
                provider.getStatus() === Constants.PatientStatus.Discharged
            ) {
                intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
            }
        }
        startActivity(intentConsultChart)
    }

    /**
     * Displaying the acuity reset confirmation dialog
     *
     * @param position
     */
    private fun showAcuityResetConfirmDialog(position: Int) {
        val isDestroyed = AtomicBoolean(false)
        // On clicking the "Ok" - Triggering the "resetAcuityScore" API call
        val okBtnClickListener = View.OnClickListener {
            if (!UtilityMethods().isInternetConnected(this@HomeActivity)!!) {
                Toast.makeText(this@HomeActivity, getString(R.string.no_internet_connectivity), Toast.LENGTH_SHORT)
                return@OnClickListener
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed.set(isDestroyed())
            }
            if (resetAcuityDialog != null && resetAcuityDialog!!.isShowing() && !isDestroyed.get() && !isFinishing) {
                resetAcuityDialog!!.dismiss()
                resetAcuityDialog = null
            }
            provider = mAdapter!!.getItem(position)

            val providerId: Long? = PrefUtility().getProviderId(this@HomeActivity)
            val token: String? = PrefUtility().getToken(this@HomeActivity)
            val patientId =
                if (provider!!.getPatientId() != null) provider!!.getPatientId()!! else 0
            showProgressBar(getString(R.string.acuity_score_reset_pb_mgs))

            if (providerId != null) {
                if (token != null) {
                    viewModel!!.resetAcuityScore(providerId, token, patientId)?.observe(this@HomeActivity,
                        {
                            dismissProgressBar()
                            if (it?.status != null && it.getStatus()!!) {
                                val consultProvider = providerFilteredList[position]
                                consultProvider.setResetAcuityFlag(true)
                               // providerFilteredList.set(position, consultProvider)
                                mAdapter!!.notifyItemChanged(position)
                            } else {
                                var errMsg: String? = null
                                if (it != null) {
                                    errMsg = ErrorMessages().getErrorMessage(
                                        this@HomeActivity,
                                        it.getErrorMessage(),
                                        Constants.API.resetAcuityScore)
                                }

                                CustomSnackBar.make(
                                    homeBinding!!.root,
                                    this@HomeActivity,
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
            }
        }
        // Cancel click listener
        val cancelBtnClickListener = View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed.set(isDestroyed())
            }
            if (resetAcuityDialog != null && resetAcuityDialog!!.isShowing() && !isDestroyed.get() && !isFinishing) {
                resetAcuityDialog!!.dismiss()
                resetAcuityDialog = null
            }
        }
        val message = getString(R.string.reset_acuity_score_confirm_msg)
        resetAcuityDialog = UtilityMethods().showDialog(
            this, getString(R.string.confirmation), message, true,
            R.string.ok, okBtnClickListener, R.string.cancel, cancelBtnClickListener, -1, true
        )
        if ((resetAcuityDialog == null || !resetAcuityDialog!!.isShowing()) && !isFinishing && !isDestroyed.get()) {
            resetAcuityDialog!!.show()
        }
    }

    /**
     * Setting up the message count
     *
     * @param consultProvider
     */
    private fun setMessageCount(consultProvider: ConsultProvider?) {
        var messageCount = 0
        //        Log.d(TAG, "setMessageCount :  " + consultProvider);
        if (consultProvider != null && consultProvider.getUnread() !== 0) {
            messageCount = messageCount + consultProvider.getUnread()
        }
        homeBinding!!.badgeCount.text = messageCount.toString()
    }



    class ImageLoader internal constructor(activity: HomeActivity, imageURL: String) :
        AsyncTask<Void?, Void?, Bitmap?>() {
        var activityReference: WeakReference<HomeActivity>
        var imageURL: String
        var imageProgressBar: ProgressBar
        var imageView: ImageView
        var cameraIcon: ImageView
        var defaultImgView: TextView
        override fun onPreExecute() {
            super.onPreExecute()
            imageProgressBar.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            defaultImgView.visibility = View.VISIBLE
            cameraIcon.visibility = View.VISIBLE
            val name: String? = activityReference.get()?.let {
                PrefUtility().getStringInPref(
                    it,
                    Constants.SharedPrefConstants.NAME,
                    "")
            }
            defaultImgView.setText(name?.let { UtilityMethods().getNameText(it) })
        }


        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            val homeActivity = activityReference.get()
            if (homeActivity != null) {
                imageProgressBar.visibility = View.GONE
                if (bitmap != null) {
                    imageView.visibility = View.VISIBLE
                    defaultImgView.visibility = View.GONE
                    cameraIcon.visibility = View.GONE
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.visibility = View.GONE
                    defaultImgView.visibility = View.VISIBLE
                    cameraIcon.visibility = View.VISIBLE
                    val name: String = PrefUtility().getStringInPref(
                        homeActivity,
                        Constants.SharedPrefConstants.NAME,
                        ""
                    )
                    defaultImgView.setText(UtilityMethods().getNameText(name))
                }
            }
        }

        override fun onCancelled() {
            super.onCancelled()
        }

        init {
            activityReference = WeakReference(activity)
            this.imageURL = imageURL
            imageView = activityReference.get()!!.navHeaderView!!.findViewById(R.id.id_profile_img)
            cameraIcon = activityReference.get()!!.navHeaderView!!.findViewById(R.id.cameraIcon)
            imageProgressBar =
                activityReference.get()!!.navHeaderView!!.findViewById(R.id.id_profile_image_pb)
            defaultImgView =
                activityReference.get()!!.navHeaderView!!.findViewById(R.id.default_image_view)
        }

        override fun doInBackground(vararg params: Void?): Bitmap? {
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
    }

    /**
     * Sorting the patient by time using comparator
     */
  class PatientSortByTime : Comparator<ConsultProvider?> {
        override fun compare(
            consultProvider1: ConsultProvider?,
            consultProvider2: ConsultProvider?
        ): Int {
            return if (consultProvider2 == null || consultProvider1 == null || consultProvider2.getTime() == null || consultProvider1.getTime() == null) {
                Int.MIN_VALUE
            } else consultProvider2.getTime()!!.compareTo(consultProvider1.getTime()!!)

        }
    }


}