package com.example.kotlinomnicure.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
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
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener


import com.example.kotlinomnicure.viewmodel.ChatActivityViewModel
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.*
import com.example.kotlinomnicure.adapter.LocationListAdapter
import com.example.kotlinomnicure.adapter.MembersDialogAdapter
import com.example.kotlinomnicure.adapter.PatientListAdapter
import com.example.kotlinomnicure.customview.CircularImageView
import com.example.kotlinomnicure.customview.CustomDialog
import com.example.kotlinomnicure.databinding.ActivityHomeBinding
import com.example.kotlinomnicure.databinding.ItemConsultListBinding
import com.example.kotlinomnicure.helper.DirectoryListHelperOld
import com.example.kotlinomnicure.helper.LogoutHelper
import com.example.kotlinomnicure.helper.NotificationHelper
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.interfaces.OnListItemClickListener
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewholder.ConsultListViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.HandOffAcceptRequest
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
import kotlin.collections.ArrayList

class HomeActivity : DrawerActivity(),BaseActivity(),NavigationView.OnNavigationItemSelectedListener,
    OnListItemClickListener {
    var expandedPosition = -1
    private var containerParent: FrameLayout? = null
    private var listViewPos = -1
    private val providerHashMap: LinkedHashMap<String?, ConsultProvider?> =
        LinkedHashMap<String?, ConsultProvider?>()
    private val providerFilteredList: MutableList<ConsultProvider?>? = ArrayList<ConsultProvider?>()
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
    private var mFirebaseAdapter: FirebaseRecyclerAdapter<ConsultProvider?, ConsultListViewHolder>? =
        null
    private var mProviderUid: String? = null
    private var mPath: String? = null
    private var messagesRef: DatabaseReference? = null
    private var query: Query? = null
    var homeBinding: ActivityHomeBinding? = null
    private var searchQueryStr: String? = null
    private var filterPatientStatus: Constants.PatientStatus? = null
    private var acuityLevel: Constants.AcuityLevel = Constants.AcuityLevel.NA
    private var acuityLevelPending: Constants.AcuityLevel = Constants.AcuityLevel.NA
    private var patientStatusType: Constants.PatientStatus = Constants.PatientStatus.NA
    private var eConsultStatus: Constants.PatientStatus = Constants.PatientStatus.NA
    private var eConsultTime: Constants.ConsultTime = Constants.ConsultTime.NA
    private var urgencyLevelType: Constants.UrgencyLevel = Constants.UrgencyLevel.NA

    private var role: String? = null
    private val customDialog: CustomDialog? = null
    private val parser: SnapshotParser<ConsultProvider>? = null
    private var itemCount: Long = -1
    private var strFeedbackForm = ""
    var context:Context?=HomeActivity(0)
    var activity:Activity=null

    /**
     * Getting the current user
     *
     * @return
     */
    private var currentUser: Provider? = null
        get() {
            if (field == null) {
                field = PrefUtility().getProviderObject(this)
            }
            return field
        }
    private var provider: ConsultProvider? = null
    private var locationListAdapter: LocationListAdapter? = null
    private val ALL_HOSPITAL_ID = UUID.randomUUID().hashCode().toLong()
    private var selectedHospitalId = ALL_HOSPITAL_ID
     var selectedTab =TAB.Active
    var mAdapter: PatientListAdapter? = null
    private var uid: String? = null
    var intentCensus: String? = ""

    //  Event listener data snap shot for Active, Pending, Completed patients data updation based on Roles
    var valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {


            val key = dataSnapshot.key
            if (dataSnapshot.value != null && !dataSnapshot.value.toString()
                    .equals("", ignoreCase = true)) {
                val consultProvider: ConsultProvider? =
                    dataSnapshot.getValue(ConsultProvider::class.java)
                providerHashMap[key] = consultProvider

                if (itemCount <= providerHashMap.size) {
                    itemCount = providerHashMap.size.toLong()
                    filterList(providerHashMap.values)
                    mAdapter?.notifyDataSetChanged()
                }
            }
            if (selectedTab == TAB.Active) {
                //Filter active screen by score
                filterByAcuityActive(acuityLevel)
            } else if (selectedTab == TAB.Pending) {
                //Filtering by pending acuity
                filterByAcuityPending(acuityLevelPending)
            } else if (selectedTab == TAB.Patients) {
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

    private fun filterList(providers: MutableCollection<ConsultProvider?>) {
        if (providers == null || providers.isEmpty()) {
            providerFilteredList!!.clear()
            //Handling the visibilty of the tab - Active, Pending, Complete
            //Means there is no selected filter applied so resetted to default all section
            handleListVisibility(false)
            homeBinding!!.badgeCount.visibility = View.GONE
            return
        }
        providerFilteredList!!.clear()
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
            if (selectedTab.equals(TAB.Active)) {
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
                            .equals(Constants.PatientStatus.HandoffPending) //                            || consultProvider.getStatus().equals(Constants.PatientStatus.Handoff)
                                )
                    ) {
                    } else {
                        continue
                    }
                }
            } else if (selectedTab == TAB.Patients) {
                if (consultProvider.getStatus() === Constants.PatientStatus.Active || consultProvider.getStatus() === Constants.PatientStatus.Pending || consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff || consultProvider.getStatus() === Constants.PatientStatus.HomeCare || consultProvider.getStatus() === Constants.PatientStatus.HandoffPending || consultProvider.getStatus() === Constants.PatientStatus.Patient) {
                    continue
                }
            } else if (selectedTab == TAB.Pending) {
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
                if (firstName != null && firstName.trim { it <= ' ' }.toLowerCase()
                        .contains(searchQueryStr!!.toLowerCase())
                    || lastName != null && lastName.trim { it <= ' ' }.toLowerCase()
                        .contains(searchQueryStr!!.toLowerCase())
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
        //Handling the visibilty of the tab - Active, Pending, Complete
        //Means there is no selected filter applied so resetted to default all section
        handleListVisibility(true)
        //Sorting the provider list by time
        //Sorting the provider list by time
        Collections.sort(providerFilteredList, PatientSortByTime())

        // To refresh patient list adapter

        // To refresh patient list adapter
        if (mAdapter != null) {
            mAdapter!!.updateList(providerFilteredList!!)
        }

        // Badge count visibiltiy

        // Badge count visibiltiy
        if (badgeCount > 0) {
            homeBinding!!.badgeCount.visibility = View.VISIBLE
            val badgeCountStr = badgeCount.toString()
            if (badgeCountStr.length == 1) {
                homeBinding!!.badgeCount.setTextSize(12)
            } else if (badgeCountStr.length > 1) {
                homeBinding!!.badgeCount.setTextSize(10)
            } else if (badgeCountStr.length > 2) {
                homeBinding!!.badgeCount.setTextSize(7)
            }
            homeBinding!!.badgeCount.text = badgeCountStr
        } else {
            homeBinding!!.badgeCount.visibility = View.GONE
        }
    }

    class PatientSortByTime : Comparator<ConsultProvider?> {
        override fun compare(consultProvider1: ConsultProvider?, consultProvider2: ConsultProvider?): Int {
            return if (consultProvider2 == null || consultProvider1 == null || consultProvider2.getTime() == null || consultProvider1.getTime() == null) {
                Int.MIN_VALUE
            } else consultProvider2.getTime()!!.compareTo(consultProvider1.getTime()!!)
        }

    }





    fun getnewpendingchange() {
        // new pending activity status change
        uid = PrefUtility().getFireBaseUid(this)
        val mProviderUid1 = uid.toString()
        val mPathnewpendng = "providers/$mProviderUid1/newPendingRequest"
        mFirebaseDatabaseReference!!.child("providers").child(mProviderUid1)
            .child("newPendingRequest").setValue(false)

    }

    /**
     * Tab change/ click listener handling
     */
    var tabChangeListener =
        View.OnClickListener { view ->
            homeBinding?.acuity?.setText(getString(R.string.acuity))
            when (view.id) {
                R.id.activeBtnLayout -> {
                    if (selectedTab == TAB.Active) {
                        return@OnClickListener
                    }
                    homeBinding?.activetab?.setVisibility(View.VISIBLE)
                    homeBinding?.pendingtab?.setVisibility(View.GONE)
                    homeBinding?.complettab?.setVisibility(View.GONE)


                    // Acuity filter click listener
                    homeBinding?.acuityFilterView?.setOnClickListener(View.OnClickListener {
                        //Handling multiple click event with view
                        handleMultipleClick(homeBinding?.acuityFilterView!!)
                        //Showing the dialog with Filter by acuity
                        FilterByAcuityDialog(this)
                    })
                    homeBinding?.txtClearactive?.setOnClickListener(View.OnClickListener { v ->
                        handleMultipleClick(v)

                        filterdeselectactive()
                    })

                    // Filter view click listener
                    homeBinding?.filterViewactive?.setOnClickListener(View.OnClickListener {
                        //Handling multiple click event with view
                        handleMultipleClick(homeBinding!!.filterViewactive)
                        if (role != null && role.equals(Constants.ProviderRole.BD.toString(),
                                ignoreCase = true)) {
                            // If the role is BP - Display Filter dialog based on -  Urgency, Status
                            FilterByDialog(this)
                        } else {
                            // If the role is RP- Display Filter dialog based on -  Urgency, Status
                            FilterByRpDialog(this)
                        }
                    })
                    // Active buttn handling
                    homeBinding?.idBtnActive?.setTextColor(getResources().getColor(R.color.colorPrimary))
                    homeBinding?.idBtnActive?.setBackground(getResources().getDrawable(R.drawable.tab_selected_rounded))
                    homeBinding?.idBtnComplete?.setTextColor(getResources().getColor(R.color.white))
                    homeBinding?.idBtnComplete?.setBackground(getResources().getDrawable(R.drawable.transparent_bg))
                    homeBinding?.idBtnPending?.setTextColor(getResources().getColor(R.color.white))
                    homeBinding?.idBtnPending?.setBackground(getResources().getDrawable(R.drawable.transparent_bg))
                   
                    if (role != null && role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
                        homeBinding?.fab?.setVisibility(View.VISIBLE)
                    } else {
                        homeBinding?.fab?.setVisibility(View.GONE)
                    }
                    homeBinding?.filterViewactive?.setVisibility(View.VISIBLE)
                    homeBinding?.filterViewpending?.setVisibility(View.GONE)
                    selectedTab = TAB.Active
                    // Filtering list with provider data
                    filterList(providerHashMap.values)
                    mAdapter?.notifyDataSetChanged()

                    // this is to filter by score
                    filterByAcuityActive(acuityLevel)
                }
                R.id.pendingBtnLayout -> {
                    if (selectedTab == TAB.Pending) {
                        return@OnClickListener
                    }
                    getnewpendingchange()

                    homeBinding?.activetab?.setVisibility(View.GONE)
                    homeBinding?.pendingtab?.setVisibility(View.VISIBLE)
                    homeBinding?.complettab?.setVisibility(View.GONE)


                    homeBinding?.acuityFilterView?.setOnClickListener(View.OnClickListener { //Handling multiple click event with view
                        handleMultipleClick(homeBinding?.acuityFilterView!!)
                        //Showing the dialog with Filter by acuity pending
                        FilterByAcuityPendingDialog(this)
                    })
                    homeBinding?.txtClearpending?.setOnClickListener(View.OnClickListener { v ->
                        handleMultipleClick(v)

                        filterByAcuityPending(Constants.AcuityLevel.NA)
                        eConsultTime = Constants.ConsultTime.NA
                        //Filtering by pending acuity
                        filterByAcuityPending(acuityLevelPending)
                        filterdeselectpending()
                    })

                    // Filter view click listener
                    homeBinding?.filterViewpending?.setOnClickListener(View.OnClickListener { //Handling multiple click event with view
                        handleMultipleClick(homeBinding?.filterViewpending!!)
                        // Filtering by time dialog
                        FilterByTimeDialog(this)
                    })
                    homeBinding?.idBtnPending?.setTextColor(getResources().getColor(R.color.colorPrimary))
                    homeBinding?.idBtnPending?.setBackground(getResources().getDrawable(R.drawable.tab_selected_rounded))
                    homeBinding?.idBtnActive?.setTextColor(getResources().getColor(R.color.white))
                    homeBinding?.idBtnActive?.setBackground(getResources().getDrawable(R.drawable.transparent_bg))
                    homeBinding?.idBtnComplete?.setTextColor(getResources().getColor(R.color.white))
                    homeBinding?.idBtnComplete?.setBackground(getResources().getDrawable(R.drawable.transparent_bg))
                    //                    homeBinding.idBtnActive.setTypeface(Typeface.DEFAULT_BOLD);
                    homeBinding?.fab?.setVisibility(View.GONE)
                    homeBinding?.filterViewpending?.setVisibility(View.VISIBLE)
                    homeBinding?.filterViewactive?.setVisibility(View.GONE)
                    selectedTab = TAB.Pending
                    filterList(providerHashMap.values)
                    mAdapter?.notifyDataSetChanged()
                    //Filtering by pending acuity
                    filterByAcuityPending(acuityLevelPending)
                }
                R.id.completedBtnLayout -> {
                    homeBinding?.acuity?.setText(getString(R.string.type))
                    if (selectedTab == TAB.Patients) {
                        return@OnClickListener
                    }


                    homeBinding?.activetab?.setVisibility(View.GONE)
                    homeBinding?.pendingtab?.setVisibility(View.GONE)
                    homeBinding?.complettab?.setVisibility(View.VISIBLE)


                    homeBinding?.acuityFilterView?.setOnClickListener(View.OnClickListener {
                        homeBinding?.acuityFilterView?.let { it1 -> handleMultipleClick(it1) }
                        FilterByType(this)
                    })
                    homeBinding?.txtClearcomplet?.setOnClickListener(View.OnClickListener { v ->
                        handleMultipleClick(v)
                        // isActivePatient = false;
                        filterdeselectcomplet()
                        //  filterByAcuityActive(Constants.AcuityLevel.NA);
                    })
                    homeBinding?.idBtnComplete?.setTextColor(getResources().getColor(R.color.colorPrimary))
                    homeBinding?.idBtnComplete?.setBackground(getResources().getDrawable(R.drawable.tab_selected_rounded))
                    homeBinding?.idBtnActive?.setTextColor(getResources().getColor(R.color.white))
                    homeBinding?.idBtnActive?.setBackground(getResources().getDrawable(R.drawable.transparent_bg))
                    homeBinding?.idBtnPending?.setTextColor(getResources().getColor(R.color.white))
                    homeBinding?.idBtnPending?.setBackground(getResources().getDrawable(R.drawable.transparent_bg))
                    homeBinding?.fab?.setVisibility(View.GONE)
                    homeBinding?.filterViewactive?.setVisibility(View.GONE)
                    homeBinding?.filterViewpending?.setVisibility(View.GONE)
                    selectedTab = TAB.Patients
                    // used to restore data
                    filterList(providerHashMap.values)
                    mAdapter?.notifyDataSetChanged()

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
    @SuppressLint("SimpleDateFormat", "ClickableViewAccessibility")
    fun consultDetailsDialog(context: Context?, provider: ConsultProvider) {


        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.econsult_detail_dialog)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)

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

                val consultProvider: ConsultProvider? =
                    snapshot.getValue(ConsultProvider::class.java)
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

        mFirebaseDatabaseReference!!.addValueEventListener(unreadMessageListener)
        dialog.setOnDismissListener {
            mFirebaseDatabaseReference!!.removeEventListener(unreadMessageListener)
        }
        val stub = dialog.findViewById<View>(R.id.layout_stub_view) as ViewStub
        val statusStub = dialog.findViewById<View>(R.id.status_stub) as RelativeLayout
        UtilityMethods().displayVitals(this, stub, provider)
        UtilityMethods().displayPatientStatusComponent(this, statusStub, provider.getUrgent(),
            provider.getStatus() === Constants.PatientStatus.Pending, provider.getScore())
        txteConsult.setOnClickListener {
            val intentConsultChart = Intent(this, ActivityConsultChart::class.java)
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
            intentConsultChart.putExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                provider.getUrgent())
            if (provider.getStatus() != null) {
                intentConsultChart.putExtra("status", provider.getStatus().toString())
                if (provider.getStatus() === Constants.PatientStatus.Invited ||
                    provider.getStatus() === Constants.PatientStatus.Handoff
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                    clearNotifications(provider.patientId)
                } else if (provider.getStatus() === Constants.PatientStatus.Completed ||
                    provider.getStatus() === Constants.PatientStatus.Discharged
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID.toLong())
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
        val timeInMillis: Long? = provider.getDob()
        val dateString = SimpleDateFormat("MM-dd-yyyy").format(timeInMillis?.let { Date(it) })
        val strDob = dot + dateString
        if (!TextUtils.isEmpty(provider.getGender())) {
            strGender = dot + provider.getGender()
        }
        if (provider.getGender().equals("Male",ignoreCase = true)) {
            strGender = dot + "M"
        } else if (provider.getGender().equals("Female",ignoreCase = true)) {
            strGender = dot + "F"
        }
        strPhone = if (!TextUtils.isEmpty(provider.getPhone()) &&
            !provider.getPhone().equals("null",ignoreCase = true)
        ) {
            dot + provider.getPhone()
        } else {
            ""
        }
        strWard = if (!TextUtils.isEmpty(provider.getWardName()) &&
            !provider.getWardName().equals("null",ignoreCase = true)
        ) {
            dot + provider.getWardName()
        } else {
            ""
        }
        val strComplaint: String? = provider.getNote()
        var stringComplaint = strComplaint
        if (strComplaint?.contains(":") == true) {
            stringComplaint = strComplaint.substring(strComplaint.indexOf(":") + 1)
        }
        txtPatientName.setText(provider.getName())
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
        txtComplaint.text = stringComplaint?.trim { it <= ' ' }
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

    private fun startChat(consultProvider: ConsultProvider) {
        if (consultProvider == null || consultProvider.getPatientId() == null) {

            CustomSnackBar.make(homeBinding!!.root,
                this,
                CustomSnackBar.WARNING,
                "Patient does not exist",
                CustomSnackBar.TOP,
                3000,
                0)!!
                .show()
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

        //        Log.d(TAG, "strConsultTeamName : " + "Team " + consultProvider.getTeamName());
        intent.putExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT, consultProvider.getUrgent())
        if (consultProvider.getStatus() != null) {
            intent.putExtra("status", consultProvider.getStatus().toString())
            if (consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff) {
                intent.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                clearNotifications(consultProvider.getPatientId().intValue())
            } else if (consultProvider.getStatus() === Constants.PatientStatus.Completed || consultProvider.getStatus() === Constants.PatientStatus.Discharged) {
                intent.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
            }
        }
        startActivity(intent)
    }

    /**
     * Filter active screen by score
     */
    fun filterByAcuityActive(level: Constants.AcuityLevel) {
        if (providerFilteredList == null) {
            return
        }
        if (level === Constants.AcuityLevel.NA) {
            //Filter for Urgency
            var lowScoreData: MutableList<ConsultProvider?> = ArrayList<ConsultProvider?>()
            for (i in providerFilteredList.indices) {
                val consultProvider: ConsultProvider? = providerFilteredList[i]
                when (urgencyLevelType) {
                    Constants.UrgencyLevel.NA -> lowScoreData.add(consultProvider)
                    Constants.UrgencyLevel.Urgent -> if (consultProvider?.getUrgent() == true) {
                        lowScoreData.add(consultProvider)
                    }
                    Constants.UrgencyLevel.NotUrgent -> if (!consultProvider?.getUrgent()!!) {
                        lowScoreData.add(consultProvider)
                    }
                    else -> {
                    }
                }
            }
            val eConsultStatusList: List<ConsultProvider> = java.util.ArrayList<ConsultProvider>(lowScoreData)
            lowScoreData.clear()
            // filter for eConsult Status
            for (i in eConsultStatusList.indices) {
                val consultProvider: ConsultProvider = eConsultStatusList[i]
                when (eConsultStatus) {
                    Constants.PatientStatus.NA -> lowScoreData.add(consultProvider)
                    Constants.PatientStatus.Active-> if (consultProvider.getStatus() === Constants.PatientStatus.Active) {
                        lowScoreData.add(consultProvider)
                    }
                    Constants.PatientStatus.Pending -> if (consultProvider.getStatus() === Constants.PatientStatus.Pending) {
                        lowScoreData.add(consultProvider)
                    }
                }
            }

            //Filter for location
            val acuityUrgencyFilteredList: List<ConsultProvider> = java.util.ArrayList<ConsultProvider>(lowScoreData)
            if (selectedHospitalId > 0) {
                if (ALL_HOSPITAL_ID == selectedHospitalId) {
                    // skip filter hospital to add all hospital
                } else {
                    // This is to add selected hospital
                    lowScoreData.clear()
                    for (i in acuityUrgencyFilteredList.indices) {
                        val consultProvider: ConsultProvider = acuityUrgencyFilteredList[i]
                        if (selectedHospitalId == consultProvider.getHospitalId()) {
                            lowScoreData.add(consultProvider)
                        }
                    }
                }
            }

            // perform search
            lowScoreData = performSearch(lowScoreData)
            acuityLevel = level

            // This is for ALL acuity filter option
            if (mAdapter != null) {
                // It will update urgent and non urgent data for all acuity level filter
                mAdapter!!.updateList(lowScoreData)
                mAdapter!!.notifyDataSetChanged()
                if (mAdapter!!.getItemCount() <= 0) {
                    // This is to show error message layout with correct message
                    showEmptyMessageForActiveTab()
                    homeBinding?.noPatientLayout?.setVisibility(View.VISIBLE)
                } else {
                    homeBinding?.messageRecyclerView?.setVisibility(View.VISIBLE)
                    homeBinding?.noPatientLayout?.setVisibility(View.GONE)
                }
            }
            homeBinding?.filterText?.setText("All")
            return
        }
        var lowScoreData: MutableList<ConsultProvider?> = ArrayList<ConsultProvider?>()
        //Filter for Urgency
        for (i in providerFilteredList.indices) {
            val consultProvider: ConsultProvider? = providerFilteredList[i]
            if (consultProvider != null) {
                if (consultProvider.getScore() === level) {
                    when (urgencyLevelType) {
                        Constants.UrgencyLevel.NA -> lowScoreData.add(consultProvider)
                        Constants.UrgencyLevel.Urgent -> if (consultProvider.getUrgent() == true) {
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
        }
        val eConsultStatusList: List<ConsultProvider> = ArrayList<>
        lowScoreData.clear()
        //Filter for eConsult Status
        for (i in eConsultStatusList.indices) {
            val consultProvider: ConsultProvider = eConsultStatusList[i]
            when (eConsultStatus) {
                Constants.PatientStatus.NA -> lowScoreData.add(consultProvider)
                Constants.PatientStatus.Active-> if (consultProvider.getStatus() === Constants.PatientStatus.Active) {
                    lowScoreData.add(consultProvider)
                }
                Constants.PatientStatus.Pending -> if (consultProvider.getStatus() === Constants.PatientStatus.Pending) {
                    lowScoreData.add(consultProvider)
                }

            }
        }
        //Filter for Location
        val acuityUrgencyFilteredList: List<ConsultProvider> = ArrayList<>
        if (selectedHospitalId > 0) {
            if (ALL_HOSPITAL_ID == selectedHospitalId) {
                // skip filter hospital to add all hospital
            } else {
                // This is to add selected hospital
                lowScoreData.clear()
                for (i in acuityUrgencyFilteredList.indices) {
                    val consultProvider: ConsultProvider = acuityUrgencyFilteredList[i]
                    if (selectedHospitalId == consultProvider.getHospitalId()) {
                        lowScoreData.add(consultProvider)
                    }
                }
            }
        }

        // perform search
        lowScoreData = performSearch(lowScoreData)
        acuityLevel = level
        if (mAdapter != null) {
            mAdapter!!.updateList(lowScoreData)
            mAdapter!!.notifyDataSetChanged()
            if (mAdapter!!.getItemCount() <= 0) {
                // This is to show error message layout with correct message
                showEmptyMessageForActiveTab()
                homeBinding?.noPatientLayout?.setVisibility(View.VISIBLE)
            } else {
                homeBinding?.messageRecyclerView?.setVisibility(View.VISIBLE)
                homeBinding?.noPatientLayout?.setVisibility(View.GONE)
            }
        }
        homeBinding?.filterText?.setText(java.lang.String.valueOf(level))
    }

    /**
     * Perform search operation for selected filter type
     *
     * @param lowScoreData original list
     */
    private fun performSearch(lowScoreData: MutableList<ConsultProvider?>): MutableList<ConsultProvider?> {
        val searchQuery: String = homeBinding?.searchEdittext?.text.toString()
        if (!TextUtils.isEmpty(searchQuery)) {
            val tempOfFilterList: List<ConsultProvider> =ArrayList<ConsultProvider>()
            lowScoreData.clear()
            for (provider in tempOfFilterList) {
                var firstName: String? = provider.getFname()
                val lastName: String? = provider.getLname()
                if (!TextUtils.isEmpty(provider.getName())) {
                    firstName = provider.getName().toString()
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
    @SuppressLint("NotifyDataSetChanged")
    fun filterByAcuityPending(level: Constants.AcuityLevel) {
        if (providerFilteredList == null) {
            return
        }
        if (level === Constants.AcuityLevel.NA) {
            // This is for ALL acuity option filter


            // This is for ALL acuity option filter
            if (mAdapter != null) {
                val tempList: List<ConsultProvider?> = java.util.ArrayList<ConsultProvider?>(providerFilteredList)
                val consultProviders = filterByTime(tempList)
                val searchResult: List<ConsultProvider?> = performSearch(consultProviders as MutableList<ConsultProvider?>)
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
            homeBinding?.filterText?.setText("All")
            return
        }
        val lowScoreData: MutableList<ConsultProvider?> = ArrayList<ConsultProvider?>()
        // filter low status data
        for (i in providerFilteredList.indices) {
            val consultProvider: ConsultProvider? = providerFilteredList[i]
            if (consultProvider?.getScore() === level) {
                lowScoreData.add(consultProvider)
            }
        }
        val consultProviders: List<ConsultProvider?> = filterByTime(lowScoreData)
        val searchResult: List<ConsultProvider?> = performSearch(consultProviders  as MutableList<ConsultProvider?>)
        acuityLevelPending = level
        if (mAdapter != null) {
            mAdapter!!.updateList(searchResult as MutableList<ConsultProvider?>)
            mAdapter!!.notifyDataSetChanged()
            if (mAdapter?.getItemCount()!! <= 0) {
                showEmptyMessageForPendingTab()
                homeBinding?.noPatientLayout?.setVisibility(View.VISIBLE)
            } else {
                homeBinding?.messageRecyclerView?.setVisibility(View.VISIBLE)
                homeBinding?.noPatientLayout?.setVisibility(View.GONE)
            }
        }
        homeBinding?.filterText?.setText(java.lang.String.valueOf(level))
    }

    /**
     * Filtering by time with provider as input list
     *
     * @param providerList
     * @return
     */
    fun filterByTime(providerList: MutableList<ConsultProvider>): MutableList<ConsultProvider> {
        if (eConsultTime === Constants.ConsultTime.NA) {
            // If it's all the we don't need to change the list
            return providerList
        } else {
            val timeList: List<ConsultProvider?> = java.util.ArrayList<ConsultProvider?>(providerList)
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
                    Constants.ConsultTime.Fifteen -> {
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
        if (patientStatus === Constants.PatientStatus.NA) {
            patientStatusType = patientStatus
            if (mAdapter != null) {
                val tempArrayList: MutableList<ConsultProvider?> =
                    ArrayList<ConsultProvider?>(providerFilteredList)
                val searchResult: List<ConsultProvider?> = performSearch(tempArrayList)
                mAdapter!!updateList(searchResult as MutableList<ConsultProvider?>)
                mAdapter!!.notifyDataSetChanged()
                if (mAdapter!!.itemCount <= 0) {
                    showEmptyMessageForCompleteTab()
                    homeBinding?.noPatientLayout?.setVisibility(View.VISIBLE)
                } else {
                    homeBinding?.messageRecyclerView?.setVisibility(View.VISIBLE)
                    homeBinding?.noPatientLayout?.setVisibility(View.GONE)
                }
            }
            homeBinding?.filterText?.setText("All")
            return
        }
        val patientStatusData: MutableList<ConsultProvider?> = ArrayList<ConsultProvider?>()
        // filter patient status data
        for (i in providerFilteredList.indices) {
            val consultProvider: ConsultProvider? = providerFilteredList[i]
            if (consultProvider?.getStatus() === patientStatus) {
                patientStatusData.add(consultProvider)
            }
        }
        val searchResult: List<ConsultProvider?> = performSearch(patientStatusData)
        patientStatusType = patientStatus
        if (mAdapter != null) {
            mAdapter!!.updateList(searchResult as MutableList<ConsultProvider?>)
            mAdapter!!.notifyDataSetChanged()
            if (mAdapter!!.getItemCount() <= 0) {
                showEmptyMessageForCompleteTab()
                homeBinding?.noPatientLayout?.setVisibility(View.VISIBLE)
            } else {
                homeBinding?.messageRecyclerView?.setVisibility(View.VISIBLE)
                homeBinding?.noPatientLayout?.setVisibility(View.GONE)
            }
        }
        when (patientStatusType) {
            Constants.PatientStatus.Completed -> homeBinding?.filterText?.setText(getResources().getString(R.string.completed))
            Constants.PatientStatus.Discharged -> homeBinding?.filterText?.setText(java.lang.String.valueOf(Constants.PatientStatus.Completed))
        }
    }

    /**
     * This block to show error message based on user role and filter type chosen
     */
    private fun showEmptyMessageForActiveTab() {
        if (isCurrentUserIsBedSideUser) {
            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA && eConsultStatus === Constants.PatientStatus.NA && TextUtils.isEmpty(
                    homeBinding?.searchEdittext?.getText().toString())) {

                // This means there is no selected filter applied so resetted to default all section
                handleListVisibility(false)
            } else {
                showFilterErrorMessage()
            }
        } else if (isCurrentUserIsRemoteSideUser) {
            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA && selectedHospitalId == ALL_HOSPITAL_ID && TextUtils.isEmpty(
                    homeBinding?.searchEdittext?.getText().toString())) {

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
                homeBinding?.searchEdittext?.getText().toString())) {

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
            && TextUtils.isEmpty(homeBinding?.searchEdittext?.getText().toString())) {

            //Handling the visibilty of the tab - Active, Pending, Complete
            //Means there is no selected filter applied so resetted to default all section
            handleListVisibility(false)
        } else {
            showFilterErrorMessage()
        }
    }

    private fun showFilterErrorMessage() {
        homeBinding?.noPatientTitle?.setVisibility(View.VISIBLE)
        homeBinding?.noPatientText?.setText(getResources()?.getString(R.string.no_results_for_filter))
    }

    private val isCurrentUserIsBedSideUser: Boolean
      get() {
            val role: String? = PrefUtility().getRole(this)
            return if (!TextUtils.isEmpty(role)) {
                role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)
            } else false
        }
    private val isCurrentUserIsRemoteSideUser: Boolean
        get() {
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
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
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
            Constants.AcuityLevel.NA -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.VISIBLE
            }
            Constants.AcuityLevel.Low-> {
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
        //Filtering by low acuity
        rlLow.setOnClickListener { //Filter active screen by score - low
            if (filterByDialog != null) {
                filterByDialog!!.dismiss()
            }
            filterByAcuityActive(Constants.AcuityLevel.Low)
            dialog.dismiss()
            filterselectactive()
        }
        //Filtering by medium acuity
        rlMedium.setOnClickListener { //Filter active screen by score - medium
            if (filterByDialog != null) {
                filterByDialog!!.dismiss()
            }
            filterByAcuityActive(Constants.AcuityLevel.Medium)
            dialog.dismiss()
            filterselectactive()
        }
        // Filtering by high acuity
        rlHigh.setOnClickListener { //Filter active screen by score - High
            if (filterByDialog != null) {
                filterByDialog!!.dismiss()
            }
            filterByAcuityActive(Constants.AcuityLevel.High)
            dialog.dismiss()
            filterselectactive()
        }
        // Filtering by NA acuity
        rlAll.setOnClickListener {
            if (filterByDialog != null) {
                filterByDialog!!.dismiss()
            }
            acuityLevel = Constants.AcuityLevel.NA
            //Filter active screen by score - NA
            filterByAcuityActive(Constants.AcuityLevel.NA)
            homeBinding?.filterText?.setText("All")
            if (role != null && role.equals(Constants.ProviderRole.BD.toString(),
                    ignoreCase = true)
            ) {
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
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
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
                 Constants.AcuityLevel.Low-> {
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
            homeBinding?.filterText?.setText("All")
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
        filterByDialog!!.setContentView(R.layout.filter_by_dialog)
        filterByDialog!!.setCancelable(false)
        filterByDialog!!.setCanceledOnTouchOutside(false)
        filterByDialog!!.window!!.setGravity(Gravity.BOTTOM)
        filterByDialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        filterByDialog!!.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlUrgency = filterByDialog!!.findViewById<View>(R.id.rlUrgency) as RelativeLayout
        val rleConsultStatus =
            filterByDialog!!.findViewById<View>(R.id.rleConsultStatus) as RelativeLayout
        val imgCancel = filterByDialog!!.findViewById<View>(R.id.imgCancel) as ImageButton
        rlUrgency.setOnClickListener {
            //Displaying Filter by urgency dialog
            FilterByUrgencyDialog(this)
        }
        rleConsultStatus.setOnClickListener {
            //Displaying Filter by status dialog
            FilterByStatusDialog(this)
        }
        imgCancel.setOnClickListener { filterByDialog!!.dismiss() }
        filterByDialog!!.show()
    }

    /**
     * Displaying the remote side filter options dialog
     *
     * @param context
     */
    fun FilterByRpDialog(context: Context?) {
        filterByDialog = Dialog(context!!, R.style.Theme_Dialog)
        filterByDialog!!.setContentView(R.layout.filter_by_rp_dialog)
        filterByDialog!!.setCancelable(false)
        filterByDialog!!.setCanceledOnTouchOutside(false)
        filterByDialog!!.window!!.setGravity(Gravity.BOTTOM)
        filterByDialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        filterByDialog!!.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlUrgency = filterByDialog!!.findViewById<View>(R.id.rlUrgency) as RelativeLayout
        val rlLocation = filterByDialog!!.findViewById<View>(R.id.rlLocation) as RelativeLayout
        val imgCancel = filterByDialog!!.findViewById<View>(R.id.imgCancel) as ImageButton
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
        homeBinding?.txtClearactive?.setVisibility(View.VISIBLE)
        homeBinding?.filterViewactive?.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_selected))
    }

    fun filterdeselectactive() {
        homeBinding?.txtClearactive?.setVisibility(View.GONE)
        homeBinding?.filterViewactive?.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter))
        urgencyLevelType = Constants.UrgencyLevel.NA
        selectedHospitalId = ALL_HOSPITAL_ID
        eConsultStatus = Constants.PatientStatus.NA
        filterByAcuityActive(Constants.AcuityLevel.NA)
    }

    //filter metods pending
    fun filterselectpending() {
        homeBinding?.txtClearpending?.setVisibility(View.VISIBLE)
        homeBinding?.filterViewpending?.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_selected))
    }

    fun filterdeselectpending() {
        homeBinding?.txtClearpending?.setVisibility(View.GONE)
        homeBinding?.filterViewpending?.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter))
    }

    //filter methods complete
    fun filterselectcomplet() {
        homeBinding?.txtClearcomplet?.setVisibility(View.VISIBLE)
    }

    fun filterdeselectcomplet() {
        filterByStatusType(Constants.PatientStatus.NA)
        homeBinding?.txtClearcomplet?.setVisibility(View.GONE)
    }

    //end filter methods
    fun FilterByUrgencyDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_by_urgency_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
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
            Constants.UrgencyLevel.Urgent -> {
                imgUrgent.visibility = View.VISIBLE
                imgNonUrgent.visibility = View.GONE
                imgAll.visibility = View.GONE
            }
            Constants.UrgencyLevel.NotUrgent-> {
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
            if (role != null && role.equals(Constants.ProviderRole.BD.toString(),
                    ignoreCase = true)
            ) {
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
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
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
            Constants.PatientStatus.Pending -> {
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
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val imgBack = dialog.findViewById<View>(R.id.imgBack) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val txtTitle = dialog.findViewById<View>(R.id.txtTitle) as TextView
        txtTitle.setText(getString(R.string.filter_by_location))

        // Setting up the location list adapter
        val recyclerviewLocation =
            dialog.findViewById<View>(R.id.recyclerviewLocation) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerviewLocation.layoutManager = linearLayoutManager
        if (providerFilteredList != null) {
            val hospitalList: HashMap<Long, ConsultProvider?> = HashMap<Long, ConsultProvider?>()
            for (i in providerFilteredList.indices) {
                hospitalList[providerFilteredList[i].getHospitalId()] = providerFilteredList[i]
            }
            val allOption = ConsultProvider()
            allOption.setHospital("All")
            allOption.setHospitalId(ALL_HOSPITAL_ID)
            //hospitalList.put(allOptin.getHospitalId(), allOptin);
            locationListAdapter = LocationListAdapter(object : LocationListAdapter.HospitalRecyclerListener() {
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
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
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
            Constants.ConsultTime.Fifteen -> {
                imgFifteenMin.visibility = View.VISIBLE
                imgThirtyMin.visibility = View.GONE
                imgOneHour.visibility = View.GONE
                imgOverOneHour.visibility = View.GONE
                imgTimeAll.visibility = View.GONE
            }
            Constants.ConsultTime.Thirty-> {
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
            Constants.ConsultTime.OverOneHour-> {
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
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
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
            homeBinding?.filterText?.setText(getResources().getString(R.string.completed))
            dialog.dismiss()
            filterselectcomplet()
        }
        rlDischarged.setOnClickListener { // Filtering by status type - Exit
            filterByStatusType(Constants.PatientStatus.Discharged)
            homeBinding?.filterText?.setText(java.lang.String.valueOf(Constants.PatientStatus.Discharged))
            dialog.dismiss()
            filterselectcomplet()
        }
        rlAll.setOnClickListener { // Filtering by status type - NA
            // filterByStatusType(Constants.PatientStatus.NA);
            patientStatusType = Constants.PatientStatus.NA
            homeBinding?.filterText?.setText("All")
            dialog.dismiss()
            filterdeselectcomplet()
        }
        dialog.show()
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<DrawerActivity>.onCreate(savedInstanceState)
        role = PrefUtility().getRole(this)
        containerParent = findViewById(R.id.container)
        homeBinding = DataBindingUtil.inflate(getLayoutInflater(),
            R.layout.activity_home,
            containerParent,
            true)
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        intentCensus = getIntent().getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)
        homeBinding!!.acuityFilterView.setOnClickListener(View.OnClickListener { //Handling multiple click event with view
            handleMultipleClick(homeBinding!!.acuityFilterView)
            //Showing the dialog with Filter by acuity
            FilterByAcuityDialog(this)
        })
        // Setting up the view
        setView()
        // Pre clear all the notifications
       clearAllNotifications()

    }

    private fun clearAllNotifications() {
        NotificationHelper(this, null).clearAllNotification()
    }

    protected override fun onNewIntent(intent: Intent) {
        super<DrawerActivity>.onNewIntent(intent)
        //Handling the notification click based on the intent
        handleNotification(intent)
    }

    private fun handleNotification(intent: Intent) {
        val userId: Long = PrefUtility.getProviderId(this)
        // To login activity if user is null / -1
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
     * Setting up the view
     */
    private fun setView() {
        binding?.leftNavView?.setNavigationItemSelectedListener(this)
        binding?.rightNavView?.setNavigationItemSelectedListener(this)
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
        handleNotification(getIntent())
        homeBinding?.activetab?.setVisibility(View.VISIBLE)
        //Designation based layout visibility handling
        strDesignation =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        if (strDesignation.equals("md/do", ignoreCase = true)) {
            homeBinding?.pendingBtnLayout?.setVisibility(View.VISIBLE)
        } else {
            homeBinding?.pendingBtnLayout?.setVisibility(View.GONE)
        }
        // Getting the bundle data intents
        val extras: Bundle? = getIntent().getExtras()
        if (extras != null) {
            val select = extras.getString(Constants.IntentKeyConstants.TARGET_PAGE, "")
            if (select == "pending") {
                getnewpendingchange()
                if (homeBinding?.pendingBtnLayout?.getVisibility() === View.VISIBLE) {
                    homeBinding?.pendingBtnLayout?.performClick()
                }
            } else if (select == "completed") {
                homeBinding?.completedBtnLayout?.performClick()
            } else if (select == "active") {
                homeBinding?.activeBtnLayout?.performClick()
            } else {
                homeBinding?.activeBtnLayout?.performClick()
            }
        } else {
            homeBinding?.activeBtnLayout?.performClick()
        }

        // Swipe refress layout handling
        homeBinding?.swipeLayout?.setOnRefreshListener(OnRefreshListener {
            //Handling the firebase listview setup
            setupFirebaseListView()
            homeBinding?.swipeLayout?.setRefreshing(false)


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
            homeBinding!!.filterText.setText("All")


            //clear
            filterdeselectactive()
            filterdeselectcomplet()
            filterByAcuityPending(Constants.AcuityLevel.NA)
            eConsultTime = Constants.ConsultTime.NA
            //Filtering by pending acuity
            filterByAcuityPending(acuityLevelPending)
            filterdeselectpending()
        if (mAdapter != null) {
            if (providerFilteredList != null) {
                mAdapter!!.updateList(providerFilteredList)
            }
            mAdapter!!.notifyDataSetChanged()
            if (mAdapter!!.getItemCount() <= 0) {
                if (selectedTab == TAB.Active) {
                    showEmptyMessageForActiveTab()
                } else if (selectedTab == TAB.Pending) {
                    showEmptyMessageForPendingTab()
                } else if (selectedTab == TAB.Patients) {
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
            homeBinding?.searchLayout?.setVisibility(View.GONE)
            homeBinding?.searchEdittext?.setText("")
            homeBinding?.tabsLayout?.setVisibility(View.VISIBLE)
            //clear
        })

        // Click listener fot add patient task - Redirected to AddPatientActivity
        homeBinding?.fab?.setOnClickListener(View.OnClickListener {
            homeBinding?.fab?.let { it1 -> handleMultipleClick(it1) }
            val intent = Intent(this, AddPatientActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.FROM_PAGE, "myConsults")
            startActivity(intent)
        })
        /**
         * Acuity filter view click listener
         */
        homeBinding?.txtClearactive?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)

            filterdeselectactive()
        })
        homeBinding?.txtClearpending?.setOnClickListener(View.OnClickListener { v ->

            filterByAcuityPending(Constants.AcuityLevel.NA)
            eConsultTime = Constants.ConsultTime.NA
            //Filtering by pending acuity
            filterByAcuityPending(acuityLevelPending)
            filterdeselectpending()
        })
        homeBinding?.txtClearcomplet?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)

            filterdeselectcomplet()

        })


        homeBinding?.filterViewactive?.setOnClickListener(View.OnClickListener {
            handleMultipleClick(homeBinding?.filterViewactive!!)
            if (role != null && role.equals(Constants.ProviderRole.BD.toString(),
                    ignoreCase = true)
            ) {
                if (selectedTab == TAB.Active) {
                    FilterByDialog(this)
                }
            } else {
                if (selectedTab == TAB.Active) {
                    FilterByRpDialog(this)
                } else {
                    FilterByTimeDialog(this)
                }
            }
        })
        homeBinding?.filterViewpending?.setOnClickListener(View.OnClickListener {
            handleMultipleClick(homeBinding?.filterViewpending!!)
            if (role != null && role.equals(Constants.ProviderRole.BD.toString(),
                    ignoreCase = true)
            ) {
                if (selectedTab == TAB.Active) {
                    FilterByDialog(this)
                }
            } else {
                if (selectedTab == TAB.Active) {
                    FilterByRpDialog(this)
                } else {
                    FilterByTimeDialog(this)
                }
            }
        })
        homeBinding?.searchIcon?.setOnClickListener(View.OnClickListener {
            searchQueryStr = ""
            homeBinding?.searchLayout?.visibility = View.VISIBLE
            homeBinding?.tabsLayout?.visibility = View.GONE
        })
        homeBinding?.closeSearch?.setOnClickListener(View.OnClickListener { view ->
            searchQueryStr = ""
            if (TextUtils.isEmpty(homeBinding?.searchEdittext?.getText().toString())) {
                homeBinding?.searchLayout?.visibility = View.GONE
                homeBinding?.tabsLayout?.visibility = View.VISIBLE
                (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
            } else if (homeBinding?.searchEdittext?.getText().toString().length > 0) {
                homeBinding?.searchEdittext?.setText("")
                homeBinding?.tabsLayout?.visibility = View.GONE
            }
        })

        //Query listener
        setSearchTextWatcher()
    }

    private fun handleTabButtons() {
        homeBinding!!.activeBtnLayout.setOnClickListener(tabChangeListener)
        homeBinding!!.completedBtnLayout.setOnClickListener(tabChangeListener)
        homeBinding!!.pendingBtnLayout.setOnClickListener(tabChangeListener)
    }

    /**
     * Query listener
     */
    private fun setSearchTextWatcher() {
        homeBinding?.searchEdittext?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {

                // Perform search
                onQuerySearch(editable.toString())
            }
        })
    }

    /**
     * Initialzing the tool bar
     */
    private fun initToolbar() {
        val pId: Long? = PrefUtility().getProviderId(this)
        setSupportActionBar(homeBinding?.toolbar)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        }
        val role: String?= PrefUtility().getRole(this)
        if (role != null && role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
            homeBinding?.fab?.setVisibility(View.VISIBLE)
        } else {
            homeBinding?.fab?.setVisibility(View.GONE)
        }
        homeBinding?.idNavigationIcon?.setImageResource(R.drawable.ic_menu_icon)

        homeBinding?.idNavigationIcon?.setOnClickListener { view ->
            PrefUtility().getProviderId(this)?.let { getProviderDetailsById(it) }
            val intent = Intent(this, MyDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
        }

        strFeedbackForm =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FEEDBACK_URL, "").toString()

    }

    /**
     * Getting provider details by ID
     *
     * @param uid
     */
    private fun getProviderDetailsById(it: Long) {
        val id: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getToken(this)
        viewModel!!.getProviderById(id, token, uid).observe(this, { commonResponse ->
//            Log.d(TAG, "getProviderDetails: " + new Gson().toJson(commonResponse));
            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus() && commonResponse.getProvider() != null) {
                val provider = commonResponse.getProvider()

//                PrefUtility.saveUserData(this, provider);
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.HOSPITAL_NAME,
                    provider!!.getHospital())
                val hospitalTxt: TextView = navHeaderView!!.findViewById(R.id.id_hospital_name)
                hospitalTxt.text = provider!!.getHospital()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    commonResponse!!.getErrorMessage(),
                    Constants.API.getProviderById)
                //                UtilityMethods.showErrorSnackBar(binding.container, errMsg, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(binding!!.container,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)!!
                    .show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.home_screen_menu, menu)
        val addPatientMenu = menu.findItem(R.id.add_patient_menu)
        val directoryMenu = menu.findItem(R.id.directory)
        addPatientMenu.isVisible = false
        directoryMenu.isVisible = false
        val role: String?= PrefUtility().getRole(this)
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            addPatientMenu.isVisible = false
            directoryMenu.isVisible = false
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (currentUser != null) {
            if (!TextUtils.isEmpty(currentUser?.getLcpType())) {
                if (currentUser?.getLcpType()
                        .equals(Constants.KeyHardcodeToken.LCP_TYPE_HOME,ignoreCase = true)
                ) {
                    menu.findItem(R.id.add_patient_menu).isVisible = false
                }
            }
        }
        return super<DrawerActivity>.onPrepareOptionsMenu(menu)
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
        return super<DrawerActivity>.onOptionsItemSelected(item)
    }

    private fun onClickAddPatient() {
        val intent = Intent(this, AddPatientActivity::class.java)
        startActivityForResult(intent, Constants.ActivityRequestCodes.ADD_PATIENT_REQ_CODE)
    }

    @SuppressLint("SetTextI18n")
    private fun handleDrawerHeaderView() {
        val profileImg: CircularImageView? = navHeaderView?.findViewById(R.id.id_profile_img)
        val defaultImgView: TextView? = navHeaderView?.findViewById(R.id.default_image_view)
        val cameraIcon: ImageView? = navHeaderView?.findViewById(R.id.cameraIcon)
        val imgLayout: FrameLayout?= navHeaderView?.findViewById(R.id.id_image_layout)
        val nameTxt: TextView? = navHeaderView?.findViewById(R.id.id_name)
        val hospitalTxt: TextView? = navHeaderView?.findViewById(R.id.id_hospital_name)
        val roleTxt: TextView? = navHeaderView?.findViewById(R.id.id_role)
        val emailTxt: TextView? = navHeaderView?.findViewById(R.id.id_email)
        val menuMyConsultsView: LinearLayout? = navHeaderView?.findViewById(R.id.menuMyConsults)
        val menuHandOffPatients: LinearLayout? = navHeaderView?.findViewById(R.id.menuHandoffPatients)
        val menuTrainingMaterials: LinearLayout? =
            navHeaderView?.findViewById(R.id.menuTrainingMaterials)
        val menuEulaTerms: LinearLayout? = navHeaderView?.findViewById(R.id.menuEulaTerms)
        val menuSystemAlert: LinearLayout? = navHeaderView?.findViewById(R.id.menuSystemAlert)
        val menuContactAdmin: LinearLayout? = navHeaderView?.findViewById(R.id.menuContactAdmin)
        val menuVirtualTeam: LinearLayout? = navHeaderView?.findViewById(R.id.menuVirtualTeam)
        val menuChangePassword: LinearLayout? = navHeaderView?.findViewById(R.id.menuChangePassword)
        val menuFeedbackView: LinearLayout? = navHeaderView?.findViewById(R.id.menuFeedback)
        val menuSignoutView: LinearLayout? = navHeaderView?.findViewById(R.id.menuSignout)

        val name: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        val hospitalName: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.HOSPITAL_NAME, "")
        val imageURL: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.PROFILE_IMG_URL, "")
        val email: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        if (!TextUtils.isEmpty(name)) {
            nameTxt?.text = name
        }
        if (!TextUtils.isEmpty(hospitalName)) {
            hospitalTxt?.text = hospitalName
        }
        if (!TextUtils.isEmpty(role)) {
            if (role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
                roleTxt?.setText(R.string.bedside_provider)
                hospitalTxt?.visibility = View.VISIBLE
            } else if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                roleTxt?.setText(R.string.remote_provider)
                hospitalTxt?.visibility = View.GONE
            }
        }
        if (currentUser != null) {
            if (!TextUtils.isEmpty(currentUser?.getLcpType())) {
                if (currentUser?.getLcpType()
                        .equals(Constants.KeyHardcodeToken.LCP_TYPE_HOME,ignoreCase = true)) {
                    hospitalTxt?.visibility = View.GONE
                    roleTxt?.text = "Home Provider"
                }
            }
        }
        if (!TextUtils.isEmpty(imageURL)) {
            profileImg?.setVisibility(View.VISIBLE)
            defaultImgView?.visibility = View.GONE
            cameraIcon?.visibility = View.GONE
            ImageLoader(this, imageURL).execute()
        } else {
            profileImg?.setVisibility(View.GONE)
            defaultImgView?.visibility = View.VISIBLE
            cameraIcon?.visibility = View.VISIBLE
            defaultImgView?.setText(UtilityMethods().getNameText(name))
        }
        if (!TextUtils.isEmpty(email)) {
            emailTxt?.visibility = View.VISIBLE
            emailTxt?.text = email
        } else {
            emailTxt?.visibility = View.GONE
        }
        imgLayout?.setOnClickListener { view: View? ->
            HomeActivity(0).selectImage() }


        menuMyConsultsView?.setOnClickListener { view: View? ->
//            Log.i(TAG, "On click myconsults: ");
            drawerLayout?.closeDrawers()
        }
        menuChangePassword?.setOnClickListener { view: View? ->
//            Log.i(TAG, "On click change password : ");
            drawerLayout?.closeDrawers()
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
        menuHandOffPatients?.setOnClickListener { view: View? ->
//            Log.i(TAG, "On click handoff patients : ");
            drawerLayout?.closeDrawers()
            val intent = Intent(this, HandOffPatientsActivity::class.java)
            startActivity(intent)
        }
        menuTrainingMaterials.setOnClickListener { view: View? ->
//            Log.i(TAG, "On click training material : ");
            drawerLayout?.closeDrawers()

            val url: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TUTORIAL_URL, "")
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        menuEulaTerms.setOnClickListener { view: View? ->

            drawerLayout?.closeDrawers()
            val intent = Intent(this, TermsAndConditionsActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.SHOW_TERMS_BUTTON, false)
            startActivity(intent)
        }
        menuSystemAlert.setOnClickListener { view: View? ->

            drawerLayout?.closeDrawers()
            val intent = Intent(this, SystemAlertActivity::class.java)
            startActivity(intent)
        }
        menuVirtualTeam.setOnClickListener { view: View? ->

            drawerLayout?.closeDrawers()
            val intent = Intent(this, MyVirtualTeamsActivity::class.java)
            startActivity(intent)
        }
        menuContactAdmin?.setOnClickListener { view: View? ->

            drawerLayout?.closeDrawers()
            val intent = Intent(this, ContactAdminActivity::class.java)
            startActivity(intent)
        }
        menuFeedbackView?.setOnClickListener { view: View? ->
            drawerLayout?.closeDrawers()

            val uri = Uri.parse(strFeedbackForm)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        menuSignoutView?.setOnClickListener { view: View ->

            drawerLayout?.closeDrawers()
            handleMultipleClick(view)
            onClickLogout()
        }
    }

    class ImageLoader: AsyncTask<java.lang.Void, java.lang.Void, Bitmap>() {
        var activityReference: WeakReference<HomeActivity>? = null
        var imageURL: String? = null
        var imageProgressBar: ProgressBar? = null
        var imageView: ImageView? = null
        var cameraIcon:android.widget.ImageView? = null
        var defaultImgView: TextView? = null

        fun ImageLoader(activity: HomeActivity, imageURL: String?) {
            activityReference = WeakReference(activity)
            this.imageURL = imageURL
            imageView = activityReference!!.get()!!.navHeaderView!!.findViewById(R.id.id_profile_img)
            cameraIcon = activityReference!!.get()!!.navHeaderView!!.findViewById(R.id.cameraIcon)
            imageProgressBar = activityReference!!.get()!!.navHeaderView!!.findViewById(R.id.id_profile_image_pb)
            defaultImgView = activityReference!!.get()!!.navHeaderView!!.findViewById(R.id.default_image_view)
        }

        protected override fun onPreExecute() {
            super.onPreExecute()
            imageProgressBar!!.visibility = View.VISIBLE
            imageView!!.visibility = View.GONE
            defaultImgView!!.visibility = View.VISIBLE
            cameraIcon?.setVisibility(View.VISIBLE)
            val name: String? = PrefUtility().getStringInPref(activityReference!!.get(),
                Constants.SharedPrefConstants.NAME, "")
            defaultImgView!!.setText(UtilityMethods().getNameText(name))
        }

        protected override fun doInBackground(vararg params: Void?): Bitmap? {
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

        protected override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            val homeActivity = activityReference!!.get()
            if (homeActivity != null) {
                imageProgressBar!!.visibility = View.GONE
                if (bitmap != null) {
                    imageView!!.visibility = View.VISIBLE
                    defaultImgView!!.visibility = View.GONE
                    cameraIcon?.setVisibility(View.GONE)
                    imageView!!.setImageBitmap(bitmap)
                } else {
                    imageView!!.visibility = View.GONE
                    defaultImgView!!.visibility = View.VISIBLE
                    cameraIcon?.setVisibility(View.VISIBLE)
                    val name: String? = PrefUtility().getStringInPref(homeActivity,
                        Constants.SharedPrefConstants.NAME, "")
                    defaultImgView.setText(UtilityMethods.getNameText(name))
                }
            }
        }

        protected override fun onCancelled() {
            super.onCancelled()
        }
    }

    /**
     * Handling the firebase listview setup and intialzation
     */
    private fun setupFirebaseListView() {
        val uid: String? = PrefUtility().getFireBaseUid(this)

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth!!.currentUser
        if (mFirebaseUser == null || uid == "") {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        mProviderUid = uid //mFirebaseUser.getUid();
        val mLinearLayoutManager = LinearLayoutManager(this)
        homeBinding?.messageRecyclerView?.setLayoutManager(mLinearLayoutManager)
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
            mAdapter?.getFilter()?.filter(query)
        }
        if (TextUtils.isEmpty(query)) {
            if (selectedTab == TAB.Active) {
                //Filter active screen by score
                filterByAcuityActive(acuityLevel)
            } else if (selectedTab == TAB.Pending) {
                //Filtering by pending acuity
                filterByAcuityPending(acuityLevelPending)
            } else if (selectedTab == TAB.Patients) {
                // Filtering by status type
                filterByStatusType(patientStatusType)
            }
        }

    }

    private fun onClickStatusButton() {
        val providerId: Long? = PrefUtility().getProviderId(this)
        var status: String? = PrefUtility().getProviderStatus(this)
        status =
            if (TextUtils.isEmpty(status) || status.equals(Constants.ProviderStatus.OffLine.toString(),
                    ignoreCase = true)
            ) {
                Constants.ProviderStatus.Active.toString()
            } else {
                Constants.ProviderStatus.OffLine.toString()
            }
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(binding?.container,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return
        }
        drawerLayout?.closeDrawer(GravityCompat.START, true)
        val provider = Provider()
        provider.setStatus(status)
        provider.setId(providerId)
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.updateProvider.toString()))
        viewModel?.updateProviderStatus(provider)?.observe(this) { commonResponse ->

            dismissProgressBar()
            if (commonResponse != null && commonResponse.status != null && commonResponse.status!!) {
                val currentStatus: String? = commonResponse.getProvider()?.getStatus()
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.PROVIDER_STATUS,
                    currentStatus)

                CustomSnackBar.make(binding?.drawerLayout,
                    this,
                    CustomSnackBar.SUCCESS,
                    getString(R.string.status_updated_successfully),
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
                updateProviderStatus()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    commonResponse?.getErrorMessage(),
                    Constants.API.updateProvider)

                CustomSnackBar.make(binding?.drawerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
            }
        }
    }

    private fun updateProviderStatus() {

        val dotImg: ImageView = navHeaderView!!.findViewById(R.id.id_dot)
        val status: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.PROVIDER_STATUS, "")
        if (TextUtils.isEmpty(status) || status.equals(Constants.ProviderStatus.OffLine.toString(),
                ignoreCase = true)) {
            dotImg.setImageResource(R.drawable.ic_grey_circle)
            homeBinding!!.idDot.setImageResource(R.drawable.ic_grey_circle)
        } else {
            //statusBtn.setText(R.string.go_offline);
            dotImg.setImageResource(R.drawable.ic_green_circle)
            homeBinding!!.idDot.setImageResource(R.drawable.ic_green_circle)
        }
    }

    // Migrated to MyProfileActivity
    private fun updateProfileImage(provider: Provider, bitmap: Bitmap?) {
        val progressBar: ProgressBar? = navHeaderView?.findViewById(R.id.id_profile_image_pb)
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(binding?.container,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            progressBar?.visibility = View.GONE
            return
        }
        viewModel?.updateProviderStatus(provider)?.observe(this) { commonResponse ->
//            Log.i(TAG, "onClickStatusButton: ");
            progressBar.visibility = View.GONE
            if (commonResponse != null && commonResponse.status!= null && commonResponse.status!!) {
                val imageView: ImageView =
                    navHeaderView!!.findViewById(R.id.id_profile_img)
                imageView.visibility = View.VISIBLE
                findViewById(R.id.default_image_view).setVisibility(View.GONE)
                findViewById(R.id.cameraIcon).setVisibility(View.GONE)
                imageView.setImageBitmap(bitmap)
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    commonResponse?.getErrorMessage(),
                    Constants.API.updateProvider)

                CustomSnackBar.make(binding?.drawerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
            }
        }
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
        messagesRef!!.addChildEventListener(childEventListener!!)
        Handler().postDelayed({
            if (providerFilteredList!!.size <= 0) {
                filterList(providerFilteredList)
            }

        }, 1000)
        mAdapter = PatientListAdapter(this, providerFilteredList)

        // Handling the search result listener
        mAdapter!!.setOnSearchResultListener(object : PatientListAdapter.OnSearchResultListener {
            override fun onSearchResult(count: Int) {
                if (count <= 0) {
                    if (selectedTab == TAB.Active) {
                        homeBinding?.noPatientLayout?.setVisibility(View.VISIBLE)
                        showEmptyMessageForActiveTab()
                    } else if (selectedTab == TAB.Pending) {
                        homeBinding?.noPatientLayout?.setVisibility(View.VISIBLE)
                        showEmptyMessageForPendingTab()
                    } else if (selectedTab == TAB.Patients) {
                        homeBinding?.noPatientLayout?.setVisibility(View.VISIBLE)
                        showEmptyMessageForCompleteTab()
                    } else {
                        //Handling the visibilty of the tab - Active, Pending, Complete
                        //Means there is no selected filter applied so resetted to default all section
                        handleListVisibility(false)
                    }
                } else {
                    homeBinding?.noPatientLayout?.setVisibility(View.GONE)
                    homeBinding?.filterLayout?.setVisibility(View.VISIBLE)
                    homeBinding?.messageRecyclerView?.setVisibility(View.VISIBLE)
                    homeBinding?.noPatientTitle?.setVisibility(View.GONE)
                }
            }
        })
        homeBinding?.messageRecyclerView?.setAdapter(mAdapter)
        if (true) {
            return
        }
        val options: FirebaseRecyclerOptions<ConsultProvider?> =
            FirebaseRecyclerOptions.Builder<ConsultProvider>()
                .setQuery(query!!, ConsultProvider::class.java)
                .build()

        // Setting up the view
        mFirebaseAdapter =
            object : FirebaseRecyclerAdapter<ConsultProvider?, ConsultListViewHolder>(options) {
                override fun onCreateViewHolder(
                    viewGroup: ViewGroup,
                    position: Int,
                ): ConsultListViewHolder {

                    val inflater = LayoutInflater.from(viewGroup.context)
                    val itemBinding: ItemConsultListBinding = DataBindingUtil.inflate(inflater,
                        R.layout.item_consult_list,
                        viewGroup,
                        false)

                    val viewHolder = ConsultListViewHolder(itemBinding)
                    viewHolder.setOnClickListeners()
                    return viewHolder
                }

                protected fun onBindViewHolder(
                    viewHolder: ConsultListViewHolder,
                    position: Int,
                    consultProvider: ConsultProvider?
                ) {

                    viewHolder.binder(consultProvider,
                        selectedTab,
                        filterPatientStatus,
                        searchQueryStr,
                        expandedPosition)
                    setMessageCount(consultProvider!!)
                }

            }
                // Firebase recyclerview adapter observer handling
                mFirebaseAdapter.registerAdapterDataObserver(
                object : AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, totalItemCount: Int) {
                        super.onItemRangeInserted(positionStart, totalItemCount)

                    }
                })

                homeBinding.messageRecyclerView.setAdapter(mFirebaseAdapter)

            }
    /**
     * Filtering the provider data item based on datasnopshot and updating the adapter
     *
     * @param dataSnapshot
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun filterItem(dataSnapshot: DataSnapshot) {
        val key = dataSnapshot.key
        val consultProvider: ConsultProvider? = providerHashMap[key]
        if (consultProvider != null) {
            providerHashMap[key] = dataSnapshot.getValue(ConsultProvider::class.java)
            if (consultProvider === dataSnapshot.getValue(ConsultProvider::class.java)) {
                val id: Long? = mAdapter!!.getExpandedPatientId()
                if (consultProvider.getPatientId() != null && id != null && consultProvider.getPatientId()!!
                        .equals(id)
                ) {
                    filterList(providerHashMap.values)
                    val index: Int = mAdapter!!.getItemIndex(id)
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
    fun getProviderDetailsById(uid: Long) {
        val id: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getToken(this)
        if (id != null) {
            if (token != null) {
                viewModel?.getProviderById(id, token, uid)?.observe(this) { commonResponse ->

                    if (commonResponse != null && commonResponse.status != null && commonResponse.status && commonResponse.getProvider() != null) {
                        val provider: Provider = commonResponse.getProvider()!!

                        PrefUtility().saveStringInPref(this,
                            Constants.SharedPrefConstants.HOSPITAL_NAME,
                            provider.getHospital())
                        val hospitalTxt: TextView = navHeaderView.findViewById(R.id.id_hospital_name)
                        hospitalTxt.setText(provider.getHospital())
                    } else {
                        val errMsg: String = ErrorMessages.getErrorMessage(this,
                            commonResponse?.getErrorMessage(),
                            Constants.API.getProviderById)

                        CustomSnackBar.make(binding?.container,
                            this,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()
                    }
                }
            }
        }
    }

    /**
     * Handling the filter list with consult provider data
     *
     * @param providers
     */
    fun filterList(providers: Collection<ConsultProvider?>?) {
        if (providers == null || providers.isEmpty()) {
            providerFilteredList!!.clear()
            //Handling the visibilty of the tab - Active, Pending, Complete
            //Means there is no selected filter applied so resetted to default all section
           HomeActivity(0).handleListVisibility(false)
            homeBinding?.badgeCount?.setVisibility(View.GONE)
            return
        }
        providerFilteredList!!.clear()
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
            if (selectedTab == TAB.Active) {
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
                        && (consultProvider.getStatus().equals(Constants.PatientStatus.Active)
                                || consultProvider.getStatus()
                            ?.equals(Constants.PatientStatus.Patient) == true
                                || consultProvider.getStatus()
                            ?.equals(Constants.PatientStatus.HandoffPending) == true //                            || consultProvider.getStatus().equals(Constants.PatientStatus.Handoff)
                                )
                    ) {
                    } else {
                        continue
                    }
                }
            } else if (selectedTab == TAB.Patients) {
                if (consultProvider.getStatus() === Constants.PatientStatus.Active || consultProvider.getStatus() === Constants.PatientStatus.Pending || consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff || consultProvider.getStatus() === Constants.PatientStatus.HomeCare || consultProvider.getStatus() === Constants.PatientStatus.HandoffPending || consultProvider.getStatus() === Constants.PatientStatus.Patient) {
                    continue
                }
            } else if (selectedTab == TAB.Pending) {
                if (consultProvider.getStatus() === Constants.PatientStatus.Pending || consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Handoff) {
                } else {
                    continue
                }
            }
            if (searchQueryStr != null) {
                var firstName: String = consultProvider.getFname().toString()
                val lastName: String? = consultProvider.getLname()
                if (!TextUtils.isEmpty(consultProvider.getName())) {
                    firstName = consultProvider.getName().toString()
                }
                var isFound = false
                if (firstName != null && firstName.trim { it <= ' ' }.toLowerCase().contains(
                        searchQueryStr!!.toLowerCase())
                    || lastName != null && lastName.trim { it <= ' ' }.toLowerCase().contains(
                        searchQueryStr!!.toLowerCase())
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
            homeBinding?.badgeCount?.setVisibility(View.GONE)
            return
        }
        //Handling the visibilty of the tab - Active, Pending, Complete
        //Means there is no selected filter applied so resetted to default all section
        handleListVisibility(true)
        //Sorting the provider list by time
        Collections.sort(providerFilteredList, PatientSortByTime())

        // To refresh patient list adapter
        if (mAdapter != null) {
            mAdapter!!.updateList(providerFilteredList)
        }

        // Badge count visibiltiy
        if (badgeCount > 0) {
            homeBinding?.badgeCount?.setVisibility(View.VISIBLE)
            val badgeCountStr = badgeCount.toString()
            if (badgeCountStr.length == 1) {
                homeBinding?.badgeCount.setTextSize(12)
            } else if (badgeCountStr.length > 1) {
                homeBinding?.badgeCount.setTextSize(10)
            } else if (badgeCountStr.length > 2) {
                homeBinding?.badgeCount.setTextSize(7)
            }
            homeBinding?.badgeCount.setText(badgeCountStr)
        } else {
            homeBinding?.badgeCount?.setVisibility(View.GONE)
        }
    }

    /**
     * Handling the visibilty of the tab - Active, Pending, Complete
     * Means there is no selected filter applied so resetted to default all section
     *
     * @param isVisible
     */
    fun handleListVisibility(isVisible: Boolean) {
        var page = TAB.values().get(selectedTab).toString()
        if (selectedTab == TAB.Active) {
            page = "Active"
        } else if (selectedTab == TAB.Pending) {
            page = "Pending"
        } else if (selectedTab == TAB.Patients) {
            page = "Complete"
        }
        homeBinding?.noPatientText?.setText(getString(R.string.no_patient_found_new, page))
        if (isVisible) {
            homeBinding?.messageRecyclerView?.setVisibility(View.VISIBLE)
            homeBinding?.filterLayout?.setVisibility(View.VISIBLE)

        } else {
            homeBinding?.messageRecyclerView?.setVisibility(View.GONE)
            homeBinding?.noPatientLayout?.setVisibility(View.VISIBLE)
            homeBinding?.noPatientTitle?.setVisibility(View.GONE)
            homeBinding?.filterLayout?.setVisibility(View.GONE)
            if (TextUtils.isEmpty(searchQueryStr) && filterPatientStatus == null) {

            }
        }
    }

    /**
     * Add patient click listener - Triggering to add patient activity
     */
    fun onClickAddPatient() {
        val intent = Intent(this, AddPatientActivity::class.java)
        startActivityForResult(intent, Constants.ActivityRequestCodes.ADD_PATIENT_REQ_CODE)
    }

    fun onClickDirectory() {

    }

    /**
     * Triggering Chat activity based on the consult provider
     *
     * @param consultProvider
     */
    fun startChat(consultProvider: ConsultProvider?) {
        if (consultProvider?.getPatientId() == null) {

            CustomSnackBar.make(homeBinding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                "Patient does not exist",
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return
        }


        val intent = Intent(getBaseContext(), ChatActivity::class.java)
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
                clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID.toLong())
            }
        }
        startActivity(intent)

    }

    fun openChartPage(provider: ConsultProvider) {
        var strCompletedByName: String? = ""
        if (provider.getCompleted_by() != null && !TextUtils.isEmpty(provider.getCompleted_by())) {
            strCompletedByName = provider.getCompleted_by()
        }
        val intentConsultChart: Intent
        intentConsultChart = if (provider.getStatus() === Constants.PatientStatus.Completed ||
            provider.getStatus() === Constants.PatientStatus.Discharged
        ) {
            Intent(this, ActivityConsultChartRemote::class.java)
        } else {
            Intent(this, ActivityConsultChart::class.java)
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

        intentConsultChart.putExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT,
            provider.getUrgent())
        if (provider.getStatus() != null) {
            intentConsultChart.putExtra("status", provider.getStatus().toString())
            if (provider.getStatus() === Constants.PatientStatus.Invited ||
                provider.getStatus() === Constants.PatientStatus.Handoff
            ) {
                intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                clearNotifications(provider.patientId)
            } else if (provider.getStatus() === Constants.PatientStatus.Completed ||
                provider.getStatus() === Constants.PatientStatus.Discharged
            ) {
                intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID.)
            }
        }
        startActivity(intentConsultChart)

    }
}

    private fun setMessageCount(model: ConsultProvider) {

    }

    enum class TAB {
   Active,Pending,Complete,Patients
}

/**
     * Accept click listener - Triggering the "acceptInvite" API call
     *
     * @param viewHolder
     * @param consultProvider
     */
    fun acceptClick(viewHolder: PatientListAdapter.ConsultListViewHolder, consultProvider: ConsultProvider, ) {
        BaseActivity().showProgressBar()
        if (consultProvider.getStatus() === Constants.PatientStatus.Invited) {

            val providerId: Long? = HomeActivity().context?.let { PrefUtility().getProviderId(it) }
            val token: String?= HomeActivity().context?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.TOKEN, "") }
            if (providerId != null) {
                if (token != null) {
                    consultProvider.getPatientId()?.let {
                        HomeActivity().viewModel?.acceptInvite(providerId, token, it)
                            ?.observe(this,{

                                //
                                BaseActivity().dismissProgressBar()
                                if (it?.status != null && it.status!!) {

                                    consultProvider.setStatus(Constants.PatientStatus.Active)
                                    //Opening the chart page based on the status of the provider
                                    HomeActivity().openChartPage(consultProvider)
                                } else {
                                    BaseActivity().dismissProgressBar()
                                    viewHolder.itemBinding.inviteBtn.setEnabled(true)
                                    val errMsg: String? = ErrorMessages().getErrorMessage(this,
                                       it?.getErrorMessage(),
                                        Constants.API.acceptInvite)

                                    CustomSnackBar.make(HomeActivity().homeBinding?.idRootLayout,
                                        HomeActivity().activity,
                                        CustomSnackBar.WARNING,
                                        errMsg,
                                        CustomSnackBar.TOP,
                                        3000,
                                        0)?.show()
                                }
                            })
                    }
                }
            }
        } else {
            val uid: Long? =
                HomeActivity().context?.let { PrefUtility().getLongInPref(it, Constants.SharedPrefConstants.USER_ID, 0) }
            val handOffAcceptRequest = HandOffAcceptRequest()
            handOffAcceptRequest.setPatientId(consultProvider.getPatientId())
            handOffAcceptRequest.setProviderId(uid)

            HomeActivity().viewModel?.acceptRemoteHandoff(handOffAcceptRequest)
                ?.observe(this,{


                    BaseActivity().dismissProgressBar()
                    if (it?.status != null && it.status!!) {

                        consultProvider.setStatus(Constants.PatientStatus.Active)
                        //Opening the chart page based on the status of the provider
                        HomeActivity().openChartPage(consultProvider)
                    } else {
                        BaseActivity().dismissProgressBar()
                        viewHolder.itemBinding.inviteBtn.setEnabled(true)
                        val errMsg: String? = ErrorMessages().getErrorMessage(this,
                            it?.getErrorMessage(),
                            Constants.API.acceptInvite)

                        CustomSnackBar.make(HomeActivity().homeBinding?.idRootLayout,
                           HomeActivity().activity,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()
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
    fun reconsultClick(viewHolder: PatientListAdapter.ConsultListViewHolder?, consultProvider: ConsultProvider, ) {
        BaseActivity().showProgressBar("Inviting provider please wait.")
        val providerId: Long?= HomeActivity().context?.let { PrefUtility().getProviderId(it) }
        val token: String? =
            HomeActivity().context?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.TOKEN, "") }
        val chatViewModel: ChatActivityViewModel = ViewModelProvider(this).get(
            ChatActivityViewModel::class.java)
        chatViewModel.inviteProviderBroadCast(providerId, token, consultProvider.getPatientId())
            ?.observe(this, object : Observer<CommonResponse?> {
                override fun onChanged(commonResponse: CommonResponse?) {
                    BaseActivity().dismissProgressBar()

                    if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                        Log.d(HomeActivity().TAG, "Response Broadcast ")
                    } else {

                        val errMsg: String? = ErrorMessages().getErrorMessage(this,
                            commonResponse?.getErrorMessage(),
                            Constants.API.invite)

                        Toast.makeText(HomeActivity().context, errMsg, Toast.LENGTH_SHORT).show()
                    }

                }
            })
    }





    // Migrated to MyProfileActivity
    override fun onNavigationItemSelected(item: View): Boolean {
        val id = item.itemId
        drawerLayout?.closeDrawers()
        when (id) {
            R.id.id_my_consults -> {
            }
            R.id.id_hand_off_patient -> {

//                Log.i(TAG, "On click Handoff Patient: ");
                val intent = Intent(HomeActivity(), HandOffPatientsActivity::class.java)
                startActivity(intent)
            }
            R.id.id_contact_admin -> {

//                Log.i(TAG, "On click Contact admin: ");
                val intent = Intent(HomeActivity(), ContactAdminActivity::class.java)
                startActivity(intent)
            }
            R.id.id_training_materials -> {

                val url: String? = PrefUtility().getStringInPref(this,
                    Constants.SharedPrefConstants.TUTORIAL_URL,
                    "")
                val uri = Uri.parse(url) // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            R.id.id_system_alert -> {


                val intent = Intent(this, SystemAlertActivity::class.java)
                startActivity(intent)
            }
            R.id.id_feedback -> {

//                Log.i(TAG, "On click feedback: ");
//                Uri uri = Uri.parse(Constants.FEEDBACK_URL); // missing 'http://' will cause crashed
                val uri = Uri.parse(strFeedbackForm) // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            R.id.id_change_password -> {

//                Log.i(TAG, "On click change password: ");
                val intent = Intent(this@HomeActivity, ResetPasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.id_logout -> {

//                Log.i(TAG, "On click logout: ");
                handleMultipleClick(item)
                onClickLogout()
            }
        }
        return false
    }
}
    /**
     * Logout click handling
     */
    private fun onClickLogout() {

        LogoutHelper(HomeActivity().activity, HomeActivity().binding?.getRoot()).doLogout()
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<DrawerActivity>.onActivityResult(requestCode, resultCode, data)
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

                    if ("true".equals(save, ignoreCase = true)) {
                        if ("Active".equals(status, ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.Active

                        } else if ("Pending".equals(status, ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.Pending
                            if (PrefUtility().getRole(this)
                                    .equals(Constants.ProviderRole.RD.toString(),ignoreCase = true)
                            ) {
                                filterPatientStatus = Constants.PatientStatus.Invited
                            }

                        } else if ("HomeCare".equals(statusHomeCare, ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.HomeCare

                        } else if (getString(R.string.discharged).equals(status,ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.Completed

                        } else if ("Patients".equals(status, ignoreCase = true)) {
                            filterPatientStatus = Constants.PatientStatus.Patient

                        } else {
                            filterPatientStatus = null

                        }
                        filterList(providerHashMap.values)
                        mAdapter?.notifyDataSetChanged()

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
                            imageBitmap = imageBitmap?.let { getResizedBitmap(it, Constants.IMAGE_MAX_SIZE) }
                            imageBitmap = getRotatedBitmap(uri!!.path, imageBitmap)
                            //Making square image
                            val dimension = Math.min(imageBitmap!!.width, imageBitmap.height)
                            imageBitmap =
                                ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension)
                            storeImgInFirebase(imageBitmap)
                        }
                    } catch (e: Exception) {
//                        Log.e(TAG, "Exception:", e.getCause());
                    }
                }
                Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE -> {
                    try {
                        val uri = data?.data
                        var imageBitmap = getBitmapFromUri(uri)
                        imageBitmap = imageBitmap?.let { getResizedBitmap(it, Constants.IMAGE_MAX_SIZE) }
                        //imageBitmap = getRotatedBitmap(uri.getPath(),imageBitmap);
                        //Making square image
                        val dimension = Math.min(imageBitmap!!.width, imageBitmap.height)
                        imageBitmap =
                            ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension)
                        HomeActivity().storeImgInFirebase(imageBitmap)
                    } catch (e: Exception) {
//                        Log.e(TAG, "Exception:", e.getCause());
                    }
                }
            }
        }
    }

    private fun scrollListToTop() {

      
        if (homeBinding!!.messageRecyclerView != null && mAdapter!!.itemCount >= 0) {
            homeBinding!!.messageRecyclerView.scrollToPosition(0)
        }
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * This method will gets called 2 times
     * first when user comes in home screen we will fetch data in background
     * second once user click on directory navigation drawer we will show the directory which is already
     * loaded ,and fetch directory in background and refresh the directory
     */
    private fun fetchDirectory() {
        DirectoryListHelperOld(binding, viewModel,
            param = object : MembersDialogAdapter.CallbackDirectory() {
                fun onClickProvierItem(var provider: ConsultProvider) {
                    showProgressBar()

                    val providerID: Long? = context?.let { PrefUtility().getProviderId(it) }
                    val token: String? = context?.let {
                        PrefUtility().getStringInPref(it,
                            Constants.SharedPrefConstants.TOKEN, "")
                    }
                    provider.getId()?.let {
                        if (providerID != null) {
                            if (token != null) {
                                HomeActivity(0).viewModel?.startCall(providerID,
                                    token,
                                    it,
                                    0L,
                                    Constants.FCMMessageType.VIDEO_CALL)
                                    ?.observe(this{
                                        dismissProgressBar()
                                        if (commonResponse != null && commonResponse.status != null && commonResponse.st) {

                                            val callScreen = Intent(this, CallActivity::class.java)
                                            callScreen.putExtra("providerName", provider.getName())
                                            callScreen.putExtra("providerId", provider.getId())
                                            callScreen.putExtra("providerHospitalName",
                                                provider.getHospital())
                                            callScreen.putExtra("profilePicUrl",
                                                provider.getProfilePicUrl())
                                            callScreen.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME,
                                                providerID.toString() + "-" + provider.getId())
                                            callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY,
                                                "")
                                            callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                                                getResources().getStringArray(R.array.encryption_mode_values)
                                                    .get(0))
                                            callScreen.putExtra("callType", "outgoing")
                                            val gson = Gson()
                                            val providerList: MutableList<Provider> =
                                                ArrayList<Provider>()
                                            providerList.add(provider)
                                            val selfProvider = Provider()
                                            selfProvider.setId(providerID)
                                            selfProvider.setName(PrefUtility().getStringInPref(this,
                                                Constants.SharedPrefConstants.NAME,
                                                ""))
                                            selfProvider.setProfilePicUrl(PrefUtility().getStringInPref(
                                                this,
                                                Constants.SharedPrefConstants.PROFILE_IMG_URL,
                                                ""))
                                            selfProvider.setHospital(PrefUtility().getStringInPref(
                                                ctx,
                                                Constants.SharedPrefConstants.HOSPITAL_NAME,
                                                ""))
                                            selfProvider.setRole(PrefUtility().getStringInPref(ctx,
                                                Constants.SharedPrefConstants.ROLE,
                                                ""))
                                            providerList.add(selfProvider)
                                            callScreen.putExtra("providerList",
                                                gson.toJson(providerList))
                                            startActivity(callScreen)
                                        } else {
                                            val errMsg: String? =
                                                ErrorMessages().getErrorMessage(this,
                                                    commonResponse?.getErrorMessage(),
                                                    Constants.API.startCall)

                                            CustomSnackBar.make(containerParent,
                                                this,
                                                CustomSnackBar.WARNING,
                                                errMsg,
                                                CustomSnackBar.TOP,
                                                3000,
                                                0).show()
                                        }
                                    })
                            }
                        }


                    }

                    /**
                     * Updating the provider status
                     */
                    /**
                     * Updating the provider status
                     */
                    fun updateProviderStatus() {

                        val dotImg: ImageView? = navHeaderView?.findViewById(R.id.id_dot)
                        val status: String? =
                            PrefUtility().getStringInPref(context,
                                Constants.SharedPrefConstants.PROVIDER_STATUS,
                                "")
                        if (TextUtils.isEmpty(status) || status.equals(Constants.ProviderStatus.OffLine.toString(),
                                ignoreCase = true)
                        ) {

                            dotImg?.setImageResource(R.drawable.ic_grey_circle)
                            homeBinding?.idDot?.setImageResource(R.drawable.ic_grey_circle)
                        } else {

                            dotImg?.setImageResource(R.drawable.ic_green_circle)
                            homeBinding?.idDot?.setImageResource(R.drawable.ic_green_circle)
                        }
                    }

                    // Migrated to MyProfileActivity
                    fun selectImage() {
                        val items =
                            arrayOf<CharSequence>(getString(R.string.take_photo),
                                getString(R.string.select_image))
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle(R.string.add_edit_image)
                        builder.setItems(items) { dialog: DialogInterface?, item: Int ->
                            val intent = Intent(this,
                                ImageCaptureActivity::class.java)
                            if (items[item] == getString(R.string.take_photo)) {

                                val cameraPermission =
                                    ActivityCompat.checkSelfPermission(context,
                                        Manifest.permission.CAMERA)
                                val storagePermission = ActivityCompat.checkSelfPermission(context,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                if (cameraPermission == PackageManager.PERMISSION_DENIED || storagePermission == PackageManager.PERMISSION_DENIED) {
                                    ActivityCompat.requestPermissions(HomeActivity(0),
                                        arrayOf(Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        Constants.PermissionCondes.CAMERA_STORAGE_PERMISSION_CODE)
                                    return@setItems
                                }
                                intent.putExtra(Constants.ImageCaptureConstants.SOURCE,
                                    HomeActivity::class.java.simpleName)
                                intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                                    Constants.ImageCaptureConstants.OPEN_CAMERA)
                                startActivityForResult(intent,
                                    Constants.ImageCaptureConstants.START_CAMERA_REQUEST_CODE)
                            } else if (items[item] == getString(R.string.select_image)) {
                                intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                                    Constants.ImageCaptureConstants.OPEN_MEDIA)
                                startActivityForResult(intent,
                                    Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE)
                            }
                        }
                        builder.show()
                    }

                    // Migrated to MyProfileActivity
                    fun getBitmapFromUri(uri: Uri?): Bitmap? {
                        var parcelFileDescriptor: ParcelFileDescriptor? = null
                        try {
                            parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r")
                            val fileDescriptor = parcelFileDescriptor.fileDescriptor
                            return BitmapFactory.decodeFileDescriptor(fileDescriptor)
                        } catch (e: FileNotFoundException) {
                            //            Log.e(TAG, "Exception:", e.getCause());
                        } finally {
                            if (parcelFileDescriptor != null) {
                                try {
                                    parcelFileDescriptor.close()
                                } catch (e: IOException) {

                                }
                            }
                        }
                        return null
                    }

                    // Migrated to MyProfileActivity
                    fun getRotatedBitmap(photoPath: String?, bitmap: Bitmap?): Bitmap? {
                        var rotatedBitmap: Bitmap? = null
                        try {
                            val ei = ExifInterface(photoPath!!)
                            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED)
                            rotatedBitmap =
                                when (orientation) {
                                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap,
                                        180f)
                                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap,
                                        270f)
                                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                                    else -> bitmap
                                }
                        } catch (e: Exception) {
                            //            Log.e(TAG, "Exception:", e.getCause());
                        }
                        return rotatedBitmap ?: bitmap
                    }

                    // Migrated to MyProfileActivity
                    fun rotateImage(source: Bitmap?, angle: Float): Bitmap {
                        val matrix = Matrix()
                        matrix.postRotate(angle)
                        return Bitmap.createBitmap(source!!, 0, 0, source.width, source.height,
                            matrix, true)
                    }

                    // Migrated to MyProfileActivity
                    fun getResizedBitmap(image: Bitmap?, maxSize: Int): Bitmap? {
                        return try {
                            var width = image!!.width
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

                            image
                        }
                    }

                    // Migrated to MyProfileActivity
                    fun onRequestPermissionsResult(
                        requestCode: Int,
                        permissions: Array<String?>,
                        grantResults: IntArray,
                    ) {
                        super<DrawerActivity>.onRequestPermissionsResult(requestCode,
                            permissions,
                            grantResults)
                        when (requestCode) {
                            Constants.PermissionCondes.CAMERA_STORAGE_PERMISSION_CODE -> {
                                val isGranted: Boolean =
                                    UtilityMethods().checkPermission(this, permissions)
                                if (isGranted) {
                                    val intent = Intent(this, ImageCaptureActivity::class.java)
                                    intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                                        Constants.ImageCaptureConstants.OPEN_CAMERA)
                                    startActivityForResult(intent,
                                        Constants.ActivityRequestCodes.IMAGE_REQ_CODE)
                                } else {
                                    val permissonErr: String = getString(R.string.permission_denied)

                                    CustomSnackBar.make(binding?.container,
                                        this,
                                        CustomSnackBar.WARNING,
                                        permissonErr,
                                        CustomSnackBar.TOP,
                                        3000,
                                        0)?.show()
                                }
                            }
                        }
                    }

                    // Migrated to MyProfileActivity
                    fun storeImgInFirebase(imageBitmap: Bitmap?) {
                        val progressBar: ProgressBar? =
                            navHeaderView?.findViewById(R.id.id_profile_image_pb)
                        progressBar?.visibility = View.VISIBLE
                        val baos = ByteArrayOutputStream()
                        imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val dataArr = baos.toByteArray()
                        val fileName = "image_" + System.currentTimeMillis()
                        val dir: String =
                            Constants.PROFILE_IMG_DIR.toString() + File.separator + fileName
                        val key: String = PrefUtility().getFireBaseUid(context).toString() + ""
                        val storageReference = FirebaseStorage.getInstance()
                            .getReference(mFirebaseUser!!.uid)
                            .child(key)
                            .child(dir)

                        storageReference.putBytes(dataArr)?.addOnCompleteListener(this,
                            OnCompleteListener<UploadTask.TaskSnapshot> { task ->
                                if (task.isSuccessful) {
                                    task.result!!.metadata!!.reference!!.downloadUrl
                                        .addOnCompleteListener(this,
                                            OnCompleteListener<Uri> { task ->
                                                if (task.isSuccessful) {
                                                    val imageURL = task.result.toString()
                                                    PrefUtility().saveStringInPref(context,
                                                        Constants.SharedPrefConstants.PROFILE_IMG_URL,
                                                        imageURL)
                                                    val provider = Provider()
                                                    val providerId: Long? =
                                                        PrefUtility().getProviderId(context)
                                                    val status: String? =
                                                        PrefUtility().getProviderStatus(context)
                                                    provider.setId(providerId)
                                                    provider.setStatus(status)
                                                    provider.setProfilePicUrl(imageURL)
                                                    updateProfileImage(provider, imageBitmap)

                                                }
                                            })
                                } else {

                                }
                            })
                    }

                    /**
                     * Handling the notification click based on the intent
                     *
                     * @param intent
                     */
                    fun handleNotification(intent: Intent) {
                        var intent = intent
                        val userId: Long? = PrefUtility().getProviderId(this)
                        // To login activity if user is null / -1
                        if (userId == null || userId == -1L) {
                            intent = Intent(this, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } // If the intent has "forward" - redirect to chat activity
                        else if (intent.hasExtra("forward")
                            && intent.getStringExtra("forward")
                                .equals(ChatActivity::class.java.getSimpleName(), ignoreCase = true)
                        ) {
                            val chatActIntent = Intent(this, ChatActivity::class.java)
                            chatActIntent.putExtras(intent.extras!!)
                            startActivity(chatActIntent)
                        }
                        // Getting the bundle data
                        val extras = intent.extras
                        if (extras != null) {
                            val select =
                                extras.getString(Constants.IntentKeyConstants.TARGET_PAGE, "")
                            if (select == "pending") {
                                getnewpendingchange()
                                if (homeBinding?.pendingBtnLayout?.getVisibility() === View.VISIBLE) {
                                    homeBinding?.pendingBtnLayout?.performClick()
                                }
                            } else if (select == "completed") {
                                homeBinding?.completedBtnLayout?.performClick()
                            } else if (select == "active") {
                                homeBinding?.activeBtnLayout?.performClick()
                            }
                        }
                    }

                    /**
                     * Clearing then notification with dedicated notification ID
                     *
                     * @param notificationId
                     */
                    /**
                     * Clearing then notification with dedicated notification ID
                     *
                     * @param notificationId
                     */
                    fun clearNotifications(notificationId: Long?) {
                        NotificationHelper(context, null).clearNotification(notificationId)
                    }

                    /**
                     * Clearing all notfications
                     */
                    private fun clearAllNotifications() {
                        NotificationHelper(context, null).clearAllNotification()
                    }


                    /**
                     * Handling multiple click event with view
                     *
                     * @param view
                     */
                    fun handleMultipleClick(view: View) {
                        view.isEnabled = false
                        mHandler?.postDelayed({ view.isEnabled = true }, 500)
                    }


                    /**
                     * Handling multiple click event with menu item
                     *
                     * @param item
                     */
                    fun handleMultipleClick(item: MenuItem) {
                        item.isEnabled = false
                        mHandler?.postDelayed({ item.setEnabled(true) }, 500)
                    }

                    /**
                     * Scroll to top of the list
                     */

                    fun scrollListToTop() {

                        if (homeBinding?.messageRecyclerView != null && mAdapter?.getItemCount() >= 0) {
                            homeBinding?.messageRecyclerView!!.scrollToPosition(0)
                        }
                        mAdapter?.notifyDataSetChanged()
                    }


                    /**
                     * Handling tab click listener
                     */
                    fun handleTabButtons() {
                        HomeActivity(0).homeBinding?.activeBtnLayout?.setOnClickListener(
                            HomeActivity(
                                0).tabChangeListener)
                        HomeActivity(0).homeBinding?.completedBtnLayout?.setOnClickListener(
                            HomeActivity(
                                0).tabChangeListener)
                        HomeActivity(0).homeBinding?.pendingBtnLayout?.setOnClickListener(
                            HomeActivity(
                                0).tabChangeListener)
                    }

                    /**
                     * Chat view click listener
                     *
                     * @param position
                     * @param provider
                     */
                    /**
                     * Chat view click listener
                     *
                     * @param position
                     * @param provider
                     */
                    fun onClickChatView(position: Int, provider: ConsultProvider) {
                        //String path = mPath +  "/" +mFirebaseAdapter.getRef(position).getKey();
                        //        startChat(mAdapter.getItem(position));
                        //        consultDetailsDialog(this, mAdapter.getItem(position));
                        //Opening the chart page based on the status of the provider
                        HomeActivity(0).openChartPage(provider)
                    }

                    /**
                     * Invite button click listener
                     *
                     * @param viewHolder
                     */
                    /**
                     * Invite button click listener
                     *
                     * @param viewHolder
                     */
                    fun onClickInviteBtn(viewHolder: PatientListAdapter.ConsultListViewHolder) {
                        viewHolder.itemBinding.inviteBtn.setEnabled(false)
                        HomeActivity(0).mAdapter?.getItem(viewHolder.getAdapterPosition())
                            ?.let { acceptClick(viewHolder, it) }
                        clearNotifications(Constants.NotificationIds.NOTIFICATION_ID)
                    }


                    /**
                     * Details button click listener
                     *
                     * @param viewHolder
                     */
                    fun onClickDetailsButton(viewHolder: PatientListAdapter.ConsultListViewHolder) {
                        handleMultipleClick(viewHolder.itemBinding.detailsBtn)
                        val provider: ConsultProvider? =
                            HomeActivity().mAdapter?.getItem(viewHolder.getAdapterPosition())
                        //Opening the chart page based on the status of the provider
                        if (provider != null) {
                            HomeActivity().openChartPage(provider)
                        }
                    }

                    /**
                     * Reconsult button click listener
                     *
                     * @param viewHolder
                     */
                    /**
                     * Reconsult button click listener
                     *
                     * @param viewHolder
                     */
                    fun onClickReconsultButton(viewHolder: PatientListAdapter.ConsultListViewHolder) {
                        handleMultipleClick(viewHolder.itemBinding.reconsultBtn)

                        HomeActivity(0).mAdapter?.getItem(viewHolder.getAdapterPosition())
                            ?.let { reconsultClick(viewHolder, it) }
                    }

                    /**
                     * Arrow drop down click listener
                     *
                     * @param position
                     */
                    /**
                     * Arrow drop down click listener
                     *
                     * @param position
                     */
                    fun onArrowDropDownClick(position: Int) {
                        HomeActivity().mAdapter?.getItem(position)
                            ?.let { HomeActivity().consultDetailsDialog(HomeActivity().context, it) }
                    }


                    fun showAcuityResetConfirmDialog(position: Int) {

                    }



                    /**
                     * Reset acuity click listener
                     *
                     * @param position
                     */
                    fun onResetAcuityClick(position: Int) {

                        showAcuityResetConfirmDialog(position)
                    }


                }





