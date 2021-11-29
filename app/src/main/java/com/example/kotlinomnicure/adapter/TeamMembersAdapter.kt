package com.example.dailytasksamplepoc.kotlinomnicure.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.kotlinomnicure.activity.MyVirtualTeamsActivity
import com.example.dailytasksamplepoc.kotlinomnicure.interfaces.OnAppointmentItemClick
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility

import org.json.JSONArray
import org.json.JSONException

class TeamMembersAdapter(private val context: Context, private val membersList: JSONArray) :
    RecyclerView.Adapter<TeamMembersAdapter.ViewHolder?>() {
    private val uid: Long
    private val appointmentItemListener: OnAppointmentItemClick? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = inflater!!.inflate(
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
        try {
            val obj = membersList.getJSONObject(position)
            if (obj.has("providerName")) {
                holder.memberName.setText(obj.getString("providerName"))
            }
            if (obj.has("rpType")) {
                holder.designation.setText(obj.getString("rpType"))
            }
            if (obj.has("status")) {
                if (obj.getString("status") == Constants.ProviderStatus.Active.toString() || obj.getString(
                        "status"
                    ) == Constants.ProviderStatus.AutoLock.toString()
                ) {
                    holder.status_icon.setImageResource(R.drawable.ic_green_circle)
                } else {
                    holder.status_icon.setImageResource(R.drawable.ic_grey_circle)
                }
            }

//            if (uid == Long.parseLong(obj.getString("providerId"))) {
            if (uid.toDouble() == obj.getString("providerId").toDouble()) {
                holder.call_icon.visibility = View.INVISIBLE
            } else {
                holder.call_icon.visibility = View.VISIBLE
            }
            holder.call_icon.setOnClickListener {
                try {
                    (context as MyVirtualTeamsActivity).videoCall(membersList.getJSONObject(position))
                } catch (e: JSONException) {
                    Log.e(TAG, "Exception:", e.cause)
                }
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Exception:", e.cause)
        }
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
        private const val TAG = "TeamMemAdap"
        var inflater: LayoutInflater? = null
    }

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        setHasStableIds(true)
        uid = PrefUtility().getLongInPref(context, Constants.SharedPrefConstants.USER_ID, 0)
    }

    override fun getItemCount(): Int {
        return membersList.length()
    }
}