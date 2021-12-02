package com.example.kotlinomnicure.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.*
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.activity.ChatActivity

import java.lang.Exception

class ChatAttachmentPopup(context: Context?) : PopupWindow(context) {

    private val TAG = ChatAttachmentPopup::class.java.simpleName
    private var activity: Activity? = null
    private var rootView: View? = null
    private var isKeyboardShowing = false
    private var height_ = 0
    private  var width_:Int = 0
    private var popupWidth = 0
    private  var popupHeight:Int = 0
    private var xOffset = 0
    private  var yOffset:Int = 0
    private var documentClickListener: View.OnClickListener? = null
    private  var cameraClickListener:View.OnClickListener? = null
    private  var galleryClickListener:View.OnClickListener? = null
    private  var videoClickListener:View.OnClickListener? = null
    private var statusBarHeight = 0
    private var keypadHeight = 0
    private var isDismissed = false

    fun ChatAttachmentPopup(activity: Activity?) {
        this.activity = activity
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun initConfig() {
        val layout = activity!!.layoutInflater.inflate(R.layout.chat_bottom_dialog, null, false) as LinearLayout
        isFocusable = false
        setBackgroundDrawable(BitmapDrawable())
        isTouchable = true
        isOutsideTouchable = true
        val screenWidth = (activity?.resources?.displayMetrics?.widthPixels!! * 0.95).toInt()
        if (screenWidth != 0) {
            width = screenWidth
        }
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        contentView = layout
        statusBarHeight = getStatusBarHeight()
        rootView = layout.rootView
        width = activity!!.window.decorView.width - 60
        height = activity!!.window.decorView.height - statusBarHeight
        xOffset = 0
        if (ChatActivity().getChatEditTextViewHeight() !== 0) {
            yOffset = ChatActivity().getChatEditTextViewHeight()
        } else {
            yOffset = UtilityMethods().dpToPx(getStatusBarHeight())
        }
        if (ChatActivity().isIsKeyboardShowing) {
            (layout.findViewById<View>(R.id.root_view) as LinearLayout).background =
                activity!!.resources.getDrawable(R.drawable.rect_drawable_white_bg)
        } else {
            (layout.findViewById<View>(R.id.root_view) as LinearLayout).background =
                activity!!.resources.getDrawable(R.drawable.rect_circular_drawable_white)
        }
        isOutsideTouchable = true
        contentView.addOnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 -> //                Log.i(TAG, "onLayoutChange: " + isKeyboardShowing);
            revealShow(true)
        }
        contentView.viewTreeObserver.addOnGlobalLayoutListener {
            popupWidth = contentView.width
            popupHeight = contentView.height
            val r = Rect()
            contentView.rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = activity!!.window.decorView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            keypadHeight = screenHeight - r.bottom


            if (keypadHeight > screenHeight * 0.15) {
                // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                }
            } else {
                // keyboard is closed
                if (isKeyboardShowing) {
                    isKeyboardShowing = false
                }
            }
        }
        val docLayout = contentView.findViewById<LinearLayout>(R.id.id_document_layout)
        val cameraLayout = contentView.findViewById<LinearLayout>(R.id.id_camera_layout)
        val GalleryLayout = contentView.findViewById<LinearLayout>(R.id.id_gallery_layout)
        val videoLayout = contentView.findViewById<LinearLayout>(R.id.id_video_layout)
        val fileTxtView = contentView.findViewById<TextView>(R.id.id_file)
        val cameraTxtView = contentView.findViewById<TextView>(R.id.id_camera)
        val galleryTxtView = contentView.findViewById<TextView>(R.id.id_gallery)
        val videoTxtView = contentView.findViewById<TextView>(R.id.id_video)
        fileTxtView.setOnClickListener(documentClickListener)
        cameraTxtView.setOnClickListener(cameraClickListener)
        galleryTxtView.setOnClickListener(galleryClickListener)
        videoTxtView.setOnClickListener(videoClickListener)
        docLayout.setOnClickListener(documentClickListener)
        cameraLayout.setOnClickListener(cameraClickListener)
        GalleryLayout.setOnClickListener(galleryClickListener)
        videoLayout.setOnClickListener(videoClickListener)
        setTouchInterceptor(OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                onDismiss()
                return@OnTouchListener true
            }
            onDismiss()
            false
        })
    }

    fun setDocumentClickListener(documentClickListener: View.OnClickListener?) {
        this.documentClickListener = documentClickListener
    }

    fun setCameraClicListener(cameraClickListener: View.OnClickListener) {
        cameraClickListener = cameraClickListener
    }

    fun setGalleryClicListener(galleryClickListener: View.OnClickListener) {
        galleryClickListener = galleryClickListener
    }

    fun setVideoClickListener(videoClickListener: View.OnClickListener) {
        videoClickListener = videoClickListener
    }

    override fun dismiss() {
        super.dismiss()

    }

    private fun revealShow(isShow: Boolean) {

        try {
            val maxRadius = Math.hypot(width.toDouble(), height.toDouble()).toFloat()
            if (isShow) {
                var revealAnimator: Animator? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (ChatActivity().isIsKeyboardShowing) {
                        revealAnimator = ViewAnimationUtils.createCircularReveal(
                            contentView,
                            width - 60,
                            keypadHeight,
                            0f,
                            maxRadius
                        )
                        revealAnimator.duration = 500
                        revealAnimator.start()
                    } else {
                        revealAnimator = ViewAnimationUtils.createCircularReveal(
                            contentView,
                            popupWidth - 60,
                            popupHeight,
                            0f,
                            maxRadius
                        )
                        revealAnimator.duration = 500
                        revealAnimator.start()
                    }
                }
            } else {
                if (!isDismissed && isShowing) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (ChatActivity().isIsKeyboardShowing) {
                            val anim = ViewAnimationUtils.createCircularReveal(
                                contentView, width - 60, keypadHeight, maxRadius, 60f
                            )
                            anim.addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    super.onAnimationEnd(animation)
                                    isDismissed = true
                                    dismiss()
                                }
                            })
                            anim.duration = 500
                            anim.start()
                        } else {
                            val anim = ViewAnimationUtils.createCircularReveal(
                                contentView, popupWidth, popupHeight, maxRadius, 0f
                            )
                            anim.addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    super.onAnimationEnd(animation)
                                    isDismissed = true
                                    dismiss()
                                }
                            })
                            anim.duration = 500
                            anim.start()
                        }
                    } else {
                        dismiss()
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    fun show() {
        val isSoftNavBar: Boolean = UtilityMethods().hasSoftNavBar()

        var navBarHeight = 0
        if (isSoftNavBar) {
            navBarHeight = UtilityMethods().getSoftNavBarHeight()
        }
        val margin = 10
        if (ChatActivity().isIsKeyboardShowing) {
            var height = ChatActivity().getKeypadHeight() - margin
            if (isSoftNavBar) {
                height = height - navBarHeight
            }
            setHeight(height)
            width = ViewGroup.LayoutParams.MATCH_PARENT
            showAtLocation(rootView, Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        } else {
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            if (isSoftNavBar) {
                showAtLocation(
                    rootView,
                    Gravity.BOTTOM or Gravity.CENTER,
                    0,
                    yOffset + margin + navBarHeight
                )
            } else {
                showAtLocation(rootView, Gravity.BOTTOM or Gravity.CENTER, 0, yOffset + margin)
            }
        }
    }

    fun onDismiss() {

        revealShow(false)
    }

    private fun getStatusBarHeight(): Int {
        var height = 0
        val resourceId = activity!!.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = activity!!.resources.getDimensionPixelSize(resourceId)
        }
        return height
    }
}