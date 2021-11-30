package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytasksamplepoc.kotlinomnicure.adapter.TeamListAdapter

import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HomeViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.MyVirtualViewModel
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model.Provider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.CallActivity
import com.example.kotlinomnicure.activity.GroupCallActivity
import com.example.kotlinomnicure.databinding.ActivityMyVirtualWardBinding
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class MyVirtualTeamsActivity : BaseActivity() {
    private val TAG: String = MyVirtualTeamsActivity::class.java.getSimpleName()
    private var binding: ActivityMyVirtualWardBinding? = null
    private var viewModel: MyVirtualViewModel? = null
    private var uid: Long = 0
    private var teamAdapter: TeamListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_virtual_ward)
        viewModel = ViewModelProvider(this).get(MyVirtualViewModel::class.java)
        uid = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, 0)
        initToolbar()
        initView()
        callApi()
    }

    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        binding?.toolbar?.setTitle(getString(R.string.my_virtual_teams))
        binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)
        val groupCallIcon = binding?.toolbar?.findViewById(R.id.group_call) as ImageView
        groupCallIcon.setOnClickListener {
            val intent = Intent(this, GroupCallActivity::class.java)
            startActivity(intent)

        }
    }

    private fun initView() {
        val mLinearLayoutManager = LinearLayoutManager(this)
        binding?.teamsRecyclerView?.setLayoutManager(mLinearLayoutManager)
    }

    private fun showDialog() {
        if (!UtilityMethods() .isInternetConnected(this)!!) {

            CustomSnackBar.make(binding?.getRoot(), this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        val providerId: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        if (providerId != null) {
            if (token != null) {
                viewModel?.getProviderList(providerId, token, Constants.ProviderRole.RD.toString())
                    ?.observe(this) { listResponse ->
                        var erroMsg = ""
                        if (listResponse?.getStatus() != null && listResponse.getStatus()!!) {
                            if (listResponse.getProviderList() != null && !listResponse.getProviderList()!!
                                    .isEmpty()
                            ) {
                                val providerList: ArrayList<Provider> = ArrayList<Provider>()
                                val selectedProviderList: ArrayList<Provider> =
                                    ArrayList<Provider>()
                                val vals = ArrayList<String>()
                                for (i in 0 until listResponse.getProviderList()!!.size) {
                                    val provider: Provider? = listResponse.getProviderList()!!.get(i)
                                    if (provider != null) {
                                        if (provider.getId() !== providerId) {
                                            provider.getName()?.let { vals.add(it) }
                                            providerList.add(provider)
                                        }
                                    }
                                }
                                val builder =
                                    AlertDialog.Builder(this)
                                builder.setTitle("Select users to call")
                                builder.setMultiChoiceItems(
                                    vals.toTypedArray(),
                                    null
                                ) { dialog, which, isChecked -> // user checked or unchecked a box
                                    if (isChecked) {
                                        selectedProviderList.add(providerList[which])
                                    } else {
                                        selectedProviderList.remove(providerList[which])
                                    }
                                }
                                builder.setPositiveButton(
                                    "Make Call"
                                ) { dialog, which ->
                                    var channelName = ""
                                    for (i in selectedProviderList.indices) {
                                        val provider: Provider = selectedProviderList[i]
                                        channelName += "-" + provider.getId()
                                    }
                                    makeCall(selectedProviderList, channelName)
                                }
                                builder.setNegativeButton("Cancel", null)
                                val dialog = builder.create()
                                dialog.show()
                            } else {
                                erroMsg = applicationContext.getString(R.string.directory_list_empty)
                            }
                        } else {
                            erroMsg = applicationContext.getString(R.string.directory_list_empty)
                        }
                        if (!TextUtils.isEmpty(erroMsg)) {
                            val errMsg: String? = ErrorMessages().getErrorMessage(
                                applicationContext,
                                listResponse?.getErrorMessage(),
                                Constants.API.getProviders
                            )
                            if (errMsg != null) {
                                CustomSnackBar.make(
                                    binding?.idContainerLayout,
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
            }
        }
    }

    private fun makeCall(selectedProviderList: ArrayList<Provider>, channelName: String) {
        showProgressBar()
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        val name: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        val hospitalName: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.HOSPITAL_NAME, "")
        val imageURL: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.PROFILE_IMG_URL, "")
        if (providerID != null) {
            selectedProviderList[0].getId()?.let {
                if (token != null) {
                    HomeViewModel().startCall(providerID, token, it, 0L, Constants.FCMMessageType.VIDEO_CALL)?.observe(this) { commonResponse ->
                        dismissProgressBar()
                        if (commonResponse?.status != null && commonResponse.status!!) {
                            val callScreen = Intent(this, CallActivity::class.java)
                            callScreen.putExtra("providerName", selectedProviderList[0].getName())
                            callScreen.putExtra("providerHospitalName", selectedProviderList[0].getHospital())
                            callScreen.putExtra("profilePicUrl", selectedProviderList[0].getProfilePicUrl())
                            callScreen.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME, providerID.toString() + channelName)
                            callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                            callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_MODE, resources.getStringArray(R.array.encryption_mode_values)[0])
                            callScreen.putExtra("callType", "outgoing")
                            callScreen.putExtra("call", Constants.FCMMessageType.AUDIO_CALL)
                            val gson = Gson()
                            val providerList: MutableList<Provider> = ArrayList<Provider>()

                            val selfProvider = Provider()
                            selfProvider.setId(providerID)
                            selfProvider.setName(
                                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, ""))
                            selfProvider.setProfilePicUrl(
                                PrefUtility().getStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.PROFILE_IMG_URL,
                                    ""
                                )
                            )
                            selfProvider.setHospital(
                                PrefUtility().getStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.HOSPITAL_NAME,
                                    ""
                                )
                            )
                            selfProvider.setRole(
                                PrefUtility().getStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.ROLE,
                                    ""
                                )
                            )
                            providerList.add(selfProvider)
                            providerList.addAll(selectedProviderList)
                            callScreen.putExtra("providerList", gson.toJson(providerList))
                            startActivity(callScreen)
                        } else {
                            val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse?.errorMessage, Constants.API.startCall)

                            if (errMsg != null) {
                                CustomSnackBar.make(
                                    binding?.idContainerLayout,
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
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun callApi() {
        showProgressBar()
        viewModel?.getTeams(uid)?.observe(this) { commonResponse ->
            dismissProgressBar()
            Log.d(TAG, "TeamsDetailsRes-->" + Gson().toJson(commonResponse))
            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                val gson = Gson()
                var obj: JSONObject? = null
                try {
                    obj = JSONObject(gson.toJson(commonResponse))
                } catch (e: JSONException) {
                    Log.d(TAG, e.toString())
                }
                try {
                    teamAdapter = TeamListAdapter(
                        this,
                        obj!!.getJSONArray("teamDetailsList")
                    )
                    binding!!.teamsRecyclerView.setAdapter(teamAdapter)
                    teamAdapter!!.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Log.e(TAG, "exception", e.cause)
                }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    java.lang.String.valueOf(commonResponse?.getErrorMessage()),
                    Constants.API.getHospital
                )

                if (errMsg != null) {
                    CustomSnackBar.make(
                        binding?.idContainerLayout,
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
    }

    //video call
    fun videoCall(provider: JSONObject) {
        showProgressBar()
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(
            this,
            Constants.SharedPrefConstants.TOKEN,
            "")
        try {
            val prId = provider.getString("providerId").toDouble()
            val lngPrId = java.lang.Double.valueOf(prId).toLong()

         //start the call
            if (providerID != null) {
                if (token != null) {
                    HomeViewModel().startCall(providerID, token, lngPrId.toString().toLong(), 0L, Constants.FCMMessageType.AUDIO_CALL)
                        ?.observe(this) { commonResponse ->
                            dismissProgressBar()
                            try {
                                if (commonResponse?.status!= null && commonResponse.status!!) {
                                    val callScreen =
                                        Intent(this, CallActivity::class.java)
                                    callScreen.putExtra("providerName", provider.getString("providerName"))
                                    callScreen.putExtra(
                                        "providerHospitalName",
                                        provider.getString("providerName")
                                    )

                                    callScreen.putExtra("providerId", lngPrId.toString().toLong())
                                    callScreen.putExtra("profilePicUrl", "")

                                    callScreen.putExtra(
                                        ConstantApp().ACTION_KEY_CHANNEL_NAME,
                                        providerID.toString() + "-" + lngPrId.toString().toLong()
                                    )
                                    callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                                    callScreen.putExtra(
                                        ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                                        resources.getStringArray(R.array.encryption_mode_values)[0]
                                    )
                                    callScreen.putExtra("callType", "outgoing")
                                    callScreen.putExtra("call", Constants.FCMMessageType.AUDIO_CALL)
                                    val gson = Gson()
                                    val providerList: MutableList<Provider> =
                                        ArrayList<Provider>()
                                    val selfVal = Provider()

                                    selfVal.setId(lngPrId.toString().toLong())
                                    selfVal.setName(provider.getString("providerName"))
                                    selfVal.setHospital("Team " + provider.getString("teamName"))
                                    selfVal.setRole(provider.getString("rpType"))
                                    providerList.add(selfVal)
                                    val selfProvider = Provider()
                                    selfProvider.setId(providerID)
                                    selfProvider.setName(
                                        PrefUtility().getStringInPref(
                                            this,
                                            Constants.SharedPrefConstants.NAME,
                                            ""
                                        )
                                    )
                                    selfProvider.setProfilePicUrl(
                                        PrefUtility().getStringInPref(
                                            this,
                                            Constants.SharedPrefConstants.PROFILE_IMG_URL,
                                            ""
                                        )
                                    )
                                    selfProvider.setHospital(
                                        PrefUtility().getStringInPref(
                                            this,
                                            Constants.SharedPrefConstants.HOSPITAL_NAME,
                                            ""
                                        )
                                    )
                                    selfProvider.setRole(
                                        PrefUtility().getStringInPref(
                                            this,
                                            Constants.SharedPrefConstants.ROLE,
                                            ""
                                        )
                                    )
                                    providerList.add(selfProvider)
                                    callScreen.putExtra("providerList", gson.toJson(providerList))
                                    startActivity(callScreen)
                                } else {
                                    val errMsg: String? = ErrorMessages().getErrorMessage(
                                        this,
                                        commonResponse?.errorMessage,
                                        Constants.API.startCall
                                    )

                                    if (errMsg != null) {
                                        CustomSnackBar.make(
                                            binding?.idContainerLayout,
                                            this,
                                            CustomSnackBar.WARNING,
                                            errMsg,
                                            CustomSnackBar.TOP,
                                            3000,
                                            0
                                        )?.show()
                                    }
                                }
                            } catch (e: JSONException) {
                                Log.e(TAG, "exception", e.cause)
                            }
                        }
                }
            }
        } catch (e: JSONException) {
            Log.e(TAG, "exception", e.cause)
        }
    }
}