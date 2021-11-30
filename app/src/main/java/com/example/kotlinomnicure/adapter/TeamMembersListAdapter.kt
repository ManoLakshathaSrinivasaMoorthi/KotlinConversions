package com.example.kotlinomnicure.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R

import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import omnicurekotlin.example.com.providerEndpoints.model.Members


class TeamMembersListAdapter(private val context: Context, arr: List<Members>) :
    RecyclerView.Adapter<TeamMembersListAdapter.ViewHolder?>() {
    private val uid: Long
    private val membersList: List<Members> = arr
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = TeamMembersAdapter.inflater!!.inflate(
            R.layout.item_team_members,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val members: Members = membersList[position]
        if (!TextUtils.isEmpty(members.getProviderName())) {
            holder.memberName.setText(members.getProviderName())
        }
        if (uid == members.getProviderId()?.toLong() ) {
            holder.call_icon.visibility = View.INVISIBLE
        } else {
            holder.call_icon.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(members.getRpType())) {
            holder.designation.setText(members.getRpType())
        }
        if (!TextUtils.isEmpty(members.getStatus())) {
            if (members.getStatus()
                    .equals(Constants.ProviderStatus.Active.toString(),ignoreCase = true) || members.getStatus()
                    .equals(Constants.ProviderStatus.AutoLock.toString(),ignoreCase = true)
            ) {
                holder.status_icon.setImageResource(R.drawable.ic_green_circle)
            } else {
                holder.status_icon.setImageResource(R.drawable.ic_grey_circle)
            }
        }
        holder.call_icon.setOnClickListener {
           /* (context as TeamGroupChatActivity).videoCall(
                members
            )*/
        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var memberName: TextView
        var designation: TextView
        var status_icon: ImageView
        var call_icon: ImageView

        init {
            memberName = itemView.findViewById<TextView>(R.id.member_name)
            designation = itemView.findViewById<TextView>(R.id.designation)
            status_icon = itemView.findViewById(R.id.id_status_icon)
            call_icon = itemView.findViewById(R.id.call_icon)
        }
    }

    companion object {
        private var inflater: LayoutInflater? = null
    }

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        setHasStableIds(true)
        uid = PrefUtility().getLongInPref(context, Constants.SharedPrefConstants.USER_ID, 0)
    }
}