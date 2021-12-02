package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class MessageListDecoration(outRect: Rect, view: View?, parent: RecyclerView, state: RecyclerView.State?) : ItemDecoration() {

    private val divider = 12
    private val header = 4
    private val footer = 4

    init {
        val itemCount = parent.adapter!!.itemCount
        val viewPosition = parent.getChildAdapterPosition(view!!)
        outRect.left = divider
        outRect.right = divider
        if (viewPosition == 0) {
            outRect.top = header
            outRect.bottom = divider / 2
        } else if (viewPosition == itemCount - 1) {
            outRect.top = divider / 2
            outRect.bottom = footer
        } else {
            outRect.top = divider / 2
            outRect.bottom = divider / 2
        }
    }
}
