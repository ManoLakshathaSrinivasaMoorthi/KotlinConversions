package com.example.kotlinomnicure.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.adapter.BedsideDirectoryAdapter
import com.example.kotlinomnicure.adapter.HospitalDirectoryAdapter
import com.example.dailytasksamplepoc.kotlinomnicure.adapter.RemoteDirectoryAdapter
import com.example.kotlinomnicure.fragment.BedsideDirectoryFragment
import com.example.kotlinomnicure.fragment.HospitalDirectoryFragment
import com.example.kotlinomnicure.fragment.RemoteDirectoryFragment

import com.example.kotlinomnicure.viewmodel.HomeViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityRemoteProviderDirectoryBinding
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import java.util.ArrayList
import java.util.LinkedHashMap

class RemoteProviderDirectoryActivity : BaseActivity(),
    RemoteDirectoryAdapter.DirectoryClickListener,
    HospitalDirectoryAdapter.HospitalClickListener, View.OnClickListener,
    HospitalDirectoryFragment.AddressFilterListener, BedsideDirectoryAdapter.DirectoryClickListener {


    private val TAG = RemoteProviderDirectoryActivity::class.java.simpleName
    private var selectedTab = TAB.Remote.ordinal
    protected var binding: ActivityRemoteProviderDirectoryBinding? = null
    var context: Context? = null
    var filteredList: List<Provider> = ArrayList<Provider>()
    var providerList: List<Provider> = ArrayList<Provider>()
    var filterType = "All"
    var hospitalFilter: kotlin.String? = "All"

    var bedsideFilter: kotlin.String? = "All"
    var searchText: kotlin.String? = ""
    var pager: UnSwipeableViewPager? = null
    var hopitalFilterErrMsg: String? = null
    var userRole: String? = null
    var addressFilterArray = ArrayList<String>()
    var addressFilterList = LinkedHashMap<String, String>()
    var bedsideHospitalfilterList = LinkedHashMap<String, String>()
    private var viewModel: HomeViewModel? = null
    private var remoteSearchListener: FragmentSearchListener? = null
    private var hospitalSearchListener: HospitalSearchListener? = null
    private var bedsideSearchListener: BedsideSearchListener? = null
    var tabChangeListener =
        View.OnClickListener { view ->
            setAppBarScroll(true)
            when (view.id) {
                R.id.remoteBtnLayout -> {
                    if (selectedTab == TAB.Remote.ordinal) {
                        return@OnClickListener
                    }
                    binding!!.idBtnRemote.setTextColor(resources.getColor(R.color.colorPrimary))
                    binding!!.idBtnRemote.setBackground(resources.getDrawable(R.drawable.tab_selected_rounded))
                    binding!!.idBtnBedside.setTextColor(resources.getColor(R.color.white))
                    binding!!.idBtnBedside.setBackground(resources.getDrawable(R.drawable.transparent_bg))
                    selectedTab = TAB.Remote.ordinal
                    onSearch()
                    pager!!.setCurrentItem(0)
                    if (remoteSearchListener != null) {
                        remoteSearchListener!!.onTabSelected()
                    }
                }
                R.id.bedsideBtnLayout -> {
                    if (selectedTab == TAB.Bedside.ordinal) {
                        return@OnClickListener
                    }
                    binding!!.idBtnBedside.setTextColor(resources.getColor(R.color.colorPrimary))
                    binding!!.idBtnBedside.setBackground(resources.getDrawable(R.drawable.tab_selected_rounded))
                    binding!!.idBtnRemote.setTextColor(resources.getColor(R.color.white))
                    binding!!.idBtnRemote.setBackground(resources.getDrawable(R.drawable.transparent_bg))
                    selectedTab = TAB.Bedside.ordinal
                    if (userRole.equals("RD", ignoreCase = true)) {
                        pager!!.setCurrentItem(1)
                    } else {
                        val hospName: String = java.lang.String.valueOf(
                            PrefUtility().getStringInPref(
                                this,
                                Constants.SharedPrefConstants.HOSPITAL_NAME,
                                ""
                            )
                        )
                        pager!!.setCurrentItem(2)
                        if (bedsideSearchListener != null) {
                            bedsideSearchListener!!.onTabSelected()
                        }
                        binding!!.hospitalText.setText(hospName)
                        binding!!.bedsideFilterLayout.setEnabled(false)
                        binding!!.dropdownArrow.setVisibility(View.GONE)
                    }
                    onSearch()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_provider_directory)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        context = applicationContext
        setViews()
        pager = findViewById<UnSwipeableViewPager>(R.id.viewPager)
        val fm = supportFragmentManager
        val pagerAdapter = ViewPagerAdapter(fm)
        // Here you would declare which page to visit on creation
        pager!!.setAdapter(pagerAdapter)
        pager!!.setOffscreenPageLimit(3)
        pager!!.setCurrentItem(0)
        userRole = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        getHospitalList()
        binding!!.closeSearch.setOnClickListener(View.OnClickListener { view ->
            if (TextUtils.isEmpty(binding!!.searchEdittext.text.toString())) {
                binding!!.searchLayout.setVisibility(View.GONE)
                binding!!.tabsLayout.setVisibility(View.VISIBLE)
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
            } else if (binding!!.searchEdittext.text.toString().length > 0) {
                binding!!.searchEdittext.setText("")
                binding!!.tabsLayout.setVisibility(View.GONE)
            }
        })
        setSearchTextWatcher()
    }

    private fun setSearchTextWatcher() {
        binding!!.searchEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchText = editable.toString()
                onSearch()
            }
        })
    }

    private fun setViews() {
        initToolbar()
        handleTabButtons()

    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    private fun initToolbar() {
        binding!!.txtClearlocation.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            // onSearchfilter();
            hospitalFilter = "All"
            onSearch()
            // isActivePatient = false;
            binding!!.txtClearlocation.setVisibility(View.GONE)
            binding!!.locationText.setText("All")
        })

        binding!!.txtClearproviderfilter.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)

            filterType = "All"
            onSearch()
            binding!!.txtClearproviderfilter.setVisibility(View.GONE)
            binding!!.filterText.setText("All")
        })
        binding!!.providerFilterView.setOnClickListener(View.OnClickListener {
            handleMultipleClick(binding!!.providerFilterView)
            val remoteMap = LinkedHashMap<String, String>()
            remoteMap["MD/DO"] = "Physician (MD/DO)"
            remoteMap["APP"] = "Advanced Practice Provider (APP)"
            remoteMap["RN"] = "Registered Nurse (RN)"
            remoteMap["RT"] = "Respiratory Therapist (RT)"
            remoteMap["RPh"] = "Registered Pharmacist (RPh)"
            remoteMap["All"] = "All"
            filterDialog(this, remoteMap, "remote")
        })
        binding!!.hospitalFilterView.setOnClickListener(View.OnClickListener {
            handleMultipleClick(binding!!.hospitalFilterView)
            filterDialog(this, addressFilterList, "hospital")
        })
        binding!!.bedsideFilterLayout.setOnClickListener(View.OnClickListener {
            handleMultipleClick(binding!!.bedsideFilterLayout)
            if (!TextUtils.isEmpty(hopitalFilterErrMsg)) {
                hopitalFilterErrMsg?.let { it1 ->
                    CustomSnackBar.make(
                        binding!!.container,
                        this,
                        CustomSnackBar.WARNING,
                        it1,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
                return@OnClickListener
            }
            filterDialog(this, bedsideHospitalfilterList, "bedside")
        })
        binding!!.idNavigationIcon.setImageResource(R.drawable.ic_menu_icon)
        binding!!.idNavigationIcon.setOnClickListener { view ->
            val intent = Intent(this, MyDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
        }
        binding!!.searchIcon.setOnClickListener(View.OnClickListener {
            searchText = ""
            binding!!.searchLayout.setVisibility(View.VISIBLE)
            binding!!.tabsLayout.setVisibility(View.GONE)
        })
        binding!!.closeSearch.setOnClickListener(View.OnClickListener {
            searchText = ""
            onSearch()
            binding!!.searchEdittext.setText("")
            binding!!.searchLayout.setVisibility(View.GONE)
            binding!!.tabsLayout.setVisibility(View.VISIBLE)
        })
    }

    private fun handleTabButtons() {
        binding?.remoteBtnLayout?.setOnClickListener(tabChangeListener)
        binding?.bedsideBtnLayout?.setOnClickListener(tabChangeListener)
    }



    override fun addressFilterReady(addressArr: ArrayList<String>) {
        if (!addressArr.contains("All")) {
            addressArr.add("All")
        }
        addressFilterArray = addressArr
        addressFilterList.clear()
        var i = 0
        while (i < addressFilterArray.size) {
            addressFilterList[i.toString()] = addressFilterArray[i]
            i++
        }
    }

    private fun getHospitalList() {
        if (!UtilityMethods().isInternetConnected(this)!!) {
            CustomSnackBar.make(
                binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        val providerId: Long? = PrefUtility().getProviderId(this)
        if (providerId != null) {
            viewModel?.getHospitalList(providerId)?.observe(this) { listResponse ->
                println("hospitalResponse2 " + Gson().toJson(listResponse))
                if (listResponse != null && listResponse.getStatus() != null && listResponse.getStatus()!!) {
                    val hospitalList: List<Hospital?>? = listResponse.hospitalList
                    bedsideHospitalfilterList.clear()
                    for (i in hospitalList?.indices!!) {
                        val h: Hospital? = hospitalList[i]
                        if (h != null) {
                            bedsideHospitalfilterList[java.lang.String.valueOf(h?.getId())] =
                                h.name.toString()
                        }
                    }
                } else {
                    hopitalFilterErrMsg = context!!.getString(R.string.directory_list_empty)
                }
            }
        }
    }

    private fun onSearch() {
        if (selectedTab == TAB.Remote.ordinal) {
            remoteSearchListener!!.onSearchFilter(searchText, filterType)
        } else if (selectedTab == TAB.Bedside.ordinal) {
            if (pager?.currentItem === 1) {
                hospitalSearchListener!!.onSearchFilter(searchText, hospitalFilter)
            } else if (pager?.currentItem === 2) {
                bedsideSearchListener!!.onSearchFilter(searchText, bedsideFilter)
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun filterDialog(
        context: Context?,
        remoteMap: LinkedHashMap<String, String>,
        filterPage: String,
    ) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_directory_dialog)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val radioGroup = dialog.findViewById<View>(R.id.radioGrp) as RadioGroup
        val close = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val title = dialog.findViewById<View>(R.id.txtTitle) as TextView
        val inflater = applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for ((key, value) in remoteMap) {
            val radioButton =
                inflater.inflate(R.layout.radiobutton, radioGroup, false) as RadioButton
            radioButton.id = View.generateViewId()
            radioButton.text = value
            radioButton.tag = key
            if (filterPage.equals("remote", ignoreCase = true)) {
                title.text = getString(R.string.filter_by_rp)
                if (filterType == key) {
                    radioButton.isChecked = true
                }
            } else if (filterPage.equals("hospital", ignoreCase = true)) {
                title.text = getString(R.string.sel_hospital_location)
                if (hospitalFilter == value) {
                    radioButton.isChecked = true
                }
            } else if (filterPage.equals("bedside", ignoreCase = true)) {
                title.text = getString(R.string.sel_hospital)
                if (bedsideFilter == key) {
                    radioButton.isChecked = true
                }
            }
            radioGroup.addView(radioButton)
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                println("radiobutton click " + checkedRadioButton.text + " Id is " + checkedId + " " + checkedRadioButton.tag.toString())
                if (filterPage.equals("remote", ignoreCase = true)) {
                    filterType = checkedRadioButton.tag.toString()
                    binding!!.filterText.setText(checkedRadioButton.tag.toString())
                    if (binding!!.filterText.getText().equals("All")) {
                        binding!!.txtClearproviderfilter.setVisibility(View.GONE)
                    } else {
                        binding!!.txtClearproviderfilter.setVisibility(View.VISIBLE)
                    }
                } else if (filterPage.equals("hospital", ignoreCase = true)) {
                    hospitalFilter = checkedRadioButton.text.toString()
                    binding!!.locationText.setText(checkedRadioButton.text.toString())
                    if (binding!!.locationText.getText().toString().equals("All")) {
                        binding!!.txtClearlocation.setVisibility(View.GONE)
                    } else {
                        binding!!.txtClearlocation.setVisibility(View.VISIBLE)
                    }
                } else if (filterPage.equals("bedside", ignoreCase = true)) {
                    bedsideFilter = checkedRadioButton.tag.toString()
                    binding!!.hospitalText.setText(checkedRadioButton.text.toString())
                    bedsideSearchListener!!.sendHospitalId(bedsideFilter)

                }
                onSearch()
                Handler().postDelayed({ dialog.dismiss() }, 300)
            }
        }
        close.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun onClick(view: View) {
        println("radiobutton click " + (view as RadioButton).text + " Id is " + view.getId())
    }

    fun setFragmentSearchListener(fragmentRefreshListener: FragmentSearchListener?) {
        remoteSearchListener = fragmentRefreshListener
    }

    fun setHospitalSearchListener(l: HospitalSearchListener?) {
        hospitalSearchListener = l
    }

    fun setBedsideSearchListener(l: BedsideSearchListener?) {
        bedsideSearchListener = l
    }

    fun setAppBarScroll(flag: Boolean) {
        val params = binding!!.appBar.getLayoutParams() as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as AppBarLayout.Behavior?
        if (flag) {
            behavior!!.setDragCallback(null)
        } else {
            behavior!!.setDragCallback(object : DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return false
                }
            })
        }
    }


    private enum class TAB {
        Remote, Bedside
    }

    interface FragmentSearchListener {
        fun onSearchFilter(search: String?, filter: String?)
        fun onTabSelected()
    }

    interface HospitalSearchListener {
        fun onSearchFilter(search: String?, filter: String?)
    }

    interface BedsideSearchListener {
        fun sendHospitalId(hospitalId: String?)
        fun onSearchFilter(search: String?, filter: String?)
        fun onTabSelected()
    }

    class ViewPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            if (position == 0) {
                fragment = RemoteDirectoryFragment(
                    RemoteProviderDirectoryActivity().binding,
                    RemoteProviderDirectoryActivity().applicationContext
                )
            } else if (position == 1) {
                fragment = HospitalDirectoryFragment(
                    RemoteProviderDirectoryActivity().binding,
                    RemoteProviderDirectoryActivity().applicationContext
                )
            } else if (position == 2) {
                fragment = BedsideDirectoryFragment(
                    RemoteProviderDirectoryActivity().binding,
                    RemoteProviderDirectoryActivity().applicationContext
                )
            }
            return fragment!!
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            var title: String? = null
            if (position == 0) {
                title = "Tab-1"
            } else if (position == 1) {
                title = "Tab-2"
            }
            return title
        }
    }


    override fun onHospitalClicked(hospital: Hospital?) {
        bedsideSearchListener!!.sendHospitalId(java.lang.String.valueOf(hospital?.getId()))
        if (hospital != null) {
            binding?.hospitalText?.setText(hospital.getName())
        }
        if (hospital != null) {
            bedsideFilter = java.lang.String.valueOf(hospital.getId())
        }
        pager?.setCurrentItem(2)
        if (bedsideSearchListener != null) {
            bedsideSearchListener!!.onSearchFilter(searchText, bedsideFilter)
        }
    }

    override fun onAudioCallClicked(provider: Provider?) {
        showProgressBar()
        Log.i(TAG, "Hospital Name : " + provider?.getHospital())
        Log.i(TAG, "Provider Name : " + provider?.getName())
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(
            this,
            Constants.SharedPrefConstants.TOKEN,
            ""
        )
        if (providerID != null) {
            if (token != null) {
                provider?.getId()?.let {
                    viewModel?.startCall(
                        providerID,
                        token,
                        it,
                        0L,
                        Constants.FCMMessageType.AUDIO_CALL
                    )?.observe(this) { commonResponse ->
                        dismissProgressBar()
                        if (commonResponse?.status != null && commonResponse.status!!) {
                            Log.i(TAG, "call response $commonResponse")
                            val callScreen =
                                Intent(this, CallActivity::class.java)
                            callScreen.putExtra("providerName", provider?.getName())
                            callScreen.putExtra("providerId", provider?.getId())
                            callScreen.putExtra("providerHospitalName", provider?.getHospital())
                            callScreen.putExtra("profilePicUrl", provider?.getProfilePicUrl())
                            callScreen.putExtra(
                                ConstantApp().ACTION_KEY_CHANNEL_NAME,
                                providerID.toString() + "-" + provider?.getId()
                            )
                            callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                            callScreen.putExtra(
                                ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                                resources.getStringArray(R.array.encryption_mode_values)[0])
                            if (commonResponse != null) {
                                callScreen.putExtra(
                                    Constants.IntentKeyConstants.AUDIT_ID,
                                    commonResponse.auditId
                                )
                            }
                            callScreen.putExtra("callType", "outgoing")
                            callScreen.putExtra("call", Constants.FCMMessageType.AUDIO_CALL)
                            val gson = Gson()
                            val providerList: MutableList<Provider> =
                                ArrayList<Provider>()
                            if (provider != null) {
                                providerList.add(provider)
                            }
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
                                    binding?.container,
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


        fun onVideoCallClicked(provider: Provider?) {
            showProgressBar()
            Log.i(TAG, "Hospital Name : " + provider?.getHospital())
            Log.i(TAG, "Provider Name : " + provider?.getName())
            val providerID: Long? = PrefUtility().getProviderId(this)
            val token: String? = PrefUtility().getStringInPref(
                this,
                Constants.SharedPrefConstants.TOKEN,
                ""
            )
            if (providerID != null) {
                if (token != null) {
                    provider?.getId()?.let {
                        viewModel?.startCall(
                            providerID,
                            token,
                            it,
                            0L,
                            Constants.FCMMessageType.VIDEO_CALL
                        )?.observe(this) { commonResponse ->
                            dismissProgressBar()
                            if (commonResponse?.status != null && commonResponse.status!!) {
                                Log.i(TAG, "call response $commonResponse")
                                val callScreen =
                                    Intent(this, CallActivity::class.java)
                                callScreen.putExtra("providerName", provider.getName())
                                callScreen.putExtra("providerId", provider.getId())
                                callScreen.putExtra("providerHospitalName", provider.getHospital())
                                callScreen.putExtra("profilePicUrl", provider.getProfilePicUrl())
                                callScreen.putExtra(
                                    ConstantApp().ACTION_KEY_CHANNEL_NAME,
                                    providerID.toString() + "-" + provider.getId()
                                )
                                callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                                callScreen.putExtra(
                                    ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                                    resources.getStringArray(R.array.encryption_mode_values)[0]
                                )
                                callScreen.putExtra(
                                    Constants.IntentKeyConstants.AUDIT_ID,
                                    commonResponse.auditId
                                )
                                callScreen.putExtra("callType", "outgoing")
                                callScreen.putExtra("call", Constants.FCMMessageType.VIDEO_CALL)
                                val gson = Gson()
                                val providerList: MutableList<Provider> =
                                    ArrayList<Provider>()
                                providerList.add(provider)
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
                                    commonResponse?.errorMessage)

                                if (errMsg != null) {
                                    CustomSnackBar.make(
                                        binding?.container,
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

        fun onHospitalClicked(hospital: Hospital) {
            bedsideSearchListener!!.sendHospitalId(java.lang.String.valueOf(hospital.getId()))
            binding!!.hospitalText.text = hospital.getName()
            bedsideFilter = java.lang.String.valueOf(hospital.getId())
            pager!!.currentItem = 2
            if (bedsideSearchListener != null) {
                bedsideSearchListener!!.onSearchFilter(searchText, bedsideFilter)
            }
        }

        fun addressFilterReady(addressArr: ArrayList<String>) {
            if (!addressArr.contains("All")) {
                addressArr.add("All")
            }
            addressFilterArray = addressArr
            addressFilterList.clear()
            var i = 0
            while (i < addressFilterArray.size) {
                addressFilterList[i.toString()] = addressFilterArray[i]
                i++
            }
        }
//    Collections.sort(arrayList);

        //    Collections.sort(arrayList);
        fun getHospitalList() {
            if (!UtilityMethods().isInternetConnected(this)!!) {
                CustomSnackBar.make(binding!!.root,
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP,
                    3000,
                    0)!!
                    .show()
                return
            }
            val providerId: Long? = PrefUtility().getProviderId(this)
            providerId?.let { viewModel!!.getHospitalList(it) }!!.observe(this, { listResponse ->

                if (listResponse != null && listResponse.getStatus() != null && listResponse.getStatus()!!) {
                    val hospitalList: List<Hospital>? = listResponse.hospitalList
                    bedsideHospitalfilterList.clear()
                    for (i in hospitalList?.indices!!) {
                        val h = hospitalList[i]
                        bedsideHospitalfilterList[java.lang.String.valueOf(h.getId())] =
                            h.getName()!!
                    }
                } else {
                    hopitalFilterErrMsg = context!!.getString(R.string.directory_list_empty)
                }
            })
        }

        fun onSearch() {
            if (selectedTab == TAB.Remote.ordinal) {
                remoteSearchListener!!.onSearchFilter(searchText, filterType)
            } else if (selectedTab == TAB.Bedside.ordinal) {
                if (pager!!.currentItem === 1) {
                    hospitalSearchListener!!.onSearchFilter(searchText, hospitalFilter)
                } else if (pager!!.currentItem === 2) {
                    bedsideSearchListener!!.onSearchFilter(searchText, bedsideFilter)
                }
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun filterDialog(
            context: Context?,
            remoteMap: LinkedHashMap<String, String>,
            filterPage: String,
        ) {
            val dialog = Dialog(context!!, R.style.Theme_Dialog)
            dialog.setContentView(R.layout.filter_directory_dialog)
            dialog.window!!.setGravity(Gravity.BOTTOM)
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
            dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
            val radioGroup = dialog.findViewById<View>(R.id.radioGrp) as RadioGroup
            val close = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
            val title = dialog.findViewById<View>(R.id.txtTitle) as TextView
            val inflater =
                applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            for ((key, value) in remoteMap) {
                val radioButton =
                    inflater.inflate(R.layout.radiobutton, radioGroup, false) as RadioButton
                radioButton.id = View.generateViewId()
                radioButton.text = value
                radioButton.tag = key
                if (filterPage.equals("remote", ignoreCase = true)) {
                    title.text = getString(R.string.filter_by_rp)
                    if (filterType == key) {
                        radioButton.isChecked = true
                    }
                } else if (filterPage.equals("hospital", ignoreCase = true)) {
                    title.text = getString(R.string.sel_hospital_location)
                    if (hospitalFilter == value) {
                        radioButton.isChecked = true
                    }
                } else if (filterPage.equals("bedside", ignoreCase = true)) {
                    title.text = getString(R.string.sel_hospital)
                    if (bedsideFilter == key) {
                        radioButton.isChecked = true
                    }
                }
                radioGroup.addView(radioButton)
            }
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val checkedRadioButton =
                    group.findViewById<View>(checkedId) as RadioButton
                val isChecked = checkedRadioButton.isChecked
                if (isChecked) {

                    if (filterPage.equals("remote", ignoreCase = true)) {
                        filterType = checkedRadioButton.tag.toString()
                        binding!!.filterText.text = checkedRadioButton.tag.toString()
                        if (binding!!.filterText.text.equals("All")) {
                            binding!!.txtClearproviderfilter.visibility = View.GONE
                        } else {
                            binding!!.txtClearproviderfilter.visibility = View.VISIBLE
                        }
                    } else if (filterPage.equals("hospital", ignoreCase = true)) {
                        hospitalFilter = checkedRadioButton.text.toString()
                        binding!!.locationText.text = checkedRadioButton.text.toString()
                        if (binding!!.locationText.text.toString().equals("All")) {
                            binding!!.txtClearlocation.visibility = View.GONE
                        } else {
                            binding!!.txtClearlocation.visibility = View.VISIBLE
                        }
                    } else if (filterPage.equals("bedside", ignoreCase = true)) {
                        bedsideFilter = checkedRadioButton.tag.toString()
                        binding!!.hospitalText.text = checkedRadioButton.text.toString()
                        bedsideSearchListener!!.sendHospitalId(bedsideFilter)

                    }
                    onSearch()
                    Handler().postDelayed({ dialog.dismiss() }, 300)
                }
            }
            close.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }


        fun onClick(view: View?) {

        }

        fun setFragmentSearchListener(fragmentRefreshListener: FragmentSearchListener?) {
            remoteSearchListener = fragmentRefreshListener
        }

        fun setHospitalSearchListener(l: HospitalSearchListener?) {
            hospitalSearchListener = l
        }

        fun setBedsideSearchListener(l: BedsideSearchListener?) {
            bedsideSearchListener = l
        }

        fun setAppBarScroll(flag: Boolean) {
            val params = binding!!.appBar.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior as AppBarLayout.Behavior?
            if (flag) {
                behavior!!.setDragCallback(null)
            } else {
                behavior!!.setDragCallback(object : DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return false
                    }
                })
            }
        }




        class ViewPagerAdapter(fm: FragmentManager?) :
            FragmentPagerAdapter(fm!!) {
            override fun getItem(position: Int): Fragment {
                var fragment: Fragment? = null
                if (position == 0) {
                    fragment = RemoteDirectoryFragment(binding, applicationContext)
                } else if (position == 1) {
                    fragment = HospitalDirectoryFragment(binding, applicationContext)
                } else if (position == 2) {
                    fragment = BedsideDirectoryFragment(binding, applicationContext)
                }
                return fragment!!
            }

            override fun getCount(): Int {
                return 3
            }

            override fun getPageTitle(position: Int): CharSequence? {
                var title: String? = null
                if (position == 0) {
                    title = "Tab-1"
                } else if (position == 1) {
                    title = "Tab-2"
                }
                return title
            }

        }

    class TAB {
        Remote, Bedside
    }

    interface FragmentSearchListener {
        fun onSearchFilter(search: String?, filter: String?)
        fun onTabSelected()
    }

    interface HospitalSearchListener {
        fun onSearchFilter(search: String?, filter: String?)
    }

    interface BedsideSearchListener {
        fun sendHospitalId(hospitalId: String?)
        fun onSearchFilter(search: String?, filter: String?)
        fun onTabSelected()
    }

