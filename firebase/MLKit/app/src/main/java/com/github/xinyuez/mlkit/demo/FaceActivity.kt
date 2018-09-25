package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.preview.Frame
import io.fotoapparat.selector.firstAvailable
import io.fotoapparat.selector.off
import io.fotoapparat.selector.torch
import io.fotoapparat.util.FrameProcessor
import kotlinx.android.synthetic.clearFindViewByIdCache
import kotlinx.android.synthetic.main.activity_face.face_appbar
import kotlinx.android.synthetic.main.content_face.camera_view

class FaceActivity : AppCompatActivity(), FrameProcessor {
    private var activeCamera: Camera = Camera.Back
    private var flashOn = false

    private val fotoapparat by lazy {
        Fotoapparat.with(applicationContext)
                .into(camera_view)
                .frameProcessor(this)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)
        setSupportActionBar(face_appbar)
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFindViewByIdCache()
    }

    @Suppress("UNUSED_PARAMETER")
    fun switchCamera(v: View) {
        activeCamera = when (activeCamera) {
            Camera.Front -> Camera.Back
            Camera.Back -> Camera.Front
        }

        fotoapparat.switchTo(
                lensPosition = activeCamera.lensPosition,
                cameraConfiguration = activeCamera.configuration
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun flash(v: View) {
        fotoapparat.updateConfiguration(
                UpdateConfiguration(
                        flashMode = if (flashOn) {
                            flashOn = false
                            firstAvailable(
                                    off()
                            )
                        } else {
                            flashOn = true
                            torch()
                        }
                )
        )
    }

    override fun invoke(frame: Frame) {
        Log.d(TAG, "${frame.image.size}")
    }

    companion object {
        private const val TAG = "face"
        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, FaceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }
    }
}