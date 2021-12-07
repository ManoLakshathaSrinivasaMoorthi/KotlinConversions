package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinomnicure.viewmodel.HomeViewModel
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.TeamMembersListAdapter
import com.example.kotlinomnicure.databinding.ActivityTeamGroupChatBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.videocall.openvcall.ui.CallActivity
import com.example.kotlinomnicure.viewmodel.TeamGroupChatViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.providerEndpoints.model.Members
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import java.util.ArrayList

class TeamGroupChatActivity : BaseActivity() {
    private val TAG = TeamGroupChatActivity::class.java.simpleName
    private var binding: ActivityTeamGroupChatBinding? = null
    private var viewModel: TeamGroupChatViewModel? = null
    private val membersListAdapter: TeamMembersListAdapter? = null
    private var strTeamName: String? = ""
    private var patientId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_team_group_chat)
        viewModel = ViewModelProvider(this).get(TeamGroupChatViewModel::class.java)
        initToolbar()
        initView()

    }

    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        patientId = intent.getLongExtra("patientID", 0)
        strTeamName = intent.getStringExtra(Constants.IntentKeyConstants.TEAM_NAME)
        binding?.toolbar?.setTitle(strTeamName)
        binding?.idToolbarTitle?.setText(strTeamName)
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

    fun videoCall(provider: Members) {
        showProgressBar()
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.TOKEN,
            "")
        if (providerID != null) {
            if (token != null) {
                provider.getProviderId()?.toLong()?.let {
                    HomeViewModel().startCall(providerID,
                        token,
                        it,
                        0L,
                        Constants.FCMMessageType.AUDIO_CALL)
                        ?.observe(this) { commonResponse ->
                            dismissProgressBar()
                            if (commonResponse != null && commonResponse.status!= null && commonResponse.status!!) {
                                val callScreen = Intent(this, CallActivity::class.java)
                                callScreen.putExtra("providerName", provider.getProviderName())
                                callScreen.putExtra("providerHospitalName", "Team " + provider.getTeamName())
                                callScreen.putExtra("providerId", provider.getProviderId()!!.toLong())
                                callScreen.putExtra("profilePicUrl", "")
                                callScreen.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME,
                                    providerID.toString() + "-" + provider.getProviderId())
                                callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                                callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                                    resources.getStringArray(R.array.encryption_mode_values)[0])
                                callScreen.putExtra("callType", "outgoing")
                                callScreen.putExtra("call", Constants.FCMMessageType.AUDIO_CALL)
                                val gson = Gson()
                                val providerList: MutableList<Provider> =
                                    ArrayList<Provider>()
                                val selfVal = Provider()
                                selfVal.setId(provider.getId()!!.toLong())
                                selfVal.setName(provider.getProviderName())
                                selfVal.setHospital("Team " + provider.getTeamName())
                                selfVal.setRole(provider.getRpType())
                                providerList.add(selfVal)
                                val selfProvider = Provider()
                                selfProvider.setId(providerID)
                                selfProvider.setName(PrefUtility().getStringInPref(this,
                                    Constants.SharedPrefConstants.NAME,
                                    ""))
                                selfProvider.setProfilePicUrl(PrefUtility().getStringInPref(this,
                                    Constants.SharedPrefConstants.PROFILE_IMG_URL,
                                    ""))
                                selfProvider.setHospital(PrefUtility().getStringInPref(this,
                                    Constants.SharedPrefConstants.HOSPITAL_NAME,
                                    ""))
                                selfProvider.setRole(PrefUtility().getStringInPref(this,
                                    Constants.SharedPrefConstants.ROLE,
                                    ""))
                                providerList.add(selfProvider)
                                callScreen.putExtra("providerList", gson.toJson(providerList))
                                startActivity(callScreen)
                            } else if (commonResponse?.getErrorMessage() != null) {
                                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                                    commonResponse?.getErrorMessage(),
                                    Constants.API.startCall)

                                CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                                    errMsg, CustomSnackBar.TOP, 3000, 0)?.show()
                            } else {
                                CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                                    getString(R.string.api_error), CustomSnackBar.TOP, 3000, 0)?.show()
                            }
                        }
                }
            }
        }
    }
}