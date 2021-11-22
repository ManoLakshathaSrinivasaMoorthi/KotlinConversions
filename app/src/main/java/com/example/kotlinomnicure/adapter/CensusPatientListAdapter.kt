package com.example.kotlinomnicure.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.ActivityPatientCensusPatient
import com.example.kotlinomnicure.databinding.ItemCensusListBinding
import com.example.kotlinomnicure.interfaces.OnItemClickListener
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import omnicurekotlin.example.com.hospitalEndpoints.model.Patient
import java.util.*

class CensusPatientListAdapter(context: Context?, private var patientList: List<Patient>?, private var teamNameArrayList: List<String>?) : RecyclerView.Adapter<CensusPatientListAdapter.ConsultListViewHolder>(),Filterable {
    private val TAG = ConsultListViewHolder::class.java.simpleName
    private var itemClickListener: OnItemClickListener? = null
    private var patientListFiltered: List<Patient>? = null
    private var onSearchResultListener: OnSearchResultListener? = null

    init {
        itemClickListener = context as ActivityPatientCensusPatient?
    }

    fun setTeamNameArrayList(teamNameArrayList: List<String>?) {
        this.teamNameArrayList = teamNameArrayList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: ItemCensusListBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_census_list, parent, false)
        return ConsultListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ConsultListViewHolder, position: Int) {
        val consultProvider: Patient = patientList!![position]
        val provider: Patient? = getItem(holder.adapterPosition)
        holder.bind(consultProvider)
    }

    override fun getItemCount(): Int {
        return if (patientList == null || patientList!!.isEmpty()) {
            0
        } else patientList!!.size
    }

    fun getOnSearchResultListener(): OnSearchResultListener? {
        return onSearchResultListener
    }

    fun setOnSearchResultListener(onSearchResultListener: OnSearchResultListener?) {
        this.onSearchResultListener = onSearchResultListener
    }

    fun updateList(providerLists: List<Patient>?) {
        if (providerLists != null) {
            patientList = providerLists
            patientListFiltered = providerLists
            notifyDataSetChanged()
        }
    }

    fun getItem(position: Int): Patient? {
        return if (patientList != null && patientList!!.size > position && position >= 0) {
            patientList!![position]
        } else null
    }

    fun getItemIndex(providerId: Long?): Int {
        if (patientList == null && patientList!!.isEmpty()) {
            return -1
        }
        for ((count, provider) in patientList!!.withIndex()) {
            if (provider.getId() != null && provider.getId()!! == providerId) {
                return count
            }
        }
        return -1
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString().toLowerCase(Locale.ROOT)
                if (charString.isEmpty()) {
                    patientList = patientListFiltered
                } else {
                    val filteredList: MutableList<Patient> = ArrayList<Patient>()
                    for (provider in patientListFiltered!!) {
                        var firstName: String? = provider.getFname()
                        val lastName: String? = provider.getLname()
                        if (!TextUtils.isEmpty(provider.getName())) {
                            firstName = provider.getName()
                        }
                        if (firstName != null && firstName.trim { it <= ' ' }.toLowerCase(Locale.ROOT)
                                        .contains(charString)
                            || lastName != null && lastName.trim { it <= ' ' }.toLowerCase(Locale.ROOT)
                                        .contains(charString)
                        ) {
                            filteredList.add(provider)
                        }
                    }
                    patientList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = patientList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                patientList = results.values as ArrayList<Patient>
                if (getOnSearchResultListener() != null) {
                    getOnSearchResultListener()!!.onSearchResult(patientList!!.size)
                }
                notifyDataSetChanged()
            }
        }
    }

    interface OnSearchResultListener {
        fun onSearchResult(count: Int)
    }

    class ConsultListViewHolder(private var itemBinding: ItemCensusListBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private var itemClickListener: OnItemClickListener? = null
        private var teamNameArrayList: List<String>? = null
        private fun handleMultipleClick(view: View) {
            view.isEnabled = false
            Handler().postDelayed({ view.isEnabled = true }, 500)
        }

        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        fun bind(patient: Patient) {
            val role: String? = PrefUtility().getRole(itemBinding.root.context)
            val strDesignation: String? = PrefUtility().getStringInPref(itemBinding.root.context,
                Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
            val providerId: Long? = PrefUtility().getProviderId(itemBinding.root.context)
            itemBinding.txtViewDetails.setOnClickListener {
                handleMultipleClick(itemBinding.txtViewDetails)
                itemClickListener?.onViewClick(adapterPosition)
                Log.d(TAG, "onDataChange: Chat activity started...")
            }
            itemBinding.txtStartConsultationSame.setOnClickListener {
                handleMultipleClick(itemBinding.txtStartConsultationSame)
                itemClickListener?.onClickStartConsultationSame(adapterPosition)
                Log.d(TAG, "StartConsultation Same")
            }
            itemBinding.txtStartConsultationOther.setOnClickListener {
                handleMultipleClick(itemBinding.txtStartConsultationOther)
                itemClickListener?.onClickStartConsultationOther(adapterPosition)
                Log.d(TAG, "StartConsultation Other")
            }
            var time = 0L
            if (patient.getInviteTime() != null && patient.getInviteTime()!! > 0) {
                time = patient.getInviteTime()!!
            } else if (patient.getJoiningTime() != null && patient.getJoiningTime()!! > 0) {
                time = patient.getJoiningTime()!!
            }
            itemBinding.txtName.text = patient.getName()
            itemBinding.txtWard.text = patient.getWardName()
            if (!TextUtils.isEmpty(patient.getStatus())
                && patient.getStatus()?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Exit)) == true
            ) {
                itemBinding.txtStatus.text = "Discharged"
            } else if (!TextUtils.isEmpty(patient.getStatus())
                && patient.getStatus()?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Discharged)) == true
            ) {
                itemBinding.txtStatus.text = "Completed"
            } else if (!TextUtils.isEmpty(patient.getStatus())
                && patient.getStatus()?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Handoff)) == true
            ) {
                if (role == "BD") {
                    if (!TextUtils.isEmpty(patient.getBdProviderId()) && patient.getBdProviderId()
                            ?.contentEquals(providerId.toString())!!
                    ) {
                        itemBinding.txtStatus.text = "Active"
                    } else {
                        itemBinding.txtStatus.text = "Pending"
                    }
                } else if (role == "RD" && strDesignation.equals("MD/DO", ignoreCase = true)) {
                    if (!TextUtils.isEmpty(patient.getRdProviderId()) && patient.getRdProviderId()!!
                            .contentEquals(providerId.toString())
                    ) {
                        itemBinding.txtStatus.text = "Active"
                    } else {
                        itemBinding.txtStatus.text = "Pending"
                    }
                } else if (role == "RD" && !strDesignation.equals("MD/DO", ignoreCase = true)) {
                    if (!TextUtils.isEmpty(patient.getTeamName())) {
                        if (teamNameArrayList != null) {
                            Log.d(TAG, "GetTeamName  : " + teamNameArrayList + " : " + patient.getTeamName())
                            if (teamNameArrayList!!.contains(patient.getTeamName())) {
                                itemBinding.txtStatus.text = "Active"
                            } else {
                                itemBinding.txtStatus.text = "Pending"
                            }
                        } else {
                            itemBinding.txtStatus.text = "Pending"
                        }
                    } else {
                        itemBinding.txtStatus.text = "Pending"
                    }
                } else {
                    itemBinding.txtStatus.text = "Pending"
                }
            } else {
                itemBinding.txtStatus.text = patient.getStatus()
            }
            if (!TextUtils.isEmpty(patient.getStatus())
                && patient.getStatus()?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Pending)) == true
            ) {
                itemBinding.rlRequestTime.visibility = View.VISIBLE
                itemBinding.txtRequestTime.text = "Request sent : " + Utils().getTimeAgo(time)
            } else {
                itemBinding.rlRequestTime.visibility = View.GONE
            }
            if (role != null && role.equals(
                    Constants.ProviderRole.BD.toString(), ignoreCase = true) &&
                patient.getStatus()?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Discharged)) == true
            ) {
                if (!TextUtils.isEmpty(patient.getBdProviderId()) &&
                    patient.getBdProviderId()!!.contentEquals(providerId.toString())
                ) {
                    itemBinding.txtViewDetails.visibility = View.GONE
                    itemBinding.txtStartConsultationSame.visibility = View.VISIBLE
                    itemBinding.txtStartConsultationOther.visibility = View.GONE
                } else {
                    itemBinding.txtViewDetails.visibility = View.GONE
                    itemBinding.txtStartConsultationSame.visibility = View.GONE
                    itemBinding.txtStartConsultationOther.visibility = View.VISIBLE
                }
            } else {
                itemBinding.txtViewDetails.visibility = View.VISIBLE
                itemBinding.txtStartConsultationSame.visibility = View.GONE
                itemBinding.txtStartConsultationOther.visibility = View.GONE
            }
            val acuityLevel: Constants.AcuityLevel? = patient.getScore()?.let {
                Constants.AcuityLevel.valueOf(it)
            }
            if (acuityLevel != null) {
                when {
                    acuityLevel === Constants.AcuityLevel.Low -> {
                        itemBinding.txtAcuity.setBackground(itemBinding.txtAcuity.resources.getDrawable(R.drawable.acuity_level_low))
                        itemBinding.txtAcuity.setTextColor(itemBinding.txtAcuity.resources.getColor(R.color.acuity_low_border))
                        itemBinding.txtAcuity.text = Constants.AcuityLevel.Low.toString()
                    }
                    acuityLevel === Constants.AcuityLevel.Medium -> {
                        itemBinding.txtAcuity.background = itemBinding.txtAcuity.resources.getDrawable(R.drawable.acuity_level_med)
                        itemBinding.txtAcuity.setTextColor(itemBinding.txtAcuity.resources.getColor(R.color.acuity_med_border))
                        itemBinding.txtAcuity.text = Constants.AcuityLevel.Medium.toString().substring(0, 3)
                    }
                    acuityLevel === Constants.AcuityLevel.High -> {
                        itemBinding.txtAcuity.background = itemBinding.txtAcuity.resources.getDrawable(R.drawable.acuity_level_high)
                        itemBinding.txtAcuity.setTextColor(itemBinding.txtAcuity.resources.getColor(R.color.acuity_high_border))
                        itemBinding.txtAcuity.text = Constants.AcuityLevel.High.toString()
                    }
                }
            } else {
                itemBinding.txtAcuity.background = itemBinding.txtAcuity.resources.getDrawable(R.drawable.acuity_level_low)
                itemBinding.txtAcuity.setTextColor(itemBinding.txtAcuity.resources.getColor(R.color.acuity_low_border))
                itemBinding.txtAcuity.text = Constants.AcuityLevel.NA.toString()
            }
        }

    }

}



