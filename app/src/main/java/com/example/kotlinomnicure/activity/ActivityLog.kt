package com.example.kotlinomnicure.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.ENotesViewModel
import com.example.kotlinomnicure.adapter.LogDateAdapter


import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityLogBinding
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.UtilityMethods

import java.util.ArrayList

class ActivityLog : BaseActivity() {
    private var binding: ActivityLogBinding? = null
    private val TAG = "ActivityENotes"
    private var patientId: Long = 0
    private var patient_name: String? = null
    private var viewModel: ENotesViewModel? = null
    private var adapter: LogDateAdapter? = null
    private val eNotesList: MutableList<ENotesList?> = ArrayList<ENotesList?>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log)
        viewModel = ViewModelProvider(this).get(ENotesViewModel::class.java)
        patientId = intent.getLongExtra("patient_id", 0)
        patient_name = intent.getStringExtra("patient_name")


        fetchLog()
        setOnclick()
    }

    private fun setOnclick() {
        binding!!.llToolBar.setOnClickListener(View.OnClickListener { finish() })
    }

    fun fetchLog() {
        if (!UtilityMethods().isInternetConnected(this)!!) {
            CustomSnackBar.make(
                binding!!.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        showProgressBar()
        viewModel?.getLogs(patientId)?.observe(this) { listResponse ->

            dismissProgressBar()


            val erroMsg = ""
            if (listResponse != null && listResponse.status != null && listResponse.status!!) {
                listResponse.geteNotesActivity()?.let { eNotesList.addAll(it) }
                setAdapter()
            }
            handleVisibility()
        }
    }

    private fun handleVisibility() {
        if (eNotesList.size <= 0) {
            binding!!.noPatientLayout.setVisibility(View.VISIBLE)
            binding!!.dateRecyclerView.setVisibility(View.GONE)
        } else {
            binding!!.noPatientLayout.setVisibility(View.GONE)
            binding!!.dateRecyclerView.setVisibility(View.VISIBLE)
        }
    }

    private fun setAdapter() {
        adapter = LogDateAdapter(applicationContext, eNotesList)
        binding!!.dateRecyclerView.setLayoutManager(LinearLayoutManager(applicationContext))
        binding!!.dateRecyclerView.setAdapter(adapter)
    }
}