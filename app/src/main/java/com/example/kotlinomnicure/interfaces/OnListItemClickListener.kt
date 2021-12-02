package com.example.kotlinomnicure.interfaces

import com.example.kotlinomnicure.adapter.PatientListAdapter
import com.example.kotlinomnicure.model.ConsultProvider


interface OnListItemClickListener {
    fun onClickChatView(position: Int, provider: ConsultProvider?)
    fun onClickInviteBtn(viewHolder: PatientListAdapter.ConsultListViewHolder?)
    fun onClickDetailsButton(viewHolder: PatientListAdapter.ConsultListViewHolder?)
    fun onClickReconsultButton(viewHolder: PatientListAdapter.ConsultListViewHolder?)
    fun onArrowDropDownClick(position: Int)
    fun onResetAcuityClick(position: Int)
}