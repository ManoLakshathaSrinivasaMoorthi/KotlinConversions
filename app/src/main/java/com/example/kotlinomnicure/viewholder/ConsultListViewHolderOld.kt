package com.example.kotlinomnicure.viewholder

import android.annotation.SuppressLint
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.HomeActivity
import com.example.kotlinomnicure.databinding.ItemConsultListOldBinding
import com.example.kotlinomnicure.interfaces.OnListItemClickListener
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import java.lang.Exception
import java.util.*

class ConsultListViewHolderOld(itemBinding: ItemConsultListOldBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    private val TAG = ConsultListViewHolderOld::class.java.simpleName
    private var itemClickListener: OnListItemClickListener? = null
    private var itemBinding: ItemConsultListOldBinding? = itemBinding

    init {
        itemClickListener = itemBinding.root.context as HomeActivity
    }

    fun setOnClickListeners() {
        itemBinding?.chatContentView?.setOnClickListener { }
        itemBinding?.inviteBtn?.setOnClickListener { itemBinding?.inviteBtn?.isEnabled = false }
        itemBinding?.idArrowIconView?.setOnClickListener {
            itemClickListener?.onArrowDropDownClick(adapterPosition)
        }
        itemBinding?.resetAcuity?.setOnClickListener {
            itemClickListener?.onResetAcuityClick(adapterPosition)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(
        consultProvider: ConsultProvider, selectedTab: Int, filterPatientStatus: Constants.PatientStatus?,
        searchQueryStr: String?, expandedPosition: Int
    ) {
        if (consultProvider.getId() == null || TextUtils.isEmpty(consultProvider.getName())) {
            itemBinding?.cardview?.visibility = View.GONE
            return
        }
        if (filterPatientStatus != null && consultProvider.getStatus() !== filterPatientStatus) {
            itemBinding?.cardview?.visibility = View.GONE
            return
        }
        if (selectedTab == TAB.Active.ordinal) {
            if (consultProvider.getStatus() === Constants.PatientStatus.Completed) {
                itemBinding?.cardview?.visibility = View.GONE
                return
            }
        } else {
            if (consultProvider.getStatus() === Constants.PatientStatus.Active || consultProvider.getStatus() === Constants.PatientStatus.Pending || consultProvider.getStatus() === Constants.PatientStatus.Invited || consultProvider.getStatus() === Constants.PatientStatus.Patient) {
                itemBinding?.cardview?.visibility = View.GONE
                return
            }
        }
        var firstName: String? = consultProvider.getFname()
        val lastName: String? = consultProvider.getLname()
        var nameStr = ""
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
            nameStr = "$firstName $lastName"
        } else if (!TextUtils.isEmpty(consultProvider.getName())) {
            nameStr = consultProvider.getName().toString()
            firstName = consultProvider.getName()
        } else if (!TextUtils.isEmpty(firstName)) {
            nameStr = firstName.toString()
        }
        if (searchQueryStr != null) {
            var isFound = false
            if (firstName != null && firstName.lowercase(Locale.getDefault())
                    .startsWith(searchQueryStr.lowercase(Locale.getDefault()))
                || lastName != null && lastName.lowercase(Locale.getDefault())
                    .startsWith(searchQueryStr.lowercase(Locale.getDefault()))
            ) {
                isFound = true
            }
            if (!isFound) {
                itemBinding?.cardview?.visibility = View.GONE
                return
            }
        }
        itemBinding?.cardview?.visibility = View.VISIBLE
        when {
            consultProvider.getText() != null -> {
                var txt: String = consultProvider.getText()!!
                if (consultProvider.getMsgName() != null) {
                    txt = "<b>" + consultProvider.getMsgName().toString() + "</b>" + ": " + txt
                }
                itemBinding?.messageTextView?.text = Html.fromHtml(txt)
                itemBinding?.messageTextView?.visibility = TextView.VISIBLE
            }
            consultProvider.getNote() != null -> {
                val txt: String = consultProvider.getNote() as Nothing
                itemBinding?.messageTextView?.text = txt
                itemBinding?.messageTextView?.visibility = TextView.VISIBLE
            }
            else -> {
                itemBinding?.messageTextView?.text = ""
                itemBinding?.messageTextView?.visibility = TextView.VISIBLE
            }
        }
        if (consultProvider.getDob() != null) {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            calendar.timeInMillis = consultProvider.getDob()!!
            val age = year - calendar[Calendar.YEAR]
            nameStr += " · $age"
        }
        nameStr += if ("Female".equals(consultProvider.getGender(), ignoreCase = true)) {
            " F"
        } else {
            " M"
        }
        itemBinding?.nameTextView?.text = nameStr
        if (consultProvider.getTime()!! > 0) {
            itemBinding?.timeTextView?.text = ChatUtils().getTimeAgo(consultProvider.getTime()!!)
        } else if (consultProvider.getJoiningTime() != null && consultProvider.getJoiningTime()!! > 0) {
            itemBinding?.timeTextView?.text = ChatUtils().getTimeAgo(consultProvider.getJoiningTime()!!)
        } else {
            itemBinding?.timeTextView?.text = ""
        }

        /*if (consultProvider.getMemberCount() == 1) {
            itemBinding.consultTextView.setText(consultProvider.getMemberCount()+ " "+ itemBinding.getRoot().getContext().getString(R.string.consultant));
        } else if (consultProvider.getMemberCount() > 1) {
            itemBinding.consultTextView.setText(consultProvider.getMemberCount()+ " " + itemBinding.getRoot().getContext().getString(R.string.consulatants));
        } else {
            if (consultProvider.getText() == null) {
                if(consultProvider.getMsgName() != null) {
                    itemBinding.messageTextView.setText(consultProvider.getMsgName() + ": "+itemBinding.getRoot().getContext().getString(R.string.patient_added) );
                } else {
                    itemBinding.messageTextView.setText(itemBinding.getRoot().getContext().getString(R.string.patient_added));
                }
                itemBinding.messageTextView.setVisibility(TextView.VISIBLE);
            }
        }*/if (consultProvider.getStatus() === Constants.PatientStatus.Pending) {
            //Todo: handle invited member OR new patient added case
            //itemBinding.consultTextView.setText(itemBinding.getRoot().getContext().getString(R.string.pending));
            //Show watch timer logo
            itemBinding?.unreadTextView?.visibility = TextView.GONE
            itemBinding?.inviteBtn?.visibility = TextView.GONE
            itemBinding?.watchTimerView?.visibility = TextView.VISIBLE
        } else if (consultProvider.getStatus() === Constants.PatientStatus.Invited) {
            itemBinding?.unreadTextView?.visibility = TextView.GONE
            itemBinding?.inviteBtn?.visibility = TextView.VISIBLE
            //itemBinding.consultTextView.setText("");
            itemBinding?.watchTimerView?.visibility = TextView.GONE
        } else {
            //Show badge count
            if (consultProvider.getUnread() > 0) {
                itemBinding?.unreadTextView?.text = consultProvider.getUnread().toString() + ""
                itemBinding?.unreadTextView?.visibility = TextView.VISIBLE
            } else {
                itemBinding?.unreadTextView?.visibility = TextView.GONE
            }
            /*if (consultProvider.getMemberCount() > 2){
                itemBinding.consultTextView.setText((consultProvider.getMemberCount()-1)+" " + itemBinding.getRoot().getContext().getString(R.string.consulatants));
            } else if (consultProvider.getMemberCount() == 2){
                itemBinding.consultTextView.setText("1 " + itemBinding.getRoot().getContext().getString(R.string.consultant));
            } else {
                itemBinding.consultTextView.setText("");
            }*/itemBinding?.watchTimerView?.visibility = TextView.GONE
            itemBinding?.inviteBtn?.visibility = TextView.GONE
        }
        if (expandedPosition != -1 && adapterPosition == expandedPosition) {
            itemBinding?.expandValuesView?.visibility = View.VISIBLE
            itemBinding?.idArrowIcon?.setImageResource(R.drawable.ic_collapse)
        } else {
            itemBinding?.expandValuesView?.visibility = View.GONE
            itemBinding?.idArrowIcon?.setImageResource(R.drawable.ic_expand)
        }
        if (consultProvider.getUrgent() != null && consultProvider.getUrgent()!!) {
            itemBinding?.urgentIcon?.visibility = View.VISIBLE
        } else {
            itemBinding?.urgentIcon?.visibility = View.GONE
        }
        setPatientReport(consultProvider)

        /*if (consultProvider.getStatus() == ChatUtils.PatientStatus.Completed){
            itemBinding.idDischargeView.setVisibility(View.VISIBLE);
            itemBinding.idArrowIcon.setVisibility(View.GONE);
        } else {
            itemBinding.idDischargeView.setVisibility(View.GONE);
            itemBinding.idArrowIcon.setVisibility(View.VISIBLE);
        }*/
    }

    private fun setPatientReport(provider: ConsultProvider) {
        val heartRate: Double? = provider.getHeartRate()
        val lowBP: Double? = provider.getArterialBloodPressureDiastolic()
        val highBP: Double? = provider.getArterialBloodPressureSystolic()
        val spo2: Double? = provider.getSpO2()
        val respRate: Double? = provider.getRespiratoryRate()
        val fiO2: Double? = provider.getFiO2()
        val isOxygenSupplement: Boolean = provider.getOxygenSupplement() == true
        val patientCondition: Constants.PatientCondition? = provider.getPatientCondition()
        val docBoxPatientId: String? = provider.getDocBoxPatientId()
        val isDocboxDataAvail =
            true
        if (!TextUtils.isEmpty(docBoxPatientId) || isDocboxDataAvail) {
            try {
                itemBinding?.idArrowIconView?.isEnabled = true
                //itemBinding.patientTypeIcon.setImageResource(R.drawable.ic_docbox);
                if (heartRate != null) {
                    itemBinding?.idHrValue?.text = heartRate.toInt().toString()
                } else {
                    itemBinding?.idHrValue?.text = "-"
                }
                if (lowBP != null && highBP != null) {
                    itemBinding?.idBpValue?.text = highBP.toInt().toString() + "/" + lowBP.toInt()
                } else if (lowBP != null) {
                    itemBinding?.idBpValue?.text = lowBP.toInt().toString()
                } else if (highBP != null) {
                    itemBinding?.idBpValue?.text = highBP.toInt().toString()
                } else {
                    itemBinding?.idBpValue?.text = "-"
                }
                if (spo2 != null) {
                    val color: Int = UtilityMethods().getSpo2TextColor(spo2.toInt())
                    val spo2Value = spo2.toInt().toString() + " %"
                    val builder: SpannableStringBuilder? = TextSpanBuilder().getSubscriptString(
                        spo2Value,
                        spo2Value.length - 1,
                        spo2Value.length
                    )
                    itemBinding?.root?.context?.resources?.getColor(color)?.let {
                        itemBinding?.idSpValue?.setTextColor(
                            it
                        )
                    }
                    //itemBinding.idSpo2Dot.setColorFilter(itemBinding.idSpo2Dot.getContext().getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
                    itemBinding?.idSpValue?.text = builder
                } else {
                    itemBinding?.idSpValue?.text = "-"
                }
                if (respRate != null) {
                    itemBinding?.idRrValue?.text = respRate.toInt().toString()
                } else {
                    itemBinding?.idRrValue?.text = "-"
                }
                if (fiO2 != null) {
                    itemBinding?.idFi02Value?.text = fiO2.toInt().toString() + "%"
                } else {
                    itemBinding?.idFi02Value?.text = "-"
                }
                if (isOxygenSupplement) {
                    itemBinding?.idOxygenValue?.text = itemBinding?.idOxygenValue?.context?.getString(R.string.yes)
                } else {
                    itemBinding?.idOxygenValue?.text = itemBinding?.idOxygenValue?.context?.getString(R.string.no)
                }
                if (patientCondition != null) {
                    when (patientCondition) {
                        Constants.PatientCondition.Alert -> {
                            itemBinding?.idAvpuValue?.text = "A"
                        }
                        Constants.PatientCondition.Voice -> {
                            itemBinding?.idAvpuValue?.text = "V"
                        }
                        Constants.PatientCondition.Pain -> {
                            itemBinding?.idAvpuValue?.text = "P"
                        }
                        Constants.PatientCondition.Unresponsive -> {
                            itemBinding?.idAvpuValue?.text = "U"
                        }
                    }
                } else {
                    itemBinding?.idAvpuValue?.text = "-"
                }
                var calAcuityLevel = true
                val map: HashMap<Long, Long>? =
                    itemBinding?.root?.context?.let {
                        PrefUtility().getPatientAcuityResetCounter(
                            it
                        )
                    }
                if (map != null && map.isNotEmpty()) {
                    val providerIdList = ArrayList<Long>()
                    for ((key) in map) {
                        if (key == provider.getId()) {
                            val resetTime = map[provider.getId()]!!
                            if (System.currentTimeMillis() - resetTime < Constants.THIRTEE_MINUTES_IN_MILIS) {
                                calAcuityLevel = false
                                itemBinding?.idArrowIconView?.resources
                                    ?.getColor(R.color.color_acuity_low)?.let {
                                        itemBinding?.idArrowIconView?.setBackgroundColor(
                                            it
                                        )
                                    }
                                itemBinding?.resetAcuity?.visibility = View.GONE
                            } else {
                                providerIdList.add(provider.getId()!!)
                            }
                        }
                    }
                    //reset the patient acuity counter
                    if (providerIdList.isNotEmpty()) {
                        for (id in providerIdList) {
                            map.remove(id)
                        }
                        PrefUtility().savePatientAcuityResetCounter(
                            itemBinding?.root?.context, map
                        )
                    }
                }
                if (calAcuityLevel) {
                    val acuityLevel: Constants.AcuityLevel = getAcuityLevel(provider)
                    Log.d(TAG, "AcuityLevel : " + acuityLevel.ordinal)
                    if (acuityLevel === Constants.AcuityLevel.Low) {
                        itemBinding?.idArrowIconView?.resources
                            ?.getColor(R.color.color_acuity_low)?.let {
                                itemBinding?.idArrowIconView?.setBackgroundColor(
                                    it
                                )
                            }
                        itemBinding?.resetAcuity?.visibility = View.GONE
                    } else if (acuityLevel === Constants.AcuityLevel.Medium) {
                        itemBinding?.idArrowIconView?.resources
                            ?.getColor(R.color.color_acuity_medium)?.let {
                                itemBinding?.idArrowIconView?.setBackgroundColor(
                                    it
                                )
                            }
                        itemBinding?.resetAcuity?.visibility = View.VISIBLE
                    } else if (acuityLevel === Constants.AcuityLevel.High) {
                        itemBinding?.idArrowIconView?.resources
                            ?.getColor(R.color.color_acuity_high)?.let {
                                itemBinding?.idArrowIconView?.setBackgroundColor(
                                    it
                                )
                            }
                        itemBinding?.resetAcuity?.visibility = View.VISIBLE
                    } else {
                        itemBinding?.idArrowIconView?.background = itemBinding?.idArrowIconView?.resources
                            ?.getDrawable(R.drawable.patient_value_card_gradient_bg)
                        itemBinding?.resetAcuity?.visibility = View.GONE
                    }
                }
                itemBinding?.idTempValue?.text = "-"
                var time = 0L
                if (provider.getTimeArterialBloodPressureDiastolic() != null && provider.getTimeArterialBloodPressureDiastolic()
                        !! > time
                ) {
                    time = provider.getTimeArterialBloodPressureDiastolic()!!
                }
                if (provider.getTimeArterialBloodPressureSystolic() != null && provider.getTimeArterialBloodPressureSystolic()
                        !! > time
                ) {
                    time = provider.getTimeArterialBloodPressureSystolic()!!
                }
                if (provider.getTimeHeartRate() != null && provider.getTimeHeartRate()
                        !! > time
                ) {
                    time = provider.getTimeHeartRate()!!
                }
                if (provider.getTimeRespiratoryRate() != null && provider.getTimeRespiratoryRate() !!> time
                ) {
                    time = provider.getTimeRespiratoryRate()!!
                }
                if (provider.getTimeSpO2() != null && provider.getTimeSpO2()!! > time) {
                    time = provider.getTimeSpO2()!!
                }
                if (provider.getTimeFiO2() != null && provider.getTimeFiO2()!! > time) {
                    time = provider.getTimeFiO2()!!
                }
                if (time > System.currentTimeMillis()) {
                    time = System.currentTimeMillis()
                }
                if (time > 0) {
                    itemBinding?.idMaticsUpdateTime?.text = itemBinding?.root?.context?.getString(R.string.sync_time)
                        .toString() + ": " + ChatUtils().getTimeAgo(time)
                    itemBinding?.idMaticsUpdateTime?.visibility = View.VISIBLE
                } else {
                    itemBinding?.idMaticsUpdateTime?.visibility = View.GONE
                }
            } catch (e: Exception) {
//                Log.e(TAG, "Exception:", e.getCause());
            }
        } else {
            itemBinding?.resetAcuity?.visibility = View.GONE
            itemBinding?.idArrowIconView?.background = itemBinding?.idArrowIconView?.resources
                ?.getDrawable(R.drawable.patient_value_card_gradient_bg)
            itemBinding?.idArrowIcon?.setImageResource(R.drawable.ic_expand_disabled)
            itemBinding?.idArrowIconView?.isEnabled = false
            itemBinding?.idHrValue?.text = "-"
            itemBinding?.idBpValue?.text = "-"
            itemBinding?.idSpValue?.text = "-"
            itemBinding?.idRrValue?.text = "-"
            itemBinding?.idFi02Value?.text = "-"
            itemBinding?.idTempValue?.text = "-"
        }
    }

    private fun getAcuityLevel(provider: ConsultProvider): Constants.AcuityLevel {
        val heartRate: Double? = provider.getHeartRate()
        val bp: Double? = provider.getArterialBloodPressureSystolic()
        val spo2: Double? = provider.getSpO2()
        val respRate: Double? = provider.getRespiratoryRate()
        val isOxygenSupplement: Boolean = provider.getOxygenSupplement() == true
        val avpu: Constants.PatientCondition? = provider.getPatientCondition()
        var score = 0
        if (heartRate != null) {
            if (heartRate in 91.0..110.0) {
                score = 1
            } else if (heartRate > 41 && heartRate <= 50 || heartRate in 111.0..130.0) {
                score = 2
            } else if (heartRate <= 40 || heartRate >= 131) {
                score = 3
            } //51-90 =0
        }
        if (bp != null) {
            if (bp in 101.0..110.0) {
                score += 1
            } else if (bp in 91.0..100.0) {
                score += 2
            } else if (bp <= 90 || bp >= 220) {
                score += 3
            } //111-219 - 0
        }
        if (spo2 != null) {
            when {
                spo2 in 94.0..95.0 -> {
                    score += 1
                }
                spo2 in 92.0..93.0 -> {
                    score += 2
                }
                spo2 <= 91 -> {
                    score += 3
                }
            } //>96% - 0
        }
        if (respRate != null) {
            if (respRate in 9.0..11.0) {
                score += 1
            } else if (respRate in 21.0..24.0) {
                score += 2
            } else if (respRate <= 8 || respRate >= 25) {
                score += 3
            } //12-20 - 0
        }
        if (isOxygenSupplement != null && isOxygenSupplement) {
            score += 2
        }
        if (avpu != null) {
            if (avpu == Constants.PatientCondition.Voice) {
                score += 3
            } //Alert - 0;
        }
        return when {
            score >= 3 -> {
                Constants.AcuityLevel.High
            }
            score in 1..2 -> {
                Constants.AcuityLevel.Medium
            }
            else -> {
                Constants.AcuityLevel.Low
            }
        }
    }

    private enum class TAB {
        Active, Patients
    }
}
