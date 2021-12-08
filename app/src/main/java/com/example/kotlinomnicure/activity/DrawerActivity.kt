package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.viewmodel.HomeViewModel


import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityDrawerBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods


open class DrawerActivity : BaseActivity() {
    open val TAG = DrawerActivity::class.java.simpleName
    var binding: ActivityDrawerBinding? = null
    protected var drawerLayout: DrawerLayout? = null
    var navHeaderView: View? = null
    var viewModel: HomeViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_drawer)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        initDrawer()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initDrawer() {
        drawerLayout = binding?.drawerLayout
        navHeaderView = binding?.leftNavView?.getHeaderView(0)
        val role: String? = PrefUtility().getRole(this)
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            val menuMyConsults = navHeaderView!!.findViewById<TextView>(R.id.menuTxtMyConsults)
            val designation = navHeaderView!!.findViewById<TextView>(R.id.id_designation)
            menuMyConsults.text = getString(R.string.my_virtual_ward)
            val strDesignation: String? =
                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
            val strRole: String? =
                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
            if (strRole == "RD") {
                if (strDesignation != "BD") {
                    designation.visibility = View.VISIBLE
                    designation.text = strDesignation
                }
            } else {
                designation.visibility = View.GONE
            }

            val menuHandOffPatients =
                navHeaderView!!.findViewById<TextView>(R.id.menuTxtHandOffPatient)
            val imgHandOffPatients = navHeaderView!!.findViewById<ImageView>(R.id.icHandOffPatient)
            menuHandOffPatients.text = getString(R.string.my_virtual_teams)
            imgHandOffPatients.setImageDrawable(resources.getDrawable(R.drawable.ic_my_virtual_teams))
        }
        val menuHandOffPatients =
            navHeaderView!!.findViewById<LinearLayout>(R.id.menuHandoffPatients)
        val menuVirtualTeam = navHeaderView!!.findViewById<LinearLayout>(R.id.menuVirtualTeam)
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            menuVirtualTeam.visibility = View.VISIBLE
            menuHandOffPatients.visibility = View.GONE
        } else {
            menuVirtualTeam.visibility = View.GONE
            menuHandOffPatients.visibility = View.VISIBLE
        }
        setVersion()
    }

    @SuppressLint("SetTextI18n")
    private fun setVersion() {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            if (UtilityMethods().isTestServer()) {
                binding!!.idVersion.setText("Test_" + getString(R.string.version) + " " + packageInfo.versionName)
            } else if (UtilityMethods().isDemoTestServer()) {
                binding!!.idVersion.setText("Demo_" + getString(R.string.version) + " " + packageInfo.versionName)
            } else if (UtilityMethods().isQaTestServer()) {
                binding!!.idVersion.setText("QA_" + getString(R.string.version) + " " + packageInfo.versionName)
            } else {
                binding!!.idVersion.setText(getString(R.string.version) + " " + packageInfo.versionName)
            }
        } catch (e: PackageManager.NameNotFoundException) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }
}