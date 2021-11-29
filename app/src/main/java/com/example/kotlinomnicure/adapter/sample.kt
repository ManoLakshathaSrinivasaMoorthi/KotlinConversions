package com.example.kotlinomnicure.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import omnicurekotlin.example.com.userEndpoints.model.User

class sample(
        private val users: ArrayList<User>
) : RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            itemView?.textViewUserName?.text = user?.name
            itemView.textViewUserEmail.text = user.email
            Glide.with(itemView.imageViewAvatar.context)
                    .load(user.avatar)
                    .into(itemView.imageViewAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            DataViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.item_layout, parent,
                            false
                    )
            )

    override fun getItemCount(): Int = users.size
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
            holder.bind(users[position])

    fun addData(list: List<User>) {
        users.addAll(list)
    }
}