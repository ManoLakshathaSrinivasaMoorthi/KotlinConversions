package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SmallVideoViewDecoration: ItemDecoration {

    private val divider = 12
    private val header = 10
    private val footer = 10

    constructor(outRect: Rect, view: View?, parent: RecyclerView, state: RecyclerView.State?) {
        val itemCount = parent.adapter!!.itemCount
        when (parent.getChildAdapterPosition(view!!)) {
            0 -> {
                outRect.left = header
                outRect.right = divider / 2
            }
            itemCount - 1 -> {
                outRect.left = divider / 2
                outRect.right = footer
            }
            else -> {
                outRect.left = divider / 2
                outRect.right = divider / 2
            }
        }
    }
}
