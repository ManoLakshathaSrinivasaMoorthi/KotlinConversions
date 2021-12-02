package com.example.kotlinomnicure.customview

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout

class ChatBottomDialog: Dialog {

   constructor(context: Context?):super(context!!)

    private var documentClickListener: View.OnClickListener? = null
    var cameraClickListener: View.OnClickListener? = null
    var galleryClickListener: View.OnClickListener? = null
    var view: View? = null
    var dialogView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogView = View.inflate(context, R.layout.chat_bottom_dialog, null)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(dialogView)
        window!!.setBackgroundDrawableResource(R.color.transparent)
        val width = (context.resources.displayMetrics.widthPixels * 0.95).toInt()
        val wlp = window!!.attributes
        wlp.width = width
        wlp.gravity = Gravity.BOTTOM
        val docLayout = findViewById<LinearLayout>(R.id.id_document_layout)
        val cameraLayout = findViewById<LinearLayout>(R.id.id_camera_layout)
        val GalleryLayout = findViewById<LinearLayout>(R.id.id_gallery_layout)
        dialogView.setOnClickListener(View.OnClickListener { dismiss() })
        docLayout.setOnClickListener(documentClickListener)
        cameraLayout.setOnClickListener(cameraClickListener)
        GalleryLayout.setOnClickListener(galleryClickListener)
        setOnShowListener(onShowListener)
        setOnDismissListener(onDismissListener)
    }

    fun setDocumentClickListener(documentClickListener: View.OnClickListener?) {
        this.documentClickListener = documentClickListener
    }

    fun setCameraClicListener(cameraClickListener: View.OnClickListener?) {
        this.cameraClickListener = cameraClickListener
    }

    fun setGalleryClicListener(galleryClickListener: View.OnClickListener?) {
        this.galleryClickListener = galleryClickListener
    }


    var onShowListener = OnShowListener {
        val view = dialogView!!.findViewById<View>(R.id.root_view)
        revealShow(view, dialogView, true)
    }

    var onDismissListener = DialogInterface.OnDismissListener {
        val view = dialogView!!.findViewById<View>(R.id.root_view)
        revealShow(view, dialogView, false)
    }

    private fun revealShow(view: View, dialogView: View?, isShow: Boolean) {
        //view = dialogView.findViewById(R.id.root_view);
        val w = view.width
        val h = view.height
        val maxRadius = Math.hypot(w.toDouble(), h.toDouble()).toFloat()
        if (isShow) {
            var revealAnimator: Animator? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                revealAnimator =
                    ViewAnimationUtils.createCircularReveal(view, w - 60, h, 0f, maxRadius)
                view.visibility = View.VISIBLE
                revealAnimator.duration = 350
                revealAnimator.start()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.post {
                    val anim = ViewAnimationUtils.createCircularReveal(view, w, h, maxRadius, 0f)
                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            dismiss()
                        }
                    })
                    anim.duration = 350
                    anim.start()
                }
            } else {
                dismiss()
            }
        }
    }
}
