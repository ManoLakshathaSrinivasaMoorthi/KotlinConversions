package com.example.kotlinomnicure.videocall.openvcall.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.videocall.propeller.Constant
import org.slf4j.LoggerFactory

class CallOptionsActivity: AppCompatActivity() {
    private val log = LoggerFactory.getLogger(CallOptionsActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_options)
        val ab = supportActionBar
        if (ab != null) {
            ab.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            ab.setCustomView(R.layout.ard_agora_actionbar_with_back_btn)
        }
        val debugSwitch = findViewById<Switch>(R.id.debug_options)
        //        debugSwitch.setChecked(Constant.DEBUG_INFO_ENABLED);
        debugSwitch.isChecked = Constant().isDebugInfoEnabled()
        debugSwitch.setOnCheckedChangeListener { _, withDebugInfo -> //                Constant.DEBUG_INFO_ENABLED = withDebugInfo;
            Constant().setDebugInfoEnabled(withDebugInfo)
        }
        (findViewById<View>(R.id.ovc_page_title) as TextView).setText(R.string.label_options)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    fun onBackPressed(view: View?) {
        onBackPressed()
    }
}
