package com.example.dailytasksamplepoc.kotlinomnicure.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityRemoteProviderDirectoryBinding
import com.example.dailytasksamplepoc.databinding.FragmentHospitalDirectoryBinding
import com.example.kotlinomnicure.activity.RemoteProviderDirectoryActivity
import com.example.dailytasksamplepoc.kotlinomnicure.adapter.HospitalDirectoryAdapter
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HomeViewModel
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import java.util.*


class HospitalDirectoryFragment(binding: Any?, applicationContext: Any?) : Fragment() {
    var binding: FragmentHospitalDirectoryBinding? = null
    var activityBinding: ActivityRemoteProviderDirectoryBinding? = null
    var ctx: Context? = null
    var filteredList: List<Hospital> = ArrayList<Hospital>()
    var hospitalList: List<Hospital> = ArrayList<Hospital>()
    var addressFilterList = ArrayList<String>()
    var search = ""
    var filter:kotlin.String? = "all"
    var isPageVisible = false
    var hospitalDirectoryAdapter: HospitalDirectoryAdapter? = null
    var addressFilterListener: HospitalDirectoryFragment.AddressFilterListener? =
        null
    private var viewModel: HomeViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        binding = FragmentHospitalDirectoryBinding.inflate(inflater, container, false)
        binding!!.noRecordsLayout.visibility = View.VISIBLE
        binding!!.recyclerView.visibility = View.GONE

        binding!!.swipeLayout.setOnRefreshListener {
            fetchHospital()
            binding!!.swipeLayout.isRefreshing = false
        }

        return binding!!.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        fetchHospital()
        searchListener()
    }

    fun searchListener() {
        (activity as RemoteProviderDirectoryActivity?)?.setHospitalSearchListener(object :
            RemoteProviderDirectoryActivity.HospitalSearchListener {


            override fun onSearchFilter(search: String?, filter: String?) {
                if (search != null) {
                    if (filter != null) {
                        filterList(search, filter)
                    }
                }
            }
        })
    }

    private fun fetchHospital() {
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
        val providerId: Long? = activity?.let { PrefUtility().getProviderId(it) }
        providerId?.let { viewModel!!.getHospitalList(it) }!!.observe(viewLifecycleOwner, { listResponse ->
//            System.out.println("hospitalResponse1--> " + new Gson().toJson(listResponse));
            var erroMsg = ""
            if (listResponse != null && listResponse.getStatus() != null && listResponse.getStatus()!!) {
                hospitalList = listResponse.getHospitalList() as List<Hospital>
                filterList(search, filter!!)
            } else {
                erroMsg = requireContext().getString(R.string.directory_list_empty)
            }

            if (!TextUtils.isEmpty(erroMsg)) {

                val errMsg: String? =
                    context?.let { ErrorMessages().getErrorMessage(it, erroMsg, Constants.API.getProviders) }

            }
        })
    }

    fun filterList(searchText: String, filterType: String) {
        filteredList.clear()
        search = searchText
        filter = filterType
        addressFilterList.clear()
        for (i in hospitalList.indices) {
            val p = hospitalList[i]
            addressFilterList.add(p.getSubRegionName()!!)
            var name = p.getName()
            var address = p.getSubRegionName()
            if (!TextUtils.isEmpty(p.getName())) {
                name = p.getName()
            }
            if (!TextUtils.isEmpty(p.getSubRegionName())) {
                address = p.getSubRegionName()
            }
            var searchCondition = true
            if (searchText.trim { it <= ' ' }.length > 0) {
                searchCondition = (name != null && name.trim { it <= ' ' }.toLowerCase()
                    .startsWith(searchText.toLowerCase())
                        || address != null && address.trim { it <= ' ' }.toLowerCase()
                    .startsWith(searchText.toLowerCase()))
            }
            var filterCondition = true
            if (!filterType.equals("all", ignoreCase = true)) {
                filterCondition = p.getSubRegionName().equals(filterType,ignoreCase = true)
            }
            if (filterCondition && searchCondition) {
                filteredList.add(p)
            }
        }
        if (addressFilterList.size > 0) {

            addressFilterList = ArrayList(LinkedHashSet(addressFilterList))
            Collections.sort(addressFilterList)
            addressFilterListener!!.addressFilterReady(addressFilterList)

        }
        setMenuVisibility(isPageVisible)
    }

    override fun setMenuVisibility(isvisible: Boolean) {
        super.setMenuVisibility(isvisible)
        isPageVisible = isvisible
        if (isvisible) {
            if (filteredList != null && !filteredList.isEmpty() && filteredList.size > 0) {
                setEmptyScreen(false)
                setDirectoryAdapter(filteredList)
            } else {
                setEmptyScreen(true)
            }
        }
    }

    private fun setDirectoryAdapter(hospitals: List<Hospital>?) {
        if (hospitals == null || hospitals.isEmpty()) {
            return
        }
        hospitalDirectoryAdapter = HospitalDirectoryAdapter(context, hospitals)
        hospitalDirectoryAdapter!!.setListener(activity as HospitalDirectoryAdapter.HospitalClickListener?)
        binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
        binding!!.recyclerView.adapter = hospitalDirectoryAdapter
        hospitalDirectoryAdapter!!.notifyDataSetChanged()
    }

    private fun setEmptyScreen(flag: Boolean) {
        activityBinding!!.hospitalFilterLayout.visibility = View.GONE
        activityBinding!!.bedsideFilterLayout.visibility = View.GONE
        activityBinding!!.remoteFilterLayout.visibility = View.GONE
        val text: String
        if (!filter.equals("all", ignoreCase = true) || search.length > 0) {
            text = requireContext().getString(R.string.no_results_for_filter)
            binding!!.noRecordTitle.visibility = View.VISIBLE
        } else {
            text = requireContext().getString(R.string.no_hospital_found)
            binding!!.noRecordTitle.visibility = View.GONE
        }
        binding!!.noRecordsText.text = text
        if (flag) {
            binding!!.recyclerView.visibility = View.GONE
            binding!!.noRecordsLayout.visibility = View.VISIBLE
            if (filter.equals("all", ignoreCase = true) && search.length <= 0) {
                activityBinding!!.hospitalFilterLayout.visibility = View.GONE
            }
            if (!filter.equals("all", ignoreCase = true)) {
                activityBinding!!.hospitalFilterLayout.visibility = View.VISIBLE
            }
        } else {
            binding!!.recyclerView.visibility = View.VISIBLE
            binding!!.noRecordsLayout.visibility = View.GONE
            activityBinding!!.hospitalFilterLayout.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        addressFilterListener = activity as AddressFilterListener?
    }

    interface AddressFilterListener {
        fun addressFilterReady(addressArr: ArrayList<String>)
    }

}