package com.example.kotlinomnicure.interfaces

interface OnItemClickListener {
    fun onViewClick(position: Int)

    fun onClickStartConsultationSame(position: Int)

    fun onClickStartConsultationOther(position: Int)
}
