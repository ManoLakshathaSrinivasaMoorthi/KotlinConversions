package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityPdfViewerBinding
import com.example.kotlinomnicure.utils.Constants
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PDFViewerActivity : AppCompatActivity(),OnPageChangeListener,
    OnLoadCompleteListener,
    OnPageErrorListener {

    //Declare the variables
    val EXTERNAL_REQUEST = 138
    private val TAG = PDFViewerActivity::class.java.simpleName
    var binding: ActivityPdfViewerBinding? = null

    var url: String? = null
    var pdfView: PDFView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_viewer)
        pdfView = findViewById<PDFView>(R.id.pdfView)
        val extras = intent.extras
        url = extras!!.getString(Constants.IntentKeyConstants.PDF_URL)
        Log.i(TAG, "pdf url to load$url")
        initToolbar()
        initView()
    }

    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)

    }

    private fun initView() {
        val loadurl = LoadPdf(this, url)
        loadurl.execute("")
    }

    protected fun addBackButton() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    class LoadPdf(loadPdf: Context?, private val pdfUrl: String?) :
        AsyncTask<String?, Void?, InputStream?>(), OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {
        private val progressDialog: ProgressDialog = ProgressDialog(loadPdf)
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Fetching PDF from server...")
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }


        override fun onPostExecute(inputStream: InputStream?) {


            PDFViewerActivity().pdfView!!.fromStream(inputStream)
                .defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(this))
                .spacing(10)
                .onPageError(this)
                .enableAnnotationRendering(false)
                .enableAntialiasing(true)
                .load()
            progressDialog.dismiss()
        }

        override fun doInBackground(vararg p0: String?): InputStream? {
            var inputStream: InputStream? = null
            try {
                val url = URL(pdfUrl)
                val urlConnection = url.openConnection() as HttpURLConnection
                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e: IOException) {
                return null
            }
            return inputStream
        }

        override fun onPageChanged(page: Int, pageCount: Int) {

        }

        override fun loadComplete(nbPages: Int) {

        }

        override fun onPageError(page: Int, t: Throwable?) {

        }
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }




}