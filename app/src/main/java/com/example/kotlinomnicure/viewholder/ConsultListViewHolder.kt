package com.example.kotlinomnicure.viewholder

import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.activity.HomeActivity
import com.example.kotlinomnicure.databinding.ItemConsultListBinding
import com.example.kotlinomnicure.interfaces.OnListItemClickListener
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.Constants
import java.util.*

class ConsultListViewHolder(itemBinding: ItemConsultListBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    private val TAG: String = ConsultListViewHolder::class.java.simpleName
    private var itemClickListener: OnListItemClickListener? = null
    private var itemBinding: ItemConsultListBinding? = itemBinding

    init {
        itemClickListener = itemBinding.root.context as HomeActivity
    }

    fun setOnClickListeners() {
        itemBinding?.containerView?.setOnClickListener {
            itemClickListener?.onClickChatView(
                adapterPosition,
                ConsultProvider()
            )
        }
        itemBinding?.inviteBtn?.setOnClickListener { itemBinding?.inviteBtn?.isEnabled = false }
    }

    fun bind(
        consultProvider: ConsultProvider,
        selectedTab: Int,
        filterPatientStatus: Constants.PatientStatus?,
        searchQueryStr: String?
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

//        if (consultProvider.getTime() > 0) {
//            itemBinding.timeTextView.setText(ChatUtils.getTimeAgo(consultProvider.getTime()));
//        } else if (consultProvider.getJoiningTime() != null && consultProvider.getJoiningTime() > 0) {
//            itemBinding.timeTextView.setText(ChatUtils.getTimeAgo(consultProvider.getJoiningTime()));
//        } else {
//            itemBinding.timeTextView.setText("");
//        }

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
            itemBinding?.messageView?.visibility = TextView.GONE
            itemBinding?.inviteBtn?.visibility = TextView.GONE
            itemBinding?.watchTimerView?.visibility = TextView.VISIBLE
        } else if (consultProvider.getStatus() === Constants.PatientStatus.Invited) {
            itemBinding?.messageView?.visibility = TextView.GONE
            itemBinding?.inviteBtn?.visibility = TextView.VISIBLE
            //itemBinding.consultTextView.setText("");
            itemBinding?.watchTimerView?.visibility = TextView.GONE
        } else {
            //Show badge count
            if (consultProvider.getUnread() > 0) {
//                itemBinding.messageView.setText(consultProvider.getUnread() + "");
                itemBinding?.messageView?.visibility = TextView.VISIBLE
            } else {
                itemBinding?.messageView?.visibility = TextView.GONE
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
        if (consultProvider.getUrgent() != null && consultProvider.getUrgent()!!) {
            itemBinding?.urgentIcon?.visibility = View.VISIBLE
        } else {
            itemBinding?.urgentIcon?.visibility = View.GONE
        }


        /*if (consultProvider.getStatus() == ChatUtils.PatientStatus.Completed){
            itemBinding.idDischargeView.setVisibility(View.VISIBLE);
            itemBinding.idArrowIcon.setVisibility(View.GONE);
        } else {
            itemBinding.idDischargeView.setVisibility(View.GONE);
            itemBinding.idArrowIcon.setVisibility(View.VISIBLE);
        }*/
    }

    fun bind(
        consultProvider: ConsultProvider,
        searchQueryStr: String?
    ) {
        if (consultProvider.getId() == null || TextUtils.isEmpty(consultProvider.getName())) {
            itemBinding?.cardview?.visibility = View.GONE
            return
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
                itemBinding?.messageTextView?.setText(txt)
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
        when {
            consultProvider.getStatus() === Constants.PatientStatus.Pending -> {
                //Todo: handle invited member OR new patient added case
                //itemBinding.consultTextView.setText(itemBinding.getRoot().getContext().getString(R.string.pending));
                //Show watch timer logo
                itemBinding?.messageView?.visibility = TextView.GONE
                itemBinding?.inviteBtn?.visibility = TextView.GONE
                itemBinding?.watchTimerView?.visibility = TextView.VISIBLE
            }
            consultProvider.getStatus() === Constants.PatientStatus.Invited -> {
                itemBinding?.messageView?.visibility = TextView.GONE
                itemBinding?.inviteBtn?.visibility = TextView.VISIBLE
                //itemBinding.consultTextView.setText("");
                itemBinding?.watchTimerView?.visibility = TextView.GONE
            }
            else -> {
                //Show badge count
                if (consultProvider.getUnread() > 0) {
    //                itemBinding.messageView.setText(consultProvider.getUnread() + "");
                    itemBinding?.messageView?.visibility = TextView.VISIBLE
                } else {
                    itemBinding?.messageView?.visibility = TextView.GONE
                }
                itemBinding?.watchTimerView?.visibility = TextView.GONE
                itemBinding?.inviteBtn?.visibility = TextView.GONE
            }
        }
        if (consultProvider.getUrgent() != null && consultProvider.getUrgent()!!) {
            itemBinding?.urgentIcon?.visibility = View.VISIBLE
        } else {
            itemBinding?.urgentIcon?.visibility = View.GONE
        }
    }


    private enum class TAB {
        Active, Patients
    }
}
