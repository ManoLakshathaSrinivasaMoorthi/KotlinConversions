package com.example.kotlinomnicure.activity

import android.app.ProgressDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityWebviewBinding
import com.example.kotlinomnicure.utils.Constants

class WebviewActivity : BaseActivity() {
    private val TAG = WebviewActivity::class.java.simpleName
    val EXTERNAL_REQUEST = 138
    var binding: ActivityWebviewBinding? = null
    var webView: WebView? = null
    private var progressDialog: ProgressDialog? = null
    private var url: String? = null
    private var imgBack: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)
        webView = findViewById(R.id.webview_image) as WebView
        val extras = intent.extras
        url = extras!!.getString(Constants.IntentKeyConstants.IMAGE_URL)
        println("WebView image url : $url")
        progressDialog = ProgressDialog(this@WebviewActivity)
        progressDialog?.setTitle("Please wait")
        progressDialog?.setMessage("Fetching image from server...")
        progressDialog?.setCancelable(false)
        progressDialog?.setCanceledOnTouchOutside(false)
        initToolbar()
        initView()
    }

    private fun initToolbar() {
        imgBack = findViewById(R.id.imgBack) as ImageView
        imgBack?.setOnClickListener { finish() }
    }

    private fun initView() {
        progressDialog?.show()
        startWebView()
    }


    private fun startWebView() {
        val webSettings = webView?.settings
        webSettings?.javaScriptEnabled = true
        webView?.settings?.builtInZoomControls = true
        webView?.settings?.setSupportZoom(true)
        if (Build.VERSION.SDK_INT >= 21) {
            webView?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (webView?.settings?.layoutAlgorithm != WebSettings.LayoutAlgorithm.SINGLE_COLUMN) webView!!.settings.layoutAlgorithm =
                WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        } else {
            if (!webView!!.settings.loadWithOverviewMode) webView!!.settings.loadWithOverviewMode =
                true
            if (!webView!!.settings.useWideViewPort) webView!!.settings.useWideViewPort = true
        }
        webView?.webViewClient = InsideWebViewClient()
        webView?.loadUrl(url!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.dismiss()
    }

    protected override fun addBackButton() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    private class InsideWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
           WebviewActivity().progressDialog?.dismiss()
        }
    }
}
