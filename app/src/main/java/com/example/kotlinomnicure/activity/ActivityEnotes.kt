package com.example.kotlinomnicure.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityEnotesBinding
import com.example.kotlinomnicure.fragment.HandoffFragment
import com.example.kotlinomnicure.fragment.ProgressFragment
import com.example.kotlinomnicure.model.HandOffList
import java.util.ArrayList


class ActivityEnotes : AppCompatActivity() {

    private var binding: ActivityEnotesBinding? = null
    val TAG = "ActivityENotes"
    var patient_id: Long = 0
    private var patient_name: String? = null
    var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enotes)
        patient_id = intent.getLongExtra("patient_id", 0)
        patient_name = intent.getStringExtra("patient_name")
        initView()
        initOnClickListener()
    }

    private fun initView() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = ""
        val adapter = ViewPagerAdapter(supportFragmentManager)
        binding?.viewPager?.setAdapter(adapter)
        binding?.tabs?.setupWithViewPager(binding?.viewPager)
        binding?.tabs?.removeAllTabs()
        binding?.tabs?.addTab(binding!!.tabs.newTab().setText(R.string.progress))
        binding?.tabs?.addTab(binding!!.tabs.newTab().setText(R.string.hand_off_summary))
        binding?.idToolbarName!!.setText(patient_name)
    }

    private fun initOnClickListener() {
        binding!!.back.setOnClickListener(View.OnClickListener { finish() })
    }

    fun detailsClick(position: Int, readStatusArr: ArrayList<Boolean>, messages: List<HandOffList>) {
            val intent = Intent(this, ActivityENotesDetails::class.java)
        intent.putExtra("patient_name", patient_name)
        val fragment1 =
            supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager.toString() + ":" + binding?.viewPager?.getCurrentItem())
        if (binding!!.viewPager.getCurrentItem() === 0 && fragment1 != null) {
            intent.putExtra("position", position)
            intent.putExtra("patient_id", patient_id)
            intent.putExtra("frag", "Progress")
        } else {
            intent.putExtra("position", position)
            intent.putExtra("patient_id", patient_id)
            intent.putExtra("frag", "Handoff")
        }
        startActivity(intent)
    }

    class ViewPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            Log.d("ActivityEnotes", "getItem: $position")
            if (position == 0) {

                ActivityEnotes().fragment = ProgressFragment(ActivityEnotes().patient_id)
            } else {
               ActivityEnotes().fragment = HandoffFragment(ActivityEnotes().patient_id)
            }
            return ActivityEnotes().fragment!!
        }

        override fun getCount(): Int {
            return 2
        }
    }
}
