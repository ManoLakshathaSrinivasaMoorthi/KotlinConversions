package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.GroupListAdapter
import com.example.kotlinomnicure.databinding.ActivityGroupsListBinding
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.videocall.openvcall.ui.CallActivity
import com.example.kotlinomnicure.viewmodel.GroupCallViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import omnicurekotlin.example.com.providerEndpoints.model.GroupCall
import java.util.ArrayList


class GroupCallActivity :  BaseActivity() , GroupListAdapter.GroupListListener {
    private val TAG = GroupCallActivity::class.java.simpleName
    var groupListAdapter: GroupListAdapter? = null
    var selectedProviderIds = ArrayList<Int>()
    var selectedProviderList: ArrayList<Provider> = ArrayList<Provider>()
    var providerList: ArrayList<Provider> = ArrayList<Provider>()
    private var binding: ActivityGroupsListBinding? = null
    private var viewModel: GroupCallViewModel? = null

    // Firebase instance variables
    private val mFirebaseAuth: FirebaseAuth? = null
    private val fcmToken: String? = null
    private val patientID = "4824526680489984"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_groups_list)
        viewModel = ViewModelProvider(this).get(GroupCallViewModel::class.java)
        initToolbar()
        setView()
        setOnclickListener()
        callApi()
    }

    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        binding?.toolbar?.setTitle("hghsdg")
        binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)
    }

    private fun setOnclickListener() {
        binding?.videoCall?.setOnClickListener(View.OnClickListener {
            makeCall(selectedProviderList,
                Constants.FCMMessageType.VIDEO_CALL)
        })
        binding?.audioCall?.setOnClickListener(View.OnClickListener {
            makeCall(selectedProviderList,
                Constants.FCMMessageType.AUDIO_CALL)
        })
    }

    fun makeCall(selectedProviderList: ArrayList<Provider>, callType: String?) {
        showProgressBar()
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String?= PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.TOKEN,
            "")
        val content = GroupCall()
        val a = ArrayList<String>()
        for (i in selectedProviderList.indices) {
            val provider: Provider = selectedProviderList[i]
            a.add(provider.getId().toString())
        }
        content.setId(providerID.toString())
        content.setMessage("")
        content.setPatientsId(patientID)
        content.setToken(token)
        content.setType(callType)
        content.setReceiverIds(a.toTypedArray())
        viewModel?.multipleCall(content)?.observe(this) { commonResponse ->
            Toast.makeText(this, commonResponse?.getErrorMessage(), Toast.LENGTH_LONG).show()
            dismissProgressBar()
            if (commonResponse?.status != null && commonResponse.status!!) {
                val callScreen = Intent(this, CallActivity::class.java)
                callScreen.putExtra("providerName",
                    PrefUtility().getStringInPref(this,
                        Constants.SharedPrefConstants.NAME,
                        ""))
                callScreen.putExtra("providerHospitalName",
                    PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.HOSPITAL_NAME, ""))
                callScreen.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME, patientID)
                callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                    resources.getStringArray(R.array.encryption_mode_values)[0])
                callScreen.putExtra("patientId", patientID)
                callScreen.putExtra("callType", "outgoing")
                callScreen.putExtra("call", callType)
                val providerList: MutableList<Provider> =
                    ArrayList<Provider>()
                providerList.addAll(selectedProviderList)
                val gson = Gson()
                callScreen.putExtra("providerList", gson.toJson(providerList))
                startActivity(callScreen)
            }
            else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse?.getErrorMessage(), Constants.API.startCall)

                CustomSnackBar.make(binding?.idContainerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun callApi() {
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return
        }
        val providerId: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.TOKEN,
            "")
        if (providerId != null) {
            if (token != null) {
                viewModel?.getProviderList(providerId, token, Constants.ProviderRole.RD.toString())
                    ?.observe(this) { listResponse ->
                        var erroMsg = ""
                        if (listResponse != null && listResponse.getStatus() != null && listResponse.getStatus()!!
                            && listResponse.getProviderList() != null && !listResponse.getProviderList()!!
                                .isEmpty()
                        ) {
                            for (i in 0 until listResponse.getProviderList()!!.size) {
                                val provider: Provider? =
                                    listResponse.getProviderList()!!.get(i)
                                if (provider?.getId() !== providerId) {
                                    if (provider != null) {
                                        providerList.add(provider)
                                    }
                                }
                            }
                            groupListAdapter = GroupListAdapter(this, providerList, selectedProviderIds)
                            binding?.teamsRecyclerView?.setAdapter(groupListAdapter)
                            groupListAdapter?.setListener(this)
                            groupListAdapter?.notifyDataSetChanged()
                        } else {
                            erroMsg = applicationContext.getString(R.string.directory_list_empty)
                        }
                        if (!TextUtils.isEmpty(erroMsg)) {
                            val errMsg: String? = ErrorMessages().getErrorMessage(applicationContext,
                                listResponse?.getErrorMessage(),
                                Constants.API.getProviders)

                            CustomSnackBar.make(binding?.idContainerLayout,
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


    override fun onResume() {
        super.onResume()
    }

    private fun setView() {
        val mLinearLayoutManager = LinearLayoutManager(this)
        binding?.teamsRecyclerView?.setLayoutManager(mLinearLayoutManager)
    }



    override fun onItemClicked(id: Int?, isChecked: Boolean?) {
        if (isChecked == true) {
            if (id != null) {
                selectedProviderIds.add(id)
            }
            selectedProviderList.add(providerList[id!!])
        } else {
            if (selectedProviderIds.contains(id)) {
                selectedProviderIds.remove(id)
            }
            selectedProviderList.remove(providerList[id!!])
        }
        groupListAdapter?.notifyDataSetChanged()
        if (selectedProviderList.size > 0) {
            binding?.menuIcons?.setVisibility(View.VISIBLE)
        } else {
            binding?.menuIcons?.setVisibility(View.GONE)
        }
    }
}