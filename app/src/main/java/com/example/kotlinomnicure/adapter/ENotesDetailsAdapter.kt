package com.example.kotlinomnicure.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.activity.EncUtil
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.EnotesDetailsItemBinding
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.model.HandOffList
import com.example.kotlinomnicure.utils.AESUtils
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import omnicurekotlin.example.com.providerEndpoints.model.Patient
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.split as split

class ENotesDetailsAdapter(
    applicationContext: Context,
    messagesList: List<HandOffList?>?,
    patientDetails: Patient?,
    fragName: String?
) : RecyclerView.Adapter<ENotesDetailsAdapter.ViewHolder>() {
    var context: Context? = null
    var messagesList: List<HandOffList>? = null
    var type: String? = null
    var patientDetails: Patient? = null
    var encKey = ""

    fun ENotesDetailsAdapter(
        context: Context?,
        list: List<HandOffList>?,
        pat: Patient?,
        t: String?,
    ) {
        this.context = context
        this.messagesList = list
        this.patientDetails = pat
        type = t
        EncUtil().generateKey(this.context)
        encKey = context?.let { PrefUtility().getAESAPIKey(it).toString() }.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: EnotesDetailsItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.enotes_details_item, parent, false)
        return ViewHolder(itemBinding)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.llSummarylayout.setOnClickListener(View.OnClickListener { listener!!.detailsClick() })
        var strGender = ""
        var strPhone = ""
        var strWard = ""
        val message: HandOffList = messagesList!![position]
        patientDetails?.getScore()?.let { Constants.AcuityLevel.valueOf(it) }?.let {
            patientDetails?.getUrgent()?.let { it1 ->
                UtilityMethods().displayPatientStatusComponent(context as Activity,
                    holder.itemBinding.statusStub,
                    it1,
                    patientDetails?.getStatus().equals(Constants.PatientStatus.Pending.toString(),ignoreCase = true),
                    it)
            }
        }
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        calendar.timeInMillis = patientDetails?.getDob()!!
        val agee = year - calendar[Calendar.YEAR]
        val age = agee.toString()
        val dot = " <b>\u00b7</b> "
        val timeInMillis: Long = patientDetails?.getDob()!!
        val dateString = SimpleDateFormat("MM-dd-yyyy").format(Date(timeInMillis))
        val strDob = dot + dateString
        if (!TextUtils.isEmpty(patientDetails?.getGender())) {
            strGender = dot + patientDetails?.getGender()
        }
        if (patientDetails?.getGender().equals("Male")) {
            strGender = dot + "M"
        } else if (patientDetails?.getGender().equals("Female",ignoreCase = true)) {
            strGender = dot + "F"
        }
        strPhone = if (!TextUtils.isEmpty(patientDetails?.getPhone()) &&
            !patientDetails?.getPhone().equals("null",ignoreCase = true)
        ) {
            dot + (patientDetails?.getPhone()  )
        } else {
            ""
        }
        strWard = if (!TextUtils.isEmpty(patientDetails!!.wardName) &&
            !patientDetails!!.wardName.equals("null",ignoreCase = true)
        ) {
            dot + patientDetails!!.wardName
        } else {
            ""
        }
        val strComplaint: String? = patientDetails!!.getNote()
        var stringComplaint = strComplaint
        if (strComplaint!!.contains(":")) {
            stringComplaint = strComplaint.substring(strComplaint.indexOf(":") + 1)
        }
        holder.itemBinding.txtPatientName.setText(patientDetails!!.getName())
        holder.itemBinding.txtAge.setText(Html.fromHtml(age + strGender + strDob + strPhone))
        holder.itemBinding.txtLocation.setText(Html.fromHtml(patientDetails!!.getHospital()
            .toString() + strWard))
        if (!TextUtils.isEmpty(patientDetails!!.recordNumber)) {
            holder.itemBinding.txtMRNNumber.setText(Html.fromHtml("MRN&nbsp;" + patientDetails!!.recordNumber))
        } else {
            holder.itemBinding.txtMRNNumber.setText("MRN ")
        }
        holder.itemBinding.sender.text = message.senderName
        holder.itemBinding.txtTime.setText(message.time?.let { Utils().timestampToDateYYYY(it) })
        holder.itemBinding.assessmentLayout.setVisibility(View.VISIBLE)
        holder.itemBinding.planLayout.setVisibility(View.VISIBLE)
        holder.itemBinding.newProviderLayout.setVisibility(View.GONE)
        var txt: String? = message.message
        if (message.subType.equals("Summary",ignoreCase = true)) {
            txt = AESUtils().decryptData(message.message!!, encKey)


            val result = txt.split("\n  \n", 2)
            holder.itemBinding.assessPlanText.setText(result[0])
            holder.itemBinding.planText.setText(result[1])
        } else if (message.subType.equals("Handoff",ignoreCase = true)) {
            txt = message.message?.let { AESUtils().decryptData(it, encKey) }
            holder.itemBinding.assessmentLayout.setVisibility(View.GONE)
            holder.itemBinding.newProviderLayout.setVisibility(View.VISIBLE)
            if (message.accepterName!= null) {
                holder.itemBinding.acceptedProvider.setText(message.accepterName)
            } else {
                holder.itemBinding.acceptedProvider.setText("-")
            }
            holder.itemBinding.planText.setText(txt)
        } else if (message.subType.equals("Progress",ignoreCase = true)) {
            txt = message.message?.let { AESUtils().decryptData(it, encKey) }

            holder.itemBinding.assessmentLayout.setVisibility(View.GONE)
            if (message.title == null || TextUtils.isEmpty(message.title)) {
                holder.itemBinding.planTitle.setVisibility(View.GONE)
            } else {
                holder.itemBinding.planTitle.setText(message.title)
                holder.itemBinding.planTitle.setVisibility(View.VISIBLE)
            }
            holder.itemBinding.planText.setText(txt)
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return messagesList!!.size
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }


    class ViewHolder(itemBinding: EnotesDetailsItemBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        val itemBinding: EnotesDetailsItemBinding

        init {
            this.itemBinding = itemBinding
        }
    }

    var listener: DetailsClick? = null
    @JvmName("setListener1")
    fun setListener(l: DetailsClick?) {
        listener = l
    }

    interface DetailsClick {
        fun detailsClick()
    }
}