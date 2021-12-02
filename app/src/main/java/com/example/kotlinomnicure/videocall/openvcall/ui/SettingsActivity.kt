package com.example.kotlinomnicure.videocall.openvcall.ui

import android.R
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.videocall.openvcall.ui.layout.SettingsButtonDecoration
import com.example.kotlinomnicure.videocall.openvcall.ui.layout.VideoEncResolutionAdapter

class SettingsActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_item)
        val ab = supportActionBar
        if (ab != null) {
            ab.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            ab.setCustomView(R.layout.ard_agora_actionbar_with_back_btn)
        }
        setupUI()
    }

    private fun setupUI() {
        (findViewById<View>(R.id.ovc_page_title) as TextView).setText(R.string.label_settings)
        val videoResolutionList = findViewById<View>(R.id.settings_video_resolution) as RecyclerView
        videoResolutionList.setHasFixedSize(true)
        videoResolutionList.addItemDecoration(SettingsButtonDecoration())
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val resolutionIdx = pref.getInt(
            ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION,
            ConstantApp().DEFAULT_VIDEO_ENC_RESOLUTION_IDX
        )
        val fpsIdx = pref.getInt(
            ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS,
            ConstantApp().DEFAULT_VIDEO_ENC_FPS_IDX
        )
        val videoResolutionAdapter = VideoEncResolutionAdapter(this, resolutionIdx)
        videoResolutionAdapter.setHasStableIds(true)
        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)
        videoResolutionList.layoutManager = layoutManager
        videoResolutionList.adapter = videoResolutionAdapter
        val videoFpsSpinner = findViewById<View>(R.id.settings_video_frame_rate) as Spinner
        val videoFpsAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.string_array_frame_rate, R.layout.simple_spinner_item_light
        )
        videoFpsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        videoFpsSpinner.adapter = videoFpsAdapter
        videoFpsSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val pref = PreferenceManager.getDefaultSharedPreferences(
                    applicationContext
                )
                val editor = pref.edit()
                editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, position)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        videoFpsSpinner.setSelection(fpsIdx)
    }

    fun onBackPressed(view: View?) {
        onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

}
