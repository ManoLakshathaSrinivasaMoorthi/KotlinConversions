package com.example.kotlinomnicure.helper

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnGroupClickListener
import androidx.core.view.GravityCompat
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.DrawerActivity
import com.example.kotlinomnicure.databinding.ActivityDrawerBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import java.util.*

class DirectoryListHelper {

    private val TAG = DirectoryListHelper::class.java.simpleName
    private var directoryMap: LinkedHashMap<String, MutableList<Provider>?>? = null
    private var binding: ActivityDrawerBinding? = null
    private var viewModel: HomeViewModel? = null
    private var context: Context? = null
    private var callbackDirectory: CallbackDirectory? = null
    private var lastExpandedGroupPosition = 0 //as 1st group default expanded, so default value should be 0 else -1


    interface CallbackDirectory {
        fun onClickProvierItem(provider: Provider?)
    }

    constructor(binding: ActivityDrawerBinding, viewModel: HomeViewModel?, callbackDirectory: CallbackDirectory?) {
        context = binding.root.context
        this.binding = binding
        this.viewModel = viewModel
        directoryMap = LinkedHashMap<String, MutableList<Provider>?>()
        this.callbackDirectory = callbackDirectory
        fetchDirectory()
    }


    private fun fetchDirectory() {

        if (!context?.let { UtilityMethods().isInternetConnected(it) }!!) {
            binding?.getRoot()?.let { UtilityMethods().showInternetError(it, Snackbar.LENGTH_LONG) }
            return
        }
        val providerId: Long? = PrefUtility().getProviderId(context!!)
        val token: String? = PrefUtility().getStringInPref(context!!, Constants.SharedPrefConstants.TOKEN, "")
        binding?.idList?.rightNavTitle?.setText( context?.getString(R.string.directory))
        providerId?.let {
            token?.let { it1 ->
                viewModel?.getProviderList(it, it1, Constants.ProviderRole.RD.toString())
                    ?.observe(context as DrawerActivity) { listResponse ->

                        var erroMsg = ""
                        if (listResponse != null && listResponse.getStatus() != null && listResponse.getStatus()!!) {
                            if (listResponse.getProviderList() != null && !listResponse.getProviderList()!!
                                    .isEmpty()
                            ) {
                                setDirectoryList(listResponse.getProviderList() as List<Provider>)
                            } else {
                                erroMsg = context!!.getString(R.string.directory_list_empty)
                            }
                        } else {
                            erroMsg = context!!.getString(R.string.directory_list_empty)
                        }
                        if (!TextUtils.isEmpty(erroMsg)) {
                            binding?.idList?.idDirEmptyView?.setVisibility(View.VISIBLE)
                            binding?.idList?.idExapandableListview?.visibility = View.GONE
                            //String errMsg = ErrorMessages.getErrorMessage(context,listResponse.getErrorMessage(),Constants.API.getProviders);
                            //UtilityMethods.showErrorSnackBar(binding.container,errMsg, Snackbar.LENGTH_LONG);
                        }
                    }
            }
        }
    }

    private fun setDirectoryList(providerList: List<Provider>) {
        if (directoryMap != null) {
            directoryMap!!.clear()
        }
        val statusActiveProviders: MutableList<Provider> = ArrayList<Provider>()
        val providers: MutableList<Provider> = ArrayList<Provider>()
        val hospitalName = context!!.getString(R.string.remote_provider_directory)
        for (i in providerList.indices) {
            val provider: Provider = providerList[i]
            //ignore self
            if (provider.getId()?.equals(PrefUtility().getProviderId(context!!)) == true) {
                continue
            }
            if (TextUtils.isEmpty(provider.getHospital())) {
                continue
            }
            if (provider.getStatus().equals(Constants.ProviderStatus.Active.toString()) || provider.getStatus()
                    .equals(Constants.ProviderStatus.AutoLock.toString())
                && !TextUtils.isEmpty(provider.getFcmKey())
            ) {
                statusActiveProviders.add(provider)
            } else {
                providers.add(provider)
            }
        }
        if (!statusActiveProviders.isEmpty()) {
            Collections.sort(statusActiveProviders,
                Comparator<Any?> { provider1, provider2 ->
                    provider1().getName().compareToIgnoreCase(provider2.getName())
                })
        }
        if (!providers.isEmpty()) {
            Collections.sort(providers,
                Comparator<Any?> { provider1, provider2 ->
                    provider1.getName().compareToIgnoreCase(provider2.getName())
                })
        }
        if (!statusActiveProviders.isEmpty()) {
            if (directoryMap != null) {
                directoryMap!![hospitalName] = statusActiveProviders
            }
        }
        if (directoryMap != null) {
            if (directoryMap!![hospitalName] == null) {
                directoryMap!![hospitalName] = providers
            } else {
                val providerList1: MutableList<Provider> = directoryMap!![hospitalName]!!
                providerList1.addAll(providers)
                directoryMap!![hospitalName] = providerList1
            }
            setDirectoryAdapter(directoryMap)
        }
    }

    private fun setDirectoryAdapter(directoryMap: HashMap<String, MutableList<Provider>?>?) {
        if (directoryMap == null || directoryMap.isEmpty()) {
            return
        }
        val hospitalNames = ArrayList(directoryMap.keys)
        if (binding?.idList?.idExapandableListview?.getExpandableListAdapter() == null) {
            val directoryAdapter = DirectoryExpandableListAdapter(context)
            directoryAdapter.setItems(hospitalNames, directoryMap)
            binding?.idList?.idExapandableListview?.setAdapter(directoryAdapter)
            binding?.idList?.idExapandableListview?.expandGroup(lastExpandedGroupPosition) //1st group default exapanded
        } else {
            val directoryAdapter: DirectoryExpandableListAdapter =
                binding?.idList.idExapandableListview.expandableListAdapter as DirectoryExpandableListAdapter
            directoryAdapter.setItems(hospitalNames, directoryMap)
            lastExpandedGroupPosition = directoryAdapter.getExpandedGroupPos()
            directoryAdapter.notifyDataSetChanged()
        }
        binding?.idList?.idExapandableListview.setOnGroupClickListener(OnGroupClickListener { expandableListView, view, groupPos, id ->
            if (binding?.idList?.idExapandableListview.isGroupExpanded(groupPos)) {
                binding?.idList?.idExapandableListview.collapseGroup(groupPos)
            } else {
                binding?.idList?.idExapandableListview.expandGroup(groupPos, true)
                if (lastExpandedGroupPosition != -1 && lastExpandedGroupPosition != groupPos) {
                    binding?.idList?.idExapandableListview.collapseGroup(lastExpandedGroupPosition)
                }
            }
            lastExpandedGroupPosition = groupPos
            true
        })
        binding?.idList?.idExapandableListview.setOnChildClickListener { expandableListView, view, groupPos, childPos, id ->
//            Log.i(TAG, "onChildClick: ");
            val hospitalName = hospitalNames[groupPos]
            val provider: Provider = directoryMap[hospitalName]!![childPos]
            //            Log.i(TAG, "Hospital Name : " + hospitalName);
//            Log.i(TAG, "Provider Name : " + provider.getName());
            if (callbackDirectory != null) {
                callbackDirectory!!.onClickProvierItem(provider)
            }
            binding?.drawerLayout?.closeDrawer(GravityCompat.END)
            false
        }
    }
}
