package com.mvp.omnicure.kotlinactivity.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mvp.omnicure.adapter.ProgressAdapter
import com.mvp.omnicure.databinding.FragmentProgressBinding
import com.mvp.omnicure.model.ConsultMessage
import java.util.ArrayList

class ProgressFragment(patient_id: Any?) : Fragment() {
    private val TAG = "ProgressFragment"
    private var binding: FragmentProgressBinding? = null

    private val messageList = ArrayList<ConsultMessage>()

    private var messageDB: DatabaseReference? = null
    private var adapter: ProgressAdapter? = null
    private var patient_id: Long = 0
    private val filterByDialog: Dialog? = null

    fun ProgressFragment() {
        // Required empty public constructor
    }


    fun ProgressFragment(patient_id: Long) {
        this.patient_id = patient_id
    }

    enum class MessageType {
        system, discharged
    }

    var dbListener: ValueEventListener = object : ValueEventListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d(TAG, "onDataChange Attachment: $dataSnapshot")
            if (dataSnapshot.value != null && dataSnapshot.value is Map<*, *>) {
                val membersMap: Map<*, *>? = dataSnapshot.value as Map<*, *>?
                messageList.clear()
                for (s in membersMap!!.keys) {
                    Log.d(TAG,
                        "key " + s + " : " + membersMap[s]
                    )
                    val gson = Gson()
                    val `val` = gson.toJson(membersMap[s])
                    Log.d(TAG,
                        "onDataChange: $`val`"
                    )
                    val consultMessage = gson.fromJson(
                        `val`,
                        ConsultMessage::class.java
                    )
                    if (consultMessage != null) {
                        consultMessage.id = dataSnapshot.key
                        if (isIncludeChat(consultMessage)) {
                            messageList.add(consultMessage)
                        }
                    }
                }
                Log.d(TAG,
                    "onDataChange: list " + messageList.size
                )
                adapter!!.notifyDataSetChanged()
                handleVisibility()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "onCancelled: $databaseError"
            )
        }
    }

  //handle the visibility of the layout
    private fun handleVisibility() {
        if (messageList.size <= 0) {
            binding!!.noPatientLayout.visibility = View.VISIBLE
            binding!!.recyclerViewProgress.visibility = View.GONE
        } else {
            binding!!.noPatientLayout.visibility = View.GONE
            binding!!.recyclerViewProgress.visibility = View.VISIBLE
        }
    }


    //on view created
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        binding = FragmentProgressBinding.inflate(inflater, container, false)
        binding!!.noPatientLayout.visibility = View.VISIBLE
        binding!!.recyclerViewProgress.visibility = View.GONE

        binding!!.swipeLayout.setOnRefreshListener { // fetchHospital();
            binding!!.swipeLayout.isRefreshing = false
        }
        setClickListeners()
        setAdapter()

        val messageChild = "consults/patient_id/messages"
        messageDB = FirebaseDatabase.getInstance().reference.child(messageChild)
        Log.d(TAG, "onCreateView: " + messageDB.toString())
        messageDB!!.addValueEventListener(dbListener)

        return binding!!.getRoot()
    }

    //set on click listeners
    private fun setClickListeners() {
        binding!!.filterViewProgress.setOnClickListener { MoreOptionRPDialog(requireContext()) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    //set the adapter
    private fun setAdapter() {
        adapter = ProgressAdapter(requireContext(), messageList)
        binding!!.recyclerViewProgress.layoutManager = LinearLayoutManager(requireContext())
        binding!!.recyclerViewProgress.adapter = adapter
    }


    private fun isIncludeChat(consultMessage: ConsultMessage): Boolean {
        return consultMessage.type.equals(
            ProgressFragment.MessageType.system.toString(),
            ignoreCase = true
        )
    }

    //set the dialog
    fun MoreOptionRPDialog(context: Context?) {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.setContentView(R.layout.rp_more_option_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val handOffENote = dialog.findViewById<View>(R.id.txtHandOffPatient) as TextView
        val summaryENote = dialog.findViewById<View>(R.id.txtCompletedConsultation) as TextView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        dialog.findViewById<View>(R.id.rleNotes).visibility = View.GONE
        handOffENote.text = "Handoff eNote"
        summaryENote.text = "Summary eNote"
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


}