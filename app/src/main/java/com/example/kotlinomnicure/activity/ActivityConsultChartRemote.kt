package com.example.kotlinomnicure.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewStub
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import com.example.kotlinomnicure.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ActivityConsultChartRemote : AppCompatActivity() {
 private  val  TAG:/*@@hclxmo@@*/kotlin.String? = /*@@yidlyd@@*/com.mvp.omnicure.activity.ActivityConsultChartRemote::class.java.getSimpleName()
    protected  var  binding:/*@@hbbbpv@@*/ActivityEconsultChartRemoteBinding? = null
      var  membersList:/*@@fuaqyv@@*/kotlin.collections.MutableList</*@@nubbmu@@*/Members?>? = java.util.ArrayList</*@@tbrboz@@*/Members?>()
      var  errorTeams:/*@@hclxmo@@*/kotlin.String? = null
      var  mUnreadMessageListener:/*@@mkgipw@@*/ValueEventListener? = object : ValueEventListener{
    @RequiresApi(api = android.os.Build.VERSION_CODES.KITKAT)  open  fun /*@@cpdbzc@@*/onDataChange(  dataSnapshot:/*@@jhuvat@@*/DataSnapshot){
          var  consultProviderA: /*@@xiuews@@*/ConsultProvider? = dataSnapshot.getValue(/*@@addzms@@*/ConsultProvider::class.java)
        //            System.out.println("snapshot data " + consultProviderA);
//            if (consultProviderA != null && !consultProviderA.equals("null")) {
        if (consultProviderA != null){
            if (consultProviderA.getUnread() > 0){
//                    Log.d(TAG, "Unread Message Count" + consultProviderA.getUnread());
//                mFirebaseDatabaseReference.child("providers").child(mProviderUid).child("active").child(mConsultProviderKey).child("unread").setValue(0);
            binding.imgMessageAlert.setVisibility(android.view.View.VISIBLE)
            } else {
                binding.imgMessageAlert.setVisibility(android.view.View.GONE)
            }
        }
    }
     open  fun /*@@bpsvxb@@*/onCancelled(  databaseError:/*@@lyrtym@@*/DatabaseError){
//            Log.d(TAG, "onCancelled: of value event listener " + databaseError);
    }
}
      var  statusStub:/*@@peethb@@*/RelativeLayout? = null
    private  var  uid:/*@@chfgmv@@*/kotlin.Long = 0
    private  var  viewModel:/*@@uigcer@@*/PatientDetailViewModel? = null
    private  var  patientDetails:/*@@khjaqr@@*/PatientDetail? = null
    private  var  strPhone:/*@@hclxmo@@*/kotlin.String? = null
    private  var  strDob:/*@@hclxmo@@*/kotlin.String? = null
    private  var  strGender:/*@@hclxmo@@*/kotlin.String? = null, private  var  strWard:/*@@hclxmo@@*/kotlin.String? = null
    private  var  mConsultProvider:/*@@eerufm@@*/ConsultProvider? = null
    private  var  mConsultProviderKey:/*@@hclxmo@@*/kotlin.String? = null
    private  var  strProviderNameType:/*@@hclxmo@@*/kotlin.String? = null, private  var  strTime:/*@@hclxmo@@*/kotlin.String? = null, private  var  strStatus:/*@@hclxmo@@*/kotlin.String? = null
    private  var  stub:/*@@xqkhgs@@*/ViewStub? = null
    private  var  strMessageReaded:/*@@hclxmo@@*/kotlin.String? = null
    private  var  mUnreadMessageDB:/*@@uvaumh@@*/DatabaseReference? = null
    private  var  mFirebaseDatabaseReference:/*@@uvaumh@@*/DatabaseReference? = null

    protected open  fun /*@@vigkuz@@*/onCreate(  savedInstanceState:/*@@icdkzv@@*/Bundle?){
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_econsult_chart_remote)
    viewModel = ViewModelProviders.of(this).get(/*@@fjpdjk@@*/PatientDetailViewModel::class.java)
    initViews()
    initOnClickListener()
}

    private open  fun /*@@iqkrbi@@*/initViews(){
    stub = findViewById(R.id.layout_stub_view) as /*@@xqkhgs@@*/ViewStub?
    statusStub = findViewById(R.id.status_stub) as /*@@peethb@@*/RelativeLayout?
    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()

//        uid = PrefUtility.getLongInPref(ActivityConsultChart.this, Constants.SharedPrefConstants.USER_ID, 0);
    uid = getIntent().getLongExtra("uid", 0)
            /*if (PrefUtility.getStringInPref(this, Constants.SharedPrefConstants.ROLE, "").equals("RD")) {
            binding.llCallLayout.setVisibility(View.GONE);
        } else {
            binding.llCallLayout.setVisibility(View.GONE);
        }*/binding.llCallLayout.setVisibility(android.view.View.GONE)
    setProviderObject()
      var  role: /*@@hclxmo@@*/kotlin.String? = PrefUtility.getRole(this@ActivityConsultChartRemote )

//        if (role.equalsIgnoreCase(Constants.ProviderRole.BD.toString())) {
//            if (mConsultProvider.getStatus().equals(Constants.PatientStatus.Completed)) {
//                binding.floatFab.setVisibility(View.VISIBLE);
//            } else {
//                binding.floatFab.setVisibility(View.GONE);
//            }
//        } else {
//            binding.floatFab.setVisibility(View.GONE);
//        }
    getPatientDetails(uid)
}

    private open  fun /*@@nhujle@@*/setProviderObject(){
    mConsultProvider = ConsultProvider()
    strProviderNameType = getIntent().getStringExtra("providerNameType")
    strTime = getIntent().getStringExtra("completedTime")
    strStatus = getIntent().getStringExtra("status")
    //        Log.d(TAG, "strstatus" + strStatus);
    mConsultProviderKey = getIntent().getStringExtra("consultProviderId")
    mConsultProvider.setId(getIntent().getStringExtra("consultProviderPatientId").toLong())
    mConsultProvider.setPatientsId(getIntent().getStringExtra("consultProviderPatientId").toLong())
    mConsultProvider.setText(getIntent().getStringExtra("consultProviderText"))
    mConsultProvider.setName(getIntent().getStringExtra("consultProviderName"))
    mConsultProvider.setUnread(getIntent().getIntExtra("unreadMessageCount", 0))
      var  dob: /*@@chfgmv@@*/kotlin.Long = getIntent().getLongExtra("dob", -1)
    mConsultProvider.setDob(dob)
      var  gender: /*@@hclxmo@@*/kotlin.String? = getIntent().getStringExtra("gender")
    mConsultProvider.setGender(gender)
      var  note: /*@@hclxmo@@*/kotlin.String? = getIntent().getStringExtra("note")
    mConsultProvider.setNote(note)
      var  status: /*@@hclxmo@@*/kotlin.String? = getIntent().getStringExtra("status")
    if (!TextUtils.isEmpty(status)){
        mConsultProvider.setStatus(Constants.PatientStatus.valueOf(status))
    }
    mConsultProvider.setUrgent(getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT, false))
    mConsultProvider.setTeamName(getIntent().getStringExtra("teamNameConsult"))
    if (getIntent().hasExtra("phone")){
        mConsultProvider.setPhone(getIntent().getStringExtra("phone"))
    }
    if (mConsultProvider.getUnread() > 0){
        binding.imgMessageAlert.setVisibility(android.view.View.VISIBLE)
    } else {
        binding.imgMessageAlert.setVisibility(android.view.View.GONE)
    }
}

    private open  fun /*@@egtena@@*/initOnClickListener(){
    binding.imgBack.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@gubeik@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            finish()
        }
    })
    binding.imgDetailUp.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@liqgjb@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            binding.imgDetailUp.setVisibility(android.view.View.GONE)
            binding.imgDetailDown.setVisibility(android.view.View.VISIBLE)
            binding.llComplaints.setVisibility(android.view.View.GONE)
        }
    })
    binding.imgDetailDown.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@lpwfar@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            binding.imgDetailDown.setVisibility(android.view.View.GONE)
            binding.imgDetailUp.setVisibility(android.view.View.VISIBLE)
            binding.llComplaints.setVisibility(android.view.View.VISIBLE)
        }
    })
    binding.imgVitalUp.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@yuiywg@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            binding.imgVitalUp.setVisibility(android.view.View.GONE)
            binding.imgVitalDown.setVisibility(android.view.View.VISIBLE)
            binding.llTimeZone.setVisibility(android.view.View.GONE)
            binding.llVital.setVisibility(android.view.View.GONE)
        }
    })
    binding.imgVitalDown.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@wkptpz@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            binding.imgVitalDown.setVisibility(android.view.View.GONE)
            binding.imgVitalUp.setVisibility(android.view.View.VISIBLE)
            binding.llTimeZone.setVisibility(android.view.View.VISIBLE)
            binding.llVital.setVisibility(android.view.View.VISIBLE)
        }
    })
    binding.llMessage.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@tmbpyd@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
              var  intentConsultChart: /*@@wogojo@@*/Intent? = Intent(this@ActivityConsultChartRemote , /*@@feqanq@@*/ChatActivity::class.java)
            intentConsultChart.putExtra("uid", mConsultProvider.getId())
            intentConsultChart.putExtra("path", "consults/" + mConsultProvider.getId())
            intentConsultChart.putExtra("consultProviderId", "" + mConsultProviderKey)
            intentConsultChart.putExtra("consultProviderPatientId", "" + mConsultProvider.getId())
            intentConsultChart.putExtra("consultProviderText", mConsultProvider.getText())
            intentConsultChart.putExtra("consultProviderName", mConsultProvider.getName())
            intentConsultChart.putExtra("dob", mConsultProvider.getDob())
            intentConsultChart.putExtra("gender", mConsultProvider.getGender())
            intentConsultChart.putExtra("note", mConsultProvider.getNote())
            intentConsultChart.putExtra("phone", mConsultProvider.getPhone())
            intentConsultChart.putExtra("patientId", mConsultProvider.getPatientsId())
            intentConsultChart.putExtra("ConsultChartRemote", "chartRemote")
            intentConsultChart.putExtra("status", mConsultProvider.getStatus())
            intentConsultChart.putExtra("teamNameConsult", mConsultProvider.getTeamName())

//                Log.d(TAG, "mConsultProviderKey : " + mConsultProvider.getId());
            intentConsultChart.putExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT, mConsultProvider.getUrgent())
            if (mConsultProvider.getStatus() != null){
                intentConsultChart.putExtra("status", mConsultProvider.getStatus().toString())
                if (mConsultProvider.getStatus() === Constants.PatientStatus.Invited ||
                mConsultProvider.getStatus() === Constants.PatientStatus.Handoff){
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                    clearNotifications(mConsultProvider.getId().intValue())
                } else if (mConsultProvider.getStatus() === Constants.PatientStatus.Completed ||
                mConsultProvider.getStatus() === Constants.PatientStatus.Discharged){
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
                }
            }
            startActivityForResult(intentConsultChart, 1)
        }
    })
    binding.txtContactTeam.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@rhjguc@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            if (membersList.size > 0 && membersList != null){
                showTeamMembersDialog(this@ActivityConsultChartRemote , membersList, mConsultProvider)
            } else {
//                    UtilityMethods.showErrorSnackBar(binding.containerLayout, errorTeams, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding.containerLayout, this@ActivityConsultChartRemote , CustomSnackBar.WARNING, errorTeams, CustomSnackBar.TOP, 3000, 0).show()
            }
        }
    })
    if (strProviderNameType != null && (!TextUtils.isEmpty(strProviderNameType))){
//            binding.txtProviderName.setText(strProviderNameType);
      var  vals: /*@@dejjqg@@*/kotlin.Array</*@@hclxmo@@*/kotlin.String?>? = strProviderNameType.split(",").toTypedArray()
          var  providerName: /*@@hclxmo@@*/kotlin.String? = ""
        if (vals.size > 1 && vals.get(0).length > 12){
            vals.get(0) = vals.get(0).substring(0, 12) + ".."
        }
        providerName = TextUtils.join(",", vals)
        providerName = providerName.substring(0, 1).toUpperCase() + providerName.substring(1)
        binding.txtProviderName.setText(providerName)
    }
    if (!(strTime == "null")){
        binding.txtTime.setText(Utils.timestampToDate(strTime.toLong()))
    }
    if (strStatus != null){
        if (strStatus.equals("Completed", ignoreCase = true)){
//                binding.txtStatus.setText("Completed");
        binding.txtStatus.setText(getApplicationContext().getResources().getString(R.string.completed))
        } else if (strStatus.equals("Discharged", ignoreCase = true)){
//                binding.txtStatus.setText("Discharged");
        binding.txtStatus.setText(getApplicationContext().getResources().getString(R.string.discharged))
        }
    }
    binding.floatFab.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@pgzsxt@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            handleMultipleClick(v)
              var  role: /*@@hclxmo@@*/kotlin.String? = PrefUtility.getRole(this@ActivityConsultChartRemote )
            if (role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)){
                MoreOptionBPDialog(this@ActivityConsultChartRemote )
            } else {
                MoreOptionRPDialog(this@ActivityConsultChartRemote )
            }
        }
    })
}

     open  fun /*@@cxeiad@@*/onStop(){
    super.onStop()
    mUnreadMessageDB.removeEventListener(mUnreadMessageListener)
}

    private open  fun /*@@bzwdkt@@*/handleMultipleClick(  view:/*@@evgecb@@*/android.view.View?){
    view.setEnabled(false)
    android.os.Handler().postDelayed(/*@@jlguhq@@*/java.lang.Runnable ({view.setEnabled(true)}), 500)
}

    protected open  fun /*@@yxxzrj@@*/onActivityResult(  requestCode:/*@@leoahi@@*/Int,   resultCode:/*@@leoahi@@*/Int,   data:/*@@zkqhsd@@*/Intent?){
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 1){
        if (resultCode == RESULT_OK){
            if (data != null){
                strMessageReaded = data.getStringExtra("messageValue")
            }
            if (!TextUtils.isEmpty(strMessageReaded)){
                binding.imgMessageAlert.setVisibility(android.view.View.GONE)
            } else {
                binding.imgMessageAlert.setVisibility(android.view.View.VISIBLE)
            }
        }
    }
}

    private open  fun /*@@iiooxs@@*/getTeamMemberDetails(  patientId:/*@@qhefrk@@*/kotlin.Long?,   teamName:/*@@hclxmo@@*/kotlin.String?){
    membersList.clear()
    //        Long patID = Long.valueOf(mConsultProvider.getPatientsId());
        //getPatientsId() is returning Long no need to convert it
      var  patID: /*@@qhefrk@@*/kotlin.Long? = mConsultProvider.getPatientsId()
    viewModel.getMemberList(patID, teamName).observe(this, {
      response -> dismissProgressBar()
    if ((response != null) && (response.getStatus() != null) && response.getStatus()){
        if (response.getTeamDetails().getMembers() != null){
            membersList.addAll(response.getTeamDetails().getMembers())
        }
    } else {
        errorTeams = response.getErrorMessage()
    }
    })
}

    private open  fun /*@@jidajz@@*/clearNotifications(  notificationId:/*@@leoahi@@*/Int){
    NotificationHelper(this).clearNotification(notificationId)
}

     open  fun /*@@zweizn@@*/onStart(){
    super.onStart()
    try {
        unReadMessageListener()
    }catch (  e:/*@@rhtthd@@*/java.lang.Exception){
//            Log.i(TAG, "onStart: EXCEPTION " + e.getMessage());
    }
}

    private open  fun /*@@wmhoou@@*/unReadMessageListener(){
//        Log.d(TAG, "unReadMessageListener: ");
  var  mProviderUid: /*@@hclxmo@@*/kotlin.String? = PrefUtility.getFireBaseUid(this) //mFirebaseUser.getUid();
    mUnreadMessageDB = mFirebaseDatabaseReference.child("providers").child(mProviderUid).child("active").child(mConsultProviderKey)
    mUnreadMessageDB.addValueEventListener(mUnreadMessageListener)
}

    private open  fun /*@@awleck@@*/getPatientDetails(  uid:/*@@chfgmv@@*/kotlin.Long){
    showProgressBar(PBMessageHelper.getMessage(this, Constants.API.getPatientDetails.toString()))
    viewModel.getPatienDetails(uid).observe(this, {
      response ->
//            Long patID = Long.valueOf(mConsultProvider.getPatientsId());
            //getPatientsId() is returning Long no need to convert it
      var  patID: /*@@qhefrk@@*/kotlin.Long? = mConsultProvider.getPatientsId()

//            Log.d(TAG, "Patient Details Res" + new Gson().toJson(response));
    if (response != null && response.getStatus()){
        patientDetails = response
        populateConsultDetails()
        getTeamMemberDetails(patID, "Team " + patientDetails.getPatient().getTeamName())
    } else {
        dismissProgressBar()
          var  errMsg: /*@@hclxmo@@*/kotlin.String? = ErrorMessages.getErrorMessage(this, java.lang.String.valueOf(response.getErrorId()), Constants.API.getHospital)
        //                UtilityMethods.showErrorSnackBar(binding.containerLayout, errMsg, Snackbar.LENGTH_LONG);
        CustomSnackBar.make(binding.containerLayout, this, CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0).show()
    }
    })
}

    private open  fun /*@@xenxxu@@*/populateConsultDetails(){
      var  heartRateValue: /*@@hclxmo@@*/kotlin.String? = patientDetails.getPatient().getHeartRateValue()
      var  highBPValue: /*@@hclxmo@@*/kotlin.String? = patientDetails.getPatient().getArterialBloodPressureSystolicValue()
      var  lowBPValue: /*@@hclxmo@@*/kotlin.String? = patientDetails.getPatient().getArterialBloodPressureDiastolicValue()
      var  spo2Value: /*@@hclxmo@@*/kotlin.String? = patientDetails.getPatient().getSpO2Value()
      var  respRateValue: /*@@hclxmo@@*/kotlin.String? = patientDetails.getPatient().getRespiratoryRateValue()
      var  fiO2Value: /*@@hclxmo@@*/kotlin.String? = patientDetails.getPatient().getFio2Value()
      var  tempValue: /*@@hclxmo@@*/kotlin.String? = patientDetails.getPatient().getTemperatureValue()
      var  provider: /*@@vjrbmq@@*/ConsultProvider? = ConsultProvider()
    if (!TextUtils.isEmpty(heartRateValue)){
        provider.setHeartRate(patientDetails.getPatient().getHeartRate())
    }
    if (!TextUtils.isEmpty(highBPValue)){
        provider.setArterialBloodPressureSystolic(patientDetails.getPatient().getArterialBloodPressureSystolic())
    }
    if (!TextUtils.isEmpty(lowBPValue)){
        provider.setArterialBloodPressureDiastolic(patientDetails.getPatient().getArterialBloodPressureDiastolic())
    }
    if (!TextUtils.isEmpty(spo2Value)){
        provider.setSpO2(patientDetails.getPatient().getSpO2())
    }
    if (!TextUtils.isEmpty(fiO2Value)){
        provider.setFiO2(patientDetails.getPatient().getFiO2())
    }
    if (!TextUtils.isEmpty(respRateValue)){
        provider.setRespiratoryRate(patientDetails.getPatient().getRespiratoryRate())
    }
    if (!TextUtils.isEmpty(tempValue)){
        provider.setTemperature(patientDetails.getPatient().getTemperature())
    }
    provider.setOxygenSupplement(patientDetails.getPatient().isOxygenSupplement())
    if (!TextUtils.isEmpty(patientDetails.getPatient().getPatientCondition())){
        if (patientDetails.getPatient().getPatientCondition().equalsIgnoreCase(java.lang.String.valueOf(Constants.PatientCondition.Alert))){
            provider.setPatientCondition(Constants.PatientCondition.Alert)
        } else if (patientDetails.getPatient().getPatientCondition().equalsIgnoreCase(java.lang.String.valueOf(Constants.PatientCondition.Voice))){
            provider.setPatientCondition(Constants.PatientCondition.Voice)
        } else if (patientDetails.getPatient().getPatientCondition().equalsIgnoreCase(java.lang.String.valueOf(Constants.PatientCondition.Pain))){
            provider.setPatientCondition(Constants.PatientCondition.Pain)
        } else if (patientDetails.getPatient().getPatientCondition().equalsIgnoreCase(java.lang.String.valueOf(Constants.PatientCondition.Unresponsive))){
            provider.setPatientCondition(Constants.PatientCondition.Unresponsive)
        }
    }
    UtilityMethods.displayVitals(this, stub, provider)
    UtilityMethods.displayPatientStatusComponent(this, statusStub, mConsultProvider.getUrgent(),
    (mConsultProvider.getStatus() === Constants.PatientStatus.Pending), Constants.AcuityLevel.valueOf(patientDetails.getPatient().getScore()))
    if ((mConsultProvider.getStatus() === Constants.PatientStatus.Pending)){
        binding.txtContactTeam.setVisibility(android.view.View.GONE)
    }
    if (!TextUtils.isEmpty(patientDetails.getPatient().getRecordNumber())){
        binding.txtMRNNumber.setText(android.text.Html.fromHtml("MRN&nbsp;" + patientDetails.getPatient().getRecordNumber()))
    } else {
        binding.txtMRNNumber.setText("MRN ")
    }
    binding.txtPatientName.setText(patientDetails.getPatient().getFname().toString() + " " + patientDetails.getPatient().getLname())
      var  dot: /*@@hclxmo@@*/kotlin.String? = " <b>\u00b7</b> "
    if (!TextUtils.isEmpty(patientDetails.getPatient().getDob())){
        strDob = dot + getDob()
    }
    if (!TextUtils.isEmpty(patientDetails.getPatient().getGender())){
        strGender = patientDetails.getPatient().getGender()
        if (strGender.equals("Male", ignoreCase = true)){
            strGender = dot + "M"
        } else if (strGender.equals("Female", ignoreCase = true)){
            strGender = dot + "F"
        }
    }
    if (((patientDetails.getPatient().getPhone() != null) && !TextUtils.isEmpty(patientDetails.getPatient().getPhone()) &&
    (!patientDetails.getPatient().getPhone().equalsIgnoreCase("null")))){
        strPhone = dot + patientDetails.getPatient().getPhone()
    } else {
        strPhone = ""
    }
    binding.txtAge.setText(android.text.Html.fromHtml(getAge() + strGender + strDob + strPhone))
    if (!TextUtils.isEmpty(patientDetails.getPatient().getWardName().trim()) &&
    (!patientDetails.getPatient().getWardName().equalsIgnoreCase("null"))){
        strWard = dot + patientDetails.getPatient().getWardName()
    } else {
        strWard = ""
    }
    if (!TextUtils.isEmpty(patientDetails.getPatient().getHospital())){
        binding.txtLocation.setText(android.text.Html.fromHtml(patientDetails.getPatient().getHospital().toString() + strWard))
    }
    if (!TextUtils.isEmpty(patientDetails.getPatient().getNote())){
          var  strComplaint: /*@@hclxmo@@*/kotlin.String? = patientDetails.getPatient().getNote()
          var  stringComplaint: /*@@hclxmo@@*/kotlin.String? = strComplaint
        if (strComplaint.contains(":")){
            stringComplaint = strComplaint.substring(strComplaint.indexOf(":") + 1)
        }
        binding.txtComplaintDetail.setText(stringComplaint.trim({it <= ' '}))
    }
    if ((patientDetails.getPatient().getSyncTime() != null) && !(patientDetails.getPatient().getSyncTime().equals("0"))){
        binding.txtTimeZone.setText(Utils.timestampToDate(patientDetails.getPatient().getSyncTime().toLong()))
    } else {
        binding.txtTimeZone.setText(" - ")
    }
}

    private open  fun /*@@pihcoe@@*/getDob(): /*@@hclxmo@@*/kotlin.String?{
      var  timeInMillis: /*@@qhefrk@@*/kotlin.Long? = java.lang.Long.valueOf(patientDetails.getPatient().getDob())
      var  dateString: /*@@hclxmo@@*/kotlin.String? = java.text.SimpleDateFormat("MM-dd-yyyy").format(java.util.Date(timeInMillis))
    return dateString
}

    private open  fun /*@@iefedl@@*/getAge(): /*@@hclxmo@@*/kotlin.String?{
      var  calendar: /*@@oqhrgs@@*/java.util.Calendar? = java.util.Calendar.getInstance()
      var  year: /*@@leoahi@@*/Int = calendar.get(java.util.Calendar.YEAR)
    calendar.setTimeInMillis(patientDetails.getPatient().getDob().toLong())
      var  agee: /*@@leoahi@@*/Int = year - calendar.get(java.util.Calendar.YEAR)
      var  age: /*@@hclxmo@@*/kotlin.String? = agee.toString()
    return age
}

     /**
 * More option dialog for RP
 *
 * @param context
 */
 open  fun /*@@xyailz@@*/MoreOptionRPDialog(  context:/*@@hjnqpc@@*/android.content.Context?){
      var  dialog: /*@@dzzode@@*/android.app.Dialog? = android.app.Dialog(context, R.style.Theme_Dialog)
    dialog.setContentView(R.layout.rp_more_option_dialog)
    dialog.setCancelable(false)
    dialog.setCanceledOnTouchOutside(false)
    dialog.getWindow().setGravity(Gravity.BOTTOM)
    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    dialog.getWindow().getAttributes().windowAnimations = R.style.SlideUpDialog
      var  rlHandOffPatient: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rlHandOffPatient) as /*@@peethb@@*/RelativeLayout?
      var  rlCompletedConsultation: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rlCompletedConsultation) as /*@@peethb@@*/RelativeLayout?
      var  rlCreateProgress: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rlCreateProgress) as /*@@peethb@@*/RelativeLayout?
      var  rleNotes: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rleNotes) as /*@@peethb@@*/RelativeLayout?
      var  rlActivityLogs: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rlActivityLogs) as /*@@peethb@@*/RelativeLayout?
      var  imgCancel: /*@@yfsvbv@@*/android.widget.ImageView? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.imgCancel) as /*@@yfsvbv@@*/android.widget.ImageView?
    rlHandOffPatient.setVisibility(android.view.View.GONE)
    rlCompletedConsultation.setVisibility(android.view.View.GONE)
    rlCreateProgress.setVisibility(android.view.View.GONE)
      var  role: /*@@hclxmo@@*/kotlin.String? = PrefUtility.getRole(this)
      var  strRpUserType: /*@@hclxmo@@*/kotlin.String? = PrefUtility.getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
    if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true) && (strRpUserType == "MD/DO")){
        rlCreateProgress.setVisibility(android.view.View.VISIBLE)
    }
    imgCancel.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@hprnjc@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            dialog.dismiss()
        }
    })
    rleNotes.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@crhndd@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            handleMultipleClick(v)
              var  intent: /*@@wogojo@@*/Intent? = Intent(this@ActivityConsultChartRemote , /*@@efwpgd@@*/ActivityENotes::class.java)
            intent.putExtra("patient_id", mConsultProvider.getPatientsId())
            intent.putExtra("patient_name", patientDetails.getPatient().getName())
            intent.putExtra("patient_status", mConsultProvider.getStatus().toString())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, "completedchart")
            startActivity(intent)
            dialog.dismiss()
        }
    })
    rlActivityLogs.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@hacmpy@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            handleMultipleClick(v)
              var  intent: /*@@wogojo@@*/Intent? = Intent(this@ActivityConsultChartRemote , /*@@gtgykz@@*/ActivityLog::class.java)
            intent.putExtra("patient_id", mConsultProvider.getPatientsId())
            intent.putExtra("patient_name", patientDetails.getPatient().getName())
            startActivity(intent)
            dialog.dismiss()
        }
    })
    rlCreateProgress.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@wecnml@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            handleMultipleClick(v)
              var  intent: /*@@wogojo@@*/Intent? = Intent(this@ActivityConsultChartRemote , /*@@sghgkg@@*/AddProgressENoteActivity::class.java)
            intent.putExtra("patient_id", mConsultProvider.getPatientsId())
            intent.putExtra("patient_name", patientDetails.getPatient().getName())
            intent.putExtra("patient_status", mConsultProvider.getStatus().toString())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, "chart")
            startActivity(intent)
            dialog.dismiss()
        }
    })
    dialog.show()
}

     open  fun /*@@oxagpr@@*/MoreOptionBPDialog(  context:/*@@hjnqpc@@*/android.content.Context?){
      var  dialog: /*@@dzzode@@*/android.app.Dialog? = android.app.Dialog(context, R.style.Theme_Dialog)
    dialog.setContentView(R.layout.bp_more_option_dialog)
    dialog.setCancelable(false)
    dialog.setCanceledOnTouchOutside(false)
    dialog.getWindow().setGravity(Gravity.BOTTOM)
    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    dialog.getWindow().getAttributes().windowAnimations = R.style.SlideUpDialog
      var  rlResetAcuity: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rlResetAcuity) as /*@@peethb@@*/RelativeLayout?
      var  rlTransferPatient: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rlTransferPatient) as /*@@peethb@@*/RelativeLayout?
      var  rlDischargePatient: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rlDischargePatient) as /*@@peethb@@*/RelativeLayout?
      var  rleNotes: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rleNotes) as /*@@peethb@@*/RelativeLayout?
      var  rlActivityLogs: /*@@peethb@@*/RelativeLayout? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.rlActivityLogs) as /*@@peethb@@*/RelativeLayout?
      var  imgCancel: /*@@yfsvbv@@*/android.widget.ImageView? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.imgCancel) as /*@@yfsvbv@@*/android.widget.ImageView?
    if (mConsultProvider.getStatus().equals(Constants.PatientStatus.Completed)){
        rlResetAcuity.setVisibility(android.view.View.GONE)
        rlTransferPatient.setVisibility(android.view.View.VISIBLE)
        rlDischargePatient.setVisibility(android.view.View.VISIBLE)
    } else {
        rlResetAcuity.setVisibility(android.view.View.GONE)
        rlTransferPatient.setVisibility(android.view.View.GONE)
        rlDischargePatient.setVisibility(android.view.View.GONE)
    }
    imgCancel.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@xmtneg@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            dialog.dismiss()
        }
    })
    rlResetAcuity.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@yjmeep@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){}
    })
    rlTransferPatient.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@ndrbis@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
              var  intent: /*@@wogojo@@*/Intent? = Intent(this@ActivityConsultChartRemote , /*@@vhauey@@*/TransferPatientActivity::class.java)
            intent.putExtra("patientId", mConsultProvider.getPatientsId())
            startActivity(intent)
            dialog.dismiss()
        }
    })
    rlDischargePatient.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@geysbf@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            doDischargePatient()
            dialog.dismiss()
        }
    })
    rleNotes.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@gvsies@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            handleMultipleClick(v)
              var  intent: /*@@wogojo@@*/Intent? = Intent(this@ActivityConsultChartRemote , /*@@zonhiu@@*/ActivityENotes::class.java)
            intent.putExtra("patient_id", mConsultProvider.getPatientsId())
            intent.putExtra("patient_name", patientDetails.getPatient().getName())
            intent.putExtra("patient_status", mConsultProvider.getStatus().toString())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, "completed")
            startActivity(intent)
            dialog.dismiss()
        }
    })
    rlActivityLogs.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@dujypl@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            handleMultipleClick(v)
              var  intent: /*@@wogojo@@*/Intent? = Intent(this@ActivityConsultChartRemote , /*@@flsdue@@*/ActivityLog::class.java)
            intent.putExtra("patient_id", mConsultProvider.getPatientsId())
            intent.putExtra("patient_name", patientDetails.getPatient().getName())
            startActivity(intent)
            dialog.dismiss()
        }
    })
    dialog.show()
}

    private open  fun /*@@kxuvcc@@*/doDischargePatient(){
      var  dialog: /*@@dzzode@@*/android.app.Dialog? = android.app.Dialog(this, R.style.AppTheme_NoActionBarDark)
    dialog.setContentView(R.layout.activity_discharge_patient)
    dialog.show()
      var  toolbarDischarge: /*@@hddwpz@@*/androidx.appcompat.widget.Toolbar? = dialog.findViewById</*@@evgecb@@*/android.view.View?>(R.id.toolbarDischarge) as /*@@hddwpz@@*/androidx.appcompat.widget.Toolbar?
    toolbarDischarge.setTitle(getString(R.string.discharge_patient))
    toolbarDischarge.setNavigationIcon(R.drawable.ic_back)
    toolbarDischarge.setNavigationOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@oxsiqa@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
            dialog.dismiss()
        }
    })
      var  idContainerLayout: /*@@dtxqkm@@*/LinearLayout? = dialog.findViewById</*@@dtxqkm@@*/LinearLayout?>(R.id.idContainerLayout)
      var  btnDischarge: /*@@yeifur@@*/android.widget.Button? = dialog.findViewById</*@@yeifur@@*/android.widget.Button?>(R.id.btnDischarge)
      var  edtDischargeSummary: /*@@qicyby@@*/EditText? = dialog.findViewById</*@@qicyby@@*/EditText?>(R.id.edtDischargeSummary)
    btnDischarge.setOnClickListener(object : android.view.View.OnClickListener{
         open  fun /*@@xedyva@@*/onClick(  v:/*@@evgecb@@*/android.view.View?){
              val  dischargeSummary: /*@@hclxmo@@*/kotlin.String? = edtDischargeSummary.getText().toString()
            if (TextUtils.isEmpty(dischargeSummary)){
                CustomSnackBar.make(v, this@ActivityConsultChartRemote , CustomSnackBar.WARNING,
                getString(R.string.summary_note_is_mandatory), CustomSnackBar.TOP, 3000, 0).show()
                return
            }
            if (!UtilityMethods.isInternetConnected(this@ActivityConsultChartRemote )){
                CustomSnackBar.make(binding.getRoot(), this@ActivityConsultChartRemote , CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0).show()
                return
            }
            dialog.dismiss()
            showProgressBar()
              var  strPatientId: /*@@hclxmo@@*/kotlin.String? = java.lang.String.valueOf(mConsultProvider.getPatientsId())
              var  dischargePatientRequest: /*@@tvjniu@@*/DischargePatientRequest? = DischargePatientRequest()
            dischargePatientRequest.setPatientId(strPatientId)
            dischargePatientRequest.setDischargeSummary(dischargeSummary)
            viewModel.bspDischargePatient(dischargePatientRequest).observe(this@ActivityConsultChartRemote , {  commonResponse ->
//                    Log.d(TAG, "Discharge patient response: " + new Gson().toJson(commonResponse));
            if ((commonResponse != null) && (commonResponse.getStatus() != null) && commonResponse.getStatus()){
                dismissProgressBar()
                if (mConsultProvider != null){
                    mConsultProvider.setStatus(Constants.PatientStatus.Discharged)
                    CustomSnackBar.make(binding.getRoot(), this@ActivityConsultChartRemote , CustomSnackBar.SUCCESS, getString(R.string.patient_discharged_successfully), CustomSnackBar.TOP, 3000, 3).show()
                }
            } else {
                dismissProgressBar()
                btnDischarge.setEnabled(true)
                  var  errMsg: /*@@hclxmo@@*/kotlin.String? = ErrorMessages.getErrorMessage(this@ActivityConsultChartRemote , commonResponse.getErrorMessage(), Constants.API.register)
                CustomSnackBar.make(idContainerLayout, this@ActivityConsultChartRemote , CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0).show()
            }})
        }
    })
    dialog.show()
}

}