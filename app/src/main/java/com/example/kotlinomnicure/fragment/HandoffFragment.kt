package com.mvp.omnicure.kotlinactivity.fragment

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
import com.mvp.omnicure.adapter.HandoffAdapter
import com.mvp.omnicure.databinding.FragmentHandoffBinding
import com.mvp.omnicure.model.ConsultMessage
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HandoffFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HandoffFragment(patient_id: Any?) : Fragment() {
    private val TAG = "HandoffFragment"
    private var binding: FragmentHandoffBinding? = null

    private val messageList = ArrayList<ConsultMessage>()

    private var messageDB: DatabaseReference? = null
    private var adapter: HandoffAdapter? = null
    private var patient_id: Long = 0
    fun HandoffFragment() {
        // Required empty public constructor
    }
    fun HandoffFragment(patient_id: Long) {
        this.patient_id = patient_id
    }

    private enum class MessageType {
        system, discharged
    }

    var dbListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d(
               TAG,
                "onDataChange Attachment: $dataSnapshot"
            )
            if (dataSnapshot.value != null && dataSnapshot.value is Map<*, *>) {
                val membersMap: Map<*, *>? = dataSnapshot.value as Map<*, *>?
                messageList.clear()
                for (s in membersMap!!.keys) {
                    Log.d(TAG, "key " + s + " : " + membersMap[s])
                    val gson = Gson()
                    val `val` = gson.toJson(membersMap[s])
                    Log.d(TAG, "onDataChange: $`val`")
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
                Log.d(TAG, "onDataChange: list " + messageList.size)
                adapter!!.notifyDataSetChanged()
                handleVisibility()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "onCancelled: $databaseError")
        }
    }

    private fun handleVisibility() {
        if (messageList.size <= 0) {
            binding!!.noPatientLayout.visibility = View.VISIBLE
            binding!!.recyclerViewHandoff.visibility = View.GONE
        } else {
            binding!!.noPatientLayout.visibility = View.GONE
            binding!!.recyclerViewHandoff.visibility = View.VISIBLE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        binding = FragmentHandoffBinding.inflate(inflater, container, false)
        binding!!.noPatientLayout.visibility = View.VISIBLE
        binding!!.recyclerViewHandoff.visibility = View.GONE

        binding!!.swipeLayout.setOnRefreshListener { // fetchHospital();
            binding!!.swipeLayout.isRefreshing = false
        }
        setClickListeners()
        setAdapter()

        val messageChild = "consults/$patient_id/messages"
        messageDB = FirebaseDatabase.getInstance().reference.child(messageChild)
        Log.d(TAG, "onCreateView: " + messageDB.toString())
        messageDB!!.addValueEventListener(dbListener)
        return binding!!.getRoot()
    }
    private fun setClickListeners() {
        binding!!.filterViewhandoff.setOnClickListener { MoreOptionRPDialog(requireContext()) }
    }

    private fun setAdapter() {
        adapter = HandoffAdapter(requireContext(), messageList)
        binding!!.recyclerViewHandoff.layoutManager = LinearLayoutManager(requireContext())
        binding!!.recyclerViewHandoff.adapter = adapter
    }

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

    private fun isIncludeChat(consultMessage: ConsultMessage): Boolean {
        return consultMessage.type.equals(
            MessageType.discharged.toString(),
            ignoreCase = true
        )
    }

    fun getMessageList(): ArrayList<ConsultMessage> {
        return messageList
    }

}