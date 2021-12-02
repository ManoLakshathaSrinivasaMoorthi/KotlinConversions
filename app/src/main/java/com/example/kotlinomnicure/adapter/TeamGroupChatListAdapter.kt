package com.example.kotlinomnicure.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import org.json.JSONArray
import org.json.JSONException

class TeamGroupChatListAdapter: RecyclerView.Adapter<TeamGroupChatListAdapter.ViewHolder>() {
    private val TAG = "TeamGroupChat"
    private var inflater: LayoutInflater? = null
    private var context: Context? = null
    private var teamList: JSONArray? = null
    private val teamMembersListAdapter: TeamMembersListAdapter? = null

    fun TeamGroupChatListAdapter(context: Context, arr: JSONArray) {
        this.context = context
        teamList = arr
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater!!.inflate(R.layout.item_teams, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val obj = teamList!!.getJSONObject(position)
            holder.teamName.text = obj.getString("name")
            holder.arrow.setOnClickListener {
                if (holder.expandLayout.visibility == View.VISIBLE) {
                    holder.expandLayout.visibility = View.GONE
                    holder.arrow.setImageResource(R.drawable.ic_collapse)
                } else {
                    holder.expandLayout.visibility = View.VISIBLE
                    holder.arrow.setImageResource(R.drawable.ic_expand)
                }
            }

        } catch (e: JSONException) {

        }
    }

    override fun getItemCount(): Int {
        return teamList!!.length()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var teamName: TextView
        var arrow: ImageView
        var membersRecycler: RecyclerView
        var expandLayout: ConstraintLayout

        init {
            teamName = itemView.findViewById(R.id.team_name)
            arrow = itemView.findViewById(R.id.id_arrow_icon)
            membersRecycler = itemView.findViewById(R.id.membersRecyclerView)
            expandLayout = itemView.findViewById(R.id.expand_layout)
            val mLinearLayoutManager = LinearLayoutManager(itemView.context)
            membersRecycler.layoutManager = mLinearLayoutManager
        }
    }
}