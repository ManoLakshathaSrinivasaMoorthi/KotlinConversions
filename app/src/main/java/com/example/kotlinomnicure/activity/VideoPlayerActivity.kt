package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.dailytasksamplepoc.R
import com.example.kotlinomnicure.utils.Constants
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import java.lang.Exception

class VideoPlayerActivity : AppCompatActivity() {

    //Declare the variables
    private val TAG = VideoPlayerActivity::class.java.simpleName
    private val progressDialog: ProgressDialog? = null
    private var url: String? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var videoView: PlayerView? = null
    private var simpleExoPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        initToolBar()
        initViews()
    }

    //Initialise the tool bar
    private fun initToolBar() {
        val imgBack = findViewById<View>(R.id.imgBack) as ImageView
        imgBack.setOnClickListener { finish() }
    }

    //Init the views for the activity
    private fun initViews() {
        val extras = intent.extras
        url = extras!!.getString(Constants.IntentKeyConstants.IMAGE_URL)
        println("VideoUrl : $url")
        videoView = findViewById<View>(R.id.videoViewPlayer) as PlayerView

    }

    /**
     * Initiating the exo play video via URL
     */
    private fun initPlayVideo() {
        try {

            //Setting player
            simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
            videoView!!.player = simpleExoPlayer
            // Setting media item
            val mediaItem = MediaItem.fromUri(
                url!!
            )
            simpleExoPlayer!!.setMediaItem(mediaItem)
            // Prepare to play
            simpleExoPlayer!!.setPlayWhenReady(playWhenReady)
            simpleExoPlayer!!.seekTo(currentWindow, playbackPosition)
            simpleExoPlayer!!.prepare()
        } catch (e: Exception) {
            // below line is used for
            // handling our errors.
            Log.e("TAG", "Error : $e")
        }
    }

    // Hiding system UI
    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        videoView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    // Releasing the player
    private fun releasePlayer() {
        if (simpleExoPlayer != null) {
            playWhenReady = simpleExoPlayer!!.getPlayWhenReady()
            playbackPosition = simpleExoPlayer!!.getCurrentPosition()
            currentWindow = simpleExoPlayer!!.getCurrentWindowIndex()
            simpleExoPlayer!!.release()
            simpleExoPlayer = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayVideo()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24 || simpleExoPlayer == null) {
            initPlayVideo()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

}