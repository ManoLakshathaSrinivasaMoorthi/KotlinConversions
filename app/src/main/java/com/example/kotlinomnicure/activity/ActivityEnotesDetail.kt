package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityEnotesDetailsBinding
import com.example.dailytasksamplepoc.kotlinomnicure.model.ConsultMessage
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.fragment.ProgressFragment
import java.util.ArrayList

class ActivityEnotesDetail : AppCompatActivity() {
    private val TAG = "ActivityENotesDetails"
    private var binding: ActivityEnotesDetailsBinding? = null
    private var patientName: String? = null
    private var fragName: String? = null
    private var position = 0
    private val messages: ArrayList<ConsultMessage> = ArrayList<ConsultMessage>()
    private var messageDB: DatabaseReference? = null
    private var patient_id: Long = 0
    private var height = 0f
    private var distance = 0f
    private var rAnimY: ObjectAnimator? = null
    private var lAnimY: ObjectAnimator? = null

    var dbListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d(TAG, "onDataChange Attachment: $dataSnapshot")
            if (dataSnapshot.value != null && dataSnapshot.value is Map<*, *>) {
                val membersMap: Map<*, *>? = dataSnapshot.value as Map<*, *>?
                messages.clear()
                for (s in membersMap!!.keys) {
                    Log.d(TAG, "key " + s + " : " + membersMap[s])
                    val gson = Gson()
                    val `val` = gson.toJson(membersMap[s])
                    Log.d(TAG, "onDataChange: $`val`")
                    val consultMessage = gson.fromJson(`val`, ConsultMessage::class.java)
                    if (consultMessage != null) {
                        consultMessage.id = dataSnapshot.key
                        if (isIncludeChat(consultMessage)) {
                            messages.add(consultMessage)
                        }
                    }
                }
                Log.d(TAG, "onDataChange: list " + messages.size)
                if (messages.size < 2) {
                    binding!!.leftFab.visibility = View.GONE
                    binding!!.rightFab.visibility = View.GONE
                }
                setContents()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "onCancelled: $databaseError")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enotes_details)
        patientName = intent.getStringExtra("patient_name")
        fragName = intent.getStringExtra("frag")
        patient_id = intent.getLongExtra("patient_id", 0)
        position = intent.getIntExtra("position", 0)
        Log.d(TAG, "onCreate: $patientName $position $fragName")
        val messageChild = "consults/$patient_id/messages"
        messageDB = FirebaseDatabase.getInstance().reference.child(messageChild)
        Log.d(TAG,
            "onCreateView: " + messageDB.toString()
        )
        messageDB!!.addValueEventListener(dbListener)
        initView()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        distance = height - binding!!.leftFab.y
    }

    override fun onStop() {
        super.onStop()
        messageDB!!.removeEventListener(dbListener)
        position = -1
    }

    private fun initView() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = ""
        height = resources.displayMetrics.heightPixels.toFloat()
        binding!!.txtPatientName.text = patientName
        lAnimY = ObjectAnimator.ofFloat(
            binding!!.leftFab,
            "translationY", height
        )
            .setDuration(800)
        rAnimY = ObjectAnimator.ofFloat(
            binding!!.rightFab,
            "translationY", height
        )
            .setDuration(800)
    }

    private fun setClickListeners() {
        binding!!.llSummarylayout.setOnClickListener {
            if (binding!!.leftFab.y < height) {
                lAnimY!!.start()
                rAnimY!!.start()
            } else {
                lAnimY!!.reverse()
                rAnimY!!.reverse()
            }
        }
        binding!!.leftFab.setOnClickListener {
            position--
            setContents()
        }
        binding!!.rightFab.setOnClickListener {
            position++
            setContents()
        }
        binding!!.imgBack.setOnClickListener { finish() }
    }

    private fun setContents() {
        val id = java.lang.String.valueOf(messages[position].senderId)
        if (id != null && id != "null") binding!!.toolbarTitle.text =
            id else binding!!.toolbarTitle.text =
            ""
        binding!!.txtLocation.text = position.toString()
        binding!!.txtAge.text = messages[position].type
        if (position == 0) {
            binding!!.leftFab.alpha = 0.5f
            binding!!.leftFab.isEnabled = false
            binding!!.rightFab.alpha = 1f
            binding!!.rightFab.isEnabled = true
        } else if (messages.size - 1 == position) {
            binding!!.rightFab.alpha = 0.5f
            binding!!.rightFab.isEnabled = false
            binding!!.leftFab.alpha = 1f
            binding!!.leftFab.isEnabled = true
        } else {
            binding!!.leftFab.alpha = 1f
            binding!!.leftFab.isEnabled = true
            binding!!.rightFab.alpha = 1f
            binding!!.rightFab.isEnabled = true
        }
    }

    private fun isIncludeChat(consultMessage: ConsultMessage): Boolean {
        return if (fragName == "Progress") consultMessage.type.equals(ProgressFragment.MessageType.system.toString(),ignoreCase = true) else consultMessage.type.equals(
            ProgressFragment.MessageType.discharged.toString(),ignoreCase = true)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}