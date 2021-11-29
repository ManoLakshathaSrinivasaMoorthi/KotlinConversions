package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityMyDashboardBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.viewmodel.CensusHospitalListViewModel
import com.example.kotlinomnicure.viewmodel.CensusWardListViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import java.lang.ref.WeakReference
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MyDashboardActivity : BaseActivity() {

    private val TAG = MyDashboardActivity::class.java.simpleName
    protected var binding: ActivityMyDashboardBinding? = null
    var linearMyConsult: LinearLayout? = null
    var linearProviderDirectory:LinearLayout? = null
    var linearMyProfile:LinearLayout? = null
    var linearRequest:LinearLayout? = null
    var linearPendingConsults:LinearLayout? = null
    var linearPatientCensus:LinearLayout? = null
    var txtHospitalCount: TextView? = null
    private var imageURL: String? = null
    private  var strName:kotlin.String? = null
    private var strProfileName: String? = null
    private  var strDashboardHospitalAddress:kotlin.String? = null
    private var viewModel: CensusHospitalListViewModel? = null
    private var wardListViewModel: CensusWardListViewModel? = null
    private var strHospitalID: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_dashboard)
        viewModel = ViewModelProviders.of(this).get(CensusHospitalListViewModel::class.java)
        wardListViewModel = ViewModelProviders.of(this).get(CensusWardListViewModel::class.java)
        strName = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FIRST_NAME, "")
        setViews()
        onClickListener()
        onConnectionChanged(Intent())
    }

    fun setViews() {
        strProfileName = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        val strRole: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        val strDesignation: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        val stub = findViewById(R.id.layout_stub) as ViewStub
        val inflatedView: View
        if (strRole == "RD" && strDesignation == "MD/DO") {
            stub.layoutResource = R.layout.layout_dashboard_rp
            inflatedView = stub.inflate()
            linearMyProfile = inflatedView.findViewById<View>(R.id.linearMyProfile) as LinearLayout
            linearPendingConsults =
                inflatedView.findViewById<View>(R.id.linearPendingConsults) as LinearLayout
            linearPatientCensus =
                inflatedView.findViewById<View>(R.id.linearPatientCensus) as LinearLayout
            txtHospitalCount = inflatedView.findViewById<View>(R.id.txtHospitalCount) as TextView
            getHospitalList()
            linearPendingConsults?.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this@MyDashboardActivity, HomeActivity::class.java)
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "pending")
                startActivity(intent)
            })
            linearPatientCensus?.setOnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, ActivityPatientCensusHospital::class.java)
                startActivity(intent)
            }
            linearMyProfile?.setOnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            }
            binding?.profileLayout?.setVisibility(View.GONE)
        } else if (strRole == "RD" && strDesignation != "MD/DO") {
            stub.layoutResource = R.layout.layout_dashboard_rp_others
            inflatedView = stub.inflate()
            linearPatientCensus =
                inflatedView.findViewById<View>(R.id.linearPatientCensus) as LinearLayout
            txtHospitalCount = inflatedView.findViewById<View>(R.id.txtHospitalCount) as TextView
            getHospitalList()
            linearPatientCensus?.setOnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this, ActivityPatientCensusHospital::class.java
                )
                startActivity(intent)
            }
            binding?.profileLayout?.setVisibility(View.VISIBLE)
        } else {
            stub.layoutResource = R.layout.layout_dashboard_bp
            inflatedView = stub.inflate()
            strHospitalID = PrefUtility().getLongInPref(
                this@MyDashboardActivity,
                Constants.SharedPrefConstants.HOSPITAL_ID,
                0L
            )
            linearPatientCensus =
                inflatedView.findViewById<View>(R.id.linearPatientCensus) as LinearLayout
            txtHospitalCount = inflatedView.findViewById<View>(R.id.txtHospitalCount) as TextView
            getBPCensusWardList()
            linearPatientCensus?.setOnClickListener(View.OnClickListener { v ->
                handleMultipleClick(v)
                val intent = Intent(this@MyDashboardActivity, ActivityPatientCensusWard::class.java)
                intent.putExtra(
                    Constants.IntentKeyConstants.SCREEN_TYPE,
                    Constants.IntentKeyConstants.SCREEN_DASHBOARD
                )
                intent.putExtra(
                    Constants.IntentKeyConstants.HOSPITAL_ADDRESS,
                    strDashboardHospitalAddress
                )
                startActivity(intent)
            })
            binding?.profileLayout?.setVisibility(View.VISIBLE)
        }
        linearMyConsult = inflatedView.findViewById<View>(R.id.linearMyConsult) as LinearLayout
        linearProviderDirectory =
            inflatedView.findViewById<View>(R.id.linearProviderDirectory) as LinearLayout
        linearRequest = inflatedView.findViewById<View>(R.id.linearRequest) as LinearLayout
        binding?.txtName?.setText("Hello, $strName")
        val timeInMillis = System.currentTimeMillis()
        val dateString = SimpleDateFormat("EEEE, MMMM dd").format(Date(timeInMillis))
        binding?.date?.setText(dateString)
    }

    override fun onResume() {
        super.onResume()
        val strRole: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        imageURL =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.PROFILE_IMG_URL, "")
        println("image url updated $imageURL")
        if (!TextUtils.isEmpty(imageURL)) {
            binding?.profilePic?.setVisibility(View.VISIBLE)
            binding?.defaultImageView?.setVisibility(View.GONE)
            ImageLoader(this, imageURL).execute()
        } else {
            binding?.profilePic?.setVisibility(View.GONE)
            binding?.defaultImageView?.setVisibility(View.VISIBLE)
            if (!TextUtils.isEmpty(strProfileName)) {
                binding?.defaultImageView?.setText(strProfileName?.let {
                    UtilityMethods().getNameText(
                        it
                    )
                })
                Log.d(TAG, "defaultImageView" + strProfileName?.let {
                    UtilityMethods().getNameText(
                        it
                    )
                })
            }
        }
        if (strRole == "RD") {
            getHospitalList()
        } else {
            getBPCensusWardList()
        }
    }

    fun onClickListener() {
        linearMyConsult?.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this@MyDashboardActivity, HomeActivity::class.java)
            startActivity(intent)
        }
        linearProviderDirectory?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            val intent =
                Intent(this@MyDashboardActivity, RemoteProviderDirectoryActivity::class.java)
            startActivity(intent)
        })
        binding?.profileLayout?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this@MyDashboardActivity, MyProfileActivity::class.java)
            startActivity(intent)
        })
        binding?.imageLayout?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this@MyDashboardActivity, MyProfileActivity::class.java)
            startActivity(intent)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun getHospitalList() {
        val providerId: Long? = PrefUtility().getProviderId(this)
        providerId?.let {
            viewModel?.getHospitalList(it)?.observe(this) { response ->
                if (response.getHospitalList() != null && !response.getHospitalList()!!.isEmpty()) {
                    Log.d(TAG, "getHospitalList response : " + Gson().toJson(response))
                    Log.d(TAG, "getHospitalList size : " + response.getHospitalList()!!.size)
                    if (response.getHospitalList()!!.size > 1) {
                        txtHospitalCount?.setText(
                            response.getHospitalList()!!.size.toString() + " hospitals"
                        )
                    } else {
                        txtHospitalCount?.setText(
                            response.getHospitalList()!!.size.toString() + " hospital"
                        )
                    }
                } else {
                    txtHospitalCount!!.visibility = View.GONE
                }
            }
        }
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler!!.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun getBPCensusWardList() {
        showProgressBar(PBMessageHelper().getMessage(this, "Getting ward list"))
        strHospitalID?.let {
            wardListViewModel?.getWardList(it)?.observe(this) {
                dismissProgressBar()
                val response = it.body()
                if (response != null && !response.equals("")) {
                    Log.d(TAG, "getCensusWardList response : " + Gson().toJson(response))
                    val hospital: Hospital? = response.getHospital()
                    strDashboardHospitalAddress = hospital?.getSubRegionName()
                    if (response.getTotalPatientCount()?.equals(0) == true) {
                        txtHospitalCount!!.visibility = View.GONE
                    } else if (response.getTotalPatientCount()?.equals(1) == true) {
                        txtHospitalCount?.setText(response.getTotalPatientCount().toString() + " patient")
                    } else {
                        txtHospitalCount?.setText(
                            response.getTotalPatientCount().toString() + " patients"
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 503) {
            if (resultCode == RESULT_OK) {
                getBPCensusWardList()
            }
        }
    }

    private class ImageLoader internal constructor(
        activity: MyDashboardActivity,
        imageURL: String?
    ) :
        AsyncTask<Void?, Void?, Bitmap?>() {
        var activityReference: WeakReference<MyDashboardActivity>
        var imageURL: String?
        var imageProgressBar: ProgressBar
        var imageView: ImageView
        var cameraIcon: ImageView? = null
        var defaultImgView: TextView
        override fun onPreExecute() {
            super.onPreExecute()
            imageProgressBar.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            defaultImgView.visibility = View.VISIBLE
            val name: String? = activityReference.get()?.let {
                PrefUtility().getStringInPref(
                    it,
                    Constants.SharedPrefConstants.NAME,
                    ""
                )
            }
            defaultImgView.setText(name?.let { UtilityMethods().getNameText(it) })
        }

        protected override fun doInBackground(vararg params: Void?): Bitmap? {
            var bitmap: Bitmap?
            try {
                val connection = URL(imageURL).openConnection()
                connection.connectTimeout = 10000
                bitmap = BitmapFactory.decodeStream(connection.getInputStream())
            } catch (e: Exception) {
                bitmap = null
                e.printStackTrace()
            }
            return bitmap
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            val myDashboardActivity = activityReference.get()
            if (myDashboardActivity != null) {
                imageProgressBar.visibility = View.GONE
                if (bitmap != null) {
                    imageView.visibility = View.VISIBLE
                    defaultImgView.visibility = View.GONE
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.visibility = View.GONE
                    defaultImgView.visibility = View.VISIBLE
                    val name: String? = PrefUtility().getStringInPref(
                        myDashboardActivity,
                        Constants.SharedPrefConstants.NAME,
                        ""
                    )
                    defaultImgView.setText(name?.let { UtilityMethods().getNameText(it) })
                }
            }
        }

        override fun onCancelled() {
            super.onCancelled()
        }

        init {
            activityReference = WeakReference(activity)
            this.imageURL = imageURL
            imageView = activityReference.get()!!.binding?.profilePic!!
            imageProgressBar = activityReference.get()!!.binding?.idProfileImagePb!!
            defaultImgView = activityReference.get()!!.binding?.defaultImageView!!
        }
    }

}
