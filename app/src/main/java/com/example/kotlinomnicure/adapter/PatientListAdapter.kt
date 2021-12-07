package com.example.kotlinomnicure.adapter

import android.content.Context
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filter.FilterResults
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.HomeActivity
import com.example.kotlinomnicure.databinding.ItemConsultListBinding
import com.example.kotlinomnicure.interfaces.OnListItemClickListener
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import java.util.*

class PatientListAdapter: RecyclerView.Adapter<PatientListAdapter.ConsultListViewHolder> {

    private var itemClickListener: OnListItemClickListener? = null

    //int expandedPosition = -1;
    var expandedPatientId: Long? = null
    private var providerList: List<ConsultProvider>? = null
    private var originalProviderList: List<ConsultProvider>? = null
    private var onSearchResultListener: OnSearchResultListener? = null
    private var context: Context? = null


    constructor(context: Context?, providerList: List<ConsultProvider>?) {
        itemClickListener = context as HomeActivity?
        originalProviderList = providerList
        this.providerList = providerList
        this.context = context
    }
     constructor()
    fun isCompleted(consultProvider: ConsultProvider): Boolean {
        return consultProvider.getStatus()!!
            .equals(Constants.PatientStatus.Completed) || consultProvider.getStatus()!!
            .equals(Constants.PatientStatus.Discharged)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //View binding
        val itemBinding: ItemConsultListBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_consult_list, parent, false)
        val viewHolder = ConsultListViewHolder(context, itemBinding)

        // Invite button click listener
        itemBinding.inviteBtn.setOnClickListener { view ->
            itemBinding.inviteBtn.isEnabled = false
            itemClickListener!!.onClickInviteBtn(viewHolder)
        }
        // Details button click listener
        itemBinding.detailsBtn.setOnClickListener { view ->
//            itemBinding.detailsBtn.setEnabled(false);
            itemClickListener!!.onClickDetailsButton(viewHolder)
        }
        // Reconsult button click listener
        itemBinding.reconsultBtn.setOnClickListener { view ->
            itemBinding.reconsultBtn.isEnabled = false
            itemClickListener!!.onClickReconsultButton(viewHolder)
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: ConsultListViewHolder, position: Int) {
        // Consult provider object
        val consultProvider = providerList!![position]
        val provider = getItem(holder.adapterPosition)
        if (provider != null && expandedPatientId != null && expandedPatientId == provider.getId()) {
            holder.bind(consultProvider, expandedPatientId)
        } else {
            holder.bind(consultProvider, null)
        }
    }

    override fun getItemCount(): Int {
        return if (providerList == null || providerList!!.isEmpty()) {
            0
        } else providerList!!.size
    }

    /**
     * Getter of getOnSearchResultListener
     * @return
     */
    fun getOnSearchResultListener(): OnSearchResultListener? {
        return onSearchResultListener
    }

    /**
     * Setter of OnSearchResultListener
     * @param onSearchResultListener
     */
    fun setOnSearchResultListener(onSearchResultListener: OnSearchResultListener?) {
        this.onSearchResultListener = onSearchResultListener
    }

    /**
     * Provider list update
     * @param providerLists
     */
    fun updateList(providerLists: List<ConsultProvider>?) {
        if (providerLists != null) {
            providerList = providerLists
            originalProviderList = providerLists
            notifyDataSetChanged()
        }
    }

    /**
     * Getting the item based on the position
     * @param position
     * @return
     */
    fun getItem(position: Int): ConsultProvider? {
        return if (providerList != null && providerList!!.size > position && position >= 0) {
            providerList!![position]
        } else null
    }

    /**
     * Getting the item index based on provider Id
     * @param providerId
     * @return
     */
    fun getItemIndex(providerId: Long?): Int {
        if (providerList == null || providerList!!.isEmpty() || expandedPatientId == null) {
            return -1
        }
        var count = 0
        for (provider in providerList!!) {
            if (provider != null && provider.getId() != null && provider.getId()!!
                    .equals(providerId)
            ) {
                return count
            }
            count++
        }
        return -1
    }


    @JvmName("getExpandedPatientId1")
    fun getExpandedPatientId(): Long? {
        return expandedPatientId
    }

    /**
     * Filter the patient list using filterable
     * @return
     */
    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString().toLowerCase()
                if (charString.isEmpty()) {
                    //providerList = originalProviderList;
                } else {
                    val filteredList: MutableList<ConsultProvider> = ArrayList()
                    for (provider in originalProviderList!!) {
                        var firstName = provider.getFname()
                        val lastName = provider.getLname()
                        if (!TextUtils.isEmpty(provider.getName())) {
                            firstName = provider.getName()
                        }
                        if (firstName != null && firstName.trim { it <= ' ' }.toLowerCase()
                                .contains(charString)
                            || lastName != null && lastName.trim { it <= ' ' }.toLowerCase()
                                .contains(charString)
                        ) {
                            // Adding the filtered provider list
                            filteredList.add(provider)
                        }
                    }
                    providerList = filteredList
                }
                //Return the filtered results
                val filterResults = FilterResults()
                filterResults.values = providerList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                providerList = results.values as ArrayList<ConsultProvider>
                if (getOnSearchResultListener() != null) {
                    getOnSearchResultListener()!!.onSearchResult(providerList!!.size)
                }
                // Updating the adapter
                notifyDataSetChanged()
            }
        }
    }

    /**
     * Interface for search result listener
     */
    interface OnSearchResultListener {
        fun onSearchResult(count: Int)
    }

    /**
     * Consult list view holder
     */
    class ConsultListViewHolder(
        var context1: Context?, // Item consult list binding
        val itemBinding: ItemConsultListBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        // Handling the multiple click event
        private fun handleMultipleClick(view: View) {
            view.isEnabled = false
            Handler().postDelayed({ view.isEnabled = true }, 500)
        }

        // Binding the view with consult provider
        fun bind(consultProvider: ConsultProvider, expandedPatientId: Long?) {
            var firstName = consultProvider.getFname()
            val lastName = consultProvider.getLname()
            var nameStr = ""
            if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
                nameStr = firstName!!.trim { it <= ' ' } + " " + lastName!!.trim { it <= ' ' }
            } else if (!TextUtils.isEmpty(consultProvider.getName())) {
                nameStr = consultProvider.getName()!!.trim()
                firstName = consultProvider.getName()!!.trim()
            } else if (!TextUtils.isEmpty(firstName)) {
                nameStr = firstName!!.trim { it <= ' ' }
            }
            // Container view click listener
            itemBinding.containerView.setOnClickListener { view ->
                if (! PatientListAdapter().isCompleted(consultProvider)) {
                    handleMultipleClick(itemBinding.containerView)
                    PatientListAdapter().itemClickListener?.onClickChatView(
                        adapterPosition,
                        consultProvider
                    )
                }
            }
            // Dropdown arrow click listener
            itemBinding.dropdownArrow.setOnClickListener { view ->
//            itemBinding.detailsBtn.setEnabled(false);
                handleMultipleClick(itemBinding.dropdownArrow)
                PatientListAdapter().itemClickListener?.onArrowDropDownClick(adapterPosition)
            }

            // Name text click listener
            itemBinding.nameTextView.setOnClickListener { view ->
//            itemBinding.detailsBtn.setEnabled(false);
                handleMultipleClick(itemBinding.nameTextView)
                PatientListAdapter(). itemClickListener?.onArrowDropDownClick(adapterPosition)
            }
            var time = 0L
            if (consultProvider.getInviteTime() != null && consultProvider.getInviteTime()!! > 0) {
                time = consultProvider.getInviteTime()!!
            } else if (consultProvider.getTime() != null && consultProvider.getTime()!! > 0) {
                time = consultProvider.getTime()!!
            }
            // If completed
            if ( PatientListAdapter().isCompleted(consultProvider)) {
                if (consultProvider.getTime() != null && consultProvider.getTime()!! > 0) {
                    time = consultProvider.getTime()!!
                }
                itemBinding.cardview.visibility = View.GONE
                itemBinding.completedCardview.visibility = View.VISIBLE
                if (consultProvider.getCompleted_by() != null && !TextUtils.isEmpty(consultProvider.getCompleted_by())) {
                    itemBinding.completedConsultName.visibility = View.VISIBLE
                    val vals: MutableList<String> = consultProvider.getCompleted_by()!!.split(",").toMutableList()
                    var completedName = ""
                    if (vals.size > 1 && vals[0].length > 12) {
                        vals[0] = vals[0].substring(0, 12) + ".."
                    }
                    completedName = TextUtils.join(",", vals)
                    completedName =
                        completedName.substring(0, 1).toUpperCase() + completedName.substring(1)
                    // Completed consult name
                    itemBinding.completedConsultName.text =
                        Html.fromHtml("$completedName    <b>\u00b7</b>    ")
                } else {
                    itemBinding.completedConsultName.visibility = View.GONE
                }
                // Complete time text
                itemBinding.completedTimeText.setText(Utils().timestampToDate(time))
                if (nameStr.trim { it <= ' ' }.length > 15) {
                    nameStr = nameStr.substring(0, 15) + "..."
                }
                itemBinding.completedNameTextView.text = nameStr
                var status = ""
                if (consultProvider.getStatus()!!.equals(Constants.PatientStatus.Discharged)) {
                    status = "Discharged"
                } else if (consultProvider.getStatus()!!
                        .equals(Constants.PatientStatus.Completed)
                ) {
                    status = "Completed"
                }
                // Completed status text
                if (status.equals("Discharged", ignoreCase = true)) {
                    itemBinding.completedStatus.text =
                        context1!!.resources.getString(R.string.discharged)
                } else if (status.equals("Completed", ignoreCase = true)) {
                    itemBinding.completedStatus.text =
                        context1!!.resources.getString(R.string.completed)
                }

//                itemBinding.completedStatus.setText(status);
                val strDesignation: String = PrefUtility().getStringInPref(
                    itemBinding.root.context,
                    Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                    ""
                )

                /*    if (strDesignation.equalsIgnoreCase("md/do") || consultProvider.getStatus().equals(Constants.PatientStatus.Discharged)) {
                    itemBinding.reconsultBtn.setVisibility(View.GONE);
                } else {
                    itemBinding.reconsultBtn.setVisibility(View.VISIBLE);
                }*/
                // Role based reconsult button visibility
                val role: String? = PrefUtility().getRole(itemBinding.root.context)
                if (role != null && role.equals(
                        Constants.ProviderRole.BD.toString(),
                        ignoreCase = true
                    ) && consultProvider.getStatus()!!
                        .equals(Constants.PatientStatus.Completed)
                ) {
                    itemBinding.reconsultBtn.visibility = View.VISIBLE
                } else {
                    itemBinding.reconsultBtn.visibility = View.GONE
                }
                return
            } else {
                itemBinding.cardview.visibility = View.VISIBLE
                itemBinding.completedCardview.visibility = View.GONE
            }
            //  Hospital text view
            itemBinding.hospitalTextView.text = consultProvider.getHospital()
            // Ward text
            if (!TextUtils.isEmpty(consultProvider.getWardName())) {
                itemBinding.wardText.visibility = View.VISIBLE
                itemBinding.wardText.text =
                    Html.fromHtml("<b>\u00b7</b>   " + consultProvider.getWardName())
            } else {
                itemBinding.wardText.visibility = View.INVISIBLE
            }

            /*    if (consultProvider.getText() != null) {
             */
            /*String txt = consultProvider.getText();
                if (consultProvider.getMsgName() != null) {
                    txt = "<font color=black><b>" + consultProvider.getMsgName().trim() + "</b></font>".trim() + ": " + txt;
                }
                itemBinding.messageTextView.setText(Html.fromHtml(txt));
                itemBinding.messageTextView.setVisibility(TextView.VISIBLE);*/
            /*
            } */
            // Message text for notes
            if (consultProvider.getNote() != null) {
                var txt = consultProvider.getNote()
                txt = txt!!.substring(txt.indexOf(":") + 1)
                /* if (consultProvider.getBdProviderName() != null) {
                    txt = "<font color=black><b>" + consultProvider.getBdProviderName().trim() + "</b></font>".trim() + ": " + txt;
                }*/
//                itemBinding.messageTextView.setText(Html.fromHtml(txt));
                itemBinding.messageTextView.text = txt.trim { it <= ' ' }
                itemBinding.messageTextView.visibility = TextView.VISIBLE
            } else {
                itemBinding.messageTextView.text = ""
                itemBinding.messageTextView.visibility = TextView.VISIBLE
            }
            if (consultProvider.getDob() != null) {
                val calendar = Calendar.getInstance()
                val year = calendar[Calendar.YEAR]
                calendar.timeInMillis = consultProvider.getDob()!!
                val age = year - calendar[Calendar.YEAR]
                //                nameStr += " · " + age;
            }
            //            if ("Female".equalsIgnoreCase(consultProvider.getGender())) {
//                nameStr += " F";
//            } else {
//                nameStr += " M";
//            }
            if (nameStr.trim { it <= ' ' }.length > 15) {
                nameStr = nameStr.substring(0, 15) + "..."
            }
            itemBinding.nameTextView.text = nameStr

//            if (consultProvider.getTime() != null && consultProvider.getTime() > 0) {
//                itemBinding.timeTextView.setText(ChatUtils.getTimeAgo(consultProvider.getTime()));
//            } else if (consultProvider.getJoiningTime() != null && consultProvider.getJoiningTime() > 0) {
//                itemBinding.timeTextView.setText(ChatUtils.getTimeAgo(consultProvider.getJoiningTime()));
//            } else {
//                itemBinding.timeTextView.setText("");
//            }

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
            }*/

//            System.out.println("patient status details " + consultProvider.getStatus());
            // Invite button text
            itemBinding.inviteBtn.text = itemBinding.root.context.getString(R.string.accept)
            if (consultProvider.getStatus() === Constants.PatientStatus.Pending) {
                //Todo: handle invited member OR new patient added case
                //itemBinding.consultTextView.setText(itemBinding.getRoot().getContext().getString(R.string.pending));
                //Show watch timer logo
                itemBinding.messageView.visibility = TextView.GONE
                itemBinding.inviteBtn.visibility = TextView.GONE
                itemBinding.inviteTime.visibility = View.GONE
                itemBinding.watchTimerView.visibility = TextView.VISIBLE
            } else if (consultProvider.getStatus() === Constants.PatientStatus.Invited) {
                itemBinding.messageView.visibility = TextView.GONE
                itemBinding.inviteBtn.isEnabled = true
                itemBinding.inviteBtn.visibility = TextView.VISIBLE
                itemBinding.inviteTime.visibility = View.VISIBLE

//                itemBinding.inviteTimeText.setText("Request sent: " + Utils.getTimeAgo(time));
                itemBinding.inviteTimeText.text =
                    context1!!.resources.getString(R.string.patient_request_sent) + " " + Utils().getTimeAgo(
                        time
                    )

                //itemBinding.consultTextView.setText("");
                itemBinding.watchTimerView.visibility = TextView.GONE
            } else {
                //Show badge count
                if (consultProvider.getUnread() > 0) {
                    itemBinding.messageView.visibility = TextView.VISIBLE
                } else {
                    itemBinding.messageView.visibility = TextView.GONE
                }
                itemBinding.watchTimerView.visibility = TextView.GONE
                // Handoff status handling
                if (consultProvider.getStatus() === Constants.PatientStatus.Handoff) {
                    itemBinding.inviteBtn.isEnabled = true
                    itemBinding.inviteBtn.visibility = View.VISIBLE
                    itemBinding.inviteBtn.text =
                        itemBinding.root.context.getString(R.string.handoff_accept)
                    itemBinding.inviteTime.visibility = View.VISIBLE
                    //                    itemBinding.inviteTimeText.setText("Request sent: " + Utils.getTimeAgo(time));
                    itemBinding.inviteTimeText.text = context1!!.resources.getString(R.string.patient_request_sent)+ " " + Utils().getTimeAgo(time)
                } else {
                    itemBinding.inviteBtn.visibility = View.GONE
                    itemBinding.inviteTime.visibility = View.GONE
                    //                    itemBinding.inviteTimeText.setText("Request sent: " + Utils.getTimeAgo(time));
                    itemBinding.inviteTimeText.text =
                        context1!!.resources.getString(R.string.patient_request_sent) +
                                " " + Utils().getTimeAgo(time)
                }
            }


//            boolean calAcuityLevel = consultProvider.getResetAcuityFlag() != null;

//            System.out.println("acuity level " + consultProvider.getScore() + " " + consultProvider.getName());
            // Acuity level handling
            val acuityLevel: Constants.AcuityLevel? = consultProvider.getScore()
            if (acuityLevel != null) {
                if (acuityLevel === Constants.AcuityLevel.Low) {
                    itemBinding.acuityTextview.background =
                        itemBinding.acuityTextview.resources.getDrawable(R.drawable.acuity_level_low)
                    itemBinding.acuityTextview.setTextColor(
                        itemBinding.acuityTextview.resources.getColor(
                            R.color.acuity_low_border
                        )
                    )
                    //                    itemBinding.acuityTextview.setText(Constants.AcuityLevel.Low.toString());
                    itemBinding.acuityTextview.text =
                        context1!!.resources.getString(R.string.patient_low)
                } else if (acuityLevel === Constants.AcuityLevel.Medium) {
                    itemBinding.acuityTextview.background =
                        itemBinding.acuityTextview.resources.getDrawable(R.drawable.acuity_level_med)
                    itemBinding.acuityTextview.setTextColor(
                        itemBinding.acuityTextview.resources.getColor(
                            R.color.acuity_med_border
                        )
                    )
                    //                    itemBinding.acuityTextview.setText(Constants.AcuityLevel.Medium.toString().substring(0, 3));
                    itemBinding.acuityTextview.text =
                        context1!!.resources.getString(R.string.patient_med)
                } else if (acuityLevel === Constants.AcuityLevel.High) {
                    itemBinding.acuityTextview.background =
                        itemBinding.acuityTextview.resources.getDrawable(R.drawable.acuity_level_high)
                    itemBinding.acuityTextview.setTextColor(
                        itemBinding.acuityTextview.resources.getColor(
                            R.color.acuity_high_border
                        )
                    )
                    //                    itemBinding.acuityTextview.setText(Constants.AcuityLevel.High.toString());
                    itemBinding.acuityTextview.text =
                        context1!!.resources.getString(R.string.patient_high)
                }
            } else {
                itemBinding.acuityTextview.background =
                    itemBinding.acuityTextview.resources.getDrawable(R.drawable.acuity_level_low)
                itemBinding.acuityTextview.setTextColor(
                    itemBinding.acuityTextview.resources.getColor(
                        R.color.acuity_low_border
                    )
                )
                //                itemBinding.acuityTextview.setText(Constants.AcuityLevel.NA.toString());
                itemBinding.acuityTextview.text =
                    context1!!.resources.getString(R.string.patient_na)
            }
            // Urgent icon visibility handling
            if (consultProvider.getUrgent() != null && consultProvider.getUrgent()!!) {
                itemBinding.urgentIcon.visibility = View.VISIBLE
            } else {
                itemBinding.urgentIcon.visibility = View.GONE
            }
        }
    }

}

