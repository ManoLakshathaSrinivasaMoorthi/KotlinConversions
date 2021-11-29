package com.example.dailytasksamplepoc.kotlinomnicure.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityRemoteProviderDirectoryBinding
import com.example.dailytasksamplepoc.databinding.FragmentRemoteDirectoryBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.RemoteProviderDirectoryActivity
import com.example.dailytasksamplepoc.kotlinomnicure.adapter.RemoteDirectoryAdapter
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HomeViewModel
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model.Provider
import java.util.*


class RemoteDirectoryFragment(binding: Any?, applicationContext: Any?) : Fragment() {

    private val TAG = RemoteDirectoryFragment::class.java.simpleName
    var filteredList: MutableList<Provider>? = ArrayList<Provider>()
    var providerList: List<Provider>? = ArrayList<Provider>()
    private var viewModel: HomeViewModel? = null
    private var remoteDirectoryAdapter: RemoteDirectoryAdapter? = null
    var activityBinding: ActivityRemoteProviderDirectoryBinding? = null
    private var ctx: Context? = null
    var filter = "all"
    var search:kotlin.String? = ""

    fun RemoteDirectoryFragment(bind: ActivityRemoteProviderDirectoryBinding?, ctx: Context?) {
        activityBinding = bind
        this.ctx = ctx
    }

    var binding: FragmentRemoteDirectoryBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        super.onCreateView(inflater!!, container, savedInstanceState)
        binding = FragmentRemoteDirectoryBinding.inflate(inflater, container, false)
        binding!!.swipeLayout.setOnRefreshListener(OnRefreshListener {
            fetchDirectory()
            binding!!.swipeLayout.setRefreshing(false)
        })
        return binding!!.getRoot()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        fetchDirectory()
        searchListener()
    }

    fun searchListener() {
        (activity as RemoteProviderDirectoryActivity?)?.setFragmentSearchListener(object :
            RemoteProviderDirectoryActivity.FragmentSearchListener {


            override fun onSearchFilter(search: String?, filter: String?) {
                if (search != null) {
                    if (filter != null) {
                        filterList(search, filter)
                    }
                }

            }

            override fun onTabSelected() {
                fetchDirectory()
            }
        })
    }

    private fun fetchDirectory() {
        (activity as RemoteProviderDirectoryActivity?)?.showProgressBar()
        if (!activity?.let { UtilityMethods().isInternetConnected(it) }!!) {
            CustomSnackBar.make(
                activityBinding?.getRoot(),
                activity,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        val providerId: Long? = activity?.let { PrefUtility().getProviderId(it) }
        val token: String? =
            activity?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.TOKEN, "") }
        viewModel?.getProviderList(providerId, token, Constants.ProviderRole.RD.toString())
            ?.observe(viewLifecycleOwner) { listResponse ->
                (activity as RemoteProviderDirectoryActivity?)?.dismissProgressBar()

                var erroMsg = ""
                if (listResponse != null && listResponse.getStatus() != null && listResponse.getStatus()!!) {
                    providerList = listResponse.getProviderList() as List<Provider>?
                    search?.let { filterList(it, filter) }
                } else {
                    erroMsg = requireContext().getString(R.string.directory_list_empty)
                }
                if (!TextUtils.isEmpty(erroMsg)) {
                    val errMsg: String? = context?.let {
                        ErrorMessages().getErrorMessage(
                            it,
                            listResponse?.getErrorMessage(),
                            Constants.API.getProviders
                        )
                    }
                    if (errMsg != null) {
                        CustomSnackBar.make(
                            activityBinding?.container,
                            activity,
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

    fun filterList(searchText: String, filterType: String) {
        filteredList!!.clear()
        search = searchText
        filter = filterType
        if (providerList == null) {
            providerList = ArrayList<Provider>()
        }
        for (p in providerList!!) {
            val bean: Provider =
                Gson().fromJson
            Gson().toJson(p),
        Provider::class.java)
            if (bean.getName() != null) {
                if (bean.getId()?.equals(activity?.let { PrefUtility().getProviderId(it) }) == true) {
                    continue
                }
                var firstName: String = bean.getFname()!!
                val lastName: String = bean.getLname()!!
                if (!TextUtils.isEmpty(bean.getName())) {
                    firstName = bean.getName()!!
                }
                var searchCondition = true
                if (searchText.trim { it <= ' ' }.length > 0) {
                    searchCondition =
                        (firstName.trim { it <= ' ' }.lowercase(Locale.getDefault())
                            .startsWith(searchText.lowercase(Locale.getDefault())) || lastName.trim { it <= ' ' }.toLowerCase()
                            .startsWith(searchText.lowercase(Locale.getDefault())))
                }
                var filterCondition = true
                if (!filterType.equals("all", ignoreCase = true)) {
                    filterCondition = bean.getRemoteProviderType().equals(filterType,ignoreCase = true)
                }
                if (filterCondition && searchCondition) {
                    filteredList!!.add(bean)
                }
            }
        }

        if (filteredList != null && !filteredList!!.isEmpty() && filteredList!!.size > 0) {
            setEmptyScreen(false)
            setDirectoryList(filteredList!!)
        } else {
            setEmptyScreen(true)
        }
    }

    private fun setDirectoryList(providerList: List<Provider>) {
        val statusActiveProviders: MutableList<Provider> = ArrayList<Provider>()
        val providers: MutableList<Provider> = ArrayList<Provider>()
        for (i in providerList.indices) {
            val provider: Provider = providerList[i]

            if (provider.getId()?.equals(context?.let { PrefUtility().getProviderId(it) }) == true) {
                continue
            }
            if (provider.getStatus()
                    .equals(Constants.ProviderStatus.Active.toString(),ignoreCase = true) || provider.getStatus()
                    .equals(Constants.ProviderStatus.AutoLock.toString(),ignoreCase = true)
                && !TextUtils.isEmpty(provider.getFcmKey())
            ) {
                statusActiveProviders.add(provider)
            } else {
                providers.add(provider)
            }
        }
        if (!statusActiveProviders.isEmpty()) {
            Collections.sort(statusActiveProviders, object : Comparator<Provider?> {

                override fun compare(p0: Provider?, p1: Provider?): Int {
                    if (p0 != null) {
                        if (p1 != null) {

                        }
                    }
                    if (p1 != null) {
                        if (p0 != null) {

                        }
                    }
                    return p1?.getName()?.let { p0?.getName()?.compareTo(it,ignoreCase = true) }!!
                }
            })
        }

        if (!providers.isEmpty()) {
            Collections.sort(providers, object : Comparator<Provider?> {


                override fun compare(p0: Provider?, p1: Provider?): Int {
                    if (p1 != null) {

                    }
                    if (p1 != null) {
                        if (p0 != null) {

                        }
                    }
                    return p1?.getName()?.let { p0?.getName()?.compareTo(it,ignoreCase = true) }!!
                }
            })
        }
        statusActiveProviders.addAll(providers)
        setDirectoryAdapter(statusActiveProviders)
    }

    private fun setDirectoryAdapter(providers: List<Provider>?) {
        if (providers == null || providers.isEmpty()) {
            return
        }
        remoteDirectoryAdapter = RemoteDirectoryAdapter(requireActivity(), context, providers)
        remoteDirectoryAdapter!!.setListener(activity as RemoteDirectoryAdapter.DirectoryClickListener?)
        binding!!.recyclerView.setLayoutManager(LinearLayoutManager(context))
        binding!!.recyclerView.setAdapter(remoteDirectoryAdapter)
        remoteDirectoryAdapter!!.notifyDataSetChanged()
    }

    private fun setEmptyScreen(flag: Boolean) {
        activityBinding!!.hospitalFilterLayout.setVisibility(View.GONE)
        activityBinding!!.bedsideFilterLayout.setVisibility(View.GONE)
        activityBinding!!.remoteFilterLayout.setVisibility(View.GONE)
        val text: String
        if (!filter.equals("all", ignoreCase = true) || search!!.length > 0) {
            text = requireContext().getString(R.string.no_results_for_filter)
            binding!!.noRecordTitle.setVisibility(View.VISIBLE)
        } else {
            text = requireContext().getString(R.string.no_remote_providers_found)
            binding!!.noRecordTitle.setVisibility(View.GONE)
        }
        binding!!.noRecordsText.setText(text)
        if (flag) {
            binding!!.recyclerView.setVisibility(View.GONE)
            binding!!.noRecordsLayout.setVisibility(View.VISIBLE)
            if (filter.equals("all", ignoreCase = true) && search!!.length <= 0) {
                activityBinding!!.remoteFilterLayout.setVisibility(View.GONE)
            }
            if (!filter.equals("all", ignoreCase = true)) {
                activityBinding!!.remoteFilterLayout.setVisibility(View.VISIBLE)
            }
        } else {
            binding!!.recyclerView.setVisibility(View.VISIBLE)
            binding!!.noRecordsLayout.setVisibility(View.GONE)
            activityBinding!!.remoteFilterLayout.setVisibility(View.VISIBLE)
        }
    }

    fun checkSelfPermissions(): Boolean {
        return checkSelfPermission(
            Manifest.permission.RECORD_AUDIO,
            ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO
        ) &&
                checkSelfPermission(
                    Manifest.permission.CAMERA,
                    ConstantApp.PERMISSION_REQ_ID_CAMERA
                )
    }

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {

        when (requestCode) {
            ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO -> {
                checkSelfPermission(
                    Manifest.permission.CAMERA,
                    ConstantApp.PERMISSION_REQ_ID_CAMERA
                )
            }
            ConstantApp.PERMISSION_REQ_ID_CAMERA -> {
            }
            else -> {
                Toast.makeText(context, "Please give permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
}