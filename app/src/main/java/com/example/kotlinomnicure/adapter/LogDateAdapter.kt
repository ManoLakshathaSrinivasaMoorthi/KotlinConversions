package com.example.kotlinomnicure.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.model.ENotesList


class LogDateAdapter : RecyclerView.Adapter<LogDateAdapter.ViewHolder> {

    private var eNoteslist: List<ENotesList>? = null
    private var context: Context? = null
    val recycledViewPool = RecycledViewPool()
    private var logsRecycler: RecyclerView? = null
    private var iterator: Iterator<ENotesList>? = null
    constructor()

    constructor(context: Context?, list: MutableList<ENotesList?>){
        eNoteslist = list
        iterator = list.iterator()
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activites_log_date_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        linearLayoutManager.initialPrefetchItemCount = 30
        logsRecycler!!.layoutManager = linearLayoutManager
        logsRecycler!!.setRecycledViewPool(recycledViewPool)
        holder.date.text = eNoteslist!![position].getDate()
        if (iterator!!.hasNext()) {
            val date = eNoteslist?.iterator()?.next()?.getDate()
            logsRecycler!!.adapter =
                InnerLogsAdapter(eNoteslist!![position].getMessages(), context, this)
            //            holder.date.setText(date);
        }
    }

    override fun getItemCount(): Int {
//        System.out.println("getting count "+eNoteslist.size());
        return eNoteslist!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var date: TextView = itemView.findViewById(R.id.datesTxt)

        init {
            LogDateAdapter().logsRecycler = itemView.findViewById(R.id.logsRecycler)
        }
    }

    fun getLogsRecycler(): RecyclerView? {
        return logsRecycler
    }

    fun setLogsRecycler(logsRecycler: RecyclerView?) {
        this.logsRecycler = logsRecycler
    }
}
