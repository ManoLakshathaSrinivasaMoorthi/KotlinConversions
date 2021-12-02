package com.example.kotlinomnicure.videocall.openvcall.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.videocall.openvcall.model.BeforeCallEventHandler
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import io.agora.rtc.IRtcEngineEventHandler.LastmileProbeResult
import io.agora.rtc.internal.LastmileProbeConfig
import org.slf4j.LoggerFactory

abstract class NetworkTestActivity: BaseActivity(), BeforeCallEventHandler {
    private val log = LoggerFactory.getLogger(NetworkTestActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_test)
        val ab = supportActionBar
        if (ab != null) {
            ab.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            ab.setCustomView(R.layout.ard_agora_actionbar_with_back_btn)
        }
    }

    override fun initUIandEvent() {
        addEventHandler(this)
        (findViewById(R.id.ovc_page_title) as TextView).setText(R.string.label_network_testing)
        val lastmileProbeConfig = LastmileProbeConfig()
        lastmileProbeConfig.probeUplink = true
        lastmileProbeConfig.probeDownlink = true
        lastmileProbeConfig.expectedUplinkBitrate = 5000
        lastmileProbeConfig.expectedDownlinkBitrate = 5000
        rtcEngine()!!.startLastmileProbeTest(lastmileProbeConfig)
    }

    override fun deInitUIandEvent() {
        rtcEngine()!!.stopLastmileProbeTest()
        removeEventHandler(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item!!)
    }

    fun onBackPressed(view: View?) {
        onBackPressed()
    }

    override fun onLastmileQuality(quality: Int) {
        runOnUiThread {
            val qualityInString: String? = ConstantApp().getNetworkQualityDescription(quality)
            val networkResult = "onLastmileQuality quality: $qualityInString"
            //                log.debug(networkResult);
            updateNetworkTestResult(qualityInString)
        }
    }

    override fun onLastmileProbeResult(result: LastmileProbeResult?) {
        runOnUiThread {
            val networkResult =
                """onLastmileProbeResult state: ${result?.state} rtt: ${result?.rtt}
     uplinkReport { packetLossRate: ${result?.uplinkReport?.packetLossRate} jitter: ${result?.uplinkReport?.jitter} availableBandwidth: ${result?.uplinkReport?.availableBandwidth}}
     downlinkReport { packetLossRate: ${result?.downlinkReport?.packetLossRate} jitter: ${result?.downlinkReport?.jitter} availableBandwidth: ${result?.downlinkReport?.availableBandwidth}}"""
            //                log.debug(networkResult);
            result?.rtt?.let {
                result?.uplinkReport?.packetLossRate?.let { it1 ->
                    result?.downlinkReport?.packetLossRate?.let { it2 ->
                        updateNetworkTestResult(
                            it,
                            it1,
                            it2
                        )
                    }
                }
            }
        }
    }

    fun updateNetworkTestResult(qualityInString: String?) {
        (findViewById(R.id.ovc_page_title) as TextView).setText(R.string.label_network_test_result)
        (findViewById(R.id.network_test_quality) as TextView).text = qualityInString
    }

    fun updateNetworkTestResult(rtt: Int, uplinkPacketLoss: Int, downlinkPacketLoss: Int) {
        (findViewById(R.id.ovc_page_title) as TextView).setText(R.string.label_network_test_result)
        (findViewById(R.id.network_test_rtt) as TextView).text = rtt.toString() + "ms"
        (findViewById(R.id.network_test_uplink_packet_loss) as TextView).text = "$uplinkPacketLoss%"
        (findViewById(R.id.network_test_downlink_packet_loss) as TextView).text =
            "$downlinkPacketLoss%"
    }
}
