package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.kotlinomnicure.utils.Constants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageCaptureActivity : AppCompatActivity() {
    private val TAG = "ImageCaptureActivity"
    private var fileUri: Uri? = null
    private  var tempFileUri:android.net.Uri? = null
    var photoURI: Uri? = null

    //By Avinash
    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
            matrix, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDataFromIntent()
    }

    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE)) {
            if (intent.getIntExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE, 0) ==
                Constants.ImageCaptureConstants.OPEN_CAMERA
            ) {
                openCamera()
            } else {
                openMediaContent()
            }
        }
    }


    private fun openMediaContent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE)
    }

    /**
     * Method to get the full size image
     */
    fun openCamera() {

        try {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val file = createImageFile()
            var isDirectoryCreated = false
            if (file.parentFile != null) isDirectoryCreated = file.parentFile.mkdirs()

            val authority = packageName + "." + Constants.FILE_PROVIDER_NAME
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tempFileUri = FileProvider.getUriForFile(this,
                    authority,  // As defined in Manifest
                    file)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri)
            } else {
                tempFileUri = Uri.fromFile(file)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri)
            }

            startActivityForResult(cameraIntent,
                Constants.ImageCaptureConstants.START_CAMERA_REQUEST_CODE)
        } catch (e: Exception) {

        }
    }

    /**
     * Create Image file
     */
    private fun createImageFile(): File {
        clearTempImages()
        @SuppressLint("SimpleDateFormat") val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(
                Date())
        val file: File = File(Constants.ImageCaptureConstants.IMAGE_PATH, "IMG_" + timeStamp +
                ".jpg")
        fileUri = Uri.fromFile(file)
        return file
    }


    private fun clearTempImages(): Boolean {
        try {
            val tempFolder: File = File(Constants.ImageCaptureConstants.IMAGE_PATH)


            val files = Objects.requireNonNull(tempFolder.listFiles())
            for (f in files) {
                if (f != null) {

                }
            }
        } catch (e: Exception) {

        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE) {

                if (intent.hasExtra(Constants.ImageCaptureConstants.SOURCE)
                    && intent.getStringExtra(Constants.ImageCaptureConstants.SOURCE).equals(
                        ChatActivity::class.java.simpleName, ignoreCase = true)) {
                    val intent = Intent()
                    if (data != null) {
                        intent.putExtra(Constants.ImageCaptureConstants.SCANNED_RESULT, data.data)
                    }
                    intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                        Constants.ImageCaptureConstants.OPEN_CAMERA)
                    setResult(RESULT_OK, intent)
                } else {

                    finish()
                }
            } else if (requestCode == Constants.ImageCaptureConstants.START_CAMERA_REQUEST_CODE) {
                val intent = Intent()
                intent.putExtra(Constants.ImageCaptureConstants.SCANNED_RESULT, fileUri)
                intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,
                    Constants.ImageCaptureConstants.OPEN_CAMERA)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                finish()
            }
        } else {
            finish()
        }
    }

    @Throws(IOException::class)
    private fun getTempFilename(context: Context): String? {
        val outputDir = context.cacheDir
        val outputFile = File.createTempFile("image_", "_tmp", outputDir)
        return outputFile.absolutePath
    }

    fun getRotatedBitmap(photoPath: String?, bitmap: Bitmap): Bitmap? {
        var rotatedBitmap: Bitmap? = null
        try {
            val ei = ExifInterface(photoPath!!)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)
            rotatedBitmap =
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> bitmap
                }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return rotatedBitmap ?: bitmap
    }

    fun getRealPathFromURI(uri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, proj, null, null, null)
        return if (cursor == null) {
            uri.path
        // Getting path from url itself
        } else {
            cursor.moveToFirst()
            val id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(id)
        }
    }

    @Throws(IOException::class)
    private fun getBitmap(selectedimg: Uri): Bitmap? {
        val options = BitmapFactory.Options()
        options.inSampleSize = 3
        var fileDescriptor: AssetFileDescriptor? = null
        var original: Bitmap? = null
        try {
            fileDescriptor = contentResolver.openAssetFileDescriptor(selectedimg, "r")
            original = BitmapFactory.decodeFileDescriptor(
                fileDescriptor!!.fileDescriptor, null, options)
        } catch (e: Exception) {
//            Log.d(TAG, e.getMessage());
        } finally {
            fileDescriptor?.close()
        }
        return original
    }

    private fun getUri(context: Context, bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        //        Log.i("Utils", "Media store path getUri: " + path);
        return Uri.parse(path)
    }
}