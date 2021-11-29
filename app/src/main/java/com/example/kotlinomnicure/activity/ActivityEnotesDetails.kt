package com.mvp.omnicure.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.dailytasksamplepoc.kotlinomnicure.activity.BaseActivity
import com.example.dailytasksamplepoc.kotlinomnicure.activity.EncUtil
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.ENotesViewModel
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.ENotesDetailsAdapter
import com.example.kotlinomnicure.databinding.ActivityEnotesDetailsBinding
import com.example.kotlinomnicure.model.HandOffList
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import omnicurekotlin.example.com.providerEndpoints.model.Patient

import java.util.*

class ActivityENotesDetails : BaseActivity(), ENotesDetailsAdapter.DetailsClick {
    private var binding: ActivityEnotesDetailsBinding? = null
    private var fragName: String? = null
    private var position = 0
    private var messagesList: List<HandOffList?>? = ArrayList<HandOffList?>()
    private var patientDetails: Patient? = null
    private var readStatusArr: ArrayList<Boolean>? = ArrayList()
    private var patientId: Long = 0
    private var height = 0f
    private var distance = 0f
    private var rAnimY: ObjectAnimator? = null
    private var lAnimY: ObjectAnimator? = null
    private var adapter: ENotesDetailsAdapter? = null
    var encKey = ""
    private var viewModel: ENotesViewModel? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enotes_details)
        viewModel = ViewModelProvider(this).get(ENotesViewModel::class.java)
        fragName = getIntent().getStringExtra("frag")
        patientId = getIntent().getLongExtra("patient_id", 0)
        position = getIntent().getIntExtra("position", 0)


        EncUtil().generateKey(this)
        encKey = EncUtil().decrypt(this, PrefUtility().getAESKey(this)).toString()
        val intent: Intent = this.getIntent()
        val bundle = intent.extras
        messagesList = if (fragName == "Progress") {
            bundle!!.getSerializable("progress_array") as List<HandOffList?>?
        } else {
            bundle!!.getSerializable("handoff_array") as List<HandOffList?>?
        }
        Collections.reverse(messagesList)
        position = messagesList!!.size - 1 - position

        patientDetails = bundle.getSerializable("patient_details") as Patient?

        readStatusArr = bundle.getSerializable("readStatusArr") as ArrayList<Boolean>?

        initView()
        setClickListeners()
        setAdapter()
        callReadApi()
    }

    private fun callReadApi() {
        if (!UtilityMethods().isInternetConnected(this)) {
            CustomSnackBar.make(binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return
        }
        if (readStatusArr!![position]) {
            return
        }
        val providerId: Long? = PrefUtility().getProviderId(this)
        showProgressBar()

        if (providerId != null) {
            messagesList!![position]?.messageId?.let {
                viewModel?.setRead(patientId, providerId, it)
                    ?.observe(this) { listResponse ->

                        dismissProgressBar()


                        val erroMsg = ""
                        if (listResponse != null) {
                            if (listResponse.status == true) {
                                readStatusArr!![position] = true
                            }
                        }
                    }
            }
        }
    }

    private fun setAdapter() {
        adapter =
            ENotesDetailsAdapter(getApplicationContext(), messagesList, patientDetails, fragName)
        adapter!!.setListener(this)
        binding?.recyclerView?.setLayoutManager(LinearLayoutManager(getApplicationContext(),
            LinearLayoutManager.HORIZONTAL,
            false))
        binding?.recyclerView?.setAdapter(adapter)
        binding?.recyclerView?.getLayoutManager()?.scrollToPosition(position)
        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(binding?.recyclerView)
    }

    protected override fun onResume() {
        super.onResume()
        distance = height - binding?.leftFab?.getY()!!
    }

    protected override fun onStop() {
        super.onStop()
        //        position = -1;
    }

    private fun initView() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setTitle("")
        height = getResources().getDisplayMetrics().heightPixels.toFloat()
        lAnimY = ObjectAnimator.ofFloat(binding?.leftFab,
            "translationY", height)
            .setDuration(800)
        rAnimY = ObjectAnimator.ofFloat(binding?.rightFab,
            "translationY", height)
            .setDuration(800)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListeners() {


        //to disable user swipe
        binding?.recyclerView?.setOnTouchListener(OnTouchListener { view, motionEvent -> true })
        setFabState()
        binding?.toolbarTitle?.setText(messagesList!![position]?.messageId)
        binding?.idToolbarSubtitle?.setText(messagesList!![position]?.subType
            .toString() + " eNote")
        binding?.leftFab?.setOnClickListener(View.OnClickListener {
            position--
            binding?.recyclerView?.smoothScrollToPosition(position)
            setFabState()
            callReadApi()
        })
        binding?.rightFab?.setOnClickListener(View.OnClickListener {
            position++
            binding?.recyclerView?.smoothScrollToPosition(position)
            setFabState()
            callReadApi()
        })
        binding?.imgBack?.setOnClickListener(View.OnClickListener { this@ActivityENotesDetails.onBackPressed() })
    }

    @SuppressLint("SetTextI18n")
    private fun setFabState() {
        binding?.leftFab?.setEnabled(true)
        binding?.rightFab?.setEnabled(true)
        if (position == 0) {
            binding?.leftFab?.setEnabled(false)
        }
        if (position == messagesList!!.size - 1) {
            binding?.rightFab?.setEnabled(false)
        }
        binding?.toolbarTitle?.setText(messagesList!![position]?.messageId)
        binding?.idToolbarSubtitle?.setText(messagesList!![position]?.subType
            .toString() + " eNote")
    }

    private fun setContents() {
        val id: String = java.lang.String.valueOf(messagesList!![position]?.senderId)
        if (id != "null") binding?.toolbarTitle?.setText(id) else binding?.toolbarTitle?.setText(
            "")

        if (position == 0) {
            binding?.leftFab?.setAlpha(0.5f)
            binding?.leftFab?.setEnabled(false)
            binding?.rightFab?.setAlpha(1f)
            binding?.rightFab?.setEnabled(true)
        } else if (messagesList!!.size - 1 == position) {
            binding?.rightFab?.setAlpha(0.5f)
            binding?.rightFab?.setEnabled(false)
            binding?.leftFab?.setAlpha(1f)
            binding?.leftFab?.setEnabled(true)
        } else {
            binding?.leftFab?.setAlpha(1f)
            binding?.leftFab?.setEnabled(true)
            binding?.rightFab?.setAlpha(1f)
            binding?.rightFab?.setEnabled(true)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("screen", fragName)
        setResult(RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }

    override fun detailsClick() {
        if (binding?.leftFab?.getY()!! < height) {
            lAnimY!!.start()
            rAnimY!!.start()
        } else {
            lAnimY!!.reverse()
            rAnimY!!.reverse()
        }
    }

    companion object {
        private const val TAG = "ActivityENotesDetails"
    }
}