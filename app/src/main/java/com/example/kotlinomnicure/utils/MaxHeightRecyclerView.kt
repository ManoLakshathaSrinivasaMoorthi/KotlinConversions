package com.example.kotlinomnicure.utils

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R

class MaxHeightRecyclerView :RecyclerView{
    private var mMaxHeight = 250

   constructor(context: Context?) :super(context!!)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {

        initialize(context, attrs)
    }

constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):super(context, attrs, defStyleAttr) {

        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView)
        val scale: Float = getContext().resources.displayMetrics.density
        val pixels = (mMaxHeight * scale + 0.5f).toInt()
        mMaxHeight = arr.getLayoutDimension(R.styleable.MaxHeightScrollView_maxHeight, pixels)
        arr.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        if (mMaxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}