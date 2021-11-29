package com.example.kotlinomnicure.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityRemoteProviderDirectoryBinding
import com.example.dailytasksamplepoc.databinding.FragmentBedsideDirectoryBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.RemoteProviderDirectoryActivity
import com.example.dailytasksamplepoc.kotlinomnicure.adapter.BedsideDirectoryAdapter
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HomeViewModel
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model.Provider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityRemoteProviderDirectoryBinding
import com.example.kotlinomnicure.databinding.FragmentBedsideDirectoryBinding
import com.example.kotlinomnicure.utils.*
import java.util.*


class BedsideDirectoryFragment(binding: Any?, applicationContext: Any?) : Fragment() {
    var binding: FragmentBedsideDirectoryBinding? = null
    private val viewModel: HomeViewModel? = null
    var activityBinding: ActivityRemoteProviderDirectoryBinding? = null
    var ctx: Context? = null
    var hospitalId: String? = null
    var userRole: String? = null
    var filteredList: List<Provider> = ArrayList<Provider>()
    var providerList: List<Provider> = ArrayList<Provider>()
    var filter = "all"
    var search:kotlin.String? = ""
    var bedsideDirectoryAdapter: BedsideDirectoryAdapter? = null
    fun BedsideDirectoryFragment(bind: ActivityRemoteProviderDirectoryBinding?, ctx: Context?) {
        activityBinding = bind
        this.ctx = ctx
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding = FragmentBedsideDirectoryBinding.inflate(inflater, container, false)
        binding!!.swipeLayout.setOnRefreshListener(OnRefreshListener {
            fetchDirectory()
            binding!!.swipeLayout.setRefreshing(false)
        })
        return binding!!.getRoot()
    }
    fun searchListener() {
        (activity as RemoteProviderDirectoryActivity?)?.setBedsideSearchListener(object :
            RemoteProviderDirectoryActivity.BedsideSearchListener {


            override fun sendHospitalId(hospitalId: String?) {
                hospitalId = id.toString()
                activityBinding!!.appBar.setExpanded(true, true)
                fetchDirectory()
            }

            override fun onSearchFilter(search: String?, filter: String?) {
                if (search != null) {
                    filterList(search, filter)
                }
            }

            override fun onTabSelected() {
                fetchDirectory()
            }
        })
    }

    private fun fetchDirectory() {
        if (!activity?.let { UtilityMethods().isInternetConnected(it) }!!) {
            CustomSnackBar.make(
                activityBinding!!.root,
                activity,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        if (!userRole.equals("BD", ignoreCase = true)) {
            binding!!.container.visibility = View.GONE
        }
        (activity as RemoteProviderDirectoryActivity?)?.showProgressBar()
        if (viewModel != null) {
            hospitalId?.let {
                viewModel.getHospitalById(it)?.observe(viewLifecycleOwner) { listResponse ->

                    (activity as RemoteProviderDirectoryActivity?)?.dismissProgressBar()
                    if (!userRole.equals("BD", ignoreCase = true)) {
                        binding!!.container.visibility = View.VISIBLE
                    }
                    var erroMsg = ""
                    if (listResponse != null && listResponse.status!= null && listResponse.status!!) {
                        providerList = listResponse.getProviderList() as List<Provider>
                    } else {
                        providerList.clear()
                        erroMsg = requireContext().getString(R.string.directory_list_empty)
                    }
                    filterList(search!!, filter)
                    if (!TextUtils.isEmpty(erroMsg)) {
                        if (listResponse != null && !listResponse.errorMessage
                                .equals("No providers are available right now.",ignoreCase = true)
                        ) {
                            val errMsg: String? = context?.let {
                                ErrorMessages().getErrorMessage(
                                    it,
                                    listResponse.errorMessage,
                                    Constants.API.getProviders
                                )
                            }
                            if (errMsg != null) {
                                CustomSnackBar.make(
                                    activityBinding!!.container,
                                    activity,
                                    CustomSnackBar.WARNING,
                                    errMsg,
                                    CustomSnackBar.TOP,
                                    3000,
                                    0)
                                    ?.show();
                            }
                        }
                    }
                }
            }
        }
    }

    fun filterList(searchText: String, filterText: String?) {
        filteredList.clear()
        search = searchText
        filter = hospitalId!!
        for (p in providerList) {
//            omnicure.mvp.com.providerEndpoints.model.Provider bean = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) p)), omnicure.mvp.com.providerEndpoints.model.Provider.class);
            val bean: Provider = Gson().fromJson(
                Gson().toJson(p),
                Provider::class.java
            )
            if (bean != null && bean.getName() != null) {

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
                        (firstName != null && firstName.trim { it <= ' ' }.toLowerCase()
                            .startsWith(searchText.toLowerCase())
                                || lastName != null && lastName.trim { it <= ' ' }.toLowerCase()
                            .startsWith(searchText.toLowerCase()))
                }
                if (searchCondition) {
                    filteredList.add(bean)
                }
            }
        }


        if (filteredList != null && !filteredList.isEmpty() && filteredList.size > 0) {
            setEmptyScreen(false)
            setDirectoryList(filteredList)
        } else {
            setEmptyScreen(true)
        }
    }

    private fun setDirectoryList(providerList: List<Provider>) {
        val statusActiveProviders: MutableList<Provider> = ArrayList()
        val providers: MutableList<Provider> = ArrayList()
        for (i in providerList.indices) {
            val provider = providerList[i]
            //ignore self
            if (provider.getId()!!.equals(context?.let { PrefUtility().getProviderId(it) })) {
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
                    return p1?.getName()?.let { p0?.getName()?.compareTo(it,ignoreCase = true) }!!
                }
            })
        }
        if (!providers.isEmpty()) {
            Collections.sort(providers, object : Comparator<Provider?> {
                override fun compare(p0: Provider?, p1: Provider?): Int {
                    return p1.getName()?.let { p0?.getName()?.compareTo(it,ignoreCase = true) }!!
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
        bedsideDirectoryAdapter = BedsideDirectoryAdapter(requireActivity(), context, providers)
        bedsideDirectoryAdapter!!.setListener(activity as BedsideDirectoryAdapter.DirectoryClickListener?)
        binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
        binding!!.recyclerView.adapter = bedsideDirectoryAdapter
        bedsideDirectoryAdapter!!.notifyDataSetChanged()
    }

    private fun setEmptyScreen(flag: Boolean) {
        activityBinding!!.hospitalFilterLayout.visibility = View.GONE
        activityBinding!!.bedsideFilterLayout.visibility = View.GONE
        activityBinding!!.remoteFilterLayout.visibility = View.GONE
        val text: String
        if (search!!.length > 0) {
            text = requireContext().getString(R.string.no_results_for_filter)
            binding!!.noRecordTitle.visibility = View.VISIBLE
        } else {
            text = requireContext().getString(R.string.no_bedside_providers_found)
            binding!!.noRecordTitle.visibility = View.GONE
        }
        //        text = context.getString(R.string.no_bedside_providers_found);
        binding!!.noRecordsText.text = text
        if (flag) {
            (activity as RemoteProviderDirectoryActivity?).setAppBarScroll(false)
            binding!!.recyclerView.visibility = View.GONE
            binding!!.noRecordsLayout.visibility = View.VISIBLE
            if (filter.equals("all", ignoreCase = true) && search!!.length <= 0) {
                activityBinding!!.bedsideFilterLayout.visibility = View.GONE
            }
            if (!filter.equals("all", ignoreCase = true)) {
                activityBinding!!.bedsideFilterLayout.visibility = View.VISIBLE
            }
        } else {
            (activity as RemoteProviderDirectoryActivity?).setAppBarScroll(true)
            binding!!.recyclerView.visibility = View.VISIBLE
            binding!!.noRecordsLayout.visibility = View.GONE
            activityBinding!!.bedsideFilterLayout.visibility = View.VISIBLE
        }
    }


}