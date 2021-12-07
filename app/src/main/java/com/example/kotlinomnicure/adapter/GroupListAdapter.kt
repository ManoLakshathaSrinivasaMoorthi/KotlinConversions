package com.example.kotlinomnicure.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.GroupCallActivity
import java.util.ArrayList

class GroupListAdapter(
    groupCallActivity: GroupCallActivity,
    providerList: ArrayList<Provider>,
    selectedProviderIds: ArrayList<Int>) : RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {
    private var inflater: LayoutInflater? = null
    var context: Context? = null
    var selectedProviderIds = ArrayList<Int>()
    var providerList: ArrayList<Provider> = ArrayList<Provider>()
    var listen: GroupListListener? = null

    fun GroupListAdapter(context: Context, arr: ArrayList<Provider>, selected: ArrayList<Int>) {
        this.context = context
        this.providerList = arr
        this.selectedProviderIds = selected
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View? = inflater?.inflate(R.layout.item_group_members, parent, false)
        return view?.let { ViewHolder(it) }!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name: String? = providerList[position].getName()
        val id: Long? = providerList[position].getId()
        holder.memberName.text = name
        if (selectedProviderIds.contains(position)) {
            holder.tick.visibility = View.VISIBLE
        } else {
            holder.tick.visibility = View.INVISIBLE
        }
        holder.container.setOnClickListener {
            listen!!.onItemClicked(position,
                !selectedProviderIds.contains(position))
        }
    }

    override fun getItemCount(): Int {
        return providerList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setListener(l: GroupListListener?) {
        listen = l
    }

    interface GroupListListener {
        fun onItemClicked(id: Int?, isChecked: Boolean?)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var memberName: TextView
        var tick: ImageView
        var container: ConstraintLayout

        init {
            memberName = itemView.findViewById(R.id.name)
            tick = itemView.findViewById(R.id.id_tick_icon)
            container = itemView.findViewById(R.id.container_layout)
        }
    }
}